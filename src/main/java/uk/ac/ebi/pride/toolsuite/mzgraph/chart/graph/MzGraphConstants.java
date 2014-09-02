package uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph;

import org.jfree.chart.ChartTheme;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.PlotOrientation;

import java.awt.*;

/**
 * MzGraphConstants provides a list of constants for both chromatogram and spectrum
 * <p/>
 * User: rwang
 * Date: 10-Jun-2010
 * Time: 14:50:23
 */
public class MzGraphConstants {

    private MzGraphConstants() {
    }

    /**
     * default peak list dataset index in plot
     */
    public final static int DEFAULT_PEAK_DATASET_INDEX = 0;

    /**
     * default mass diff dataset index in plot
     */
    public final static int DEFAULT_MASS_DIFF_DATASET_INDEX = 1;

    /**
     * default fragment ion dataset index in plot
     */
    public final static int DEFAULT_ION_DATASET_INDEX = 2;

    /**
     * default amino acid dataset index in plot
     */
    public final static int DEFAULT_AMINO_ACID_DATASET_INDEX = 3;

    /**
     * default peak list data series index in dataset
     */
    public final static int DEFAULT_PEAK_DATA_SERIES_INDEX = 0;

    /**
     * default mass difference data series index in dataset
     */
    public final static int DEFAULT_MASS_DIFF_SERIES_INDEX = 0;

    /**
     * default temporary difference data series index in dataset.
     */
    public static final int DEFAULT_TEMP_MASS_DIFF_SERIES_INDEX = 1;

    /**
     * default chromatogram dataset's first series name
     */
    public final static String DEFAULT_CHROMATOGRAM_DATASET_NAME = "Chromatogram";

    /**
     * default peak list dataset's first series name
     */
    public final static String DEFAULT_PEAK_LIST_DATASET_NAME = "Peak List";

    /**
     * default mass diff dataset's first series name, note: there can only be one data series
     */
    public final static String DEFAULT_MASS_DIFF_DATASET_NAME = "Mass Difference";

    /**
     * default temporary mass diff data sereis
     */
    public final static String DEFAULT_TMP_MASS_DIFF_DATASET_NAME = "Tmp Mass Difference";

    /**
     * default title for spectrum chart
     */
    public final static String DEFAULT_SPECTRUM_CHART_TITLE = "";

    /**
     * default mz axis label
     */
    public final static String DEFAULT_MZ_AXIS_LABEL = "m/z";

    /**
     * default intensity axis label
     */
    public final static String DEFAULT_INTENSITY_AXIS_LABEL = "Intensity";

    /**
     * default time axis label
     */
    public final static String DEFAULT_TIME_AXIS_LABEL = "Time";

    /**
     * default chart orientation
     */
    public final static PlotOrientation DEFAULT_PLOT_ORIENTATION = PlotOrientation.VERTICAL;

    /**
     * default legend visibility
     */
    public final static boolean DEFAULT_LEGEND_VISIBILITY = false;

    /**
     * default grid line visibility
     */
    public final static boolean DEFAULT_GRID_LINE_VISIBILITY = false;

    /**
     * default chart theme
     */
    public final static ChartTheme DEFAULT_CHART_THEME = new StandardChartTheme("JFree");

    /**
     * default upper margin for intensity axis
     */
    public final static double DEFAULT_Y_AXIS_UPPER_MARGIN = 0.2;

    /**
     * default upper margin for mz axis
     */
    public final static double DEFAULT_X_AXIS_UPPER_MARGIN = 0.05;

    /**
     * default color for peaks
     */
    public final static Paint DEFAULT_PEAK_COLOR = Color.black;

    /**
     * default color for mass difference
     */
    public final static Paint DEFAULT_MASS_DIFF_COLOR = new Color(220, 30, 0);

    /**
     * default color for temporary mass differences.
     */
    public final static Paint DEFAULT_TEMP_MASS_DIFF_COLOR = new Color(127, 157, 227);

    /**
     * default peak list visibility
     */
    public final static boolean DEFAULT_PEAK_VISIBILITY = true;

    /**
     * default mass difference annotation visibility
     */
    public final static boolean DEFAULT_MASS_DIFF_VISIBILITY = false;

    /**
     * default fragment ion annotation visibility
     */
    public final static boolean DEFAULT_FRAGMENT_ION_VISIBILITY = false;

    /**
     * default amino acid annotation visibility
     */
    public final static boolean DEFAULT_AMINO_ACID_VISIBILITY = false;

    /**
     * default name for fragment ion
     */
    public final static String FRAGMENT_ION_NAME = "Fragment Ion";

    /**
     * default name for amino acid
     */
    public final static String AMINO_ACID_NAME = "Amino Acid";

    /**
     * default name for mass difference
     */
    public static final String MASS_DIFFERENT_NAME = "Mass Difference";

    /**
     * default label for showing mass difference
     */
    public static final String SHOW_MASS_DIFFERENT_LABEL = "Show mass differences";

    /**
     * default name for mass error tolerance
     */
    public static final String MASS_ERROR_TOLERANCE = "Mass Error Tolerance";

    /**
     * Dalton
     */
    public static final String DALTON = "Da";

    /**
     * theoretical and experimental fragmented ions table mz fraction.
     */
    public static final int TABLE_FRACTION = 3;
    public static final String TABLE_FONT_NAME = "sansserif";
    public static final int TABLE_COLUMN_FONT_SIZE = 16;
    public static final int TABLE_CELL_FONT_SIZE = 12;

    /**
     * Peptide Spectra Matches Algorithm range interval from [-0.5Da, 0.5Da]
     */
    public static final double INTERVAL_RANGE = 0.5;

    /**
     * Pride-asa-pipeline noise filter algorithm parameters
     */
    public static final double ASA_PRECURSOR_MASS_WINDOW = 18.0;
    public static final double ASA_WINSORISATION_CONSTANT = 1.5;
    public static final double ASA_OUTLIER_LIMIT = 2;
    public static final double ASA_CONVERGENCE_CRITERIUM = 0.001;
    public static final double ASA_MEAN_RATIO_THRESHOLD = 0.8;
    public static final double ASA_DENSITY_THRESHOLD = 1.0;

    public static final String DELTA_MZ_OVERFLOW1 = "Delta m/z value higher than 1 Da, ms/ms automatic ";
    public static final String DELTA_MZ_OVERFLOW2 = "spectrum annotations and fragmentation table not provided";
}
