/*
 * File:                ThinSingularValueDecomposition.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 9, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.pca;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.function.vector.MatrixMultiplyVectorFunction;
import gov.sandia.cognition.math.MultivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.decomposition.EigenvectorPowerIteration;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Computes the "thin" singular value decomposition of a dataset.  That is,
 * we find the top "numComponents" left singular values of the data matrix by
 * using the {@code EigenvectorPowerIteration} algorithm to find successive
 * components. This method is extremely fast to converge, produces accurate
 * estimates of eigenvectors, and is computationally and memory efficient.
 * In my experience, this approach has been uniformly superior to the 
 * {@code GeneralizedHebbianAlgorithm} approach to computing singular vectors
 * (in terms of accuracy, memory, and computation time).
 *
 * @see gov.sandia.cognition.math.matrix.decomposition.EigenvectorPowerIteration
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-23",
    changesNeeded=false,
    comments={
        "Minor changes to javadoc.",
        "Looks fine."
    }
)
public class ThinSingularValueDecomposition
    extends AbstractPrincipalComponentsAnalysis
{

    /**
     * Creates a new instance of ThinSingularValueDecomposition
     * @param numComponents
     * Number of components to extract from the data, must be greater than zero
     */
    public ThinSingularValueDecomposition(
        final int numComponents)
    {
        this(numComponents, null);
    }

    /**
     * Creates a new instance of ThingSingularValueDecomposition
     * @param numComponents 
     * Number of components to extract from the data, must be greater than zero
     * @param learned 
     * Vector function that maps the input space onto a numComponents-dimension
     * Vector representing the directions of maximal variance (information
     * gain).  The i-th row in the matrix approximates the i-th column of the
     * "U" matrix of the Singular Value Decomposition.
     */
    public ThinSingularValueDecomposition(
        final int numComponents,
        final PrincipalComponentsAnalysisFunction learned)
    {
        super(numComponents, learned);
    }

    /**
     * Creates a PrincipalComponentsAnalysisFunction based on the number of
     * components and the given data.  This will return the top "numComponents"
     * number of left eigenvectors of the data.
     * @param data 
     * Dataset of which compute the PCA, with each Vector of equal dimension
     * @return 
     * Vector function that maps the input space onto a numComponents-dimension
     * Vector representing the directions of maximal variance (information
     * gain).  The i-th row in the matrix approximates the i-th column of the
     * "U" matrix of the Singular Value Decomposition.
     */
    public PrincipalComponentsAnalysisFunction learn(
        final Collection<Vector> data)
    {
        PrincipalComponentsAnalysisFunction pca =
            ThinSingularValueDecomposition.learn(data, this.getNumComponents());
        this.setResult(pca);
        return pca;
    }

    /**
     * Creates a PrincipalComponentsAnalysisFunction based on the number of
     * components and the given data.  This will return the top "numComponents"
     * number of left eigenvectors of the data.
     * @param data 
     * Dataset of which compute the PCA, with each Vector of equal dimension
     * @param numComponents 
     * Number of components to extract from the data, must be greater than zero
     * @return 
     * Vector function that maps the input space onto a numComponents-dimension
     * Vector representing the directions of maximal variance (information
     * gain).  The i-th row in the matrix approximates the i-th column of the
     * "U" matrix of the Singular Value Decomposition.
     */
    public static PrincipalComponentsAnalysisFunction learn(
        final Collection<Vector> data,
        final int numComponents)
    {

        final Vector mean = MultivariateStatisticsUtil.computeMean(data);
        final ArrayList<Vector> dataArray = new ArrayList<Vector>(data.size());
        for (Vector x : data)
        {
            dataArray.add(x.minus(mean));
        }

        Matrix XXt = DatasetUtil.computeOuterProductDataMatrix(dataArray);

        ArrayList<Vector> components =
            EigenvectorPowerIteration.estimateEigenvectors(XXt, numComponents);

        Matrix V = MatrixFactory.getDefault().createMatrix(
            components.size(), mean.getDimensionality());
        for (int i = 0; i < components.size(); i++)
        {
            V.setRow(i, components.get(i));
        }

        return new PrincipalComponentsAnalysisFunction(
            mean, new MatrixMultiplyVectorFunction(V));

    }

}
