package uk.ac.ebi.pride.toolsuite.mzgraph.gui.filter;

import uk.ac.ebi.pride.toolsuite.gui.EDTUtils;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.action.ActionCascadePanel;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.MzGraphConstants;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.SpectrumPanel;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.SpectrumPanelModel;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.setting.MassErrorToleranceEvent;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.setting.SettingPanel;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.setting.ShowMassDifferentEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

/**
 * AnnotationControlPanel is used to set the visibility of the amino acid and ion series annotations
 * <p/>
 * <p/>
 * User: rwang
 * Date: 15-Aug-2010
 * Time: 12:45:22
 */
public class AnnotationControlPanel extends ActionCascadePanel implements PropertyChangeListener {
    private SpectrumPanel spectrum;

    private SettingPanel settingPanel;

    public AnnotationControlPanel(SpectrumPanel spectrum) {
        this.spectrum = spectrum;
        spectrum.getModel().addPropertyChangeListener(this);
        this.setBorder(BorderFactory.createEmptyBorder());
        addComponents();
    }

    private void addComponents() {
        // remove all existing components first.
        this.removeAll();
        // add ion filters.
        Map<Comparable, Boolean> ionVisibilities = spectrum.getFragmentIonVisibilities();
        if (!ionVisibilities.isEmpty()) {
            CheckBoxFilterPanel fragPanel = new CheckBoxFilterPanel(MzGraphConstants.FRAGMENT_ION_NAME, ionVisibilities);
            this.add(fragPanel);
        }
        // add amino acid series filter
        Map<Comparable, Boolean> aminoAcidVisibilities = spectrum.getAminoAcidVisibilities();
        if (!aminoAcidVisibilities.isEmpty()) {
            CheckBoxFilterPanel aminoPanel = new CheckBoxFilterPanel(MzGraphConstants.AMINO_ACID_NAME, aminoAcidVisibilities);

            // add settings
            JPanel settingPanel = createSettingPane();
            aminoPanel.add(settingPanel);
            this.add(aminoPanel);
        }

        this.revalidate();
        this.repaint();
    }

    /**
     * Create setting panel
     *
     * @return JPanel  setting panel
     */
    private JPanel createSettingPane() {
        JPanel settingPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        Icon settingIcon = GUIUtilities.loadIcon("icon/16x16/setting.png");
        JButton settingButton = GUIUtilities.createLabelLikeButton(settingIcon, "Config");
        settingButton.setForeground(Color.blue);
        settingButton.addActionListener(new SettingActionListener());
        settingPanel.add(settingButton);
        return settingPanel;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtCmd = evt.getPropertyName();
        if (SpectrumPanelModel.NEW_PEAK_SERIES_PROP.equals(evtCmd)) {

            Runnable eventDispatcher = new Runnable() {
                public void run() {
                    AnnotationControlPanel.this.removeAll();
                }
            };
            EDTUtils.invokeLater(eventDispatcher);
        } else if (SpectrumPanelModel.NEW_ION_SERIES_PROP.equals(evtCmd) ||
                SpectrumPanelModel.AMINO_ACID_ANNOTATION_GEN_PROP.equals(evtCmd) ||
                SpectrumPanelModel.AMINO_ACID_ANNOTATION_GEN_PARAM_CHANGE.equals(evtCmd)) {

            Runnable eventDispatcher = new Runnable() {
                public void run() {
                    addComponents();
                }
            };
            EDTUtils.invokeLater(eventDispatcher);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e instanceof ShowMassDifferentEvent || e instanceof MassErrorToleranceEvent) {
            addComponents();
        }
        super.actionPerformed(e);
    }

    /**
     * Action listener to trigger settings
     */
    private class SettingActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // remove all the components
            AnnotationControlPanel.this.removeAll();

            // show setting panel
            if (settingPanel == null) {
                settingPanel = new SettingPanel(spectrum);
            }
            AnnotationControlPanel.this.add(settingPanel);
            settingPanel.addActionListener(AnnotationControlPanel.this);

            AnnotationControlPanel.this.revalidate();
            AnnotationControlPanel.this.repaint();
        }
    }
}
