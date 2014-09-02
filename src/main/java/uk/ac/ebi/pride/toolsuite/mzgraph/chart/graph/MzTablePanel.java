package uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYBoxAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.XYPlot;
import uk.ac.ebi.pride.utilities.iongen.model.ProductIon;
import uk.ac.ebi.pride.utilities.mol.ProductIonPair;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.renderer.TheoreticalFragmentedIonsRenderer;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.ExperimentalFragmentedIonsScatterChartPanel;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.ExperimentalFragmentedIonsTable;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.ExperimentalFragmentedIonsDataset;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.ExperimentalParams;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.List;

/**
 * This is a ExperimentalTableModelObserver, which maintain the mapping between
 * table model Cell (row, column) and chart panel XYSeries (series, item).
 * When user click one cell, the chart panel point will highlight. On the contrary,
 * when user click a point, the table cell background color will display grey.
 * <p/>
 * Creator: Qingwei-XU
 * Date: 15/10/12
 */

public class MzTablePanel extends JPanel implements PropertyChangeListener {
    private ExperimentalFragmentedIonsScatterChartPanel scatterChartPanel;
    private ExperimentalParams params = ExperimentalParams.getInstance();

    private JScrollPane tablePanel;
    private ChartPanel chartPanel;
    private JPanel toolbar;

    //    private ExperimentalFragmentedIonsTableModel tableModel;
    private ExperimentalFragmentedIonsTable table;

    /**
     * MzTablePanel have initial or not.
     */
//    private boolean initial = false;

    /**
     * whether calculate auto annotations or not.
     */
//    private boolean calculate = true;

    private JCheckBox waterChecker = new JCheckBox("Show H2O Neutral Loss");
    private JCheckBox ammoniaChecker = new JCheckBox("Show NH3 Neutral Loss");
    private JLabel ionPairLabel;
    private JComboBox ionPairChooser;
    private JLabel rangeLabel;
    private JSlider rangeSlider;
    private JToggleButton helpButton;

    private void flushPanel() {
        if (table.hasManualAnnotations()) {
            // have manual annotations.
            table.setShowAuto(false);
            waterChecker.setVisible(false);
            ammoniaChecker.setVisible(false);
            ionPairLabel.setVisible(true);
            ionPairChooser.setVisible(true);
            ionPairChooser.setEnabled(false);
            rangeLabel.setVisible(false);
            rangeSlider.setVisible(false);
            tablePanel.setVisible(true);
            chartPanel.setVisible(true);
        } else if (!table.isCalculate()) {
            // no manual annotations, and not calculate auto annotations too!
            table.setShowAuto(true);
            waterChecker.setVisible(false);
            ammoniaChecker.setVisible(false);
            ionPairLabel.setVisible(false);
            ionPairChooser.setVisible(false);
            rangeLabel.setVisible(false);
            rangeSlider.setVisible(false);
            tablePanel.setVisible(false);
            chartPanel.setVisible(false);
        } else {
            // no manual annotations, but have calculated auto annotations.
            table.setShowAuto(true);
            waterChecker.setVisible(true);
            ammoniaChecker.setVisible(true);
            ionPairLabel.setVisible(true);
            ionPairChooser.setVisible(true);
            rangeLabel.setVisible(true);
            rangeSlider.setVisible(true);
            tablePanel.setVisible(true);
            chartPanel.setVisible(true);
        }
    }

    private void init(final ExperimentalFragmentedIonsTable table) {
        int height = 35;
        this.table = table;
        tablePanel = new JScrollPane(table);
//        initial = true;

        ExperimentalFragmentedIonsTableModel tableModel = (ExperimentalFragmentedIonsTableModel) table.getModel();
        ExperimentalFragmentedIonsDataset dataset = new ExperimentalFragmentedIonsDataset(tableModel);
        scatterChartPanel = new ExperimentalFragmentedIonsScatterChartPanel(dataset);
        chartPanel = scatterChartPanel.getChartPanel();
        table.addPropertyChangeListener(scatterChartPanel);
        table.addPropertyChangeListener(this);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(scatterChartPanel, BorderLayout.EAST);
        contentPane.add(tablePanel, BorderLayout.CENTER);

        waterChecker.setSelected(params.isShowWaterLoss());
        waterChecker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox checker = (JCheckBox) e.getSource();
                table.setShowWaterLoss(checker.isSelected());
            }
        });

        ammoniaChecker.setSelected(params.isShowAmmoniaLoss());
        ammoniaChecker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox checker = (JCheckBox) e.getSource();
                table.setShowAmmoniaLoss(checker.isSelected());
            }
        });

        ionPairLabel = new JLabel("           Choose Ion Type: ");
        ionPairChooser = new JComboBox();
        ionPairChooser.addItem(ProductIonPair.B_Y);
        ionPairChooser.addItem(ProductIonPair.A_X);
        ionPairChooser.addItem(ProductIonPair.C_Z);

        int ionPairIndex;
        switch (params.getIonPair()) {
            case B_Y:
                ionPairIndex = 0;
                break;
            case A_X:
                ionPairIndex = 1;
                break;
            case C_Z:
                ionPairIndex = 2;
                break;
            default:
                ionPairIndex = 0;
        }

        ionPairChooser.setSelectedIndex(ionPairIndex);
        ionPairChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox chooser = (JComboBox) e.getSource();
                ProductIonPair ionPair = (ProductIonPair) chooser.getSelectedItem();
                table.setProductIonPair(ionPair);
            }
        });

        rangeLabel = new JLabel("            Tolerance(Da):");
        rangeSlider = new JSlider(
                JSlider.HORIZONTAL,
                1,        // minimum range is 0.1 Da
                10,        // maximum range is 1 Da
                (int) (params.getRange() * 10)         // default range is 0.5 Da
        );
        rangeSlider.setMinorTickSpacing(1);
        rangeSlider.setMajorTickSpacing(1);
        rangeSlider.setPaintLabels(true);
        rangeSlider.setPaintTicks(false);
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        JLabel unitLabel;
        for (int i = 1; i <= 9; i++) {
            unitLabel = new JLabel("0." + i);
            unitLabel.setFont(new Font(unitLabel.getFont().getFontName(), unitLabel.getFont().getStyle(), unitLabel.getFont().getSize() - 4));
            labelTable.put(i, unitLabel);
        }
        unitLabel = new JLabel("1.0");
        unitLabel.setFont(new Font(unitLabel.getFont().getFontName(), unitLabel.getFont().getStyle(), unitLabel.getFont().getSize() - 4));
        labelTable.put(10, unitLabel);
        rangeSlider.setLabelTable(labelTable);
        rangeSlider.setPreferredSize(new Dimension(300, height));
        rangeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    double newRange = source.getValue() / 10d;
                    table.setRange(newRange);
                    source.setToolTipText(newRange + "Da");
                }
            }
        });

        helpButton = new JToggleButton();
        helpButton.setFocusable(false);
        helpButton.setOpaque(false);
        helpButton.setBorderPainted(false);

        toolbar = new JPanel(null);
        toolbar.setPreferredSize(new Dimension(1000, height));
        toolbar.add(waterChecker);
        toolbar.add(ammoniaChecker);
        toolbar.add(ionPairLabel);
        toolbar.add(ionPairChooser);
        toolbar.add(rangeLabel);
        toolbar.add(rangeSlider);
        toolbar.add(helpButton);

        // absolute layout
        Insets insets = toolbar.getInsets();
        int x_offset = 20 + insets.left;
        int y_offset = 5 + insets.top;

        Dimension size = waterChecker.getPreferredSize();
        waterChecker.setBounds(x_offset, y_offset, size.width, size.height);
        x_offset += size.width + 5;

        size = ammoniaChecker.getPreferredSize();
        ammoniaChecker.setBounds(x_offset, y_offset, size.width, size.height);
        x_offset += size.width + 30;

        size = ionPairLabel.getPreferredSize();
        ionPairLabel.setBounds(x_offset, y_offset + 5, size.width, size.height);
        x_offset += size.width + 5;

        ionPairChooser.setPreferredSize(new Dimension(70, ionPairLabel.getHeight()));
        size = ionPairChooser.getPreferredSize();
        ionPairChooser.setBounds(x_offset, y_offset + 5, size.width, size.height);
        x_offset += size.width + 30;

        size = rangeLabel.getPreferredSize();
        rangeLabel.setBounds(x_offset, y_offset + 5, size.width, size.height);
        x_offset += size.width + 5;

        size = rangeSlider.getPreferredSize();
        rangeSlider.setBounds(x_offset, y_offset - 2, size.width, size.height);
        x_offset += size.width + 90;

        size = helpButton.getPreferredSize();
        helpButton.setBounds(x_offset, y_offset + 2, size.width, 25);
        helpButton.setVisible(false);

        table.flush();
        flushPanel();

        setLayout(new BorderLayout());
        add(toolbar, BorderLayout.NORTH);
        add(contentPane, BorderLayout.CENTER);


        addTableAction(table, chartPanel);
        addChartAction(table, chartPanel);
    }

    public MzTablePanel() {

    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    public JScrollPane getTablePanel() {
        return tablePanel;
    }

    public ExperimentalFragmentedIonsTable getTable() {
        return table;
    }

    public JPanel getToolbar() {
        return toolbar;
    }

    public boolean isCalculate() {
        return this.table.isCalculate();
    }

    public boolean isShowAuto() {
        return this.table.isShowAuto();
    }

    /**
     * This button default is invisible. User can add some Action in it.
     */
    public JToggleButton getHelpButton() {
        return helpButton;
    }

    public void setTable(ExperimentalFragmentedIonsTable table) {
        if (this.table != null) {
            for (PropertyChangeListener propertyChangeListener : this.table.getPropertyChangeListeners()) {
                this.table.removePropertyChangeListener(propertyChangeListener);
            }
        }

        removeAll();
        init(table);
    }

    public void addAllManualAnnotations(List<IonAnnotation> ionAnnotationList) {
        this.table.addAllManualAnnotations(ionAnnotationList);

        flushPanel();
    }
//

    /**
     * whether calculate auto annotations, or not.
     */
    public void setCalculate(boolean calculate) {
        if (this.table != null) {
            this.table.setCalculate(calculate);
            flushPanel();
        }
    }

    public void setPeaks(double[] mzArray, double[] intensityArray) {
        this.table.setPeaks(mzArray, intensityArray);
    }

    /**
     * If there are matched data in the clicked cell, highlight the corresponding point in the chart.
     */
    private void addTableAction(ExperimentalFragmentedIonsTable table, final ChartPanel chartPanel) {
        final ExperimentalFragmentedIonsTableModel tableModel = (ExperimentalFragmentedIonsTableModel) table.getModel();
        final ExperimentalFragmentedIonsDataset dataset = (ExperimentalFragmentedIonsDataset) chartPanel.getChart().getXYPlot().getDataset();

        JFreeChart chart = chartPanel.getChart();
        final XYPlot plot = (XYPlot) chart.getPlot();

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ExperimentalFragmentedIonsTable target = (ExperimentalFragmentedIonsTable) e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();

                Object[][] matchedData = tableModel.getMatchedData();
                int item;
                int series;

                if (matchedData[row][column] != null) {
                    ProductIon ion = (ProductIon) tableModel.getValueAt(row, column);

                    item = dataset.getItemNumber(row, column);
                    series = dataset.getSeriesNumber(row, column);

                    double x = dataset.getXValue(series, item);
                    double y = dataset.getYValue(series, item);

                    Dimension size = chartPanel.getPreferredSize();

                    plot.clearAnnotations();
                    NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
                    double ySize = yAxis.getRange().getLength() / 50;
                    NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
                    double xSize = xAxis.getRange().getLength() / 50;

                    XYBoxAnnotation boxAnnotation = new XYBoxAnnotation(x - xSize, y - ySize, x + xSize, y + ySize, new BasicStroke(0.0f), Color.green, Color.green);

                    NumberFormat formatter = NumberFormat.getInstance();
                    formatter.setMaximumFractionDigits(3);

                    String msg = ion.getType() + "(" + formatter.format(x) + ", " + formatter.format(y) + ")";

                    XYTextAnnotation textAnnotation;
                    if (y - ySize * 2 < 0) {
                        textAnnotation = new XYTextAnnotation(msg, x - xSize * 2, y + ySize * 2);
                    } else {
                        textAnnotation = new XYTextAnnotation(msg, x - xSize * 2, y - ySize * 2);
                    }

                    plot.addAnnotation(boxAnnotation);
                    plot.addAnnotation(textAnnotation);
                } else {
                    plot.clearAnnotations();
                }
            }
        });
    }

    /**
     * If click one point in the chart, highlight corresponding table cell.
     */
    private void addChartAction(final ExperimentalFragmentedIonsTable table, ChartPanel chartPanel) {
        final ExperimentalFragmentedIonsDataset dataset = (ExperimentalFragmentedIonsDataset) chartPanel.getChart().getXYPlot().getDataset();

        chartPanel.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent chartMouseEvent) {
                ChartEntity entity = chartMouseEvent.getEntity();

                XYItemEntity itemEntity;
                int item;
                int series;
                int row;
                int col;
                if (entity instanceof XYItemEntity) {
                    itemEntity = (XYItemEntity) entity;
                    item = itemEntity.getItem();
                    series = itemEntity.getSeriesIndex();

                    row = dataset.getRowNumber(series, item);
                    col = dataset.getColNumber(series, item);

                    if (row != -1 && col != -1) {
                        TheoreticalFragmentedIonsRenderer cellRenderer = (TheoreticalFragmentedIonsRenderer) table.getCellRenderer(row, col);
                        cellRenderer.setHighlight(row, col);
                        table.revalidate();
                        table.repaint();
                    }
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent chartMouseEvent) {

            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ExperimentalFragmentedIonsTable.FLUSH_TABLEMODEL)) {
            firePropertyChange(ExperimentalFragmentedIonsTable.FLUSH_TABLEMODEL, evt.getOldValue(), evt.getNewValue());
        }
    }
}
