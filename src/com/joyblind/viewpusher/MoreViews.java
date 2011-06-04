package com.joyblind.viewpusher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MoreViews {

    public static void main (String[] args) throws InterruptedException, IOException {
        
        int numberOfViews = 1000;
        

        for (int i= 0; i < numberOfViews; i++) {

            URL url = new URL("http://www.youtube.com/watch?v=tqKzy_a3NGU");
            url.openConnection();
            url.openStream();
            Thread.sleep(2500);
            
            System.out.println("We're at: " + i);
        }
    }
}
