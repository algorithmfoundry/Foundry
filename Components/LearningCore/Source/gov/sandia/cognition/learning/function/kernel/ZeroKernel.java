/*
 * File:                ZeroKernel.java
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
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * The <code>ZeroKernel</code> always returns zero. On its own it is not useful,
 * but it is useful in combination with some other kernels when there
 * is some aspect of the data that needs to be ignored.
 *
 * @author Justin Basilico
 * @since  2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2009-07-08",
    changesNeeded=false,
    comments="Looks fine."
)
public final class ZeroKernel
    extends AbstractCloneableSerializable
    implements Kernel<Object>
{

    /** A static instance of this class because it has no internal fields. */
    private static final ZeroKernel INSTANCE = new ZeroKernel();

    /**
     * Gets the single instance of this class because it has no internal data.
     *
     * @return The instance of the singleton.
     */
    public static ZeroKernel getInstance()
    {
        return INSTANCE;
    }

    /**
     * Creates a new instance of ZeroKernel.
     */
    public ZeroKernel()
    {
        super();
    }

    @Override
    public ZeroKernel clone()
    {
        return (ZeroKernel) super.clone();
    }

    /**
     * Returns zero regardless of the input values.
     *
     * @param  x The first item.
     * @param  y The second item.
     * @return Zero.
     */
    public double evaluate(
        final Object x,
        final Object y)
    {
        return 0.0;
    }

}
