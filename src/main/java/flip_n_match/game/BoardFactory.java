package flip_n_match.game;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;

import java.util.*;

public class BoardFactory {
    public static Board createBoard(int rows, int cols, int mineCount) {
        Board board = new Board(rows, cols);

        Set<Coordinate> mineCoordinates = placeMines(rows, cols, mineCount);
        List<Coordinate> memoryCoordinates = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; ++c) {
                Coordinate current = new Coordinate(r, c);

                if (mineCoordinates.contains(current)) {
                    board.setTile(new MineTile(current));
                } else {
                    int hazards = countAdjacentMines(current, mineCoordinates);

                    if (hazards == 0) {
                        memoryCoordinates.add(current);
                    } else {
                        board.setTile(new ClueTile(current, hazards));
                    }
                }
            }
        }

        Collections.shuffle(memoryCoordinates);

        List<String> availableIcons = getRandomizedIcons();

        int i = 0;
        int pairIndex = 0;

        while (i < memoryCoordinates.size() - 1) {
            String matchId = availableIcons.get(pairIndex % availableIcons.size());

            board.setTile(new MemoryTile(memoryCoordinates.get(i), matchId));
            board.setTile(new MemoryTile(memoryCoordinates.get(i + 1), matchId));

            pairIndex++;
            i += 2;
        }

        // if there was an excess tile
        if (i < memoryCoordinates.size()) {
            board.setTile(new SpecialTile(memoryCoordinates.get(i)));
        }

        return board;
    }

    private static List<String> getRandomizedIcons() {
        List<String> icons = new ArrayList<>();

        // The full path as it appears on the classpath
        String dirPath = "flip_n_match/ui/assets/icons/memory_match_icons";

        // Scan only the specific directory for efficiency
        try (ScanResult scanResult = new ClassGraph()
                .acceptPathsNonRecursive(dirPath)
                .scan()) {

            // Filter for just the .svg files
            ResourceList svgResources = scanResult.getResourcesWithExtension("svg");

            for (String path : svgResources.getPaths()) {
                // ClassGraph returns the full classpath path (e.g., flip_n_match/ui/assets/...)
                // Your SVGIconUIColor expects it to start with "memory_match_icons/"
                String fileName = path.substring(path.lastIndexOf('/') + 1);
                icons.add("memory_match_icons/" + fileName);
            }
        } catch (Exception e) {
            System.err.println("Failed to load memory match icons using ClassGraph.");
        }

        // Shuffle the loaded icons
        Collections.shuffle(icons);

        // Fallback in case the directory is empty or scanning fails
        if (icons.isEmpty()) {
            System.err.println("Warning: No SVG icons found in " + dirPath);
        }

        return icons;
    }

    private static Set<Coordinate> placeMines(int rows, int cols, int mineCount) {
        Set<Coordinate> mines = new HashSet<>();
        Random random = new Random();

        while (mines.size() < mineCount) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);

            mines.add(new Coordinate(r, c));
        }

        return mines;
    }

    private static int countAdjacentMines(Coordinate current, Set<Coordinate> mineCoordinates) {
        int count = 0;

        for (int rowOffset = -1; rowOffset <= 1; ++rowOffset) {
            for (int colOffset = -1; colOffset <= 1; ++colOffset) {
                if (rowOffset == 0 && colOffset == 0) {
                    continue;
                }

                Coordinate neighbor = new Coordinate(current.getRow() + rowOffset, current.getCol() + colOffset);

                if (mineCoordinates.contains(neighbor)) {
                    count += 1;
                }
            }
        }

        return count;
    }
}
