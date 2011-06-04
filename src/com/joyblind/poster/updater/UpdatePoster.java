package com.joyblind.poster.updater;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.swing.JOptionPane;


/**
 * Notes
 * 
 * System.getProperties().getProperty("user.home")
 * 
 * @author Uri
 *
 */
public class UpdatePoster {

    private static String VERSION = "";
    private static String[] commands;
    private static String[] prefs;
    private static String DOWNLOAD_LOCATION_JAR = "";
    private static String LAUNCH_NAME = "";
    private static File file = new File(System.getenv("APPDATA") + File.separator + ".joyblind");
    
    public static void main(String[] args) throws MalformedURLException, IOException {
        
        getCommands();
        getPrefs();
        
        // First time launch
        if (file.mkdir()) {
            update();
            launch("");
            System.exit(1);
        }

        // Normal
        if (!updateAvailable()) {
            
            // Check that they have something to run
            if (!checkForExistingPoster()) {
                update();
            }
            
            // Launch normally
            launch("");
            System.exit(1);
        }
        
        System.exit(1);
    }
    
    private static void getPrefs() throws MalformedURLException, IOException {
        try {
            BufferedReader in = new BufferedReader(new FileReader(file + File.separator + "prefs.cfg"));
            String strPrefs = in.readLine();
            in.close();

            prefs = strPrefs.split(",");
            
        } catch (FileNotFoundException e) {

        } 
    }

    private static boolean checkForExistingPoster() {
        
        File jar = new File(file + File.separator + LAUNCH_NAME);
        
        return jar.exists();
        
    }
    

    private static void launch(String special) throws IOException{
        Runtime.getRuntime().exec("javaw -jar " + file + File.separator + LAUNCH_NAME + " "
                + System.getProperties().getProperty("user.dir")+ "\\" + " "
                + special);
        
        System.exit(1);
    }
    
    public static void update() throws MalformedURLException, IOException {
        URL urlDownload = new URL(DOWNLOAD_LOCATION_JAR);
        ReadableByteChannel rbc = Channels.newChannel(urlDownload.openStream());
        FileOutputStream fos = new FileOutputStream(file.getAbsolutePath()+ File.separator + LAUNCH_NAME);
        fos.getChannel().transferFrom(rbc, 0, 1 << 24);
        
    }
    
    /**
     * Reads in the update file
     */
    public static void getCommands() {
        String returning = "";
        URL update = null;
        int option = -1;
        
        // Get the update file
        try { 
            update = new URL("http://people.scs.carleton.ca/~ugorelik/content/update");
        } catch (MalformedURLException e) { e.printStackTrace(); }
        
        // Read it in
        try { 
            BufferedReader in = new BufferedReader(new InputStreamReader(update.openStream()));
            
            // Read in the update
            while (in.ready()) {
                returning+= in.readLine();
            }
        } catch (IOException e) {  e.printStackTrace();}
        
        // Grab the commands
        commands = returning.split(",");

        // Set the commands
        VERSION = commands[0];
        DOWNLOAD_LOCATION_JAR = commands[1];
        LAUNCH_NAME = DOWNLOAD_LOCATION_JAR.split("/")[DOWNLOAD_LOCATION_JAR.split("/").length - 1];
    }
    

    public static boolean updateAvailable() throws MalformedURLException, IOException{
        
        int option = -1;
        
        
        if (prefs == null) {
            return false;
        }
        
        if (!prefs[0].equals(VERSION)) {
            option = JOptionPane.showConfirmDialog(null, 
                    "There is a snazy new version of the Poster. Would you like to perform an update?",
                    "Update Available", JOptionPane.INFORMATION_MESSAGE);
            
            if (option == JOptionPane.YES_OPTION) {
                update();
                launch("updated");
            } else if (option == JOptionPane.NO_OPTION) {
                launch("");
            } else if (option == JOptionPane.CLOSED_OPTION) {
                launch("");
            }
            
            return true;
        }
        
        
        return false;
    }

    


}
