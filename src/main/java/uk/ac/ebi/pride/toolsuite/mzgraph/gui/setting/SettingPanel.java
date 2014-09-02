package uk.ac.ebi.pride.toolsuite.mzgraph.gui.setting;

import uk.ac.ebi.pride.toolsuite.gui.EDTUtils;
import uk.ac.ebi.pride.toolsuite.gui.action.ActionCascadePanel;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.MzGraphConstants;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.SpectrumPanel;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.SpectrumPanelModel;
import uk.ac.ebi.pride.utilities.util.NumberUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Panel configure the parameters for visualizing spectrum
 * <p/>
 * User: rwang
 * Date: 05/09/2011
 * Time: 11:24
 */
public class SettingPanel extends ActionCascadePanel implements PropertyChangeListener {
    public static final String APPLY_CHANGE_ACTION = "Apply";

    private SpectrumPanel spectrum;
    private JCheckBox showMassDiffCheckBox;
    private JTextField massErrorTextField;

    public SettingPanel(SpectrumPanel spectrum) {
        this.spectrum = spectrum;
        this.setLayout(new BorderLayout());
        addComponents();
    }

    private void addComponents() {


        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setOpaque(false);

        // constraints
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.BOTH;

        // show mass difference checkbox
        JPanel massDiffPanel = new JPanel(new BorderLayout());
        massDiffPanel.setOpaque(false);
        JLabel massDiffLabel = new JLabel("<html><b>" + MzGraphConstants.MASS_DIFFERENT_NAME + "</b></html>");
        massDiffPanel.add(massDiffLabel, BorderLayout.NORTH);
        showMassDiffCheckBox = new JCheckBox(MzGraphConstants.SHOW_MASS_DIFFERENT_LABEL, false);
        massDiffPanel.add(showMassDiffCheckBox, BorderLayout.CENTER);
        controlPanel.add(massDiffPanel, constraints);

        // mass error tolerance
        constraints.gridy = 1;
        JPanel massErrorPanel = new JPanel(new BorderLayout());
        massErrorPanel.setOpaque(false);
        JLabel massErrorLabel = new JLabel("<html><b>" + MzGraphConstants.MASS_ERROR_TOLERANCE + "</b></html>");
        massErrorPanel.add(massErrorLabel, BorderLayout.NORTH);
        massErrorPanel.add(new JLabel("±"), BorderLayout.WEST);
        massErrorTextField = new JTextField(spectrum.getModel().getAminoAcidAnnotationMassError() + "");
        massErrorPanel.add(massErrorTextField, BorderLayout.CENTER);
        massErrorPanel.add(new JLabel(MzGraphConstants.DALTON), BorderLayout.EAST);
        controlPanel.add(massErrorPanel, constraints);

        // buttons
        constraints.gridy = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        JButton applyButton = new JButton(APPLY_CHANGE_ACTION);
        applyButton.setActionCommand(APPLY_CHANGE_ACTION);
        applyButton.addActionListener(this);
        buttonPanel.add(applyButton);
        controlPanel.add(buttonPanel, constraints);

        this.add(controlPanel, BorderLayout.NORTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(APPLY_CHANGE_ACTION)) {
            // mass difference
            super.actionPerformed(new ShowMassDifferentEvent(this, e.getID(), showMassDiffCheckBox.isSelected()));
            // mass error tolerance
            String massErrorToleranceStr = massErrorTextField.getText();
            if (NumberUtilities.isNumber(massErrorToleranceStr)) {
                massErrorTextField.setForeground(Color.black);
                super.actionPerformed(new MassErrorToleranceEvent(this, e.getID(), Double.parseDouble(massErrorToleranceStr)));
            } else {
                massErrorTextField.setForeground(Color.red);
                massErrorTextField.setText(massErrorTextField.getText() + " (Number Only)");
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtCmd = evt.getPropertyName();
        if (SpectrumPanelModel.NEW_PEAK_SERIES_PROP.equals(evtCmd)) {

            Runnable eventDispatcher = new Runnable() {
                public void run() {
                    SettingPanel.this.removeAll();
                }
            };
            EDTUtils.invokeLater(eventDispatcher);
        }
    }
}
