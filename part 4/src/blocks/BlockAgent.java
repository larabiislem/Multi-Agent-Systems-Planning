package blocks;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

/**
 * Agent responsible for executing actions that move a specific block.
 */
public final class BlockAgent extends Agent {
    private WorldModel model;
    private String blockName;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        model = (WorldModel) args[0];
        blockName = (String) args[1];

        BlocksWorldLogger.info(getLocalName(), "Ready for block " + blockName + " actions.");

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchConversationId(AgentMessages.STEP));
                if (msg == null) {
                    block();
                    return;
                }

                ACLMessage reply = msg.createReply();
                reply.setConversationId(AgentMessages.ACK);

                try {
                    Action action = (Action) msg.getContentObject();
                    if (!blockName.equalsIgnoreCase(action.getBlock())) {
                        reply.setPerformative(ACLMessage.FAILURE);
                        reply.setContent("Wrong block agent for " + action);
                    } else {
                        model.apply(action);
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent("done");
                    }
                } catch (UnreadableException | RuntimeException ex) {
                    reply.setPerformative(ACLMessage.FAILURE);
                    reply.setContent(ex.getMessage());
                }

                send(reply);
            }
        });
    }
}
