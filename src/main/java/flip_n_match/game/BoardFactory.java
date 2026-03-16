package flip_n_match.game;

import java.util.*;

public class BoardFactory {
    private static final String[] ICONS = {
            "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🍒", "🍑",
            "🥭", "🍍", "🥥", "🥝", "🍅", "🍆", "🥑", "🥦", "🌽", "🥕",
            "🍔", "🍟", "🍕", "🌭", "🌮", "🌯", "🍿", "🍩", "🍪", "🍰",
            "🍫", "🍬", "🍭", "☕", "🍺", "🍷", "⚽", "🏀", "🏈", "⚾",
            "🎾", "🎱"
    };

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

        int i = 0;
        int pairIndex = 0;

        while (i < memoryCoordinates.size() - 1) {
            String matchId =  ICONS[pairIndex % ICONS.length];

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
