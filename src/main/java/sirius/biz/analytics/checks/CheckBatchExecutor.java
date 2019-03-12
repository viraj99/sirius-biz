/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.analytics.checks;

import sirius.biz.analytics.scheduler.AnalyticsBatchExecutor;

public class CheckBatchExecutor extends AnalyticsBatchExecutor {

    @Override
    public String queueName() {
        return CheckSchedulerExecutor.QUEUE_CHECKS;
    }
}
