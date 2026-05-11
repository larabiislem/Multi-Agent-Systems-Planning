package auction;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JPanel;

/**
 * Panel that renders auction status and bids.
 */
public final class AuctionPanel extends JPanel {
    private static final int MARGIN = 30;
    private static final int HEADER_HEIGHT = 80;
    private static final int BAR_GAP = 12;

    private final AuctionModel model;
    private final Map<String, Color> bidderColors = new LinkedHashMap<>();

    public AuctionPanel(AuctionModel model) {
        this.model = model;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AuctionModel.Snapshot snapshot = model.snapshot();
        int width = getWidth();
        int height = getHeight();

        drawHeader(g2, snapshot, width);
        drawBids(g2, snapshot, width, height);
    }

    private void drawHeader(Graphics2D g2, AuctionModel.Snapshot snapshot, int width) {
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22f));
        String title = snapshot.getItem();
        FontMetrics metrics = g2.getFontMetrics();
        int titleWidth = metrics.stringWidth(title);
        g2.drawString(title, Math.max(MARGIN, (width - titleWidth) / 2), MARGIN + metrics.getAscent());

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 16f));
        String roundLabel = "Round: " + snapshot.getRound();
        g2.drawString(roundLabel, MARGIN, MARGIN + 40);

        String priceLabel = "Current price: " + snapshot.getCurrentPrice();
        int priceWidth = g2.getFontMetrics().stringWidth(priceLabel);
        g2.drawString(priceLabel, Math.max(MARGIN, width - MARGIN - priceWidth), MARGIN + 40);
    }

    private void drawBids(Graphics2D g2, AuctionModel.Snapshot snapshot, int width, int height) {
        Map<String, Integer> bids = snapshot.getLastBids();
        int top = HEADER_HEIGHT;

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 15f));
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("Latest bids", MARGIN, top);
        int labelHeight = g2.getFontMetrics().getHeight();
        int startY = top + labelHeight;
        int availableHeight = height - startY - MARGIN;

        if (bids.isEmpty()) {
            g2.setColor(Color.GRAY);
            g2.drawString("No bids yet.", MARGIN, top + 30);
            return;
        }

        int barCount = Math.max(1, bids.size());
        int barHeight = Math.max(24, (availableHeight - BAR_GAP * (barCount + 1)) / barCount);
        int y = startY + BAR_GAP;
        int maxBid = snapshot.getCurrentPrice();
        for (int bid : bids.values()) {
            maxBid = Math.max(maxBid, bid);
        }
        int scale = Math.max(1, maxBid);

        for (Map.Entry<String, Integer> entry : bids.entrySet()) {
            String bidder = entry.getKey();
            int bid = entry.getValue();
            Color color = colorFor(bidder);
            int barWidth = (int) ((width - MARGIN * 2) * (bid / (double) scale));

            g2.setColor(color);
            g2.fillRoundRect(MARGIN, y, barWidth, barHeight, 10, 10);
            g2.setColor(Color.DARK_GRAY);
            g2.drawRoundRect(MARGIN, y, barWidth, barHeight, 10, 10);

            String label = bidder + ": " + bid;
            g2.setColor(Color.BLACK);
            g2.drawString(label, MARGIN + 10, y + barHeight - 6);

            y += barHeight + BAR_GAP;
        }

        if (snapshot.getWinner() != null) {
            g2.setColor(new Color(52, 135, 72));
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
            g2.drawString("Winner: " + snapshot.getWinner(), MARGIN, height - MARGIN);
        }
    }

    private Color colorFor(String bidder) {
        if (bidderColors.containsKey(bidder)) {
            return bidderColors.get(bidder);
        }
        Color[] palette = new Color[]{
                new Color(122, 198, 225),
                new Color(170, 201, 125),
                new Color(255, 201, 107),
                new Color(239, 132, 132),
                new Color(179, 154, 219),
                new Color(248, 182, 210)
        };
        Color color = palette[bidderColors.size() % palette.length];
        bidderColors.put(bidder, color);
        return color;
    }
}
