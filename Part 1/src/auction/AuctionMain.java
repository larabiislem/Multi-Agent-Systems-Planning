package auction;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

/**
 * Entry point that starts the JADE platform and the auction agents.
 */
public final class AuctionMain {

    private AuctionMain() {
    }

    public static void main(String[] args) {
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "true");

        ContainerController mainContainer = runtime.createMainContainer(profile);

        try {
            mainContainer.createNewAgent("seller", SellerAgent.class.getName(), null).start();
            mainContainer.createNewAgent("buyer1", BuyerAgent.class.getName(), null).start();
            mainContainer.createNewAgent("buyer2", BuyerAgent.class.getName(), null).start();
            mainContainer.createNewAgent("buyer3", BuyerAgent.class.getName(), null).start();
        } catch (StaleProxyException e) {
            System.err.println("Failed to launch agents: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
