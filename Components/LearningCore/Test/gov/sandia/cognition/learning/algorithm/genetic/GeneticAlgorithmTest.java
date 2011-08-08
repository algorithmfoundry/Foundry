/*
 * File:                GeneticAlgorithmTest.java
 * Authors:             Jonathan McClain
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright May 3, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.genetic;

import gov.sandia.cognition.learning.algorithm.genetic.reproducer.CrossoverReproducer;
import gov.sandia.cognition.learning.algorithm.genetic.reproducer.MultiReproducer;
import gov.sandia.cognition.learning.algorithm.genetic.reproducer.MutationReproducer;
import gov.sandia.cognition.learning.algorithm.genetic.reproducer.Reproducer;
import gov.sandia.cognition.learning.algorithm.genetic.selector.TournamentSelector;
import gov.sandia.cognition.learning.algorithm.genetic.reproducer.VectorizableCrossoverFunction;
import gov.sandia.cognition.learning.function.cost.CostFunction;
import gov.sandia.cognition.learning.function.cost.EuclideanDistanceCostFunction;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.learning.algorithm.annealing.VectorizablePerturber;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.*;

/**
 * This class implements JUnit tests for the following classes
 *
 *     GeneticAlgorithm
 *
 * @author Jonathan McClain
 * @since 1.0
 */
public class GeneticAlgorithmTest extends TestCase 
{   
//    /** The genetic algorithm to use in the tests. */
//    protected GeneticAlgorithm<Vectorizable, Vectorizable> ga;
    
    /** The cost function to use in the tests. */
    protected EuclideanDistanceCostFunction cost;
    
    /** The size of the population for the tests. */
    private int populationSize;
    
    /** The size of the vectors to be used in the tests. */
    private int vectorSize;
    
    /** The goal of the tests. */
    private Vector goal;
    
    /** The percent of the population to carry over using the tournament selector. */
    private double carryOverPercent;
    
    /** The percent of the population to crossover. */
    private double crossoverPercent;
    
    /** The percent of the population to mutate. */
    private double mutatePercent;
    
    /** The random number generator for the tests. */
    private Random random = new Random();
    
    /** Initial population to use in creating a new ga instance */
    protected ArrayList<Vectorizable> initialPopulation;
    
    /** Reproducer to use in creating a new ga instance */
    protected MultiReproducer<Vectorizable> multiReproducer;
    
    /**
     * Creates a new instance of GeneticAlgorithmTest.
     */
    public GeneticAlgorithmTest(String testName)
    {
        super(testName);

        this.setPopulationSize(100);
        this.setVectorSize(2);
        this.setCarryOverPercent(0.1);
        this.setCrossoverPercent(0.4);
        this.setMutatePercent(0.5);
        
        double range = 2;
        // Create the goal
        Vector goalVector = VectorFactory.getDefault().createUniformRandom(this.getVectorSize(), -range, range, random);
        this.setGoal(goalVector.clone());
        System.out.print("Goal = ");
        for(int i = 0; 
            i < this.getGoal().convertToVector().getDimensionality(); 
            i++)
        {
            double num = this.getGoal().convertToVector().getElement(i);
            System.out.print(num);
            System.out.print(" ");
        }
        System.out.println("");
        this.setCost(new EuclideanDistanceCostFunction(goal));
        Matrix covariance = MatrixFactory.getDefault().createIdentity(
            this.getVectorSize(), this.getVectorSize()).scale( 1e-6 );

        // Prepare the reproducers
        TournamentSelector<Vectorizable> bestSelector = 
                new TournamentSelector<Vectorizable>(this.getCarryOverPercent(), 10);
        TournamentSelector<Vectorizable> mutationSelector = 
                new TournamentSelector<Vectorizable>(this.getMutatePercent(), 10);
        TournamentSelector<Vectorizable> crossoverSelector = 
                new TournamentSelector<Vectorizable>(this.getCrossoverPercent(), 10);
        VectorizablePerturber perturber = 
                new VectorizablePerturber(new Random(), covariance);
        MutationReproducer<Vectorizable> mutationReproducer = 
                new MutationReproducer<Vectorizable>(
                    perturber, 
                    mutationSelector);
        double probabilityCrossover = 0.5;
        VectorizableCrossoverFunction crossoverFunction = 
                new VectorizableCrossoverFunction( probabilityCrossover );
        CrossoverReproducer<Vectorizable> crossoverReproducer = 
                new CrossoverReproducer<Vectorizable>(
                    crossoverSelector, crossoverFunction);
        ArrayList<Reproducer<Vectorizable>> reproducers =
                new ArrayList<Reproducer<Vectorizable>>();
        reproducers.add(bestSelector);
        reproducers.add(crossoverReproducer);
        reproducers.add(mutationReproducer);
        
        this.multiReproducer = 
                new MultiReproducer<Vectorizable>(reproducers);
        
        // Create the random population
        this.initialPopulation = createPopulation(this.populationSize, range);
    }

    
    protected ArrayList<Vectorizable> createPopulation( int size, double range )
    {
        ArrayList<Vectorizable> population = new ArrayList<Vectorizable>(size);
        for(int i = 0; i < size; i++)
        {
            Vector initialVector = VectorFactory.getDefault().createUniformRandom(this.getVectorSize(), -range, range, random);
            Vector initial = initialVector.clone();
            population.add(initial);
        }
        return population;
    }

    /**
     * Creates a default instance of GeneticAlgorithm.
     *
     * @return Returns a new default instance.
     */
    public GeneticAlgorithm<Vectorizable, Vectorizable> createInstance()
    {                
        return new GeneticAlgorithm<Vectorizable, Vectorizable>(
            this.initialPopulation, this.multiReproducer, this.getCost());
    }

    /**
     * Test of minimize method, of class 
     * gov.sandia.isrc.learning.reinforcement.GeneticAlgorithm.
     */
    public void testMinimize() {
        System.out.println("minimize");
        
        GeneticAlgorithm<Vectorizable, Vectorizable> ga = 
            this.createInstance();

        // Minimize on the population.
        ga.learn(this.getCost().getCostParameters());
        EvaluatedGenome<Vectorizable> result = ga.getBestSoFar();
            
        System.out.print("Result = ");
        for(int i = 0; 
            i < result.getGenome().convertToVector().getDimensionality(); 
            i++)
        {
            double num = result.getGenome().convertToVector().getElement(i);
            System.out.print(num);
            System.out.print(" ");
        }
        System.out.println("");
        System.out.println("Result cost = " + result.getCost());
        assertEquals(result.getCost(), 0, .01);
    }

    /**
     * Test of searchForBetter method, of class gov.sandia.isrc.learning.genetic.GeneticAlgorithm.
     */
    public void testSearchForBetter()
    {
        GeneticAlgorithm<Vectorizable, Vectorizable> ga = 
            this.createInstance();

        EvaluatedGenome<Vectorizable> bestSoFar = 
            new EvaluatedGenome<Vectorizable>(0.0, VectorFactory.getDefault().createVector(3));
        
        EvaluatedGenome<Vectorizable> better = 
            new EvaluatedGenome<Vectorizable>(-1.0, VectorFactory.getDefault().createVector(3));
        EvaluatedGenome<Vectorizable> worse = 
            new EvaluatedGenome<Vectorizable>(1.0, VectorFactory.getDefault().createVector(3));
        
        LinkedList<EvaluatedGenome<Vectorizable>> population = 
            new LinkedList<EvaluatedGenome<Vectorizable>>();
        population.add(worse);
        
        assertSame(bestSoFar, ga.searchForBetter(bestSoFar, population));
        
        population.add(bestSoFar);
        population.add(better);
        
        assertSame(better, ga.searchForBetter(bestSoFar, population));
    }

    /**
     * Test of evaluatePopulation method, of class gov.sandia.isrc.learning.genetic.GeneticAlgorithm.
     */
    public void testEvaluatePopulation()
    {
        GeneticAlgorithm<Vectorizable, Vectorizable> ga = 
            this.createInstance();

        LinkedList<Vectorizable> population = new LinkedList<Vectorizable>();
        Vector member = VectorFactory.getDefault().createVector(this.getVectorSize());
        population.add(member);
        
        ArrayList<EvaluatedGenome<Vectorizable>> result = 
            ga.evaluatePopulation(population);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(member, result.get(0).getGenome());
    }

    /**
     * Test of getCost method, of class 
     * gov.sandia.isrc.learning.reinforcement.GeneticAlgorithm.
     */
    public void testGetCost() {
        System.out.println("getCost");
        
        GeneticAlgorithm<Vectorizable, Vectorizable> ga = 
            this.createInstance();

        EuclideanDistanceCostFunction expected = null;
        ga.setCostFunction(expected);
        CostFunction actual = ga.getCostFunction();
        assertEquals(
                "getCost did not return the expected CostFunction",
                expected,
                actual);
    }

    /**
     * Test of getReproducer method, of class 
     * gov.sandia.isrc.learning.reinforcement.GeneticAlgorithm.
     */
    public void testGetReproducer() {
        System.out.println("getReproducer");
        
        GeneticAlgorithm<Vectorizable, Vectorizable> ga = 
            this.createInstance();

        Reproducer<Vectorizable> expected = null;
        ga.setReproducer(expected);
        Reproducer<Vectorizable> actual = ga.getReproducer();
        assertEquals(
                "getReproducer did not return the expected Reproducer",
                expected,
                actual);
    }

    /**
     * Test of getBestSoFar method, of class 
     * gov.sandia.isrc.learning.reinforcement.GeneticAlgorithm.
     */
    public void testGetBestSoFar() {
        System.out.println("getBestSoFar");
        
        GeneticAlgorithm<Vectorizable, Vectorizable> ga = 
            this.createInstance();

        EvaluatedGenome<Vectorizable> expected = null;
        ga.setBestSoFar(expected);
        EvaluatedGenome<Vectorizable> actual = ga.getBestSoFar();
        assertEquals(
                "getBestSoFar did not return the expected EvaluatedGenome",
                expected,
                actual);
    }

    /**
     * Test of getMaxIterations method, of class 
     * gov.sandia.isrc.learning.reinforcement.GeneticAlgorithm.
     */
    public void testGetMaxIterations() {
        System.out.println("getMaxIterations");
        
        GeneticAlgorithm<Vectorizable, Vectorizable> ga = 
            this.createInstance();

        int expected = 1;
        ga.setMaxIterations(expected);
        int actual = ga.getMaxIterations();
        assertEquals(
                "getMaxIterations did not return the expected integer",
                expected,
                actual);
    }

    /**
     * Test of getIteration method, of class 
     * gov.sandia.isrc.learning.reinforcement.GeneticAlgorithm.
     */
    public void testGetIteration() {
        GeneticAlgorithm<Vectorizable, Vectorizable> ga = 
            this.createInstance();

        int actual = ga.getIteration();
        assertTrue(actual >= 0);
    }

    /**
     * Test of getMaxIterationsWithoutImprovement method, of 
     * class gov.sandia.isrc.learning.reinforcement.GeneticAlgorithm.
     */
    public void testGetMaxIterationsWithoutImprovement() {
        System.out.println("getMaxIterationsWithoutImprovement");
        
        GeneticAlgorithm<Vectorizable, Vectorizable> ga = 
            this.createInstance();

        int expected = 0;
        ga.setMaxIterationsWithoutImprovement(expected);
        int actual = ga.getMaxIterationsWithoutImprovement();
        assertEquals(
                "getMaxIterationsWithoutImprovement did not return the expected integer",
                expected,
                actual);
    }

    /**
     * Test of getIterationsWithoutImprovement method, of class 
     * gov.sandia.isrc.learning.reinforcement.GeneticAlgorithm.
     */
    public void testGetIterationsWithoutImprovement() {
        System.out.println("getIterationsWithoutImprovement");
        
        GeneticAlgorithm<Vectorizable, Vectorizable> ga = 
            this.createInstance();

        int expected = 0;
        ga.setIterationsWithoutImprovement(expected);
        int actual = ga.getIterationsWithoutImprovement();
        assertEquals(
                "getIterationsWithoutImprovement did not return the expected integer",
                expected,
                actual);
    }

    /**
     * Test of getPopulation method, of class 
     * gov.sandia.isrc.learning.reinforcement.GeneticAlgorithm.
     */
    public void testGetPopulation() {
        System.out.println("getPopulation");
        
        GeneticAlgorithm<Vectorizable, Vectorizable> ga = 
            this.createInstance();

        Collection<EvaluatedGenome<Vectorizable>> expected = null;
        ga.setPopulation(expected);
        Collection<EvaluatedGenome<Vectorizable>> actual = ga.getPopulation();
        assertEquals(
                "getPopulation did not return the expected population",
                expected,
                actual);
    }

    /**
     * Test of setCost method, of class 
     * gov.sandia.isrc.learning.reinforcement.GeneticAlgorithm.
     */
    public void testSetCost() {
        System.out.println("setCost");
        
        GeneticAlgorithm<Vectorizable, Vectorizable> ga = 
            this.createInstance();

        EuclideanDistanceCostFunction expected = null;
        ga.setCostFunction(expected);
        CostFunction actual = ga.getCostFunction();
        assertEquals(
                "setCost did not set the expected CostFunction",
                expected,
                actual);
    }

    /**
     * Test of setReproducer method, of class 
     * gov.sandia.isrc.learning.reinforcement.GeneticAlgorithm.
     */
    public void testSetReproducer() {
        System.out.println("setReproducer");
        
        GeneticAlgorithm<Vectorizable, Vectorizable> ga = 
            this.createInstance();

        Reproducer<Vectorizable> expected = null;
        ga.setReproducer(expected);
        Reproducer<Vectorizable> actual = ga.getReproducer();
        assertEquals(
                "setReproducer did not set the expected Reproducer",
                expected,
                actual);
    }

    /**
     * Test of setBestSoFar method, of class 
     * gov.sandia.isrc.learning.reinforcement.GeneticAlgorithm.
     */
    public void testSetBestSoFar() {
        System.out.println("setBestSoFar");
        
        GeneticAlgorithm<Vectorizable, Vectorizable> ga = 
            this.createInstance();

        EvaluatedGenome<Vectorizable> expected = null;
        ga.setBestSoFar(expected);
        EvaluatedGenome<Vectorizable> actual = ga.getBestSoFar();
        assertEquals(
                "setBestSoFar did not set the expected EvaluatedGenome",
                expected,
                actual);
    }

    /**
     * Test of setMaxIterations method, of class 
     * gov.sandia.isrc.learning.reinforcement.GeneticAlgorithm.
     */
    public void testSetMaxIterations() {
        System.out.println("setMaxIterations");
        
        GeneticAlgorithm<Vectorizable, Vectorizable> ga = 
            this.createInstance();

        int expected = 1;
        ga.setMaxIterations(expected);
        int actual = ga.getMaxIterations();
        assertEquals(
                "setMaxIterations did not set the expected integer",
                expected,
                actual);
    }

    /**
     * Test of setMaxIterationsWithoutImprovement method, of class 
     * gov.sandia.isrc.learning.reinforcement.GeneticAlgorithm.
     */
    public void testSetMaxIterationsWithoutImprovement() {
        System.out.println("setMaxIterationsWithoutImprovement");
        
        GeneticAlgorithm<Vectorizable, Vectorizable> ga = 
            this.createInstance();

        int expected = 0;
        ga.setMaxIterationsWithoutImprovement(expected);
        int actual = ga.getMaxIterationsWithoutImprovement();
        assertEquals(
                "setMaxIterationsWithoutImprovement did not set the expected integer",
                expected,
                actual);
    }

    /**
     * Test of setIterationsWithoutImprovement method, of class 
     * gov.sandia.isrc.learning.reinforcement.GeneticAlgorithm.
     */
    public void testSetIterationsWithoutImprovement() {
        System.out.println("setIterationsWithoutImprovement");
        
        GeneticAlgorithm<Vectorizable, Vectorizable> ga = 
            this.createInstance();

        int expected = 0;
        ga.setIterationsWithoutImprovement(expected);
        int actual = ga.getIterationsWithoutImprovement();
        assertEquals(
                "setIterationsWithoutImprovement did not set the expected integer",
                expected,
                actual);
    }

    /**
     * Test of setPopulation method, of class 
     * gov.sandia.isrc.learning.reinforcement.GeneticAlgorithm.
     */
    public void testSetPopulation() {
        System.out.println("setPopulation");
        
        GeneticAlgorithm<Vectorizable, Vectorizable> ga = 
            this.createInstance();

        Collection<EvaluatedGenome<Vectorizable>> expected = null;
        ga.setPopulation(expected);
        Collection<EvaluatedGenome<Vectorizable>> actual = ga.getPopulation();
        assertEquals(
                "setPopulation did not set the expected population",
                expected,
                actual);
    }
    
   
    /** 
     * Gets the cost function to use in the tests. 
     *
     * @return The cost function.
     */
    protected EuclideanDistanceCostFunction getCost()
    {
        return this.cost;
    }
       
    /**
     * Gets the size of the vectors to be used in the tests. 
     *
     * @return The vector size.
     */
    private int getVectorSize()
    {
        return this.vectorSize;
    }
    
    /**
     * Gets the goal of the tests. 
     *
     * @return The goal.
     */
    private Vector getGoal()
    {
        return this.goal;
    }
    
    /** 
     * Gets the percent of the population to carry over using the tournament selector. 
     *
     * @return The percent.
     */
    private double getCarryOverPercent()
    {
        return this.carryOverPercent;
    }
    
    /** 
     * Gets the percent of the population to crossover. 
     *
     * @return The percent.
     */
    private double getCrossoverPercent()
    {
        return this.crossoverPercent;
    }
    
    /** 
     * Gets the percent of the population to mutate. 
     *
     * @return The percent.
     */
    private double getMutatePercent()
    {
        return this.mutatePercent;
    }
    
   
    /**
     * Sets the cost function to use in the tests. 
     *
     * @param cost The new cost function.
     */
    private void setCost(EuclideanDistanceCostFunction cost)
    {
        this.cost = cost;
    }
    
    /**
     * Sets the size of the population for the tests. 
     *
     * @param populationSize The new population size.
     */
    private void setPopulationSize(int populationSize)
    {
        this.populationSize = populationSize;
    }
    
    /**
     * Sets the size of the vectors to be used in the tests. 
     *
     * @param vectorSize The new vector size.
     */
    private void setVectorSize(int vectorSize)
    {
        this.vectorSize = vectorSize;
    }
    
    /**
     * Sets the goal of the tests.
     *
     * @param goal The new goal. 
     */
    private void setGoal(Vector goal)
    {
        this.goal = goal;
    }
    
    /** 
     * Sets the percent of the population to carry over using the tournament selector. 
     *
     * @param carryOverPercent The new percent.
     */
    private void setCarryOverPercent(double carryOverPercent)
    {
        this.carryOverPercent = carryOverPercent;
    }
    
    /** 
     * Sets the percent of the population to crossover.
     *
     * @param crossoverPercent The new percent. 
     */
    private void setCrossoverPercent(double crossoverPercent)
    {
        this.crossoverPercent = crossoverPercent;
    }
    
    /** 
     * Sets the percent of the population to mutate. 
     *
     * @param mutatePercent The new percent.
     */
    private void setMutatePercent(double mutatePercent)
    {
        this.mutatePercent = mutatePercent;
    }

    /**
     * Test of getCostFunction method, of class gov.sandia.isrc.learning.genetic.GeneticAlgorithm.
     */
    public void testGetCostFunction()
    {
        GeneticAlgorithm<Vectorizable, Vectorizable> instance = 
            this.createInstance();
        assertSame(this.cost, instance.getCostFunction());
    }

    /**
     * Test of setCostFunction method, of class gov.sandia.isrc.learning.genetic.GeneticAlgorithm.
     */
    public void testSetCostFunction()
    {
        GeneticAlgorithm<Vectorizable, Vectorizable> instance = 
            this.createInstance();
        assertSame(this.cost, instance.getCostFunction());
        instance.setCostFunction(null);
        assertNull(instance.getCostFunction());
    }

    /**
     * Test of getInitialPopulation method, of class gov.sandia.isrc.learning.genetic.GeneticAlgorithm.
     */
    public void testGetInitialPopulation()
    {
        GeneticAlgorithm<Vectorizable, Vectorizable> instance = 
            this.createInstance();
        assertSame(this.initialPopulation, instance.getInitialPopulation());
    }

    /**
     * Test of setInitialPopulation method, of class gov.sandia.isrc.learning.genetic.GeneticAlgorithm.
     */
    public void testSetInitialPopulation()
    {
        GeneticAlgorithm<Vectorizable, Vectorizable> instance = 
            this.createInstance();
        assertSame(this.initialPopulation, instance.getInitialPopulation());
        Collection<Vectorizable> population = new LinkedList<Vectorizable>();
        instance.setInitialPopulation(population);
        assertSame(population, instance.getInitialPopulation());
    }

    /**
     * Test of getKeepGoing method, of class gov.sandia.isrc.learning.genetic.GeneticAlgorithm.
     */
    public void testGetKeepGoing()
    {
        GeneticAlgorithm<Vectorizable, Vectorizable> instance = 
            this.createInstance();
        assertFalse(instance.getKeepGoing());
    }

    /**
     * Test of setKeepGoing method, of class gov.sandia.isrc.learning.genetic.GeneticAlgorithm.
     */
    public void testSetKeepGoing()
    {
        GeneticAlgorithm<Vectorizable, Vectorizable> instance = 
            this.createInstance();
        assertFalse(instance.getKeepGoing());
        instance.setKeepGoing(false);
        assertFalse(instance.getKeepGoing());
        instance.setKeepGoing(false);
        assertFalse(instance.getKeepGoing());
        instance.setKeepGoing(true);
        assertTrue(instance.getKeepGoing());
    }

    /**
     * Test of isResultValid method, of class gov.sandia.isrc.learning.genetic.GeneticAlgorithm.
     */
    public void testIsResultValid()
    {
        GeneticAlgorithm<Vectorizable, Vectorizable> instance = 
            this.createInstance();
        assertFalse(instance.isResultValid());
        instance.setBestSoFar(
            new EvaluatedGenome<Vectorizable>(0.0, VectorFactory.getDefault().createVector(3)));
        assertTrue(instance.isResultValid());
    }

    /**
     * Test of stop method, of class gov.sandia.isrc.learning.genetic.GeneticAlgorithm.
     */
    public void testStop()
    {
        GeneticAlgorithm<Vectorizable, Vectorizable> instance = 
            this.createInstance();
        instance.setKeepGoing(true);
        instance.stop();
        assertFalse(instance.getKeepGoing());
    }
}
