package com.example.gambarucmsui.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringJoiner;
public class LoggingService {
    private static final LoggingService INSTANCE = new LoggingService();
    public static LoggingService getLogger() {
        return INSTANCE;
    }
    private LoggingService() {
    }

    //  CORE
    ///////////////////////////////////////////////////////////

    private final static Deque<Object> logQueue = new ArrayDeque<>();
    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final static int maxMessages=1000;

    public static void log(Object... mesages) {
        StringJoiner joiner = new StringJoiner(" ");
        for (Object mesage : mesages) {
            joiner.add(mesage.toString());
        }
        log(joiner.toString());
    }
    public static void log(Object message) {
        String formatMessage = formatMessage(message);
        System.out.println(formatMessage);
        // If the queue is full, remove the oldest message to make space for the new one
        if (logQueue.size() >= maxMessages) {
            logQueue.removeFirst();
        }

        // Add the new message to the end of the queue
        logQueue.addLast(formatMessage);
    }

    private static String formatMessage(Object message) {
        String formattedTimestamp = LocalDateTime.now().format(dateTimeFormatter);
        String formattedMessage = formattedTimestamp + " - " + message.toString();
        return formattedMessage;
    }

    public static String getLogs() {
        StringBuilder sb = new StringBuilder();
        for (Object message : logQueue) {
            sb.append(message).append("\n");
        }
        return sb.toString();
    }
}
