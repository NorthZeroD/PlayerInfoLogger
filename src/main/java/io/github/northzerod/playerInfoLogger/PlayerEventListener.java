package io.github.northzerod.playerInfoLogger;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class PlayerEventListener implements Listener {

    private final DatabaseManager db;

    public PlayerEventListener(DatabaseManager db) {
        this.db = db;
    }

    private String getNowFormatted() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now());
    }

    private long getNowStamp() {
        return System.currentTimeMillis();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        String username = player.getName();
        String time = getNowFormatted();
        long stamp = getNowStamp();

        if (!db.existsInFirstJoin(uuid)) {
            db.insertInto("first_join", uuid, username, time, stamp);
        }

        db.updateTable("last_join", uuid, username, time, stamp);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        String username = player.getName();
        String time = getNowFormatted();
        long stamp = getNowStamp();

        db.updateTable("last_quit", uuid, username, time, stamp);
    }
}