/*
 * File:                TermFrequencyLocalTermWeighter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 20, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.vector.weighter.local;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.SparseVectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;

/**
 * Local weighting for term frequency. The input is assumed to be a vector of
 * the number of times a term appears in the document. If n_i,j is the number of
 * times term i appears in document j, the term frequency for term i in document
 * j is:
 *
 *     tf_(i,j) = n_(i,j) / (sum_k n_(k, j)
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
@PublicationReference
(
    author="Wikipedia",
    title="tf-idf",
    type=PublicationType.WebPage,
    url="http://en.wikipedia.org/wiki/tf-idf",
    year=2009
)
public class TermFrequencyLocalTermWeighter
    extends AbstractLocalTermWeighter
{

    /**
     * Creates a new {@code TermFrequencyLocalTermWeighter}.
     */
    public TermFrequencyLocalTermWeighter()
    {
        this(SparseVectorFactory.getDefault());
    }
    
    /**
     * Creates a new {@code LogLocalTermWeighter}.
     *
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public TermFrequencyLocalTermWeighter(
        final VectorFactory<? extends Vector> vectorFactory)
    {
        super(vectorFactory);
    }

    public Vector computeLocalWeights(
        final Vector counts)
    {
        // Since the counts are positive, the 1-norm of them is their sum.
        final Vector result = this.vectorFactory.copyVector(counts);
        final double countSum = counts.norm1();

        if (countSum != 0.0)
        {
            result.scaleEquals(1.0 / countSum);
        }

        return result;
    }
    
}
