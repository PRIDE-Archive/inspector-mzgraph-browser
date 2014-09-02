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
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.BooleanList;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.MzGraphConstants;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static uk.ac.ebi.pride.toolsuite.mzgraph.chart.renderer.RendererConstants.*;

/**
 * Renderer for drawing amino acid annotations
 *
 * User: rwang
 * Date: 18-Jun-2010
 * Time: 16:41:13
 */
public class PeakRangeRenderer extends AbstractXYItemRenderer
        implements org.jfree.chart.renderer.xy.XYItemRenderer,
        java.lang.Cloneable,
        org.jfree.util.PublicCloneable,
        java.io.Serializable {

    private BooleanList additionalItemLabelsVisibleList;
    private Boolean additionalItemLabelsVisible;
    private boolean hasSetUpperMargin;

    private XYItemLabelGenerator additionalItemLabelGenerator;

    /**
     * Stores the height of range with series index
     */
    private Map<Integer, Double> seriesRangeHeight;

    public PeakRangeRenderer() {
        this.seriesRangeHeight = new HashMap<Integer, Double>();
        this.additionalItemLabelsVisibleList = new BooleanList();
        this.additionalItemLabelGenerator = null;
        this.hasSetUpperMargin = false;
    }

    public void reset() {
        seriesRangeHeight.clear();
        additionalItemLabelsVisibleList.clear();
        hasSetUpperMargin = false;
    }

    @Override
    public boolean isItemLabelVisible(int row, int column) {
        return true;
    }

    public boolean isAdditionalItemLabelVisible(int series, int item) {
        return isSeriesAdditionalItemLabelsVisible(series);
    }

    public boolean isSeriesAdditionalItemLabelsVisible(int series) {
        // return the override, if there is one
        if (this.additionalItemLabelsVisible != null) {
            return this.additionalItemLabelsVisible;
        }

        // otherwise look up the boolean table
        Boolean b = this.additionalItemLabelsVisibleList.getBoolean(series);
        if (b == null) {
            b = Boolean.FALSE;
        }
        return b;
    }

    public void setAdditionalItemLabelVisible(boolean visible) {
        if (this.additionalItemLabelsVisible == null || this.additionalItemLabelsVisible.booleanValue() != visible) {
            this.additionalItemLabelsVisible = visible;
            fireChangeEvent();
        }
    }

    public void setSeriesAdditionalItemLabelVisible(int series, boolean visible) {
        setSeriesAdditionalItemLabelVisible(series, visible, true);
    }

    public void setSeriesAdditionalItemLabelVisible(int series, boolean visible, boolean notify) {
        this.additionalItemLabelsVisibleList.setBoolean(series, visible);
        if (notify) {
            fireChangeEvent();
        }
    }

    public XYItemLabelGenerator getAdditionalItemLabelGenerator() {
        return additionalItemLabelGenerator;
    }

    public void setAdditionalItemLabelGenerator(XYItemLabelGenerator additionalItemLabelGenerator) {
        this.additionalItemLabelGenerator = additionalItemLabelGenerator;
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

        if (dataset instanceof XYIntervalSeriesCollection) {
            EntityCollection entities = null;
            if (info != null) {
                entities = info.getOwner().getEntityCollection();
            }

            XYIntervalSeriesCollection seriesCollection = (XYIntervalSeriesCollection) dataset;
            // get item values
            double x1 = seriesCollection.getStartXValue(series, item);
            double y1 = seriesCollection.getStartYValue(series, item);
            double x2 = seriesCollection.getEndXValue(series, item);
            double y2 = seriesCollection.getEndYValue(series, item);

            // get the max range value for the series
            double yMax = getRangeHeight(plot, series);
            // increase y axis upper margin if necessary
            adjustRangeUpperMargin(plot, yMax);

            // get axis location
            RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge yAxisLocation = plot.getRangeAxisEdge();

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
                line1 = new Line2D.Double(yy1, xx1 - DEFAULT_ADDITIONAL_ITEM_LABEL_ANCHOR_OFFSET - 8, yyMax, xx1);
                line2 = new Line2D.Double(yy2, xx2 - DEFAULT_ADDITIONAL_ITEM_LABEL_ANCHOR_OFFSET - 8, yyMax, xx2);
                line3 = new Line2D.Double(yyLink, xx1, yyLink, xx2);
                leftArrow.moveTo(yyLink - ARROW_WEIGHT, xx1 + ARROW_WEIGHT);
                leftArrow.lineTo(yyLink, xx1);
                leftArrow.lineTo(yyLink + ARROW_WEIGHT, xx1 + ARROW_WEIGHT);
                rightArrow.moveTo(yyLink - ARROW_WEIGHT, xx2 - ARROW_WEIGHT);
                rightArrow.lineTo(yyLink, xx2);
                rightArrow.lineTo(yyLink + ARROW_WEIGHT, xx2 - ARROW_WEIGHT);
            } else if (orientation == PlotOrientation.VERTICAL) {
                line1 = new Line2D.Double(xx1, yy1 - DEFAULT_ADDITIONAL_ITEM_LABEL_ANCHOR_OFFSET - 8, xx1, yyMax);
                line2 = new Line2D.Double(xx2, yy2 - DEFAULT_ADDITIONAL_ITEM_LABEL_ANCHOR_OFFSET - 8, xx2, yyMax);
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
                drawItemLabel(g2, orientation, dataset, series, item, xxLabel, yyLabel, false);
            }

            if (isAdditionalItemLabelVisible(series, item)) {
                drawAdditionalItemLabel(g2, orientation, dataset, series, item, xxLabel, yyLabel);
            }

            if (entities != null) {
                addEntity(entities, line3.getBounds(), dataset, series, item, 0.0, 0.0);
            }
        }
    }

    private double getRangeHeight(XYPlot plot, int seriesIndex) {
        double height;
        Double seriesHeight = seriesRangeHeight.get(seriesIndex);
        if (seriesHeight != null) {
            // if the high of the peak has already been calculated.
            height = seriesHeight;
        } else {
            // get highest y
            double highestY = 0;
            int dataSetCnt = plot.getDatasetCount();
            for (int i = 0; i < dataSetCnt; i++) {
                XYDataset dataSet = plot.getDataset(i);
                double hy = DatasetUtilities.findMaximumRangeValue(dataSet).doubleValue();
                if (hy > highestY) {
                    highestY = hy;
                }
            }
            Collection<Double> previousHeights = seriesRangeHeight.values();
            double maxHeight = previousHeights.isEmpty() ? highestY : Collections.max(seriesRangeHeight.values());
            // set the increase step for height
            double heightStep = highestY * DEFAULT_HEIGHT_MARGIN;
            height = maxHeight + heightStep;
            seriesRangeHeight.put(seriesIndex, height);
        }
        return height;
    }


    /**
     * Increase/Decrease the range upper margin.
     *
     * @param plot plot
     * @param yMax the maximum height of the lines, this doesn't include labels
     */
    private void adjustRangeUpperMargin(XYPlot plot, double yMax) {
        ValueAxis yAxis = plot.getRangeAxis();
        // the height of the label
        double labelHeight = yMax * DEFAULT_CHART_MARGIN;
        // the height of the label plus the highest range
        double totalHeight = yMax + labelHeight;
        // the current length of the yAxis
        double yLength = yAxis.getRange().getLength();
        // the current height of the upper margin
        double upperMarginHeight = yLength - totalHeight;
        // the current margin
        double currMargin = yAxis.getUpperMargin();

        // increase the upper margin, which will trigger a repaint of the chart
        if (!hasSetUpperMargin) {
            // the new margin
            double newMargin = (totalHeight + labelHeight - yLength)/yLength;
            yAxis.setUpperMargin(currMargin + newMargin);
            hasSetUpperMargin = true;
        }
    }

    /**
     * Draws amino acid annotation label
     *
     * @param g2          the graphics device.
     * @param orientation the orientation.
     * @param dataset     the dataset.
     * @param series      the series index (zero-based).
     * @param item        the item index (zero-based).
     * @param x           the x coordinate (in Java2D space).
     * @param y           the y coordinate (in Java2D space).
     * @param negative    indicates a negative value (which affects the item
     *                    label position).
     */
    protected void drawItemLabel(Graphics2D g2, PlotOrientation orientation,
                                 XYDataset dataset, int series, int item, double x, double y,
                                 boolean negative) {

        XYItemLabelGenerator generator = getItemLabelGenerator(series, item);
        if (generator != null) {
            Font labelFont = getItemLabelFont(series, item);
            Paint paint = getItemPaint(series, item);
            g2.setFont(labelFont);
            g2.setPaint(paint);
            String label = generator.generateLabel(dataset, series, item);

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

    }

    /**
     * Draws mass difference label
     *
     * @param g2          the graphics device.
     * @param orientation the orientation.
     * @param dataset     the dataset.
     * @param series      the series index (zero-based).
     * @param item        the item index (zero-based).
     * @param x           the x coordinate (in Java2D space).
     * @param y           the y coordinate (in Java2D space).
     */
    private void drawAdditionalItemLabel(Graphics2D g2, PlotOrientation orientation,
                                         XYDataset dataset, int series,
                                         int item, double x, double y) {

        if (this.additionalItemLabelGenerator == null) {
            return;
        }

        Font labelFont = getItemLabelFont(series, item);
        Paint paint = getItemPaint(series, item);
        g2.setFont(labelFont);
        g2.setPaint(paint);
        String label = this.additionalItemLabelGenerator.generateLabel(dataset,
                series, item);

        ItemLabelPosition position = getSeriesNegativeItemLabelPosition(series);
        Point2D anchorPoint = calculateLabelAnchorPoint(
                position.getItemLabelAnchor(), x, y, orientation);
        TextUtilities.drawRotatedString(label, g2,
                (float) anchorPoint.getX(), (float) anchorPoint.getY(),
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
