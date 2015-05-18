/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.codelists;

import sirius.biz.tenants.TenantAware;
import sirius.mixing.Column;
import sirius.mixing.annotations.Length;
import sirius.mixing.annotations.NullAllowed;
import sirius.mixing.annotations.Trim;
import sirius.mixing.annotations.Unique;

/**
 * Created by aha on 11.05.15.
 */
public class CodeList extends TenantAware {

    @Trim
    @Length(length = 50)
    @Unique(within = "tenant")
    private String code;
    public static final Column CODE = Column.named("code");

    @Trim
    @NullAllowed
    @Length(length = 150)
    private String name;
    public static final Column NAME = Column.named("name");

    @Trim
    @NullAllowed
    @Length(length = 1024)
    private String description;
    public static final Column DESCRIPTION = Column.named("description");

    private boolean autofill = true;
    public static final Column AUTO_FILL = Column.named("autofill");

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAutofill() {
        return autofill;
    }

    public void setAutofill(boolean autofill) {
        this.autofill = autofill;
    }
}
