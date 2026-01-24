package com.savin.microhabits.model;

import java.time.LocalDate;
import java.util.Objects;

public final class HabitLog {
    private final LocalDate date;
    private final boolean completed;

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
