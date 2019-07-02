package karbonator.test;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

import karbonator.collection.Vector;
import karbonator.swing.IndexSelector;

public class HIndexSelectorTest extends JFrame {

    private static final long serialVersionUID = 1L;

    public HIndexSelectorTest() {
        super("Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(256, 256, 384, 72);
        
        strings = new Vector<String>(new String [] {
            "My", "Name", "Is", "Foobar"
        });

        Container paneMain = getContentPane();        
        setLayout(new GridLayout(1, 2));
            paneStringSelect = new IndexSelector();
            paneStringSelect.setMaximum(strings.getSize()-1);
            paneStringSelect.addAdjustmentListener(new AdjustmentListener() {
                @Override
                public void adjustmentValueChanged(AdjustmentEvent e) {
                    lblCurrentString.setText(strings.at(e.getValue()));
                }
            });
            paneMain.add(paneStringSelect);
            
            lblCurrentString = new JLabel();
            paneMain.add(lblCurrentString);
    }

    public static void main(String [] args) {
        new HIndexSelectorTest().setVisible(true);
    }
    
    private Vector<String> strings;
    private IndexSelector paneStringSelect;
    private JLabel lblCurrentString;

}
