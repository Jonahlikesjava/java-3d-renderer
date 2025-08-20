package render;

import model.Matrix3;
import model.Triangle;
import model.Vertex;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
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
        double pitch   = Math.toRadians(pitchSlider   != null ? pitchSlider.getValue()   : 0);

        Matrix3 headingTransform = new Matrix3(new double[]{
                Math.cos(heading), 0, Math.sin(heading),
                0, 1, 0,
                -Math.sin(heading), 0, Math.cos(heading)
        });
        Matrix3 pitchTransform = new Matrix3(new double[]{
                1, 0, 0,
                0, Math.cos(pitch), Math.sin(pitch),
                0, -Math.sin(pitch), Math.cos(pitch)
        });
        Matrix3 transform = headingTransform.multiply(pitchTransform);

        BufferedImage img =
                new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (Triangle t : tris) {
            Vertex v1 = transform.transform(t.v1);
            Vertex v2 = transform.transform(t.v2);
            Vertex v3 = transform.transform(t.v3);

            v1.x += getWidth() / 2;
            v1.y += getHeight() / 2;
            v2.x += getWidth() / 2;
            v2.y += getHeight() / 2;
            v3.x += getWidth() / 2;
            v3.y += getHeight() / 2;

            int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
            int maxX = (int) Math.min(img.getWidth() - 1,
                    Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
            int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
            int maxY = (int) Math.min(img.getHeight() - 1,
                    Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

            double triangleArea = (v1.y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - v1.x);

            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    double b1 = ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea;
                    double b2 = ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea;
                    double b3 = ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea;
                    if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1) {
                        img.setRGB(x, y, t.color.getRGB());
                    }
                }
            }
        }

        g2.drawImage(img, 0, 0, null);
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
