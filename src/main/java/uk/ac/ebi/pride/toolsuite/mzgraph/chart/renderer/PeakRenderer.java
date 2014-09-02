package uk.ac.ebi.pride.toolsuite.mzgraph.chart.renderer;

import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.jfree.util.BooleanList;
import org.jfree.util.ShapeUtilities;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.label.PeakItemLabelGenerator;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

import static uk.ac.ebi.pride.toolsuite.mzgraph.chart.renderer.RendererConstants.*;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 17-Jun-2010
 * Time: 22:40:51
 */
public class PeakRenderer extends AbstractXYItemRenderer
        implements org.jfree.chart.renderer.xy.XYItemRenderer,
        java.lang.Cloneable,
        org.jfree.util.PublicCloneable,
        java.io.Serializable {


    public final static XYItemLabelGenerator DEFAULT_ITEM_LABEL_GENERATOR = new PeakItemLabelGenerator();

    /**
     * default tool tip generator for peaks
     */
    // public final static XYToolTipGenerator DEFAULT_PEAK_TOOL_TIP = new PeakToolTipGenerator();

    private int highlightSeries;
    private int highlightItem;
    private Paint highlightColor;
    private Shape highlightShape;
    private BooleanList additionalItemLabelsVisibleList;
    private Boolean additionalItemLabelsVisible;
    private XYItemLabelGenerator itemLabelGenerator;
    private XYItemLabelGenerator additionalItemLabelGenerator;

    public PeakRenderer() {
        this.highlightSeries = -1;
        this.highlightItem = -1;
        this.highlightColor = DEFAULT_HIGHLIGHT_PAINT;
        this.itemLabelGenerator = DEFAULT_ITEM_LABEL_GENERATOR;
        this.highlightShape = DEFAULT_HIGHLIGHT_SHAPE;
        this.additionalItemLabelGenerator = null;
        this.additionalItemLabelsVisible = null;
        this.additionalItemLabelsVisibleList = new BooleanList();
    }

    public void reset() {
        this.highlightSeries = -1;
        this.highlightItem = -1;
        this.additionalItemLabelsVisible = null;
        this.additionalItemLabelsVisibleList.clear();
    }

    public XYItemLabelGenerator getAdditionalItemLabelGenerator() {
        return this.additionalItemLabelGenerator;
    }

    public void setAdditionalItemLabelGenerator(XYItemLabelGenerator generator) {
        this.additionalItemLabelGenerator = generator;
        fireChangeEvent();
    }

    public void setHighlightedItem(int series, int item) {
        if (highlightSeries != series || highlightItem != item) {
            highlightSeries = series;
            highlightItem = item;
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    @Override
    public Paint getItemPaint(int series, int item) {
        if (series == highlightSeries && item == highlightItem) {
            return highlightColor;
        } else {
            return super.getItemPaint(series, item);
        }
    }

    @Override
    public boolean isItemLabelVisible(int series, int item) {
        if (series == highlightSeries && item == highlightItem) {
            return true;
        } else {
            return super.isItemLabelVisible(series, item);
        }
    }

    public boolean isAdditionalItemLabelVisible(int series, int item) {
        return isSeriesAdditionalItemLabelsVisible(series);
    }

    public boolean isSeriesAdditionalItemLabelsVisible(int series) {
        // return the overide, if there is one
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
        this.additionalItemLabelsVisible = visible;
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

    @Override
    public Shape getSeriesShape(int series) {
        return highlightShape;
    }

    @Override
    public XYItemLabelGenerator getItemLabelGenerator(int series, int item) {
        return itemLabelGenerator;
    }

    public boolean isItemShapeVisible(int row, int column) {
        return (row == highlightSeries && column == highlightItem);
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

        // setup for collecting optional entity info
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }

        // get item value
        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        double yLow = 0.0;
        // get axis location
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        // get location in the plot
        double xx = domainAxis.valueToJava2D(x, dataArea, xAxisLocation);
        double yy = rangeAxis.valueToJava2D(y, dataArea, yAxisLocation);
        double yyLow = rangeAxis.valueToJava2D(yLow, dataArea, yAxisLocation);

        Paint p = getItemPaint(series, item);
        Stroke s = getItemStroke(series, item);
        Shape shape = getItemShape(series, item);
        Line2D line = null;
        Shape top = null;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(yyLow, xx, yy, xx);
            top = ShapeUtilities.createTranslatedShape(shape, yy, xx);
        } else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(xx, yyLow, xx, yy);
            top = ShapeUtilities.createTranslatedShape(shape, xx, yy);
        }

        // draw the peak
        g2.setPaint(p);
        g2.setStroke(s);
        g2.draw(line);
        if (isItemShapeVisible(series, item)) {
            g2.draw(top);
            // set m/z and intensity annotation
            java.util.List<XYAnnotation> annotations = plot.getAnnotations();
            for (XYAnnotation annotation : annotations) {
                plot.removeAnnotation(annotation, true);
            }
            double lx = domainAxis.getRange().getLength() * 0.02;
            double ly = rangeAxis.getRange().getLength() * 0.95;
            XYTextAnnotation mzAnnotation = new XYTextAnnotation("m/z: " + NumberFormat.getInstance().format(x), lx, ly);
            mzAnnotation.setTextAnchor(TextAnchor.TOP_LEFT);
            plot.addAnnotation(mzAnnotation, true);
            ly = rangeAxis.getRange().getLength() * 0.9;
            XYTextAnnotation intentAnnotation = new XYTextAnnotation("Intensity: " + NumberFormat.getInstance().format(y), lx, ly);
            intentAnnotation.setTextAnchor(TextAnchor.TOP_LEFT);
            plot.addAnnotation(intentAnnotation);
        }

        // draw item label
        if (isItemLabelVisible(series, item)) {
            drawItemLabel(g2, orientation, dataset, series, item, xx, yy, false);
        }

        if (isAdditionalItemLabelVisible(series, item)) {
            drawAdditionalItemLabel(g2, orientation, dataset, series, item, xx, yy);
        }

        // add an entity for the item
        if (entities != null) {
            addEntity(entities, line.getBounds(), dataset, series, item, 0.0, 0.0);
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
                    (float) (anchorPoint.getX() + DEFAULT_ITEM_LABEL_ANCHOR_OFFSET), (float) anchorPoint.getY(),
                    TextAnchor.BOTTOM_LEFT, position.getAngle(),
                    position.getRotationAnchor());
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
    private void drawAdditionalItemLabel(Graphics2D g2,
                                         PlotOrientation orientation,
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
        ItemLabelPosition position = getSeriesPositiveItemLabelPosition(series);
        Point2D anchorPoint = new Point2D.Double(x, y - DEFAULT_ADDITIONAL_ITEM_LABEL_ANCHOR_OFFSET);
        TextUtilities.drawRotatedString(label, g2,
                (float) anchorPoint.getX(), (float) anchorPoint.getY(),
                TextAnchor.CENTER, position.getAngle(),
                position.getRotationAnchor());
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
