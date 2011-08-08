/*
 * File:                DummySelector.java
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

package gov.sandia.cognition.learning.algorithm.genetic.selector;

import gov.sandia.cognition.learning.algorithm.genetic.EvaluatedGenome;
import java.util.Collection;

/**
 * The DummySelector is a Selector that does nothing but track the number of
 * times select has been called since the object was created.
 *
 * @author Jonathan McClain
 * @since 1.0
 */
public class DummySelector
        extends AbstractSelector<Integer>
{
    /** The number of times select has been called. */
    private int selectCount = 0;

    /**
    * Creates a new instance of DummySelector.
    */
    public DummySelector()
    {
        super();
    }

    /**
     * Increments select count and returns the exact genomes it was given.
     *
     * @param genomes The genomes to select.
     * @return The exact genomes it was given.
     */
    public Collection<EvaluatedGenome<Integer>> select(
            Collection<EvaluatedGenome<Integer>> genomes) 
    {
        this.setSelectCount(this.getSelectCount() + 1);
        return genomes;
    }

    /**
     * Gets the number of times select has been called.
     *
     * @return The number of times select has been called.
     */
    public int getSelectCount()
    {
        return this.selectCount;
    }

    /**
     * Sets the number of times select has been called.
     *
     * @param selectCount The new number of times select has been called.
     */
    private void setSelectCount(
        int selectCount)
    {
        this.selectCount = selectCount;
    }
}
