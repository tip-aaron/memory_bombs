package flip_n_match.game;

public class MineTile extends Tile implements Tile.Explosive {
    public MineTile(Coordinate coordinate) {
        super(coordinate);
    }

    @Override
    public void reveal() {
        this.setStatus(TileStatus.REVEALED);
        detonate();
    }

    @Override
    public void detonate() {
        System.out.println("BOOM!");
    }
}
