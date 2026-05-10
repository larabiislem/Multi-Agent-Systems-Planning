package auction;

/**
 * Message constants and ontology terms.
 */
public final class AuctionMessages {
    private AuctionMessages() {}

    public static final String AUCTION_ITEM = "Laptop Pro 2026";
    public static final String CFP = "CFP";           // Call for proposal
    public static final String PROPOSE = "PROPOSE";   // Bid
    public static final String INFORM = "INFORM";     // Highest bid info
    public static final String ACCEPT = "ACCEPT";     // Winner accepted
    public static final String REJECT = "REJECT";     // Rejected bids
    public static final String END = "END";           // Auction ended
}
