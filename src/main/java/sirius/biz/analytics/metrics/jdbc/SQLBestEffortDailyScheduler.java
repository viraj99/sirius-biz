/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.analytics.metrics.jdbc;

import sirius.biz.analytics.metrics.MetricsBestEffortBatchExecutor;
import sirius.biz.analytics.metrics.MetricsBestEffortSchedulerExecutor;
import sirius.biz.analytics.metrics.MonthlyMetricComputer;
import sirius.biz.analytics.scheduler.AnalyticsBatchExecutor;
import sirius.biz.analytics.scheduler.AnalyticsScheduler;
import sirius.biz.analytics.scheduler.AnalyticsSchedulerExecutor;
import sirius.biz.analytics.scheduler.SQLAnalyticalTaskScheduler;
import sirius.biz.analytics.scheduler.ScheduleInterval;
import sirius.kernel.di.std.Register;

import javax.annotation.Nonnull;

@Register(classes = AnalyticsScheduler.class)
public class SQLBestEffortDailyScheduler extends SQLAnalyticalTaskScheduler {

    @Override
    protected Class<?> getAnalyticalTaskType() {
        return MonthlyMetricComputer.class;
    }

    @Override
    public Class<? extends AnalyticsSchedulerExecutor> getExecutorForScheduling() {
        return MetricsBestEffortSchedulerExecutor.class;
    }

    @Override
    public Class<? extends AnalyticsBatchExecutor> getExecutorForTasks() {
        return MetricsBestEffortBatchExecutor.class;
    }

    @Override
    public boolean useBestEffortScheduling() {
        return true;
    }

    @Override
    public ScheduleInterval getInterval() {
        return ScheduleInterval.DAILY;
    }

    @Nonnull
    @Override
    public String getName() {
        return "sql-metrics-best-effort-daily";
    }
}
