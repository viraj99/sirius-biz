/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.mongo;

public class PrefixSearchableTestEntity extends PrefixSearchableEntity {

    @PrefixSearchContent
    private String test;

    private String unsearchableTest;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getUnsearchableTest() {
        return unsearchableTest;
    }

    public void setUnsearchableTest(String unsearchableTest) {
        this.unsearchableTest = unsearchableTest;
    }
}
