/*
 * File:                ThresholdBinaryCategorizerTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 1, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.categorization;

/**
 * Unit tests for ThresholdBinaryCategorizerTestHarness.
 *
 * @param <InputType> Input type
 * @author krdixon
 */
public abstract class ThresholdBinaryCategorizerTestHarness<InputType>
    extends CategorizerTestHarness<InputType,Boolean>
{

    /**
     * Tests for class ThresholdBinaryCategorizerTestHarness.
     * @param testName Name of the test.
     */
    public ThresholdBinaryCategorizerTestHarness(
        String testName)
    {
        super(testName);
    }


    /**
     * Test of evaluateWithoutThreshold method, of class AbstractThresholdBinaryCategorizer.
     */
    abstract public void testEvaluateWithoutThreshold();


    abstract public AbstractThresholdBinaryCategorizer<InputType> createInstance();

    @Override
    public void testKnownValues()
    {
        this.testEvaluateWithoutThreshold();
    }

    /**
     * Test of evaluate method, of class AbstractThresholdBinaryCategorizer.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        AbstractThresholdBinaryCategorizer<InputType> instance = this.createInstance();
        for( int n = 0; n < NUM_TESTS; n++ )
        {
            InputType input = this.createRandomInput();
            double result = instance.evaluateWithoutThreshold(input);
            assertEquals( (result>=instance.getThreshold()), instance.evaluate(input).booleanValue() );
        }
    }

    /**
     * Test of getThreshold method, of class AbstractThresholdBinaryCategorizer.
     */
    public void testGetThreshold()
    {
        System.out.println("getThreshold");
        AbstractThresholdBinaryCategorizer<?> instance = this.createInstance();
        double threshold = instance.getThreshold() + RANDOM.nextDouble();
        instance.setThreshold(threshold);
        assertEquals( threshold, instance.getThreshold() );
    }

    /**
     * Test of setThreshold method, of class AbstractThresholdBinaryCategorizer.
     */
    public void testSetThreshold()
    {
        System.out.println("setThreshold");
        AbstractThresholdBinaryCategorizer<?> instance = this.createInstance();
        double threshold = instance.getThreshold() + RANDOM.nextDouble();
        instance.setThreshold(threshold);
        assertEquals( threshold, instance.getThreshold() );
    }

}
