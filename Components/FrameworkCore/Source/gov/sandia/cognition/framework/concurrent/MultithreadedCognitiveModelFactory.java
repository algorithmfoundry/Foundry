/*
 * File:                MultithreadedCognitiveModelFactory.java
 * Authors:             Zachary Benz
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 * 
 * Copyright Jan 9, 2008, Sandia Corporation.  Under the terms of Contract 
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by 
 * or on behalf of the U.S. Government. Export of this program may require a 
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 * 
 */
 
package gov.sandia.cognition.framework.concurrent;

import gov.sandia.cognition.framework.AbstractCognitiveModelFactory;

/**
 * This class defines a CognitiveModelFactory for creating 
 * MultithreadedCognitiveModel objects.
 *
 * @author Zachary Benz
 * @since 2.0
 */
public class MultithreadedCognitiveModelFactory
    extends AbstractCognitiveModelFactory
{
    /** Number of threads to use in the thread pool */
    private int numThreadsInPool;
    
    /** 
     * Creates a new instance of MultithreadedCognitiveModelFactory.
     * 
     * @param numThreadsInPool Number of threads to use in the thread pool
     */
    public MultithreadedCognitiveModelFactory(
        final int numThreadsInPool)
    {
        super();
        
        this.setNumThreadsInPool(numThreadsInPool);
    }
    
    /**
     * Creates a MultithreadedCognitiveModel using the CognitiveModuleFactories
     * that are part of the model factory.
     *
     * @return A new MultithreadedCognitiveModel using the module factories on
     *         this factory
     */
    public MultithreadedCognitiveModel createModel()
    {
        return new MultithreadedCognitiveModel(this.getNumThreadsInPool(),
            this.getModuleFactories());
    }

    /**
     * Returns the number of threads to use in the thread pool
     * 
     * @return Number of threads to use in thread pool
     */
    private int getNumThreadsInPool()
    {
        return numThreadsInPool;
    }

    /**
     * Sets the number of threads to use in the thread pool
     * 
     * @param numThreadsInPool Number of threads to use in thread pool
     */
    private void setNumThreadsInPool(int numThreadsInPool)
    {
        this.numThreadsInPool = numThreadsInPool;
    }

}
