package blocks;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.util.List;
import java.util.UUID;

/**
 * Coordinates distributed execution of a centralized plan.
 */
public final class ExecutionCoordinatorAgent extends Agent {
    private static final long STEP_DELAY_MS = 900L;

    private WorldModel model;
    private BlocksWorldFrame frame;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        model = (WorldModel) args[0];
        frame = (BlocksWorldFrame) args[1];

        BlocksWorldLogger.info(getLocalName(), "Waiting for plan from planner.");

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchConversationId(AgentMessages.PLAN));
                if (msg == null) {
                    block();
                    return;
                }

                if (msg.getPerformative() != ACLMessage.INFORM) {
                    log("Planner failed: " + msg.getContent());
                    return;
                }

                try {
                    Plan plan = (Plan) msg.getContentObject();
                    if (plan.isEmpty()) {
                        log("No actions to execute.");
                        return;
                    }
                    log("Executing plan with " + plan.getSteps().size() + " steps.");
                    addBehaviour(new PlanExecutionBehaviour(plan.getSteps()));
                } catch (UnreadableException e) {
                    log("Failed to read plan: " + e.getMessage());
                }
            }
        });
    }

    private void log(String message) {
        BlocksWorldLogger.info(getLocalName(), message);
        if (frame != null) {
            frame.log(message);
        }
    }

    private String agentNameFor(String block) {
        return "block-" + block.toLowerCase();
    }

    private final class PlanExecutionBehaviour extends Behaviour {
        private final List<Action> steps;
        private int index;
        private boolean waiting;
        private boolean done;
        private String pendingReply;
        private long lastDispatch;

        private PlanExecutionBehaviour(List<Action> steps) {
            this.steps = steps;
        }

        @Override
        public void action() {
            if (done) {
                return;
            }
            if (waiting) {
                MessageTemplate template = MessageTemplate.and(
                        MessageTemplate.MatchConversationId(AgentMessages.ACK),
                        MessageTemplate.MatchInReplyTo(pendingReply)
                );
                ACLMessage ack = myAgent.receive(template);
                if (ack == null) {
                    block();
                    return;
                }
                waiting = false;
                if (ack.getPerformative() == ACLMessage.INFORM) {
                    Action finished = steps.get(index);
                    log("Step " + (index + 1) + " completed: " + finished);
                    if (model.isGoalReached()) {
                        log("Goal reached.");
                    }
                    index++;
                    if (index >= steps.size()) {
                        log("Plan execution complete.");
                        done = true;
                    }
                } else {
                    log("Step failed: " + ack.getContent());
                    done = true;
                }
                return;
            }

            if (index >= steps.size()) {
                done = true;
                return;
            }

            long now = System.currentTimeMillis();
            if (now - lastDispatch < STEP_DELAY_MS) {
                block(STEP_DELAY_MS - (now - lastDispatch));
                return;
            }

            Action action = steps.get(index);
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            request.addReceiver(new AID(agentNameFor(action.getBlock()), AID.ISLOCALNAME));
            request.setConversationId(AgentMessages.STEP);
            pendingReply = UUID.randomUUID().toString();
            request.setReplyWith(pendingReply);
            try {
                request.setContentObject(action);
            } catch (Exception e) {
                log("Failed to send action: " + e.getMessage());
                done = true;
                return;
            }
            log("Dispatching step " + (index + 1) + ": " + action);
            send(request);
            waiting = true;
            lastDispatch = now;
        }

        @Override
        public boolean done() {
            return done;
        }
    }
}
