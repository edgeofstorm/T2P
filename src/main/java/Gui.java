
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
// - 3 TANE CUMLE VARSA PANELI 3 E BOLUYO BUNUN YERINE HER CUMLE BELIRLI BIR SIZE DA ROW KAPLARSA RESIZE IMAGE OLAYI BITER.(outputPanel DECLARATION I CUMLEDEKI . COUNT SAYISINA GORE YAP)
// - SEARCH MIGLAYOUT, JAVAFX
// - noktaya gore boluyo cumleleri onu duzelt

public class Gui extends JPanel {//implements ActionListener

    private JFrame frame = new JFrame();
    private JButton buttonCevir;
    private JTextField textYazılan;
    private JPanel inputPanel;
    private JPanel outputPanel = new JPanel(new GridLayout(0, 1));

    private Map<String, ImageIcon> imageMap = createImageMap(T2P.listAllFiles(T2P.PICTO_FOLDER_PATH).toArray(new String[0])); //final

    public static List<List<String>> sentences = new ArrayList<List<String>>();

    public class PictoListRenderer extends DefaultListCellRenderer {

        //personal choice of font
        Font font = new Font("helvitica", Font.BOLD, 24);

        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            //Get the JLabel (as JList component) and modify
            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            //Get the icon from map
            label.setIcon(imageMap.get((String) value));

            //Printing Text under pictogram
            if (value == "nokta.png") {//discard "."
                label.setText("");
                label.setVerticalAlignment(JLabel.NORTH);
            } else {
                label.setText(((String) value).substring(0, ((String) value).indexOf(".")));
                label.setHorizontalTextPosition(JLabel.CENTER);
                label.setVerticalTextPosition(JLabel.BOTTOM);
                label.setFont(font);
            }
            return label;
        }
    }

    private Map<String, ImageIcon> createImageMap(String[] list) {
        Map<String, ImageIcon> map = new HashMap<>();
        for (String s : list) {
            map.put(s, new ImageIcon(new ImageIcon(
                    //get the icon version of pictogram and map it to its name.
                    getClass().getResource("\\Pictograms\\" + s)).getImage().getScaledInstance(200, 200, SCALE_SMOOTH)));
        }
        //map<String,ImageIcon> is created like this ((String)"araba.png" , (ImageIcon)araba.png)
        return map;
    }

    //StretchIcon si=new StretchIcon(getClass().getResource("bandaj.png"));

    Gui() {
        //input panel configuration
        inputPanel = new JPanel();
        inputPanel.setBackground(Color.cyan);
        //generate and add button and textField to inputPanel
        buttonCevir = new JButton("Cevir");
        textYazılan = new JTextField();
        textYazılan.setPreferredSize(new Dimension(800, 30));
        inputPanel.add(textYazılan);
        inputPanel.add(buttonCevir);


        buttonCevir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //First clear everything from outputPanel
                outputPanel.removeAll();
                outputPanel.revalidate();
                outputPanel.repaint();

                //for keeping the received output from other classes
                List<String> temp = new ArrayList<String>();
                //Main process(Getting input and sending it to other classes -> Receiving output)
                try {
                    temp = T2P.SelectDB(T2P.ConvertPostag(T2P.Input2Picto(textYazılan.getText())));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                //add "." at the end of list if it lacks punctuation at the end of input
                if (!temp.isEmpty() && temp.get(temp.size() - 1) != "nokta.png")
                    temp.add("nokta.png");

                //keep the "." count (for splitting)
                int count = 0;
                for (String s : temp) {
                    if (s == "nokta.png")
                        count++;
                }

                //split the received output into sentences using punctuations
                for (int i = 0; i < count; i++) {
                    sentences.add(temp.subList(0, temp.indexOf("nokta.png") + 1));
                    temp = temp.subList(temp.indexOf("nokta.png") + 1, temp.size());
                }

                //iterate through all sentences
                for (List<String> sentence : sentences) {
                    //construct JList from output
                    String[] arr = sentence.toArray(new String[0]);
                    JList list = new JList(arr);

                    //call the modified cell renderer for list(every element of list is modified)
                    list.setCellRenderer(new PictoListRenderer());

                    //one sentence for one row
                    list.setVisibleRowCount(1);

                    //display list elements horizontally
                    list.setLayoutOrientation(JList.HORIZONTAL_WRAP);

                    //in case if a sentence is too long display it in a scrollable container
                    JScrollPane scroll = new JScrollPane(list);

                    //finally add scrollable pane to outputPanel
                    outputPanel.add(scroll);
                    //repeat the process for every sentence
                }
                //show the output panel after button clicked and actions are performed
                outputPanel.setVisible(true);

                //clear everything
                temp.clear();
                sentences.clear();
            }
        });


        //frame configuration
        frame.add(outputPanel);//put outputpanel in frame
        outputPanel.setVisible(false);//hide outputPanel at first(before button click)
        frame.add(inputPanel, BorderLayout.SOUTH);//put the input panel at the bottom
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);//Default size of GUI
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);//Start in fullscreen
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] Args) {
        //Running GUI from main
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Gui();
            }
        });
    }
}

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