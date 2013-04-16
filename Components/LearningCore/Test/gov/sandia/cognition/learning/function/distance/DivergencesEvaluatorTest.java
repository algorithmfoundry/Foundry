/*
 * File:            DivergencesEvaluatorTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.function.distance;

import org.junit.Test;
import gov.sandia.cognition.learning.algorithm.clustering.KMeansFactory;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.math.DivergenceFunction;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.SparseVectorFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import static org.junit.Assert.*;

/**
 * Unit tests for class DivergencesEvaluator.
 * 
 * @author  Justin Basilico
 * @since   3.3.3
 */
public class DivergencesEvaluatorTest
{
    /** Random number generator to use. */
    protected Random random = new Random(211);

    /**
     * Creates a new test.
     */
    public DivergencesEvaluatorTest()
    {
    }

    /**
     * Test of constructors of class DivergencesEvaluator.
     */
    @Test
    public void testConstructors()
    {
        DivergenceFunction<Vectorizable, Vectorizable> divergenceFunction = null;
        Collection<Vector> values = null;
        VectorFactory<?> vectorFactory = VectorFactory.getDefault();
        DivergencesEvaluator<Vector, Vector> instance = new DivergencesEvaluator<Vector, Vector>();
        assertSame(divergenceFunction, instance.getDivergenceFunction());
        assertTrue(instance.getValues().isEmpty());
        assertSame(vectorFactory, instance.getVectorFactory());

        divergenceFunction = new ManhattanDistanceMetric();
        values = new ArrayList<Vector>();
        values.add(new Vector2());
        instance = new DivergencesEvaluator<Vector, Vector>(
            divergenceFunction, values);
        assertSame(divergenceFunction, instance.getDivergenceFunction());
        assertSame(values, instance.getValues());
        assertSame(vectorFactory, instance.getVectorFactory());

        vectorFactory = new SparseVectorFactoryMTJ();
        instance = new DivergencesEvaluator<Vector, Vector>(
            divergenceFunction, values, vectorFactory);
        assertSame(divergenceFunction, instance.getDivergenceFunction());
        assertSame(values, instance.getValues());
        assertSame(vectorFactory, instance.getVectorFactory());
    }

    /**
     * Test of clone method, of class DivergencesEvaluator.
     */
    @Test
    public void testClone()
    {
        DivergencesEvaluator<Vector, Vector> instance = new DivergencesEvaluator<Vector, Vector>();
        DivergencesEvaluator<Vector, Vector> clone = instance.clone();
        assertNotSame(clone, instance);
        assertNotSame(clone, instance.clone());
    }

    /**
     * Test of evaluate method, of class DivergencesEvaluator.
     */
    @Test
    public void testEvaluate()
    {
        DivergencesEvaluator<Vector, Vector3> instance =
            DivergencesEvaluator.create(new ManhattanDistanceMetric(),
                Arrays.asList(
                    new Vector3( 1.0,  0.0,  0.0),
                    new Vector3( 0.0, -3.0,  0.0),
                    new Vector3( 0.0,  0.0,  2.0),
                    new Vector3( 3.0,  2.0,  1.0)));

        VectorFactory<?> vectorFactory = VectorFactory.getDenseDefault();
        assertEquals(vectorFactory.copyValues(2.0, 4.0, 3.0, 5.0),
            instance.evaluate(new Vector3(1.0, -1.0, 1.0)));
        assertEquals(vectorFactory.copyValues(4.0, 0.0, 5.0, 9.0),
            instance.evaluate(new Vector3(0.0, -3.0, 0.0)));
    }

    /**
     * Test of getOutputDimensionality method, of class DivergencesEvaluator.
     */
    @Test
    public void testGetOutputDimensionality()
    {
        DivergencesEvaluator<Vector, Vector> instance =
            new DivergencesEvaluator<Vector, Vector>();
        assertEquals(0, instance.getOutputDimensionality());

        instance.getValues().add(new Vector3());
        assertEquals(1, instance.getOutputDimensionality());

        instance.getValues().add(new Vector3());
        assertEquals(2, instance.getOutputDimensionality());

        instance.setValues(new LinkedHashSet<Vector>());
        assertEquals(0, instance.getOutputDimensionality());
    }

    /**
     * Test of getDivergenceFunction method, of class DivergencesEvaluator.
     */
    @Test
    public void testGetDivergenceFunction()
    {
        this.testSetDivergenceFunction();
    }

    /**
     * Test of setDivergenceFunction method, of class DivergencesEvaluator.
     */
    @Test
    public void testSetDivergenceFunction()
    {
        DivergenceFunction<Vectorizable, Vectorizable> divergenceFunction = null;
        DivergencesEvaluator<Vector, Vector> instance = new DivergencesEvaluator<Vector, Vector>();
        assertSame(divergenceFunction, instance.getDivergenceFunction());

        divergenceFunction = new ManhattanDistanceMetric();
        instance.setDivergenceFunction(divergenceFunction);
        assertSame(divergenceFunction, instance.getDivergenceFunction());

        divergenceFunction = null;
        instance.setDivergenceFunction(divergenceFunction);
        assertSame(divergenceFunction, instance.getDivergenceFunction());

        divergenceFunction = new EuclideanDistanceSquaredMetric();
        instance.setDivergenceFunction(divergenceFunction);
        assertSame(divergenceFunction, instance.getDivergenceFunction());
    }

    /**
     * Test of getValues method, of class DivergencesEvaluator.
     */
    @Test
    public void testGetValues()
    {
        this.testSetValues();;
    }

    /**
     * Test of setValues method, of class DivergencesEvaluator.
     */
    @Test
    public void testSetValues()
    {
        Collection<Vector> values = null;
        DivergencesEvaluator<Vector, Vector> instance = new DivergencesEvaluator<Vector, Vector>();
        assertTrue(instance.getValues().isEmpty());

        values = new ArrayList<Vector>();
        values.add(new Vector2());
        instance.setValues(values);
        assertSame(values, instance.getValues());

        values = new HashSet<Vector>();
        instance.setValues(values);
        assertSame(values, instance.getValues());
    }

    /**
     * Test of create method, of class DivergencesEvaluator.
     */
    @Test
    public void testCreate()
    {
        ManhattanDistanceMetric divergenceFunction =
            new ManhattanDistanceMetric();
        Collection<Vector> values = new ArrayList<Vector>();
        values.add(new Vector2());
        DivergencesEvaluator<?, Vector> instance =
            DivergencesEvaluator.create(divergenceFunction, values);
        assertSame(divergenceFunction, instance.getDivergenceFunction());
        assertSame(values, instance.getValues());
    }

    /**
     * Test of learn method, of class DivergencesEvaluator.Learner.
     */
    @Test
    public void testLearn()
    {
        VectorFactory<?> vectorFactory = VectorFactory.getDenseDefault();
        DivergencesEvaluator.Learner<Collection<? extends Vector>, Vector, CentroidCluster<Vector>> instance =
            DivergencesEvaluator.Learner.create(
                KMeansFactory.create(2, random),
                KMeansFactory.create(2, random).getDivergenceFunction());

        Collection<Vector> data = new ArrayList<Vector>();
        data.add(vectorFactory.copyValues( 0.0,  1.0,  0.0,  0.0));
        data.add(vectorFactory.copyValues( 0.0, -1.0,  0.0,  0.0));
        data.add(vectorFactory.copyValues( 0.0,  0.0,  3.0,  0.0));
        data.add(vectorFactory.copyValues( 0.0,  0.0,  5.0,  0.0));

        DivergencesEvaluator<Vector, ?> learned = instance.learn(data);
        assertEquals(new Vector2(4.0, 0.0), 
            learned.evaluate(vectorFactory.copyValues(0.0, 0.0, 0.0, 0.0)));
        assertEquals(new Vector2(0.0, 4.0),
            learned.evaluate(vectorFactory.copyValues(0.0, 0.0, 4.0, 0.0)));
    }
}
