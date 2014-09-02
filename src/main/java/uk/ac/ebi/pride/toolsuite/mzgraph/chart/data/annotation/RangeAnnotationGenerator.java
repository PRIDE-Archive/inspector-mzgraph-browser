package uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation;

import java.util.List;

/**
 * RangeAnnotationGenerator is the interface for generating RangeAnnotation.
 *
 * User: rwang
 * Date: 11-Aug-2010
 * Time: 08:58:35
 */
public interface RangeAnnotationGenerator<T, V> {

    public T generate(V peaks);
}
