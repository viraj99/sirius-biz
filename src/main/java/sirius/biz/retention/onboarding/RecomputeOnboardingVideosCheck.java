/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.retention.onboarding;

import sirius.biz.analytics.checks.DailyCheck;
import sirius.db.mixing.BaseEntity;
import sirius.kernel.di.std.Part;

import java.util.List;

public class RecomputeOnboardingVideosCheck<E extends BaseEntity<?>> extends DailyCheck<E> {

    @Part
    private OnboardingEngine onboardingEngine;

    @Override
    protected void execute(E entity) {
        List<AcademyVideoData> videos = onboardingEngine.getVideos(null, null);
        for(AcademyVideoData academyVideo : videos) {
            OnboardingVideo onboardingVideo = onboardingEngine.findOrCreateVideo(entity, academyVideo);


        }
    }

    @Override
    public Class<E> getType() {
        return null;
    }
}


