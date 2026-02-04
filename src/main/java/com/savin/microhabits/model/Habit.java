package com.savin.microhabits.model;

import java.time.LocalDate;
import java.util.*;

/**
 * Represents a single habit and its daily completion history.
 */
public class Habit {

    private final UUID id;
    private String name;
    private String description;

    // Stores completion status per date
    private final Map<LocalDate, Boolean> dailyStatus = new HashMap<>();

    /**
     * Creates a new habit with a generated identifier.
     */
    public Habit(String name, String description) {
        this(UUID.randomUUID(), name, description);
    }

    /**
     * Creates a habit with a fixed ID.
     * Used when loading habits from storage.
     */
    public Habit(UUID id, String name, String description) {
        this.id = Objects.requireNonNull(id, "id");
        setName(name);
        setDescription(description);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * Updates the habit name with basic validation.
     */
    public final void setName(String name) {
        String trimmed = (name == null) ? "" : name.trim();

        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Habit name cannot be empty.");
        }
        if (trimmed.length() > 40) {
            throw new IllegalArgumentException("Habit name too long (max 40 characters).");
        }

        this.name = trimmed;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Updates the habit description.
     */
    public final void setDescription(String description) {
        this.description = (description == null) ? "" : description.trim();
    }

    /**
     * Checks whether the habit was completed on a given date.
     */
    public boolean isCompletedOn(LocalDate date) {
        return Boolean.TRUE.equals(dailyStatus.get(date));
    }

    /**
     * Marks the habit as completed or not completed on a specific date.
     */
    public void markCompleted(LocalDate date, boolean completed) {
        Objects.requireNonNull(date, "date");
        dailyStatus.put(date, completed);
    }

    /**
     * Returns a read-only view of the completion history.
     */
    public Map<LocalDate, Boolean> getDailyStatusReadOnly() {
        return Collections.unmodifiableMap(dailyStatus);
    }

    /**
     * Calculates the current completion streak up to the given date.
     */
    public int getStreak(LocalDate upToDate) {
        Objects.requireNonNull(upToDate, "upToDate");

        int streak = 0;
        LocalDate current = upToDate;

        while (isCompletedOn(current)) {
            streak++;
            current = current.minusDays(1);
        }

        return streak;
    }

    /**
     * Returns the current streak up to today.
     */
    public int getStreakToday() {
        return getStreak(LocalDate.now());
    }
}
