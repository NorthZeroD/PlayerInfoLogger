package io.github.northzerod.playerInfoLogger;

import org.bukkit.plugin.java.JavaPlugin;

public final class PlayerInfoLogger extends JavaPlugin {

    private DatabaseManager db;

    @Override
    public void onEnable() {
        System.setProperty("org.jooq.no-logo", "true");
        System.setProperty("org.jooq.no-tips", "true");

        saveDefaultConfig();
        db = new DatabaseManager(this);
        db.init();
        getServer().getPluginManager().registerEvents(new PlayerEventListener(db), this);
        this.getLogger().info("PlayerInfoLogger 插件已启用");
    }

    @Override
    public void onDisable() {
        db.close();
        this.getLogger().info("PlayerInfoLogger 插件已禁用");
    }
}
