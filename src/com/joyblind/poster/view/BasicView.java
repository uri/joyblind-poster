package com.joyblind.poster.view;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.joyblind.poster.controller.Poster;
import com.joyblind.poster.model.PosterModel;

/**
 * @author Uri
 *
 */
public class BasicView extends JPanel {
	public static int	minusW	= 150;
	public static int	minusH	= 60;
	
	private static int TEXT_AREA_WIDTH = 460;
	private static int TEXT_AREA_HEIGHT = 280;
	
	private static int BUTTON_SIZE_WIDTH = 160;
	private static int BUTTON_SIZE_HEIGHT = 40;
	

	private JComboBox       userNames;
    private JTextField      titleField;
    private JTextArea       mainText;
    private JButton         addTagsButton;
    private JButton         generateTemplateButton;
    private JButton         generateLocalButton;
    private JRadioButton[]  radioButtons;
	private JCheckBox       toArchiveCheckBox;
	private JCheckBox       llArchiveCheckBox;
	private ButtonGroup     archiveButtongGroup;
	private ButtonGroup     radioButtonGroup;

	public BasicView() {
		super();
		setLayout(null);

		initialize();
	}

	private void initTextField() {

	    mainText = new JTextArea(12, 14);
	    mainText.setWrapStyleWord(true);
	    mainText.setLineWrap(true);
	    mainText.setSize(TEXT_AREA_WIDTH, TEXT_AREA_HEIGHT);
	    mainText.setLocation(0, 0);
	    
	    JLabel label = new JLabel("Number of Paragraphs");
	    label.setSize(200, 20);
	    label.setLocation(mainText.getWidth() + 20, 240);
	    add(label);
	    
	    // Names
	    userNames = new JComboBox(PosterModel.USER_NAMES);
	    userNames.setSize(BUTTON_SIZE_WIDTH, 20);
	    userNames.setLocation(mainText.getWidth() + 10, 10);
	    add(userNames);
	    
	    // Radio Buttons
	    radioButtonGroup = new ButtonGroup();
	    radioButtons = new JRadioButton[4];
	    for (int i = 0; i < radioButtons.length; i++) {
	        radioButtons[i] = new JRadioButton(""+(i+1));
	        radioButtons[i].setSize(34, 20);
	        radioButtons[i].setLocation(mainText.getWidth() + 15 + ((i) *40), 260);
	        add(radioButtons[i]);
	        radioButtonGroup.add(radioButtons[i]);
	    }
	    
	    radioButtons[0].setSelected(true);     // Default selection
	    
	    // Title field
	    titleField = new JTextField();
	    titleField.setSize(TEXT_AREA_WIDTH, 25);
	    titleField.setLocation(5, 10);
	    add(titleField);
	    

		

		// Enable scrolling
		JScrollPane scroll = new JScrollPane(mainText,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// Set dimentions
		scroll.setSize(TEXT_AREA_WIDTH, TEXT_AREA_HEIGHT);
//		scroll.setSize(Poster.width - minusW, Poster.height - minusH);
		scroll.setLocation(5 , 10 + titleField.getHeight());

		add(scroll); // Add the text area
		
		// Button group for the archiving
//		archiveButtongGroup = new ButtonGroup();
		
		toArchiveCheckBox = new JCheckBox("Archive");
        toArchiveCheckBox.setSize(70,20);
        toArchiveCheckBox.setLocation(mainText.getWidth() + 10, 290);
        toArchiveCheckBox.setSelected(true);
        add(toArchiveCheckBox);
        
//        llArchiveCheckBox = new JCheckBox("Lucas Links");
//        llArchiveCheckBox.setSize(100,20);
//        llArchiveCheckBox.setLocation(mainText.getWidth() + 80, 290);
//        llArchiveCheckBox.setSelected(true);
//        add(llArchiveCheckBox);
        
        // Add the check boxes to the button group
//        archiveButtongGroup.add(toArchiveCheckBox);
//        archiveButtongGroup.add(llArchiveCheckBox);
        
		
	}

	private void initButtons() {
		addTagsButton = new JButton("Add Tags");
		addTagsButton.setMnemonic('t');
		addTagsButton.setSize(BUTTON_SIZE_WIDTH, BUTTON_SIZE_HEIGHT);
		addTagsButton.setLocation(mainText.getWidth() + 10, 40);
		add(addTagsButton);
		
		generateTemplateButton = new JButton("Create Template");
        generateTemplateButton.setMnemonic('T');
        generateTemplateButton.setSize(BUTTON_SIZE_WIDTH, BUTTON_SIZE_HEIGHT);
        generateTemplateButton.setLocation(mainText.getWidth() + 10, BUTTON_SIZE_HEIGHT +addTagsButton.getLocation().y+ 10);
        add(generateTemplateButton);
		
		generateLocalButton = new JButton("Create Local");
		generateLocalButton.setMnemonic('c');
		generateLocalButton.setSize(BUTTON_SIZE_WIDTH, BUTTON_SIZE_HEIGHT);
		generateLocalButton.setLocation(mainText.getWidth() + 10, BUTTON_SIZE_HEIGHT + generateTemplateButton.getLocation().y + 10);
		add(generateLocalButton);
		
	}

	private void initialize() {
		initTextField();
		initButtons();
	}

	// Getters and Setters
	public JTextArea getMainText() { return mainText; }
	public void setMainText(JTextArea mainText) { this.mainText = mainText; }

    public JTextField getTitleField() { return titleField;}
    public void setTitleField(JTextField titleField) { this.titleField = titleField;}
    public JCheckBox getToArchiveCheckBox() { return toArchiveCheckBox;}
    
    public JButton getAddTagsButton() { return addTagsButton; }
    public JButton getGenerateTemplateButton() { return generateTemplateButton;}
    public JButton getGenerateLocalButton() { return generateLocalButton;}
    public ButtonGroup getRadioButtonGroup() { return radioButtonGroup; }

    public JRadioButton[] getRadioButtons() {return radioButtons; }
    
    public JComboBox getUserNames() { return userNames;}
    
    public static void main (String[] args) {
        JFrame f = new JFrame("View Testing");

        f.add(new BasicView());

       f.setVisible(true);
       f.setResizable(false);
       f.setSize(Poster.WIDTH, Poster.HEIGHT);
       f.setLocationRelativeTo(null);
       f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}








