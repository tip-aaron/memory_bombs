package flip_n_match.game;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public abstract class Tile {
    public interface Explosive {
        void detonate();
    }

    public interface Matchable {
        boolean getIsMatched();
        void setMatched(boolean value);
        String getMatchId();
        boolean matches(Matchable other);

        boolean isSymbolRevealed();
        void setSymbolRevealed(boolean revealed);
    }

    public interface ClueProvider {
        int getAdjacentHazardCount();
        boolean isEmpty();
    }

    @Getter
    private final Coordinate coordinate;

    @Getter @Setter
    private TileStatus status;

    public Tile(Coordinate coordinate) {
        this.coordinate = coordinate;
        this.status = TileStatus.HIDDEN;
    }

    public abstract void reveal();
}
