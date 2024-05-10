package cc.artisan;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import static cc.artisan.Downloader.download;

public class Renovate {
    public static BrigadierCommand createBrigadierCommand(final Logger logger) {
        // create node for the command
        LiteralCommandNode<CommandSource> node = BrigadierCommand.literalArgumentBuilder("renovate")
            // check if we should renovate all plugins
            .then(BrigadierCommand.requiredArgumentBuilder("all", StringArgumentType.word())
                // execute the command
                .executes(context -> {
                    // get config
                    File configFile = new File("/home/container/plugins/artisan/config.yml");
                    Map<String, String> map;
                    try {
                        map = new Yaml().load(new FileInputStream(configFile));
                    } catch (FileNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                    // start renovating plugins
                    for (String name : map.keySet()) {
                        // get the URL from config
                        String url = map.get(name);
                        if (url == null) {
                            logger.atError().log("Cannot renovate \"" + name + "\" - no URL found in entry.");
                            break;
                        }
                        // prepare the origin
                        URL origin;
                        try {
                            origin = URI.create(url).toURL();
                        } catch (MalformedURLException e) {
                            logger.atError().log("Cannot renovate \"" + name + "\" - the URL is malformed.");
                            break;
                        }
                        // download the file
                        download(origin, name, logger);
                    }
                    logger.atInfo().log("All plugins have been renovated successfully!");
                    return Command.SINGLE_SUCCESS;
                })
            )
            .build();
        return new BrigadierCommand(node);
    }
}
