package uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation;

/**
 * RangeAnnotation describes an annotation between two peaks.
 *
 * User: rwang
 * Date: 08-Aug-2010
 * Time: 08:16:52
 */
public interface RangeAnnotation {
    /** get the start peak m/z value */
    public Number getStartMz();
    /** get the stop peak m/z value */
    public Number getStopMz();
    /** get the start intensity value */
    public Number getStartIntensity();
    /** get the stop intensity value */
    public Number getStopIntensity();
    /** get the detailed range annotation information */
    public RangeAnnotationInfo getRangeAnnotationInfo();
}
