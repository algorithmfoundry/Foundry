/*
 * File:                AbstractMTJMatrix.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 21, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.annotation.SoftwareLicenseType;
import gov.sandia.cognition.annotation.SoftwareReference;
import gov.sandia.cognition.math.matrix.mtj.decomposition.SingularValueDecompositionMTJ;
import gov.sandia.cognition.math.matrix.mtj.decomposition.EigenDecompositionRightMTJ;
import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.math.matrix.AbstractMatrix;
import gov.sandia.cognition.math.matrix.DimensionalityMismatchException;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixEntry;
import gov.sandia.cognition.math.matrix.decomposition.SingularValueDecomposition;
import gov.sandia.cognition.math.matrix.TwoMatrixEntry;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.uib.cipr.matrix.MatrixSingularException;

/**
 * Relies on internal MTJ matrix to do some of the heavy lifting, without
 * assuming that the underlying matrix is Dense or Sparse
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-18",
    changesNeeded=true,
    comments="Comments marked throughout the file with / / / on first column.",
    response=@CodeReviewResponse(
        respondent="Kevin R. Dixon",
        date="2006-05-18",
        moreChangesNeeded=false,
        comments={
            "Added an assertion to dotTimesEquals",
            "Commented the private classes so that they're up to snuff."
        }
    )
)
@PublicationReference(
    author="Bjorn-Ove Heimsund",
    title="Matrix Toolkits for Java (MTJ)",
    type=PublicationType.WebPage,
    year=2006,
    url="http://ressim.berlios.de/",
    notes="All subclasses essentially wrap one of MTJ's matrix classes."
)
@SoftwareReference(
    name="Matrix Toolkits for Java (MTJ)",
    version="0.9.6",
    url="http://ressim.berlios.de/",
    license=SoftwareLicenseType.LGPL,
    licenseVersion="2.1",
    licenseURL="http://ressim.berlios.de/")
public abstract class AbstractMTJMatrix
    extends AbstractMatrix
{ 

    /**
     * Internal matrix that does the heavy lifting from the MTJ package
     */
    private transient no.uib.cipr.matrix.Matrix internalMatrix;

    /**
     * Creates a new instance of AbstractMTJMatrix
     * @param internalMatrix MTJ-based matrix to store inside of this
     */
    protected AbstractMTJMatrix(
        no.uib.cipr.matrix.Matrix internalMatrix)
    {
        this.setInternalMatrix(internalMatrix);
    }

    @Override
    public AbstractMTJMatrix clone()
    {
        AbstractMTJMatrix clone = (AbstractMTJMatrix) super.clone();
        clone.setInternalMatrix( this.getInternalMatrix().copy() );
        return clone;
    }

    /**
     * Gets the internal MTJ matrix that this class is wrapping.
     * 
     * @return Internal MTJ matrix.
     */
    public no.uib.cipr.matrix.Matrix getInternalMatrix()
    {
        return this.internalMatrix;
    }

    /**
     * Setter for internalMatrix
     * @param internalMatrix internal MTJ-based matrix
     */
    protected void setInternalMatrix(
        no.uib.cipr.matrix.Matrix internalMatrix)
    {
        if( internalMatrix == null )
        {
            throw new IllegalArgumentException(
                "internalMatrix cannot be null!" );
        }
        this.internalMatrix = internalMatrix;
    }

    public int getNumRows()
    {
        return this.internalMatrix.numRows();
    }

    public int getNumColumns()
    {
        return this.internalMatrix.numColumns();
    }

    public double getElement(
        int rowIndex,
        int columnIndex)
    {
        return this.internalMatrix.get(rowIndex, columnIndex);
    }

    public void setElement(
        int rowIndex,
        int columnIndex,
        double value)
    {
        this.internalMatrix.set(rowIndex, columnIndex, value);
    }

    public ComplexNumber logDeterminant()
    {

        if (this.isSquare() == false)
        {
            throw new IllegalArgumentException("Matrix must be square");
        }

        DenseMatrix decompositionMatrix;


        if (this instanceof DenseMatrix)
        {
            decompositionMatrix = (DenseMatrix) this;
        }
        else
        {
            decompositionMatrix = new DenseMatrix(this);
        }

        boolean useEVD = false;
        if (useEVD)
        {
            EigenDecompositionRightMTJ evd = EigenDecompositionRightMTJ.create(
                decompositionMatrix);
            return evd.getLogDeterminant();
        }
        else
        {

            // Note: It's generally faster to use LU decomposition for
            // determinants, but QR can be more stable in some circumstances
            no.uib.cipr.matrix.UpperTriangDenseMatrix triangularMatrix;
            boolean useQR = false;
            int sign;
            if (useQR)
            {
                no.uib.cipr.matrix.QR decomposition =
                    no.uib.cipr.matrix.QR.factorize(this.internalMatrix);
                triangularMatrix = decomposition.getR();
                sign = 1;
            }
            else
            {
                no.uib.cipr.matrix.DenseLU decomposition =
                    no.uib.cipr.matrix.DenseLU.factorize(this.internalMatrix);
                triangularMatrix = decomposition.getU();

                sign = 1;
                for (int i = 0; i < decomposition.getPivots().length; i++)
                {
                    if (decomposition.getPivots()[i] != (i + 1))
                    {
                        sign = -sign;
                    }
                }

            }

            int M = triangularMatrix.numRows();
            int N = triangularMatrix.numColumns();
            int maxIndex = (M < N) ? M : N;

            //Both LU and QR return upper triangular matrices and the "L" and
            // "Q" matrices have a determinant of one, so the real action is
            // in the "U" and "R" matrices, which are both upper (right)
            // triangular.  As we remember from our algebra courses, the
            // eigenvalues of a triangular matrix are the diagonal elements
            // themselves.  We also remember that the product of the eigenvalues
            // is the determinant.
            // 
            // The diagonal elements (for either LU or QR) will be REAL, but
            // they may be negative.  The logarithm of a negative number is
            // the logarithm of the absolute value of the number, with an
            // imaginary part of PI.  The exponential is all that matters, so
            // the log-determinant is equivalent, MODULO PI (3.14...), so
            // we just toggle this sign bit.
            double logsum = 0.0;
            for (int i = 0; i < maxIndex; i++)
            {
                double eigenvalue = triangularMatrix.get(i, i);
                if (eigenvalue < 0.0)
                {
                    sign = -sign;
                    logsum += Math.log(-eigenvalue);
                }
                else
                {
                    logsum += Math.log(eigenvalue);
                }

            }

            return new ComplexNumber(logsum, (sign < 0) ? Math.PI : 0.0);
        }

    }

    /**
     * Determines if the matrices are effectively equal to each other
     * 
     * 
     * @return true if effectively equal, false otherwise
     * @param matrix Matrix to compare <code>this</code> to, must be same size as
     *          <code>this</code>
     * @param effectiveZero tolerance to determine element-wise equality
     */
    public boolean equals(
        final AbstractMTJMatrix matrix,
        double effectiveZero)
    {
        // Create a new iterator that iterates through the UNION of nonzero
        // entries between the two given matrices.  Then all we have to do
        // is see if any of these entries are move than "effectiveZero" apart
        MatrixUnionIteratorMTJ iterator =
            new MatrixUnionIteratorMTJ(this, matrix);

        while (iterator.hasNext())
        {
            TwoMatrixEntry entry = iterator.next();
            double diff = entry.getFirstValue() - entry.getSecondValue();
            if (Math.abs(diff) > effectiveZero)
            {
                return false;
            }
        }

        return true;

    }

    public void plusEquals(
        final Matrix matrix)
    {
        this.plusEquals((AbstractMTJMatrix) matrix);
    }

    /**
     * Inline addition of <code>this</code> and <code>matrix</code>, modifies
     * the elements of <code>this</code>
     * 
     * 
     * @param matrix Must have same dimensions <code>this</code>
     */
    public void plusEquals(
        final AbstractMTJMatrix matrix)
    {
        // MTJ does the dimension checking, so I'm not going to duplicate that
        // with an additional check
        this.internalMatrix.add(matrix.internalMatrix);
    }

    public void minusEquals(
        final Matrix matrix)
    {
        this.minusEquals((AbstractMTJMatrix) matrix);
    }

    /**
     * Subtracts the elements of <code>matrix</code> from the elements of 
     * <code>this</code>, modifies the elements of <code>this</code>
     * 
     * 
     * @param matrix Must have same dimensions <code>this</code>
     */
    public void minusEquals(
        final AbstractMTJMatrix matrix)
    {
        // MTJ does the dimension checking, so I'm not going to duplicate that
        // with an additional check
        double additionScaleFactor = -1.0;
        this.internalMatrix.add(additionScaleFactor,
            matrix.internalMatrix);
    }

    public AbstractMTJMatrix times(
        final Matrix matrix)
    {
        return this.times((AbstractMTJMatrix) matrix);
    }

    /**
     * Matrix multiplication of <code>this</code> and <code>matrix</code>,
     * operates like the "<code>*</code>" operator in Matlab
     * 
     * 
     * @return Matrix multiplication of <code>this</code> and
     * <code>matrix</code>, will <code>this.getNumRows()</code> rows and
     * <code>matrix.getNumColumns()</code> columns
     * @param matrix <code>this.getNumColumns()==matrix.getNumRows()</code>
     */
    public abstract AbstractMTJMatrix times(
        final AbstractMTJMatrix matrix);

    public void dotTimesEquals(
        final Matrix matrix)
    {
        this.dotTimesEquals((AbstractMTJMatrix) matrix);
    }

    /**
     * Inline element-wise multiplication of the elements in <code>this</code>
     * and <code>matrix</code>, modifies the elements of <code>this</code>
     * 
     * @param matrix Must have same dimensions <code>this</code>
     */
    public void dotTimesEquals(
        final AbstractMTJMatrix matrix)
    {
        // make sure the matrices are the same dimension
        this.assertSameDimensions(matrix);

        // Iterators loop over all values, or all nonzero, values in "this".
        // The resulting matrix doesn't need to element-wise multiply the
        // zero values, since the result would be zero anyway.
        for (MatrixEntry e : this)
        {
            double otherValue = matrix.getElement(
                e.getRowIndex(), e.getColumnIndex());

            e.setValue(e.getValue() * otherValue);
        }
    }

    public void scaleEquals(
        double scaleFactor)
    {
        // Iterators loop over all values, or all nonzero, values in "this".
        // The resulting matrix doesn't need to element-wise multiply the
        // zero values, since the result would be zero anyway.
        for (MatrixEntry e : this)
        {
            e.setValue(e.getValue() * scaleFactor);
        }
    }

    @Override
    public void scaledPlusEquals(
        final double scaleFactor,
        final Matrix other)
    {
        this.scaledPlusEquals(scaleFactor, (AbstractMTJMatrix) other);
    }

    /**
     * Adds to this vector the scaled version of the other given vector.
     *
     * @param   scaleFactor
     *      The scale factor to use.
     * @param   other
     *      The other vector to scale and then add to this vector.
     */
    public void scaledPlusEquals(
        final double scaleFactor,
        final AbstractMTJMatrix other)
    {
        this.internalMatrix.add(scaleFactor, other.internalMatrix);
    }

    /**
     * Subtracts from this matrix the scaled version of the other given matrix.
     *
     * @param   scaleFactor
     *      The scale factor to use.
     * @param   other
     *      The other matrix to scale and then subtract from this matrix.
     */
    public void scaledMinusEquals(
        final double scaleFactor,
        final AbstractMTJMatrix other)
    {
        this.scaledPlusEquals(-scaleFactor, other);
    }

    public Iterator<MatrixEntry> iterator()
    {
        return new AbstractMTJMatrixIterator();
    }

    public boolean isSquare()
    {
        return this.getNumRows() == this.getNumColumns();
    }

    public AbstractMTJVector times(
        final Vector vector)
    {
        return this.times((AbstractMTJVector) vector);
    }

    /**
     * Returns the column vector from the equation
     * return = this * vector 
     *
     *
     * @param vector
     *          Vector by which to post-multiply this, must have the
     *          same number of rows as this
     * @return Vector with the same dimensionality as the number of rows as
     * this and vector
     */
    abstract public AbstractMTJVector times(
        final AbstractMTJVector vector);

    public void identity()
    {
        int N;
        this.internalMatrix.zero();
        if (this.getNumRows() < this.getNumColumns())
        {
            N = this.getNumRows();
        }
        else
        {
            N = this.getNumColumns();
        }

        for (int i = 0; i < N; i++)
        {
            this.setElement(i, i, 1.0);
        }
    }

    public Matrix solve(
        final Matrix B)
    {
        return this.solve((AbstractMTJMatrix) B);
    }

    public Vector solve(
        final Vector b)
    {
        return this.solve((AbstractMTJVector) b);
    }

    /**
     * Solves for "X" in the equation this * X = B, where X is a DenseMatrix,
     * "this" and "B" will be converted to a DenseMatrix (if not already)
     * @param B AbstractMTJMatrix, will be converted to a DenseMatrix
     * @return DenseMatrix of "X" in this * X = B
     */
    final public Matrix solve(
        AbstractMTJMatrix B)
    {
        DenseMatrix X =
            new DenseMatrix(this.getNumColumns(), B.getNumColumns());

        DenseMatrix Bdense;
        if (B instanceof DenseMatrix)
        {
            Bdense = (DenseMatrix) B;
        }
        else
        {
            Bdense = new DenseMatrix(B);
        }

        DenseMatrix Adense;
        if (this instanceof DenseMatrix)
        {
            Adense = (DenseMatrix) this;
        }
        else
        {
            Adense = new DenseMatrix(this);
        }

        boolean usePseudoInverse = false;
        try
        {
            Adense.solveInto(Bdense, X);
            usePseudoInverse = false;
        }
        catch ( MatrixSingularException e )
        {
            Logger.getLogger(AbstractMTJMatrix.class.getName()).log(Level.WARNING,
                "AbstractMTJMatrix.solve(): Matrix is singular.");
            usePseudoInverse = true;
        }
        
        // Sometimes LAPACK will return NaNs or infs as the solutions, but MTJ
        // won't throw the exception, so we need to check for this.
        // If we detect this, then we'll use a pseudoinverse
        if(!usePseudoInverse)        
        {
            for( int i = 0; i < X.getNumRows(); i++ )
            {
                for (int j = 0; j < X.getNumColumns(); j++ )
                {
                    double v = X.getElement(i, j);
                    if( Double.isNaN(v) || Double.isInfinite(v) )
                    {
                        Logger.getLogger(AbstractMTJMatrix.class.getName()).log(Level.WARNING,
                            "AbstractMTJMatrix.solve(): Solver produced invalid results.");
                        usePseudoInverse = true;
                        break;
                    }
                }
                if( usePseudoInverse )
                {
                    break;
                }
            }
        }
        
        if( usePseudoInverse )
        {
            // The original LU solver produced a sucky answer, so let's use
            // the absurdly expensive SVD least-squares solution
            return Adense.pseudoInverse().times(Bdense);
        }
        
        return X;
    }

    /**
     * Solves for "x" in the equation: this*x = b
     *
     * @param b must satisfy this.getNumColumns() == b.getDimensionality()
     * @return x Vector with dimensions (this.getNumRows())
     */
    public Vector solve(
        final AbstractMTJVector b)
    {
        DenseVector x = new DenseVector(this.getNumColumns());
        DenseVector bdense;
        if (b instanceof DenseVector)
        {
            bdense = (DenseVector) b;
        }
        else
        {
            bdense = new DenseVector(b);
        }

        DenseMatrix Adense;
        if (this instanceof DenseMatrix)
        {
            Adense = (DenseMatrix) this;
        }
        else
        {
            Adense = new DenseMatrix(this);
        }

        boolean usePseudoInverse = false;
        try
        {
            Adense.solveInto(bdense, x);
            usePseudoInverse = false;
        }
        catch ( MatrixSingularException e )
        {
            Logger.getLogger(AbstractMTJMatrix.class.getName()).log(Level.WARNING,
                "AbstractMTJMatrix.solve(): Matrix is singular.");
            usePseudoInverse = true;
        }
        
        // Sometimes LAPACK will return NaNs or infs as the solutions, but MTJ
        // won't throw the exception, so we need to check for this.
        // If we detect this, then we'll use a pseudoinverse
        if(!usePseudoInverse)        
        {
            for( int i = 0; i < x.getDimensionality(); i++ )
            {
                double v = x.getElement(i);
                if( Double.isNaN(v) || Double.isInfinite(v) )
                {
                    Logger.getLogger(AbstractMTJMatrix.class.getName()).log(Level.WARNING,
                        "AbstractMTJMatrix.solve(): Solver produced invalid results.");
                    usePseudoInverse = true;
                    break;
                }
            }
        }
        
        if( usePseudoInverse )
        {
            // The original LU solver produced a sucky answer, so let's use
            // the absurdly expensive SVD least-squares solution
            return Adense.pseudoInverse().times(b);
        }
        
        return x;
    }

    public Matrix inverse()
    {

        if (this.isSquare() == false)
        {
            throw new UnsupportedOperationException(
                "Can only invert square matrices.");
        }

        DenseMatrix I = DenseMatrixFactoryMTJ.INSTANCE.createIdentity(
            this.getNumRows(), this.getNumColumns());

        Matrix AI = this.solve(I);

        return AI;

    }

    public double normFrobenius()
    {
        return this.internalMatrix.norm(
            no.uib.cipr.matrix.Matrix.Norm.Frobenius);
    }

    public boolean isSymmetric(
        double effectiveZero)
    {

        if (this.isSquare() == false)
        {
            throw new DimensionalityMismatchException(
                this.getNumRows(), this.getNumColumns());
        }

        double difference;

        // Loop over each row, but start the column one to the right of the
        // diagonal, which is row+1
        for (int i = 0; i < this.getNumRows(); i++)
        {
            for (int j = i + 1; j < this.getNumColumns(); j++)
            {
                // If we find any transposed entry which has an absolute
                // difference greater than the effectiveZero, then we
                // know that the matrix isn't symmetric
                difference = this.getElement(i, j) - this.getElement(j, i);
                if (Math.abs(difference) > effectiveZero)
                {
                    return false;
                }
            }
        }

        /*
         * We didn't find any transposed entries that are different,
         * so the matrix is symmetric
         */
        return true;
    }

    @Override
    public void zero()
    {
        this.internalMatrix.zero();
    }

    public int rank(
        double effectiveZero)
    {

        DenseMatrix svdDude;

        if (this instanceof DenseMatrix)
        {
            svdDude = (DenseMatrix) this;
        }
        else
        {
            svdDude = new DenseMatrix(this);
        }

        SingularValueDecomposition svd =
            SingularValueDecompositionMTJ.create(svdDude);

        return svd.effectiveRank(effectiveZero);

    }

    /**
     * Internal method for accessing MTJ's general transpose routine
     *
     * @param destinationMatrix
     *          matrix into which to store the result
     */
    protected void transposeInto(
        AbstractMTJMatrix destinationMatrix)
    {
        this.internalMatrix.transpose(
            destinationMatrix.internalMatrix);
    }

    /**
     * Internal routine for storing a submatrix into and AbstractMTJMatrix.
     * Gets the embedded submatrix inside of the Matrix, specified by the
     * inclusive, zero-based indices such that the result matrix will have size
     * (maxRow-minRow+1) x (maxColum-minCcolumn+1)
     * 
     * 
     * @param minRow Zero-based index into the rows of the Matrix, must be less than
     *          or equal to maxRow
     * @param maxRow Zero-based index into the rows of the Matrix, must be greater
     *          than or equal to minRow
     * @param minColumn Zero-based index into the rows of the Matrix, must be less than
     *          or equal to maxColumn
     * @param maxColumn Zero-based index into the rows of the Matrix, must be greater
     *          than or equal to minColumn
     * @param destinationMatrix
     *          the destination submatrix of dimension
     *          (maxRow-minRow+1)x(maxColumn-minColumn+1)
     *
     */
    protected void getSubMatrixInto(
        int minRow,
        int maxRow,
        int minColumn,
        int maxColumn,
        AbstractMTJMatrix destinationMatrix)
    {

        if ((destinationMatrix.getNumRows() != (maxRow - minRow + 1)) ||
            (destinationMatrix.getNumColumns() != (maxColumn - minColumn + 1)))
        {
            throw new DimensionalityMismatchException(
                "Submatrix is incorrect size.");
        }

        for (int i = minRow; i <= maxRow; i++)
        {
            for (int j = minColumn; j <= maxColumn; j++)
            {
                destinationMatrix.setElement(
                    i - minRow, j - minColumn, this.getElement(i, j));
            }
        }

    }

    /**
     * Internal function call that stores the result of a matrix multiply
     * into the given destinationMatrix.
     *
     * @param multiplicationMatrix
     *          matrix to postmultiply this by
     * @param destinationMatrix
     *          matrix to store the matrix multiplication result, must have
     *          rows == this.getNumRows and columns ==
     *          multiplicationMatrix.getNumColumns
     */
    protected void timesInto(
        final AbstractMTJMatrix multiplicationMatrix,
        AbstractMTJMatrix destinationMatrix)
    {
        int M = this.getNumRows();
        int N = multiplicationMatrix.getNumColumns();
        if ((M != destinationMatrix.getNumRows()) ||
            (N != destinationMatrix.getNumColumns()))
        {
            throw new DimensionalityMismatchException(
                "Multiplication dimensions do not agree.");
        }

        this.internalMatrix.mult(
            multiplicationMatrix.internalMatrix,
            destinationMatrix.internalMatrix);
    }

    /**
     * Internal function call that stores the result of a matrix multiply
     * into the given destinationVector.
     *
     * @param multiplicationVector
     *          vector to postmultiply this by
     * @param destinationVector
     *          vector to store the matrix multiplication result, must have
     *          dimensionality == this.getNumRows
     */
    protected void timesInto(
        final AbstractMTJVector multiplicationVector,
        AbstractMTJVector destinationVector)
    {
        int M = this.getNumRows();
        if (M != destinationVector.getDimensionality())
        {
            throw new DimensionalityMismatchException(
                M, destinationVector.getDimensionality());
        }

        this.internalMatrix.mult(
            multiplicationVector.getInternalVector(),
            destinationVector.getInternalVector());
    }

    public void convertFromVector(
        Vector parameters)
    {

        int M = this.getNumRows();
        int N = this.getNumColumns();

        if ((M * N) != parameters.getDimensionality())
        {
            throw new IllegalArgumentException(
                "Elements in this does not equal elements in parameters");
        }

        int index = 0;
        for (int j = 0; j < N; j++)
        {
            for (int i = 0; i < M; i++)
            {
                this.setElement(i, j, parameters.getElement(index));
                index++;
            }
        }

    }

    public DenseVector convertToVector()
    {

        int M = this.getNumRows();
        int N = this.getNumColumns();

        DenseVector parameters = new DenseVector(M * N);

        int index = 0;
        for (int j = 0; j < N; j++)
        {
            for (int i = 0; i < M; i++)
            {
                parameters.setElement(index, this.getElement(i, j));
                index++;
            }
        }

        return parameters;
    }

    /**
     * Private iterator class for MTJ-based matrices, contains backpointer to
     * the AbstractMTJMatrix so that the entry can modify the underlying
     * elements
     */
    class AbstractMTJMatrixIterator
        implements Iterator<MatrixEntry>
    {

        /**
         * Internal MTJ-based iterator that does the heavy lifting
         */
        private Iterator<no.uib.cipr.matrix.MatrixEntry> internalIterator;

        /**
         * MatrixEntry for this matrix
         */
        private MatrixEntry entry = null;

        /**
         * Creates a new instance of the iterator, with a back pointer to the matrix
         */
        public AbstractMTJMatrixIterator()
        {
            setInternalIterator(
                AbstractMTJMatrix.this.internalMatrix.iterator());
            setEntry(new AbstractMTJMatrixEntry());
        }

        /**
         * Getter for internalIterator
         * @return Internal MTJ-based iterator that does the heavy lifting
         */
        protected Iterator<no.uib.cipr.matrix.MatrixEntry> getInternalIterator()
        {
            return this.internalIterator;
        }

        /**
         * Setter for internalIterator
         * @param internalIterator Internal MTJ-based iterator that does the heavy lifting
         */
        protected void setInternalIterator(
            Iterator<no.uib.cipr.matrix.MatrixEntry> internalIterator)
        {
            this.internalIterator = internalIterator;
        }

        /**
         * {@inheritDoc}
         * @return {@inheritDoc}
         */
        public boolean hasNext()
        {
            return this.getInternalIterator().hasNext();
        }

        /**
         * {@inheritDoc}
         * @return {@inheritDoc}
         */
        public MatrixEntry next()
        {
            no.uib.cipr.matrix.MatrixEntry internalNext =
                getInternalIterator().next();

            getEntry().setRowIndex(internalNext.row());
            getEntry().setColumnIndex(internalNext.column());
            return getEntry();
        }

        /**
         * {@inheritDoc}
         */
        public void remove()
        {
            getInternalIterator().remove();
        }

        /**
         * Getter for entry
         * @return MatrixEntry for this matrix
         */
        public MatrixEntry getEntry()
        {
            return this.entry;
        }

        /**
         * Setter for entry
         * @param entry MatrixEntry for this matrix
         */
        public void setEntry(
            MatrixEntry entry)
        {
            this.entry = entry;
        }

    }

    /**
     * MatrixEntry implementation for MTJ-based matrices,
     * with a backpointer to the underlying AbstractMTJMatrix
     */
    class AbstractMTJMatrixEntry
        implements MatrixEntry
    {

        /**
         * Current row index for the entry
         */
        private int rowIndex;

        /**
         * Current column index for the entry
         */
        private int columnIndex;

        /**
         * Creates a new matrix entry with zeros for indices
         */
        AbstractMTJMatrixEntry()
        {
            this(0, 0);
        }

        /**
         * Creates a new matrix entry at the specified indices
         * @param rowIndex zero-based row index for the entry
         * @param columnIndex zero-based column index for the entry
         */
        AbstractMTJMatrixEntry(
            int rowIndex,
            int columnIndex)
        {
            this.setRowIndex(rowIndex);
            this.setColumnIndex(columnIndex);
        }

        /**
         * {@inheritDoc}
         * @return {@inheritDoc}
         */
        public double getValue()
        {
            return AbstractMTJMatrix.this.getElement(
                this.getRowIndex(), this.getColumnIndex());
        }

        /**
         * {@inheritDoc}
         * @param value {@inheritDoc}
         */
        public void setValue(
            double value)
        {
            AbstractMTJMatrix.this.setElement(
                this.getRowIndex(), this.getColumnIndex(), value);
        }

        /**
         * {@inheritDoc}
         * @return {@inheritDoc}
         */
        public int getRowIndex()
        {
            return this.rowIndex;
        }

        /**
         * {@inheritDoc}
         * @param rowIndex {@inheritDoc}
         */
        public void setRowIndex(
            int rowIndex)
        {
            this.rowIndex = rowIndex;
        }

        /**
         * {@inheritDoc}
         * @return {@inheritDoc}
         */
        public int getColumnIndex()
        {
            return this.columnIndex;
        }

        /**
         * {@inheritDoc}
         * @param columnIndex {@inheritDoc}
         */
        public void setColumnIndex(
            int columnIndex)
        {
            this.columnIndex = columnIndex;
        }

    }

}
