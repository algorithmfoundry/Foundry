/*
 * File:                TermVectorSimilarityNetworkCreator.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 18, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.relation;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.text.relation.SimilarityFunction;
import gov.sandia.cognition.text.term.TermIndex;
import gov.sandia.cognition.text.term.vector.CosineSimilarityFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;

/**
 * Creates term similarity networks by comparing vectors representing the
 * terms.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class TermVectorSimilarityNetworkCreator
    extends AbstractCloneableSerializable
{
// TODO: This should probably implement some kind of interface...

    /** The default effective zero value is {@value}. */
    public static final double DEFAULT_EFFECTIVE_ZERO = 0.0;

    /** The similarity function between term vectors used to determine the
     *  similarity between two terms. */
    protected SimilarityFunction<? super Vector, ? super Vector>
        similarityFunction;

    /** The value to treat as zero. Used to increase the sparseness of a
     *  similarity network. */
    protected double effectiveZero;

    /** The matrix factory to create the matrix that backs the similarity
     *  network. */
    protected MatrixFactory<? extends Matrix> matrixFactory;

    /**
     * Creates a new {@code TermVectorSimilarityNetworkCreator}.
     */
    public TermVectorSimilarityNetworkCreator()
    {
        this(CosineSimilarityFunction.getInstance());
    }

    /**
     * Creates a new {@code TermVectorSimilarityNetworkCreator}.
     *
     * @param similarityFunction
     *      The similarity function between term vectors used to determine the
     *      term similarity.
     */
    public TermVectorSimilarityNetworkCreator(
        final SimilarityFunction<? super Vector, ? super Vector> similarityFunction)
    {
        this(similarityFunction, DEFAULT_EFFECTIVE_ZERO);
    }

    /**
     * Creates a new {@code TermVectorSimilarityNetworkCreator}.
     *
     * @param similarityFunction
     *      The similarity function between term vectors used to determine the
     *      term similarity.
     * @param effectiveZero
     *      The effective value to treat as zero. Used to increase the
     *      sparseness of a similarity network.
     */
    public TermVectorSimilarityNetworkCreator(
        final SimilarityFunction<? super Vector, ? super Vector> similarityFunction,
        final double effectiveZero)
    {
        this(similarityFunction, effectiveZero, MatrixFactory.getDefault());
    }

    /**
     * Creates a new {@code TermVectorSimilarityNetworkCreator}.
     *
     * @param similarityFunction
     *      The similarity function between term vectors used to determine the
     *      term similarity.
     * @param effectiveZero
     *      The effective value to treat as zero. Used to increase the
     *      sparseness of a similarity network.
     * @param matrixFactory
     *      The matrix factory used to create the similarity matrix.
     */
    public TermVectorSimilarityNetworkCreator(
        final SimilarityFunction<? super Vector, ? super Vector> similarityFunction,
        final double effectiveZero,
        final MatrixFactory<? extends Matrix> matrixFactory)
    {
        this.similarityFunction = similarityFunction;
        this.effectiveZero = effectiveZero;
        this.matrixFactory = matrixFactory;
    }

    /**
     * Creates a new similarity network between the terms in the given
     * documents. First the document vectors are turned into a term-by-document
     * matrix. Then the similarity function in this object is used to calculate
     * the similarity between the column vectors representing each term to
     * populate a term-by-term matrix. The resulting matrix will be symmetric.
     *
     * @param   documents
     *      The term vectors for each document to calculate the similarity
     *      network from.
     * @param   termIndex
     *      The index of terms that was used to create the term vectors for
     *      each document.
     * @return
     *      A new similarity network for the terms in the given index
     *      calculated using the given vectors.
     */
    public MatrixBasedTermSimilarityNetwork create(
        final Collection<? extends Vectorizable> documents,
        final TermIndex termIndex)
    {
        final int termCount = termIndex.getTermCount();

        // Create a term-by-document matrix, since the given set of documents
        // are document-by-term.
        final Matrix termByDocumentMatrix =
            this.getMatrixFactory().copyColumnVectors(documents);

        // Create the matrix to hold the result.
        final Matrix similiarities =
            this.getMatrixFactory().createMatrix(termCount, termCount);

        // Go through the terms to compute term-to-term similarity.
        for (int i = 0; i < termCount; i++)
        {
            final Vector termIVector = termByDocumentMatrix.getRow(i);

            // We assume that sim(a, b) = sim(b, a) and only loop over the
            // upper diagonal of the matrix.
            for (int j = i; j < termCount; j++)
            {
                final Vector termJVector = termByDocumentMatrix.getRow(j);
                final double similarity = this.similarityFunction.evaluate(
                    termIVector, termJVector);

                if (Math.abs(similarity) > this.effectiveZero)
                {
                    similiarities.setElement(i, j, similarity);

                    if (i != j)
                    {
                        // For non-diagonal elements we set the similarity for
                        // the lower diagonal portion, since we are only looping
                        // over the upper diagonal.
                        similiarities.setElement(j, i, similarity);
                    }
                }
            }
        }

        // Return the resulting network backed by the similarity matrix.
        return new MatrixBasedTermSimilarityNetwork(termIndex, similiarities);
    }

    /**
     * Gets the similarity function between term vectors used to determine the
     * similarity between two terms.
     *
     * @return
     *      The similarity function.
     */
    public SimilarityFunction<? super Vector, ? super Vector> getSimilarityFunction()
    {
        return this.similarityFunction;
    }

    /**
     * Sets the similarity function between term vectors used to determine the
     * similarity between two terms.
     *
     * @param   similarityFunction
     *      The similarity function.
     */
    public void setSimilarityFunction(
        final SimilarityFunction<? super Vector, ? super Vector> similarityFunction)
    {
        this.similarityFunction = similarityFunction;
    }

    /**
     * Gets the value to treat as zero. Used to increase the sparseness of a
     * similarity network.
     *
     * @return
     *      The threshold to treat absolute values below as zero.
     */
    public double getEffectiveZero()
    {
        return this.effectiveZero;
    }

    /**
     * Sets the value to treat as zero. Used to increase the sparseness of a
     * similarity network.
     *
     * @param   effectiveZero
     *      The threshold to treat absolute values below as zero.
     */
    public void setEffectiveZero(
        final double effectiveZero)
    {
        if (effectiveZero < 0.0)
        {
            throw new IllegalArgumentException(
                "effectiveZero must be non-negative");
        }

        this.effectiveZero = effectiveZero;
    }

    /**
     * Gets the matrix factory to create the matrix that backs the similarity
     * network.
     *
     * @return
     *      The matrix factory.
     */
    public MatrixFactory<? extends Matrix> getMatrixFactory()
    {
        return this.matrixFactory;
    }

    /**
     * Sets the matrix factory to create the matrix that backs the similarity
     * network.
     *
     * @param   matrixFactory
     *      The matrix factory.
     */
    public void setMatrixFactory(
        final MatrixFactory<? extends Matrix> matrixFactory)
    {
        this.matrixFactory = matrixFactory;
    }

}
