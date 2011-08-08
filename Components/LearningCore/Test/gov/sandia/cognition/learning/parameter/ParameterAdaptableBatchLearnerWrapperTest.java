/*
 * File:                ParameterAdaptableBatchLearnerWrapperTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright October 03, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.parameter;

import gov.sandia.cognition.learning.algorithm.clustering.KMeansClusterer;
import gov.sandia.cognition.learning.algorithm.clustering.KMeansFactory;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.VectorMeanCentroidClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.CentroidClusterDivergenceFunction;
import gov.sandia.cognition.learning.algorithm.clustering.initializer.GreedyClusterInitializer;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for class ParameterAdaptableBatchLearnerWrapper.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class ParameterAdaptableBatchLearnerWrapperTest
    extends TestCase
{
    protected Random random = new Random(1);
    
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public ParameterAdaptableBatchLearnerWrapperTest(
        String testName)
    {
        super(testName);        
    }

    /**
     * Test of constructors of class ParameterAdaptableBatchLearnerWrapper.
     */
    public void testConstructors()
    {
        KMeansClusterer<String, CentroidCluster<String>> learner = null;
        ParameterAdaptableBatchLearnerWrapper<Collection<? extends String>, Collection<CentroidCluster<String>>, KMeansClusterer<String, CentroidCluster<String>>>
            instance = new ParameterAdaptableBatchLearnerWrapper<Collection<? extends String>, Collection<CentroidCluster<String>>, KMeansClusterer<String, CentroidCluster<String>>>();
        assertSame(learner, instance.getLearner());
        assertNull(instance.getParameterAdapters());
        
        learner = new KMeansClusterer<String, CentroidCluster<String>>();
        instance = new ParameterAdaptableBatchLearnerWrapper<Collection<? extends String>, Collection<CentroidCluster<String>>, KMeansClusterer<String, CentroidCluster<String>>>(
            learner);
        assertSame(learner, instance.getLearner());
        assertNull(instance.getParameterAdapters());
    }

    /**
     * Clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );
        KMeansClusterer<Vector, CentroidCluster<Vector>> learner = KMeansFactory.create(3, random);
        ParameterAdaptableBatchLearnerWrapper<Collection<? extends Vector>, Collection<CentroidCluster<Vector>>, KMeansClusterer<Vector, CentroidCluster<Vector>>>
            instance = new ParameterAdaptableBatchLearnerWrapper<Collection<? extends Vector>, Collection<CentroidCluster<Vector>>, KMeansClusterer<Vector, CentroidCluster<Vector>>>(
                learner);

        double fraction = 0.25;
        instance.addParameterAdapter(new DummyParameterAdapter(fraction));
        ParameterAdaptableBatchLearnerWrapper<Collection<? extends Vector>, Collection<CentroidCluster<Vector>>, KMeansClusterer<Vector, CentroidCluster<Vector>>>
            clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotNull( clone.getLearner() );
        assertNotSame( instance.getLearner(), clone.getLearner() );
        assertNotNull( clone.getParameterAdapters() );
        assertNotSame( instance.getParameterAdapters(), clone.getParameterAdapters() );

    }

    /**
     * Test of learn method, of class ParameterAdaptableBatchLearnerWrapper.
     */
    public void testLearn()
    {
        EuclideanDistanceMetric metric = EuclideanDistanceMetric.INSTANCE;
        VectorMeanCentroidClusterCreator creator = VectorMeanCentroidClusterCreator.INSTANCE;
        GreedyClusterInitializer<CentroidCluster<Vector>, Vector> initializer =
            new GreedyClusterInitializer<CentroidCluster<Vector>, Vector>(
            metric, creator, random);
        CentroidClusterDivergenceFunction<Vector> clusterMetric =
            new CentroidClusterDivergenceFunction<Vector>(metric);
        int numRequestedClusters = 100;
        KMeansClusterer<Vector, CentroidCluster<Vector>> learner = new KMeansClusterer<Vector, CentroidCluster<Vector>>(numRequestedClusters, 10000, initializer, clusterMetric, creator);
        ParameterAdaptableBatchLearnerWrapper<Collection<? extends Vector>, Collection<CentroidCluster<Vector>>, KMeansClusterer<Vector, CentroidCluster<Vector>>>
            instance = new ParameterAdaptableBatchLearnerWrapper<Collection<? extends Vector>, Collection<CentroidCluster<Vector>>, KMeansClusterer<Vector, CentroidCluster<Vector>>>(
                learner);
        
        double fraction = 0.25;
        instance.addParameterAdapter(new DummyParameterAdapter(fraction));
        
        int numExamples = 25;
        ArrayList<Vector> data = new ArrayList<Vector>(numExamples);
        for (int i = 0; i < numExamples; i++)
        {
            data.add(new Vector3(this.random.nextDouble(), this.random.nextDouble(), this.random.nextDouble()));
        }
        
        assertEquals(numRequestedClusters, learner.getNumRequestedClusters());
        
        Collection<CentroidCluster<Vector>> result = instance.learn(data);
        int expectedNumClusters = (int) (fraction * numExamples);
        assertEquals(expectedNumClusters, result.size());
        assertEquals(expectedNumClusters, learner.getNumRequestedClusters());
        
    }

    /**
     * Test of getLearner method, of class ParameterAdaptableBatchLearnerWrapper.
     */
    public void testGetLearner()
    {
        this.testSetLearner();
    }

    /**
     * Test of setLearner method, of class ParameterAdaptableBatchLearnerWrapper.
     */
    public void testSetLearner()
    {
        KMeansClusterer<String, CentroidCluster<String>> learner = null;
        ParameterAdaptableBatchLearnerWrapper<Collection<? extends String>, Collection<CentroidCluster<String>>, KMeansClusterer<String, CentroidCluster<String>>>
            instance = new ParameterAdaptableBatchLearnerWrapper<Collection<? extends String>, Collection<CentroidCluster<String>>, KMeansClusterer<String, CentroidCluster<String>>>();
        assertSame(learner, instance.getLearner());
        
        learner = new KMeansClusterer<String, CentroidCluster<String>>();
        instance.setLearner(learner);
        assertSame(learner, instance.getLearner());
        
        learner = null;
        instance.setLearner(learner);
        assertSame(learner, instance.getLearner());
    }

    /**
     * Test of addParameterAdapter method, of class ParameterAdaptableBatchLearnerWrapper.
     */
    public void testAddParameterAdapter()
    {
        this.testRemoveParameterAdapter();
    }

    /**
     * Test of removeParameterAdapter method, of class ParameterAdaptableBatchLearnerWrapper.
     */
    public void testRemoveParameterAdapter()
    {
        ParameterAdaptableBatchLearnerWrapper<Collection<? extends String>, Collection<CentroidCluster<String>>, KMeansClusterer<String, CentroidCluster<String>>>
            instance = new ParameterAdaptableBatchLearnerWrapper<Collection<? extends String>, Collection<CentroidCluster<String>>, KMeansClusterer<String, CentroidCluster<String>>>();
        assertNull(instance.getParameterAdapters());
        
        DummyParameterAdapter adapter1 = new DummyParameterAdapter(0.25);
        instance.addParameterAdapter(adapter1);
        assertEquals(1, instance.getParameterAdapters().size());
        assertSame(adapter1, instance.getParameterAdapters().get(0));
        
        DummyParameterAdapter adapter2 = new DummyParameterAdapter(0.33);
        instance.addParameterAdapter(adapter2);
        assertEquals(2, instance.getParameterAdapters().size());
        assertTrue(instance.getParameterAdapters().contains(adapter1));
        assertTrue(instance.getParameterAdapters().contains(adapter2));
        
        instance.removeParameterAdapter(adapter2);
        assertEquals(1, instance.getParameterAdapters().size());
        assertSame(adapter1, instance.getParameterAdapters().get(0));
        
        instance.removeParameterAdapter(adapter1);
        assertNull(instance.getParameterAdapters());
        
        instance.removeParameterAdapter(adapter2);
        assertNull(instance.getParameterAdapters());
        
        instance.addParameterAdapter(adapter2);
        assertEquals(1, instance.getParameterAdapters().size());
        assertSame(adapter2, instance.getParameterAdapters().get(0));
        
        instance.removeParameterAdapter(adapter1);
        assertEquals(1, instance.getParameterAdapters().size());
        assertSame(adapter2, instance.getParameterAdapters().get(0));
        
        instance.removeParameterAdapter(adapter2);
        assertNull(instance.getParameterAdapters());
    }

    /**
     * Test of getParameterAdapters method, of class ParameterAdaptableBatchLearnerWrapper.
     */
    public void testGetParameterAdapters()
    {
        this.testSetParameterAdapters();
    }

    /**
     * Test of setParameterAdapters method, of class ParameterAdaptableBatchLearnerWrapper.
     */
    public void testSetParameterAdapters()
    {
        LinkedList<ParameterAdapter<? super KMeansClusterer<String, CentroidCluster<String>>, ? super Collection<? extends String>>> parameterAdapters = null;
        ParameterAdaptableBatchLearnerWrapper<Collection<? extends String>, Collection<CentroidCluster<String>>, KMeansClusterer<String, CentroidCluster<String>>>
            instance = new ParameterAdaptableBatchLearnerWrapper<Collection<? extends String>, Collection<CentroidCluster<String>>, KMeansClusterer<String, CentroidCluster<String>>>();
        assertSame(parameterAdapters, instance.getParameterAdapters());
        
        parameterAdapters = new LinkedList<ParameterAdapter<? super KMeansClusterer<String, CentroidCluster<String>>, ? super Collection<? extends String>>>();
        instance.setParameterAdapters(parameterAdapters);
        assertSame(parameterAdapters, instance.getParameterAdapters());
        
        parameterAdapters = null;
        instance.setParameterAdapters(parameterAdapters);
        assertSame(parameterAdapters, instance.getParameterAdapters());
    }

    private static class DummyParameterAdapter
        extends AbstractCloneableSerializable
        implements ParameterAdapter<KMeansClusterer<?, ?>, Collection<?>>
    {
        private double fraction;
        
        public DummyParameterAdapter(
            final double fraction)
        {
            this.fraction = fraction;
        }
        
        public void adapt(
            final KMeansClusterer<?, ?> object, Collection<?> data)
        {
            object.setNumRequestedClusters((int) (data.size() * fraction));
        }
        
    }
}
