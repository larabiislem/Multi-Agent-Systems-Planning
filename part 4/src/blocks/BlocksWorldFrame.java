package blocks;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Swing frame for visualizing the blocks world and logs.
 */
public final class BlocksWorldFrame extends JFrame implements PropertyChangeListener {
    private final BlocksWorldPanel panel;
    private final JTextArea logArea;
    private final JTextArea goalArea;

    public BlocksWorldFrame(WorldModel model, Set<Predicate> goalPredicates) {
        super("Blocks World - Centralized Planning");
        this.panel = new BlocksWorldPanel(model);
        this.logArea = new JTextArea();
        this.goalArea = new JTextArea(formatGoal(goalPredicates));

        logArea.setEditable(false);
        goalArea.setEditable(false);
        goalArea.setBackground(new Color(247, 247, 247));
        goalArea.setBorder(BorderFactory.createTitledBorder("Goal"));
        logArea.setBorder(BorderFactory.createTitledBorder("Execution Log"));

        setLayout(new BorderLayout(12, 12));
        add(goalArea, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        add(new JScrollPane(logArea), BorderLayout.SOUTH);

        panel.setPreferredSize(new Dimension(900, 480));
        logArea.setRows(8);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        model.addChangeListener(this);
    }

    public void log(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(panel::repaint);
    }

    private String formatGoal(Set<Predicate> goalPredicates) {
        return goalPredicates.stream()
                .map(Predicate::toString)
                .sorted()
                .collect(Collectors.joining(", "));
    }
}
