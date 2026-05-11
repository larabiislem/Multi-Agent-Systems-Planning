package auction;

import java.time.LocalTime;
import java.util.function.Consumer;

public final class AuctionLogger {
    private AuctionLogger() {}

    private static volatile Consumer<String> logListener;

    public static void setLogListener(Consumer<String> listener) {
        logListener = listener;
    }

    public static void info(String agent, String msg) {
        String formatted = String.format("[%s] [%s] %s", LocalTime.now(), agent, msg);
        System.out.println(formatted);
        Consumer<String> listener = logListener;
        if (listener != null) {
            listener.accept(formatted);
        }
    }
}
