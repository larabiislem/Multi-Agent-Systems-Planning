package blocks;

import java.time.LocalTime;

/**
 * Simple console logger with timestamps.
 */
public final class BlocksWorldLogger {
    private BlocksWorldLogger() {}

    public static void info(String agent, String message) {
        System.out.printf("[%s] [%s] %s%n", LocalTime.now(), agent, message);
    }
}
