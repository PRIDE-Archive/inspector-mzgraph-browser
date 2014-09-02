package uk.ac.ebi.pride.toolsuite.mzgraph.gui.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.iongen.model.PeakSet;
import uk.ac.ebi.pride.utilities.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.utilities.mol.NeutralLoss;
import uk.ac.ebi.pride.utilities.mol.ProductIonPair;
import uk.ac.ebi.pride.utilities.iongen.ion.FragmentIonType;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotationInfo;
import uk.ac.ebi.pride.toolsuite.mzgraph.psm.PSMMatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Based on the theoretical fragmented ions table model, we allow user add annotations in experimental data.
 * There are two type of annotations, one is automatic annotations, the other is manual annotations.
 * <ol>
 *     <li>automatic annotations: generate by the matching between peak list and theoretical m/z list. </li>
 *     <li>manual annotations: based on some data source, such as mascot. </li>
 * </ol>
 *
 * Creator: Qingwei-XU
 * Date: 11/10/12
 */

public class ExperimentalFragmentedIonsTableModel extends TheoreticalFragmentedIonsTableModel {
    private static final Logger logger = LoggerFactory.getLogger(ExperimentalFragmentedIonsTableModel.class);

    private PrecursorIon precursorIon;

    private ExperimentalParams params = ExperimentalParams.getInstance();

    private boolean calculate = false;

    /**
     * Whether show auto annotations, or show manual annotations. Default, the value is {@value}.
     */
    private boolean showAuto = false;

    /**
     * store all manual annotations. including a, b, c, x, y, z ions annotations,
     * excluding ammonium ion.
     */
    private List<IonAnnotation> manualAnnotations = new ArrayList<IonAnnotation>();

    /**
     * store all peak set, base on m/z ascent order.
     */
    private PeakSet peakSet = new PeakSet();

    /**
     * There are some observers of experimental table model data change.
     * In our system, we use a sorted map to store the observers, which key means the update order.
     * All observers update their data with ascending order.
     */
    public ExperimentalFragmentedIonsTableModel(PrecursorIon precursorIon, ProductIonPair ionPair) {
        super(precursorIon, ionPair);
        this.precursorIon = precursorIon;
    }

    public ExperimentalFragmentedIonsTableModel(PrecursorIon precursorIon, ProductIonPair ionPair,
                                                List<IonAnnotation> manualAnnotations) {
        this(precursorIon, ionPair);
        addAllManualAnnotations(manualAnnotations);
    }

    /**
     * if change product ion pair successful
     */
    @Override
    public void setProductIonPair(ProductIonPair ionPair) {
        super.setProductIonPair(ionPair);
    }

    public void setShowWaterLoss(boolean showWaterLoss) {
        params.setShowWaterLoss(showWaterLoss);
    }

    public void setShowAmmoniaLoss(boolean showAmmoniaLoss) {
        params.setShowAmmoniaLoss(showAmmoniaLoss);
    }

    private List<IonAnnotation> toList(IonAnnotation[][] matrix) {
        List<IonAnnotation> annotationList = new ArrayList<IonAnnotation>();

        if (matrix == null) {
            return annotationList;
        }

        IonAnnotation annotation;
        for (IonAnnotation[] row : matrix) {
            for (IonAnnotation cell : row) {
                annotation = cell;
                if (annotation != null) {
                    annotationList.add(annotation);
                }
            }
        }

        return annotationList;
    }

    /**
     * set peak set
     */
    public void setPeaks(PeakSet peakSet) {
        this.peakSet = peakSet;
    }


    /**
     * @see #setPeaks(PeakSet)
     */
    public void setPeaks(double[] mzArray, double[] intensityArray) {
        PeakSet peaks = PeakSet.getInstance(mzArray, intensityArray);
        setPeaks(peaks);
    }

    /**
     * Calculate the column offset, based on charge and neutral loss type. maxCharge is the count of max charges
     * of product ion.
     */
    private int getOffset(int charge, NeutralLoss loss, int precursorCharge) {
        if (precursorCharge > 3) {
            precursorCharge = 3;
        }

        if (charge > precursorCharge) {
            return -1;
        }

        if (loss == null) {
            return charge;
        } else if (loss == NeutralLoss.AMMONIA_LOSS) {
            return precursorCharge + charge;
        } else {
            return 2 * precursorCharge + charge;
        }
    }

    /**
     * Get the table column number based on the {@link IonAnnotationInfo.Item}
     */
    public int getColumnNumber(FragmentIonType type, int charge, NeutralLoss loss) {
        int precursorCharge = precursorIon.getCharge();

        int offset = getOffset(charge, loss, precursorCharge);

        if (type.equals(FragmentIonType.X_ION) || type.equals(FragmentIonType.Y_ION) || type.equals(FragmentIonType.Z_ION)) {
            return offset;
        } else if (type.equals(FragmentIonType.A_ION) || type.equals(FragmentIonType.B_ION) || type.equals(FragmentIonType.C_ION)) {
            return getColumnCount() / 2 + offset;
        } else {
            return -1;
        }
    }

    /**
     * Get the table row number based on the position of {@link IonAnnotationInfo.Item}.
     */
    public int getRowNumber(FragmentIonType type, int location) {
        int row = -1;

        if (type.equals(FragmentIonType.X_ION) || type.equals(FragmentIonType.Y_ION) || type.equals(FragmentIonType.Z_ION)) {
            row = getRowCount() - location - 1;
        } else if (type.equals(FragmentIonType.A_ION) || type.equals(FragmentIonType.B_ION) || type.equals(FragmentIonType.C_ION)) {
            row = location;
        }

        return row;
    }

    /**
     * Add a manual annotation, and update manual data matrix. This method will reused by the
     * {@link #addAllManualAnnotations(java.util.List)}
     * methods.
     */
    public boolean addManualAnnotation(IonAnnotation annotation) {
        /**
         * No allow two annotation work on same peak.
         * @see IonAnnotation#equals(Object)
         */
        if (manualAnnotations.contains(annotation)) {
            return false;
        }

        manualAnnotations.add(annotation);
        return true;
    }

    /**
     * Based on Product Ions Pairs to decide whether show manual annotations, or not.
     */
    private boolean isFitManual(IonAnnotation annotation) {
        IonAnnotationInfo info = annotation.getAnnotationInfo();

        FragmentIonType type;
        for (int i = 0; i < info.getNumberOfItems(); i++) {
            type = info.getItem(i).getType();

            switch (params.getIonPair()) {
                case B_Y:
                    if (type.equals(FragmentIonType.B_ION) || type.equals(FragmentIonType.Y_ION)) {
                        return true;
                    }
                    break;
                case A_X:
                    if (type.equals(FragmentIonType.A_ION) || type.equals(FragmentIonType.X_ION)) {
                        return true;
                    }
                    break;
                case C_Z:
                    if (type.equals(FragmentIonType.C_ION) || type.equals(FragmentIonType.Z_ION)) {
                        return true;
                    }
                    break;
            }
        }

        return false;
    }

    /**
     * Based on Product Ions Pairs to show according manual annotations.
     * This is a read only ion annotation list. Can not do add, remove operations, just used for browse.
     */
    public List<IonAnnotation> getManualAnnotations() {
        List<IonAnnotation> newManualAnnotations = new ArrayList<IonAnnotation>();

        /**
         * filteredManualAnnotations is subset of manualAnnotationList, which only include b, y, a, x, c, z ion
         * annotations. While, manualAnnotations include all annotations. the {@link #getManualAnnotations()}
         * will get ion annotations based on ion pairs.
         */
        List<IonAnnotation> filteredManualAnnotations = filterAnnotations(manualAnnotations);

        for (IonAnnotation annotation : filteredManualAnnotations) {
            if (isFitManual(annotation)) {
                newManualAnnotations.add(annotation);
            }
        }

        return Collections.unmodifiableList(newManualAnnotations);
    }

    /**
     * Get all manual annotations, not based on the product ions pairs.
     * This is unmodifiable list.
     */
    public List<IonAnnotation> getAllManualAnnotations() {
        return Collections.unmodifiableList(manualAnnotations);
    }

    private java.util.List<IonAnnotation> filterAnnotations(java.util.List<IonAnnotation> srcList) {
        java.util.List<IonAnnotation> tarList = new ArrayList<IonAnnotation>();

        // filter non- a, b, c, x, y, z ions annotation
        // filter charge > 2 ions annotation.
        // which not display in the mz table panel.
        IonAnnotationInfo srcInfo;
        IonAnnotationInfo.Item srcItem;
        IonAnnotationInfo tarInfo;
        IonAnnotationInfo.Item tarItem;
        IonAnnotation tarAnnotation;
        for (IonAnnotation annotation : srcList) {
            srcInfo = annotation.getAnnotationInfo();
            tarInfo = new IonAnnotationInfo();
            for (int i = 0; i < srcInfo.getNumberOfItems(); i++) {
                srcItem = srcInfo.getItem(i);
                if (srcItem.getType().getName().equals(FragmentIonType.B_ION.getName()) ||
                        srcItem.getType().getName().equals(FragmentIonType.Y_ION.getName()) ||
                        srcItem.getType().getName().equals(FragmentIonType.A_ION.getName()) ||
                        srcItem.getType().getName().equals(FragmentIonType.X_ION.getName()) ||
                        srcItem.getType().getName().equals(FragmentIonType.C_ION.getName()) ||
                        srcItem.getType().getName().equals(FragmentIonType.Z_ION.getName())) {

                    try {
                        if (srcItem.getCharge() <= 2) {
                            tarItem = (IonAnnotationInfo.Item) srcItem.clone();
                            tarInfo.addItem(tarItem);
                        }
                    } catch (CloneNotSupportedException e) {
                        logger.error(e.getMessage());
                    }
                }
            }

            if (tarInfo.getNumberOfItems() != 0) {
                tarAnnotation = new IonAnnotation(annotation.getMz(), annotation.getIntensity(), tarInfo);
                tarList.add(tarAnnotation);
            }
        }

        return tarList;
    }

    /**
     * Generate matched data matrix based on manual annotation list.
     * If patch operation failure, system will rollback all add annotations.
     */
    public boolean addAllManualAnnotations(List<IonAnnotation> manualAnnotationList) {
        if (manualAnnotationList == null) {
            return false;
        }

        // clone manual annotation list.
        List<IonAnnotation> tempAnnotationList = new ArrayList<IonAnnotation>();
        for (IonAnnotation annotation : this.manualAnnotations) {
            try {
                tempAnnotationList.add((IonAnnotation) annotation.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        for (IonAnnotation annotation : manualAnnotationList) {
            if (! addManualAnnotation(annotation)) {
                // rollback
                this.manualAnnotations = tempAnnotationList;
//                this.manualData = tempManualData;

                return false;
            }
        }

        return true;
    }

    public PrecursorIon getPrecursorIon() {
        return precursorIon;
    }

    /**
     * call PSM algorithm to generate auto annotations.
     */
    private IonAnnotation[][]  match() {
        return PSMMatcher.getInstance().match(this, this.peakSet);
    }

    public boolean isCalculate() {
        return calculate;
    }

    /**
     * Whether using PSM algorithm to generate auto annotations. For example, if delta m/z is bigger,
     * not calculate.
     */
    public void setCalculate(boolean calculate) {
        this.calculate = calculate;
    }

    /**
     * Experimental fragmented ions table model can only show auto or manual annotations at one time.
     * @see #getAnnotations()
     * @see #getMatchedData()
     * @param showAuto set true means show auto annotations, set false show manual annotations.
     */
    public void setShowAuto(boolean showAuto) {
        this.showAuto = showAuto;
    }

    public boolean isShowAuto() {
        return showAuto;
    }

    public List<IonAnnotation> getAllAutoAnnotations() {
        IonAnnotation[][] autoData = calculate ? match() : new IonAnnotation[getRowCount()][getColumnCount()];
        return toList(autoData);
    }

    private boolean haveNeutralLoss(IonAnnotation annotation) {
        IonAnnotationInfo info = annotation.getAnnotationInfo();
        NeutralLoss loss = info.getItem(0).getNeutralLoss();

        boolean fit = false;
        if (loss == null) {
            fit = true;
        } else if (loss.equals(NeutralLoss.WATER_LOSS) && params.isShowWaterLoss()) {
            fit = true;
        } else if (loss.equals(NeutralLoss.AMMONIA_LOSS) && params.isShowAmmoniaLoss()) {
            fit = true;
        }

        return fit;
    }

    public List<IonAnnotation> getAutoAnnotations() {
        List<IonAnnotation> autoAnnotations = getAllAutoAnnotations();

        List<IonAnnotation> newAutoAnnotations = new ArrayList<IonAnnotation>();
        for (IonAnnotation annotation : autoAnnotations) {
            if (haveNeutralLoss(annotation)) {
                newAutoAnnotations.add(annotation);
            }
        }

        return Collections.unmodifiableList(newAutoAnnotations);
    }

    /**
     * based on {@link #params#isShowAuto()} the decide to show auto annotations or manual annotations.
     */
    public List<IonAnnotation> getAnnotations() {
        if (showAuto) {
            return getAutoAnnotations();
        } else {
            return getManualAnnotations();
        }
    }

    public IonAnnotation[][] getManualData(List<IonAnnotation> annotationList) {
        IonAnnotation[][] manualData = new IonAnnotation[getRowCount()][getColumnCount()];

        if (annotationList == null || annotationList.size() == 0) {
            return manualData;
        }

        List<IonAnnotation> filteredAnnotationList = filterAnnotations(annotationList);

        int charge;
        FragmentIonType type;
        int location;
        NeutralLoss loss;
        int row;
        int col;

        IonAnnotationInfo annotationInfo;
        IonAnnotationInfo.Item item;
        for (IonAnnotation annotation : filteredAnnotationList) {
            if (! isFitManual(annotation)) {
                continue;
            }

            annotationInfo = annotation.getAnnotationInfo();

            for (int i = 0; i < annotationInfo.getNumberOfItems(); i++) {
                item = annotationInfo.getItem(i);
                charge = item.getCharge();
                type = item.getType();
                loss = item.getNeutralLoss();
                location = item.getLocation();

                // ignore the annotation which location on n-terminal or c-terminal.
                if (location == getRowCount()) {
                    continue;
                }

                row = getRowNumber(type, location);
                col = getColumnNumber(type, charge, loss);

                manualData[row][col] = annotation;
            }
        }

        return manualData;
    }

    public IonAnnotation[][] getAutoData() {
        IonAnnotation[][] autoData = new IonAnnotation[getRowCount()][getColumnCount()];

        IonAnnotation[][] psmData = calculate ? match() : new IonAnnotation[getRowCount()][getColumnCount()];
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                if (psmData[i][j] != null && haveNeutralLoss(psmData[i][j])) {
                    autoData[i][j] = psmData[i][j];
                }
            }
        }

        return autoData;
    }

    /**
     * based on {@link #params#isShowAuto()} parameter, display auto data matrix or manual data matrix.
     * @see #setShowAuto(boolean)
     */
    public IonAnnotation[][] getMatchedData() {
        if (showAuto) {
            return getAutoData();
        } else {
            return getManualData(manualAnnotations);
        }
    }

    /**
     * If user modify interval range, system will store the new value into {@link ExperimentalParams},
     */
    public void setRange(double range) {
        params.setRange(range);
    }
}
