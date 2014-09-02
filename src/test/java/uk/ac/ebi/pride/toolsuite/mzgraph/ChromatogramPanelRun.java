package uk.ac.ebi.pride.toolsuite.mzgraph;

import uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.ChromatogramPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 07-Jun-2010
 * Time: 12:09:51
 */
public class ChromatogramPanelRun {
    public static void main(String[] args) {
        Runnable runner = new Runnable() {
            public void run() {
                createGUI();
            }
        };

        EventQueue.invokeLater(runner);
    }

    public static void createGUI() {
        double[] xArr = new double[]{1.0, 2.012312313, 3.0};
        double[] yArr = new double[]{2.0, 4.345345345, 6.0};

        JFrame frame = new JFrame("Chromatogram Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ChromatogramPanel panel = new ChromatogramPanel(xArr, yArr);
        panel.paintGraph();
        frame.setContentPane(panel);
        frame.setSize(new Dimension(400, 400));
        frame.pack();
        frame.setVisible(true);
    }
}
