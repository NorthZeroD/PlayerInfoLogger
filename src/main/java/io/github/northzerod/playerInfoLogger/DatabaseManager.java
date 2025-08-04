package io.github.northzerod.playerInfoLogger;

import org.bukkit.plugin.java.JavaPlugin;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.DefaultConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.jooq.impl.DSL.*;

public class DatabaseManager {

    private final JavaPlugin plugin;
    private Connection connection;
    private DSLContext dsl;

    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/data.db");
            dsl = DSL.using(new DefaultConfiguration()
                    .set(connection)
                    .set(SQLDialect.SQLITE));

            createTableIfNotExists("first_join");
            createTableIfNotExists("last_join");
            createTableIfNotExists("last_quit");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists(String tableName) {
        dsl.createTableIfNotExists(table(tableName))
                .column("uuid", SQLDataType.VARCHAR(36).nullable(false))
                .column("username", SQLDataType.VARCHAR(32).nullable(false))
                .column("time", SQLDataType.VARCHAR(32).nullable(false))
                .column("stamp", SQLDataType.BIGINT.nullable(false))
                .constraints(constraint("pk_" + tableName).primaryKey("uuid"))
                .execute();
    }

    public boolean existsInFirstJoin(String uuid) {
        return dsl.fetchExists(
                selectOne().from(table("first_join")).where(field("uuid").eq(uuid))
        );
    }

    public void insertInto(String tableName, String uuid, String username, String time, long stamp) {
        dsl.insertInto(table(tableName))
                .columns(field("uuid"), field("username"), field("time"), field("stamp"))
                .values(uuid, username, time, stamp)
                .onConflictDoNothing()
                .execute();
    }

    public void updateTable(String tableName, String uuid, String username, String time, long stamp) {
        dsl.insertInto(table(tableName))
                .columns(field("uuid"), field("username"), field("time"), field("stamp"))
                .values(uuid, username, time, stamp)
                .onConflict(field("uuid"))
                .doUpdate()
                .set(field("username"), username)
                .set(field("time"), time)
                .set(field("stamp"), stamp)
                .execute();
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}