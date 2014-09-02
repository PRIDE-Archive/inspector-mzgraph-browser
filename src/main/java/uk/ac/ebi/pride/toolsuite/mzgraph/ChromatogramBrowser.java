package uk.ac.ebi.pride.toolsuite.mzgraph;

import uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.ChromatogramPanel;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 15-Aug-2010
 * Time: 16:43:15
 */
public class ChromatogramBrowser extends MzGraphBrowser{

    private final ChromatogramPanel chroma;

    public ChromatogramBrowser() {
        chroma = new ChromatogramPanel();
        addComponents();
    }

    private void addComponents() {
        // set spectrum as the main component
        sidePane.setMainComponent(chroma);
        // add standard tool kits.
        addStandardToolKits();
    }

    public void setGraphData(double[] time, double[] intensity) {
        chroma.setGraphData(time, intensity);
    }

    /**
     * Get chromatogram panel
     *
     * @return ChromatogramPanel    chromatogram panel
     */
    public ChromatogramPanel getChromatogramPanel() {
        return chroma;
    }

    /**
     * Get the source of the spectrum
     *
     * @return  Source name
     */
    public String getSource() {
        return chroma.getSource();
    }

    /**
     * Set the source of the spectrum
     * @param source    source name
     */
    public void setSource(String source) {
        chroma.setSource(source);
    }

    /**
     * Get the id of the spectrum
     * @return  id  id of the spectrum
     */
    public Comparable getId() {
        return chroma.getId();
    }

    public void setId(Comparable id) {
        chroma.setId(id);
    }
}
