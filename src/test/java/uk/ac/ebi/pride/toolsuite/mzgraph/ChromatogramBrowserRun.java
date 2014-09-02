package uk.ac.ebi.pride.toolsuite.mzgraph;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 15-Aug-2010
 * Time: 16:48:31
 */
public class ChromatogramBrowserRun {
        public static void main(String[] args) {
        Runnable runner = new Runnable() {
            public void run() {
                createGUI();
            }
        };

        EventQueue.invokeLater(runner);
    }

    private static void createGUI() {
        double[] mzArr = new double[]{1.0, 2.012312313, 3.0, 4.234, 6.0, 7.34342};
        double[] intentArr = new double[]{0.05, 4.345345345, 6.0, 1.4545, 5.0, 8.23423};

        ChromatogramBrowser browser = new ChromatogramBrowser();
        browser.setGraphData(mzArr, intentArr);

        JFrame frame = new JFrame("Side Bar Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(browser, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}
