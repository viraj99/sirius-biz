/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.analytics.metrics;

import sirius.biz.analytics.scheduler.AnalyticsBatchExecutor;

public class MetricsBestEffortBatchExecutor extends AnalyticsBatchExecutor {

    @Override
    public String queueName() {
        return MetricsBestEffortSchedulerExecutor.QUEUE_METRICS_BEST_EFFORT;
    }
}
