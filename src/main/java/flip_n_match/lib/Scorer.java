package flip_n_match.lib;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Scorer {

    private static final Preferences PREFS = Preferences.userNodeForPackage(Scorer.class);

    // Added difficulty to the record
    public record ScoreEntry(String name, long timeValue, long timestamp, String difficulty) implements Comparable<ScoreEntry> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter
                .ofPattern("EEEE, MMM dd, yyyy hh:mm a")
                .withZone(ZoneId.systemDefault())
                .withLocale(Locale.US);

        @Override
        public int compareTo(ScoreEntry other) {
            int timeComparison = Long.compare(this.timeValue, other.timeValue);

            if (timeComparison != 0) {
                return timeComparison;
            }

            return Long.compare(this.timestamp, other.timestamp);
        }

        public String getFormattedTime() {
            long minutes = timeValue / 60;
            long seconds = timeValue % 60;
            return String.format("%02d:%02d", minutes, seconds);
        }

        public String getFormattedDateTimestamp() {
            return FORMATTER.format(Instant.ofEpochMilli(this.timestamp));
        }
    }

    /**
     * Saves the player's score. Generates a timestamped default name if none is provided.
     * @param rawName    The name entered by the user (can be null/empty)
     * @param timeValue  The raw time score (e.g., elapsed seconds or milliseconds) to allow accurate sorting
     * @param difficulty The difficulty level the game was played on
     */
    public static void saveScore(String rawName, long timeValue, String difficulty) {
        long currentTimestamp = System.currentTimeMillis();
        String playerName = rawName;

        if (playerName == null || playerName.trim().isEmpty()) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            playerName = "Player_" + LocalDateTime.now().format(dtf);
        }

        // Value format: "PlayerName|TimeValue|Difficulty"
        String value = playerName.trim() + "|" + timeValue + "|" + difficulty;
        PREFS.put(String.valueOf(currentTimestamp), value);
    }

    /**
     * Retrieves all saved scores for a specific difficulty and returns them sorted.
     * @param targetDifficulty The difficulty to filter by (e.g. "EASY", "HARD")
     */
    public static List<ScoreEntry> getSortedScores(String targetDifficulty) {
        List<ScoreEntry> scores = new ArrayList<>();

        try {
            String[] keys = PREFS.keys();

            for (String key : keys) {
                String value = PREFS.get(key, null);
                if (value != null && value.contains("|")) {
                    // Use a regex limit to avoid dropping empty strings if malformed
                    String[] parts = value.split("\\|", -1);

                    if (parts.length >= 2) {
                        String name = parts[0];
                        long timeValue = Long.parseLong(parts[1]);

                        // Backwards compatibility for old saved scores that don't have a difficulty yet
                        String entryDifficulty = (parts.length >= 3 && !parts[2].isEmpty()) ? parts[2] : "EASY";

                        long timestamp = Long.parseLong(key); // The key is our timestamp

                        // Only add to the list if the difficulty matches the requested filter
                        if (entryDifficulty.equalsIgnoreCase(targetDifficulty)) {
                            scores.add(new ScoreEntry(name, timeValue, timestamp, entryDifficulty));
                        }
                    }
                }
            }
        } catch (BackingStoreException | NumberFormatException e) {
            System.err.println("Failed to load or parse scores from Preferences: " + e.getMessage());
        }

        Collections.sort(scores);

        return scores;
    }

    /**
     * Overload to retrieve ALL scores regardless of difficulty, sorted by time.
     */
    public static List<ScoreEntry> getSortedScores() {
        List<ScoreEntry> scores = new ArrayList<>();

        try {
            String[] keys = PREFS.keys();

            for (String key : keys) {
                String value = PREFS.get(key, null);
                if (value != null && value.contains("|")) {
                    String[] parts = value.split("\\|", -1);

                    if (parts.length >= 2) {
                        String name = parts[0];
                        long timeValue = Long.parseLong(parts[1]);
                        String entryDifficulty = (parts.length >= 3 && !parts[2].isEmpty()) ? parts[2] : "EASY";
                        long timestamp = Long.parseLong(key);

                        scores.add(new ScoreEntry(name, timeValue, timestamp, entryDifficulty));
                    }
                }
            }
        } catch (BackingStoreException | NumberFormatException e) {
            System.err.println("Failed to load or parse scores from Preferences: " + e.getMessage());
        }

        Collections.sort(scores);

        return scores;
    }

    /**
     * Utility to clear all high scores (useful for a "Reset Leaderboard" button)
     */
    public static void clearAllScores() {
        try {
            PREFS.clear();
        } catch (BackingStoreException e) {
            System.err.println("Failed to clear scores: " + e.getMessage());
        }
    }
}