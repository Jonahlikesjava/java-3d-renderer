package render;

import model.Triangle;
import model.Vertex; // only if you need it elsewhere
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;

public class ShapeRenderer extends JPanel {
    private final List<Triangle> tris;

    public ShapeRenderer(List<Triangle> tris) {
        this.tris = tris;
        setBackground(Color.BLACK);
    }

    @Override public Dimension getPreferredSize() { return new Dimension(800, 600); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // put (0,0) at panel center
        g2.translate(getWidth() / 2, getHeight() / 2);
        g2.setStroke(new BasicStroke(2f));

        for (Triangle t : tris) {
            Path2D path = new Path2D.Double();
            path.moveTo(t.v1.x, t.v1.y);
            path.lineTo(t.v2.x, t.v2.y);
            path.lineTo(t.v3.x, t.v3.y);
            path.closePath();

            g2.setColor(t.color);   // or Color.WHITE
            g2.draw(path);          // or g2.fill(path);
        }
    }
}
