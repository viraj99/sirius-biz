/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.analytics.indicators;

import sirius.db.mixing.BaseEntity;

public interface Indicator<E extends BaseEntity<?> & IndicatedEntity> {

    Class<E> getType();

    boolean isBatch();

    boolean executeFor(E entity);

    String getName();
}
