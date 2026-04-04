package flip_n_match.lib;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Scorer {

    private static final Preferences PREFS = Preferences.userNodeForPackage(Scorer.class);

    public static final long NANOS_PER_SECOND = 1_000_000_000L;

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

    public static long convertToSeconds(long rawNanos) {
        return rawNanos / NANOS_PER_SECOND;
    }

    private static long normalizeTimeValue(long timeValue) {
        if (timeValue > 100_000_000L) {
            return timeValue / NANOS_PER_SECOND;
        } else if (timeValue > 86_400L) {
            return timeValue / 1_000L;
        }
        return timeValue;
    }

    /**
     * Saves the player's score. Generates a timestamped default name if none is provided.
     * @param rawName    The name entered by the user (can be null/empty)
     * @param timeValueSeconds  The raw time score (e.g., elapsed seconds or milliseconds) to allow accurate sorting
     * @param difficulty The difficulty level the game was played on
     */
    public static void saveScore(String rawName, long timeValueSeconds, String difficulty) {
        long currentTimestamp = System.currentTimeMillis();
        String playerName = rawName;

        if (playerName == null || playerName.trim().isEmpty()) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            playerName = "Player_" + LocalDateTime.now().format(dtf);
        }

        // Value format: "PlayerName|TimeValue|Difficulty"
        String value = playerName.trim() + "|" + timeValueSeconds + "|" + difficulty;
        PREFS.put(String.valueOf(currentTimestamp), value);
    }

    public static int getRank(String playerName, long timeValueSeconds, String difficulty) {
        List<ScoreEntry> scores = getSortedScores(difficulty);
        for (int i = 0; i < scores.size(); i++) {
            ScoreEntry entry = scores.get(i);
            if (entry.name().equals(playerName) && entry.timeValue() == timeValueSeconds) {
                return i + 1;
            }
        }
        return -1;
    }

    private static List<ScoreEntry> fetchScores(String targetDifficulty) {
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
                        long rawTimeValue = Long.parseLong(parts[1]);

                        // Backwards compatibility for old saved scores that don't have a difficulty yet
                        String entryDifficulty = (parts.length >= 3 && !parts[2].isEmpty()) ? parts[2] : "EASY";

                        long timestamp = Long.parseLong(key); // The key is our timestamp

                        long normalizedTime = normalizeTimeValue(rawTimeValue);

                        if (normalizedTime != rawTimeValue) {
                            String healedValue = name + "|" + normalizedTime + "|" + entryDifficulty;
                            PREFS.put(key, healedValue);
                        }

                        // Only add to the list if the difficulty matches the requested filter (or if filter is null)
                        if (targetDifficulty == null || entryDifficulty.equalsIgnoreCase(targetDifficulty)) {
                            scores.add(new ScoreEntry(name, normalizedTime, timestamp, entryDifficulty));
                        }
                    }
                }
            }
        } catch (BackingStoreException | NumberFormatException e) {
            System.err.println("Failed to load or parse scores from Preferences: " + e.getMessage());
        }

        return scores;
    }

    /**
     * Retrieves all saved scores for a specific difficulty and returns them sorted.
     * @param targetDifficulty The difficulty to filter by (e.g. "EASY", "HARD")
     */
    public static List<ScoreEntry> getSortedScores(String targetDifficulty) {
        List<ScoreEntry> scores = fetchScores(targetDifficulty);
        MergeSorter.sort(scores);
        return scores;
    }

    /**
     * Overload to retrieve ALL scores regardless of difficulty, sorted by time.
     */
    public static List<ScoreEntry> getSortedScores() {
        List<ScoreEntry> scores = fetchScores(null);
        MergeSorter.sort(scores);
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