/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.analytics.indicators;

import sirius.db.mixing.Mapping;

public interface IndicatedEntity {

    Mapping INDICATORS = Mapping.named("indicators");

    IndicatorData getIndicators();

}
