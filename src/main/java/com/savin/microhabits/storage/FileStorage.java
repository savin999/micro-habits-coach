package com.savin.microhabits.storage;

import com.savin.microhabits.model.Habit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class FileStorage {

    private static final String VERSION_LINE = "MICROHABITS_V1";
    private final Path filePath;

    public FileStorage(Path filePath) {
        this.filePath = Objects.requireNonNull(filePath, "filePath");
    }

    public void save(List<Habit> habits) throws IOException {
        Files.createDirectories(filePath.getParent());

        StringBuilder sb = new StringBuilder();
        sb.append(VERSION_LINE).append("\n");

        for (Habit h : habits) {
            sb.append("H|")
              .append(h.getId()).append("|")
              .append(escape(h.getName())).append("|")
              .append(escape(h.getDescription()))
              .append("\n");

            for (var entry : h.getDailyStatusReadOnly().entrySet()) {
                LocalDate date = entry.getKey();
                boolean completed = Boolean.TRUE.equals(entry.getValue());
                sb.append("S|")
                  .append(date).append("|")
                  .append(completed ? "1" : "0")
                  .append("\n");
            }
            sb.append("END\n");
        }

        Files.writeString(
                filePath,
                sb.toString(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    public List<Habit> load() throws IOException {
        if (!Files.exists(filePath)) return new ArrayList<>();

        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        if (lines.isEmpty() || !VERSION_LINE.equals(lines.get(0).trim())) {
            throw new IOException("Unknown or incompatible save file format.");
        }

        List<Habit> result = new ArrayList<>();
        Habit current = null;

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            if (line.startsWith("H|")) {
                String[] parts = line.split("\\|", -1);
                if (parts.length < 3) throw new IOException("Corrupted habit header line.");

                UUID id = UUID.fromString(parts[1]);
                String name = unescape(parts[2]);
                String desc = unescape(parts.length >= 4 ? parts[3] : "");

                current = new Habit(id, name, desc);

            } else if (line.startsWith("S|")) {
                if (current == null) throw new IOException("Status line found before habit header.");

                String[] parts = line.split("\\|", -1);
                if (parts.length < 3) throw new IOException("Corrupted status line.");

                LocalDate date = LocalDate.parse(parts[1]);
                boolean completed = "1".equals(parts[2]);
                current.markCompleted(date, completed);

            } else if (line.equals("END")) {
                if (current != null) result.add(current);
                current = null;
            } else {
                throw new IOException("Unknown line type in save file: " + line);
            }
        }

        return result;
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("|", "\\p");
    }

    private static String unescape(String s) {
        if (s == null) return "";
        return s.replace("\\p", "|")
                .replace("\\n", "\n")
                .replace("\\\\", "\\");
    }
}
