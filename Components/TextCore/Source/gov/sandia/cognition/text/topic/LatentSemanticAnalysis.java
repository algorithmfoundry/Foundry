/*
 * File:                LatentSemanticAnalysis.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 03, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.topic;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.math.matrix.VectorOutputEvaluator;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.decomposition.SingularValueDecomposition;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrix;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.decomposition.SingularValueDecompositionMTJ;
import gov.sandia.cognition.text.topic.LatentSemanticAnalysis.Transform;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;

/**
 * Implements the Latent Semantic Analysis (LSA) algorithm using Singular Value
 * Decomposition (SVD).
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author={"Scott Deerwester", "Susan T. Dumais", "George W. Furnas", "Thomas K. Landauer", "Richard Harshman"},
            title="Indexing by Latent Semantic Analysis",
            year=1990,
            type=PublicationType.Journal,
            publication="Journal of the American Society for Information Science",
            pages={391, 407},
            url="http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.108.8490"
        ),
        @PublicationReference(
            author={"Thomas K. Landauer", "Peter W. Foltz", "Darrell Laham"},
            title="An Introduction to Latent Semantic Analysis",
            year=1998,
            type=PublicationType.Journal,
            publication="Discourse Processes",
            pages={259, 284},
            url="http://lsa.colorado.edu/papers/dp1.LSAintro.pdf"
        ),
        @PublicationReference(
            author="Wikipedia",
            title="Latent semantic analysis",
            year=2009,
            type=PublicationType.WebPage,
            url="http://en.wikipedia.org/wiki/Latent_semantic_analysis"
        )
    }
)
public class LatentSemanticAnalysis
    extends AbstractCloneableSerializable
    implements BatchLearner<Collection<? extends Vectorizable>, Transform>
{
// TODO: Implement an iterative LSA that allows documents to be added and removed.
// TODO: Implement a sparse LSA.

    /** The default requested rank is {@value}. */
    public static final int DEFAULT_REQUESTED_RANK = 10;

    /** The rank requested for the result LSA. The results may have smaller rank
     *  if the requested rank is greater than the number of documents. Must
     *  be positive.
     */
    protected int requestedRank;

    /**
     * Creates a new {@code LatentSemanticAnalysis} with default parameters.
     */
    public LatentSemanticAnalysis()
    {
        this(DEFAULT_REQUESTED_RANK);
    }

    /**
     * Creates a new {@code LatentSemanticAnalysis} with the given parameters.
     *
     * @param   requestedRank
     *      The requested rank to create results of.
     */
    public LatentSemanticAnalysis(
        final int requestedRank)
    {
        super();

        this.setRequestedRank(requestedRank);
    }

    public Transform learn(
        final Collection<? extends Vectorizable> documents)
    {
        // Get the dimensionality of the documents. This is also the number of
        // terms in the documents.
        final int dimensionality =
            CollectionUtil.getFirst(documents).convertToVector().getDimensionality();

        // Create the input matrix for SVD by stacking the documents as column
        // vectors.
        final DenseMatrix inputMatrix =
            DenseMatrixFactoryMTJ.INSTANCE.copyColumnVectors(documents);

        // Perform SVD on the matrix.
// TODO: Do a thin SVD to only take up to the requested rank number of values.
        final SingularValueDecomposition svd =
            SingularValueDecompositionMTJ.create(inputMatrix);

        // Get the singular values and term basis from the SVD.
        // The singular values are a diagonal matrix that have the different
        // singular values on the diagonal.
        // The term basis is the matrix of orthogonal term columns
        Matrix singularValues = svd.getS();
        Matrix termBasis = svd.getU();

        // Figure out if we need to downselect the number of rows and columns.
        // This happens if
        final boolean filterRows =
            this.getRequestedRank() < singularValues.getNumRows();
        final boolean filterColumns =
            this.getRequestedRank() < singularValues.getNumColumns();

        if (filterRows || filterColumns)
        {
            // Change the diagonal to be the proper size.
            final int newRows =
                Math.min(singularValues.getNumRows(), this.getRequestedRank());
            final int newCols =
                Math.min(singularValues.getNumRows(), this.getRequestedRank());
            singularValues = singularValues.getSubMatrix(
                0, newRows - 1,
                0, newCols - 1);
        }

        if (filterRows)
        {
            // Change the term basis to only include the proper rank of values.
            termBasis = termBasis.getSubMatrix(
                0, dimensionality - 1,
                0, this.getRequestedRank() - 1);
        }

        // Create learned result.
        return new Transform(termBasis, singularValues);
    }

    /**
     * Gets the requested rank for the analysis.
     *
     * @return
     *      The requested rank for the analysis.
     */
    public int getRequestedRank()
    {
        return this.requestedRank;
    }

    /**
     * Sets the requested rank of the analysis. The analysis will attempt to
     * find the requested number of latent topics. If the number of documents
     * is less than the requested rank, the actual rank of the analysis will
     * be reduced to the number of documents.
     *
     * @param   requestedRank
     *      The requested rank of the analysis. Must be positive.
     */
    public void setRequestedRank(
        final int requestedRank)
    {
        if (requestedRank <= 0)
        {
            throw new IllegalArgumentException("requestedRank must be positive.");
        }

        this.requestedRank = requestedRank;
    }

    /**
     * The result from doing latent semantic analysis (LSA). It is a transform
     * that can be applied as a dimensionality reduction.
     */
    public static class Transform
        extends AbstractCloneableSerializable
        implements Evaluator<Vectorizable, Vector>,
        VectorInputEvaluator<Vectorizable, Vector>,
        VectorOutputEvaluator<Vectorizable, Vector>
    {

        /** The matrix of orthogonal term column vectors. */
        protected Matrix termBasis;

        /** The diagonal matrix of singular values. */
        protected Matrix singularValues;

        /** The cached transform matrix. It is the term basis times the
         *  singular values. */
        protected Matrix transform;

        /**
         * Create a new {@code Transform}
         *
         * @param   termBasis
         *      The matrix of orthogonal term column vectors.
         * @param   singularValues
         *      The diagonal matrix of singular values.
         */
        public Transform(
            final Matrix termBasis,
            final Matrix singularValues)
        {
            super();

            this.termBasis = termBasis;
            this.singularValues = singularValues;
            this.setTransform(termBasis.times(singularValues));
        }

        public Vector evaluate(
            final Vectorizable input)
        {
            // Apply the transform to the input vector.
            return input.convertToVector().times(this.transform);
        }

        public int getInputDimensionality()
        {
            return this.transform.getNumRows();
        }

        public int getOutputDimensionality()
        {
            return this.transform.getNumColumns();
        }

        /**
         * Gets the rank of the LSA. This is equivalent to the output
         * dimensionality of the transform.
         *
         * @return
         *      The rank of the LSA.
         */
        public int getRank()
        {
            return this.getOutputDimensionality();
        }

        /**
         * Gets the i-th orthogonal term vector that makes up the basis for
         * the transform.
         *
         * @param   i
         *      An index. Must be between 0 (inclusive) and rank (exclusive).
         * @return
         *      The i-th orthogonal term vector.
         */
        public Vector getTermVector(
            final int i)
        {
            return this.termBasis.getColumn(i);
        }

        /**
         * Gets the matrix of orthogonal term column vectors.
         *
         * @return
         *      The matrix of orthogonal term column vectors.
         */
        public Matrix getTermBasis()
        {
            return this.termBasis;
        }

        /**
         * Sets the matrix of orthogonal term column vectors.
         *
         * @param   termBasis
         *      The matrix of orthogonal term column vectors.
         */
        protected void setTermBasis(
            final Matrix termBasis)
        {
            this.termBasis = termBasis;
        }

        /**
         * Gets the diagonal matrix of singular values.
         *
         * @return
         *      The diagonal matrix of singular values.
         */
        public Matrix getSingularValues()
        {
            return this.singularValues;
        }

        /**
         * Sets the diagonal matrix of singular values.
         *
         * @param   singularValues
         *      The diagonal matrix of singular values.
         */
        protected void setSingularValues(
            final Matrix singularValues)
        {
            this.singularValues = singularValues;
        }

        /**
         * Gets the cached transform matrix. It is the term basis times the
         *  singular values.
         *
         * @return
         *      The cached transform matrix.
         */
        public Matrix getTransform()
        {
            return this.transform;
        }

        /**
         * Gets the cached transform matrix. It is the term basis times the
         *  singular values.
         *
         * @param   transform
         *      The cached transform matrix.
         */
        protected void setTransform(
            final Matrix transform)
        {
            this.transform = transform;
        }

    }

}
