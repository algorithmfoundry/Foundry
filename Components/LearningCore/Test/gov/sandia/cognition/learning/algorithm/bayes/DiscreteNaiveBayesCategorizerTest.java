/*
 * File:                DiscreteNaiveBayesCategorizerTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Oct 21, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.bayes;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.statistics.distribution.MapBasedDataHistogram;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for DiscreteNaiveBayesCategorizerTest.
 *
 * @author krdixon
 */
@PublicationReference(
    author="Raymond J. Mooney",
    title="CS 391L: Machine Learning: Bayesian Learning: Naive Bayes",
    type=PublicationType.Misc,
    year=2009,
    url="http://www.cs.utexas.edu/~mooney/cs391L/slides/naive-bayes.pdf",
    notes="Undergrad course lecture notes."
)
public class DiscreteNaiveBayesCategorizerTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public final double TOLERANCE = 1e-5;

    /**
     * Class 1
     */
    public static final String CLASS1 = "positive";

    /**
     * Class 2
     */
    public static final String CLASS2 = "negative";

    /**
     * Tests for class DiscreteNaiveBayesCategorizerTest.
     * @param testName Name of the test.
     */
    public DiscreteNaiveBayesCategorizerTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Create instance
     * @return
     * instance
     */
    @SuppressWarnings("unchecked")
    public DiscreteNaiveBayesCategorizer<Boolean,String> createInstance()
    {

        // From 
        Map<String,List<MapBasedDataHistogram<Boolean>>> conditionalPMFs =
            new LinkedHashMap<String, List<MapBasedDataHistogram<Boolean>>>();

        MapBasedDataHistogram<Boolean> p0 = new MapBasedDataHistogram<Boolean>();
        p0.add( Boolean.TRUE, 10 );
        p0.add( Boolean.FALSE, 90 );

        MapBasedDataHistogram<Boolean> p1 = new MapBasedDataHistogram<Boolean>();
        p1.add( Boolean.TRUE, 90 );
        p1.add( Boolean.FALSE, 10 );

        MapBasedDataHistogram<Boolean> p2 = new MapBasedDataHistogram<Boolean>();
        p2.add( Boolean.TRUE, 90 );
        p2.add( Boolean.FALSE, 10 );

        MapBasedDataHistogram<Boolean> n0 = new MapBasedDataHistogram<Boolean>();
        n0.add( Boolean.TRUE, 20 );
        n0.add( Boolean.FALSE, 80 );

        MapBasedDataHistogram<Boolean> n1 = new MapBasedDataHistogram<Boolean>();
        n1.add( Boolean.TRUE, 30 );
        n1.add( Boolean.FALSE, 70 );

        MapBasedDataHistogram<Boolean> n2 = new MapBasedDataHistogram<Boolean>();
        n2.add( Boolean.TRUE, 30 );
        n2.add( Boolean.FALSE, 70 );

        conditionalPMFs.put(CLASS1, Arrays.asList( p0, p1, p2 ) );
        conditionalPMFs.put(CLASS2, Arrays.asList( n0, n1, n2 ) );

        MapBasedDataHistogram<String> priors =
            new MapBasedDataHistogram<String>();
        priors.add( CLASS1, 300 );
        priors.add( CLASS2, 300 );

        return new DiscreteNaiveBayesCategorizer<Boolean, String>(
                3, priors, conditionalPMFs );
        
    }

    /**
     * createInstance
     */
    public void testCreateInstance()
    {
        System.out.println( "createInstance" );

        DiscreteNaiveBayesCategorizer<Boolean, String> instance =
            this.createInstance();

        assertEquals( 3, instance.getInputDimensionality() );
        assertEquals( 0.5, instance.getPriorProbability(CLASS1) );
        assertEquals( 0.5, instance.getPriorProbability(CLASS2) );

        assertEquals( 0.1, instance.getConditionalProbability(0, Boolean.TRUE, CLASS1) );
        assertEquals( 0.9, instance.getConditionalProbability(0, Boolean.FALSE, CLASS1) );

        assertEquals( 0.9, instance.getConditionalProbability(1, Boolean.TRUE, CLASS1) );
        assertEquals( 0.1, instance.getConditionalProbability(1, Boolean.FALSE, CLASS1) );

        assertEquals( 0.9, instance.getConditionalProbability(2, Boolean.TRUE, CLASS1) );
        assertEquals( 0.1, instance.getConditionalProbability(2, Boolean.FALSE, CLASS1) );


        assertEquals( 0.2, instance.getConditionalProbability(0, Boolean.TRUE, CLASS2) );
        assertEquals( 0.8, instance.getConditionalProbability(0, Boolean.FALSE, CLASS2) );

        assertEquals( 0.3, instance.getConditionalProbability(1, Boolean.TRUE, CLASS2) );
        assertEquals( 0.7, instance.getConditionalProbability(1, Boolean.FALSE, CLASS2) );

        assertEquals( 0.3, instance.getConditionalProbability(2, Boolean.TRUE, CLASS2) );
        assertEquals( 0.7, instance.getConditionalProbability(2, Boolean.FALSE, CLASS2) );

    }

    /**
     * Tests the constructors of class DiscreteNaiveBayesCategorizerTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        DiscreteNaiveBayesCategorizer<Boolean, String> instance =
            new DiscreteNaiveBayesCategorizer<Boolean, String>();
        assertEquals( 0, instance.getInputDimensionality() );
        assertEquals( 0, instance.getCategories().size() );

    }

    /**
     * Test of clone method, of class DiscreteNaiveBayesCategorizer.
     */
    public void testClone()
    {
        System.out.println("clone");
        DiscreteNaiveBayesCategorizer<Boolean,String> instance = this.createInstance();
        DiscreteNaiveBayesCategorizer<Boolean,String> clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertEquals( instance.getInputDimensionality(), clone.getInputDimensionality() );

        double prior = instance.getPriorProbability(CLASS1);
        assertEquals( prior, clone.getPriorProbability(CLASS1) );
        clone.update( Arrays.asList(true,false,true), CLASS1);
        assertEquals( prior, instance.getPriorProbability(CLASS1) );
        assertEquals( 301.0/601.0, clone.getPriorProbability(CLASS1) );

    }

    /**
     * Test of getCategories method, of class DiscreteNaiveBayesCategorizer.
     */
    public void testGetCategories()
    {
        System.out.println("getCategories");
        DiscreteNaiveBayesCategorizer<Boolean,String> instance = this.createInstance();
        assertEquals( 2, instance.getCategories().size() );
        assertEquals( CLASS1, CollectionUtil.getElement( instance.getCategories(), 0 ) );
        assertEquals( CLASS2, CollectionUtil.getElement( instance.getCategories(), 1 ) );
    }

    /**
     * computeConjuctiveProbability
     */
    public void testComputeConjuctiveProbability()
    {
        System.out.println( "computeConjuctiveProbability" );

        DiscreteNaiveBayesCategorizer<Boolean, String> instance =
            this.createInstance();

        List<Boolean> inputs = Arrays.asList( Boolean.TRUE, Boolean.TRUE, Boolean.TRUE );
        assertEquals( 0.0405, instance.computeConjuctiveProbability( inputs, CLASS1), TOLERANCE );
        assertEquals( 0.0090, instance.computeConjuctiveProbability( inputs, CLASS2), TOLERANCE );

        assertEquals( 0.0, instance.computeConjuctiveProbability( inputs, "zero"), TOLERANCE );


    }

    /**
     * computePosterior
     */
    public void testComputePosterior()
    {
        System.out.println( "computePosterior" );

        DiscreteNaiveBayesCategorizer<Boolean, String> instance =
            this.createInstance();

        List<Boolean> inputs = Arrays.asList( Boolean.TRUE, Boolean.TRUE, Boolean.TRUE );
        assertEquals( 0.818181, instance.computePosterior( inputs, CLASS1), TOLERANCE );
        assertEquals( 0.181818, instance.computePosterior( inputs, CLASS2), TOLERANCE );

    }

    /**
     * computeEvidenceProbability
     */
    public void testComputeEvidenceProbability()
    {
        System.out.println( "computeEvidenceProbability" );
        
        DiscreteNaiveBayesCategorizer<Boolean, String> instance =
            this.createInstance();

        List<Boolean> inputs = Arrays.asList( Boolean.TRUE, Boolean.TRUE, Boolean.TRUE );
        assertEquals( 0.0495, instance.computeEvidenceProbabilty( inputs ), TOLERANCE );
        
    }

    /**
     * Test of evaluate method, of class DiscreteNaiveBayesCategorizer.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");

        DiscreteNaiveBayesCategorizer<Boolean, String> instance =
            this.createInstance();
        assertEquals( CLASS1, instance.evaluate( Arrays.asList( true, true, true ) ) );

        try
        {
            instance.evaluate( Arrays.asList( true, true ) );
            fail( "Input dimension doesn't match" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getInputDimensionality method, of class DiscreteNaiveBayesCategorizer.
     */
    public void testGetInputDimensionality()
    {
        System.out.println("getInputDimensionality");
        DiscreteNaiveBayesCategorizer instance = this.createInstance();
        assertEquals( 3, instance.getInputDimensionality() );
    }

    /**
     * Test of setInputDimensionality method, of class DiscreteNaiveBayesCategorizer.
     */
    public void testSetInputDimensionality()
    {
        System.out.println("setInputDimensionality");
        DiscreteNaiveBayesCategorizer<Boolean,String> instance = this.createInstance();
        assertEquals( 3, instance.getInputDimensionality() );

        instance.setInputDimensionality(1);
        assertEquals( 0, instance.getCategories().size() );
    }

    /**
     * Update
     */
    public void testUpdate()
    {
        System.out.println( "update" );

        DiscreteNaiveBayesCategorizer<String,Integer> instance =
            new DiscreteNaiveBayesCategorizer<String,Integer>();

        assertEquals( 0, instance.getInputDimensionality() );
        instance.update( Arrays.asList( "small", "red", "circle" ), 1 );
        assertEquals( 3, instance.getInputDimensionality() );
        assertEquals( 1, instance.getCategories().size() );

        instance.update( Arrays.asList( "large", "red", "circle" ), 1 );
        assertEquals( 3, instance.getInputDimensionality() );
        assertEquals( 1, instance.getCategories().size() );

        instance.update( Arrays.asList( "small", "red", "circle" ), 2 );
        assertEquals( 3, instance.getInputDimensionality() );
        assertEquals( 2, instance.getCategories().size() );
        assertEquals( 2.0/3.0, instance.getPriorProbability(1) );
        assertEquals( 1.0/3.0, instance.getPriorProbability(2) );

        instance.update( Arrays.asList( "large", "blue", "triangle" ), 2 );
        assertEquals( 3, instance.getInputDimensionality() );
        assertEquals( 2, instance.getCategories().size() );
        assertEquals( 2.0/4.0, instance.getPriorProbability(1) );
        assertEquals( 2.0/4.0, instance.getPriorProbability(2) );

        // input 0
        assertEquals( 0.5, instance.getConditionalProbability(0, "small", 1 ) );
        assertEquals( 0.0, instance.getConditionalProbability(0, "medium", 1 ) );
        assertEquals( 0.5, instance.getConditionalProbability(0, "large", 1 ) );
        
        assertEquals( 0.5, instance.getConditionalProbability(0, "small", 2 ) );
        assertEquals( 0.0, instance.getConditionalProbability(0, "medium", 2 ) );
        assertEquals( 0.5, instance.getConditionalProbability(0, "large", 2 ) );

        // input 1
        assertEquals( 1.0, instance.getConditionalProbability(1, "red", 1 ) );
        assertEquals( 0.0, instance.getConditionalProbability(1, "blue", 1 ) );
        assertEquals( 0.0, instance.getConditionalProbability(1, "green", 1 ) );

        assertEquals( 0.5, instance.getConditionalProbability(1, "red", 2 ) );
        assertEquals( 0.5, instance.getConditionalProbability(1, "blue", 2 ) );
        assertEquals( 0.0, instance.getConditionalProbability(1, "green", 2 ) );

        // input 2
        assertEquals( 0.0, instance.getConditionalProbability(2, "square", 1 ) );
        assertEquals( 0.0, instance.getConditionalProbability(2, "triangle", 1 ) );
        assertEquals( 1.0, instance.getConditionalProbability(2, "circle", 1 ) );

        assertEquals( 0.0, instance.getConditionalProbability(2, "square", 2 ) );
        assertEquals( 0.5, instance.getConditionalProbability(2, "triangle", 2 ) );
        assertEquals( 0.5, instance.getConditionalProbability(2, "circle", 2 ) );


        // Test a couple of thigs
        List<String> i1 = Arrays.asList("medium", "red", "circle");
        assertEquals( 0.0, instance.computeConjuctiveProbability( i1, 1 ) );
        assertEquals( 0.0, instance.computeConjuctiveProbability( i1, 2 ) );
        assertEquals( 0.0, instance.computeEvidenceProbabilty( i1 ) );
        assertEquals( 0.0, instance.computePosterior( i1, 1 ) );
        assertEquals( 0.0, instance.computePosterior( i1, 2 ) );


        List<String> i2 = Arrays.asList("small",null,null);
        assertEquals( 0.5, instance.computeEvidenceProbabilty(i2));

        List<String> i3 = Arrays.asList(null,"red",null);
        assertEquals( 0.75, instance.computeEvidenceProbabilty(i3));

        List<String> i4 = Arrays.asList(null,"red","circle");
        assertEquals( 1.0, instance.computeConditionalProbability(i4,1));
        assertEquals( 0.25, instance.computeConditionalProbability(i4,2));

        instance.update(i4, 1);
        assertEquals( 0.6, instance.getPriorProbability(1) );
        assertEquals( 0.857142857, instance.computePosterior(i4, 1), TOLERANCE );

        try
        {
            instance.update( Arrays.asList("fail",null), 1 );
            fail( "Input dimensionality doesn't match");
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Learner
     */
    public void testLearner()
    {
        System.out.println( "Learner" );


        DiscreteNaiveBayesCategorizer.Learner<String,Integer> learner =
            new DiscreteNaiveBayesCategorizer.Learner<String, Integer>();

        LinkedList<InputOutputPair<List<String>,Integer>> data =
            new LinkedList<InputOutputPair<List<String>, Integer>>();
        data.add( DefaultInputOutputPair.create(Arrays.asList( "small", "red", "circle" ), 1 ));
        data.add( DefaultInputOutputPair.create(Arrays.asList( "large", "red", "circle" ), 1 ));
        data.add( DefaultInputOutputPair.create(Arrays.asList( "small", "red", "circle" ), 2 ));
        data.add( DefaultInputOutputPair.create(Arrays.asList( "large", "blue", "triangle" ), 2) );

        DiscreteNaiveBayesCategorizer<String,Integer> instance = learner.learn(data);

        assertEquals( 3, instance.getInputDimensionality() );
        assertEquals( 2, instance.getCategories().size() );
        assertEquals( 2.0/4.0, instance.getPriorProbability(1) );
        assertEquals( 2.0/4.0, instance.getPriorProbability(2) );

        // input 0
        assertEquals( 0.5, instance.getConditionalProbability(0, "small", 1 ) );
        assertEquals( 0.0, instance.getConditionalProbability(0, "medium", 1 ) );
        assertEquals( 0.5, instance.getConditionalProbability(0, "large", 1 ) );

        assertEquals( 0.5, instance.getConditionalProbability(0, "small", 2 ) );
        assertEquals( 0.0, instance.getConditionalProbability(0, "medium", 2 ) );
        assertEquals( 0.5, instance.getConditionalProbability(0, "large", 2 ) );

        // input 1
        assertEquals( 1.0, instance.getConditionalProbability(1, "red", 1 ) );
        assertEquals( 0.0, instance.getConditionalProbability(1, "blue", 1 ) );
        assertEquals( 0.0, instance.getConditionalProbability(1, "green", 1 ) );

        assertEquals( 0.5, instance.getConditionalProbability(1, "red", 2 ) );
        assertEquals( 0.5, instance.getConditionalProbability(1, "blue", 2 ) );
        assertEquals( 0.0, instance.getConditionalProbability(1, "green", 2 ) );

        // input 2
        assertEquals( 0.0, instance.getConditionalProbability(2, "square", 1 ) );
        assertEquals( 0.0, instance.getConditionalProbability(2, "triangle", 1 ) );
        assertEquals( 1.0, instance.getConditionalProbability(2, "circle", 1 ) );

        assertEquals( 0.0, instance.getConditionalProbability(2, "square", 2 ) );
        assertEquals( 0.5, instance.getConditionalProbability(2, "triangle", 2 ) );
        assertEquals( 0.5, instance.getConditionalProbability(2, "circle", 2 ) );


        // Test a couple of thigs
        List<String> i1 = Arrays.asList("medium", "red", "circle");
        assertEquals( 0.0, instance.computeConjuctiveProbability( i1, 1 ) );
        assertEquals( 0.0, instance.computeConjuctiveProbability( i1, 2 ) );
        assertEquals( 0.0, instance.computeEvidenceProbabilty( i1 ) );


        List<String> i2 = Arrays.asList("small",null,null);
        assertEquals( 0.5, instance.computeEvidenceProbabilty(i2));

        List<String> i3 = Arrays.asList(null,"red",null);
        assertEquals( 0.75, instance.computeEvidenceProbabilty(i3));

        List<String> i4 = Arrays.asList(null,"red","circle");
        assertEquals( 1.0, instance.computeConditionalProbability(i4,1));
        assertEquals( 0.25, instance.computeConditionalProbability(i4,2));

        assertNotNull( learner.clone() );

    }

}
