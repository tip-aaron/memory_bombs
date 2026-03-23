package flip_n_match.lib;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Scorer {

    private static final Preferences PREFS = Preferences.userNodeForPackage(Scorer.class);

    public record ScoreEntry(String name, long timeValue, long timestamp) implements Comparable<ScoreEntry> {
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
    }

    /**
     * Saves the player's score. Generates a timestamped default name if none is provided.
     * * @param rawName   The name entered by the user (can be null/empty)
     * @param timeValue The raw time score (e.g., elapsed seconds or milliseconds) to allow accurate sorting
     */
    public static void saveScore(String rawName, long timeValue) {
        long currentTimestamp = System.currentTimeMillis();
        String playerName = rawName;

        if (playerName == null || playerName.trim().isEmpty()) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            playerName = "Player_" + LocalDateTime.now().format(dtf);
        }

        // Value format: "PlayerName|TimeValue"
        String value = playerName.trim() + "|" + timeValue;
        PREFS.put(String.valueOf(currentTimestamp), value);
    }

    /**
     * Retrieves all saved scores and returns them sorted using a stable sort.
     */
    public static List<ScoreEntry> getSortedScores() {
        List<ScoreEntry> scores = new ArrayList<>();

        try {
            String[] keys = PREFS.keys();

            for (String key : keys) {
                String value = PREFS.get(key, null);
                if (value != null && value.contains("|")) {
                    String[] parts = value.split("\\|");

                    if (parts.length == 2) {
                        String name = parts[0];
                        long timeValue = Long.parseLong(parts[1]);
                        long timestamp = Long.parseLong(key); // The key is our timestamp

                        scores.add(new ScoreEntry(name, timeValue, timestamp));
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