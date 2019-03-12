/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.analytics;

import sirius.biz.cluster.work.DistributedTaskExecutor;
import sirius.kernel.async.BackgroundLoop;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract  class BatchBasedBackgroundLoop extends BackgroundLoop {

    @Nonnull
    @Override
    public String getName() {
        return null;
    }

    protected abstract Class<? extends DistributedTaskExecutor> getExecutor();

    @Nullable
    @Override
    protected String doWork() throws Exception {

        return null;
    }

}
