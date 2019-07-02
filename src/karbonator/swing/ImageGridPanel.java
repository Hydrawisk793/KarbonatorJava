package karbonator.swing;

import java.awt.AWTEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.EventListener;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.event.EventListenerList;

import karbonator.collection.Array;
import karbonator.image.BitmapBpp32;

public class ImageGridPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private class CellPanel extends JPanel {
        
        private static final long serialVersionUID = 1L;
        
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D)g;
            Dimension cellSize = getSize();
            
            if(pBitmap == null) {
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, cellSize.width, cellSize.height);
            }
            else {
                g2.drawImage(image, 0, 0, cellSize.width, cellSize.height, this);
            }
            
            if(showGridEnabled) {
                g2.setStroke(new BasicStroke(1));
                g2.setXORMode(Color.BLACK);
                g2.drawRect(0, 0, cellSize.width, cellSize.height);
            }

            if(selected) {
                g2.setStroke(new BasicStroke(4));
                g2.setXORMode(Color.WHITE);
                g2.drawRect(0, 0, cellSize.width, cellSize.height);
            }
        }

        public void setShowGridEnabled(boolean v) {
            showGridEnabled = v;
        }
        public void setSelected(boolean v) {
            selected = v;
            
            repaint();
        }
        
        public void attachBitmap(BitmapBpp32 o) {
            pBitmap = o;
            
            if(pBitmap != null) {
                final int width = pBitmap.getWidth();
                final int height = pBitmap.getHeight();
                image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                image.setRGB(0, 0, width, height, pBitmap.toInt32Array(), 0, width);
            }
            
            repaint();
        }
        
        public CellPanel(int cellNdxX, int cellNdxY) {
            super();
            
            this.cellNdxX = cellNdxX;
            this.cellNdxY = cellNdxY;
            showGridEnabled = true;
            selected = false;
            
            pBitmap = null;
            
            registerEventHandlers();
        }
        
        private void registerEventHandlers() {
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {}
                @Override
                public void mouseEntered(MouseEvent e) {}
                @Override
                public void mouseExited(MouseEvent e) {}
                @Override
                public void mousePressed(MouseEvent e) {
                    selectCell(cellNdxX, cellNdxY, true, e);
                }
                @Override
                public void mouseReleased(MouseEvent e) {}
            }); 
        }

        private int cellNdxX;
        private int cellNdxY;
        private boolean showGridEnabled;
        private boolean selected;
        
        private BitmapBpp32 pBitmap;
        private BufferedImage image;
        
    }
        
    public static interface HEventListener extends EventListener {
        
        public void cellSelected(HCellSelectedEvent cellSelectedEvent, MouseEvent mouseEvent);
    
    }
    public static class HCellSelectedEvent extends AWTEvent {
        
        private static final long serialVersionUID = 1L;
        
        @Override
        public HCellSelectedEvent getSource() {
            return (HCellSelectedEvent)super.getSource();
        }
        public int getPreviousSelectedCellIndexX() {
            return prevX;
        }
        public int getPreviousSelectedCellIndexY() {
            return prevY;
        }
        public int getCurrentSelectedCellIndexX() {
            return curX;
        }
        public int getCurrentSelectedCellIndexY() {
            return curY;
        }
        public int getCurrentSelectedImageIndex() {
            return curImageIndex;
        }
        
        public HCellSelectedEvent(Event o) {
            super(o);
        }
        public HCellSelectedEvent(ImageGridPanel source, int id, int prevX, int prevY, int curX, int curY, int curImageIndex) {
            super(source, id);
            
            this.prevX = prevX;
            this.prevY = prevY;
            this.curX = curX;
            this.curY = curY;
            this.curImageIndex = curImageIndex;
        }
        
        private int prevX;
        private int prevY;
        private int curX;
        private int curY;
        private int curImageIndex;
        
    }
    
    public int getCellCountX() {
        return nCellX;
    }
    public int getCellCountY() {
        return nCellY;
    }
    public int getCellCount() {
        return nCellX*nCellY;
    }
    public int getSelectedCellIndexX() {
        return selectedCellNdxX;
    }
    public int getSelectedCellIndexY() {
        return selectedCellNdxY;
    }
    public int getSelectedCellIndex() {
        return selectedCellNdxY*nCellX + selectedCellNdxX;
    }
    public int getSelectedPageIndex() {
        return sbVertical.getValue();
    }
    public int getSelectedImageIndex() {
        return (selectedCellNdxY*nCellX + selectedCellNdxX) + (sbVertical.getValue()*getCellCount());
    }
    
    public void setPreferredCellSize(Dimension o) {
        for(JPanel cell : paneCells) {
            cell.setPreferredSize(o);            
        }
    }
    public void setMinimumCellSize(Dimension o) {
        for(JPanel cell : paneCells) {
            cell.setMinimumSize(o);            
        }
    }
    public void setMaximumCellSize(Dimension o) {
        for(JPanel cell : paneCells) {
            cell.setMaximumSize(o);            
        }
    }
    public void setShowGridCellEnabled(boolean v) {
        for(CellPanel cell : paneCells) {
            cell.setShowGridEnabled(v);            
        }
    }
    
    public void attachBitmaps(Array<BitmapBpp32> o) {
        pBitmaps = o;
        
        refreshComponents();
    }
    
    public void addEventListener(HEventListener l) {
        listenerList.add(HEventListener.class, l);
    }
    public void removeEventListener(HEventListener l) {
        listenerList.remove(HEventListener.class, l);
    }
    
    public void selectCell(int x, int y) {
        selectCell(x, y, true);
    }
    public void selectCell(int x, int y, boolean invokeEvent) {
        selectCell(x, y, invokeEvent, null);
    }
    public void selectCell(int x, int y, boolean invokeEvent, MouseEvent mouseEvent) {
        final CellPanel prevSel = paneCells[selectedCellNdxY*nCellX + selectedCellNdxX];
        prevSel.setSelected(false);
        prevSel.repaint();

        final CellPanel curSel = paneCells[y*nCellX + x];
        curSel.setSelected(true);
        curSel.repaint();
        
        final int prevX = selectedCellNdxX;
        final int prevY = selectedCellNdxY;
        selectedCellNdxX = x;
        selectedCellNdxY = y;
        
        if(invokeEvent) {
            fireCellSelected(prevX, prevY, x, y, mouseEvent);
        }
    }
    public void constructGrid(int nCellX, int nCellY) {    
        this.nCellX = nCellX;
        this.nCellY = nCellY;
        
        paneGrid = new JPanel();
        sbVertical = new JScrollBar(JScrollBar.VERTICAL);
        
        GridLayout loGrid = new GridLayout(nCellY, nCellX);
        paneGrid.setLayout(loGrid);
        
        paneCells = new CellPanel [nCellX*nCellY];
        for(int y=0, yOff=0; y<nCellY; ++y, yOff+=nCellX) {
            for(int x=0; x<nCellX; ++x) {
                final int index = yOff + x;
                paneCells[index] = new CellPanel(x, y);
                paneGrid.add(paneCells[index]);
            }
        }
        
        GridBagLayout loMain = new GridBagLayout();
        setLayout(loMain);
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(2, 2, 2, 2);
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weighty = 1;
        
        c.weightx = 16;
        c.gridx = 0;
        c.gridy = 0;
        loMain.setConstraints(paneGrid, c);
        add(paneGrid);
        
        c.weightx = 1;
        c.gridx = 1;
        c.gridy = 0;
        loMain.setConstraints(sbVertical, c);
        add(sbVertical);
        
        refreshComponents(0);
        
        selectCell(0, 0);
    }
    public void refreshComponents() {
        refreshComponents(sbVertical.getValue());
    }
    public void refreshComponents(int pageNo) {
        if(pBitmaps != null && pBitmaps.getSize() > 0) {
            final int nBitmap = pBitmaps.getSize();
            final int nCell = nCellX*nCellY;
            final int nMin = (nBitmap - nCell > 0?nCell:nBitmap);
            final int bitmapNdxOff = nCell*pageNo;
            int cellNdx = 0;
            
            for(; cellNdx<nMin; ++cellNdx) {
                paneCells[cellNdx].attachBitmap(pBitmaps.at(cellNdx+bitmapNdxOff));
            }
            
            if(nCell > nBitmap) {
                for(; cellNdx<nCell; ++cellNdx) {
                    paneCells[cellNdx].attachBitmap(null);
                }
            }
            
            sbVertical.setEnabled(true);
            sbVertical.setValues(pageNo, 0, 0, (nBitmap/nCell)-1);
        }
        else {
            sbVertical.setEnabled(false);
        }
    }
    
    public ImageGridPanel(int nCellX, int nCellY, Array<BitmapBpp32> bitmaps) {
        super();
        
        pBitmaps = bitmaps;
        
        listenerList = new EventListenerList();
        
        selectedCellNdxX = 0;
        selectedCellNdxY = 0;
        constructGrid(nCellX, nCellY);
        sbVertical.setUnitIncrement(1);
        sbVertical.setBlockIncrement(1);
        sbVertical.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                refreshComponents(e.getValue());
            }
        });
    }
    
    protected void fireCellSelected(int prevX, int prevY, int curX, int curY, MouseEvent mouseEvent) {
        Object [] listeners = listenerList.getListenerList();
        
        HCellSelectedEvent cellSelectedEvent = null;
        
        for(int r1=listeners.length-2;r1>=0;r1-=2) {
            if(listeners[r1] == HEventListener.class) {
                if(cellSelectedEvent == null) {
                    cellSelectedEvent = new HCellSelectedEvent(this, 0, prevX, prevY, curX, curY, getSelectedImageIndex());
                }
                
                ((HEventListener)listeners[r1+1]).cellSelected(cellSelectedEvent, mouseEvent);
            }
        }
    }    

    private Array<BitmapBpp32> pBitmaps;
    
    private EventListenerList listenerList;
    
    private int selectedCellNdxX;
    private int selectedCellNdxY;
    private int nCellX;
    private int nCellY;

    private JPanel paneGrid;
    private JScrollBar sbVertical;
    private CellPanel [] paneCells;
    
}
