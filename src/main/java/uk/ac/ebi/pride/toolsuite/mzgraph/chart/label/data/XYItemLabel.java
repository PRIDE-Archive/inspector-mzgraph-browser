package uk.ac.ebi.pride.toolsuite.mzgraph.chart.label.data;

/**
 * 
 * User: rwang
 * Date: 21-Jun-2010
 * Time: 14:59:54
 */
public class XYItemLabel implements ItemLabel {
    private String label;
    private String subscript;
    private String superscript;

    public XYItemLabel(String label, String subscript, String superscript) {
        this.label = label;
        this.subscript = subscript;
        this.superscript = superscript;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSubscript() {
        return subscript;
    }

    public void setSubscript(String subscript) {
        this.subscript = subscript;
    }

    public String getSuperscript() {
        return superscript;
    }

    public void setSuperscript(String superscript) {
        this.superscript = superscript;
    }
}
