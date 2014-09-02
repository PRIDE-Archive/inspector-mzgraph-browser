package uk.ac.ebi.pride.toolsuite.mzgraph.chart.label.data;

/**
 * ItemLabel is an interface to get label details
 * 
 * User: rwang
 * Date: 21-Jun-2010
 * Time: 14:58:45
 */
public interface ItemLabel {

    public String getLabel();

    public void setLabel(String label);

    public String getSubscript();

    public void setSubscript(String subscript);

    public String getSuperscript();

    public void setSuperscript(String superscript);
}
