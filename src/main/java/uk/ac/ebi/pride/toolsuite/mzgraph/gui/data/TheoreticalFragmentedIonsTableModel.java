package uk.ac.ebi.pride.toolsuite.mzgraph.gui.data;

import uk.ac.ebi.pride.utilities.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.utilities.iongen.model.ProductIon;
import uk.ac.ebi.pride.utilities.util.ProductIonFactory;
import uk.ac.ebi.pride.utilities.mol.AminoAcid;
import uk.ac.ebi.pride.utilities.mol.ProductIonPair;
import uk.ac.ebi.pride.utilities.mol.ProductIonType;
import uk.ac.ebi.pride.utilities.iongen.ion.FragmentIonType;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Theoretical Fragmented Ions table model. Which include five modules:
 * <ol>
 *     <li>ID: show previous matrix id, which values from [sequence.length-1..1]. The last row is empty.</li>
 *     <li>Previous matrix: The last row is empty.</li>
 *     <li>Seq: the peptide sequence, each row display one amino acid. If there exists modification, enclosed by bracket.</li>
 *     <li>Posterior matrix: the first row is empty.</li>
 *     <li>ID: show posterior matrix id, which values from [1..sequence.length-1]. The first row is empty.</li>
 * </ol>
 *
 * Creator: Qingwei-XU
 * Date: 10/10/12
 */

public class TheoreticalFragmentedIonsTableModel extends AbstractTableModel {
    private ExperimentalParams params = ExperimentalParams.getInstance();

    private PrecursorIon precursorIon;

    private Object[] columnNames;
    private Object[][] data;

    private List<List<ProductIon>> createProductIonListByCharge(PrecursorIon precursorIon, ProductIonType type) {
        List<List<ProductIon>> matrix = new ArrayList<List<ProductIon>>();

        int charge = precursorIon.getCharge();
        if (charge <= 0) {
            throw new IllegalArgumentException("precursor charge can not less than 1");
        }

        // the product ions charge up to 2.
        int prodCharge;
        if (charge <= 2) {
            prodCharge = charge;
        } else {
            prodCharge = 2;
        }

        for (int i = 1; i <= prodCharge; i++) {
            matrix.add(ProductIonFactory.createDefaultProductIons(precursorIon, type, i));
        }

        return matrix;
    }

    private List<List<ProductIon>> createProductIonMatrix(PrecursorIon precursorIon, FragmentIonType ionGroup) {
        List<List<ProductIon>> matrix = new ArrayList<List<ProductIon>>();

        if (ionGroup.equals(FragmentIonType.B_ION)) {
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.B));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.B_NH3));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.B_H2O));
        } else if (ionGroup.equals(FragmentIonType.Y_ION)) {
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.Y));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.Y_NH3));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.Y_H2O));
        } else if (ionGroup.equals(FragmentIonType.A_ION)) {
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.A));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.A_NH3));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.A_H2O));
        }else if (ionGroup.equals(FragmentIonType.X_ION)) {
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.X));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.X_NH3));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.X_H2O));
        }else if (ionGroup.equals(FragmentIonType.C_ION)) {
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.C));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.C_NH3));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.C_H2O));
        }else if (ionGroup.equals(FragmentIonType.Z_ION)) {
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.Z));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.Z_NH3));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.Z_H2O));
        }

        return matrix;
    }

    private Object[] createColumnNames(List<List<ProductIon>> predMatrix, List<List<ProductIon>> postMatrix) {
        List<String> columns = new ArrayList<String>();

        columns.add("#");
        for (List<ProductIon> ionList : predMatrix) {
            columns.add(ionList.get(0).toString());
        }
        columns.add("seq");
        for (List<ProductIon> ionList : postMatrix) {
            columns.add(ionList.get(0).toString());
        }
        columns.add("#");

        return columns.toArray();
    }

    /**
     * @see TheoreticalFragmentedIonsTableModel
     */
    private Object[][] createData(PrecursorIon precursorIon,
                                  List<List<ProductIon>> predMatrix,
                                  List<List<ProductIon>> postMatrix) {
        this.precursorIon = precursorIon;

        List<AminoAcid> acidList = precursorIon.getPeptide().getAminoAcids();
        Object[][] data = new Object[acidList.size()][predMatrix.size() + postMatrix.size() + 3];


        for (int i = 1; i < acidList.size(); i++) {
            data[i - 1][0] = acidList.size() - i;
        }

        for (int i = 0; i < acidList.size(); i++) {
            data[i][predMatrix.size() + 1] = acidList.get(i);
        }

        for (int i = 1; i < acidList.size(); i++) {
            data[i][predMatrix.size() + postMatrix.size() + 2] = i;
        }

        List<ProductIon> column;
        for (int j = 0; j < predMatrix.size(); j++) {
            column = predMatrix.get(j);
            int offset = 1;
            for (int i = 0; i < column.size(); i++) {
                data[i][offset + j] = column.get(i);
            }

            column = postMatrix.get(j);
            offset += predMatrix.size() + 1;
            for (int i = 0; i < column.size(); i++) {
                data[i + 1][offset + j] = column.get(i);
            }
        }

        return data;
    }

   public TheoreticalFragmentedIonsTableModel(PrecursorIon precursorIon, ProductIonPair ionPair) {
       if (precursorIon == null) {
           throw new IllegalArgumentException("Precursor ion can not be null!");
       }

       this.precursorIon = precursorIon;

       setProductIonPair(ionPair);
    }

    public void setProductIonPair(ProductIonPair ionPair) {
        params.setIonPair(ionPair);

        /**
         * Previous matrix: display x, y, z ions m/z values, based on the {@link #ionPair}.
         * Most matrix has three columns (precursor ion have one charge) or six columns (have two charges).
         * The six columns based on the following order:
         * y+, y++(optional), y_NH3, y_NH3++(optional), y_WATER, y_WATER++(optional)
         *
         * <P>Notice: the matrix up to have nine columns, if precursor ion have three charges. </P>
         * <P>Notice: the last row of previous matrix is empty</P>
         * <P>Notice: the order is descending. </P>
         */
        List<List<ProductIon>> prevMatrix = null;

        /**
         * Posterior matrix: display a, b, c ions m/z values, based on the {@link #ionPair}.
         * matrix has three columns (precursor ion have one charge) or six columns (have two charges).
         * The six columns based on the following order:
         * b+, b++(optional), b_NH3, b_NH3++(optional), b_WATER, b_WATER++(optional)
         *
         * <P>Notice: the first row of posterior matrix is empty</P>
         */
        List<List<ProductIon>> postMatrix = null;

        switch (params.getIonPair()) {
            case A_X:
                prevMatrix =  createProductIonMatrix(precursorIon, FragmentIonType.X_ION);
                postMatrix = createProductIonMatrix(precursorIon, FragmentIonType.A_ION);
                break;
            case B_Y:
                prevMatrix =  createProductIonMatrix(precursorIon, FragmentIonType.Y_ION);
                postMatrix = createProductIonMatrix(precursorIon, FragmentIonType.B_ION);
                break;
            case C_Z:
                prevMatrix =  createProductIonMatrix(precursorIon, FragmentIonType.Z_ION);
                postMatrix = createProductIonMatrix(precursorIon, FragmentIonType.C_ION);
                break;
        }

        columnNames = createColumnNames(prevMatrix, postMatrix);

        data = createData(precursorIon, prevMatrix, postMatrix);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return (String) columnNames[column];
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    public boolean isMassColumn(int columnIndex) {
        return isPredMatrixColumn(columnIndex) || isPostMatrixColumn(columnIndex);
    }

    public boolean isPredMatrixColumn(int columnIndex) {
        return columnIndex > 0 && columnIndex < getColumnCount() / 2;
    }

    public boolean isPostMatrixColumn(int columnIndex) {
        return columnIndex < getColumnCount() - 1 && columnIndex > getColumnCount() / 2;
    }

    public boolean isSeqColumn(int columnIndex) {
        return columnIndex == getColumnCount() / 2;
    }

    public boolean isIDColumn(int columnIndex) {
        return columnIndex == 0 || columnIndex == getColumnCount() - 1;
    }

    public PrecursorIon getPrecursorIon() {
        return precursorIon;
    }
}
