package com.savin.microhabits.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a completion record for a habit on a specific date.
 */
public final class HabitLog {

    private final LocalDate date;
    private final boolean completed;

    /**
     * Creates a log entry for a given date.
     */
    public HabitLog(LocalDate date, boolean completed) {
        this.date = Objects.requireNonNull(date, "date");
        this.completed = completed;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isCompleted() {
        return completed;
    }
}
