package com.joyblind.poster.model;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import com.joyblind.poster.controller.Poster;

/**
 * Know issues
 * 
 * @author Uri
 * 
 */
public class PosterModel {

    public static File APPDATA = new File(System.getenv("APPDATA") + File.separator + ".joyblind");

    public static String VERSION = "1.5.11";

    public static boolean RELEASE_MODE = true; // Only used for icon might be
                                               // useless.

    public static String[] USER_NAMES = { "Uri Gorelik", "Vsevolod \"Seva\" Vodonenko", "Lucas Hudson", "Patrick Vice", "Serge Leshchuk" };
    private static String TEMPLATE_NAME = "template.txt"; // Name of the template file
    private static String INCEPTION_INDEX = "<!-- Extras -->";
    private static String INCEPTION_ARTICLES = "<!--Articles-->";
    private static String INCEPTION_RSS = "<!--items-->";
    private static String ENDPOINT_INDEX = "<!--Articles-->"; // was <-- Archive -->
    private static String ENDPOINT_ARTICLES = "<!--End Archive-->";
    private static String ENDPOINT_RSS = "<!--End Rss-->";

    // Where to insert in the URLs
    private static String LIVE_INCEPTION_INDEX = "<!--  title  -->";
    private static String LIVE_INCEPTION_ARTICLES = "<!--Archive-->";
    private static String LIVE_INCEPTION_RSS = "<item>";

    // Where to insert the paragraphs
    private static String PARAGRAPH_MAIN = "<!--insert here 1-->";
    private static String PARAGRAPH_INDEX = "<!--insert here 2-->";
    private static String PARAGRAPH_RSS = "<!--insert here 3-->";

    // URLS
    private static String URL_INDEX = "http://people.scs.carleton.ca/~ugorelik/index.html";
    private static String URL_OLD_INDEX = "http://people.scs.carleton.ca/~ugorelik/old_index.html";
    private static String URL_ARTICLES = "http://people.scs.carleton.ca/~ugorelik/articles.html";
    private static String URL_RSS = "http://people.scs.carleton.ca/~ugorelik/jb_rss.xml";
    private ArrayList<String> template; // The template for posting

    private boolean toArchive;
    private Article article;

    // URLs from the LIVE website
    private ArrayList<String> index;
    private ArrayList<String> oldIndex;
    private ArrayList<String> articles;
    private ArrayList<String> rss;
    private boolean initialized;

    // Main
    public static void main(String[] args) {

        // Prcesses with - Add to arive -- title args[0] --- article args[1]
        // ---- number of paragraphs to use in index ----- author

        long time = System.nanoTime();
        PosterModel joyblind = new PosterModel(true, new Article(args[0], args[1], 2, "Garry Manjaw"));

        joyblind.generateAllFiles();
        System.out.println((System.nanoTime() - time) * 1e-9);

    }

    /*******************************************************************************************************************
     * CONSTRUCTORS and Helper *
     *******************************************************************************************************************/

    /**
     * Constructor
     */
    public PosterModel() {
        initialized = false;
        article = new Article();

        template = new ArrayList<String>();

        index = new ArrayList<String>();
        oldIndex = new ArrayList<String>();
        articles = new ArrayList<String>();
        rss = new ArrayList<String>();
        establishURLConnection();

        loadTemplate(); // Loads the template

    }

    public PosterModel(boolean toArchive, Article article) {

        initialized = true;
        this.toArchive = toArchive;
        this.article = article;

        template = new ArrayList<String>();

        index = new ArrayList<String>();
        oldIndex = new ArrayList<String>();
        articles = new ArrayList<String>();
        rss = new ArrayList<String>();
        establishURLConnection();
        addParagraphTags(article.getArticleAsString());
        loadTemplate(); // Loads the template
        formatTemplate(article.getTitle());

    }

    public void loadAssets(boolean toArchive, Article article) {
        this.toArchive = toArchive;
        this.article = article;
        addParagraphTags(article.getArticleAsString());
        formatTemplate(article.getTitle());
        initialized = true;
    }

    /*******************************************************************************************************************
     * FILE CREATION *
     *******************************************************************************************************************/

    public void savePrefs(int selectedIndex) {

        // TODO: make is save locally if there is no appdata (i.e. on a mac)
        try {
            PrintWriter out = new PrintWriter(new FileWriter(APPDATA + File.separator + "prefs.cfg"));

            out.print(VERSION + "," + selectedIndex);

            out.print("," + Poster.SAVE_PATH);

            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertIntoTemplate() {
        // Insert the article
        findInceptionPoint(template, PARAGRAPH_MAIN, article.getParagraphs(), -1);
        findInceptionPoint(template, PARAGRAPH_INDEX, article.getParagraphs(), article.getNumberOfParagraphsForIndex());
        findInceptionPoint(template, PARAGRAPH_RSS, article.getParagraphs(), article.getNumberOfParagraphsForIndex());

        // Remove "Insert Heres"
        for (Iterator iterator = template.iterator(); iterator.hasNext();) {
            String s = (String) iterator.next();

            if (s.trim().startsWith("<!--insert here")) {
                iterator.remove();
            }
        }
    }

    public void generateTemplate() {

        insertIntoTemplate();

        try {
            PrintWriter out = new PrintWriter(new FileWriter(Poster.SAVE_PATH + article.getName() + ".html"));

            for (String s : template) {
                out.println(s);
            }

            out.close();
        } catch (IOException ex) {

        }

        System.out.println("Written successfully.");

    }

    public void generateAllFiles() {

        insertIntoTemplate();

        ArrayList<String> theIndex = null;

        try {
            createDocuments(rss, extractFromTemplate(INCEPTION_RSS, ENDPOINT_RSS), LIVE_INCEPTION_RSS, "jb_rss.xml");
            if (toArchive) {
                createDocuments(articles, extractFromTemplate(INCEPTION_ARTICLES, ENDPOINT_ARTICLES), LIVE_INCEPTION_ARTICLES, "articles.html");
            }

            theIndex = extractFromTemplate(INCEPTION_INDEX, ENDPOINT_INDEX);
            createDocuments(index, theIndex, LIVE_INCEPTION_INDEX, "index.html");
            createDocuments(oldIndex, theIndex, LIVE_INCEPTION_INDEX, "old_index.html");
            createDocuments(extractFromTemplate("<!--start-->", "<!-- Extras -->"));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("There was an IOEXCEPTION");
        }

    }

    private void createDocuments(ArrayList<String> theArticle) throws IOException {
        theArticle.add("</div>\r\n" + "</div>\r\n" + "</body>\r\n" + "</html>");

        PrintWriter out = new PrintWriter(new FileWriter(Poster.SAVE_PATH + article.getNameAsString() + ".html"));

        for (String s : theArticle) {
            out.println(s);
        }

        out.close();
    }

    private void createDocuments(ArrayList<String> listToAddTo, ArrayList<String> contentToAdd, String liveInception, String name) throws IOException {

        for (int i = 0; i < listToAddTo.size(); i++) {

            if (listToAddTo.get(i).trim().equals(liveInception)) {
                i--;
                for (String s : contentToAdd) {
                    i++;
                    listToAddTo.add(i, s);
                }

                break;
            }
        }

        PrintWriter out = new PrintWriter(new FileWriter(Poster.SAVE_PATH +File.separator+ name));

        for (String s : listToAddTo) {
            out.println(s);
        }

        out.close();

    }

    /**
     * Loads in the template
     */
    private void loadTemplate() {
        BufferedReader in;
        try {
            if (RELEASE_MODE) {
                in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(TEMPLATE_NAME)));
            } else {
                in = new BufferedReader(new FileReader(TEMPLATE_NAME));
            }

            while (in.ready()) {
                template.add(in.readLine());
            }
            in.close();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Template not found cannot continue.", "No Template", JOptionPane.ERROR_MESSAGE);

            System.exit(-1);
        } catch (IOException ex) {
        }
    }

    /**
     * 
     * @param title
     */
    private void formatTemplate(String title) {
        Pattern date = Pattern.compile("DDD, DD MM YYYY XX:XX");
        Pattern remove = Pattern.compile("<!--insert here");
        Pattern name = Pattern.compile("1XXXXXXX");
        Pattern blurb = Pattern.compile("<!--BLURB-->");
        Pattern titlePattern = Pattern.compile("TITLE");
        Pattern comment = Pattern.compile("Read More...");
        Pattern authorName = Pattern.compile("<!-- Author Name -->");
        Pattern thumb = Pattern.compile("<!-- thumb_author -->");
        Matcher matcher;

        ArrayList<String> temp = new ArrayList<String>();

        String currentDate = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss").format(new Date());

        for (Iterator iterator = template.iterator(); iterator.hasNext();) {
            String s = (String) iterator.next();

            // Removes unescesary lines
            matcher = remove.matcher(s.trim());
            if (matcher.find()) {
                iterator.remove();
            }

            if (toArchive) {
                // Swap the blurb and check for length
                matcher = blurb.matcher(s);
                if (matcher.find()) {

                    String theBlurb = ""; // The short blurb in the archives
                                          // section

                    // 235 characters is the maximum size we can go.
                    if (article.getParagraphs().get(0).length() > 245) {
                        theBlurb = removeParagraphTags(article.getParagraphs().get(0).trim());
                        theBlurb = theBlurb.substring(0, 235) + "...";
                    } else {
                        theBlurb = removeParagraphTags(article.getParagraphs().get(0));
                    }

                    // TODO Fix to support $ signs
                    s = matcher.replaceAll(theBlurb);
                }
            }

            // Swap for author name
            matcher = authorName.matcher(s);
            if (matcher.find()) {
                s = matcher.replaceAll(this.article.getAuthor());

                // Replace the thumbnail
                matcher = thumb.matcher(s);
                s = matcher.replaceAll(this.article.getThumbName());
            }

            // Swap the file name
            matcher = name.matcher(s);
            if (matcher.find()) {
                s = matcher.replaceAll(this.article.getNameAsString());
            }

            // Swap the date
            matcher = date.matcher(s);
            if (matcher.find()) {
                s = matcher.replaceAll(currentDate);
            }

            // Swap the title
            matcher = titlePattern.matcher(s);
            if (matcher.find()) {
                s = matcher.replaceAll(title);
            }

            // Check if article is same length as its index version
            if (article.getTotalParagraphs() == article.getNumberOfParagraphsForIndex()) {
                matcher = comment.matcher(s);
                s = matcher.replaceAll("Comment!"); // TODO check if this works
            }

            temp.add(s);

        }

        template = temp; // Set the new template

        // DEBUG
        // for (String s : template) {
        // System.out.println(s);
        // }
    }

    private String removeParagraphTags(String s) {
        String returning = "";

        // 6 because </p> is 4 plus \n \n
        returning = s.substring(3, s.length() - 6);

        return returning;
    }

    public void addParagraphTags(String s) {

        article.setParagraphs(new ArrayList<String>());
        for (String x : s.split("\n")) {
            if (!x.trim().equals("")) {

                if (x.trim().startsWith("<p") || x.trim().startsWith("<h")) {
                    article.getParagraphs().add(x + "\n\n");
                } else {
                    article.getParagraphs().add(addTags("p", x));
                }

            }
        }

    }

    private String addTags(String tag, String toAddTo) {

        String returning = "";

        returning += "<" + tag + ">" + toAddTo.trim() + "</" + tag + ">" + "\n\n"; // Build
                                                                                   // the
                                                                                   // document

        return returning;
    }

    public String getArticleAsString() {

        String returning = "";

        for (String s : article.getParagraphs()) {
            returning += s; // Build the document
        }

        return returning;
    }

    private ArrayList<String> extractFromTemplate(String start, String end) {

        ArrayList<String> returning = new ArrayList<String>();
        int intStart = 0;
        int intEnd = 0;

        for (int i = 0; i < template.size(); i++) {

            if (template.get(i).trim().equals(start.trim())) {
                intStart = i;
            } else if (template.get(i).trim().equals(end.trim())) {
                intEnd = i;
                break;
            }
        }

        intStart++; // Line after
        intEnd--; // Line before

        for (int i = intStart; i <= intEnd; i++) {
            returning.add(template.get(i));
            template.remove(i);
            i--;
            intEnd--;
        }

        return returning;
    }

    /**
     * Opens a URL connection and grabs a file. Saves into ArrayList
     */
    private void establishURLConnection() {

        index = establishURLConnection(URL_INDEX);
        oldIndex = establishURLConnection(URL_OLD_INDEX);
        articles = establishURLConnection(URL_ARTICLES);
        rss = establishURLConnection(URL_RSS);
    }

    private ArrayList<String> establishURLConnection(String theURL) {

        URL url = null;
        try {
            url = new URL(theURL);
        } catch (MalformedURLException e) {
            System.out.println("The URL was not found");
        }

        try {
            // Make sure the whole thing gets sent
            ArrayList<String> returning;
            returning = readURL(url.openStream());
            while (!returning.get(returning.size() - 1).trim().equals("</html>") && !returning.get(returning.size() - 1).trim().equals("</rss>")) {
                url = new URL(theURL);
                returning = readURL(url.openStream());
            }

            return returning;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Helper method for establishURL Connection. Stores in program.
     * 
     * @param stream
     *            : The stream opened by the URL
     * @param addTo
     *            : The collection to be added to
     * @throws IOException
     */
    private ArrayList<String> readURL(InputStream stream) {
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(stream));
            ArrayList<String> addTo = new ArrayList<String>();

            while (!in.ready()) {
                in = new BufferedReader(new InputStreamReader(stream));
            }

            while (in.ready()) {
                String currentLine = in.readLine();
                if (!currentLine.trim().equals("") || currentLine == null)
                    addTo.add(currentLine);
            }

            in.close();

            return addTo;
        } catch (IOException ex) {

        }

        return null;

    }

    /**
     * 
     * @param template
     * @param inceptionPoint
     * @param toAppend
     * @param numOfParagraphs
     */
    private void findInceptionPoint(ArrayList<String> template, String inceptionPoint, ArrayList<String> toAppend, int numOfParagraphs) {

        int counter = 0;
        for (int i = 0; i < template.size(); i++) {

            counter++;
            if (template.get(i).contains(inceptionPoint)) {
                break;
            }
        }

        insertArticle(counter, template, toAppend, numOfParagraphs);

    }

    /**
     * Inserts the article at a specified location
     * 
     * @param location
     *            : where the article is to be inserted (line number)
     * @param template
     *            : What the user entered
     * @param toAppend
     * @param numOfParagraphs
     */
    private void insertArticle(int location, ArrayList<String> template, ArrayList<String> toAppend, int numOfParagraphs) {

        int counter = location;

        // Full inception
        if (numOfParagraphs == -1) {
            for (int i = 0; i < toAppend.size(); i++) {
                template.add(counter, toAppend.get(i));
                counter++;
            }
        } else { // Adds a specified number of paragraphs (used for index and
                 // what not);
            for (int i = 0; i < numOfParagraphs; i++) {
                template.add(counter, toAppend.get(i));
                counter++;
            }
        }

    }

    // Work in progress methods

    /*******************************************************************************************************************
     * GETTERS and SETTERS *
     *******************************************************************************************************************/
    public boolean isInitialized() {
        return initialized;
    }

}
