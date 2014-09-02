package uk.ac.ebi.pride.toolsuite.mzgraph;

import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.SideToolBarPanel;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.SpectrumPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 15-Aug-2010
 * Time: 12:22:54
 */
public abstract class MzGraphBrowser extends JPanel {

    public static enum ToolbarCommand {
        GRID("Grid", "Show grid", SpectrumPanel.GRID_LINE_COMMAND),
        SAVE("Save", "Save as image", SpectrumPanel.SAVE_AS),
        PRINT("Print", "Print as image", SpectrumPanel.PRINT_COMMAND),
        EXPORT("Export", "Export to text file", SpectrumPanel.EXPORT),
        ZOOM_OUT("Zoom out", "Zoom out", SpectrumPanel.ZOOM_OUT),
        ANNOTATION("Annotation", "Show annotations", "Annotation"),
        MASS_DIFF("MassDiff", "Clear mass differences", SpectrumPanel.CLEAR_MASS_DIFF),
        PEAK_LIST("Peaks", "Hide peaks lists", SpectrumPanel.HIDE_PEAK_LIST),
        SETTING("Settings", "Change settings", "Settings");

        private final String label;
        private final String tooltip;
        private final String actionCommand;

        private ToolbarCommand(String label, String tooltip, String actionCommand) {
            this.label = label;
            this.tooltip = tooltip;
            this.actionCommand = actionCommand;
        }

        public String getLabel() {
            return label;
        }

        public String getTooltip() {
            return tooltip;
        }

        public String getActionCommand() {
            return actionCommand;
        }
    }

    protected SideToolBarPanel sidePane;

    public MzGraphBrowser() {
        this.sidePane = new SideToolBarPanel();
        this.setLayout(new BorderLayout());
        this.add(sidePane, BorderLayout.CENTER);
    }

    /**
     * Get the source of the mzgraph
     *
     * @return  Source name
     */
    public abstract String getSource();

    /**
     * Set the source of the mzgraph
     * @param source    source name
     */
    public abstract void setSource(String source);

    /**
     * Get the id of the mzgraph
     * @return  id  id of the mzgraph
     */
    public abstract Comparable getId();

    /**
     * Set the id of the mzgraph
     * @param id    id of the mzgraph
     */
    public abstract void setId(Comparable id);

    public SideToolBarPanel getSidePane() {
        return sidePane;
    }

    protected void addStandardToolKits() {
        // add standard tool kits

        // save
        Icon saveIcon = GUIUtilities.loadIcon("icon/16x16/save.gif");
        sidePane.addCommand(saveIcon, null,
                MzGraphBrowser.ToolbarCommand.SAVE.getTooltip(), MzGraphBrowser.ToolbarCommand.SAVE.getActionCommand(), false);
        // print
        Icon printIcon = GUIUtilities.loadIcon("icon/16x16/print.gif");
        sidePane.addCommand(printIcon, null,
                MzGraphBrowser.ToolbarCommand.PRINT.getTooltip(), MzGraphBrowser.ToolbarCommand.PRINT.getActionCommand(), false);
        // export
        Icon exportIcon = GUIUtilities.loadIcon("icon/16x16/export_enable.gif");
        sidePane.addCommand(exportIcon, null,
                MzGraphBrowser.ToolbarCommand.EXPORT.getTooltip(), MzGraphBrowser.ToolbarCommand.EXPORT.getActionCommand(), false);
        // grid
        Icon gridIcon = GUIUtilities.loadIcon("icon/16x16/grid.gif");
        sidePane.addCommand(gridIcon, null,
                MzGraphBrowser.ToolbarCommand.GRID.getTooltip(), MzGraphBrowser.ToolbarCommand.GRID.getActionCommand(), true);
        // zoom out
        Icon zoomOutIcon = GUIUtilities.loadIcon("icon/16x16/zoom_out.png");
        sidePane.addCommand(zoomOutIcon, null,
                MzGraphBrowser.ToolbarCommand.ZOOM_OUT.getTooltip(), MzGraphBrowser.ToolbarCommand.ZOOM_OUT.getActionCommand(), false);

    }

    /**
     * Enabled the save button
     *
     * @param isEnabled true means enabled
     */
    public void enableSave(boolean isEnabled) {
        sidePane.enableAction(MzGraphBrowser.ToolbarCommand.SAVE.getActionCommand(), isEnabled);
    }

    /**
     * Invoke save action
     */
    public void invokeSave() {
        sidePane.invokeAction(MzGraphBrowser.ToolbarCommand.SAVE.getActionCommand());
    }

    /**
     * Enabled the print button
     *
     * @param isEnabled true means enabled
     */
    public void enablePrint(boolean isEnabled) {
        sidePane.enableAction(MzGraphBrowser.ToolbarCommand.PRINT.getActionCommand(), isEnabled);
    }

    /**
     * Invoke the print action
     */
    public void invokePrint() {
        sidePane.invokeAction(MzGraphBrowser.ToolbarCommand.PRINT.getActionCommand());
    }

    /**
     * Enable the export button
     * @param isEnabled true means enabled
     */
    public void enableExport(boolean isEnabled) {
        sidePane.enableAction(MzGraphBrowser.ToolbarCommand.EXPORT.getActionCommand(), isEnabled);
    }

    /**
     * Invoke the export action
     */
    public void invokeExport() {
        sidePane.invokeAction(MzGraphBrowser.ToolbarCommand.EXPORT.getActionCommand());
    }

    /**
     * Enable the grid button
     * @param isEnabled true means enabled
     */
    public void enableGrid(boolean isEnabled) {
        sidePane.enableAction(MzGraphBrowser.ToolbarCommand.GRID.getActionCommand(), isEnabled);
    }

    /**
     * Invoke the grid action
     */
    public void invokeGrid() {
        sidePane.invokeAction(MzGraphBrowser.ToolbarCommand.GRID.getActionCommand());
    }

    /**
     * Enable the zoom out button
     * @param isEnabled true mean enabled
     */
    public void enableZoomOut(boolean isEnabled) {
        sidePane.enableAction(MzGraphBrowser.ToolbarCommand.ZOOM_OUT.getActionCommand(), isEnabled);
    }
}

