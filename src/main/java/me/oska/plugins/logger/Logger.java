package me.oska.plugins.logger;

import com.google.gson.JsonObject;
import me.oska.minecraft.OskaRPG;
import me.oska.plugins.hibernate.AbstractRepository;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class Logger {

    private static Object MUTEX;
    private static String DISCORD_WEBHOOK;
    private static SimpleDateFormat DATE_FORMAT;
    private static TimeZone MALAYSIA_TIMEZONE;
    private static AbstractRepository<Issue> repository;


    public static void register(JavaPlugin plugin) {
        MUTEX = new Object();
        DISCORD_WEBHOOK = "https://discordapp.com/api/webhooks/753560505255460894/x1Px1wNlKslV1TVnEf5fV-AD60Aj8xxfMq7He2A6mSsBEsFJVejmdGF_jV3kPiKfICeL";
        DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        MALAYSIA_TIMEZONE = TimeZone.getTimeZone("Asia/Kuala_Lumpur");
        repository = new AbstractRepository<>(Issue.class);
    }

    private String title;

    public Logger(String title) {
        this.title = title;
    }

    private String trackId() {
        return UUID.randomUUID().toString();
    }

    private List<String> build(String trackId, String message, Exception exception) {
        List<String> messages = new ArrayList(
                Arrays.asList(
                        "------------- ------------- -------------",
                        "Track ID : " + trackId,
                        "Title: " + title,
                        "Timestamp: " + DATE_FORMAT.format(Calendar.getInstance(MALAYSIA_TIMEZONE).getTime()),
                        "Message: " + message
                )
        );

        if (exception != null) {
            messages.add("Exception: "+ exception.getMessage());
            messages.add("Stack:\n" + ExceptionUtils.getStackTrace(exception).replaceAll(",","\n"));
        }
        messages.add("------------- ------------- -------------");
        return messages;
    }

    public void withTracker(String message, Tracker runnable) {
        try {
            runnable.track();
        } catch (Exception e) {
            final String id = trackId();
            toConsole(id, message, e);
            toDiscord(id, message, e);
            toDB(id, message, e);
        }
    }

    public String toConsole(String message) {
        return toConsole(null, message, null);
    }

    public String toConsole(String message, Exception exception) {
        return toConsole(null, message, exception);
    }

    public String toConsole(String id, String message, Exception exception) {
        if (id == null) {
            id = trackId();
        }
        String finalId = id;

        new Thread(() -> {
            synchronized (MUTEX) {
                build(finalId, message, exception).forEach(Bukkit.getLogger()::info);
            }
        }).start();
        return id;
    }

    public String toDiscord(String message)  {
        return toDiscord(null, message, null);
    }

    public String toDiscord(String message, Exception e)  {
        return toDiscord(null, message, e);
    }

    public String toDiscord(String id, String message, Exception exception)  {
        if (id == null) {
            id = trackId();
        }
        String finalId = id;

        new Thread(
                ()->{
                    try {
                        URL url = new URL(DISCORD_WEBHOOK);
                        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
                        connection.addRequestProperty("Content-Type", "application/json");
                        connection.addRequestProperty("User-Agent", "OskaRPG-Logger");
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);

                        JsonObject json = new JsonObject();
                        json.addProperty("content", String.join("\n", build(finalId, message, exception)));
                        OutputStream stream = connection.getOutputStream();
                        stream.write(json.toString().getBytes());
                        stream.close();
                        connection.getInputStream().close();
                        connection.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        ).start();
        return id;
    }

    public String toDB(String message)  {
        return toDB(null, message, null);
    }

    public String toDB(String message, Exception e)  {
        return toDB(null, message, e);
    }

    public String toDB(String id, String message, Exception exception) {
        return toDB(id, message, exception, "");
    }

    public String toDB(String id, String message, Exception exception, String playerUUID) {
        List<String> players = new ArrayList<>();
        if (!playerUUID.equals("")) {
            players.add(playerUUID);
        }
        return toDB(id, message,exception, players);
    }

    public String toDB(String id, String message, Exception exception, List<String> playerUUID) {
        Issue issue = new Issue(id);
        issue.setTitle(this.title);
        issue.setMessage(message);
        issue.setTimestamp(DATE_FORMAT.format(Calendar.getInstance(MALAYSIA_TIMEZONE).getTime()));
        if (playerUUID != null) {
            issue.setPlayers(playerUUID);
        }
        if (exception != null) {
            issue.setStack(ExceptionUtils.getStackTrace(exception));
        }
        repository.createAsync(issue, null);
        return id;
    }
}
