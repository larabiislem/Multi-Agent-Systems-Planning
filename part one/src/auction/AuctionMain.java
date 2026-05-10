package auction;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

/**
 * Bootstraps JADE runtime and starts Seller + Buyers.
 */
public class AuctionMain {

    public static void main(String[] args) {
        Runtime rt = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "true"); // RMA + GUI tools
        ContainerController container = rt.createMainContainer(profile);

        try {
            // Seller
            container.createNewAgent("seller", SellerAgent.class.getName(), null).start();

            // Buyers
            container.createNewAgent("buyer1", BuyerAgent.class.getName(), new Object[]{"buyer1"}).start();
            container.createNewAgent("buyer2", BuyerAgent.class.getName(), new Object[]{"buyer2"}).start();
            container.createNewAgent("buyer3", BuyerAgent.class.getName(), new Object[]{"buyer3"}).start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
