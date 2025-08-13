package render;

import model.Matrix3;
import model.Triangle;
import model.Vertex;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;

public class ShapeRenderer extends JPanel {
    private final List<Triangle> tris;
    private JSlider headingSlider;
    private JSlider pitchSlider;

    public ShapeRenderer(List<Triangle> tris) {
        this.tris = tris;
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        double heading = Math.toRadians(headingSlider != null ? headingSlider.getValue() : 0);
        double pitch = Math.toRadians(pitchSlider != null ? pitchSlider.getValue() : 0);

        Matrix3 headingTransform = new Matrix3(new double[] {
           Math.cos(heading), 0, Math.sin(heading),
           0, 1, 0,
           -Math.sin(heading), 0, Math.cos(heading)
        });

        Matrix3 pitchTransform = new Matrix3(new double[] {
            1, 0, 0,
            0, Math.cos(pitch), Math.sin(pitch),
            0, -Math.sin(pitch), Math.cos(pitch)
        });
        Matrix3 transform = headingTransform.multiply(pitchTransform);


        g2.translate(getWidth() / 2, getHeight() / 2);
        g2.setStroke(new BasicStroke(2f));

        for (Triangle t : tris) {
            Vertex v1 = transform.transform(t.v1);
            Vertex v2 = transform.transform(t.v2);
            Vertex v3 = transform.transform(t.v3);


            Path2D path = new Path2D.Double();
            path.moveTo(v1.x, v1.y);
            path.lineTo(v2.x, v2.y);
            path.lineTo(v3.x, v3.y);
            path.closePath();

            g2.setColor(t.color != null ? t.color : Color.WHITE);
            g2.draw(path);
        }
    }

    public void setHeadingSlider(JSlider slider) {
        this.headingSlider = slider;
        repaint();
    }

    public void setPitchSlider(JSlider slider) {
        this.pitchSlider = slider;
        repaint();
    }

}
