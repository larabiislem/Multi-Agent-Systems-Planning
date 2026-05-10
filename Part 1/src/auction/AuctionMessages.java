package auction;

/**
 * Shared constants for auction content and conversation identifiers.
 */
public final class AuctionMessages {

    private AuctionMessages() {
    }

    public static final String ITEM_NAME = "Product";

    public static final String CONV_CFP = "AUCTION_CFP";
    public static final String CONV_BID = "AUCTION_BID";
    public static final String CONV_HIGHEST = "AUCTION_HIGHEST";
    public static final String CONV_WIN = "AUCTION_WIN";
    public static final String CONV_END = "AUCTION_END";
}
