package karbonator.swing;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

/**
    @brief ���� �����̳� ������ ���� Swing ������Ʈ
    @date 2015/04/11
    @author Hydrawisk793
*/
@SuppressWarnings("unchecked")
public class ItemController<E> extends JPanel {

    ////////////////////////////////
    //Constants

    private static final long serialVersionUID = 1L;
    
    ////////////////////////////////
    
    ////////////////////////////////
    //Event Listener Interfaces
    
    public static interface HItemAdapter<E> {

        public int getItemCount();
        public boolean isEmpty();
        
        public E refer(int index);
        
        public E add();
        public E remove(int index);
        public void move(int destIndex, int srcIndex);
        public void swap(int lhsIndex, int rhsIndex);

    }
    public static interface HEventListener<E> extends EventListener {
    
        public void indexChanged(HIndexChangedEvent<E> e);
        public void itemAdded(HItemAddedEvent<E> e);
        public void itemRemoved(HItemRemovedEvent<E> e);
        public void itemMoved(HItemMovedEvent<E> e);
        public void itemSwapped(HItemMovedEvent<E> e);
        
    }
    
    ////////////////////////////////
    
    ////////////////////////////////
    //Event Classes

    public static class HIndexChangedEvent<E> extends AWTEvent {

        private static final long serialVersionUID = 1L;
        
        @Override
        public ItemController<E> getSource() {
            return (ItemController<E>)super.getSource();
        }
        public int getPreviousIndex() {
            return prevIndex;
        }
        public int getCurrentIndex() {
            return curIndex;
        }
        public E getSelectedItem() {
            return selectedItem;
        }
    
        public HIndexChangedEvent(ItemController<E> source, int id, int prevIndex, int curIndex, E selectedItem) {
            super(source, id);
            
            this.prevIndex = prevIndex;
            this.curIndex = curIndex;
            this.selectedItem = selectedItem;
        }
        
        private final int prevIndex;
        private final int curIndex;
        private final E selectedItem;
        
    }
    public static class HItemAddedEvent<E> extends AWTEvent {

        private static final long serialVersionUID = 1L;
        
        @Override
        public ItemController<E> getSource() {
            return (ItemController<E>)super.getSource();
        }
        public int getNewElementIndex() {
            return newElemIndex;
        }
    
        public HItemAddedEvent(Event o) {
            super(o);
        }
        public HItemAddedEvent(ItemController<E> source, int id, int newElemIndex) {
            super(source, id);
            
            this.newElemIndex = newElemIndex;
        }
        
        private int newElemIndex;
        
    }
    public static class HItemRemovedEvent<E> extends AWTEvent {
    
        private static final long serialVersionUID = 1L;
        
        @Override
        public ItemController<E> getSource() {
            return (ItemController<E>)super.getSource();
        }
        public E getRemovedItem() {
            return removedItem;
        }
        
        public HItemRemovedEvent(ItemController<E> source, int id, E removedItem) {
            super(source, id);
            
            this.removedItem = removedItem;
        }
        
        private final E removedItem;
        
    }
    public static class HItemMovedEvent<E> extends AWTEvent {

        private static final long serialVersionUID = 1L;
        
        @Override
        public ItemController<E> getSource() {
            return (ItemController<E>)super.getSource();
        }
        public int getSourceElementIndex() {
            return srcElemIndex;
        }
        public int getDestinationElementIndex() {
            return destElemIndex;
        }
    
        public HItemMovedEvent(Event o) {
            super(o);
        }
        public HItemMovedEvent(ItemController<E> source, int id, int srcElemIndex, int destElemIndex) {
            super(source, id);
            
            this.srcElemIndex = srcElemIndex;
            this.destElemIndex = destElemIndex;
        }
        
        private int srcElemIndex;
        private int destElemIndex;
        
    }
    
    ////////////////////////////////

    ////////////////////////////////
    //Methods
    
    public int getSelectedItemIndex() {
        return ndxSel.getValue();
    }
    public void selectItem(int v) {
        selectItem(v, true);
    }
    public void selectItem(int v, boolean invokeEvent) {
        setGUIComponentStatus();
    
        int prevIndex = ndxSel.getValue();
        
        ndxSel.setValue(v);
        final int selectedIndex = ndxSel.getValue();
        E selectedItem = adapter.refer(selectedIndex);
        
        if(ndxSel.isEnabled() && invokeEvent) {
            fireIndexChanged(prevIndex, selectedIndex, selectedItem);
        }
    }

    public int getMinimumItemCount() {
        return minItemCount;
    }
    public void setMinimumItemCount(int v) {
        minItemCount = v;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        
        setGUIComponentStatus();
    }
    
    public void refreshComponents() {
        setGUIComponentStatus();
    }
    
    ////////////////////////////////
    
    ////////////////////////////////
    //Event Listener Setters
    
    public void attachItemAdapter(HItemAdapter<E> o) {
        adapter = o;
        
        setGUIComponentStatus();
        
        if(isItemSelectable()) {
            selectItem(0, true);
        }
    }
    public void addEventListener(HEventListener<E> l) {
        listenerList.add(HEventListener.class, l);
    }
    public void removeEventListener(HEventListener<E> l) {
        listenerList.remove(HEventListener.class, l);
    }
    
    ////////////////////////////////
    
    ////////////////////////////////
    //Ctors
    
    public ItemController() {
        super();

        adapter = null;
        listenerList = new EventListenerList();
        
        minItemCount = 0;
        
        constructGUIComponents();
        placeGUIComponents();
        registerEventHandlers();
        
        setGUIComponentStatus();
    }
    
    ////////////////////////////////
    
    ////////////////////////////////
    //GUI Component Initializers
    
    private void constructGUIComponents() {
        Dimension defaultSize = new Dimension(0, 0);
        
        ndxSel = new IndexSelector();
        ndxSel.setPreferredSize(defaultSize);
        ndxSel.setEnabled(false);
        
        btnAdd = new JButton("Add");
        btnAdd.setPreferredSize(defaultSize);

        btnRemove = new JButton("Remove");
        btnRemove.setPreferredSize(defaultSize);

        btnMove = new JButton("Move");
        btnMove.setPreferredSize(defaultSize);

        btnSwap = new JButton("Swap");
        btnSwap.setPreferredSize(defaultSize);
    }
    private void placeGUIComponents() {
        GridBagConstraints mainConsts = new GridBagConstraints();
        GridBagLayout mainLayout = new GridBagLayout();
        setLayout(mainLayout);
        
        mainConsts.fill = GridBagConstraints.BOTH;
        mainConsts.insets = new Insets(2, 2, 2, 2);
        mainConsts.gridwidth = 4;
        mainConsts.gridheight = 1;
        mainConsts.gridx = 0;
        mainConsts.gridy = 0;
        mainConsts.weightx = 1;
        mainConsts.weighty = 1;
        mainLayout.setConstraints(ndxSel, mainConsts);
        add(ndxSel);
        
        mainConsts.gridwidth = 1;
        mainConsts.gridx = 0;
        mainConsts.gridy = 1;
        mainLayout.setConstraints(btnAdd, mainConsts);
        add(btnAdd);
        
        mainConsts.gridx = 1;
        mainLayout.setConstraints(btnRemove, mainConsts);
        add(btnRemove);
        
        mainConsts.gridx = 2;
        mainLayout.setConstraints(btnMove, mainConsts);
        add(btnMove);
        
        mainConsts.gridx = 3;
        mainLayout.setConstraints(btnSwap, mainConsts);
        add(btnSwap);
    }
    private void registerEventHandlers() {
        ndxSel.addEventListener(new IndexSelector.HEventListener() {
            @Override
            public void indexChanged(IndexSelector.HIndexChangedEvent e) {
                changeIndex(e.getPreviousIndex(), e.getCurrentIndex());
            }
        });
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItem();
            }
        });        
        btnRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeItem();
            }
        });        
        btnMove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveItem();
            }
        });      
        btnSwap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                swapItems();
            }
        });
    }
    
    ////////////////////////////////
    
    ////////////////////////////////
    //Operations
    
    private void changeIndex(int prevIndex, int curIndex) {
        if(isItemSelectable()) {
            E selectedItem = adapter.refer(curIndex);
            
            fireIndexChanged(prevIndex, curIndex, selectedItem);
        }
    }
    private void addItem() {
        if(isAvailable()) {
            E newElement = adapter.add();
            
            if(newElement != null) {
                int newElemIndex = adapter.getItemCount()-1;
                
                setGUIComponentStatus();
                selectItem(newElemIndex, true);
                
                fireElementAdded(newElemIndex);
            }
        }
    }
    private void removeItem() {
        if(isItemSelectable()) {
            if(adapter.getItemCount() > minItemCount) {
                int userCmd = JOptionPane.showConfirmDialog(
                        ItemController.this, 
                        "Are you sure to remove?", 
                        "Confirm", 
                        JOptionPane.YES_NO_OPTION);
                
                if(userCmd == JOptionPane.YES_OPTION) {
                    int index = ndxSel.getValue();                    
                    final E removedElement = adapter.remove(index);
                    
                    setGUIComponentStatus();
                    if(--index >= 0) {
                        selectItem(index, true);
                    }
                    
                    fireElementRemoved(removedElement);
                }
            }
            else {
                JOptionPane.showMessageDialog(ItemController.this, "The minimum number of element is " + minItemCount + ".");
            }
        }
    }
    private void moveItem() {
        if(isItemSelectable()) {
            final IndexSelector idxselPosToMove = new IndexSelector();
            idxselPosToMove.setMinimum(0);
            idxselPosToMove.setMaximum(adapter.getItemCount()-1);
            idxselPosToMove.setValue(0); 
            
            final int userCmd = JOptionPane.showOptionDialog(
                ItemController.this, 
                idxselPosToMove, 
                "Select an index of element", 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE, 
                null, 
                null, 
                null);
                
            if(userCmd == JOptionPane.OK_OPTION) {
                final int srcElemIndex = ndxSel.getValue();
                final int destElemIndex = idxselPosToMove.getValue();
                adapter.move(destElemIndex, srcElemIndex);
                
                selectItem(destElemIndex, true);
                
                fireElementMoved(srcElemIndex, destElemIndex);
            }
        }
    }
    private void swapItems() {
        if(isItemSelectable()) {
            IndexSelector idxselPosToMove = new IndexSelector();                   
            idxselPosToMove.setMinimum(0);
            idxselPosToMove.setMaximum(adapter.getItemCount()-1);
            idxselPosToMove.setValue(0); 
            
            int userCmd = JOptionPane.showOptionDialog(
                ItemController.this, 
                idxselPosToMove, 
                "Select an index of element", 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE, 
                null, 
                null, 
                null);
                
            if(userCmd == JOptionPane.OK_OPTION) {
                int srcElemIndex = ndxSel.getValue();
                int destElemIndex = idxselPosToMove.getValue();
            
                adapter.swap(srcElemIndex, destElemIndex);
                
                selectItem(srcElemIndex, true);
                
                fireElementSwapped(srcElemIndex, destElemIndex);
            }
        }
    }
    
    private boolean isAvailable() {
        return isEnabled() && adapter != null;
    }
    private boolean isItemSelectable() {
        return isAvailable() && !adapter.isEmpty();
    }
    private void setGUIComponentStatus() {
        boolean available = isAvailable();
        boolean itemSelectable = isItemSelectable();
    
        ndxSel.setEnabled(itemSelectable);
        if(itemSelectable) {
            ndxSel.setMaximum(adapter.getItemCount()-1);
        }
        
        btnAdd.setEnabled(available);
        btnRemove.setEnabled(itemSelectable);
        btnMove.setEnabled(itemSelectable);
        btnSwap.setEnabled(itemSelectable);
    }
    
    ////////////////////////////////
    
    ////////////////////////////////
    //Event Handler Firers
    
    protected void fireIndexChanged(int prevIndex, int curIndex, E selectedItem) {
        Object [] listeners = listenerList.getListenerList();
        
        HIndexChangedEvent<E> event = null;
        
        for(int r1=listeners.length-2;r1>=0;r1-=2) {
            if(listeners[r1] == HEventListener.class) {
                if(event == null) {
                    event = new HIndexChangedEvent<E>(this, 0, prevIndex, curIndex, selectedItem);
                }
                
                ((HEventListener<E>)listeners[r1+1]).indexChanged(event);
            }
        }
    }
    protected void fireElementAdded(int newElemIndex) {
        Object [] listeners = listenerList.getListenerList();
        
        HItemAddedEvent<E> event = null;
        
        for(int r1=listeners.length-2;r1>=0;r1-=2) {
            if(listeners[r1] == HEventListener.class) {
                if(event == null) {
                    event = new HItemAddedEvent<E>(this, 0, newElemIndex);
                }
                
                ((HEventListener<E>)listeners[r1+1]).itemAdded(event);
            }
        }
    }
    protected void fireElementRemoved(E removedElement) {
        Object [] listeners = listenerList.getListenerList();
        
        HItemRemovedEvent<E> event = null;
        
        for(int r1=listeners.length-2;r1>=0;r1-=2) {
            if(listeners[r1] == HEventListener.class) {
                if(event == null) {
                    event = new HItemRemovedEvent<E>(this, 0, removedElement);
                }
                
                ((HEventListener<E>)listeners[r1+1]).itemRemoved(event);
            }
        }
    }
    protected void fireElementMoved(int srcElemIndex, int destElemIndex) {
        Object [] listeners = listenerList.getListenerList();
        
        HItemMovedEvent<E> event = null;
        
        for(int r1=listeners.length-2;r1>=0;r1-=2) {
            if(listeners[r1] == HEventListener.class) {
                if(event == null) {
                    event = new HItemMovedEvent<E>(this, 0, srcElemIndex, destElemIndex);
                }
                
                ((HEventListener<E>)listeners[r1+1]).itemMoved(event);
            }
        }
    }
    protected void fireElementSwapped(int srcElemIndex, int destElemIndex) {
        Object [] listeners = listenerList.getListenerList();
        
        HItemMovedEvent<E> event = null;
        
        for(int r1=listeners.length-2;r1>=0;r1-=2) {
            if(listeners[r1] == HEventListener.class) {
                if(event == null) {
                    event = new HItemMovedEvent<E>(this, 0, srcElemIndex, destElemIndex);
                }
                
                ((HEventListener<E>)listeners[r1+1]).itemSwapped(event);
            }
        }
    }
    
    ////////////////////////////////

    ////////////////////////////////
    //Fields

    private HItemAdapter<E> adapter;
    private EventListenerList listenerList;
    
    private int minItemCount;
    
    private IndexSelector ndxSel;
    private JButton btnAdd;
    private JButton btnRemove;
    private JButton btnMove;
    private JButton btnSwap;

    ////////////////////////////////

}
