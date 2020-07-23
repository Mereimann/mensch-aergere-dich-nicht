package mensch.ui;

import java.awt.*;
import java.awt.geom.Point2D;

public class Layout {

    private Point2D[] positions;

    private Point2D[] dice;

    public Layout() {
        this.positions = new Point2D[72];
        this.positions[ 0] = new Point2D.Double(-0.5, -0.1);
        this.positions[ 1] = new Point2D.Double(-0.4, -0.1);
        this.positions[ 2] = new Point2D.Double(-0.3, -0.1);
        this.positions[ 3] = new Point2D.Double(-0.2, -0.1);
        this.positions[ 4] = new Point2D.Double(-0.1, -0.1);
        this.positions[ 5] = new Point2D.Double(-0.1, -0.2);
        this.positions[ 6] = new Point2D.Double(-0.1, -0.3);
        this.positions[ 7] = new Point2D.Double(-0.1, -0.4);
        this.positions[ 8] = new Point2D.Double(-0.1, -0.5);
        this.positions[ 9] = new Point2D.Double(   0, -0.5);
        this.positions[10] = new Point2D.Double( 0.1, -0.5);
        this.positions[11] = new Point2D.Double( 0.1, -0.4);
        this.positions[12] = new Point2D.Double( 0.1, -0.3);
        this.positions[13] = new Point2D.Double( 0.1, -0.2);
        this.positions[14] = new Point2D.Double( 0.1, -0.1);
        this.positions[15] = new Point2D.Double( 0.2, -0.1);
        this.positions[16] = new Point2D.Double( 0.3, -0.1);
        this.positions[17] = new Point2D.Double( 0.4, -0.1);
        this.positions[18] = new Point2D.Double( 0.5, -0.1);
        this.positions[19] = new Point2D.Double( 0.5,    0);
        this.positions[20] = new Point2D.Double( 0.5,  0.1);
        this.positions[21] = new Point2D.Double( 0.4,  0.1);
        this.positions[22] = new Point2D.Double( 0.3,  0.1);
        this.positions[23] = new Point2D.Double( 0.2,  0.1);
        this.positions[24] = new Point2D.Double( 0.1,  0.1);
        this.positions[25] = new Point2D.Double( 0.1,  0.2);
        this.positions[26] = new Point2D.Double( 0.1,  0.3);
        this.positions[27] = new Point2D.Double( 0.1,  0.4);
        this.positions[28] = new Point2D.Double( 0.1,  0.5);
        this.positions[29] = new Point2D.Double(   0,  0.5);
        this.positions[30] = new Point2D.Double(-0.1,  0.5);
        this.positions[31] = new Point2D.Double(-0.1,  0.4);
        this.positions[32] = new Point2D.Double(-0.1,  0.3);
        this.positions[33] = new Point2D.Double(-0.1,  0.2);
        this.positions[34] = new Point2D.Double(-0.1,  0.1);
        this.positions[35] = new Point2D.Double(-0.2,  0.1);
        this.positions[36] = new Point2D.Double(-0.3,  0.1);
        this.positions[37] = new Point2D.Double(-0.4,  0.1);
        this.positions[38] = new Point2D.Double(-0.5,  0.1);
        this.positions[39] = new Point2D.Double(-0.5,    0);

        this.positions[40] = new Point2D.Double(-0.4,    0);
        this.positions[41] = new Point2D.Double(-0.3,    0);
        this.positions[42] = new Point2D.Double(-0.2,    0);
        this.positions[43] = new Point2D.Double(-0.1,    0);

        this.positions[44] = new Point2D.Double(   0, -0.4);
        this.positions[45] = new Point2D.Double(   0, -0.3);
        this.positions[46] = new Point2D.Double(   0, -0.2);
        this.positions[47] = new Point2D.Double(   0, -0.1);

        this.positions[48] = new Point2D.Double( 0.4,    0);
        this.positions[49] = new Point2D.Double( 0.3,    0);
        this.positions[50] = new Point2D.Double( 0.2,    0);
        this.positions[51] = new Point2D.Double( 0.1,    0);

        this.positions[52] = new Point2D.Double(   0,  0.4);
        this.positions[53] = new Point2D.Double(   0,  0.3);
        this.positions[54] = new Point2D.Double(   0,  0.2);
        this.positions[55] = new Point2D.Double(   0,  0.1);

        this.positions[56] = new Point2D.Double(-0.5, -0.5);
        this.positions[57] = new Point2D.Double(-0.4, -0.5);
        this.positions[58] = new Point2D.Double(-0.5, -0.4);
        this.positions[59] = new Point2D.Double(-0.4, -0.4);

        this.positions[60] = new Point2D.Double( 0.4, -0.5);
        this.positions[61] = new Point2D.Double( 0.5, -0.5);
        this.positions[62] = new Point2D.Double( 0.4, -0.4);
        this.positions[63] = new Point2D.Double( 0.5, -0.4);

        this.positions[64] = new Point2D.Double( 0.4,  0.4);
        this.positions[65] = new Point2D.Double( 0.5,  0.4);
        this.positions[66] = new Point2D.Double( 0.4,  0.5);
        this.positions[67] = new Point2D.Double( 0.5,  0.5);

        this.positions[68] = new Point2D.Double(-0.5,  0.4);
        this.positions[69] = new Point2D.Double(-0.4,  0.4);
        this.positions[70] = new Point2D.Double(-0.5,  0.5);
        this.positions[71] = new Point2D.Double(-0.4,  0.5);

        this.dice = new Point2D[4];

        this.dice[0] = new Point2D.Double(-0.65, -0.45);
        this.dice[1] = new Point2D.Double(0.65, -0.45);
        this.dice[2] = new Point2D.Double(0.65, 0.45);
        this.dice[3] = new Point2D.Double(-0.65, 0.45);
    }

    public int getX(int id, int scale) {
        return (int) (positions[id].getX() * scale);
    }

    public int getY(int id, int scale) {
        return (int) (positions[id].getY() * scale);
    }

    public int getDieX(int id, int scale) {
        return (int)(dice[id].getX() * scale);
    }

    public int getDieY(int id, int scale) {
        return (int)(dice[id].getY() * scale);
    }
}
