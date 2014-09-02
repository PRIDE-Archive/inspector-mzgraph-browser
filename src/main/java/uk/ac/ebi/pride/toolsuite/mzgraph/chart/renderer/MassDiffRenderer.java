package uk.ac.ebi.pride.toolsuite.mzgraph.chart.renderer;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.label.AminoAcidLabelGenerator;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.label.MassDiffItemLabelGenerator;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 10-Jun-2010
 * Time: 12:29:33
 */
public class MassDiffRenderer extends AbstractXYItemRenderer
        implements org.jfree.chart.renderer.xy.XYItemRenderer,
        java.lang.Cloneable,
        org.jfree.util.PublicCloneable,
        java.io.Serializable {
    private final double MASS_DIFF_HEIGHT_MARGIN = 0.1;
    private final Stroke REGULAR_LINE_STROKE = new BasicStroke(0.5f);
    private final Stroke DASH_LINE_STROKE = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{3}, 2);
    private final double ARROW_WEIGHT = 2;
    private XYItemLabelGenerator massItemLabelGenerator;
    private XYItemLabelGenerator aminoAcidItemLabelGenerator;

    public MassDiffRenderer() {
        super();
        this.massItemLabelGenerator = new MassDiffItemLabelGenerator();
        this.aminoAcidItemLabelGenerator = new AminoAcidLabelGenerator();
    }


    @Override
    public boolean isItemLabelVisible(int row, int column) {
        return true;
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2             the graphics device.
     * @param state          the renderer state.
     * @param dataArea       the area within which the plot is being drawn.
     * @param info           collects information about the drawing.
     * @param plot           the plot (can be used to obtain standard color
     *                       information etc).
     * @param domainAxis     the domain axis.
     * @param rangeAxis      the range axis.
     * @param dataset        the dataset.
     * @param series         the series index (zero-based).
     * @param item           the item index (zero-based).
     * @param crosshairState crosshair information for the plot
     *                       (<code>null</code> permitted).
     * @param pass           the pass index (ignored here).
     */
    public void drawItem(Graphics2D g2,
                         XYItemRendererState state,
                         Rectangle2D dataArea,
                         PlotRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset dataset,
                         int series,
                         int item,
                         CrosshairState crosshairState,
                         int pass) {
        if (!getItemVisible(series, item)) {
            return;
        }

        int itemCount = dataset.getItemCount(series);
        if (item < itemCount - 1) {
            EntityCollection entities = null;
            if (info != null) {
                entities = info.getOwner().getEntityCollection();
            }
            // get item values
            double x1 = dataset.getXValue(series, item);
            double y1 = dataset.getYValue(series, item);
            double x2 = dataset.getXValue(series, item + 1);
            double y2 = dataset.getYValue(series, item + 1);
            double yMax = DatasetUtilities.findMaximumRangeValue(plot.getDataset()).doubleValue();

            // get axis location
            RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge yAxisLocation = plot.getRangeAxisEdge();

            // get range axis upper margin
            Range range = rangeAxis.getRange();
            double massDiffLength = range.getLength() * MASS_DIFF_HEIGHT_MARGIN;
            yMax += massDiffLength;

            // get location in the plot
            double xx1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
            double yy1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
            double xx2 = domainAxis.valueToJava2D(x2, dataArea, xAxisLocation);
            double yy2 = rangeAxis.valueToJava2D(y2, dataArea, yAxisLocation);
            double yyMax = rangeAxis.valueToJava2D(yMax, dataArea, yAxisLocation);
            double yyLink = yyMax;
            //ToDo: order by x
            double xxLabel = xx1 + ((xx2 - xx1) / 2);
            double yyLabel = yyLink;

            // lines
            Line2D line1 = null;
            Line2D line2 = null;
            Line2D line3 = null;

            // Paint
            Paint p = getItemPaint(series, item);

            // arrow shapes
            GeneralPath leftArrow = new GeneralPath();
            GeneralPath rightArrow = new GeneralPath();

            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                line1 = new Line2D.Double(yy1, xx1, yyMax, xx1);
                line2 = new Line2D.Double(yy2, xx2, yyMax, xx2);
                line3 = new Line2D.Double(yyLink, xx1, yyLink, xx2);
                leftArrow.moveTo(yyLink, xx1);
                leftArrow.lineTo(yyLink + ARROW_WEIGHT, xx1 + ARROW_WEIGHT);
                leftArrow.lineTo(yyLink + ARROW_WEIGHT, xx1 - ARROW_WEIGHT);
                rightArrow.moveTo(yyLink, xx2);
                rightArrow.lineTo(yyLink + ARROW_WEIGHT, xx2 + ARROW_WEIGHT);
                rightArrow.lineTo(yyLink + ARROW_WEIGHT, xx2 - ARROW_WEIGHT);
            } else if (orientation == PlotOrientation.VERTICAL) {
                line1 = new Line2D.Double(xx1, yy1, xx1, yyMax);
                line2 = new Line2D.Double(xx2, yy2, xx2, yyMax);
                line3 = new Line2D.Double(xx1, yyLink, xx2, yyLink);
                leftArrow.moveTo(xx1 + ARROW_WEIGHT, yyLink - ARROW_WEIGHT);
                leftArrow.lineTo(xx1, yyLink);
                leftArrow.lineTo(xx1 + ARROW_WEIGHT, yyLink + ARROW_WEIGHT);
                rightArrow.moveTo(xx2 - ARROW_WEIGHT, yyLink - ARROW_WEIGHT);
                rightArrow.lineTo(xx2, yyLink);
                rightArrow.lineTo(xx2 - ARROW_WEIGHT, yyLink + ARROW_WEIGHT);
            }

            g2.setPaint(p);
            g2.setStroke(DASH_LINE_STROKE);
            g2.draw(line1);
            g2.draw(line2);
            g2.setStroke(REGULAR_LINE_STROKE);
            g2.draw(line3);
            g2.draw(leftArrow);
            g2.draw(rightArrow);

            // draw label
            if (isItemLabelVisible(series, item)) {
                drawMassDiffItemLabel(g2, orientation, dataset, series, item, xxLabel, yyLabel);
                drawAminoAcidItemLabel(g2, orientation, dataset, series, item, xxLabel, yyLabel, false);
            }

            if (entities != null) {
                addEntity(entities, line3.getBounds(), dataset, series, item, 0.0, 0.0);
            }
        }
    }

    /**
     * Draws an item label.
     *
     * @param g2          the graphics device.
     * @param orientation the orientation.
     * @param dataset     the dataset.
     * @param series      the series index (zero-based).
     * @param item        the item index (zero-based).
     * @param x           the x coordinate (in Java2D space).
     * @param y           the y coordinate (in Java2D space).
     */
    private void drawMassDiffItemLabel(Graphics2D g2, PlotOrientation orientation,
                                       XYDataset dataset, int series,
                                       int item, double x, double y) {

        if (this.massItemLabelGenerator == null) {
            return;
        }

        Font labelFont = getItemLabelFont(series, item);
        Paint paint = getItemLabelPaint(series, item);
        g2.setFont(labelFont);
        g2.setPaint(paint);
        String label = this.massItemLabelGenerator.generateLabel(dataset,
                series, item);

        ItemLabelPosition position = getSeriesNegativeItemLabelPosition(series);
        Point2D anchorPoint = calculateLabelAnchorPoint(
                position.getItemLabelAnchor(), x, y, orientation);
        TextUtilities.drawRotatedString(label, g2,
                (float) anchorPoint.getX(), (float) anchorPoint.getY(),
                position.getTextAnchor(), position.getAngle(),
                position.getRotationAnchor());
    }

    private void drawAminoAcidItemLabel(Graphics2D g2, PlotOrientation orientation,
                                        XYDataset dataset, int series,
                                        int item, double x, double y,
                                        boolean negative) {
        if (this.aminoAcidItemLabelGenerator == null) {
            return;
        }

        Font labelFont = getItemLabelFont(series, item);
        Paint paint = getItemPaint(series, item);
        g2.setFont(labelFont);
        g2.setPaint(paint);
        String label = this.aminoAcidItemLabelGenerator.generateLabel(dataset,
                series, item);

        // get the label position..
        ItemLabelPosition position;
        if (!negative) {
            position = getPositiveItemLabelPosition(series, item);
        } else {
            position = getNegativeItemLabelPosition(series, item);
        }

        // work out the label anchor point...
        Point2D anchorPoint = calculateLabelAnchorPoint(
                position.getItemLabelAnchor(), x, y, orientation);
        TextUtilities.drawRotatedString(label, g2,
                (float) (anchorPoint.getX()), (float) anchorPoint.getY(),
                position.getTextAnchor(), position.getAngle(),
                position.getRotationAnchor());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || ((!(obj instanceof MassDiffRenderer)) && super.equals(obj));
    }
}
