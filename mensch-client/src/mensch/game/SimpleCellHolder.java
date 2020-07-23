package mensch.game;

import java.util.ArrayList;

public class SimpleCellHolder implements CellHolder {

    private ArrayList<Cell> cells;

    public SimpleCellHolder(int amount) {
        cells = new ArrayList<>(amount);
    }

    public int size() {
        return cells.size();
    }

    @Override
    public Cell getCell(int id) {
        return cells.get(id);
    }
}
