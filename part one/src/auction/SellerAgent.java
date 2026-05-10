package auction;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static auction.AuctionMessages.*;

/**
 * Seller agent: runs an English auction.
 */
public class SellerAgent extends Agent {

    // Hardcoded parameters
    private static final int START_PRICE = 100;
    private static final int RESERVE_PRICE = 300;
    private static final int TIMEOUT_ROUNDS = 5;

    private int currentPrice = START_PRICE;
    private int round = 0;

    private final List<AID> buyers = new ArrayList<>();
    private final Map<AID, Integer> lastBids = new HashMap<>();

    @Override
    protected void setup() {
        AuctionLogger.info(getLocalName(), "Seller ready. Item: " + AUCTION_ITEM);
        discoverBuyers();
        addBehaviour(new AuctionCycle(this, 2000)); // 2s per round
    }

    private void discoverBuyers() {
        buyers.add(new AID("buyer1", AID.ISLOCALNAME));
        buyers.add(new AID("buyer2", AID.ISLOCALNAME));
        buyers.add(new AID("buyer3", AID.ISLOCALNAME));
    }

    private class AuctionCycle extends TickerBehaviour {
        public AuctionCycle(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            round++;
            AuctionLogger.info(getLocalName(), "Round " + round + " starting. Current price: " + currentPrice);

            // 1) Send CFP to all buyers
            ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
            cfp.setContent(CFP + ":" + currentPrice);
            for (AID b : buyers) cfp.addReceiver(b);
            send(cfp);

            // 2) Collect bids (simple blocking loop)
            lastBids.clear();
            long deadline = System.currentTimeMillis() + 1500; // 1.5s window
            while (System.currentTimeMillis() < deadline) {
                ACLMessage msg = receive();
                if (msg != null && PROPOSE.equals(msg.getConversationId())) {
                    int bid = Integer.parseInt(msg.getContent());
                    lastBids.put(msg.getSender(), bid);
                    AuctionLogger.info(getLocalName(), "Received bid " + bid + " from " + msg.getSender().getLocalName());
                } else {
                    block(200);
                }
            }

            if (lastBids.isEmpty() || round >= TIMEOUT_ROUNDS) {
                endAuction();
                stop();
                return;
            }

            // 3) Determine highest bid
            AID winner = null;
            int highest = currentPrice;
            for (Map.Entry<AID, Integer> entry : lastBids.entrySet()) {
                if (entry.getValue() > highest) {
                    highest = entry.getValue();
                    winner = entry.getKey();
                }
            }

            currentPrice = highest;

            // 4) Inform buyers about highest bid
            ACLMessage info = new ACLMessage(ACLMessage.INFORM);
            info.setContent(INFORM + ":" + currentPrice);
            for (AID b : buyers) info.addReceiver(b);
            send(info);

            AuctionLogger.info(getLocalName(), "Highest bid this round: " + currentPrice);
        }

        private void endAuction() {
            AuctionLogger.info(getLocalName(), "Auction ended. Final price: " + currentPrice);

            if (currentPrice >= RESERVE_PRICE) {
                // Winner is the highest bidder in the last round
                AID winner = lastBids.entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse(null);

                if (winner != null) {
                    ACLMessage accept = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                    accept.setConversationId(ACCEPT);
                    accept.addReceiver(winner);
                    accept.setContent("Winner: " + winner.getLocalName() + " price=" + currentPrice);
                    send(accept);

                    AuctionLogger.info(getLocalName(), "Sold to " + winner.getLocalName() + " for " + currentPrice);
                }
            } else {
                AuctionLogger.info(getLocalName(), "Reserve price not met. No sale.");
            }

            ACLMessage end = new ACLMessage(ACLMessage.INFORM);
            end.setConversationId(END);
            for (AID b : buyers) end.addReceiver(b);
            end.setContent("Auction finished. Final price=" + currentPrice);
            send(end);
        }
    }
}
