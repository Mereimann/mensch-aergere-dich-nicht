package mensch.game;

public class Cell {

    // -1 = no occupation
    // 0-3 = team number of occupying figure
    private int occupation = -1;

    public boolean isOccupied() {
        return occupation > -1;
    }

    public int getOccupyingTeam() {
        return occupation;
    }
}
