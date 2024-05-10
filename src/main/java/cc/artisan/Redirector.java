package cc.artisan;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class Redirector {
    public static String redirect(String redirectURL) {
        // prepare the contents
        String contents;
        try {
            // prepare the json url
            URL jsonOrigin = URI.create(redirectURL).toURL();
            // open the connection
            HttpURLConnection connection = (HttpURLConnection) jsonOrigin.openConnection();
            // set the user agent
            connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            // get contents of the json
            contents = new String(connection.getInputStream().readAllBytes());
            // close the connection
            connection.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // parse the json
        JsonElement json = JsonParser.parseString(contents);
        // get the origin indicator
        String indicator = redirectURL.substring(8,redirectURL.indexOf("/",8));
        // decide where the json is from
        return switch (indicator) {
            case "api.github.com":
                // get the url from GitHub
                yield json.getAsJsonArray().get(0).getAsJsonObject().get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
            case "api.modrinth.com":
                // get the url from Modrinth
                yield json.getAsJsonArray().get(0).getAsJsonObject().get("files").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString();
            case "ci.lucko.me":
                // get the url for LuckPerms
                switch (redirectURL.substring(24, redirectURL.indexOf("/", 24))) {
                    case "LuckPerms" -> {
                        //
                        String luckpermsPath = json.getAsJsonObject().get("artifacts").getAsJsonArray().get(7).getAsJsonObject().get("relativePath").getAsString();
                        yield "https://ci.lucko.me/job/LuckPerms/lastStableBuild/artifact/" + luckpermsPath;
                    }
                    case "spark" -> {
                        //
                        String sparkPath = json.getAsJsonObject().get("artifacts").getAsJsonArray().get(8).getAsJsonObject().get("relativePath").getAsString();
                        yield "https://ci.lucko.me/job/spark/lastStableBuild/artifact/" + sparkPath;
                    }
                }
            default:
                yield null;
        };
        // we're done here
    }
}