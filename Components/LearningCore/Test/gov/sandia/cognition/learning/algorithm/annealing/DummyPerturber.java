/*
 * File:                DummyPerturber.java
 * Authors:             Jonathan McClain
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright May 9, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.annealing;

import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * The DummyPerturber is a Perturber that does nothing but track how many 
 * times perturb has been called since the object was created.
 *
 * @author Jonathan McClain
 * @since 1.0
 */
public class DummyPerturber
    extends AbstractCloneableSerializable
    implements Perturber<Integer>
{
    /** The number of times perturb has been called. */
    private int perturbCount = 0;

    /**
    * Creates a new instance of DummyPerturber.
    */
    public DummyPerturber()
    {
        super();
    }

    /**
     * Increments the number of times perturb has been called and returns
     * the exact input it was given.
     *
     * @param input The input to perturb.
     * @return The exact input it was given.
     */
    public Integer perturb(
        final Integer input) 
    {
        this.setPerturbCount(this.getPerturbCount() + 1);
        return input;
    }

    /**
     * Gets the number of times perturb has been called.
     *
     * @return The number of times perturb has been called.
     */
    public int getPerturbCount()
    {
        return this.perturbCount;
    }

    /**
     * Sets the number of times perturb has been called.
     *
     * @param perturbCount The new number of times perturb has been called.
     */
    private void setPerturbCount(
        int perturbCount)
    {
        this.perturbCount = perturbCount;
    }
}
