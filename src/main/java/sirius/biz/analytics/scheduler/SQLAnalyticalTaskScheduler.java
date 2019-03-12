/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.analytics.scheduler;

import com.alibaba.fastjson.JSONObject;
import sirius.biz.analytics.SQLEntityBatchEmitter;
import sirius.db.jdbc.SQLEntity;
import sirius.db.jdbc.SmartQuery;
import sirius.db.mongo.MongoEntity;
import sirius.kernel.commons.MultiMap;
import sirius.kernel.di.GlobalContext;
import sirius.kernel.di.std.Part;

import java.time.LocalDate;
import java.util.function.Consumer;

/**
 * Provides a base implementation which schedules all JDBC entities matching a given set of tasks (defined by a
 * subclass of {@link AnalyticalTask}) using a specified batch size.
 * <p>
 * This is done by creating batches with a specified <tt>startId</tt> and <tt>endId</tt>.
 * <p>
 * Use {@code @Register(classes = AnalyticsScheduler.class)} to make this scheduler visible to the framework.
 */
public abstract class SQLAnalyticalTaskScheduler implements AnalyticsScheduler {

    @Part
    private GlobalContext context;

    @Part
    private SQLEntityBatchEmitter batchEmitter;

    private MultiMap<Class<?>, AnalyticalTask<?>> tasks;

    protected abstract Class<?> getAnalyticalTaskType();

    @SuppressWarnings("unchecked")
    protected Class<AnalyticalTask<?>> getEffectiveAnalyticalTaskType() {
        return (Class<AnalyticalTask<?>>) getAnalyticalTaskType();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void scheduleBatches(Consumer<JSONObject> batchConsumer) {
        getTasks().keySet().forEach(type -> scheduleBatches((Class<? extends SQLEntity>) type, batchConsumer));
    }

    protected void scheduleBatches(Class<? extends SQLEntity> type, Consumer<JSONObject> batchConsumer) {
        try {
            batchEmitter.computeBatches(type, this::extendBatchQuery, 250, batchConsumer);
        } catch (Exception e) {
            //TODO
            // e.printStackTrace();
        }
    }

    protected <E extends SQLEntity> void extendBatchQuery(SmartQuery<E> query) {

    }

    private MultiMap<Class<?>, AnalyticalTask<?>> getTasks() {
        if (tasks == null) {
            tasks = determineTasks();
        }
        return tasks;
    }

    private MultiMap<Class<?>, AnalyticalTask<?>> determineTasks() {
        MultiMap<Class<?>, AnalyticalTask<?>> result = MultiMap.create();
        context.getParts(getEffectiveAnalyticalTaskType()).forEach(task -> {
            if (MongoEntity.class.isAssignableFrom(task.getType())) {
                result.put(task.getType(), task);
            }
        });

        return result;
    }

    @Override
    public void executeBatch(JSONObject batchDescription, LocalDate date) {
        batchEmitter.evaluateBatch(batchDescription, e -> executeEntity(e, date));
    }

    @SuppressWarnings("unchecked")
    protected void executeEntity(SQLEntity entity, LocalDate date) {
        for (AnalyticalTask<?> task : getTasks().get(entity.getClass())) {
            try {
                ((AnalyticalTask<SQLEntity>) task).compute(date, entity);
            } catch (Exception ex) {
                //TODO
            }
        }
    }
}
