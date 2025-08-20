package render;

import model.Matrix3;
import model.Triangle;
import model.Vertex;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static java.awt.Color.*;

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

        double[] zBuffer = new double[img.getWidth() * img.getHeight()];
        for (int q = 0; q < zBuffer.length; q++) {
            zBuffer[q] = Double.NEGATIVE_INFINITY;
        }

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

            Vertex ab = new Vertex(
                    v2.x - v1.x,
                    v2.y - v1.y,
                    v2.getZ() - v1.getZ()
            );

            Vertex ac = new Vertex(
                    v3.x - v1.x,
                    v3.y - v1.y,
                    v3.getZ() - v1.getZ()
            );

            Vertex norm = new Vertex(
                    ab.y * ac.getZ() - ab.getZ() * ac.y,
                    ab.getZ() * ac.x - ab.x * ac.getZ(),
                    ab.x * ac.y - ab.y * ac.x
            );
            double normalLength =
                    Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.getZ() * norm.getZ());
            norm.x /= normalLength;
            norm.y /= normalLength;
            norm.setZ(norm.getZ() / normalLength);

            double angleCos = Math.abs(norm.getZ());

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
                        double depth = b1 * v1.getZ() + b2 * v2.getZ() + b3 * v3.getZ();
                        int zIndex = y * img.getWidth() + x;

                        if (zBuffer[zIndex] < depth) {
                            img.setRGB(x, y, getShade(t.color, angleCos).getRGB());
                            zBuffer[zIndex] = depth;
                        }
                    }
                }
            }
        }

        g2.drawImage(img, 0, 0, null);
    }

    public static Color getShade(Color color, double shade) {
        double redLinear = Math.pow(color.getRed(), 2.4) * shade;
        double greenLinear = Math.pow(color.getGreen(), 2.4) * shade;
        double blueLinear = Math.pow(color.getBlue(), 2.4) * shade;


        int red = (int) (color.getRed() * shade);
        int green = (int) (color.getGreen() * shade);
        int blue = (int) (color.getBlue() * shade);

        return new Color(red, green, blue);
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
