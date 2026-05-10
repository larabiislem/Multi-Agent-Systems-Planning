package auction;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Seller behavior for an English auction.
 */
public class SellerAgent extends Agent {

    // Hardcoded parameters required by the specification.
    private static final int START_PRICE = 100;
    private static final int RESERVE_PRICE = 300;
    private static final int MAX_ROUNDS = 5;
    private static final long ROUND_PERIOD_MS = 2500L;
    private static final long BID_WINDOW_MS = 1500L;

    private int currentPrice;
    private int currentRound;
    private final List<AID> buyers = new ArrayList<>();
    private final Map<AID, Integer> latestRoundBids = new HashMap<>();

    @Override
    protected void setup() {
        currentPrice = START_PRICE;
        currentRound = 0;

        buyers.add(new AID("buyer1", AID.ISLOCALNAME));
        buyers.add(new AID("buyer2", AID.ISLOCALNAME));
        buyers.add(new AID("buyer3", AID.ISLOCALNAME));

        AuctionLogger.info(getLocalName(), String.format(
                "Starting English auction for '%s' (start=%d, reserve=%d, maxRounds=%d)",
                AuctionMessages.ITEM_NAME, START_PRICE, RESERVE_PRICE, MAX_ROUNDS));

        addBehaviour(new AuctionRoundBehaviour(this, ROUND_PERIOD_MS));
    }

    private final class AuctionRoundBehaviour extends TickerBehaviour {

        private AuctionRoundBehaviour(Agent agent, long period) {
            super(agent, period);
        }

        @Override
        protected void onTick() {
            currentRound++;
            AuctionLogger.info(getLocalName(), "Round " + currentRound + " - asking price: " + currentPrice);

            sendCallForBids();
            collectRoundBids();

            if (latestRoundBids.isEmpty()) {
                AuctionLogger.info(getLocalName(), "No bids received in this round.");
                finalizeAuction(null, currentPrice);
                stop();
                return;
            }

            Map.Entry<AID, Integer> highestEntry = findHighestBid(latestRoundBids);
            AID highestBidder = highestEntry.getKey();
            int highestBid = highestEntry.getValue();

            currentPrice = highestBid;
            broadcastHighestBid(highestBid, highestBidder);

            if (currentRound >= MAX_ROUNDS) {
                AuctionLogger.info(getLocalName(), "Maximum rounds reached.");
                finalizeAuction(highestBidder, highestBid);
                stop();
            }
        }
    }

    private void sendCallForBids() {
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        cfp.setConversationId(AuctionMessages.CONV_CFP);
        cfp.setContent(currentPrice + "");
        for (AID buyer : buyers) {
            cfp.addReceiver(buyer);
        }
        send(cfp);
    }

    private void collectRoundBids() {
        latestRoundBids.clear();

        long deadline = System.currentTimeMillis() + BID_WINDOW_MS;
        MessageTemplate bidTemplate = MessageTemplate.MatchConversationId(AuctionMessages.CONV_BID);

        while (System.currentTimeMillis() < deadline) {
            ACLMessage bidMessage = receive(bidTemplate);
            if (bidMessage == null) {
                block(100);
                continue;
            }

            try {
                int bid = Integer.parseInt(bidMessage.getContent().trim());
                if (bid > currentPrice) {
                    latestRoundBids.put(bidMessage.getSender(), bid);
                    AuctionLogger.info(getLocalName(), "Bid " + bid + " from " + bidMessage.getSender().getLocalName());
                }
            } catch (NumberFormatException ignored) {
                AuctionLogger.info(getLocalName(), "Ignoring malformed bid from " + bidMessage.getSender().getLocalName());
            }
        }
    }

    private Map.Entry<AID, Integer> findHighestBid(Map<AID, Integer> bids) {
        Map.Entry<AID, Integer> highestEntry = null;
        for (Map.Entry<AID, Integer> entry : bids.entrySet()) {
            if (highestEntry == null || entry.getValue() > highestEntry.getValue()) {
                highestEntry = entry;
            }
        }
        return highestEntry;
    }

    private void broadcastHighestBid(int highestBid, AID bidder) {
        ACLMessage highestInfo = new ACLMessage(ACLMessage.INFORM);
        highestInfo.setConversationId(AuctionMessages.CONV_HIGHEST);
        highestInfo.setContent(highestBid + "|" + bidder.getLocalName());
        for (AID buyer : buyers) {
            highestInfo.addReceiver(buyer);
        }
        send(highestInfo);

        AuctionLogger.info(getLocalName(), "Highest bid now " + highestBid + " by " + bidder.getLocalName());
    }

    private void finalizeAuction(AID highestBidder, int finalPrice) {
        boolean sold = highestBidder != null && finalPrice >= RESERVE_PRICE;

        if (sold) {
            ACLMessage win = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
            win.setConversationId(AuctionMessages.CONV_WIN);
            win.addReceiver(highestBidder);
            win.setContent("Won with final price=" + finalPrice);
            send(win);

            AuctionLogger.info(getLocalName(), "Item sold to " + highestBidder.getLocalName() + " at " + finalPrice);
        } else {
            AuctionLogger.info(getLocalName(), "Reserve price not met. Item not sold.");
        }

        ACLMessage end = new ACLMessage(ACLMessage.INFORM);
        end.setConversationId(AuctionMessages.CONV_END);
        end.setContent("finalPrice=" + finalPrice + ", sold=" + sold);
        for (AID buyer : buyers) {
            end.addReceiver(buyer);
        }
        send(end);
    }
}
