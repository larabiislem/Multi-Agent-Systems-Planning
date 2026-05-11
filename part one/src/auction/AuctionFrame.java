package auction;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Swing frame for visualizing the auction and logs.
 */
public final class AuctionFrame extends JFrame implements PropertyChangeListener {
    private final AuctionPanel panel;
    private final JTextArea logArea;
    private final JTextArea summaryArea;
    private final AuctionModel model;

    public AuctionFrame(AuctionModel model) {
        super("Auction - English Auction");
        this.model = model;
        this.panel = new AuctionPanel(model);
        this.logArea = new JTextArea();
        this.summaryArea = new JTextArea();

        logArea.setEditable(false);
        summaryArea.setEditable(false);
        summaryArea.setBackground(new Color(247, 247, 247));
        summaryArea.setBorder(BorderFactory.createTitledBorder("Auction Status"));
        logArea.setBorder(BorderFactory.createTitledBorder("Execution Log"));

        setLayout(new BorderLayout(12, 12));
        add(summaryArea, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        add(new JScrollPane(logArea), BorderLayout.SOUTH);

        panel.setPreferredSize(new Dimension(900, 480));
        logArea.setRows(8);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        updateSummary(model.snapshot());
        model.addChangeListener(this);
    }

    public void log(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        AuctionModel.Snapshot snapshot = model.snapshot();
        SwingUtilities.invokeLater(() -> {
            updateSummary(snapshot);
            panel.repaint();
        });
    }

    private void updateSummary(AuctionModel.Snapshot snapshot) {
        StringBuilder summary = new StringBuilder();
        summary.append("Item: ").append(snapshot.getItem()).append("\n");
        summary.append("Round: ").append(snapshot.getRound()).append("\n");
        summary.append("Current price: ").append(snapshot.getCurrentPrice()).append("\n");
        if (snapshot.getStatus() != null) {
            summary.append("Status: ").append(snapshot.getStatus()).append("\n");
        }
        if (snapshot.getWinner() != null) {
            summary.append("Winner: ").append(snapshot.getWinner());
        }
        summaryArea.setText(summary.toString());
    }
}
