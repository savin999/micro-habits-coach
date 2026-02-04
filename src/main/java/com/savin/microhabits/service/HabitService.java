package com.savin.microhabits.service;

import com.savin.microhabits.model.Habit;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles core habit operations (add, update, delete, and mark completion).
 * Keeps UI code separate from business logic.
 */
public class HabitService {

    private final List<Habit> habits = new ArrayList<>();

    /**
     * Returns a read-only view of the current habit list.
     */
    public List<Habit> getHabitsReadOnly() {
        return Collections.unmodifiableList(habits);
    }

    /**
     * Replaces the current list with loaded data (used after loading from storage).
     */
    public void replaceAll(List<Habit> loaded) {
        habits.clear();
        if (loaded != null) {
            habits.addAll(loaded);
        }
    }

    /**
     * Creates and stores a new habit.
     */
    public Habit addHabit(String name, String description) {
        Habit habit = new Habit(name, description);
        habits.add(habit);
        return habit;
    }

    /**
     * Removes a habit by ID. If it does not exist, nothing happens.
     */
    public void removeHabit(UUID habitId) {
        habits.removeIf(h -> h.getId().equals(habitId));
    }

    /**
     * Finds a habit by ID.
     */
    public Optional<Habit> findById(UUID habitId) {
        return habits.stream()
                .filter(h -> h.getId().equals(habitId))
                .findFirst();
    }

    /**
     * Marks today's completion status for the selected habit.
     */
    public void markToday(UUID habitId, boolean completed) {
        Habit habit = findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("Habit not found."));
        habit.markCompleted(LocalDate.now(), completed);
    }

    /**
     * Updates the name and description of a habit.
     */
    public void updateHabit(UUID habitId, String newName, String newDescription) {
        Habit habit = findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("Habit not found."));
        habit.setName(newName);
        habit.setDescription(newDescription);
    }

    /**
     * Deletes a habit (alias for removeHabit).
     */
    public void deleteHabit(UUID habitId) {
        removeHabit(habitId);
    }
}
