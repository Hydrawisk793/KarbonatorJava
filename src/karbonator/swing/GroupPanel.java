package karbonator.swing;

import java.awt.Component;

import javax.swing.JPanel;

public class GroupPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    
        Component [] components = getComponents();
        
        for(int r1=0;r1<components.length;++r1) {
            components[r1].setEnabled(enabled);
        }
    }

}
