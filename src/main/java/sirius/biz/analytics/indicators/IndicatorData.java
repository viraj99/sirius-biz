/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.analytics.indicators;

import sirius.biz.protocol.NoJournal;
import sirius.db.mixing.BaseEntity;
import sirius.db.mixing.Composite;
import sirius.db.mixing.annotations.BeforeSave;
import sirius.db.mixing.annotations.NullAllowed;
import sirius.db.mixing.annotations.Transient;
import sirius.kernel.commons.Explain;
import sirius.kernel.di.PartCollection;
import sirius.kernel.di.std.Parts;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class IndicatorData extends Composite {

    @Parts(Indicator.class)
    private static PartCollection<Indicator<?>> indicators;

    @Transient
    protected BaseEntity<?> owner;

    @NoJournal
    private final List<String> indications = new ArrayList<>();

    @NoJournal
    @NullAllowed
    private LocalDateTime lastBatchIndicatorExecution;

    public IndicatorData(BaseEntity<?> owner) {
        this.owner = owner;
    }

    public boolean setIndication(String indicator) {
        return indications.add(indicator);
    }

    @SuppressWarnings("squid:S2250")
    @Explain("There should only be some idicators present, so there is no performance hot spot expected.")
    public boolean clearIndication(String indicator) {
        return indications.remove(indicator);
    }

    @BeforeSave
    protected void beforeSave() {
        for (Indicator<?> indicator : indicators) {
            try {
                executeIndicator(indicator);
            } catch (Exception e) {
                //TODO
                clearIndication(indicator.getName());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <E extends BaseEntity<?> & IndicatedEntity> void executeIndicator(Indicator<E> indicator) {
        if (indicator.getType().isAssignableFrom(owner.getClass()) && !indicator.isBatch()) {
            if (indicator.executeFor((E) owner)) {
                setIndication(indicator.getName());
            } else {
                clearIndication(indicator.getName());
            }
        }
    }

    public List<String> getIndications() {
        return indications;
    }

    public LocalDateTime getLastBatchIndicatorExecution() {
        return lastBatchIndicatorExecution;
    }

    public void setLastBatchIndicatorExecution(LocalDateTime lastBatchIndicatorExecution) {
        this.lastBatchIndicatorExecution = lastBatchIndicatorExecution;
    }
}
