
import org.apache.xerces.xs.StringList;

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
// - textleri gozukmucek sekilde ayarla(make ignoreList and add NER+Punctuations)
// - make == -> equals

public class Gui extends JPanel {//implements ActionListener

    private JFrame frame = new JFrame();
    private JButton translateButton;
    private JTextField userInputField;
    private JPanel inputPanel;
    private JPanel outputPanel = new JPanel(new GridLayout(0, 1));
    private final static String PATH = Linker.PICTO_FOLDER_PATH;

    private Map<String, ImageIcon> imageMap = createImageMap(Linker.listAllFiles(PATH).toArray(new String[0])); //final

    private static List<List<String>> sentences = new ArrayList<List<String>>();

    //each cell uses this renderer and calls the getListCellRendererComponent method.
    // The value you see passed to the method is the value in each cell, in my case, one of the character names in the list.
    // I then map that to the corresponding ImageIcon and set the Icon on the  JLabel renderer component.
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
            if (value == "nokta.png" && value == "person.png" && value == "location.png" && value == "organization.png") {//discard "."
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

    public List<List<String>> SplitPictoNameList(List<String> pictoNameList) {

        List<List<String>> splitSentences = new ArrayList<>();

        //add "." at the end of list if it lacks punctuation at the end of input
        if (!pictoNameList.isEmpty() && pictoNameList.get(pictoNameList.size() - 1) != "nokta.png")
            pictoNameList.add("nokta.png");

        //keep the "." count (for splitting)
        int count = 0;
        for (String s : pictoNameList) {
            if (s == "nokta.png" || s == "soru işareti.png" || s == "ünlem.png" || s == "noktalama işaretleri.png")
                count++;
        }

        //split the received output into sentences using punctuations
        for (int i = 0; i < count; i++) {
            splitSentences.add(pictoNameList.subList(0, pictoNameList.indexOf("nokta.png") + 1));
            pictoNameList = pictoNameList.subList(pictoNameList.indexOf("nokta.png") + 1, pictoNameList.size());
        }

        return splitSentences;
    }

    public void Display(List<List<String>> sentences) {
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
    }

    //StretchIcon si=new StretchIcon(getClass().getResource("bandaj.png"));

    Gui() {
        //input panel configuration
        inputPanel = new JPanel();
        inputPanel.setBackground(Color.cyan);
        //generate and add button and textField to inputPanel
        translateButton = new JButton("Cevir");
        userInputField = new JTextField();
        userInputField.setPreferredSize(new Dimension(800, 30));
        inputPanel.add(userInputField);
        inputPanel.add(translateButton);

        translateButton.addActionListener(new ActionListener() {
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
                    temp = PictogramRetriever.SelectDB(MorphologicalAnalysis.Analyze(userInputField.getText()));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                sentences = SplitPictoNameList(temp);
                Display(sentences);

                //show the output panel after button clicked and actions are performed
                outputPanel.setVisible(true);

                //clear everything
                temp.clear();
                sentences.clear();
            }
        });

        //frame configuration
        frame.add(outputPanel);//put outputPanel in frame
        outputPanel.setVisible(false);//hide outputPanel at first(before button click)
        frame.add(inputPanel, BorderLayout.SOUTH);//put the inputPanel at the bottom
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
