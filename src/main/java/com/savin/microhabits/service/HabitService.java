package com.savin.microhabits.service;

import com.savin.microhabits.model.Habit;

import java.time.LocalDate;
import java.util.*;

public class HabitService {
    private final List<Habit> habits = new ArrayList<>();

    public List<Habit> getHabitsReadOnly() {
        return Collections.unmodifiableList(habits);
    }

    public void replaceAll(List<Habit> loaded) {
        habits.clear();
        if (loaded != null) habits.addAll(loaded);
    }

    public Habit addHabit(String name, String description) {
        Habit habit = new Habit(name, description);
        habits.add(habit);
        return habit;
    }

    public void removeHabit(UUID habitId) {
        habits.removeIf(h -> h.getId().equals(habitId));
    }

    public Optional<Habit> findById(UUID habitId) {
        return habits.stream().filter(h -> h.getId().equals(habitId)).findFirst();
    }

    public void markToday(UUID habitId, boolean completed) {
        Habit habit = findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("Habit not found."));
        habit.markCompleted(LocalDate.now(), completed);
    }
    public void updateHabit(UUID habitId, String newName, String newDescription) {
    Habit habit = findById(habitId)
            .orElseThrow(() -> new IllegalArgumentException("Habit not found."));
    habit.setName(newName);
    habit.setDescription(newDescription);
    }

    public void deleteHabit(UUID habitId) {
        removeHabit(habitId);
    }   

}
