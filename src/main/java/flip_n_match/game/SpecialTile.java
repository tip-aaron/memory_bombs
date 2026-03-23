package flip_n_match.game;

public class SpecialTile extends Tile implements Tile.ClueProvider {

    public SpecialTile(Coordinate coordinate) {
        super(coordinate);
    }

    @Override
    public void reveal() {
        if (this.getStatus() == TileStatus.HIDDEN) {
            this.setStatus(TileStatus.REVEALED);
        }
    }

    public void activateSpecialAbility() {
        System.out.println("Special tile activated! Maybe it reveals all mines for 1 second?");
    }


    @Override
    public int getAdjacentHazardCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
