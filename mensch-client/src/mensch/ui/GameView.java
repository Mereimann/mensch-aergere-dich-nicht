package mensch.ui;

import mensch.game.Cell;
import mensch.game.Figure;
import mensch.game.Game;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.LinkedList;

public class GameView extends JPanel {

    private Game game;

    private int width, height;
    private int scale;
    private int cellSize;

    private Layout layout;

    private CellColors colors;

    private int mouseX, mouseY;

    private boolean showNumbers = false;

    private Font fontDice = new Font("Arial", Font.BOLD, 24);
    private Font fontMsgs = new Font("Arial", Font.PLAIN, 12);

    private LinkedList<String> messages = new LinkedList<>();

    public GameView() {
        this.width = 800;
        this.height = 600;
        this.scale = Math.min(width, height) - 80;
        this.cellSize = (int)(0.08 * this.scale);
        if(this.cellSize % 2 == 0) {
            this.cellSize++;
        }
        this.setPreferredSize(new Dimension(width, height));
        this.layout = new Layout();
        this.colors = new CellColors(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW);
        MouseInputAdapter mouseInput = new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(!messages.isEmpty()) {
                    messages.removeFirst();
                    return;
                }
                if(e.getButton() == 3) {
                    showNumbers = !showNumbers;
                    return;
                }
                if(game == null) {
                    return;
                }
                if(isOverOwnDie(mouseX, mouseY) && !game.hasRolled()) {
                    game.roll();
                } else {
                    if(game.hasRolled() && game.hasTurn()) {
                        int cell = getCellIdOver(mouseX, mouseY);
                        if(cell > -1) {
                            Figure f = game.getFigureOn(cell);
                            if(f != null && f.getTeam() == game.getCurrentTurn() && game.canFigureMove(f, game.getRolledTurnNumber())) {
                                int newCell = game.getMovedFigurePosition(f, game.getRolledTurnNumber());
                                if(newCell > -1) {
                                    Figure g = game.getFigureOn(newCell);
                                    if(g == null || g.getTeam() != f.getTeam()) {
                                        game.moveFigureForAll(f.getGlobalId(), newCell);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        };
        this.addMouseListener(mouseInput);
        this.addMouseMotionListener(mouseInput);
    }

    public void showMessages(String... messages) {
        for(int i = 0; i < messages.length; i++) {
            this.messages.add(messages[i]);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        long now = System.currentTimeMillis();
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, width, height);
        int centerX = width / 2;
        int centerY = height / 2;

        g2d.setFont(fontMsgs);

        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);

        BasicStroke stroke = new BasicStroke(4F);
        g2d.setStroke(stroke);

        int from = 0;
        int to = 4;
        int step = 1;
        g2d.setColor(Color.BLACK);
        do {
            connect(g2d, from, to, centerX, centerY);
            step++;
            from = to;
            to += ((step % 3 == 0) ? 2 : 4);
            to = to % 40;
        } while(from != 0);

        for(int i = 0; i < 4; i++) {
            int entry = i * 10;
            int beforeEntry = ((entry - 1) + 40) % 40;
            int lastInHouse = 43 + (i * 4);
            connect(g2d, beforeEntry, lastInHouse, centerX, centerY);
        }

        boolean hasDrawnHighlight = !messages.isEmpty();
        int halfSize = cellSize / 2;
        for(int i = 0; i < 72; i++) {
            int x = centerX + layout.getX(i, scale) - halfSize;
            int y = centerY + layout.getY(i, scale) - halfSize;
            g2d.setColor(colors.getColor(i));
            g2d.fillOval(x, y, cellSize, cellSize);
            Color outline = Color.BLACK;
            if(!hasDrawnHighlight) {
                if (distanceSq(x + halfSize, y + halfSize, mouseX, mouseY) <= halfSize * halfSize) {
                    hasDrawnHighlight = true;
                    outline = Color.WHITE;
                }
            }
            g2d.setColor(outline);
            g2d.drawOval(x, y, cellSize, cellSize);
            g2d.setColor(Color.BLACK);
            if(showNumbers) {
                g2d.drawString("" + i, x + cellSize / 8, y + cellSize / 2);
            }
        }
        int figureSize = (cellSize / 3) * 2;
        halfSize = figureSize / 2;
        if(game != null) {
            for(int i = 0; i < game.getTeamCount(); i++) {
                for(int j = 0; j < 4; j++) {
                    Figure fig = game.getFigure(i, j);
                    int positionCellIndex = fig.getPosition();
                    int x = centerX + layout.getX(positionCellIndex, scale) - halfSize;
                    int y = centerY + layout.getY(positionCellIndex, scale) - halfSize;
                    // Draw figure
                    g2d.setColor(colors.getTeamColor(i));
                    boolean canMove = i == game.getCurrentTurn() && game.hasRolled() && game.canFigureMove(fig, game.getRolledTurnNumber());
                    g2d.fillOval(x, y, figureSize, figureSize);
                    g2d.setColor(Color.BLACK);
                    if(showNumbers) {
                        g2d.drawString(""+ j, x + halfSize, y + halfSize);
                    }
                    if(canMove) {
                        g2d.setColor(Color.ORANGE);
                    }
                    g2d.drawOval(x, y, figureSize, figureSize);
                }
            }

            g2d.setFont(fontDice);
            // Draw dice
            int dieSize = (int)(cellSize * 1.5F);
            halfSize = dieSize / 2;
            for(int i = 0; i < 4; i++) {
                int x = centerX + layout.getDieX(i, scale) - halfSize;
                int y = centerY + layout.getDieY(i, scale) - halfSize;
                if(game.getCurrentTurn() == i) {
                    g2d.setColor(Color.WHITE);
                } else {
                    g2d.setColor(Color.DARK_GRAY);
                }
                g2d.fillRect(x, y, dieSize, dieSize);
                g2d.setColor(Color.BLACK);
                if(game.isBadRoll() && game.getCurrentTurn() == i) {
                    g2d.setColor(Color.RED);
                }
                if(game.hasTurn() && !game.hasRolled() && game.getCurrentTurn() == i && isOverDie(i, mouseX, mouseY) && messages.isEmpty()) {
                    g2d.setColor(Color.ORANGE);
                }
                g2d.drawRect(x, y, dieSize, dieSize);
                if(game.getCurrentTurn() == i && game.hasRolled()) {
                    // draw number on die
                    g2d.drawString("" + game.getRolledTurnNumber(), x + ((halfSize / 7) * 6), y + (int)(halfSize * 1.3));
                }
            }

        }

        // Draw current message
        if(this.messages != null && !this.messages.isEmpty()) {
            g2d.setColor(new Color(0, 0, 0, 120));
            g2d.fillRect(0, 0, width, height);
            g2d.setFont(fontMsgs);
            g2d.setColor(Color.WHITE);
            g2d.fillRect(centerX - 100, centerY - 30, 200, 60);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(centerX - 100, centerY - 30, 200, 60);
            String[] split = this.messages.peekFirst().split("\n");
            for(int i = 0; i < split.length; i++) {
                g2d.drawString(split[i], centerX - 90, centerY - 12 + i * 12);
            }
            g2d.drawString("Click to continue.", centerX - 50, centerY + 20);
        }
        int elapsed = (int)(System.currentTimeMillis() - now);
        if(game != null && game.waitTimer > 0 && game.giveTurnPeerId > -1) {
            game.waitTimer -= elapsed;
            if(game.waitTimer <= 0) {
                game.waitTimer = -1;
                game.giveTurn(game.giveTurnPeerId);
                game.giveTurnPeerId = -1;
            }
        }
        this.repaint();
    }

    private void connect(Graphics2D g2d, int cellIdA, int cellIdB, int centerX, int centerY) {
        int xa = centerX + layout.getX(cellIdA, scale);
        int ya = centerY + layout.getY(cellIdA, scale);
        int xb = centerX + layout.getX(cellIdB, scale);
        int yb = centerY + layout.getY(cellIdB, scale);
        g2d.drawLine(xa, ya, xb, yb);
    }

    private int getCellIdOver(int mouseX, int mouseY) {
        int centerX = width / 2;
        int centerY = height / 2;
        for(int i = 0; i < 72; i++) {
            int x = centerX + layout.getX(i, scale);
            int y = centerY + layout.getY(i, scale);
            if (distanceSq(x, y, mouseX, mouseY) <= (cellSize / 2) * (cellSize / 2)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isOverOwnDie(int mouseX, int mouseY) {
        if(game != null && game.hasTurn()) {
            return isOverDie(game.getCurrentTurn(), mouseX, mouseY);
        }
        return false;
    }

    private boolean isOverDie(int die, int mouseX, int mouseY) {
        int centerX = width / 2;
        int centerY = height / 2;
        int dieSize = (int)(cellSize * 1.5F);
        int halfSize = dieSize / 2;
        int minX = centerX + layout.getDieX(die, scale) - halfSize;
        int minY = centerY + layout.getDieY(die, scale) - halfSize;
        int maxX = minX + dieSize;
        int maxY = minY + dieSize;
        return mouseX >= minX && mouseX <= maxX && mouseY >= minY && mouseY <= maxY;
    }

    private double distanceSq(int x1, int y1, int x2, int y2) {
        return Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2);
    }

    public static GameView build(Game game) {
        JFrame frame = new JFrame("Mensch Ärgere Dich Nicht");
        GameView view = new GameView();
        view.game = game;
        frame.add(view);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        return view;
    }

    public static void main(String[] args) {
        build(null);
    }
}
