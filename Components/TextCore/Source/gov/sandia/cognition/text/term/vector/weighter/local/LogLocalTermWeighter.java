/*
 * File:                LogLocalTermWeighter.java
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
import gov.sandia.cognition.math.matrix.VectorEntry;
import gov.sandia.cognition.math.matrix.VectorFactory;

/**
 * Implements the log-based local term weighting scheme. Takes in a vector of
 * term counts and for positive entries, it makes the weight log(1 + count).
 * Counts of zero (or less) are weighted as zero.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
@PublicationReference(
    author={"Susan T. Dumais"},
    title="Improving the retrieval of information from external sources",
    year=1991,
    type=PublicationType.Journal,
    publication="Behavior Research Methods, Instruments, and Computers",
    pages={229, 236},
    url="http://www.google.com/url?sa=t&source=web&ct=res&cd=1&url=http%3A%2F%2Fwww.psychonomic.org%2Fsearch%2Fview.cgi%3Fid%3D5145&ei=o7joSdGEHY-itgPLre3tAQ&usg=AFQjCNEvm6PZEL6_Hk3XThI6DQ-gGx9EnQ&sig2=-gjFzNroJQirwGtwjaJvgQ"
)
public class LogLocalTermWeighter
    extends AbstractLocalTermWeighter
{

    /**
     * Creates a new {@code LogLocalTermWeighter}.
     */
    public LogLocalTermWeighter()
    {
        this(SparseVectorFactory.getDefault());
    }

    /**
     * Creates a new {@code LogLocalTermWeighter}.
     *
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public LogLocalTermWeighter(
        final VectorFactory<? extends Vector> vectorFactory)
    {
        super(vectorFactory);
    }

    public Vector computeLocalWeights(
        final Vector counts)
    {
        // Create the vector to hold the result.
        final Vector result = this.getVectorFactory().copyVector(counts);

        // Compute the local weight, which is log(1 + count) for each term.
        for (VectorEntry entry : result)
        {
            final double value = entry.getValue();

            if (value > 0.0)
            {
                entry.setValue(Math.log(1.0 + value));
            }
            else if (value != 0.0)
            {
                entry.setValue(0.0);
            }

        }

        return result;
    }

}
