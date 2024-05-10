package cc.artisan;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

public class Renovate {
    public static BrigadierCommand createBrigadierCommand(final ProxyServer server, final Logger logger, final Artisan plugin) {
        // create node for the command
        LiteralCommandNode<CommandSource> node = BrigadierCommand.literalArgumentBuilder("renovate")
            // check if we should renovate all plugins
            .then(BrigadierCommand.requiredArgumentBuilder("all", StringArgumentType.word())
                // execute the command
                .executes(context -> {
                    // get config
                    File configFile = new File("/home/container/plugins/artisan/config.yml");
                    Map<String,String> config;
                    try {
                        config = new Yaml().load(new FileInputStream(configFile));
                    } catch (FileNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                    logger.atInfo().log("Booting up proxy system renovation, file downloads incoming...");
                    Manager.renovate(config, server, logger, plugin);
                    return Command.SINGLE_SUCCESS;
                })
            )
            .build();
        return new BrigadierCommand(node);
    }
}