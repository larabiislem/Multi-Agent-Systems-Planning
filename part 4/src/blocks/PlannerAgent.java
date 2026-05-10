package blocks;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.Optional;

/**
 * Centralized planner agent that computes a plan and sends it to the coordinator.
 */
public final class PlannerAgent extends Agent {
    @Override
    protected void setup() {
        Object[] args = getArguments();
        PlanningProblem problem = (PlanningProblem) args[0];
        String coordinatorName = (String) args[1];

        BlocksWorldLogger.info(getLocalName(), "Planning started.");
        Planner planner = new Planner();
        Optional<Plan> plan = planner.computePlan(problem);

        ACLMessage msg = new ACLMessage(plan.isPresent() ? ACLMessage.INFORM : ACLMessage.FAILURE);
        msg.addReceiver(new AID(coordinatorName, AID.ISLOCALNAME));
        msg.setConversationId(AgentMessages.PLAN);

        if (plan.isPresent()) {
            try {
                msg.setContentObject(plan.get());
                BlocksWorldLogger.info(getLocalName(), "Plan found with " + plan.get().getSteps().size() + " steps.");
            } catch (IOException e) {
                msg.setPerformative(ACLMessage.FAILURE);
                msg.setContent("Failed to serialize plan: " + e.getMessage());
            }
        } else {
            msg.setContent("No plan found.");
            BlocksWorldLogger.info(getLocalName(), "No plan found.");
        }

        send(msg);
    }
}
