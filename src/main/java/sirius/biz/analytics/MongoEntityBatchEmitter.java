/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.analytics;

import com.alibaba.fastjson.JSONObject;
import sirius.db.mixing.Mixing;
import sirius.db.mongo.Mango;
import sirius.db.mongo.MongoEntity;
import sirius.db.mongo.MongoQuery;
import sirius.db.mongo.QueryBuilder;
import sirius.kernel.async.TaskContext;
import sirius.kernel.commons.ValueHolder;
import sirius.kernel.di.std.Part;
import sirius.kernel.di.std.Register;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Provides a base implementation which schedules all MongoDB entities of a given type using a specified batch size.
 * <p>
 * This is done by creating batches with a specified <tt>startId</tt> and <tt>endId</tt>.
 * <p>
 * Use {@code @Register(classes =  AnalyticsScheduler.class)} to make this scheduler visible to the framework.
 */
@Register(classes = MongoEntityBatchEmitter.class)
public class MongoEntityBatchEmitter {

    private static final String TYPE = "type";
    private static final String START_ID = "startId";
    private static final String END_ID = "endId";

    @Part
    protected Mango mango;

    @Part
    protected Mixing mixing;

    public <E extends MongoEntity> void computeBatches(Class<E> type,
                                                        @Nullable Consumer<MongoQuery<E>> queryExtender,
                                                        int batchSize,
                                                        Consumer<JSONObject> batchConsumer) {
        TaskContext taskContext = TaskContext.get();
        ValueHolder<String> lastLimit = ValueHolder.of("");
        while (taskContext.isActive()) {
            ValueHolder<String> nextLimit = ValueHolder.of(lastLimit.get());
            MongoQuery<E> query = mango.select(type)
                                       .fields(MongoEntity.ID)
                                       .where(QueryBuilder.FILTERS.gt(MongoEntity.ID, lastLimit.get()));
            if (queryExtender != null) {
                queryExtender.accept(query);
            }

            query.orderAsc(MongoEntity.ID).limit(batchSize).iterateAll(e -> {
                nextLimit.set(e.getId());
            });

            JSONObject batch = new JSONObject();
            batch.put(TYPE, Mixing.getNameForType(type));
            batch.put(START_ID, lastLimit.get());
            batch.put(END_ID, nextLimit.get());
            batchConsumer.accept(batch);

            if (lastLimit.get().equals(nextLimit.get())) {
                return;
            }

            lastLimit.set(nextLimit.get());
        }
    }

    @SuppressWarnings("unchecked")
    public <E extends MongoEntity> void evaluateBatch(JSONObject batchDescription, Consumer<E> entityConsumer) {
        long startId = batchDescription.getLongValue(START_ID);
        long endId = batchDescription.getLongValue(END_ID);
        String typeName = batchDescription.getString(TYPE);

        Class<? extends MongoEntity> type = (Class<? extends MongoEntity>) mixing.getDescriptor(typeName).getType();
        mango.select(type)
             .where(QueryBuilder.FILTERS.gte(MongoEntity.ID, startId))
             .where(QueryBuilder.FILTERS.lte(MongoEntity.ID, endId))
             .iterateAll(e -> entityConsumer.accept((E) e));
    }
}
