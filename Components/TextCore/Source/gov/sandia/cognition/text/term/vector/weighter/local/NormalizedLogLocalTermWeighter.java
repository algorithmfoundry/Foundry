/*
 * File:                NormalizedLogLocalTermWeighter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 22, 2009, Sandia Corporation.
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
 * Implements a normalized version of the log local weighter. It is similar
 * to the {@code LogLocalWeighter} except that it normalizes by the average
 * frequency. It takes in a vector of term counts and for positive entries, it
 * makes the weight log(1 + count) / log(1 + average) where average is the
 * average count across the whole document. Counts of zero (or less) are
 * weighted as zero.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
@PublicationReference(
    author={"Erica Chisholm", "Tamara G. Kolda"},
    title="New Term Weighting Formulas for the Vector Space Method in Information Retrieval",
    type=PublicationType.TechnicalReport,
    url="http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.40.3899",
    year=1999,
    notes="We use a slight modification of the formula for normalizing in this paper."
)
public class NormalizedLogLocalTermWeighter
    extends LogLocalTermWeighter
{

    /**
     * Creates a new {@code NormalizedLogLocalTermWeighter}.
     */
    public NormalizedLogLocalTermWeighter()
    {
        this(SparseVectorFactory.getDefault());
    }

    /**
     * Creates a new {@code NormalizedLogLocalTermWeighter}
     *
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public NormalizedLogLocalTermWeighter(
        final VectorFactory<? extends Vector> vectorFactory)
    {
        super(vectorFactory);
    }

    @Override
    public Vector computeLocalWeights(
        final Vector counts)
    {
        // Compute the local weights.
        final Vector result = super.computeLocalWeights(counts);

        final int dimensionality = result.getDimensionality();
        if (dimensionality != 0)
        {
            final double average = counts.norm1() / dimensionality;
            final double divisor = Math.log(1.0 + average);
            result.scaleEquals(1.0 / divisor);
        }

        return result;
    }


}
