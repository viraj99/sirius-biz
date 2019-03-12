/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.retention.onboarding;

import sirius.kernel.di.std.Named;

import java.util.List;

public interface AcademyProvider extends Named {

    List<AcademyVideoData> fetchVideos(String academy);
}
