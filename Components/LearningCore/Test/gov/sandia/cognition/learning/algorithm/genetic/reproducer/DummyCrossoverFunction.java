/*
 * File:                DummyCrossoverFunction.java
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

package gov.sandia.cognition.learning.algorithm.genetic.reproducer;

/**
 * The DummyCrossoverFunction is a CrossoverFunction that does nothing but track
 * how many times crossover has been called since the object was created.
 *
 * @author Jonathan McClain
 * @since 1.0
 */
public class DummyCrossoverFunction
        implements CrossoverFunction<Integer>
{
    /** The number of times crossover has been called. */
    public int crossoverCount = 0;
            
    /**
     * Creates a new instance of DummyCrossoverFunction.
     */
    public DummyCrossoverFunction()
    {
        super();
    }

    /**
     * Increments crossover count, and returns genome1.
     *
     * @param genome1 The first genome to crossover.
     * @param genome2 The second genome to crossover.
     * @return genome1.
     */
    public Integer crossover(
        Integer genome1, 
        Integer genome2) 
    {
        this.setCrossoverCount(this.getCrossoverCount() + 1);
        return genome1;
    }
    
    /**
     * Gets the number of times crossover has been called.
     *
     * @return The number of times crossover has been called.
     */
    public int getCrossoverCount()
    {
        return this.crossoverCount;
    }
    
    /**
     * Sets the number of times crossover has been called.
     *
     * @param crossoverCount The new number of times crossover has been called.
     */
    private void setCrossoverCount(
        int crossoverCount)
    {
        this.crossoverCount = crossoverCount;
    }
}
