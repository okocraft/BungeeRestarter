package net.okocraft.bungeerestarter;

import com.github.siroshun09.configapi.api.util.ResourceUtils;
import com.github.siroshun09.configapi.yaml.YamlConfiguration;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

public class BungeeRestarterPlugin extends Plugin {

    private final YamlConfiguration config = YamlConfiguration.create(getDataFolder().toPath().resolve("config.yml"));

    @Override
    public void onEnable() {
        try {
            Files.createDirectories(config.getPath().getParent());
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not create plugin directory.", e);
        }

        try {
            ResourceUtils.copyFromClassLoaderIfNotExists(getClass().getClassLoader(), "config.yml", config.getPath());
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save default config.yml", e);
            return;
        }

        try {
            config.load();
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not load config.yml", e);
            return;
        }

        getProxy().getPluginManager().registerCommand(this, new RestartCommand(this));
    }

    @Override
    public void onDisable() {
        getProxy().getPluginManager().unregisterCommands(this);
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}
