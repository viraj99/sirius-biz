/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.analytics.metrics.jdbc;

import sirius.db.mixing.Mapping;
import sirius.db.mixing.annotations.Index;
import sirius.kernel.di.std.Framework;

/**
 * Stores monthly metrics as a table.
 * <p>
 * Note that metrics should only be accessed via {@link sirius.biz.analytics.metrics.Metrics}.
 */
@Framework(SQLMetrics.FRAMEWORK_JDBC_METRICS)
@Index(name = "lookup", columns = {"targetType", "targetId", "name", "year", "month"})
public class MonthlyMetric extends YearlyMetric {

    /**
     * Contains the month for which this metric is recorded
     */
    public static final Mapping MONTH = Mapping.named("month");
    private int month;

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
