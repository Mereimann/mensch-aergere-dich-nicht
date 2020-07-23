package mensch.game;

public class Figure {

    private int team; // team id of this figure (0-3)
    private int position; // Cell ID this figure is on
    private int id; // figure id within this team (0-3)

    public Figure(int team, int id) {
        this.team = team;
        this.id = id;
        this.position = 56 + team * 4 + id;
    }

    public int getPosition() {
        return position;
    }

    public int getId() {
        return id;
    }

    public int getTeam() {
        return team;
    }

    public int getGlobalId() {
        return team * 4 + id;
    }

    public void setPosition(int newPosition) {
        position = newPosition;
    }

    public void resetPosition() {
        position = 56 + getGlobalId();
    }

    public boolean isInStart() {
        return position == 56 + getGlobalId();
    }

    public boolean isInHouse() { return position >= 40 + getTeam() * 4 && position <= 43 + getTeam() * 4;}
}
