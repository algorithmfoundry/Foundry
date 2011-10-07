/*
 * File:                KernelPrincipalComponentsAnalysis.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright December 17, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government.
 *
 */

package gov.sandia.cognition.learning.algorithm.pca;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.function.kernel.DefaultKernelContainer;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorOutputEvaluator;
import gov.sandia.cognition.math.matrix.decomposition.EigenDecomposition;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrix;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.decomposition.EigenDecompositionRightMTJ;
import gov.sandia.cognition.util.ArgumentChecker;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An implementation of the Kernel Principal Components Analysis (KPCA)
 * algorithm. KPCA generalizes the standard PCA for use with a Mercer kernel.
 * Thus, it can take a kernel function and data and come up with vector
 * principal components for it. This allows a transform to be made for the
 * arbitrary data from the kernel to some vector space.
 *
 * The implementation uses a closed-form solution based on an
 * eigen-decomposition of the (centered) kernel matrix. Doing so does require
 * computing the whole kernel matrix, which means that it is a computationally
 * intensive algorithm, that scales in O(n^2) where n is the size of the data.
 * Thus, this analysis may not scale well to large datasets.
 *
 * @param   <DataType>
 *      The type of data that the analysis is to be done over. It must match
 *      the input type of the kernel function that is given.
 * @author  Justin Basilico
 * @since   3.1
 */
@PublicationReferences(references={
    @PublicationReference(
        author={"Bernard Scholkopf", "Alexander Smola", "Klaus-Robert Muller"},
        title="Nonlinear Component Analysis as a Kernel Eigenvalue Problem",
        year=1996,
        type=PublicationType.TechnicalReport,
        url="http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.29.1366"),
    @PublicationReference(
        author={"John  Shawe-Taylor", "Nello Christianini"},
        title="Kernel Methods for Pattern Analysis",
        year=2004,
        type=PublicationType.Book,
        pages={150, 153})})
public class KernelPrincipalComponentsAnalysis<DataType>
    extends DefaultKernelContainer<DataType>
    implements BatchLearner<Collection<? extends DataType>, KernelPrincipalComponentsAnalysis.Function<DataType>>
{

    /** The default number of components to create is {@value}. */
    public static final int DEFAULT_COMPONENT_COUNT = 10;

    /** The default setting for centering data is {@value}. */
    public static final boolean DEFAULT_CENTER_DATA = true;

    /** The number of components to create from the analysis. Must be positive.
     */
    protected int componentCount;

    /** Whether or not the data should be centered before doing KPCA. */
    protected boolean centerData;

    /**
     * Creates a new Kernel Principal Components Analysis with a null kernel
     * and a default component count.
     */
    public KernelPrincipalComponentsAnalysis()
    {
        this(null, DEFAULT_COMPONENT_COUNT);
    }

    /**
     * Creates a new Kernel Principal Components Analysis with the given
     * kernel and component count. It will perform centering.
     *
     * @param   kernel
     *      The kernel to use in the analysis.
     * @param   componentCount
     *      The number of components for the analysis to create.
     *      Must be positive.
     */
    public KernelPrincipalComponentsAnalysis(
        final Kernel<? super DataType> kernel,
        final int componentCount)
    {
        this(kernel, componentCount, DEFAULT_CENTER_DATA);
    }

    /**
     * Creates a new Kernel Principal Components Analysis with the given
     * kernel and component count.
     *
     * @param   kernel
     *      The kernel to use in the analysis.
     * @param   componentCount
     *      The number of components for the analysis to create.
     *      Must be positive.
     * @param   centerData
     *      True to center the data in the input space before applying the
     *      analysis. Only set this to false if the data is pre-centered. If
     *      in doubt, set to true.
     */
    public KernelPrincipalComponentsAnalysis(
        final Kernel<? super DataType> kernel,
        final int componentCount,
        final boolean centerData)
    {
        super(kernel);

        this.setComponentCount(componentCount);
        this.setCenterData(centerData);
    }

    @Override
    public Function<DataType> learn(
        final Collection<? extends DataType> data)
    {
        final int dataSize = data.size();
        final ArrayList<? extends DataType> dataList =
            CollectionUtil.asArrayList(data);
        final DenseMatrix kernelMatrix =
            new DenseMatrixFactoryMTJ().createMatrix(dataSize, dataSize);
        for (int i = 0; i < dataSize; i++)
        {
            final DataType x = dataList.get(i);
            kernelMatrix.setElement(i, i, this.kernel.evaluate(x, x));
            for (int j = i + 1; j < dataSize; j++)
            {
                final DataType y = dataList.get(j);
                final double value = this.kernel.evaluate(x, y);
                kernelMatrix.setElement(i, j, value);
                kernelMatrix.setElement(j, i, value);
            }
        }

        // Center the data, if needed.
        final DenseMatrix k;
        if (!this.centerData)
        {
            // When not centering, just use the kernel matrix.
            k = kernelMatrix;
        }
        else
        {
            // Center the data before applying the analysis.
            
            // Khat = K - 1_m K - K 1_m + 1_m K 1_m
            // Where m is the data size
            // and 1_m is a m x m with 1/m on the diagonal
            // and K is the  kernel m x m matrix
            final Matrix centeringTerm =
                MatrixFactory.getDiagonalDefault().createIdentity(
                    dataSize, dataSize);
            centeringTerm.scaleEquals(1.0 / dataSize);

            k = kernelMatrix.clone();
            k.minusEquals(centeringTerm.times(kernelMatrix));
            k.minusEquals(kernelMatrix.times(centeringTerm));
            k.plusEquals(centeringTerm.times(kernelMatrix.times(centeringTerm)));
        }

        // We can only have up to dataSize components.
        final int realComponentCount = Math.min(this.componentCount, dataSize);

        // Perform an eigendecomposition on the kernel matrix.
        final EigenDecomposition decomposition =
            EigenDecompositionRightMTJ.create(k);

        // Now we need to take the result from the eigen-decomposition and
        // transform it into a form we can use to transform data.
        final Matrix components = MatrixFactory.getDenseDefault().createMatrix(
            realComponentCount, dataSize);
        for (int i = 0; i < realComponentCount; i++)
        {
            // Get the i-th eigenvector and eigenvalue.
            final Vector eigenVector =
                decomposition.getEigenVectorsRealPart().getColumn(i);
            final double eigenValue = decomposition.getEigenValue(i).getRealPart();

            // Create the component by scaling the eigenvector by one over
            // the square root of the eigenvalue.
            final Vector component = eigenVector.scale(
                1.0 / Math.sqrt(Math.abs(eigenValue)));
            
            components.setRow(i, component);
        }

        // Return the result.
        return new Function<DataType>(this.kernel, dataList, components,
            this.centerData, kernelMatrix);
    }

    /**
     * Gets the number of components the analysis attempts to find. If there
     * are less data points than the number of components, then the number of
     * data points is used instead.
     *
     * @return
     *      The number of components for the analysis. Must be positive.
     */
    public int getComponentCount()
    {
        return this.componentCount;
    }

    /**
     * Gets the number of components the analysis attempts to find. If there
     * are less data points than the number of components, then the number of
     * data points is used instead.
     *
     * @param   componentCount
     *      The number of components for the analysis. Must be positive.
     */
    public void setComponentCount(
        final int componentCount)
    {
        ArgumentChecker.assertIsPositive("componentCount", componentCount);
        this.componentCount = componentCount;
    }

    /**
     * Gets whether or not the data needs to be centered in the kernel space
     * before applying the algorithm. Only set this to false if the data has
     * been pre-centered. If in doubt, set it to true.
     *
     * @return
     *      True if the algorithm will apply to the centered version of the
     *      input data. False if it will just apply directly to the given
     *      data.
     */
    public boolean isCenterData()
    {
        return this.centerData;
    }

    /**
     * Sets whether or not the data needs to be centered in the kernel space
     * before applying the algorithm. Only set this to false if the data has
     * been pre-centered. If in doubt, set it to true.
     *
     * @param   centerData
     *      True if the algorithm will apply to the centered version of the
     *      input data. False if it will just apply directly to the given
     *      data.
     */
    public void setCenterData(
        final boolean centerData)
    {
        this.centerData = centerData;
    }

    /**
     * The resulting transformation function learned by Kernel Principal
     * Components Analysis. It can take any data item from the input space and
     * return a vector version of that item transformed into the principal
     * components space.
     *
     * @param   <DataType>
     *      The type of data used with the kernel that the function can be
     *      applied to.
     */
    public static class Function<DataType>
        extends DefaultKernelContainer<DataType>
        implements VectorOutputEvaluator<DataType, Vector>
    {

        /** The data that the KPCA was performed over. Each one corresponds to
         *  a column in the components matrix. */
        protected List<? extends DataType> data;

        /** The matrix of components for the function. The number of rows is
         *  the dimensionality of the reduction. The number of columns is equal
         *  to the number of data points that the KPCA was done over. */
        protected Matrix components;

        /** A flag indicating if the incoming data needs to be centered or not.
         *  Unless the data is being pre-centered, this should be true. */
        protected boolean centerData;

        /** The kernel matrix for all the data the KPCA was done over. It is
         *  a square matrix whose size is equal to the data. */
        protected Matrix kernelMatrix;

        /**
         * Creates a new, empty Kernel Principal Components Analysis function.
         */
        public Function()
        {
            this(null, null, null, DEFAULT_CENTER_DATA, null);
        }

        /**
         * Creates a new Kernel Principal Components Analysis function.
         *
         * @param   kernel
         *      The kernel to use.
         * @param   data
         *      The base data used in the KPCA analysis.
         * @param   components
         *      The matrix of components for the function.
         * @param   centerData
         *      True to center the data. Must correspond to whether or not
         *      the analysis was done with centered data.
         * @param   kernelMatrix
         *      The kernel matrix. Must be provided if data centering is
         *      true.
         */
        public Function(
            final Kernel<? super DataType> kernel,
            final List<? extends DataType> data,
            final Matrix components,
            final boolean centerData,
            final Matrix kernelMatrix)
        {
            super(kernel);
            
            this.setData(data);
            this.setComponents(components);
            this.setCenterData(centerData);
            this.setKernelMatrix(kernelMatrix);
        }

        @Override
        public Vector evaluate(
            final DataType input)
        {
            final int dataSize = this.data.size();

            // Create the kernel vector.
            final Vector kernelVector =
                VectorFactory.getDenseDefault().createVector(dataSize);
            int index = 0;
            for (DataType other : this.data)
            {
                final double value = this.kernel.evaluate(input, other);
                kernelVector.setElement(index, value);
                index++;
            }

            // Transform the kernel vector, if needed.
            final Vector kInput;
            if (!this.centerData || this.kernelMatrix == null)
            {
                // In the case we don't need to center the data just use the
                // kernel vector directly.
                kInput = kernelVector;
            }
            else
            {
                // Center the input before applying the transform.

                // Kt2 = Kt - 1'_m K - Kt 1_m + 1'_m K 1_m
                // Where 1_m is a m x m with 1/m on the diagonal
                // and 1'_m is an m-dimensional vector filled with 1/m
                // and m is the data size
                // and K is the original kernel m x m matrix
                // and Kt is the m-dimensional vector of K(t, x_i)
                final Matrix centeringMatrix = 
                    MatrixFactory.getDiagonalDefault().createIdentity(
                        dataSize, dataSize);
                centeringMatrix.scaleEquals(1.0 / dataSize);
                final Vector centeringVector =
                    VectorFactory.getDenseDefault().createVector(
                        dataSize, 1.0 / dataSize);

                kInput = kernelVector.clone();
                kInput.minusEquals(centeringVector.times(this.kernelMatrix));
                kInput.minusEquals(kernelVector.times(centeringMatrix));
                kInput.plusEquals(centeringVector.times(
                    this.kernelMatrix.times(centeringMatrix)));
            }
            
            return this.components.times(kInput);
        }

        @Override
        public int getOutputDimensionality()
        {
            return this.components.getNumRows();
        }

        /**
         * Gets the number of components in the analysis result.
         *
         * @return
         *      The number of components in the analysis result.
         */
        public int getComponentCount()
        {
            return this.components.getNumRows();
        }

        /**
         * Gets the data that was used in the analysis. It is still required for
         * applying the transform.
         *
         * @return
         *      The data that was used in the analysis.
         */
        public List<? extends DataType> getData()
        {
            return this.data;
        }

        /**
         * Sets the data that was used in the analysis. It is still required for
         * applying the transform.
         *
         * @param   data
         *      The data that was used in the analysis.
         */
        public void setData(
            final List<? extends DataType> data)
        {
            this.data = data;
        }

        /**
         * Gets the matrix of components from the analysis. The number of rows
         * equals the number of components. The number of columns must equal
         * the size of the data.
         *
         * @return
         *      The component matrix.
         */
        public Matrix getComponents()
        {
            return this.components;
        }

        /**
         * Sets the matrix of components from the analysis. The number of rows
         * equals the number of components. The number of columns must equal
         * the size of the data.
         *
         * @param   components
         *      The component matrix.
         */
        public void setComponents(
            final Matrix components)
        {
            this.components = components;
        }

        /**
         * Gets whether or not the data needs to be centered in the kernel space
         * before applying the function. Only set this to false if the data has
         * been pre-centered. If in doubt, set it to true. If this is set to
         * true, the kernel matrix must be provided for the centering to be
         * performed.
         *
         * @return
         *      True if the function will apply to the centered version of the
         *      input data. False if it will just apply directly to the given
         *      data.
         */
        public boolean isCenterData()
        {
            return this.centerData;
        }

        /**
         * Sets whether or not the data needs to be centered in the kernel space
         * before applying the function. Only set this to false if the data has
         * been pre-centered. If in doubt, set it to true. If this is set to
         * true, the kernel matrix must be provided for the centering to be
         * performed.
         *
         * @param   centerData
         *      True if the function will apply to the centered version of the
         *      input data. False if it will just apply directly to the given
         *      data.
         */
        public void setCenterData(
            final boolean centerData)
        {
            this.centerData = centerData;
        }

        /**
         * Gets the kernel matrix for the data that the analysis was done over.
         * If the data needs to be centered, then this matrix must be provided.
         * Each dimension corresponds to an element in the data list.
         *
         * @return
         *      The data kernel matrix.
         */
        public Matrix getKernelMatrix()
        {
            return this.kernelMatrix;
        }

        /**
         * Sets the kernel matrix for the data that the analysis was done over.
         * If the data needs to be centered, then this matrix must be provided.
         * Each dimension corresponds to an element in the data list.
         *
         * @param   kernelMatrix
         *      The data kernel matrix.
         */
        public void setKernelMatrix(
            final Matrix kernelMatrix)
        {
            this.kernelMatrix = kernelMatrix;
        }

    }
    
}
