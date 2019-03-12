/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.analytics.checks;

import sirius.biz.analytics.scheduler.AnalyticsSchedulerExecutor;

public class CheckSchedulerExecutor extends AnalyticsSchedulerExecutor {

    public static final String QUEUE_CHECKS = "checks";

    @Override
    public String queueName() {
        return QUEUE_CHECKS;
    }
}
