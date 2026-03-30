package flip_n_match.game;

import lombok.Getter;
import lombok.ToString;

@ToString
public class Board {
    @Getter
    private final int rows;
    @Getter
    private final int cols;

    private final Tile[][] grid;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Tile[rows][cols];
    }

    public void reset() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = null;
            }
        }
    }

    public Tile getTile(Coordinate coordinate) {
        if (isOutOfBounds(coordinate)) {
            return null;
        }

        return grid[coordinate.getRow()][coordinate.getCol()];
    }

    public void setTile(Tile tile) {
        Coordinate c = tile.getCoordinate();

        if (!isOutOfBounds(c)) {
            grid[c.getRow()][c.getCol()] = tile;
        }
    }

    // A helper method to easily loop through all tiles
    public Tile[] getAllTiles() {
        Tile[] allTiles = new Tile[rows * cols];
        int index = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                allTiles[index++] = grid[r][c];
            }
        }

        return allTiles;
    }

    public int countFlags() {
        int count = 0;

        for (int r = 0; r < getRows(); ++r) {
            for (int c = 0; c < getCols(); ++c) {
                var tile = getTile(Coordinate.builder().col(c).row(r).build());
                if (tile != null && tile.getStatus() == TileStatus.FLAGGED) {
                    count++;
                }
            }
        }

        return count;
    }

    private boolean isOutOfBounds(Coordinate coordinate) {
        return coordinate.getRow() < 0 || coordinate.getRow() >= rows ||
                coordinate.getCol() < 0 || coordinate.getCol() >= cols;
    }
}

