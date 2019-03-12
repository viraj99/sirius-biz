/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.analytics.checks;

import sirius.biz.analytics.scheduler.AnalyticalTask;
import sirius.db.mixing.BaseEntity;

import java.time.LocalDate;

public abstract class DailyCheck<E extends BaseEntity<?>> implements AnalyticalTask<E> {

    @Override
    public void compute(LocalDate date, E entity) {
        execute(entity);
    }

    protected abstract void execute(E entity);
}
