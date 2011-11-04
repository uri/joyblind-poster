package com.joyblind.poster.model;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Article {
    
    private String title;
    private String author;
    private int name;
    private int numberOfParagraphsForIndex;
    private int totalParagraphs;
    ArrayList<String> paragraphs;
    
    public static void main (String[] args) {

    }
    
    public Article() {
        this.totalParagraphs = 0;
        this.title = "";
        this.author = "";
        this.name = generateArticleName();
        this.paragraphs = new ArrayList<String>();
        this.numberOfParagraphsForIndex = -1;
    }
    
    // TODO find out if this is ever used
    public Article(String title, String paragraphs) {
        this.name = generateArticleName();
        this.title = title;
        this.paragraphs = parseStringToList(paragraphs);
        this.numberOfParagraphsForIndex = -1;
    }
    
    public Article(String title, String paragraphs, int numberOfParagraphs, String author) {
        this.name = generateArticleName();
        this.title = title;
        this.author = author;
        this.paragraphs = parseStringToList(paragraphs);
        this.numberOfParagraphsForIndex = numberOfParagraphs;
    }
   
    
    public int generateArticleName() {
        
        URL url = null;
        try {
            url = new URL("http://people.scs.carleton.ca/~ugorelik/index.html");
            
        } catch (MalformedURLException ex) {
            return -1;
        }
        
        ArrayList<String> index = new ArrayList<String>();
        
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            
            // Tries to establish a connetion
            while(!in.ready()) {
                in = new BufferedReader(new InputStreamReader(url.openStream()));
            }
            
            while (in.ready()) {
                index.add(in.readLine());
            }
            
            in.close();
        } catch (IOException ex) {
            return -1;
        }
        
        
        
        
        DateFormat nameFormat = new SimpleDateFormat("MMddyy");
        Pattern p = Pattern.compile("(1[0-1][0-9][0-3][0-9][0-9]{4})\\.[a-zA-Z]{3,4}");
        Matcher m;
        
        String generated = (1 + nameFormat.format(new Date()) + "01");
        String currentDate = "";
        int intDate = 0;
        
        for (String s : index) {
            m = p.matcher(s);
            
            if (m.find()) {
                currentDate = m.group(1);
                break;
            }
        }
        
        if (currentDate.equals("")) return -1;
        
        
        int intGeneratedDate = Integer.parseInt(generated.substring(0, generated.length() - 2));
        int intCurrentDate = Integer.parseInt(currentDate.substring(0, currentDate.length() - 2));;
        
        // If there's already an article today add one
        if (intCurrentDate == intGeneratedDate) {
            return Integer.parseInt(currentDate) + 1;
        } else {
            return Integer.parseInt(generated);
        }
        
        
    }
    
    public String getThumbName() {
        return "thumb_" + author.split(" ")[0].toLowerCase() + ".jpg";
    }
    
    public String getArticleAsString() {
        String returning = "";
        
        for (String s : paragraphs) {
            returning += s + "\n\n";
        }
        
        return returning;
    }
    
    public ArrayList<String> parseStringToList(String s){
        
        ArrayList<String> returning = new ArrayList<String>();
        
        // In-case there is no backslash
        s += "\n";
        
        String[] temp = s.split("\n");
        
        int numPara = 0;
        
        for (int i = 0; i < temp.length; i++) {
            if (!temp[i].trim().equals("")) {
                returning.add(s.split("\n")[i]);
                numPara++;
            }
        }
        
        
        totalParagraphs = numPara;
        return returning;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getName() {
        return name;
    }
    
    public String getNameAsString() {
        return name + "";
    }

    public void setName(int name) {
        this.name = name;
    }

    public ArrayList<String> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(ArrayList<String> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public int getNumberOfParagraphsForIndex() {
        return numberOfParagraphsForIndex;
    }

    public int getTotalParagraphs() {
        return totalParagraphs;
    }
    
    public String getAuthor() {
        return author;
    }
    
    // Getters and Setters
    
    
}










