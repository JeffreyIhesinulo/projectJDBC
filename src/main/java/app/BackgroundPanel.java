package app;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class BackgroundPanel extends JPanel {

    private Image background;

    public BackgroundPanel(String imagePath) {
        URL url = getClass().getResource(imagePath);
        if (url != null) {
            this.background = new ImageIcon(url).getImage();
        }
        // If image not found, falls back to plain background color
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
