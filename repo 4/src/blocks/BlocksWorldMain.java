package blocks;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.SwingUtilities;

/**
 * Bootstraps JADE runtime and launches the blocks world planning demo.
 */
public final class BlocksWorldMain {
    public static void main(String[] args) {
        List<String> blocks = Arrays.asList("A", "B", "C");

        State initialState = State.of(
                Predicate.handEmpty(),
                Predicate.onTable("C"),
                Predicate.onTable("B"),
                Predicate.on("A", "B"),
                Predicate.clear("C"),
                Predicate.clear("A")
        );

        Set<Predicate> goalPredicates = new LinkedHashSet<>();
        goalPredicates.add(Predicate.handEmpty());
        goalPredicates.add(Predicate.onTable("A"));
        goalPredicates.add(Predicate.on("B", "A"));
        goalPredicates.add(Predicate.on("C", "B"));
        goalPredicates.add(Predicate.clear("C"));

        PlanningProblem problem = new PlanningProblem(initialState, goalPredicates, blocks);
        WorldModel model = new WorldModel(blocks, initialState, goalPredicates);

        BlocksWorldFrame frame = createFrame(model, goalPredicates);

        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "true");
        ContainerController container = runtime.createMainContainer(profile);

        try {
            container.createNewAgent("coordinator", ExecutionCoordinatorAgent.class.getName(),
                    new Object[]{model, frame}).start();
            container.createNewAgent("planner", PlannerAgent.class.getName(),
                    new Object[]{problem, "coordinator"}).start();

            for (String block : blocks) {
                String agentName = "block-" + block.toLowerCase();
                container.createNewAgent(agentName, BlockAgent.class.getName(),
                        new Object[]{model, block}).start();
            }
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    private static BlocksWorldFrame createFrame(WorldModel model, Set<Predicate> goalPredicates) {
        AtomicReference<BlocksWorldFrame> frameRef = new AtomicReference<>();
        try {
            SwingUtilities.invokeAndWait(() -> {
                BlocksWorldFrame frame = new BlocksWorldFrame(model, goalPredicates);
                frame.setVisible(true);
                frameRef.set(frame);
            });
        } catch (Exception e) {
            throw new IllegalStateException("Failed to start GUI", e);
        }
        return frameRef.get();
    }
}
