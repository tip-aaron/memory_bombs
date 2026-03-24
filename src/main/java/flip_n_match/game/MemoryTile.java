package flip_n_match.game;

public class MemoryTile extends Tile implements Tile.Matchable, Tile.ClueProvider {
    private final String matchId;
    private boolean matched;
    private boolean symbolRevealed;

    public MemoryTile(Coordinate coordinate, String matchId) {
        super(coordinate);
        this.matchId = matchId;
    }

    @Override
    public void reveal() {
        if (this.getStatus() == TileStatus.HIDDEN) {
            this.setStatus(TileStatus.REVEALED);
            // The GameState will listen for this reveal and check for matches
        }
    }

    @Override
    public boolean matches(Matchable other) {
        return other != null && this.matchId.matches(other.getMatchId());
    }

    @Override
    public String getMatchId() {
        return matchId;
    }

    @Override
    public boolean getIsMatched() {
        return matched;
    }

    @Override
    public void setMatched(boolean value) {
        this.matched = value;
    }

    @Override
    public int getAdjacentHazardCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean isSymbolRevealed() { return this.symbolRevealed; }

    @Override
    public void setSymbolRevealed(boolean revealed) { this.symbolRevealed = revealed; }
}
