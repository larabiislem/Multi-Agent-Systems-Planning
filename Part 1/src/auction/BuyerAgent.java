package auction;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Random;

/**
 * Buyer behavior that bids above the current ask until budget is reached.
 */
public class BuyerAgent extends Agent {

    private final Random random = new Random();
    private int budget;

    @Override
    protected void setup() {
        budget = 220 + random.nextInt(420); // 220..639
        AuctionLogger.info(getLocalName(), "Ready with budget=" + budget);

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = receive();
                if (message == null) {
                    block();
                    return;
                }

                String conversationId = message.getConversationId();

                if (AuctionMessages.CONV_CFP.equals(conversationId)) {
                    handleCallForProposal(message);
                } else if (AuctionMessages.CONV_HIGHEST.equals(conversationId)) {
                    AuctionLogger.info(getLocalName(), "Observed highest bid update: " + message.getContent());
                } else if (AuctionMessages.CONV_WIN.equals(conversationId)) {
                    AuctionLogger.info(getLocalName(), "I won the auction! " + message.getContent());
                } else if (AuctionMessages.CONV_END.equals(conversationId)) {
                    AuctionLogger.info(getLocalName(), "Auction closed: " + message.getContent());
                    doDelete();
                }
            }
        });
    }

    private void handleCallForProposal(ACLMessage cfp) {
        int askPrice;
        try {
            askPrice = Integer.parseInt(cfp.getContent().trim());
        } catch (NumberFormatException e) {
            AuctionLogger.info(getLocalName(), "Received invalid ask price. Ignoring round.");
            return;
        }

        if (askPrice >= budget) {
            AuctionLogger.info(getLocalName(), "Stops bidding (ask " + askPrice + " >= budget " + budget + ")");
            return;
        }

        int increment = 10 + random.nextInt(41); // 10..50
        int bid = Math.min(askPrice + increment, budget);

        ACLMessage bidMessage = cfp.createReply();
        bidMessage.setPerformative(ACLMessage.PROPOSE);
        bidMessage.setConversationId(AuctionMessages.CONV_BID);
        bidMessage.setContent(String.valueOf(bid));
        send(bidMessage);

        AuctionLogger.info(getLocalName(), "Bids " + bid + " (ask " + askPrice + ")");
    }
}
