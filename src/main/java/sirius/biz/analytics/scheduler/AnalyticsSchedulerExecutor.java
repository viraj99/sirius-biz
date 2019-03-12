/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.analytics.scheduler;

import com.alibaba.fastjson.JSONObject;
import sirius.biz.cluster.work.DistributedTaskExecutor;
import sirius.biz.cluster.work.DistributedTasks;
import sirius.kernel.commons.Value;
import sirius.kernel.di.GlobalContext;
import sirius.kernel.di.std.Part;

import java.time.LocalDate;
import java.util.function.Consumer;

/**
 * Provides a base implementation for executing an {@link AnalyticsScheduler}.
 * <p>
 * This reads the scheduler name and date from each given task description and instructs the appropriate scheduler via
 * {@link AnalyticsScheduler#scheduleBatches(Consumer)} to compute all batches to be executed. The emitted batches
 * are then submitted via {@link DistributedTasks} to be executed by the appropriate {@link AnalyticsBatchExecutor}.
 */
public abstract class AnalyticsSchedulerExecutor extends DistributedTaskExecutor {

    @Part
    protected GlobalContext ctx;

    @Part
    protected DistributedTasks cluster;

    @Override
    public void executeWork(JSONObject context) throws Exception {
        String schedulerName = context.getString(AnalyticalEngine.CONTEXT_SCHEDULER_NAME);
        LocalDate date = Value.of(context.get(AnalyticalEngine.CONTEXT_DATE)).asLocalDate(LocalDate.now());
        AnalyticsScheduler scheduler = ctx.findPart(schedulerName, AnalyticsScheduler.class);
        scheduler.scheduleBatches(batch -> scheduleBatch(scheduler, date, batch));
    }

    protected void scheduleBatch(AnalyticsScheduler scheduler, LocalDate date, JSONObject batch) {
        batch.put(AnalyticalEngine.CONTEXT_SCHEDULER_NAME, scheduler.getName());
        batch.put(AnalyticalEngine.CONTEXT_DATE, date);
        cluster.submitFIFOTask(scheduler.getExecutorForTasks(), batch);
    }
}
