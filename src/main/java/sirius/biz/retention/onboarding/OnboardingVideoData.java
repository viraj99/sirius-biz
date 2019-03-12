/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.retention.onboarding;

import sirius.db.KeyGenerator;
import sirius.db.mixing.Composite;
import sirius.db.mixing.Mapping;
import sirius.db.mixing.annotations.BeforeSave;
import sirius.db.mixing.annotations.Length;
import sirius.db.mixing.annotations.NullAllowed;
import sirius.kernel.commons.Strings;
import sirius.kernel.di.std.Part;

import java.time.LocalDateTime;

public class OnboardingVideoData extends Composite {

    public static final Mapping OWNER = Mapping.named("owner");
    @Length(50)
    private String owner;

    public static final Mapping UPDATE_KEY = Mapping.named("updateKey");
    @Length(50)
    private String updateKey;

    public static final Mapping RECOMMENDED = Mapping.named("recommended");
    private boolean recommended;

    public static final Mapping MANDATORY = Mapping.named("mandatory");
    private boolean mandatory;

    public static final Mapping PRIORITY = Mapping.named("priority");
    private int priority;

    public static final Mapping TRACK_HASH = Mapping.named("trackHash");
    @Length(50)
    private String trackHash;

    public static final Mapping CREATED = Mapping.named("created");
    private LocalDateTime created;

    public static final Mapping LAST_UPDATED = Mapping.named("lastUpdated");
    @NullAllowed
    private LocalDateTime lastUpdated;

    public static final Mapping LAST_SHOWN_IN_UI = Mapping.named("lastShownInUI");
    @NullAllowed
    private LocalDateTime lastShownInUI;

    public static final Mapping NUM_SHOWN_IN_UI = Mapping.named("numShownInUI");
    private int numShownInUI;

    public static final Mapping LAST_RECOMMENDED_PER_MAIL = Mapping.named("lastRecommendedPerMail");
    @NullAllowed
    private LocalDateTime lastRecommendedPerMail;

    public static final Mapping NUM_RECOMMENDED_PER_MAIL = Mapping.named("numRecommendedPerMail");
    private int numRecommendedPerMail;

    public static final Mapping LAST_WATCHED = Mapping.named("lastWatched");
    @NullAllowed
    private LocalDateTime lastWatched;

    public static final Mapping NUM_WATCHED = Mapping.named("numWatched");
    private int numWatched;

    public static final Mapping PERCENT_WATCHED = Mapping.named("percentWatched");
    private int percentWatched;

    public static final Mapping WATCHED = Mapping.named("watched");
    private boolean watched;

    public static final Mapping SKIPPED = Mapping.named("skipped");
    private boolean skipped;

    public static final Mapping LIKED = Mapping.named("liked");
    @NullAllowed
    private Boolean liked;

    public static final Mapping DELETED = Mapping.named("deleted");
    private boolean deleted;

    @Part
    private static KeyGenerator keyGen;

    @BeforeSave
    protected void beforeSave() {
        if (Strings.isEmpty(updateKey)) {
            updateKey = keyGen.generateSecureId();
        }
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getUpdateKey() {
        return updateKey;
    }

    public void setUpdateKey(String updateKey) {
        this.updateKey = updateKey;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTrackHash() {
        return trackHash;
    }

    public void setTrackHash(String trackHash) {
        this.trackHash = trackHash;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LocalDateTime getLastShownInUI() {
        return lastShownInUI;
    }

    public void setLastShownInUI(LocalDateTime lastShownInUI) {
        this.lastShownInUI = lastShownInUI;
    }

    public int getNumShownInUI() {
        return numShownInUI;
    }

    public void setNumShownInUI(int numShownInUI) {
        this.numShownInUI = numShownInUI;
    }

    public LocalDateTime getLastRecommendedPerMail() {
        return lastRecommendedPerMail;
    }

    public void setLastRecommendedPerMail(LocalDateTime lastRecommendedPerMail) {
        this.lastRecommendedPerMail = lastRecommendedPerMail;
    }

    public int getNumRecommendedPerMail() {
        return numRecommendedPerMail;
    }

    public void setNumRecommendedPerMail(int numRecommendedPerMail) {
        this.numRecommendedPerMail = numRecommendedPerMail;
    }

    public LocalDateTime getLastWatched() {
        return lastWatched;
    }

    public void setLastWatched(LocalDateTime lastWatched) {
        this.lastWatched = lastWatched;
    }

    public int getNumWatched() {
        return numWatched;
    }

    public void setNumWatched(int numWatched) {
        this.numWatched = numWatched;
    }

    public int getPercentWatched() {
        return percentWatched;
    }

    public void setPercentWatched(int percentWatched) {
        this.percentWatched = percentWatched;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public boolean isSkipped() {
        return skipped;
    }

    public void setSkipped(boolean skipped) {
        this.skipped = skipped;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
