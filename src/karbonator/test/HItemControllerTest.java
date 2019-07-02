package karbonator.test;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import karbonator.collection.Vector;
import karbonator.swing.ItemController;
import karbonator.swing.ItemController.HIndexChangedEvent;
import karbonator.swing.ItemController.HItemAddedEvent;
import karbonator.swing.ItemController.HItemMovedEvent;
import karbonator.swing.ItemController.HItemRemovedEvent;

public class HItemControllerTest extends JFrame {

    private static final long serialVersionUID = 1L;

    public HItemControllerTest() {
        super("");
        
        strings = new Vector<String>(new String [] {
            "Foo", "Bar", "Baz", "Qux", "Quux", "Quuux", "Piyo"
        });
        
        paneMain = new JPanel();
        strCtrl = new ItemController<String>();
        lbString = new JLabel();

        paneMain.setLayout(new BorderLayout());
        paneMain.add(strCtrl, BorderLayout.CENTER);
        paneMain.add(lbString, BorderLayout.SOUTH);
        strCtrl.setPreferredSize(new Dimension(256, 96));
        lbString.setPreferredSize(new Dimension(256, 32));
        setContentPane(paneMain);
        
        //����� ���� �� ù ��° �������� ǥ�õ��� �ʴ� ������ �ذ��ϱ�����
        //�̺�Ʈ �����ʸ� ���� �߰�
        strCtrl.addEventListener(new ItemController.HEventListener<String>() {
            @Override
            public void indexChanged(HIndexChangedEvent<String> e) {
                lbString.setText(e.getSelectedItem());
            
                System.out.println("indexChanged\n");
            }
            @Override
            public void itemAdded(HItemAddedEvent<String> e) {
                System.out.println("itemAdded\n");
            }
            @Override
            public void itemRemoved(HItemRemovedEvent<String> e) {
                if(strings.isEmpty()) {
                    lbString.setText("");
                }
            
                System.out.println("itemRemoved\n");
            }
            @Override
            public void itemMoved(HItemMovedEvent<String> e) {
                System.out.println("itemMoved\n");
            }
            @Override
            public void itemSwapped(HItemMovedEvent<String> e) {
                System.out.println("itemSwapped\n");
            }
        });
        strCtrl.attachItemAdapter(new ItemController.HItemAdapter<String>() {
            @Override
            public int getItemCount() {                
                return strings.getSize();
            }
            @Override
            public boolean isEmpty() {
                return strings.isEmpty();
            }

            @Override
            public String refer(int index) {
                return strings.at(index);
            }

            @Override
            public String add() {
                String str = new String(""+((int)(Math.random()*128)));
                strings.pushBack(str);
                
                return str;
            }
            @Override
            public String remove(int index) {
                return strings.remove(index);
            }
            @Override
            public void move(int destIndex, int srcIndex) {
                String target = strings.remove(srcIndex);
                strings.insert(destIndex, target);
            }
            @Override
            public void swap(int lhsIndex, int rhsIndex) {
                strings.swap(lhsIndex, rhsIndex);
            }
        });

        pack();
    }

    public static void main(String [] args) {
        new HItemControllerTest().setVisible(true);
    }

    private Vector<String> strings;

    private JPanel paneMain;
    private ItemController<String> strCtrl;
    private JLabel lbString;

}
