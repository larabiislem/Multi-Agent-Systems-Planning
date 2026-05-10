package blocks;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.swing.JPanel;

/**
 * Panel that renders the blocks world state.
 */
public final class BlocksWorldPanel extends JPanel {
    private static final int TABLE_MARGIN = 30;
    private static final int TABLE_HEIGHT = 40;

    private final WorldModel model;
    private final Map<String, Color> blockColors;

    public BlocksWorldPanel(WorldModel model) {
        this.model = model;
        this.blockColors = buildPalette(model.getBlocks());
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int tableY = height - TABLE_HEIGHT;

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(TABLE_MARGIN, tableY, width - TABLE_MARGIN * 2, 6);

        List<List<String>> stacks = model.getStacks();
        int stackCount = Math.max(1, stacks.size());
        int gap = (width - TABLE_MARGIN * 2) / stackCount;
        int blockSize = Math.min(70, (height - TABLE_HEIGHT - 40) / Math.max(1, model.getBlocks().size()));

        for (int i = 0; i < stacks.size(); i++) {
            List<String> stack = stacks.get(i);
            int centerX = TABLE_MARGIN + gap * i + gap / 2;
            int x = centerX - blockSize / 2;
            int y = tableY - blockSize;
            for (String block : stack) {
                drawBlock(g2, block, x, y, blockSize);
                y -= blockSize;
            }
        }

        Optional<String> held = model.getHeldBlock();
        if (held.isPresent()) {
            int x = width - TABLE_MARGIN - blockSize;
            int y = 40;
            drawBlock(g2, held.get(), x, y, blockSize);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString("hand", x + 5, y - 8);
        }
    }

    private void drawBlock(Graphics2D g2, String block, int x, int y, int size) {
        Color color = blockColors.getOrDefault(block, Color.LIGHT_GRAY);
        g2.setColor(color);
        g2.fillRoundRect(x, y, size, size, 8, 8);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(x, y, size, size, 8, 8);
        FontMetrics metrics = g2.getFontMetrics();
        int textWidth = metrics.stringWidth(block);
        int textX = x + (size - textWidth) / 2;
        int textY = y + (size + metrics.getAscent()) / 2 - 4;
        g2.drawString(block, textX, textY);
    }

    private Map<String, Color> buildPalette(List<String> blocks) {
        Color[] palette = new Color[]{
                new Color(122, 198, 225),
                new Color(170, 201, 125),
                new Color(255, 201, 107),
                new Color(239, 132, 132),
                new Color(179, 154, 219),
                new Color(248, 182, 210)
        };
        Map<String, Color> colors = new HashMap<>();
        for (int i = 0; i < blocks.size(); i++) {
            colors.put(blocks.get(i), palette[i % palette.length]);
        }
        return colors;
    }
}
