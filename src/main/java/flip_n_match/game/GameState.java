package flip_n_match.game;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import javax.swing.Timer; // Added for the UI delay

import flip_n_match.game.events.GameEventMessenger;
import flip_n_match.lib.Stopwatch;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

public class GameState {
    @Getter
    private final Stopwatch stopwatch;

    @Getter
    private Board board;

    // To track the memory match logic
    private Tile.Matchable firstFlippedTile = null;

    // To prevent the user from clicking other tiles while mismatched tiles are
    // flipping back
    @Getter
    private boolean inputLocked = false;

    // To stop the game when they hit a bomb or win
    @Getter
    private boolean isGameOver = false;

    @Setter
    private Runnable onBoardUpated;

    public GameState(Consumer<String> stopwatchListener, Runnable onBoardUpated) {
        this.stopwatch = new Stopwatch(stopwatchListener);
        this.onBoardUpated = onBoardUpated;
    }

    public void pauseGame() {
        if (isGameOver) return;

        inputLocked = true;
        stopwatch.stop();
    }

    public void resumeGame() {
        if (isGameOver) return;

        inputLocked = false;

        if (!stopwatch.isRunning()) {
            stopwatch.start();
        }
    }

    public void initializeGame(int rows, int cols, int mineCount) {
        this.board = BoardFactory.createBoard(rows, cols, mineCount);
        this.firstFlippedTile = null;
        this.inputLocked = false;
        this.isGameOver = false;
        this.clearAndStop();
    }

    public void start() {
        this.stopwatch.start();
    }

    public void clearAndStop() {
        this.stopwatch.stop();
        this.stopwatch.reset();
    }

    public void onTileRightClicked(Coordinate coordinate) {
        if (isGameOver || inputLocked) return;

        Tile clickedTile = board.getTile(coordinate);

        if (clickedTile == null) return;

        if (clickedTile.getStatus() == TileStatus.HIDDEN) {
            clickedTile.setStatus(TileStatus.FLAGGED);
        } else if (clickedTile.getStatus() == TileStatus.FLAGGED) {
            clickedTile.setStatus(TileStatus.HIDDEN);
        }
    }

    public void onTileClicked(Coordinate coordinate) {
        if (board == null || inputLocked || isGameOver) {
            return;
        }

        Tile clickedTile = board.getTile(coordinate);

        if (clickedTile == null) {
            return;
        }

        if (!stopwatch.isRunning()) {
            start();
        }

        if (clickedTile.getStatus() == TileStatus.FLAGGED) {
            return;
        }

        switch (clickedTile.getStatus()) {
            case TileStatus.HIDDEN -> {
                clickedTile.reveal();

                switch (clickedTile) {
                    case Tile.Explosive bomb -> handleExplosive(bomb);
                    case Tile.ClueProvider clue when clue.isEmpty() -> {
                        floodFill(coordinate);
                    }
                    case SpecialTile special -> special.activateSpecialAbility();
                    default -> {
                    }
                }
            }
            case TileStatus.REVEALED -> {
                if (clickedTile instanceof Tile.Matchable matchable && !matchable.getIsMatched()
                        && !matchable.isSymbolRevealed()) {
                    matchable.setSymbolRevealed(true);
                    handleMatchable(matchable);
                }
            }
            default -> {
            }
        }

        if (onBoardUpated != null) {
            onBoardUpated.run();
        }

        checkWinCondition();
    }

    private void floodFill(Coordinate coordinate) {
        Queue<Coordinate> queue = new LinkedList<>();

        queue.add(coordinate);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();

            for (int rowOffset = -1; rowOffset <= 1; ++rowOffset) {
                for (int colOffset = -1; colOffset <= 1; ++colOffset) {
                    Coordinate neighborCoordinates = new Coordinate(current.getRow() + rowOffset,
                            current.getCol() + colOffset);
                    Tile neighbor = board.getTile(neighborCoordinates);

                    if (neighbor != null && neighbor.getStatus() == TileStatus.HIDDEN) {
                        neighbor.reveal();

                        // If the neighbor is ALSO an empty tile (a MemoryTile), add it to the queue to
                        // continue the cascade
                        if (neighbor instanceof Tile.ClueProvider clue && clue.isEmpty()) {
                            queue.add(neighborCoordinates);
                        }
                    }
                }
            }
        }
    }

    private void handleMatchable(Tile.Matchable tile) {
        if (firstFlippedTile == null) {
            firstFlippedTile = tile;
            return;
        }

        if (firstFlippedTile.matches(tile)) {
            firstFlippedTile.setMatched(true);
            tile.setMatched(true);
            firstFlippedTile = null;
        } else {
            inputLocked = true;

            Timer delayTimer = getDelayTimer(tile);
            delayTimer.start();
        }
    }

    private @NotNull Timer getDelayTimer(Tile.Matchable tile) {
        Timer delayTimer = new Timer(500, e -> {
            if (firstFlippedTile != null) {
                firstFlippedTile.setSymbolRevealed(false);
            }

            if (tile != null) {
                tile.setSymbolRevealed(false);
            }

            firstFlippedTile = null;

            // 3. UNLOCK THE INPUT
            inputLocked = false;

            if (onBoardUpated != null) {
                onBoardUpated.run();
            }
        });

        delayTimer.setRepeats(false);
        return delayTimer;
    }

    private void handleExplosive(Tile.Explosive bomb) {
        this.isGameOver = true;

        this.stopwatch.stop();
        bomb.detonate();

        for (Tile tile : board.getAllTiles()) {
            if (tile instanceof Tile.Explosive) {
                tile.setStatus(TileStatus.REVEALED);
            }
        }

        GameEventMessenger.getInstance().triggerGameOver(false);
    }

    private void checkWinCondition() {
        if (isGameOver || board == null) return;

        boolean allSafeRevealed = true;
        boolean allMatched = true;

        for (Tile tile : board.getAllTiles()) {
            // Check if non-mines are revealed
            if (tile == null) {
                continue;
            }

            if (!(tile instanceof Tile.Explosive) && tile.getStatus() == TileStatus.HIDDEN) {
                allSafeRevealed = false;
            }

            if (tile instanceof Tile.Matchable matchable && !matchable.getIsMatched()) {
                allMatched = false;
            }
        }

        if (allSafeRevealed && allMatched) {
            this.isGameOver = true;
            this.stopwatch.stop();

            // --- BROADCAST WIN ---
            GameEventMessenger.getInstance().triggerGameOver(true);
        }
    }
}