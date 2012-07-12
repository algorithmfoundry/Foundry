/*
 * File:                AbstractBinaryCategorizerTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 29, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.categorization;

import java.util.Set;
import java.util.Random;

/**
 * Unit tests for AbstractBinaryCategorizerTest.
 *
 * @author krdixon
 */
public class AbstractBinaryCategorizerTest
    extends CategorizerTestHarness<Double,Boolean>
{


    /**
     * Tests for class AbstractBinaryCategorizerTest.
     * @param testName Name of the test.
     */
    public AbstractBinaryCategorizerTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class AbstractBinaryCategorizerTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        AbstractBinaryCategorizer<?> instance = this.createInstance();
        assertSame( AbstractBinaryCategorizer.BINARY_CATEGORIES, instance.getCategories() );
    }

    @Override
    public ScalarThresholdBinaryCategorizer createInstance()
    {
        return new ScalarThresholdBinaryCategorizer( RANDOM.nextGaussian() );
    }

    @Override
    public Double createRandomInput()
    {
        return RANDOM.nextGaussian();
    }

    @Override
    public void testKnownValues()
    {
        System.out.println( "Known values" );

        ScalarThresholdBinaryCategorizer f = this.createInstance();
        for( int n = 0; n < NUM_TESTS; n++ )
        {
            double input = this.createRandomInput();
            assertEquals( (input >= f.getThreshold()), f.evaluate(input).booleanValue() );
        }

    }

}
