package karbonator.swing;

import java.awt.AWTEvent;
import java.awt.Adjustable;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.text.NumberFormat;
import java.util.EventListener;

import javax.swing.JFormattedTextField;
import javax.swing.JScrollBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
    @brief �ε��� ������ ���� Swing ������Ʈ
    @date 2015/04/11
    @author Hydrawisk793
*/
public class IndexSelector extends GroupPanel implements Adjustable {

    private static final long serialVersionUID = 1L;
    
    public static interface HEventListener extends EventListener {
        public void indexChanged(HIndexChangedEvent e);
    }
    public static class HIndexChangedEvent extends AWTEvent {
    
        private static final long serialVersionUID = 1L;

        public int getPreviousIndex() {
            return prevIndex;
        }
        public int getCurrentIndex() {
            return curIndex;
        }

        public HIndexChangedEvent(Event o) {
            super(o);
        }
        public HIndexChangedEvent(IndexSelector source, int id, int prevIndex, int curIndex) {
            super(source, id);
            
            this.prevIndex = prevIndex;
            this.curIndex = curIndex;
        }
        
        private int prevIndex;
        private int curIndex;
        
    }
    
    @Override
    public int getBlockIncrement() {
        return sbIndex.getBlockIncrement();
    }
    @Override
    public int getMaximum() {
        return sbIndex.getMaximum();
    }
    @Override
    public int getMinimum() {
        return sbIndex.getMinimum();
    }
    @Override
    public int getOrientation() {
        return sbIndex.getOrientation();
    }
    @Override
    public int getUnitIncrement() {
        return sbIndex.getUnitIncrement();
    }
    @Override
    public int getValue() {
        return sbIndex.getValue();
    }
    @Override
    public int getVisibleAmount() {
        return sbIndex.getVisibleAmount();
    }
    
    @Override
    public void setBlockIncrement(int v) {
        sbIndex.setBlockIncrement(v);
    }
    @Override
    public void setMaximum(int v) {
        sbIndex.setMaximum(v);
    }
    @Override
    public void setMinimum(int v) {
        sbIndex.setMinimum(v);
    }
    @Override
    public void setUnitIncrement(int v) {
        sbIndex.setUnitIncrement(v);
    }
    @Override
    public void setValue(int v) {
        sbIndex.setValue(v);
        
        tfIndexEventFlag = false;
        tfIndex.setText(""+v);
        tfIndexEventFlag = true;
    }
    @Override
    public void setVisibleAmount(int v) {
        sbIndex.setVisibleAmount(v);
    }
    
    @Override
    public void addAdjustmentListener(AdjustmentListener l) {
        sbIndex.addAdjustmentListener(l);
    }
    public void addEventListener(HEventListener l) {
        listenerList.add(HEventListener.class, l);
    }
    @Override
    public void removeAdjustmentListener(AdjustmentListener l) {
        sbIndex.removeAdjustmentListener(l);
    }
    public void removeEventListener(HEventListener l) {
        listenerList.remove(HEventListener.class, l);
    }
    
    public IndexSelector() {
        super();
        
        prevIndex = 0;
        tfIndexEventFlag = true;
        sbIndexEventFlag = true;
        
        listenerList = new EventListenerList();
        
        constructGUIComponents();        
        placeGUIComponents();
        registerEventHandlers();
    }
    
    private void constructGUIComponents() {
        tfIndex = new JFormattedTextField(NumberFormat.getInstance());
        tfIndex.setText("0");
        sbIndex = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, 128);
    }
    private void placeGUIComponents() {
        GridBagConstraints mainConsts = new GridBagConstraints();
        GridBagLayout mainLayout = new GridBagLayout();
        setLayout(mainLayout);
        
        mainConsts.fill = GridBagConstraints.BOTH;
        mainConsts.insets = new Insets(2, 2, 2, 2);
        mainConsts.gridwidth = 1;
        mainConsts.gridheight = 1;
        mainConsts.gridx = 0;
        mainConsts.gridy = 0;
        mainConsts.weightx = 1;
        mainConsts.weighty = 1;
        mainLayout.setConstraints(tfIndex, mainConsts);
        add(tfIndex);
        
        mainConsts.gridx = 1;
        mainConsts.weightx = 7;
        mainLayout.setConstraints(sbIndex, mainConsts);
        add(sbIndex);
    }
    private void registerEventHandlers() {
        tfIndex.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                doAction(e);
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                doAction(e);
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                doAction(e);
            }
            
            private void doAction(DocumentEvent e) {
                if(tfIndexEventFlag) {
                    Document document = e.getDocument();
                    String text = null;
                    Integer value = null;
                    
                    try {
                        text = document.getText(0, document.getLength());
                            
                        if(text != null && !text.equals("")) {
                                value = Integer.parseInt(text);
                                
                                if(value >= sbIndex.getMinimum() && value <= sbIndex.getMaximum()) {
                                    sbIndexEventFlag = false;
                                    sbIndex.setValue(value);
                                    
                                    fireIndexChanged(prevIndex, value);
                                    prevIndex = value;
                                }
                        }
                    }
                    catch (NumberFormatException nfe) {
                        //nfe.printStackTrace();
                    }
                    catch (BadLocationException ble) {
                        ble.printStackTrace();
                    }
                }
                
                tfIndexEventFlag = true;
            }
        });
        sbIndex.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if(sbIndexEventFlag) {
                    final int curIndex = sbIndex.getValue();
                
                    tfIndexEventFlag = false;
                    tfIndex.setText(""+curIndex);
                    
                    fireIndexChanged(prevIndex, curIndex);                    
                    prevIndex = curIndex;
                }
                
                sbIndexEventFlag = true;
            }
        });
    }
    
    protected void fireIndexChanged(int prevIndex, int curIndex) {
        Object [] listeners = listenerList.getListenerList();
        
        HIndexChangedEvent event = null;
        
        for(int r1=listeners.length-2;r1>=0;r1-=2) {
            if(listeners[r1] == HEventListener.class) {
                if(event == null) {
                    event = new HIndexChangedEvent(this, 0, prevIndex, curIndex);
                }
                
                ((HEventListener)listeners[r1+1]).indexChanged(event);
            }
        }
    }
    
    private int prevIndex;
    private boolean tfIndexEventFlag;
    private boolean sbIndexEventFlag;
    
    private EventListenerList listenerList;

    private JFormattedTextField tfIndex;
    private JScrollBar sbIndex;
    
}
