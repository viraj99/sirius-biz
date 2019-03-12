/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.analytics;

import com.alibaba.fastjson.JSONObject;
import sirius.db.jdbc.OMA;
import sirius.db.jdbc.SQLEntity;
import sirius.db.jdbc.SmartQuery;
import sirius.db.mixing.Mixing;
import sirius.kernel.async.TaskContext;
import sirius.kernel.commons.ValueHolder;
import sirius.kernel.di.std.Part;
import sirius.kernel.di.std.Register;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Provides a base implementation which schedules all JDBC entities of a given type using a specified batch size.
 * <p>
 * This is done by creating batches with a specified <tt>startId</tt> and <tt>endId</tt>.
 * <p>
 * Use {@code @Register(classes =  AnalyticsScheduler.class)} to make this scheduler visible to the framework.
 */
@Register(classes = SQLEntityBatchEmitter.class)
public class SQLEntityBatchEmitter {

    private static final String TYPE = "type";
    private static final String START_ID = "startId";
    private static final String END_ID = "endId";

    @Part
    protected OMA oma;

    @Part
    protected Mixing mixing;

    public <E extends SQLEntity> void computeBatches(Class<E> type,
                                                     @Nullable Consumer<SmartQuery<E>> queryExtender,
                                                     int batchSize,
                                                     Consumer<JSONObject> batchConsumer) {
        TaskContext taskContext = TaskContext.get();
        ValueHolder<Long> lastLimit = ValueHolder.of(0L);
        while (taskContext.isActive()) {
            ValueHolder<Long> nextLimit = ValueHolder.of(lastLimit.get());
            SmartQuery<E> query = oma.selectFromSecondary(type)
                                     .fields(SQLEntity.ID)
                                     .where(OMA.FILTERS.gt(SQLEntity.ID, lastLimit.get()));
            if (queryExtender != null) {
                queryExtender.accept(query);
            }

            query.orderAsc(SQLEntity.ID).limit(batchSize).iterateAll(e -> {
                nextLimit.set(e.getId());
            });

            JSONObject batch = new JSONObject();
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
    public <E extends SQLEntity> void evaluateBatch(JSONObject batchDescription, Consumer<E> entityConsumer) {
        long startId = batchDescription.getLongValue(START_ID);
        long endId = batchDescription.getLongValue(END_ID);
        String typeName = batchDescription.getString(TYPE);

        Class<? extends SQLEntity> type = (Class<? extends SQLEntity>) mixing.getDescriptor(typeName).getType();
        oma.selectFromSecondary(type)
           .where(OMA.FILTERS.gte(SQLEntity.ID, startId))
           .where(OMA.FILTERS.lte(SQLEntity.ID, endId))
           .iterateAll(e -> entityConsumer.accept((E) e));
    }
}
