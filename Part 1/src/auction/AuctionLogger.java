package auction;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Lightweight console logger for consistent auction traces.
 */
public final class AuctionLogger {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private AuctionLogger() {
    }

    public static void info(String agentName, String message) {
        System.out.printf("[%s] [%s] %s%n", LocalTime.now().format(TIME_FORMAT), agentName, message);
    }
}
