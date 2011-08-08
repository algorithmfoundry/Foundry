/*
 * File:                NearestNeighborExhaustiveTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 25, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.nearest;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Tests of NearestNeighborExhaustive
 * @author  Justin Basilico
 * @since   2.1
 */
public class NearestNeighborExhaustiveTest
    extends NearestNeighborTestHarness
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public NearestNeighborExhaustiveTest(
        String testName)
    {
        super(testName);
    }
    
    public void testConstructors()
    {
        EuclideanDistanceMetric divergenceFunction = null;
        LinkedList<InputOutputPair<? extends Vector, String>> data = null;
        NearestNeighborExhaustive<Vector, String> instance =
            new NearestNeighborExhaustive<Vector, String>();
        assertSame(divergenceFunction, instance.getDivergenceFunction());
        assertNotNull( instance.getData() );
        assertEquals( 0, instance.getData().size() );
        
        divergenceFunction = EuclideanDistanceMetric.INSTANCE;
        instance = new NearestNeighborExhaustive<Vector, String>(divergenceFunction);
        assertSame(divergenceFunction, instance.getDivergenceFunction());
        assertNotNull( instance.getData() );
        assertEquals( 0, instance.getData().size() );
        
        data = new LinkedList<InputOutputPair<? extends Vector, String>>();
        
        instance = new NearestNeighborExhaustive<Vector, String>(divergenceFunction, data);
        assertSame(divergenceFunction, instance.getDivergenceFunction());
        assertEquals(instance.getData().size(), data.size());
    }

    /**
     * Test of evaluate method, of class NearestNeighborExhaustive.
     */
    public void testEvaluateString()
    {
        LinkedList<InputOutputPair<? extends Vector, String>> data =
            new LinkedList<InputOutputPair<? extends Vector, String>>();
        data.add(new DefaultInputOutputPair<Vector, String>(
            new Vector3(1.0, 0.0, 0.0), "a"));
        data.add(new DefaultInputOutputPair<Vector, String>(
            new Vector3(0.0, 2.0, 0.0), "b"));
        data.add(new DefaultInputOutputPair<Vector, String>(
            new Vector3(0.0, 0.0, 3.0), "c"));
        
        NearestNeighborExhaustive<Vector, String> instance =
            new NearestNeighborExhaustive<Vector, String>(
                CounterEuclidenDistance.INSTANCE, data);
        assertEquals("a", instance.evaluate(new Vector3(0.0, 0.0, 0.0)));
        assertEquals("c", instance.evaluate(new Vector3(0.0, 0.0, 4.0)));
        assertEquals("b", instance.evaluate(new Vector3(0.0, 3.0, 0.0)));
        
        data = new LinkedList<InputOutputPair<? extends Vector, String>>();
        instance.setData(data);
        assertNull(instance.evaluate(new Vector3(0.0, 0.0, 0.0)));
    }

    /**
     * Test of setData method, of class NearestNeighborExhaustive.
     */
    public void testSetData()
    {
        LinkedList<InputOutputPair<? extends Vector, String>> data = null;
        NearestNeighborExhaustive<Vector, String> instance =
            new NearestNeighborExhaustive<Vector, String>();
        assertEquals( 0, instance.getData().size() );
        
        data = new LinkedList<InputOutputPair<? extends Vector, String>>();
        instance.setData(data);
        assertEquals( data.size(), instance.getData().size() );
        
        data = new LinkedList<InputOutputPair<? extends Vector, String>>();
        instance.setData(data);
        assertEquals( data.size(), instance.getData().size() );
        
        data = null;
        instance.setData(data);
        assertNull( instance.getData() );
    }

    @Override
    public NearestNeighborExhaustive<Vector, Double> createInstance(
        Collection<? extends InputOutputPair<Vector, Double>> data)
    {
        return new NearestNeighborExhaustive<Vector, Double>(
            CounterEuclidenDistance.INSTANCE, data );
    }

    @Override
    public void testLearner()
    {

        NearestNeighborExhaustive.Learner<Vector,Double> learner =
            new NearestNeighborExhaustive.Learner<Vector, Double>();
        assertNull( learner.getDivergenceFunction() );
        learner = new NearestNeighborExhaustive.Learner<Vector, Double>(
            CounterEuclidenDistance.INSTANCE );

        NearestNeighbor<Vector,Double> nn = learner.learn(POINTS);
        assertTrue( nn.getData().containsAll(POINTS) );
    }

}
