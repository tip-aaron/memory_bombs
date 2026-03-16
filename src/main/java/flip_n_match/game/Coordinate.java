package flip_n_match.game;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Coordinate {
    private int row;
    private int col;

    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
