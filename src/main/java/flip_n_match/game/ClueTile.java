package flip_n_match.game;

public class ClueTile extends Tile implements Tile.ClueProvider {
    private final int adjacentHazards;

    public ClueTile(Coordinate coordinate, int adjacentHazards) {
        super(coordinate);
        this.adjacentHazards = adjacentHazards;
    }

    @Override
    public void reveal() {
        if (this.getStatus() == TileStatus.HIDDEN) {
            this.setStatus(TileStatus.REVEALED);

            // Note: If adjacentHazards == 0, the GameState will intercept this
            // reveal event and trigger a flood-fill to reveal surrounding tiles.
        }
    }

    @Override
    public int getAdjacentHazardCount() {
        return this.adjacentHazards;
    }

    @Override
    public boolean isEmpty() {
        return this.adjacentHazards == 0;
    }
}
