package com.joyblind.poster.controller;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.joyblind.poster.model.Article;
import com.joyblind.poster.model.PosterModel;
import com.joyblind.poster.view.BasicView;

/**
 * @author Uri
 * 
 * 
 */
public class Poster extends JFrame implements ActionListener, DocumentListener {
    
    public static int WIDTH = 645; // was 665
    public static int HEIGHT = 375; // was 700
    public static String nameofProg = "JOYBLIND Poster " + PosterModel.VERSION + " By: Uri Gorelik";
    public static String SAVE_PATH = "";
    public static boolean newSavePath = false;

    private BasicView theView;
    private PosterModel model;
    private JMenuItem exit;
    private JMenuItem about;
    private JMenuItem saveDirectoryMenuItem;
    private String result;

    // Main
    public static void main(String[] args) {
        
        // If launched without the launcher.
        SAVE_PATH = System.getProperties().getProperty("user.dir");
        
        // Sets the version number
        if (args.length > 0) {
            SAVE_PATH = args[0];
        }

        new Poster();
        
        // Thanks the user for updating
        if (args.length > 1) {
            JOptionPane.showMessageDialog(null, 
                    "What's new in " + PosterModel.VERSION + "\nAdded http:// to the beginning of the direct Links.",
                    "Update Successful", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Constructor
    public Poster() {
        super(nameofProg);

        initialize();
        loadPrefs();

        addActionListeners();

        setVisible(true);
        setResizable(false);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);

        // This is for the jar
        if (PosterModel.RELEASE_MODE) {
            setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("poster.png")));
        } else {
            setIconImage(Toolkit.getDefaultToolkit().getImage("poster.png")); 
        }
        
    }

    /**
     * For the updater, does not work for Mac
     */
    private void loadPrefs() {
        String[] prefs = null;
        String strPrefs = null;
        try{
           BufferedReader in = new BufferedReader(new FileReader(PosterModel.APPDATA + File.separator +"prefs.cfg")); 
           strPrefs = in.readLine();
           in.close();
        } catch (FileNotFoundException ex) {
            return;
        } catch (IOException ex) {
            
        }
        
        prefs = strPrefs.split(",");
        
        // Make adjustments
        
        // Set the Default Author
        if (prefs.length > 1) {
            theView.getUserNames().setSelectedIndex(Integer.parseInt(prefs[1]));
        }
        
        // Set the Save path
        if (prefs.length > 2) {
            SAVE_PATH = prefs[2] + File.separator;
        }
        
    }

    private void initialize() {
        initMenu();

        // Add the view
        theView = new BasicView();
        model = new PosterModel();
        getContentPane().add(theView);
        
    }

    /**
     * Initializes the menues
     */
    private void initMenu() {
        // Menu
        JMenuBar menu = new JMenuBar();
        setJMenuBar(menu);

        JMenu file = new JMenu("File");
        menu.add(file);
        
        // File Menu
        saveDirectoryMenuItem = new JMenuItem("Output Location");
        file.add(saveDirectoryMenuItem);
        
        about = new JMenuItem("About");
        file.add(about);
        
        // Separator
        file.add(new JSeparator());
        exit = new JMenuItem("Exit");
        file.add(exit);
    }

    private void addActionListeners() {
        saveDirectoryMenuItem.addActionListener(this);
        about.addActionListener(this);
        exit.addActionListener(this);
        theView.getAddTagsButton().addActionListener(this);
        theView.getGenerateLocalButton().addActionListener(this);
        theView.getGenerateTemplateButton().addActionListener(this);
        theView.getMainText().getDocument().addDocumentListener(this);
        
        addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent we){
                model.savePrefs(theView.getUserNames().getSelectedIndex());
                System.exit(1);
              }
        });
    }

    private void update() {

        theView.getMainText().setText(result);
    }

    /**
     * @param j
     *            the action event which triggered this function
     */
    private void handleMenus(JMenuItem j) {
        if (j == exit) {
            System.exit(1);
        } 
        
        // About button
        else if (j == about) {
            JOptionPane.showMessageDialog(this, nameofProg, "About", JOptionPane.INFORMATION_MESSAGE);
        }
        
        // Save path
        else if (j == saveDirectoryMenuItem) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            
            int returnVal = chooser.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
              
                SAVE_PATH = chooser.getSelectedFile().getAbsolutePath() + File.separator;
                newSavePath = true;
            }

        }
    }

    
    /**
     * @param b
     */
    private void handleButtons(JButton b) {

        String paragraphs = "";
        String title = "";
        String author = "";
        int numberOfParagraphs = 0;
        
        // Get the number of paragraphs
        for (int i = 0; i < theView.getRadioButtons().length; i++) {
            if (theView.getRadioButtons()[i].isSelected()) {
                numberOfParagraphs = i + 1;
                break;
            }
        }
        
        title = theView.getTitleField().getText();
        paragraphs = theView.getMainText().getText();
        author = (String)theView.getUserNames().getSelectedItem();
        
        
        // Nothing entered
        if (paragraphs.equals("")) {
            JOptionPane.showMessageDialog(this,
                            "You need an article dummy.",
                            "No Article", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Too short 
        else if (paragraphs.length() < 168){                          
            JOptionPane.showMessageDialog(this,
                    "Make a twitter post instead.",
                    "Article too Short", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // addTags button
        if (b == theView.getAddTagsButton()) {
            
            result = "";
            model.addParagraphTags(theView.getMainText().getText());
            result = model.getArticleAsString();
            update();
        }
        
        // Generate Local Button
        else if (b == theView.getGenerateLocalButton()) {
            
            if (title.equals("")) {
                JOptionPane.showMessageDialog(this, 
                        "You must enter a title",
                        "No Title", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!model.isInitialized()){
                model.loadAssets(theView.getToArchiveCheckBox().isSelected(), new Article(title, paragraphs,numberOfParagraphs,author) );
            }
            
            model.generateAllFiles();
        } 
        
        // Generate Template
        else if (b == theView.getGenerateTemplateButton()) {
            
            if (title.equals("")) {
                JOptionPane.showMessageDialog(this, 
                        "You must enter a title",
                        "No Title", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!model.isInitialized()){
                model.loadAssets(theView.getToArchiveCheckBox().isSelected(), new Article(title, paragraphs,numberOfParagraphs, author));
            }
            model.generateTemplate();
        }
        
        // Reset the model here.
        model = new PosterModel();
    }

    private void handleTextFieldEntry() {

        // TODO figure out how to handle the text field
        // model.loadAssets(title, article);
        // model.loadText(theView.getMainText().getText());
    }


    /****************************************
     * Interfaces
     ****************************************/

    public void actionPerformed(ActionEvent e) {

        // Menus events
        if (e.getSource() instanceof JMenuItem) {
            handleMenus((JMenuItem) e.getSource());
        }

        // Button event
        if (e.getSource() instanceof JButton) {
            handleButtons((JButton) e.getSource());
        }

    }

    public void changedUpdate(DocumentEvent e) {
        handleTextFieldEntry();
    }

    public void insertUpdate(DocumentEvent e) {
        handleTextFieldEntry();
    }

    public void removeUpdate(DocumentEvent e) {
        handleTextFieldEntry();
    }
    
   

}
