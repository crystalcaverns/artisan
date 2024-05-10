package cc.artisan;

import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

public class Manager {
    public static void renovate(Map<String,String> config, ProxyServer server, Logger logger, Artisan plugin) {
        // start renovating plugins
        server.getScheduler().buildTask(plugin, () -> {
            // renovate all plugins
            for (String name : config.keySet()) {
                // get the URL from config
                String rawURL = config.get(name);
                if (rawURL == null) {
                    logger.atError().log("Cannot renovate \"" + name + "\" - no URL found in entry.");
                    break;
                }
                // check if the URL is from Modrinth
                if (rawURL.startsWith("[redirect]")) {
                    // get the redirect URL
                    String redirectURL = rawURL.substring(10);
                    // get the origin URL
                    rawURL = Redirector.redirect(redirectURL);
                    // check if origin URL is valid
                    if (rawURL == null) {
                        logger.atError().log("Cannot renovate \"" + name + "\" - the API URL is invalid.");
                        break;
                    }
                }
                // prepare the origin URL
                URL origin;
                // create the origin URL
                try {
                    origin = URI.create(rawURL).toURL();
                } catch (MalformedURLException e) {
                    logger.atError().log("Cannot renovate \"" + name + "\" - the URL is malformed.");
                    break;
                }
                // download the file
                Downloader.download(origin, name, logger);
            }
            logger.atInfo().log("All plugins have been renovated successfully!");
            // we're done here
        }).schedule();
    }
}