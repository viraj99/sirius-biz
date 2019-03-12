/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.analytics.metrics;

import sirius.kernel.di.std.Part;

import java.time.LocalDate;

public abstract class MonthlyGlobalMetricComputer {

    @Part
    protected Metrics metrics;

    public abstract void compute(LocalDate date);


}
