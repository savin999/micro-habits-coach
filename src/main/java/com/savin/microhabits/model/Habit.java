package com.savin.microhabits.model;

import java.time.LocalDate;
import java.util.*;

public class Habit {
    private final UUID id;
    private String name;
    private String description;

    // Key = date, Value = completed
    private final Map<LocalDate, Boolean> dailyStatus = new HashMap<>();

    public Habit(String name, String description) {
        this(UUID.randomUUID(), name, description);
    }

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

    public final void setName(String name) {
        String trimmed = (name == null) ? "" : name.trim();
        if (trimmed.isEmpty()) throw new IllegalArgumentException("Habit name cannot be empty.");
        if (trimmed.length() > 40) throw new IllegalArgumentException("Habit name too long (max 40).");
        this.name = trimmed;
    }

    public String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        this.description = (description == null) ? "" : description.trim();
    }

    public boolean isCompletedOn(LocalDate date) {
        return Boolean.TRUE.equals(dailyStatus.get(date));
    }

    public void markCompleted(LocalDate date, boolean completed) {
        Objects.requireNonNull(date, "date");
        dailyStatus.put(date, completed);
    }

    public Map<LocalDate, Boolean> getDailyStatusReadOnly() {
        return Collections.unmodifiableMap(dailyStatus);
    }

    // Current streak up to "today" (or given date)
    public int getStreak(LocalDate upToDate) {
        Objects.requireNonNull(upToDate, "upToDate");
        int streak = 0;
        LocalDate d = upToDate;

        while (isCompletedOn(d)) {
            streak++;
            d = d.minusDays(1);
        }
        return streak;
    }

    public int getStreakToday() {
        return getStreak(java.time.LocalDate.now());
    }

}
