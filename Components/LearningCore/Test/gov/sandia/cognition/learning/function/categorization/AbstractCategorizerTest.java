/*
 * File:                AbstractCategorizerTest.java
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

/**
 * Unit tests for AbstractCategorizerTest.
 *
 * @author krdixon
 */
public class AbstractCategorizerTest
    extends CategorizerTestHarness<Double,Boolean>
{

    /**
     * Tests for class AbstractCategorizerTest.
     * @param testName Name of the test.
     */
    public AbstractCategorizerTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Type of AbstractCategorizer
     */
    public class MyAbstractCategorizer
        extends AbstractCategorizer<Double,Boolean>
    {

        /**
         * Threshold
         */
        public final double threshold = RANDOM.nextDouble();

        /**
         * Default constructor
         */
        public MyAbstractCategorizer()
        {
            this.setCategories( AbstractBinaryCategorizer.BINARY_CATEGORIES );
        }

        public Boolean evaluate(
            Double input)
        {
            return (input >= threshold) ? Boolean.TRUE : Boolean.FALSE;
        }
        
    }

    /**
     * Tests the constructors of class AbstractCategorizerTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        assertNotNull( this.createInstance() );
    }

    @Override
    public MyAbstractCategorizer createInstance()
    {
        return new MyAbstractCategorizer();
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

        MyAbstractCategorizer f = this.createInstance();
        for( int n = 0; n < NUM_TESTS; n++ )
        {
            double input = this.createRandomInput();
            assertEquals( (input >= f.threshold), f.evaluate(input).booleanValue() );
        }

    }

    /**
     * Test of setCategories
     */
    public void testSetCategories()
    {
        System.out.println( "setCategories" );

        AbstractCategorizer<Double,Boolean> f = this.createInstance();
        Set<Boolean> set = f.getCategories();
        assertNotNull( set );
        f.setCategories(null);
        assertNull( f.getCategories() );
        f.setCategories(set);
        assertSame( set, f.getCategories() );
    }

}
