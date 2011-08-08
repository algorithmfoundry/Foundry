/*
 * File:                EvaluatedGenomeTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright October 5, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.genetic;

import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Random;
import junit.framework.*;

/**
 *
 * @author jdbasil
 */
public class EvaluatedGenomeTest extends TestCase
{
    static double RANGE = 100.0;
    double score;
    Vector vector;
    EvaluatedGenome<Vector> evaluatedGenome;
    
    /** The random number generator for the tests. */
    private Random random = new Random();
    
    public EvaluatedGenomeTest(String testName)
    {
        super(testName);
    }

    protected void setUp() throws Exception
    {
        this.score = Math.random() * RANGE;
        int dim = (int) ((Math.random() * RANGE) + 1.0);
        this.vector = VectorFactory.getDefault().createUniformRandom( dim, -RANGE, RANGE, random );
        this.evaluatedGenome = new EvaluatedGenome<Vector>( score, vector );
    }


    public static Test suite()
    {
        TestSuite suite = new TestSuite(EvaluatedGenomeTest.class);
        
        return suite;
    }

    /**
     * Test of getGenome method, of class gov.sandia.isrc.learning.genetic.EvaluatedGenome.
     */
    public void testGetGenome()
    {
        Object genome = new Object();
        EvaluatedGenome<Object> instance = new EvaluatedGenome<Object>(
            0.0, genome);
        assertSame(genome, instance.getGenome());
    }

    /**
     * Test of getCost method, of class gov.sandia.isrc.learning.genetic.EvaluatedGenome.
     */
    public void testGetCost()
    {
        double cost = Math.random();
        EvaluatedGenome<Object> instance = new EvaluatedGenome<Object>(
            cost, new Object());
        assertEquals(cost, instance.getCost());
    }

    /**
     * Test of setGenome method, of class gov.sandia.isrc.learning.genetic.EvaluatedGenome.
     */
    public void testSetGenome()
    {
        Object genome = new Object();
        EvaluatedGenome<Object> instance = new EvaluatedGenome<Object>(
            0.0, genome);
        assertSame(genome, instance.getGenome());
        Object newGenome = new Object();
        instance.setGenome(newGenome);
        assertSame(newGenome, instance.getGenome());
        instance.setGenome(null);
        assertNull(instance.getGenome());
    }

    /**
     * Test of setCost method, of class gov.sandia.isrc.learning.genetic.EvaluatedGenome.
     */
    public void testSetCost()
    {
        double cost = Math.random();
        EvaluatedGenome<Object> instance = new EvaluatedGenome<Object>(
            cost, new Object());
        assertEquals(cost, instance.getCost());
        double newCost = 0.0;
        instance.setCost(newCost);
        assertEquals(newCost, instance.getCost());
    }
    
    /**
     * Test of getFirst method, of class EvaluatedGenome.
     */
    public void testGetFirst()
    {
        Object genome = new Object();
        EvaluatedGenome<Object> instance = new EvaluatedGenome<Object>(
            0.0, genome);
        assertSame(genome, instance.getFirst());
    }
    
    /**
     * Test of getCost method, of class EvaluatedGenome.
     */
    public void testGetSecond()
    {
        double cost = Math.random();
        EvaluatedGenome<Object> instance = new EvaluatedGenome<Object>(
            cost, new Object());
        assertEquals(cost, instance.getSecond());
    }
}
