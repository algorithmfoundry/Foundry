/*
 * File:                LinearKernel.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 19, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.kernel;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * The <code>LinearKernel</code> class implements the most basic kernel: it just
 * does the actual inner product between two vectors.
 *
 * @author Justin Basilico
 * @since  2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2009-07-08",
    changesNeeded=false,
    comments={
        "Made clone call super.clone.",
        "Looks fine otherwise."
    }
)
public class LinearKernel
    extends AbstractCloneableSerializable
    implements Kernel<Vectorizable>
{
    /** A static instance of this class because it has no internal fields. */
    private static final LinearKernel INSTANCE = new LinearKernel();
    
    /**
     * Gets the single instance of this class because it has no internal data.
     *
     * @return The instance of the singleton.
     */
    public static LinearKernel getInstance()
    {
        return INSTANCE;
    }
    
    /**
     * Creates a new instance of LinearKernel.
     */
    public LinearKernel()
    {
        super();
    }
    
    @Override
    public LinearKernel clone()
    {
        return (LinearKernel) super.clone();
    }
    
    /**
     * Evaluates the linear kernel by taking the inner product of the two
     * vectors.
     *
     * @param  x The first vector.
     * @param  y The second vector.
     * @return The dot product of the two vectors.
     */
    public double evaluate(
        final Vectorizable x,
        final Vectorizable y)
    {
        return x.convertToVector().dotProduct(y.convertToVector());
    }
    
}
