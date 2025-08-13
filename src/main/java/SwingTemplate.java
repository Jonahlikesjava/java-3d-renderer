import model.Triangle;
import render.ShapeRenderer;
import scene.ShapeFactory;
import java.util.List;
import model.Triangle;

import javax.swing.*;
import java.awt.*;

public class SwingTemplate {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());

        // slider to control horizontal rotation
        JSlider headingSlider = new JSlider(0, 360, 180);
        pane.add(headingSlider, BorderLayout.SOUTH);

        // slider to control vertical rotation
        JSlider pitchSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
        pane.add(pitchSlider, BorderLayout.EAST);

        frame.setSize(400, 400);
        List<Triangle> tris = ShapeFactory.buildTriangles();
        frame.add(new ShapeRenderer(tris), BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}
