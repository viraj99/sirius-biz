/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.retention.onboarding;

import sirius.db.mixing.Composite;
import sirius.db.mixing.Mapping;
import sirius.db.mixing.annotations.Length;
import sirius.db.mixing.annotations.Lob;
import sirius.db.mixing.annotations.NullAllowed;

public class AcademyVideoData extends Composite {

    public static final Mapping ACADEMY_PROVIDER = Mapping.named("academyProvider");
    @Length(50)
    private String academyProvider;

    public static final Mapping ACADEMY = Mapping.named("academy");
    @Length(50)
    private String academy;

    public static final Mapping VIDEO_ID = Mapping.named("videoId");
    @Length(50)
    private String videoId;

    public static final Mapping TRACK = Mapping.named("track");
    @Length(255)
    private String track;

    public static final Mapping TITLE = Mapping.named("title");
    @Length(255)
    private String title;

    public static final Mapping DESCRIPTION = Mapping.named("description");
    @Lob
    @NullAllowed
    private String description;

    public static final Mapping DURATION = Mapping.named("duration");
    private int duration;

    public static final Mapping PRIORITY = Mapping.named("priority");
    private int priority;

    public static final Mapping REQUIRED_FEATURE = Mapping.named("requiredFeature");
    @Length(255)
    @NullAllowed
    private String requiredFeature;

    public static final Mapping REQUIRED_PERMISSION = Mapping.named("requiredPermission");
    @Length(255)
    @NullAllowed
    private String requiredPermission;

    public static final Mapping MANDATORY = Mapping.named("mandatory");
    private boolean mandatory;

    public String getAcademyProvider() {
        return academyProvider;
    }

    public void setAcademyProvider(String academyProvider) {
        this.academyProvider = academyProvider;
    }

    public String getAcademy() {
        return academy;
    }

    public void setAcademy(String academy) {
        this.academy = academy;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getRequiredFeature() {
        return requiredFeature;
    }

    public void setRequiredFeature(String requiredFeature) {
        this.requiredFeature = requiredFeature;
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }

    public void setRequiredPermission(String requiredPermission) {
        this.requiredPermission = requiredPermission;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }
}
