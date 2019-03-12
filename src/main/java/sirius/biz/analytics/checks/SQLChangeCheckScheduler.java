/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.analytics.checks;

import sirius.biz.analytics.scheduler.AnalyticsBatchExecutor;
import sirius.biz.analytics.scheduler.AnalyticsScheduler;
import sirius.biz.analytics.scheduler.AnalyticsSchedulerExecutor;
import sirius.biz.analytics.scheduler.SQLAnalyticalTaskScheduler;
import sirius.biz.analytics.scheduler.ScheduleInterval;
import sirius.biz.protocol.TraceData;
import sirius.biz.protocol.Traced;
import sirius.db.jdbc.OMA;
import sirius.db.jdbc.SQLEntity;
import sirius.db.jdbc.SmartQuery;
import sirius.kernel.di.std.Register;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;

@Register(classes = AnalyticsScheduler.class)
public class SQLChangeCheckScheduler extends SQLAnalyticalTaskScheduler {

    @Override
    protected Class<?> getAnalyticalTaskType() {
        return ChangeCheck.class;
    }

    @Override
    public Class<? extends AnalyticsSchedulerExecutor> getExecutorForScheduling() {
        return CheckSchedulerExecutor.class;
    }

    @Override
    public Class<? extends AnalyticsBatchExecutor> getExecutorForTasks() {
        return CheckBatchExecutor.class;
    }

    @Override
    protected <E extends SQLEntity> void extendBatchQuery(SmartQuery<E> query) {
        if (!Traced.class.isAssignableFrom(query.getDescriptor().getType())) {
            throw new IllegalArgumentException("Entitis for which 'ChangeChecks' are create must implement 'Traced'.");
        }

        query.where(OMA.FILTERS.gt(Traced.TRACE.inner(TraceData.CHANGED_AT), LocalDateTime.now().minusDays(1)));
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
        return "sql-change-checks";
    }
}
