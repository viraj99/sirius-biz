/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.importer;

import sirius.biz.tenants.jdbc.SQLTenantAware;
import sirius.db.jdbc.SQLEntity;
import sirius.db.jdbc.batch.DeleteQuery;
import sirius.db.jdbc.batch.FindQuery;
import sirius.db.jdbc.batch.InsertQuery;
import sirius.db.jdbc.batch.UpdateQuery;
import sirius.db.mixing.BaseEntity;
import sirius.db.mixing.Mapping;
import sirius.db.mixing.Property;
import sirius.kernel.commons.Context;
import sirius.kernel.commons.Strings;
import sirius.kernel.commons.Tuple;
import sirius.kernel.commons.Value;
import sirius.kernel.commons.ValueHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Provides a base implementation and also generic handler for all {@link SQLEntity JDBC/SQL entities}.
 * <p>
 * Subclasses might most probably want to overwrite:
 * {@link #determineCacheKey(SQLEntity)} and {@link #getAutoImportMappings()}
 * as well as maybe {@link #parseProperty(BaseEntity, Property, Value, Context)}.
 *
 * @param <E> the type of entity being handled by this handler
 */
public abstract class SQLEntityImportHandler<E extends SQLEntity> extends BaseImportHandler<E> {

    protected static final Mapping[] MAPPING_ARRAY = new Mapping[0];

    protected UpdateQuery<E> updateQuery;
    protected InsertQuery<E> insertQuery;
    protected DeleteQuery<E> deleteQuery;

    protected List<Tuple<Predicate<E>, Supplier<FindQuery<E>>>> findQueries = new ArrayList<>();
    protected Mapping[] mappingsToLoadForFind;
    protected Mapping[] mappingsToLoad;
    protected Mapping[] mappingsToUpdate;
    protected Mapping[] mappingsToCompare;

    /**
     * Creates a new instance for the given type of entities and import context.
     *
     * @param clazz   the type of entities being handled
     * @param context the import context to use
     */
    protected SQLEntityImportHandler(Class<?> clazz, ImporterContext context) {
        super(clazz, context);
        collectFindQueries((p, q) -> findQueries.add(Tuple.create(p, q)));
        collectFindQueriesFromExtenders(clazz, context);

        this.mappingsToCompare = getMappingsToCompare().toArray(MAPPING_ARRAY);
        this.mappingsToLoad = getAutoImportMappings().toArray(MAPPING_ARRAY);
        this.mappingsToUpdate = getMappingsToUpdate().toArray(MAPPING_ARRAY);
        this.mappingsToLoadForFind = getMappingsToLoadForFind().toArray(MAPPING_ARRAY);
    }

    @SuppressWarnings("unchecked")
    private void collectFindQueriesFromExtenders(Class<?> clazz, ImporterContext context) {
        for (EntityImportHandlerExtender extender : extenders) {
            if (extender instanceof SQLEntityImportHandlerExtender) {
                SQLEntityImportHandlerExtender<E> sqlExtender = (SQLEntityImportHandlerExtender<E>) extender;
                sqlExtender.collectFindQueries(this,
                                               descriptor,
                                               context,
                                               (p, q) -> findQueries.add(Tuple.create(p, q)));
            }
        }
    }

    protected List<Mapping> getMappingsToUpdate() {
        return descriptor.getProperties()
                         .stream()
                         .map(Property::getName)
                         .map(Mapping::named)
                         .filter(mapping -> !SQLEntity.ID.equals(mapping))
                         .collect(Collectors.toList());
    }

    /**
     * Provides a list of mappings, which each is used to compute a {@link FindQuery}.
     * <p>
     * Using this approach, several find queries based on different field combinations can be used to execute
     * find.
     * <p>
     * Additional queries can also added by providing an {@link SQLEntityImportHandlerExtender} if extending
     * the importer class itself isn't a viable option.
     */
    protected abstract void collectFindQueries(BiConsumer<Predicate<E>, Supplier<FindQuery<E>>> queryConsumer);

    /**
     * Returns a list of mappings to use (compare) if an entity is being updated.
     *
     * @return a list of mappings used in the <tt>WHERE</tt> part of <tt>INSERT</tt>
     */
    protected List<Mapping> getMappingsToCompare() {
        return Collections.singletonList(SQLEntity.ID);
    }

    /**
     * Returns a list of mappings to load for a {@link #tryFindByExample(SQLEntity) find}.
     *
     * @return the list of mappings to update in this entity
     */
    protected Collection<Mapping> getMappingsToLoadForFind() {
        return findQueries.stream()
                          .flatMap(qry -> qry.getSecond().get().getFilterMappings().stream().map(Mapping::named))
                          .collect(Collectors.toSet());
    }

    @Override
    public E load(Context data, E entity) {
        return load(data, entity, mappingsToLoad);
    }

    @Override
    protected E load(Context data, E entity, Mapping... mappings) {
        E e = super.load(data, entity, mappings);
        if (e instanceof SQLTenantAware) {
            ((SQLTenantAware) e).fillWithCurrentTenant();
        }

        return e;
    }

    @Override
    public Optional<E> tryFind(Context data) {
        for (Tuple<Predicate<E>, Supplier<FindQuery<E>>> predicateAndQuery : findQueries) {
            Optional<E> result = tryFindByExample(load(data, mappingsToLoadForFind), predicateAndQuery);
            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }

    protected boolean containsAllRequiredFields(Context data, Mapping[] mappings) {
        return Arrays.stream(mappings).map(Mapping::toString).allMatch(data::containsKey);
    }

    private Optional<E> tryFindByExample(E example, Tuple<Predicate<E>, Supplier<FindQuery<E>>> predicateAndQuery) {
        if (!predicateAndQuery.getFirst().test(example)) {
            return Optional.empty();
        }

        if (!(predicateAndQuery.getSecond() instanceof ValueHolder<?>)) {
            predicateAndQuery.setSecond(new ValueHolder<>(predicateAndQuery.getSecond().get()));
        }

        return predicateAndQuery.getSecond().get().find(example);
    }

    /**
     * Tries to find a persistent entity using the given example.
     *
     * @param example the example instance used to search by
     * @return a matching entity wrapped as optional or an empty optional if no match is available
     */
    protected Optional<E> tryFindByExample(E example) {
        for (Tuple<Predicate<E>, Supplier<FindQuery<E>>> predicateAndQuery : findQueries) {
            Optional<E> result = tryFindByExample(example, predicateAndQuery);
            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<E> tryFindInCache(Context data) {
        E example = load(data, mappingsToLoadForFind);
        Tuple<Class<?>, String> cacheKey = Tuple.create(descriptor.getType(), determineCacheKey(example));
        if (Strings.isFilled(cacheKey.getSecond())) {
            Object result = context.getLocalCache().getIfPresent(cacheKey);
            if (result != null) {
                return Optional.of((E) result);
            }
        }

        Optional<E> result = tryFindByExample(load(data, mappingsToLoadForFind));
        if (result.isPresent() && Strings.isFilled(cacheKey)) {
            context.getLocalCache().put(cacheKey, result.get());
        }

        return result;
    }

    /**
     * Determines the cache key used by {@link #tryFindInCache(Context)} to find an instance in the cache.
     * <p>
     * Note that this isn't implemented by default and has to be overwritten by subclasses which want to support caching.
     *
     * @param example the entity to derive the cache key from
     * @return a unique string representation used for cache lookups or <tt>null</tt> to indicate that either caching
     * is completely disabled or that the example instance doesn't provide values in the relevant fields to support a
     * cache lookup.
     */
    protected String determineCacheKey(E example) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E createOrUpdateNow(E entity) {
        return createOrUpdate(entity, false);
    }

    @Override
    public void createOrUpdateInBatch(E entity) {
        createOrUpdate(entity, true);
    }

    @Override
    public void createNowOrUpdateInBatch(E entity) {
        boolean executeImmediatelly = entity.isNew() || entity.isChanged(mappingsToLoadForFind);
        createOrUpdate(entity, !executeImmediatelly);
    }

    /**
     * Determines if and how the given entity needs to be persisted.
     * <p>
     * If the given entity was already persisted, an update is pushed to the database. If the entity is
     * {@link BaseEntity#isNew() new}, an insert is performed.
     *
     * @param entity the entity to update or create
     * @param batch  <tt>true</tt> if batch updates / creates should be used, <tt>false</tt> otherwise
     * @return the updated or created entity or, if batch updates are active, the given entity
     */
    protected E createOrUpdate(E entity, boolean batch) {
        if (entity.isNew()) {
            return createIfChanged(entity, batch);
        } else {
            return updateIfChanged(entity, batch);
        }
    }

    /**
     * Creates a new entity into the database if there are any values set at all.
     *
     * @param entity the entity to insert into the database
     * @param batch  <tt>true</tt> if batch inserts should be used, <tt>false</tt> otherwise
     * @return the created entity or, if batch updates are active, the given entity
     */
    protected E createIfChanged(E entity, boolean batch) {
        if (isChanged(entity)) {
            getInsertQuery().insert(entity, true, batch);
        }

        return entity;
    }

    /**
     * Transfers all changed properties from the updated entity to the entity loaded from the database.
     *
     * @param entityWithUpdates the entity which contains the update
     * @param persistentEntity  the entity which was loaded from the database and is to be updated
     */
    protected void updatePersistentEntity(E entityWithUpdates, E persistentEntity) {
        for (Mapping mapping : mappingsToLoad) {
            Property property = descriptor.getProperty(mapping);
            property.setValue(persistentEntity, property.getValue(entityWithUpdates));
        }
    }

    /**
     * Updates the given entity in the database if there are any changes.
     *
     * @param entity the entity to update in the database
     * @param batch  <tt>true</tt> if batch updates should be used, <tt>false</tt> otherwise
     * @return the updated entity or, if batch updates are active, the given entity
     */
    protected E updateIfChanged(E entity, boolean batch) {
        // Invoke the beforeSave checks so that the change-detection below works for
        // computed properties...
        descriptor.beforeSave(entity);

        if (isChanged(entity)) {
            getUpdateQuery().update(entity, true, batch);
        }

        return entity;
    }

    /**
     * Checks whether any property of the entity has changed.
     *
     * @param entity the entity to check
     * @return <tt>true</tt> if any property checked is changed, <tt>false</tt> otherwise
     */
    protected boolean isChanged(E entity) {
        return entity.isChanged(mappingsToLoad);
    }

    /**
     * Creates the prepared statement as {@link UpdateQuery} used to update entities.
     *
     * @return the update query to use
     */
    @SuppressWarnings("unchecked")
    protected UpdateQuery<E> getUpdateQuery() {
        if (updateQuery == null) {
            updateQuery = context.getBatchContext()
                                 .updateQuery((Class<E>) descriptor.getType(), mappingsToCompare)
                                 .withUpdatedMappings(mappingsToUpdate);
        }

        return updateQuery;
    }

    /**
     * Creates the prepared statement as {@link UpdateQuery} used to insert entities.
     *
     * @return the insert query to use
     */
    @SuppressWarnings("unchecked")
    protected InsertQuery<E> getInsertQuery() {
        if (insertQuery == null) {
            insertQuery =
                    context.getBatchContext().insertQuery((Class<E>) descriptor.getType(), true, mappingsToUpdate);
        }

        return insertQuery;
    }

    /**
     * Creates the prepared statement as {@link DeleteQuery} used to delete entities.
     *
     * @return the delete query to use
     */
    @SuppressWarnings("unchecked")
    protected DeleteQuery<E> getDeleteQuery() {
        if (deleteQuery == null) {
            deleteQuery = context.getBatchContext().deleteQuery((Class<E>) descriptor.getType(), mappingsToCompare);
        }

        return deleteQuery;
    }

    @Override
    public void deleteNow(E entity) {
        getDeleteQuery().delete(entity, true, false);
    }

    @Override
    public void deleteInBatch(E entity) {
        getDeleteQuery().delete(entity, true, true);
    }

    @Override
    public void commit() {
        if (insertQuery != null) {
            insertQuery.commit();
        }

        if (updateQuery != null) {
            updateQuery.commit();
        }

        if (deleteQuery != null) {
            deleteQuery.commit();
        }
    }
}
