package cc.artisan;

import org.slf4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Downloader {
    public static void download(URL origin, String name, Logger logger) {
        // prepare streams
        BufferedInputStream in = null;
        FileOutputStream out = null;
        // prepare destination
        File destination = new File("/home/container/plugins/.renovated/" + name + ".jar");
        // start downloading
        try {
            // open the connection
            HttpURLConnection connection = (HttpURLConnection) origin.openConnection();
            // set the user agent
            connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            // initialize streams
            in = new BufferedInputStream(connection.getInputStream());
            out = new FileOutputStream(destination);
            // prepare variables
            byte[] bytes = new byte[1024];
            int counter;
            // download the file
            while((counter = in.read(bytes,0,1024)) != -1) {
                out.write(bytes,0,counter);
            }
            // close the connection
            connection.disconnect();
            // we're done!
            logger.atInfo().log("Renovated \"" + name + "\"!");
        } catch (Exception e) {
            logger.atError().log("Cannot renovate \"" + name + "\" - could not download.",e);
        } finally {
            // close the in stream
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                logger.atError().log("Cannot close IN stream.",e);
            }
            // close the out stream
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                logger.atError().log("Cannot close OUT stream.",e);
            }
        }
        // we're done here
    }
}