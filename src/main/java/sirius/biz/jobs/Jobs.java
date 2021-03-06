/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.jobs;

import sirius.kernel.Sirius;
import sirius.kernel.commons.MultiMap;
import sirius.kernel.commons.Strings;
import sirius.kernel.commons.Tuple;
import sirius.kernel.di.GlobalContext;
import sirius.kernel.di.std.Part;
import sirius.kernel.di.std.PriorityParts;
import sirius.kernel.di.std.Priorized;
import sirius.kernel.di.std.Register;
import sirius.kernel.settings.Extension;
import sirius.web.security.UserContext;
import sirius.web.security.UserInfo;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides a central place to find all available jobs.
 */
@Register(classes = Jobs.class)
public class Jobs {

    /**
     * Names the framework which must be enabled to activate the jobs feature.
     */
    public static final String FRAMEWORK_JOBS = "biz.jobs";

    /**
     * Defines the permission required to execute jobs.
     */
    public static final String PERMISSION_EXECUTE_JOBS = "permission-execute-jobs";

    @Part
    private GlobalContext ctx;

    @PriorityParts(JobFactory.class)
    private List<JobFactory> factories;

    private Map<String, JobCategory> categories;

    /**
     * Returns a map of all known categories.
     *
     * @return all known categories as a map (name to category)
     */
    protected Map<String, JobCategory> getCategories() {
        if (categories == null) {
            Map<String, JobCategory> result = new HashMap<>();
            for (Extension ext : Sirius.getSettings().getExtensions("jobs.categories")) {
                result.put(ext.getId(),
                           new JobCategory(ext.getId(),
                                           ext.getRaw("label").asString(),
                                           ext.get("icon").asString(),
                                           ext.get("priority").asInt(Priorized.DEFAULT_PRIORITY)));
            }

            categories = result;
        }

        return Collections.unmodifiableMap(categories);
    }

    /**
     * Returns a stream of {@link JobFactory jobs} available for the current user.
     *
     * @param query the search query to filter by
     * @return a stream of jobs which are available to the current user and match the given search query
     */
    public Stream<JobFactory> getAvailableJobs(@Nullable String query) {
        UserInfo currentUser = UserContext.getCurrentUser();
        if (!currentUser.hasPermission(PERMISSION_EXECUTE_JOBS)) {
            return Stream.empty();
        }

        Stream<JobFactory> stream = factories.stream()
                                             .filter(factory -> factory.getRequiredPermissions()
                                                                       .stream()
                                                                       .allMatch(currentUser::hasPermission));
        if (Strings.isFilled(query)) {
            String queryAsLowerCase = query.toLowerCase();
            stream = stream.filter(factory -> factory.getLabel().toLowerCase().contains(queryAsLowerCase));
        }

        return stream;
    }

    /**
     * Groups and sorts a stream of {@link JobFactory jobs} by their {@link JobCategory}.
     *
     * @param jobs the stream of jobs to sort and group
     * @return a list of tuples containing a category and their list of jobs
     */
    public List<Tuple<JobCategory, Collection<JobFactory>>> groupByCategory(Stream<JobFactory> jobs) {
        MultiMap<JobCategory, JobFactory> map = MultiMap.createOrdered();
        JobCategory defaultCategory = getCategories().get(JobCategory.CATEGORY_MISC);
        jobs.forEach(job -> {
            map.put(getCategories().getOrDefault(job.getCategory(), defaultCategory), job);
        });

        return map.stream()
                  .map(e -> Tuple.create(e.getKey(), e.getValue()))
                  .sorted(Comparator.comparing(t -> t.getFirst().getPriority()))
                  .collect(Collectors.toList());
    }

    /**
     * Returns a jobs which can provide a preset URL for the given target object.
     *
     * @param target the target to generate a preset URL for
     * @return a list of tuples containing the preset URL associated job for the given object
     */
    public List<Tuple<String, JobFactory>> getMatchingUIJobs(Object target) {
        return getAvailableJobs(null).filter(JobFactory::canStartInUI)
                                     .map(job -> Tuple.create(job.generatePresetUrl(target), job))
                                     .filter(tuple -> tuple.getFirst() != null)
                                     .collect(Collectors.toList());
    }

    /**
     * Resolves the job factory with the given name.
     * <p>
     * Note that this ensures, that the current user may invoke jobs at all, but it will not check the permissions of
     * the factory itself.
     *
     * @param name         the name of the job factory to resolve
     * @param expectedType a type to cast the job factory to
     * @param <J>          the generic version of <tt>expectedType</tt>
     * @return the job factory with the given name, casted to the given class
     */
    @SuppressWarnings("unchecked")
    public <J extends JobFactory> J findFactory(String name, Class<J> expectedType) {
        UserInfo currentUser = UserContext.getCurrentUser();
        currentUser.assertPermission(PERMISSION_EXECUTE_JOBS);

        JobFactory result = ctx.getPart(name, JobFactory.class);

        if (result == null) {
            throw new IllegalArgumentException(Strings.apply("No JobFactory found for '%s'", name));
        }
        if (!expectedType.isInstance(result)) {
            throw new IllegalArgumentException(Strings.apply("JobFactory '%s' of type '%s' does not implement '%s'",
                                                             name,
                                                             result.getClass().getName(),
                                                             expectedType.getName()));
        }

        return (J) result;
    }
}
