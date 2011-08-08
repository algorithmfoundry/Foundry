/*
 * File:                DummyReproducer.java
 * Authors:             Jonathan McClain
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright May 10, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.genetic;

import gov.sandia.cognition.learning.algorithm.genetic.reproducer.Reproducer;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The DummyReproducer is a Reproducer that does nothing but track how many
 * times reproduce has been called.
 *
 * @author Jonathan McClain
 * @since 1.0
 */
public class DummyReproducer
        implements Reproducer<Integer>
{
    /** The number of times reproduce has been called. */
    private int reproduceCount = 0;
    
    /**
     * Creates a new instance of DummyReproducer.
     */
    public DummyReproducer()
    {
        super();
    }

    /**
     * Increments the number of times reproduce has been called and returns the
     * same genomes it was given unpacked from their EvaluatedGenome wrapper.
     *
     * @param genomes The genomes to reproduce.
     * @return The same genomes unpacked from their EvaluatedGenome wrapper.
     */
    public Collection<Integer> reproduce(
        Collection<EvaluatedGenome<Integer>> genomes)
    {
        this.setReproduceCount(this.getReproduceCount() + 1);
        ArrayList<Integer> newGenomes = new ArrayList<Integer>(genomes.size());
        for(EvaluatedGenome<Integer> genome : genomes)
        {
            newGenomes.add(genome.getGenome());
        }
        return newGenomes;
    }
    
    /**
     * Gets the number of times reproduce has been called.
     *
     * @return The number of times reproduce has been called.
     */
    public int getReproduceCount()
    {
        return this.reproduceCount;
    }
    
    /**
     * Sets the number of times reproduce has been called.
     *
     * @param reproduceCount The new number of times reproduce has been called.
     */
    public void setReproduceCount(
        int reproduceCount)
    {
        this.reproduceCount = reproduceCount;
    }
}
