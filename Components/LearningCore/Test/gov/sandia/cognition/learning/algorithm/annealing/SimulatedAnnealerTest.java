/*
 * File:                SimulatedAnnealerTest.java
 * Authors:             Jonathan McClain and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 16, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.annealing;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.learning.function.cost.CostFunction;
import gov.sandia.cognition.learning.function.cost.EuclideanDistanceCostFunction;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     SimulatedAnnealer
 *
 * @author Jonathan McClain
 * @author Kevin R. Dixon
 * @since 1.0
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2006-10-02",
    changesNeeded=false,
    comments="Looks fine."
)
public class SimulatedAnnealerTest 
        extends TestCase
{
    /** The simulated annealer to use in the tests. */
    private SimulatedAnnealer<Vectorizable, Vectorizable> simulatedAnnealer;
    
    /** The cost function to use in the tests. */
    private EuclideanDistanceCostFunction cost;
    
    /** The random number generator for the tests. */
    protected Random random = new Random(2);
    
    /**
     * The test constructor.
     *
     * @param testName The name of the test.
     */
    public SimulatedAnnealerTest(
            String testName) 
    {
        super(testName);
    }

    /**
     * Called before each test is run. Prepares the SimulatedAnnealer for the 
     * tests.
     */
    @Override
    protected void setUp() 
    {
        int N = 2;
        double range = 2;
        Vector goal = VectorFactory.getDefault().createUniformRandom(N,-range,range, random);
        System.out.println("Goal = " + goal );
        this.cost = new EuclideanDistanceCostFunction(goal);
        Matrix covariance = MatrixFactory.getDefault().createIdentity(N,N).scale(1e-3);
        VectorizablePerturber perturber = 
            new VectorizablePerturber(new Random(3), covariance);
        Vectorizable initialVector = VectorFactory.getDefault().createUniformRandom(N,-range,range, random);
        this.simulatedAnnealer = 
            new SimulatedAnnealer<Vectorizable, Vectorizable>(
                initialVector,
                perturber,
                this.cost,
                10000);
    }

    /**
     * Called after each test is run.
     */
    @Override
    protected void tearDown()
    {
        this.cost = null;
        this.simulatedAnnealer = null;
    }

    /**
     * Returns the test.
     * @return Test
     */
    public static Test suite() 
    {
        TestSuite suite = new TestSuite(SimulatedAnnealerTest.class);
        
        return suite;
    }

    public void testClone()
    {

        SimulatedAnnealer clone = this.simulatedAnnealer.clone();
        assertNotNull( clone );
        assertNotSame( this.simulatedAnnealer, clone );

    }

    /**
     * Test of minimizeCost method, of class 
     * gov.sandia.isrc.learning.reinforcement.SimulatedAnnealer.
     */
    public void testMinimize() 
    {
        System.out.println("minimize");
        

        Vectorizable result = 
            this.simulatedAnnealer.learn(this.cost.getCostParameters());
        System.out.println("Result = " + result );

        assertEquals("minimizeCost did not reach the expected minimum cost", 
                0.0, this.cost.evaluate(result), 0.1);
    }
    
    /**
     * Test of step method, of class gov.sandia.isrc.learning.annealing.SimulatedAnnealer.
     */
    public void testStep()
    {
        this.simulatedAnnealer.learn(this.cost.getCostParameters());
        boolean expected = false;
        boolean result = this.simulatedAnnealer.step();
        assertEquals(expected, result);
    }

    /**
     * Test of getCostFunction method, of class 
     * gov.sandia.isrc.learning.reinforcement.SimulatedAnnealer.
     */
    public void testGetCostFunction() 
    {
        System.out.println("getCostFunction");
        
        EuclideanDistanceCostFunction expected = null;
        this.simulatedAnnealer.setCostFunction(expected);        
        CostFunction actual = this.simulatedAnnealer.getCostFunction();
        assertEquals(
                "getCostFunction did not return the expected CostFunction", 
                expected, 
                actual);
    }

    /**
     * Test of getPerturber method, of class 
     * gov.sandia.isrc.learning.reinforcement.SimulatedAnnealer.
     */
    public void testGetPerturber() 
    {
        System.out.println("getPerturber");

        Perturber<Vectorizable> expected = null;
        this.simulatedAnnealer.setPerturber(expected);
        Perturber<Vectorizable> actual = this.simulatedAnnealer.getPerturber();
        assertEquals(
                "getPerturber did not return the expected Perturber", 
                expected, 
                actual);
    }

    /**
     * Test of getTemperature method, of class 
     * gov.sandia.isrc.learning.reinforcement.SimulatedAnnealer.
     */
    public void testGetTemperature() 
    {
        System.out.println("getTemperature");
              
        double expected = 1.0;
        this.simulatedAnnealer.setTemperature(expected);
        double actual = this.simulatedAnnealer.getTemperature();
        assertEquals(
                "getTemperature did not return the expected temperature", 
                expected, 
                actual, 
                0.0);
    }
    
    /**
     * Test of getMaxIterations method, of class 
     * gov.sandia.isrc.learning.reinforcement.SimulatedAnnealer.
     */
    public void testGetMaxIterations() 
    {
        System.out.println("getMaxIterations");
        
        int expected = 1;
        this.simulatedAnnealer.setMaxIterations(expected);
        int actual = this.simulatedAnnealer.getMaxIterations();
        assertEquals(
                "getMaxIterations did not return the expected iteration", 
                expected, 
                actual);
    }
    
    /**
     * Test of getMaxIterationsWithoutImprovement method, of class 
     * gov.sandia.isrc.learning.reinforcement.SimulatedAnnealer.
     */
    public void testGetMaxIterationsWithoutImprovement()
    {
        System.out.println("getMaxIterationsWithoutImprovement");
        
        int expected = 1;
        this.simulatedAnnealer.setMaxIterationsWithoutImprovement(expected);
        int actual = 
                this.simulatedAnnealer.getMaxIterationsWithoutImprovement();
        assertEquals(
                  "getMaxIterationsWithoutImprovement did not return the "
                + "expected iteration", 
                expected, 
                actual);
    }
    
    /**
     * Test of getIterationsWithoutImprovement method, of class 
     * gov.sandia.isrc.learning.reinforcement.SimulatedAnnealer.
     */
    public void testGetIterationsWithoutImprovement()
    {
        System.out.println("getIterationsWithoutImprovement");
        
        int expected = 0;
        this.simulatedAnnealer.setIterationsWithoutImprovement(expected);
        int actual = 
                this.simulatedAnnealer.getIterationsWithoutImprovement();
        assertEquals(
                  "getIterationsWithoutImprovement did not return the expected"
                + "iteration", 
                expected, 
                actual);
    }

    /**
     * Test of getCoolingFactor method, of class 
     * gov.sandia.isrc.learning.reinforcement.SimulatedAnnealer.
     */
    public void testGetCoolingFactor() 
    {
        System.out.println("getCoolingFactor");
        
        double expected = 0.5;
        this.simulatedAnnealer.setCoolingFactor(expected);
        double actual = this.simulatedAnnealer.getCoolingFactor();
        assertEquals(
                "getCoolingFactor did not return the expected cooling factor", 
                expected, 
                actual, 
                0.0);
    }

    /**
     * Test of getRandom method, of class 
     * gov.sandia.isrc.learning.reinforcement.SimulatedAnnealer.
     */
    public void testGetRandom() 
    {
        System.out.println("getRandom");
        
        Random expected = new Random(4);
        this.simulatedAnnealer.setRandom(expected);
        Random actual = this.simulatedAnnealer.getRandom();
        assertEquals(
                "getRandom did not return the expected Random", 
                expected, 
                actual);
    }

    /**
     * Test of setCostFunction method, of class 
     * gov.sandia.isrc.learning.reinforcement.SimulatedAnnealer.
     */
    public void testSetCostFunction() 
    {
        System.out.println("setCostFunction");
                
        EuclideanDistanceCostFunction expected = null;
        this.simulatedAnnealer.setCostFunction(expected);
        CostFunction actual = 
            this.simulatedAnnealer.getCostFunction();
        assertEquals(
                "setCostFunction did not set the expected CostFunction", 
                expected, 
                actual);
    }

    /**
     * Test of setPerturber method, of class 
     * gov.sandia.isrc.learning.reinforcement.SimulatedAnnealer.
     */
    public void testSetPerturber() 
    {
        System.out.println("setPerturber");
        
        Perturber<Vectorizable> expected = null;
        this.simulatedAnnealer.setPerturber(expected);
        Perturber<Vectorizable> actual = this.simulatedAnnealer.getPerturber();
        assertEquals(
                "setPerturber did not set the expected Perturber", 
                expected, 
                actual);
    }

    /**
     * Test of setMaxIterations method, of class 
     * gov.sandia.isrc.learning.reinforcement.SimulatedAnnealer.
     */
    public void testSetMaxIterations() 
    {
        System.out.println("setMaxIterations");
        
        int expected = 1;
        this.simulatedAnnealer.setMaxIterations(expected);
        int actual = this.simulatedAnnealer.getMaxIterations();
        assertEquals(
                "setMaxIterations did not set the expected integer", 
                expected, 
                actual);
    }
    
    /**
     * Test of setMaxIterationsWithoutImprovement method, of class 
     * gov.sandia.isrc.learning.reinforcement.SimulatedAnnealer.
     */
    public void testSetMaxIterationsWithoutImprovement() 
    {
        System.out.println("setMaxIterationsWithoutImprovement");
        
        int expected = 0;
        this.simulatedAnnealer.setMaxIterationsWithoutImprovement(expected);
        int actual = 
                this.simulatedAnnealer.getMaxIterationsWithoutImprovement();
        assertEquals(
              "setMaxIterationsWithoutImprovement did not set the expected "
            + "integer", 
            expected, 
            actual);
    }
    
    /**
     * Test of setIterationsWithoutImprovement method, of class 
     * gov.sandia.isrc.learning.reinforcement.SimulatedAnnealer.
     */
    public void testSetIterationsWithoutImprovement() 
    {
        System.out.println("setIterationsWithoutImprovement");
        
        int expected = 0;
        this.simulatedAnnealer.setIterationsWithoutImprovement(expected);
        int actual = this.simulatedAnnealer.getIterationsWithoutImprovement();
        assertEquals(
            "setIterationsWithoutImprovement did not set the expected integer", 
            expected, 
            actual);
    }

    /**
     * Test of setCoolingFactor method, of class 
     * gov.sandia.isrc.learning.reinforcement.SimulatedAnnealer.
     */
    public void testSetCoolingFactor() 
    {
        System.out.println("setCoolingFactor");
        
        double expected = 0.5;
        this.simulatedAnnealer.setCoolingFactor(expected);
        double actual = this.simulatedAnnealer.getCoolingFactor();
        assertEquals(
                "setCoolingFactor did not set the expected double", 
                expected, 
                actual);
        
        
        this.simulatedAnnealer.setCoolingFactor(0.1);
        assertEquals(0.1, this.simulatedAnnealer.getCoolingFactor());
        
        this.simulatedAnnealer.setCoolingFactor(1.0);
        assertEquals(1.0, this.simulatedAnnealer.getCoolingFactor());
        
        boolean exceptionThrown = false;
        try
        {
            this.simulatedAnnealer.setCoolingFactor(0.0);
        }
        catch ( IllegalArgumentException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
        exceptionThrown = false;
        try
        {
            this.simulatedAnnealer.setCoolingFactor(1.1);
        }
        catch ( IllegalArgumentException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

    /**
     * Test of setRandom method, of class 
     * gov.sandia.isrc.learning.reinforcement.SimulatedAnnealer.
     */
    public void testSetRandom() {
        System.out.println("setRandom");
        
        Random expected = null;
        this.simulatedAnnealer.setRandom(expected);
        Random actual = this.simulatedAnnealer.getRandom();
        assertEquals(
                "setRandom did not set the expected Random", 
                expected, 
                actual);
    }
    
    /**
     * The DoublePerturber implements a Perturber for doubles.
     *
     * @author Jonathan McClain
     * @since 1.0
     */
    private class DoublePerturber
        extends AbstractCloneableSerializable
        implements Perturber<Double>
    {
        /** The random number generator to use. */
        private Random random;
        
        /** The difference between max and min. */
        private double span;
        
        /** The minimum for the random number generator. */
        private double min;
        
        /** The maximum for the random number generator. */
        private double max;
        
        /**
         * Creates a new instance of DoublePerturber.
         *
         * @param min The minimum for the random number generator.
         * @param max The maximum for the random number generator.
         */
        public DoublePerturber(
                double min, 
                double max)
        {
            if(max < min)
            {
                throw new IllegalArgumentException("Max is less than min!");
            }
            this.setRandom(new Random(5));
            this.setSpan(max - min);
            this.setMin(min);
            this.setMax(max);
        }
        
        /**
         * Perturbs the given number by generating a random number between max
         * and min and adding it to the number.
         *
         * @param input The number to perturb.
         * @return The perturbed version of the number.
         */
        public Double perturb(
            Double input)
        {
            double perturbedValue = 
                input + (this.getMin() 
                      + (this.random.nextDouble() * this.getSpan()));
            return perturbedValue;
        }
        
        /**
         * Sets the random number generator.
         *
         * @param random The new random number generator.
         */
        private void setRandom(
                Random random)
        {
            this.random = random;
        }
        
        /**
         * Sets the difference between max and min.
         *
         * @param span The new span.
         */
        private void setSpan(
                double span)
        {
            this.span = span;
        }
        
        /**
         * Sets the minimum for the random number generator.
         *
         * @param min The new minimum.
         */
        private void setMin(
                double min)
        {
            this.min = min;
        }
        
        /**
         * Sets the maximum for the random number generator.
         *
         * @param max The new maximum.
         */
        private void setMax(
                double max)
        {
            this.max = max;
        }
        
        /**
         * Gets the random number generator.
         *
         * @return The random number generator.
         */
        private Random getRandom()
        {
            return this.random;
        }
        
        /**
         * Gets the difference between max and min.
         *
         * @return The span.
         */
        private double getSpan()
        {
            return this.span;
        }
        
        /**
         * Gets the minimum for the random number generator.
         *
         * @return The minimum.
         */
        private double getMin()
        {
            return this.min;
        }
        
        /**
         * Gets the maximum for the random number generator.
         *
         * @return The maximum.
         */
        private double getMax()
        {
            return this.max;
        }
    }
    
    /**
     * The DoubleCostFunction implements a cost function for functions that take
     * as input a double and return a double.
     *
     * @author Jonathan McClain
     * @since 1.0
     */
    private class DoubleCostFunction
            extends AbstractCloneableSerializable
            implements CostFunction<Double, Double>
    {
        /** The goal of the search */
        private double goal;
        
        /**
         * Creates a new instance of DoubleCostFunction.
         *
         * @param goal The goal of the search.
         */
        DoubleCostFunction(
                double goal)
        {
            this.setGoal(goal);
        }

        @Override
        public DoubleCostFunction clone()
        {
            return (DoubleCostFunction) super.clone();
        }
        
        /**
         * Computes the cost of the given target.
         *
         * @param target The double to evaluate.
         * @return The cost of the given double.
         */
        public Double evaluate(
                Double target)
        {
            return ((this.getGoal() - target) * (this.getGoal() - target));
        }
        
        /**
         * Sets the goal of the search.
         *
         * @param goal The new goal of the search.
         */
        private void setGoal(
                double goal)
        {
            this.goal = goal;
        }
        
        /**
         * Gets the goal of the search.
         *
         * @return The goal.
         */
        private double getGoal()
        {
            return this.goal;
        }
        
        public void setCostParameters(
            Double costParameters)
        {
            this.setGoal(costParameters);
        }
        
        public Double getCostParameters()
        {
            return this.getGoal();
        }
    }
    
    /**
     * Creates an empty default annealer for testing.
     *
     * @return An empty default annealer.
     */
    public SimulatedAnnealer<Vectorizable, Vectorizable> createInstance()
    {
        return new SimulatedAnnealer<Vectorizable, Vectorizable>(
            null, null, null);
    }
    
    /**
     * Test of getBestSoFar method, of class gov.sandia.isrc.learning.annealing.SimulatedAnnealer.
     */
    public void testGetBestSoFar()
    {
        SimulatedAnnealer<Vectorizable, Vectorizable> instance = 
            this.createInstance();
        assertNull(instance.getBestSoFar());
    }

    /**
     * Test of getBestSoFarScore method, of class gov.sandia.isrc.learning.annealing.SimulatedAnnealer.
     */
    public void testGetBestSoFarScore()
    {
        SimulatedAnnealer<Vectorizable, Vectorizable> instance = 
            this.createInstance();
        assertEquals(0.0, instance.getBestSoFarScore());
    }

    /**
     * Test of getCurrent method, of class gov.sandia.isrc.learning.annealing.SimulatedAnnealer.
     */
    public void testGetCurrent()
    {
        SimulatedAnnealer<Vectorizable, Vectorizable> instance = 
            this.createInstance();
        assertNull(instance.getCurrent());
    }

    /**
     * Test of getCurrentScore method, of class gov.sandia.isrc.learning.annealing.SimulatedAnnealer.
     */
    public void testGetCurrentScore()
    {
        SimulatedAnnealer<Vectorizable, Vectorizable> instance = 
            this.createInstance();
        assertEquals(0.0, instance.getCurrentScore());
    }

    /**
     * Test of getIteration method, of class gov.sandia.isrc.learning.annealing.SimulatedAnnealer.
     */
    public void testGetIteration()
    {
        SimulatedAnnealer<Vectorizable, Vectorizable> instance = 
            this.createInstance();
        assertEquals(0, instance.getIteration());
    }

    /**
     * Test of setTemperature method, of class gov.sandia.isrc.learning.annealing.SimulatedAnnealer.
     */
    public void testSetTemperature()
    {
        double expected = random.nextDouble();
        this.simulatedAnnealer.setTemperature(expected);
        assertEquals(expected, this.simulatedAnnealer.getTemperature());
    }

    /**
     * Test of setBestSoFar method, of class gov.sandia.isrc.learning.annealing.SimulatedAnnealer.
     */
    public void testSetBestSoFar()
    {
        Vectorizable expected = VectorFactory.getDefault().createVector(3);
        this.simulatedAnnealer.setBestSoFar(expected);
        assertSame(expected, this.simulatedAnnealer.getBestSoFar());
    }

    /**
     * Test of setBestSoFarScore method, of class gov.sandia.isrc.learning.annealing.SimulatedAnnealer.
     */
    public void testSetBestSoFarScore()
    {
        double expected = 1.0;
        this.simulatedAnnealer.setBestSoFarScore(expected);
        assertEquals(expected, this.simulatedAnnealer.getBestSoFarScore());
    }

    /**
     * Test of setCurrent method, of class gov.sandia.isrc.learning.annealing.SimulatedAnnealer.
     */
    public void testSetCurrent()
    {
        Vectorizable expected = VectorFactory.getDefault().createVector(3);
        this.simulatedAnnealer.setCurrent(expected);
        assertSame(expected, this.simulatedAnnealer.getCurrent());
    }

    /**
     * Test of setCurrentScore method, of class gov.sandia.isrc.learning.annealing.SimulatedAnnealer.
     */
    public void testSetCurrentScore()
    {
        double expected = 4.0;
        this.simulatedAnnealer.setCurrentScore(expected);
        assertEquals(expected, this.simulatedAnnealer.getCurrentScore());
    }

}
