package auction;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Random;

import static auction.AuctionMessages.*;

/**
 * Buyer agent: bids randomly above current price.
 */
public class BuyerAgent extends Agent {

    private final Random random = new Random();
    private int maxBudget;

    @Override
    protected void setup() {
        maxBudget = 200 + random.nextInt(400); // hardcoded random budget
        AuctionLogger.info(getLocalName(), "Buyer ready. Budget=" + maxBudget);

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg == null) {
                    block();
                    return;
                }

                if (msg.getPerformative() == ACLMessage.CFP) {
                    int current = Integer.parseInt(msg.getContent().split(":")[1]);
                    if (current < maxBudget) {
                        int bid = current + 10 + random.nextInt(40);
                        if (bid > maxBudget) bid = maxBudget;

                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.PROPOSE);
                        reply.setConversationId(PROPOSE);
                        reply.setContent(String.valueOf(bid));
                        send(reply);

                        AuctionLogger.info(getLocalName(), "Bid " + bid);
                    } else {
                        AuctionLogger.info(getLocalName(), "Stops bidding (price too high).");
                    }
                }

                if (ACCEPT.equals(msg.getConversationId())) {
                    AuctionLogger.info(getLocalName(), "I WON! " + msg.getContent());
                }

                if (END.equals(msg.getConversationId())) {
                    AuctionLogger.info(getLocalName(), "Auction ended.");
                }
            }
        });
    }
}
