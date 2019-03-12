/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.analytics.indicators;

import sirius.biz.analytics.checks.ChangeCheck;
import sirius.biz.protocol.Traced;
import sirius.db.mixing.BaseEntity;
import sirius.kernel.di.PartCollection;
import sirius.kernel.di.std.Parts;

import java.time.LocalDateTime;

public abstract class BatchIndicatorCheck<E extends BaseEntity<?> & IndicatedEntity & Traced> extends ChangeCheck<E> {

    @Parts(Indicator.class)
    private static PartCollection<Indicator<?>> indicators;

    @SuppressWarnings("unchecked")
    @Override
    protected void execute(E entity) {
        for (Indicator<?> indicator : indicators) {
            try {
                if (indicator.getType().isAssignableFrom(entity.getClass()) && indicator.isBatch()) {
                    if (((Indicator<E>) indicator).executeFor(entity)) {
                        entity.getIndicators().setIndication(indicator.getName());
                    } else {
                        entity.getIndicators().clearIndication(indicator.getName());
                    }
                }
            } catch (Exception e) {
                //TODO
                entity.getIndicators().clearIndication(indicator.getName());
            }
        }

        entity.getTrace().setSilent(true);
        entity.getIndicators().setLastBatchIndicatorExecution(LocalDateTime.now());
        entity.getDescriptor().getMapper().update(entity);
    }
}
