package me.oska.plugins.logger;

import com.google.gson.JsonObject;
import me.oska.minecraft.OskaRPG;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;

import javax.net.ssl.HttpsURLConnection;
import javax.sound.midi.Track;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class Logger {

    private static final String ENVIRONMENT = "development";
    private static final String DISCORD_WEBHOOK = "https://discordapp.com/api/webhooks/753560505255460894/x1Px1wNlKslV1TVnEf5fV-AD60Aj8xxfMq7He2A6mSsBEsFJVejmdGF_jV3kPiKfICeL";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final TimeZone MALAYSIA_TIMEZONE = TimeZone.getTimeZone("Asia/Kuala_Lumpur");

    private String title;
    private File file;

    public Logger(String title) {
        this.title = title;

        file = new File(OskaRPG.getLoggerFolder(), title + ".log");
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private List<String> build(String message, Exception exception) {
        List<String> messages = new ArrayList(
                Arrays.asList(
                        "------------- ------------- -------------",
                        "Environment: " + ENVIRONMENT,
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
            toConsole(message, e);
            toDiscord(message, e);
            toFile(message, e);
        }
    }

    public void toConsole(String message, Exception exception) {
        new Thread(() -> build(message, exception).forEach(Bukkit.getLogger()::info)).start();
    }

    public void toDiscord(String message, Exception exception)  {
        new Thread(
                ()->{
                    try {
                        URL url = new URL(DISCORD_WEBHOOK);
                        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
                        connection.addRequestProperty("Content-Type", "application/json");
                        connection.addRequestProperty("User-Agent", "OskaRPG");
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);

                        JsonObject json = new JsonObject();
                        json.addProperty("content", String.join("\n", build(message, exception)));
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
    }

    public void toFile(String message, Exception exception) {
        new Thread(
                () -> {
                    try (FileWriter writer = new FileWriter(file)) {
                        for (String msg : build(message, exception)) {
                            writer.write(msg + "\n");
                        }
                        writer.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        ).start();
    }

}
