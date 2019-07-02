package karbonator.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class TestPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        Dimension size = getSize();
        g.setColor(getBackground());
        g.fillRect(0, 0, size.width, size.height);
    }

    public TestPanel() {
        super();
    }
    public TestPanel(TestPanel o) {
        setBackground(o.getBackground());
    }
    public TestPanel(Color bgColor) {
        setBackground(bgColor);
    }

}
