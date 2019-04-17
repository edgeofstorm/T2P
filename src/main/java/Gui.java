
import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.ImageObserver;
import java.net.URL;
import javax.swing.ImageIcon;
import java.lang.Object;
import java.lang.*;
import java.util.List;

import static java.awt.Image.SCALE_SMOOTH;

//FIXME
// - mouse a basinca resim gitsin adi gelsin.
// - jlist of jlist dene kac cumle oldugu -> outer jlist , cumleler -> inner jlist.
// - consider JTable
// - ONE SCROLLPANE thus one list for each sentence maybe try it
// - search for multiple scroll panels
// - 3 TANE CUMLE VARSA PANELI 3 E BOLUYO BUNUN YERINE HER CUMLE BELIRLI BIR SIZE DA ROW KAPLARSA RESIZE IMAGE OLAYI BITER.(PANELMAIN DECLARATION I CUMLEDEKI . COUNT SAYISINA GORE YAP)
// - SEARCH MIGLAYOUT, JAVAFX

public class Gui extends JPanel {//implements ActionListener

    private JFrame frame = new JFrame();
    private ImageIcon imageIcon;
    private JLabel label;
    private JScrollPane scrollPane;
    private JLabel row = new JLabel();
    private JButton buttonCevir;
    private JTextField textYazılan;
    private Map<String, ImageIcon> imageMap = createImageMap(T2P.listAllFiles(T2P.PICTO_FOLDER_PATH).toArray(new String[0])); //final
    private JPanel panelMain = new JPanel(new GridLayout(0, 1));
    private JPanel inputPanel;
    private JPanel container;


    public static List<List<String>> sentences = new ArrayList<List<String>>();

    public class MarioListRenderer extends DefaultListCellRenderer {

        Font font = new Font("helvitica", Font.BOLD, 24);

        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

           /* BufferedImage img = null;
            try {
                img = ImageIO.read(new File("C:\\Users\\haQQi\\Desktop\\Projects\\deneme1\\src\\bandaj.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Image dimg = img.getScaledInstance(label.getWidth(), label.getHeight(),
                    Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(dimg);
            label.setIcon(imageIcon);*/

            //ImageIcon io=new ImageIcon(imageMap.get((String) value).getImage());
            //label.setIcon(new StretchIcon(getClass().getResource("bandaj.png")));
            label.setIcon(imageMap.get((String) value));
            //label.setIcon(getClass().getResource("\\Pictograms\\"+s)).getImage().getScaledInstance(200, 200, SCALE_SMOOTH));
            /*label.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    JLabel label=(JLabel) e.getComponent();
                    Dimension size=label.getSize();
                    Image resized = imageIcon.getImage().getScaledInstance(size.width,size.height, SCALE_SMOOTH);
                    label.setIcon(new ImageIcon(resized));
                    //super.componentResized(e);
                }
            });*/
            //label.setIcon(new StretchIcon(imageMap.get((String) value).getImage()));
            /*label.setHorizontalTextPosition(JLabel.RIGHT);
            label.setFont(font);*/
            label.setText("");
            label.setIconTextGap(-200);
            return label;
        }
    }

    private Map<String, ImageIcon> createImageMap(String[] list) {
        Map<String, ImageIcon> map = new HashMap<>();
        for (String s : list) {
            map.put(s, new ImageIcon(new ImageIcon(
                    //getClass().getResource(s)));
                    getClass().getResource("\\Pictograms\\"+s)).getImage().getScaledInstance(200, 200, SCALE_SMOOTH)));
        }
        //StretchIcon si=new StretchIcon(getClass().getResource("bandaj.png"));
        return map;
    }

    Gui(List<String> images) {

        System.out.println(images);
        String[] nameList = images.toArray(new String[0]);//{"acil durum hattı.png","bandaj.png","ejderha.png","enerji.png","diyet.png","doktor.png",".","elf.png"};
        //List<List<String>> sentences=new ArrayList<List<String>>();
        //imageMap = createImageMap(nameList);

        /*int count = 0;
        for (String s : images) {
            if (s == ".")
                count++;
        }
        for (int i = 0; i < count; i++) {
            sentences.add(images.subList(0, images.indexOf(".")));
            images = images.subList(images.indexOf(".") + 1, images.size());
        }*/
        /*for(List<String> sentence:sentences){
            System.out.println(sentence);
            String[] arr = sentence.toArray(new String[0]);
            JList list = new JList(arr);
            list.setCellRenderer(new MarioListRenderer());
            list.setVisibleRowCount(1); //nokta sayisi
            list.setLayoutOrientation(JList.HORIZONTAL_WRAP);

            JScrollPane scroll = new JScrollPane(list);
            //scroll.setPreferredSize(new Dimension(300, 400));
            //scroll.setBackground(Color.GREEN);
            //scroll.setBorder(BorderFactory.createLineBorder(Color.BLACK,5));

            panelMain.add(scroll);
        }*/
        inputPanel = new JPanel();
        inputPanel.setBackground(Color.cyan);
        buttonCevir = new JButton("Cevir");
        buttonCevir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelMain.removeAll();
                panelMain.revalidate();
                panelMain.repaint();
                List<String> temp = new ArrayList<String>();
               try {
                    temp=T2P.SelectDB(T2P.ConvertPostag(T2P.Input2Picto(textYazılan.getText())));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                //temp.add("akvaryum.png");
                //temp.add("ejderha.png");
                //temp.add("enerji.png");
                temp.add(".");

                //imageMap=createImageMap(temp.toArray(new String[0]));
                System.out.println(temp);
                int count = 0;
                for (String s : temp) {
                    if (s == ".")
                        count++;
                }
                for(int i =0;i<count;i++){
                    sentences.add(temp.subList(0,temp.indexOf(".")));
                    temp=temp.subList(temp.indexOf(".")+1,temp.size());
                }
                for (List<String> sentence : sentences) {
                    System.out.println(sentence);
                    String[] arr = sentence.toArray(new String[0]);
                    JList list = new JList(arr);
                    list.setCellRenderer(new MarioListRenderer());
                    list.setVisibleRowCount(1); //nokta sayisi
                    list.setLayoutOrientation(JList.HORIZONTAL_WRAP);

                    JScrollPane scroll = new JScrollPane(list);
                    //scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                    //scroll.setPreferredSize(new Dimension(300, 400));
                    //scroll.setBackground(Color.GREEN);
                    //scroll.setBorder(BorderFactory.createLineBorder(Color.BLACK,5));

                    /*JLabel test = new JLabel(new StretchIcon(getClass().getResource("bandaj.png")));
                    scroll.add(test);*/
                    panelMain.add(scroll);
                }
                panelMain.setVisible(true);
                //clear everything
                temp.clear();
                sentences.clear();
                /*frame.revalidate();
                frame.repaint();*/
            }
        });
        textYazılan = new JTextField();
        textYazılan.setPreferredSize(new Dimension(800, 30));
        inputPanel.add(textYazılan);
        inputPanel.add(buttonCevir);
        /*panelMain.add(textYazılan);
        panelMain.add(buttonCevir);*/

        //container.add(panelMain);
        //container.add(inputPanel,BorderLayout.SOUTH);
        //JScrollPane scrPane = new JScrollPane(container);
        //frame.add(scrPane);

        /*JLabel test=new JLabel(new StretchIcon(getClass().getResource("bandaj.png")));
        panelMain.add(test);*/
        frame.add(panelMain);
        panelMain.setVisible(false);
        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

       /* int count=0;
        int temp=0;
        int highest=0;
        for (String image : images) {
            temp++;
            if(image=="."){
                highest=temp;
                temp=0;
                count++;
            }
        }*/

        /*setLayout(new FlowLayout());
        //setLayout(new GridBagLayout(count,highest));
        //setLayout(new MigLayout("fillx", "[right]rel[grow,fill]", "[]10[]"));

        //satira sigdircak sekilde scale olsun resimler . gelirse assagi satira gecsin bi satirda hallolsun bir cumle
        *//*scrollPane=new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBackground(Color.cyan);*//*

        DefaultListModel listModel = new DefaultListModel();
       *//* listModel.addElement("Jane Doe");
        listModel.addElement("John Smith");
        listModel.addElement("Kathy Green");*//*


        for (String image : images) {
            if(image=="."){
                count++;
                continue;
            }
            imageIcon = new ImageIcon(getClass().getResource(image));
            //Image newimg =imageIcon.getImage().getScaledInstance(240,240, Image.SCALE_SMOOTH);
            //imageIcon=new ImageIcon(newimg);
            label = new JLabel(imageIcon);
            //scrollPane.add(label);
            listModel.addElement(label);
            //add(label);
        }

        JList list = new JList(listModel); //data has type Object[]
        //list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        //list.setVisibleRowCount(-1);
        list.setBackground(Color.blue);
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setBackground(Color.cyan);
        //listScroller.setPreferredSize(new Dimension(250, 80));
        add(listScroller);*/

    }

    public class Action implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            /*for(List<String> sentence:sentences){
                System.out.println(sentence);
                String[] arr = sentence.toArray(new String[0]);
                JList list = new JList(arr);
                list.setCellRenderer(new MarioListRenderer());
                list.setVisibleRowCount(1); //nokta sayisi
                list.setLayoutOrientation(JList.HORIZONTAL_WRAP);

                JScrollPane scroll = new JScrollPane(list);
                //scroll.setPreferredSize(new Dimension(300, 400));
                //scroll.setBackground(Color.GREEN);
                //scroll.setBorder(BorderFactory.createLineBorder(Color.BLACK,5));

                panelMain.add(scroll);
                frame.add(panelMain,BorderLayout.NORTH);
            }*/
            /*panelMain.setVisible(true);
            panelMain.revalidate();
            panelMain.validate();
            panelMain.repaint();*/
            System.out.println("bas bas");

        }
    }

    public static void main(String[] Args) { // point png
        List<String> imageList = new ArrayList<String>();
        imageList.add("acil durum hattı.png");
        imageList.add("bandaj.png");
        imageList.add("ejderha.png");
        imageList.add("enerji.png");
        imageList.add("diyet.png");
        imageList.add("doktor.png");
        imageList.add(".");
        imageList.add("elf.png");
        imageList.add("ejderha.png");
        imageList.add("enerji.png");
        imageList.add(".");
        /*imageList.add("ejderha.png");
        imageList.add("acil durum hattı.png");
        imageList.add("bandaj.png");
        imageList.add("ejderha.png");
        imageList.add(".");*/

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Gui(imageList);
            }
        });
        /*JFrame frame=new JFrame();


        deneme1 gui = new deneme1(list);
        frame.setContentPane(gui);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //gui.setUndecorated(true);
        //frame.setPreferredSize();
        frame.setVisible(true);
        //gui.pack();
        frame.setTitle("imageTest");*/

        /*JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());

        MyTableModel model = new MyTableModel();

        JTable table = new JTable(model);
        table.setRowHeight(80);
        table.getColumnModel().getColumn(0).setCellRenderer(new ImageRenderer());
        JScrollPane pane = new JScrollPane(table);
        frame.getContentPane().add(BorderLayout.CENTER, pane);
        frame.setSize(500, 400);
        frame.setVisible(true);*/
    }
}

/*class MyTableModel extends AbstractTableModel {
    public Object getValueAt(int row, int column) {
        return "" + (row * column);
    }

    public int getColumnCount() {
        return 4;
    }

    public int getRowCount() {
        return 5;
    }
}

class ImageRenderer extends DefaultTableCellRenderer {
    JLabel lbl = new JLabel();

    ImageIcon icon = new ImageIcon(getClass().getResource("bandaj.png"));

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        lbl.setText((String) value);
        lbl.setIcon(icon);
        return lbl;
    }
}*/
class StretchIcon extends ImageIcon {

    /**
     * Determines whether the aspect ratio of the image is maintained.
     * Set to <code>false</code> to allow th image to distort to fill the component.
     */
    protected boolean proportionate = true;

    /**
     * Creates a <CODE>StretchIcon</CODE> from an array of bytes.
     *
     * @param imageData an array of pixels in an image format supported by
     *                  the AWT Toolkit, such as GIF, JPEG, or (as of 1.3) PNG
     * @see ImageIcon#ImageIcon(byte[])
     */
    public StretchIcon(byte[] imageData) {
        super(imageData);
    }

    /**
     * Creates a <CODE>StretchIcon</CODE> from an array of bytes with the specified behavior.
     *
     * @param imageData     an array of pixels in an image format supported by
     *                      the AWT Toolkit, such as GIF, JPEG, or (as of 1.3) PNG
     * @param proportionate <code>true</code> to retain the image's aspect ratio,
     *                      <code>false</code> to allow distortion of the image to fill the
     *                      component.
     * @see ImageIcon#ImageIcon(byte[])
     */
    public StretchIcon(byte[] imageData, boolean proportionate) {
        super(imageData);
        this.proportionate = proportionate;
    }

    /**
     * Creates a <CODE>StretchIcon</CODE> from an array of bytes.
     *
     * @param imageData   an array of pixels in an image format supported by
     *                    the AWT Toolkit, such as GIF, JPEG, or (as of 1.3) PNG
     * @param description a brief textual description of the image
     * @see ImageIcon#ImageIcon(byte[], java.lang.String)
     */
    public StretchIcon(byte[] imageData, String description) {
        super(imageData, description);
    }

    /**
     * Creates a <CODE>StretchIcon</CODE> from an array of bytes with the specified behavior.
     *
     * @param imageData     an array of pixels in an image format supported by
     *                      the AWT Toolkit, such as GIF, JPEG, or (as of 1.3) PNG
     * @param description   a brief textual description of the image
     * @param proportionate <code>true</code> to retain the image's aspect ratio,
     *                      <code>false</code> to allow distortion of the image to fill the
     *                      component.
     * @see ImageIcon#ImageIcon(byte[])
     * @see ImageIcon#ImageIcon(byte[], java.lang.String)
     */
    public StretchIcon(byte[] imageData, String description, boolean proportionate) {
        super(imageData, description);
        this.proportionate = proportionate;
    }

    /**
     * Creates a <CODE>StretchIcon</CODE> from the image.
     *
     * @param image the image
     * @see ImageIcon#ImageIcon(java.awt.Image)
     */
    public StretchIcon(Image image) {
        super(image);
    }

    /**
     * Creates a <CODE>StretchIcon</CODE> from the image with the specified behavior.
     *
     * @param image         the image
     * @param proportionate <code>true</code> to retain the image's aspect ratio,
     *                      <code>false</code> to allow distortion of the image to fill the
     *                      component.
     * @see ImageIcon#ImageIcon(java.awt.Image)
     */
    public StretchIcon(Image image, boolean proportionate) {
        super(image);
        this.proportionate = proportionate;
    }

    /**
     * Creates a <CODE>StretchIcon</CODE> from the image.
     *
     * @param image       the image
     * @param description a brief textual description of the image
     * @see ImageIcon#ImageIcon(java.awt.Image, java.lang.String)
     */
    public StretchIcon(Image image, String description) {
        super(image, description);
    }

    /**
     * Creates a <CODE>StretchIcon</CODE> from the image with the specified behavior.
     *
     * @param image         the image
     * @param description   a brief textual description of the image
     * @param proportionate <code>true</code> to retain the image's aspect ratio,
     *                      <code>false</code> to allow distortion of the image to fill the
     *                      component.
     * @see ImageIcon#ImageIcon(java.awt.Image, java.lang.String)
     */
    public StretchIcon(Image image, String description, boolean proportionate) {
        super(image, description);
        this.proportionate = proportionate;
    }

    /**
     * Creates a <CODE>StretchIcon</CODE> from the specified file.
     *
     * @param filename a String specifying a filename or path
     * @see ImageIcon#ImageIcon(java.lang.String)
     */
    public StretchIcon(String filename) {
        super(filename);
    }

    /**
     * Creates a <CODE>StretchIcon</CODE> from the specified file with the specified behavior.
     *
     * @param filename      a String specifying a filename or path
     * @param proportionate <code>true</code> to retain the image's aspect ratio,
     *                      <code>false</code> to allow distortion of the image to fill the
     *                      component.
     * @see ImageIcon#ImageIcon(java.lang.String)
     */
    public StretchIcon(String filename, boolean proportionate) {
        super(filename);
        this.proportionate = proportionate;
    }

    /**
     * Creates a <CODE>StretchIcon</CODE> from the specified file.
     *
     * @param filename    a String specifying a filename or path
     * @param description a brief textual description of the image
     * @see ImageIcon#ImageIcon(java.lang.String, java.lang.String)
     */
    public StretchIcon(String filename, String description) {
        super(filename, description);
    }

    /**
     * Creates a <CODE>StretchIcon</CODE> from the specified file with the specified behavior.
     *
     * @param filename      a String specifying a filename or path
     * @param description   a brief textual description of the image
     * @param proportionate <code>true</code> to retain the image's aspect ratio,
     *                      <code>false</code> to allow distortion of the image to fill the
     *                      component.
     * @see ImageIcon#ImageIcon(java.awt.Image, java.lang.String)
     */
    public StretchIcon(String filename, String description, boolean proportionate) {
        super(filename, description);
        this.proportionate = proportionate;
    }

    /**
     * Creates a <CODE>StretchIcon</CODE> from the specified URL.
     *
     * @param location the URL for the image
     * @see ImageIcon#ImageIcon(java.net.URL)
     */
    public StretchIcon(URL location) {
        super(location);
    }

    /**
     * Creates a <CODE>StretchIcon</CODE> from the specified URL with the specified behavior.
     *
     * @param location      the URL for the image
     * @param proportionate <code>true</code> to retain the image's aspect ratio,
     *                      <code>false</code> to allow distortion of the image to fill the
     *                      component.
     * @see ImageIcon#ImageIcon(java.net.URL)
     */
    public StretchIcon(URL location, boolean proportionate) {
        super(location);
        this.proportionate = proportionate;
    }

    /**
     * Creates a <CODE>StretchIcon</CODE> from the specified URL.
     *
     * @param location    the URL for the image
     * @param description a brief textual description of the image
     * @see ImageIcon#ImageIcon(java.net.URL, java.lang.String)
     */
    public StretchIcon(URL location, String description) {
        super(location, description);
    }

    /**
     * Creates a <CODE>StretchIcon</CODE> from the specified URL with the specified behavior.
     *
     * @param location      the URL for the image
     * @param description   a brief textual description of the image
     * @param proportionate <code>true</code> to retain the image's aspect ratio,
     *                      <code>false</code> to allow distortion of the image to fill the
     *                      component.
     * @see ImageIcon#ImageIcon(java.net.URL, java.lang.String)
     */
    public StretchIcon(URL location, String description, boolean proportionate) {
        super(location, description);
        this.proportionate = proportionate;
    }

    /**
     * Paints the icon.  The image is reduced or magnified to fit the component to which
     * it is painted.
     * <p>
     * If the proportion has not been specified, or has been specified as <code>true</code>,
     * the aspect ratio of the image will be preserved by padding and centering the image
     * horizontally or vertically.  Otherwise the image may be distorted to fill the
     * component it is painted to.
     * <p>
     * If this icon has no image observer,this method uses the <code>c</code> component
     * as the observer.
     *
     * @param c the component to which the Icon is painted.  This is used as the
     *          observer if this icon has no image observer
     * @param g the graphics context
     * @param x not used.
     * @param y not used.
     * @see ImageIcon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
     */
    @Override
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
        Image image = getImage();
        if (image == null) {
            return;
        }
        Insets insets = ((Container) c).getInsets();
        x = insets.left;
        y = insets.top;

        int w = c.getWidth() - x - insets.right;
        int h = c.getHeight() - y - insets.bottom;

        if (proportionate) {
            int iw = image.getWidth(c);
            int ih = image.getHeight(c);

            if (iw * h < ih * w) {
                iw = (h * iw) / ih;
                x += (w - iw) / 2;
                w = iw;
            } else {
                ih = (w * ih) / iw;
                y += (h - ih) / 2;
                h = ih;
            }
        }

        ImageObserver io = getImageObserver();
        g.drawImage(image, x, y, w, h, io == null ? c : io);
    }

    /**
     * Overridden to return 0.  The size of this Icon is determined by
     * the size of the component.
     *
     * @return 0
     */
    @Override
    public int getIconWidth() {
        return 0;
    }

    /**
     * Overridden to return 0.  The size of this Icon is determined by
     * the size of the component.
     *
     * @return 0
     */
    @Override
    public int getIconHeight() {
        return 0;
    }
}