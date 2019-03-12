/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.analytics.metrics;

import sirius.biz.analytics.scheduler.AnalyticsSchedulerExecutor;

public class MetricsGuaranteedSchedulerExecutor extends AnalyticsSchedulerExecutor {

    public static final String QUEUE_METRICS_SCHEDULER = "metrics-scheduler";

    @Override
    public String queueName() {
        return QUEUE_METRICS_SCHEDULER;
    }

}
