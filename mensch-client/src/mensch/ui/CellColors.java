package mensch.ui;

import java.awt.*;

public class CellColors {

    private Color[] baseColors;
    private Color[] lighterColors;

    public CellColors(Color... baseColors) {
        this.baseColors = baseColors;
        this.lighterColors = new Color[] {
                lerp(this.baseColors[0], Color.WHITE, 0.2F),
                lerp(this.baseColors[1], Color.WHITE, 0.2F),
                lerp(this.baseColors[2], Color.WHITE, 0.2F),
                lerp(this.baseColors[3], Color.WHITE, 0.2F)
        };
    }

    public Color getTeamColor(int teamId) {
        return baseColors[teamId];
    }

    public Color getColor(int id) {
        if(id == 0 || id == 10 || id == 20 || id == 30) {
            return lighterColors[id / 10];
        }
        if(id >= 40) {
            id -= 40;
            id /= 4;
            id %= 4;
            return baseColors[id];
        }
        return Color.WHITE;
    }

    public static Color lerp(Color a, Color b, float t) {
        int ar = a.getRed();
        int ag = a.getGreen();
        int ab = a.getBlue();
        int aa = a.getAlpha();
        int dr = b.getRed() - ar;
        int dg = b.getGreen() - ag;
        int db = b.getBlue() - ab;
        int da = b.getAlpha() - aa;
        return new Color(ar + (int)(dr * t), ag + (int)(dg * t), ab + (int)(db * t), aa + (int)(da * t));
    }
}
