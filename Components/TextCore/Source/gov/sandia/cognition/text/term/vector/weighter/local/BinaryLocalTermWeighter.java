/*
 * File:                BinaryLocalWeighter.java
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

import gov.sandia.cognition.math.matrix.SparseVectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorEntry;
import gov.sandia.cognition.math.matrix.VectorFactory;

/**
 * Makes the given term weights binary, by creating a vector that contains a
 * 1.0 for all non-zero entries in the given vector and a 0.0 for the all the
 * zeros. Used to indicate that a given term exists in a document, ignoring the
 * frequency of occurrence.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class BinaryLocalTermWeighter
    extends AbstractLocalTermWeighter
{

    /**
     * Creates a new {@code BinaryLocalTermWeighter}.
     */
    public BinaryLocalTermWeighter()
    {
        this(SparseVectorFactory.getDefault());
    }

    /**
     * Creates a new {@code BinaryLocalTermWeighter}.
     *
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public BinaryLocalTermWeighter(
        final VectorFactory<? extends Vector> vectorFactory)
    {
        super(vectorFactory);
    }

    public Vector computeLocalWeights(
        final Vector counts)
    {
        // Create the vector to store the result.
        final Vector result = this.getVectorFactory().copyVector(counts);

        // Compute the local weight, which just turns the vector into a binary
        // form.
        for (VectorEntry entry : result)
        {
            final double value = entry.getValue();

            if (value != 0.0)
            {
                entry.setValue(1.0);
            }
        }

        return result;
    }

}
