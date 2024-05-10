package cc.artisan;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

@Plugin(
    id = "artisan",
    name = "Artisan",
    version = BuildConstants.VERSION,
    url = "https://crystalcaverns.net",
    authors = {
        "japicraft"
    }
)
public class Artisan {
    private final ProxyServer server; // instance of the Velocity proxy
    private final Logger logger; // instance of the Velocity logger
    @Inject
    public Artisan(ProxyServer server, Logger logger) {
        // get the server and logger instances from the Velocity proxy
        this.server = server;
        this.logger = logger;
    }
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent e) {
        // prepare config
        File config = new File("/home/container/plugins/artisan/config.yml");
        if (!config.exists()) {
            config.getParentFile().mkdirs();
            try {
                config.createNewFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        // prepare cache
        File cache = new File("/home/container/plugins/.renovated/");
        if (!cache.exists()) {
            cache.mkdirs();
        }
        // register the main command
        server.getCommandManager().register(Renovate.createBrigadierCommand(logger));
        // welcome message
        logger.atInfo().log("Utility Artisan protocol running, ready to update the proxy to a new version.");
        // yay, we're up and running!
    }
}
