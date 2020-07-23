package mensch.game;

import java.util.ArrayList;

public class Board implements CellHolder {

    private SimpleCellHolder mainPath;
    private SimpleCellHolder[] houses;
    private SimpleCellHolder[] starts;

    public Board() {
        mainPath = new SimpleCellHolder(40);
        houses = new SimpleCellHolder[4];
        starts = new SimpleCellHolder[4];
        for(int i = 0; i < 4; i++) {
            houses[i] = new SimpleCellHolder(4);
            starts[i] = new SimpleCellHolder(4);
        }
    }

    @Override
    public Cell getCell(int id) {
        if(id < 0) {
            return null;
        }
        if(id < mainPath.size()) {
            return mainPath.getCell(id);
        } else {
            int newId = id - mainPath.size();
            if(newId < 16) {
                SimpleCellHolder house = houses[newId / 4];
                return house.getCell(newId % 4);
            } else if(newId < 32) {
                newId -= 16;
                SimpleCellHolder start = starts[newId / 4];
                return start.getCell(newId % 4);
            }
        }
        return null;
    }

    public Cell getNextCell(int id, int team) {
        return getCell(getNextCellId(id, team));
    }

    public int getNextCellId(int id, int team) {
        switch(id) {
            case -1:
                return -1;
            case 39: // entries into houses
                return team == 0 ? 40 : 0;
            case 9:
                return team == 1 ? 44 : 10;
            case 19:
                return team == 2 ? 48 : 20;
            case 29:
                return team == 3 ? 52 : 30;
            case 56: case 57: case 58: case 59: // starts
                return 0;
            case 60: case 61: case 62: case 63:
                return 10;
            case 64: case 65: case 66: case 67:
                return 20;
            case 68: case 69: case 70: case 71:
                return 30;
            case 43: case 47: case 51: case 55: // end of house
                return -1;
            default:
                return id + 1;
        }
    }

    public int getCellIdAhead(int id, int team, int stepsAhead) {
        int cellId = id;
        for(int i = 0; i < stepsAhead; i++) {
            cellId = getNextCellId(cellId, team);
        }
        return cellId;
    }

    public Cell getCellAhead(int id, int team, int stepsAhead) {
        return getCell(getCellIdAhead(id, team, stepsAhead));
    }
}
