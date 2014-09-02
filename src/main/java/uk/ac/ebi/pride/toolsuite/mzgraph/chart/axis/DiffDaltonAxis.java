package uk.ac.ebi.pride.toolsuite.mzgraph.chart.axis;

import org.jfree.chart.axis.*;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.data.Range;
import org.jfree.data.RangeType;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * User: Qingwei
 * Date: 29/11/12
 *
 * @see uk.ac.ebi.pride.toolsuite.mzgraph.gui.ExperimentalFragmentedIonsScatterChartPanel
 */
public class DiffDaltonAxis extends NumberAxis {
    private double lowerBound;
    private double upperBound;

    public DiffDaltonAxis(double lowerBound, double upperBound) {
        if (Double.compare(lowerBound, upperBound) > 0) {
            throw new IllegalArgumentException(lowerBound + " great than " + upperBound);
        }

        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    protected void autoAdjustRange() {
        Plot plot = getPlot();
        if (plot == null) {
            return;  // no plot, no data
        }

        if (plot instanceof ValueAxisPlot) {
            ValueAxisPlot vap = (ValueAxisPlot) plot;

            Range r = vap.getDataRange(this);
            if (r == null) {
                r = getDefaultAutoRange();
            }

            double upper = r.getUpperBound();
            double lower = r.getLowerBound();
            if (getRangeType() == RangeType.POSITIVE) {
                lower = Math.max(0.0, lower);
                upper = Math.max(0.0, upper);
            }
            else if (getRangeType() == RangeType.NEGATIVE) {
                lower = Math.min(0.0, lower);
                upper = Math.min(0.0, upper);
            }

            if (getAutoRangeIncludesZero()) {
                lower = Math.min(lower, 0.0);
                upper = Math.max(upper, 0.0);
            }
            double range = upper - lower;

            // if fixed auto range, then derive lower bound...
            double fixedAutoRange = getFixedAutoRange();
            if (fixedAutoRange > 0.0) {
                lower = upper - fixedAutoRange;
            }
            else {
                // ensure the autorange is at least <minRange> in size...
                double minRange = getAutoRangeMinimumSize();
                if (range < minRange) {
                    double expand = (minRange - range) / 2;
                    upper = upper + expand;
                    lower = lower - expand;
                    if (lower == upper) { // see bug report 1549218
                        double adjust = Math.abs(lower) / 10.0;
                        lower = lower - adjust;
                        upper = upper + adjust;
                    }
                    if (getRangeType() == RangeType.POSITIVE) {
                        if (lower < 0.0) {
                            upper = upper - lower;
                            lower = 0.0;
                        }
                    }
                    else if (getRangeType() == RangeType.NEGATIVE) {
                        if (upper > 0.0) {
                            lower = lower - upper;
                            upper = 0.0;
                        }
                    }
                }

                if (getAutoRangeStickyZero()) {
                    if (upper <= 0.0) {
                        upper = Math.min(0.0, upper + getUpperMargin() * range);
                    }
                    else {
                        upper = upper + getUpperMargin() * range;
                    }
                    if (lower >= 0.0) {
                        lower = Math.max(0.0, lower - getLowerMargin() * range);
                    }
                    else {
                        lower = lower - getLowerMargin() * range;
                    }
                }
                else {
                    upper = upper + getUpperMargin() * range;
                    lower = lower - getLowerMargin() * range;
                }
            }

            lower = lower > lowerBound ? lowerBound : lower;
            upper = upper < upperBound ? upperBound : upper;
            setRange(new Range(lower, upper), false, true);
        }
    }

    /**
     * only draw -0.5Da, 0 and 0.5Da, and ignore other labels.
     */
    @Override
    protected AxisState drawTickMarksAndLabels(Graphics2D g2,
                                               double cursor, Rectangle2D plotArea, Rectangle2D dataArea,
                                               RectangleEdge edge) {
        AxisState state = new AxisState(cursor);

        if (isAxisLineVisible()) {
            drawAxisLine(g2, cursor, dataArea, edge);
        }

        java.util.List ticks = refreshTicks(g2, state, dataArea, edge);
        state.setTicks(ticks);
        g2.setFont(getTickLabelFont());
        Iterator iterator = ticks.iterator();
        while (iterator.hasNext()) {
            ValueTick tick = (ValueTick) iterator.next();
            //only draw -0.5Da, 0 and 0.5Da ticks
            if (Double.compare(tick.getValue(), 0.5) != 0 && Double.compare(tick.getValue(), 0) != 0 && Double.compare(tick.getValue(), -0.5) != 0) {
                continue;
            }

            if (isTickLabelsVisible()) {
                g2.setPaint(getTickLabelPaint());
                float[] anchorPoint = calculateAnchorPoint(tick, cursor,
                        dataArea, edge);
                TextUtilities.drawRotatedString(tick.getText(), g2,
                        anchorPoint[0], anchorPoint[1], tick.getTextAnchor(),
                        tick.getAngle(), tick.getRotationAnchor());
            }

            if ((isTickMarksVisible() && tick.getTickType().equals(
                    TickType.MAJOR)) || (isMinorTickMarksVisible()
                    && tick.getTickType().equals(TickType.MINOR))) {

                double ol = (tick.getTickType().equals(TickType.MINOR)) ?
                        getMinorTickMarkOutsideLength() : getTickMarkOutsideLength();

                double il = (tick.getTickType().equals(TickType.MINOR)) ?
                        getMinorTickMarkInsideLength() : getTickMarkInsideLength();

                float xx = (float) valueToJava2D(tick.getValue(), dataArea,
                        edge);
                Line2D mark = null;
                g2.setStroke(getTickMarkStroke());
                g2.setPaint(getTickMarkPaint());
                if (edge == RectangleEdge.LEFT) {
                    mark = new Line2D.Double(cursor - ol, xx, cursor + il, xx);
                }
                else if (edge == RectangleEdge.RIGHT) {
                    mark = new Line2D.Double(cursor + ol, xx, cursor - il, xx);
                }
                else if (edge == RectangleEdge.TOP) {
                    mark = new Line2D.Double(xx, cursor - ol, xx, cursor + il);
                }
                else if (edge == RectangleEdge.BOTTOM) {
                    mark = new Line2D.Double(xx, cursor + ol, xx, cursor - il);
                }
                g2.draw(mark);
            }
        }

        // need to work out the space used by the tick labels...
        // so we can update the cursor...
        double used = 0.0;
        if (isTickLabelsVisible()) {
            if (edge == RectangleEdge.LEFT) {
                used += findMaximumTickLabelWidth(ticks, g2, plotArea,
                        isVerticalTickLabels());
                state.cursorLeft(used);
            }
            else if (edge == RectangleEdge.RIGHT) {
                used = findMaximumTickLabelWidth(ticks, g2, plotArea,
                        isVerticalTickLabels());
                state.cursorRight(used);
            }
            else if (edge == RectangleEdge.TOP) {
                used = findMaximumTickLabelHeight(ticks, g2, plotArea,
                        isVerticalTickLabels());
                state.cursorUp(used);
            }
            else if (edge == RectangleEdge.BOTTOM) {
                used = findMaximumTickLabelHeight(ticks, g2, plotArea,
                        isVerticalTickLabels());
                state.cursorDown(used);
            }
        }

        return state;
    }
}
