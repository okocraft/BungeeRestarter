package net.okocraft.bungeerestarter;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.IOException;
import java.util.logging.Level;

public class RestartCommand extends Command {

    private final BungeeRestarterPlugin plugin;

    public RestartCommand(BungeeRestarterPlugin plugin) {
        super("bungeerestart", null, "brestart");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (!commandSender.hasPermission("bungeerestarter.command")) {
            commandSender.sendMessage(TextComponent.fromLegacyText("You don't have the permission to restart proxy."));
            return;
        }

        var command = plugin.getConfig().getString("command-to-start-proxy");

        if (command.isEmpty()) {
            commandSender.sendMessage(
                    TextComponent.fromLegacyText("The command to start proxy is not set in config.yml")
            );
            return;
        }

        plugin.getProxy().getPlayers().forEach(this::kickPlayer);

        try {
            Thread.sleep(150);
        } catch (InterruptedException ignored) {
        }

        Runtime.getRuntime().addShutdownHook(createShutdownHook(command));

        plugin.getProxy().stop();
    }

    private Thread createShutdownHook(String command) {
        var shutdownHook =
                new Thread(() -> {
                    try {
                        Runtime.getRuntime().exec(command);
                    } catch (IOException e) {
                        plugin.getProxy().getLogger().log(
                                Level.SEVERE,
                                "Could not execute command \"" + command + "\"",
                                e
                        );
                    }
                });

        shutdownHook.setDaemon(true);

        return shutdownHook;
    }

    private void kickPlayer(ProxiedPlayer player) {
        var locale = player.getLocale();
        var kickMessageKey = "kick-message." + (locale != null ? locale.toString() : "default");
        var kickMessage = plugin.getConfig().getString(kickMessageKey);

        if (kickMessage.isEmpty()) {
            kickMessage = plugin.getConfig().getString("kick-message.default");
        }

        player.disconnect(TextComponent.fromLegacyText(kickMessage));
    }
}
