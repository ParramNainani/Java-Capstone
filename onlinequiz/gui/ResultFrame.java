package gui;

import javax.swing.*;

/**
 * ResultFrame - GUI for displaying quiz results.
 */
public class ResultFrame extends JFrame {
    public ResultFrame() {
        setTitle("Result");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        // Add result display components here
        setVisible(true);
    }
}
