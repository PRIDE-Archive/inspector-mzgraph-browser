package uk.ac.ebi.pride.toolsuite.mzgraph.chart.renderer;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 12-Aug-2010
 * Time: 23:01:33
 */
public class RendererConstants {
    private RendererConstants() {
    }

    public final static Paint DEFAULT_HIGHLIGHT_PAINT = Color.blue;
    public final static Shape DEFAULT_HIGHLIGHT_SHAPE = new RoundRectangle2D.Float(-2.0f, -2.0f, 4.0f, 4.0f, 2.0f, 2.0f);
    public final static double DEFAULT_ITEM_LABEL_ANCHOR_OFFSET = 2;
    public final static double DEFAULT_ADDITIONAL_ITEM_LABEL_ANCHOR_OFFSET = 10;

    /**
     * The margin between different range annotations.
     */
    public final static double DEFAULT_HEIGHT_MARGIN = 0.13;
    /**
     * The margin between the upper border and the chart content
     */
    public final static double DEFAULT_CHART_MARGIN = 0.2;
    public final static Stroke REGULAR_LINE_STROKE = new BasicStroke(0.5f);
    public final static Stroke DASH_LINE_STROKE = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{3}, 2);
    public final static double ARROW_WEIGHT = 2;
}
