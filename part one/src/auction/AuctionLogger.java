package auction;

import java.time.LocalTime;

public final class AuctionLogger {
    private AuctionLogger() {}

    public static void info(String agent, String msg) {
        System.out.printf("[%s] [%s] %s%n", LocalTime.now(), agent, msg);
    }
}
