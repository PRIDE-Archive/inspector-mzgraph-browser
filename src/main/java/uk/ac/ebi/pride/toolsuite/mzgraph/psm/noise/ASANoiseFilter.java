package uk.ac.ebi.pride.toolsuite.mzgraph.psm.noise;

import uk.ac.ebi.pride.utilities.iongen.model.Peak;
import uk.ac.ebi.pride.utilities.iongen.model.PeakSet;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.MzGraphConstants;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;
import uk.ac.ebi.pride.toolsuite.mzgraph.psm.NoiseFilter;

import java.util.Arrays;

/**
 * Get from pride-asa-pipeline project.
 * https://code.google.com/p/pride-asa-pipeline/
 *
 * Creator: Qingwei-XU
 * Date: 02/11/12
 */
public class ASANoiseFilter implements NoiseFilter {
    private double calcSum(double[] values) {
        if (values == null) {
            throw new IllegalArgumentException("Can not calculate sum of null!");
        }
        double sum = 0D;
        for (double d : values) {
            sum += d;
        }
        return sum;
    }

    private double calculateMean(double[] values) {
        if (values == null) {
            throw new IllegalArgumentException("Can not calculate mean of null!");
        }

        return (calcSum(values) / values.length);
    }

    private double calcVariance(double[] values, double mean) {
        double squares = 0D;
        for (double value : values) {
            squares += Math.pow((value - mean), 2);
        }
        return squares / (double) values.length;
    }

    private double calculateMedian(double[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Can not calculate median of null or empty array!");
        }

        // if there is only one value, then it automatically is the median
        if (values.length == 1) {
            return values[0];
        }

        double result; // the median we are going to calculate
        Arrays.sort(values);

        int middle = values.length / 2; // determine the middle of the list
        if (values.length % 2 == 0) { // even number of elements (middle is between two values)
            // build the average between the two values next to the middle
            result = (values[middle] + values[middle - 1]) / 2d;
        } else { // uneven number of elements (middle is one value of the list)
            result = values[middle];
        }

        return result;
    }

    private double min(double[] doubles) {
        if (doubles == null || doubles.length == 0) {
            throw new NullPointerException("array is null!");
        }

        double min = doubles[0];
        for (int i = 1; i < doubles.length; i++) {
            min = Math.min(min, doubles[i]);
        }
        return min;
    }

    private double max(double[] doubles) {
        if (doubles == null || doubles.length == 0) {
            throw new NullPointerException("array is null!");
        }

        double max = doubles[0];
        for (int i = 1; i < doubles.length; i++) {
            max = Math.max(max, doubles[i]);
        }
        return max;
    }

    /**
     * There are different 3 spectrum scenarios this noise threshold finder considers:
     * 1. a spectrum with few signal peaks compared to the noisepeaks
     * 2. a spectrum consisting out of signal peaks only
     * 3. a spectrum consisting out of noise peaks only
     *
     * @param signalValues the double array of signal values
     * @return the double threshold value
     */
    private double findNoiseThreshold(double[] signalValues) {
        double noiseThreshold;

        //calculate mean and standard deviation
        double mean = calculateMean(signalValues);
        double standardDeviation = Math.sqrt(calcVariance(signalValues, mean));

        //first use a winsonrisation approach (with the preset configuration) to find
        //the noise treshold for the privided signal values.
        double[] winsorisedValues = winsorise(signalValues);

        double winsorisedMedian = calculateMedian(winsorisedValues);
        double winsorisedMean = calculateMean(winsorisedValues);
        double winsorisedStandardDeviation = Math.sqrt(calcVariance(winsorisedValues, winsorisedMean));

        //check if the winsorised mean to mean ratio exceeds a given threshold
        double meanRatio = ((winsorisedMean - 0.0) < 0.001) ? 0.0 : winsorisedMean / mean;
//        double standardDeviationRatio = ((winsorisedStandardDeviation - 0.0) < 0.001) ? 0.0 : winsorisedStandardDeviation / standardDeviation;

        if (meanRatio < MzGraphConstants.ASA_MEAN_RATIO_THRESHOLD) {
            //scenario 1, the winsorisation has significantly decreased the mean
            //calculate the noise threshold for the spectrum (based on the winsorisation result)
            noiseThreshold = winsorisedMedian + MzGraphConstants.ASA_OUTLIER_LIMIT * winsorisedStandardDeviation;
        } //scenario 2 or 3
        //to make a distinction between the only signal or only noise spectrum, check the peak density
        else {
            double minimumValue = min(signalValues);
            double maximumValue = max(signalValues);
            //peak density: number of peaks / dalton
            double density = signalValues.length / (maximumValue - minimumValue);
            if (density < MzGraphConstants.ASA_DENSITY_THRESHOLD) {
                //scenario 2
                noiseThreshold = Math.max(mean - (1.5 * standardDeviation), 0.0);
            } else {
                //scenario 3
                noiseThreshold = mean + 1.5 * standardDeviation;
            }
        }

        return noiseThreshold;
    }

    private double[] winsorise(double[] signalValues) {
        double median = calculateMedian(signalValues);
        double currMAD = calcIntensityMAD(signalValues, median);
        double prevMAD = 3d * currMAD; //initial start value
        double[] correctedIntensities = new double[signalValues.length];

        while (((prevMAD - currMAD) / prevMAD) >= MzGraphConstants.ASA_CONVERGENCE_CRITERIUM) {
            correctedIntensities = reduceOutliers(signalValues, median + (MzGraphConstants.ASA_WINSORISATION_CONSTANT * currMAD));
            prevMAD = currMAD;
            currMAD = calcIntensityMAD(correctedIntensities, median);
        }

        return correctedIntensities;
    }

    private double calcIntensityMAD(double[] values, double median) {
        double[] diffs = new double[values.length];
        int cnt = 0;
        for (double p : values) {
            diffs[cnt++] = (Math.abs(p - median));
        }

        return calculateMedian(diffs);
    }

    private double[] reduceOutliers(double[] intensities, double maxIntensityLimit) {
        double[] correctedIntensities = new double[intensities.length];
        //sets all the values above the limit (outliers) to the limit
        //and therefore effectively eliminating outliers
        for (int i = 0; i < intensities.length; i++) {
            if (intensities[i] <= maxIntensityLimit) {
                correctedIntensities[i] = intensities[i];
            } else {
                correctedIntensities[i] = maxIntensityLimit;
            }
        }
        return correctedIntensities;
    }

    @Override
    public PeakSet filterNoise(PeakSet peaks, ExperimentalFragmentedIonsTableModel tableModel) {
        if (peaks == null) {
            return null;
        }

        if (peaks.size() == 0) {
            return peaks;
        }

        double experimentalPrecursorMass = tableModel.getPrecursorIon().getMassOverCharge();
        double threshold = findNoiseThreshold(peaks.getIntensityArray());

        PeakSet result = new PeakSet();

        double intensity;
        double mz;
        double lower = experimentalPrecursorMass - MzGraphConstants.ASA_PRECURSOR_MASS_WINDOW;
        double upper = experimentalPrecursorMass + MzGraphConstants.ASA_PRECURSOR_MASS_WINDOW;
        for (Peak peak : peaks) {
            //add the peak to the peak list if the peak intensity > threshold
            // and if the MZ ratio is not in 18D range of experimental precursor mass
            intensity = peak.getIntensity();
            mz = peak.getMz();

            if (intensity >= threshold && !(lower < mz && mz < upper)) {
                result.add(peak);
            }
        }

        return result;
    }
}
