/*
 * File:                ParallelDirichletProcessMixtureModelTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 3, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.algorithm.ParallelUtil;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * Unit tests for ParallelDirichletProcessMixtureModelTest.
 *
 * @author krdixon
 */
public class ParallelDirichletProcessMixtureModelTest
    extends DirichletProcessMixtureModelTest
{

    /**
     * Tests for class ParallelDirichletProcessMixtureModelTest.
     * @param testName Name of the test.
     */
    public ParallelDirichletProcessMixtureModelTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public ParallelDirichletProcessMixtureModel<Vector> createInstance()
    {
        DirichletProcessMixtureModel<Vector> superInstance =
            super.createInstance();

        ParallelDirichletProcessMixtureModel<Vector> instance =
            new ParallelDirichletProcessMixtureModel<Vector>();
        instance.setThreadPool( ParallelUtil.createThreadPool(1) );
        instance.setUpdater( superInstance.getUpdater() );
        instance.setMaxIterations( superInstance.getMaxIterations() );
        instance.setBurnInIterations( superInstance.getMaxIterations() );
        instance.setIterationsPerSample( superInstance.getIterationsPerSample() );
        instance.setRandom( superInstance.getRandom() );
        return instance;
    }

    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        DirichletProcessMixtureModel<Vector> instance =
            new DirichletProcessMixtureModel<Vector>();
        assertNotNull( instance );
        assertNull( instance.getUpdater() );
        assertNull( instance.getRandom() );
        assertTrue( instance.getIterationsPerSample() >= 1 );
        assertTrue( instance.getNumInitialClusters() > 1 );
    }


}
