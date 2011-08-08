/*
 * File:                CategorizerTestHarness.java
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

import java.util.Iterator;
import junit.framework.TestCase;
import java.util.Random;
import java.util.Set;

/**
 * Unit tests for CategorizerTestHarness.
 *
 * @param <InputType> Input type
 * @param <CategoryType> Category type.
 * @author krdixon
 */
public abstract class CategorizerTestHarness<InputType,CategoryType>
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
     * Number of tests to run
     */
    public final int NUM_TESTS = 100;

    /**
     * Tests for class CategorizerTestHarness.
     * @param testName Name of the test.
     */
    public CategorizerTestHarness(
        String testName)
    {
        super(testName);
    }


    /**
     * Creates a new instance of the Categorizer
     * @return
     * Categorizer
     */
    public abstract Categorizer<InputType,CategoryType> createInstance();

    /**
     * Creates a random input
     * @return
     * Random input
     */
    public abstract InputType createRandomInput();

    /**
     * Tests the constructors of class CategorizerTestHarness.
     */
    public abstract void testConstructors();

    /**
     * Test of evaluate() method
     */
    public abstract void testKnownValues();

    /**
     * Tests clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );
        Categorizer<InputType,CategoryType> instance = this.createInstance();
        assertNotNull( instance );
        @SuppressWarnings("unchecked")
        Categorizer<InputType,CategoryType> clone =
            (Categorizer<InputType, CategoryType>) instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        Set<? extends CategoryType> originalSet = instance.getCategories();
        Set<? extends CategoryType> cloneSet = instance.getCategories();
        assertEquals( originalSet.size(), cloneSet.size() );
        Iterator<? extends CategoryType> i1 = originalSet.iterator();
        Iterator<? extends CategoryType> i2 = cloneSet.iterator();
        int num = originalSet.size();
        for( int n = 0; n < num; n++ )
        {
            assertSame( i1.next(), i2.next() );
        }

    }

    /**
     * Tests the evaluate method against random inputs and makes sure the
     * result is in the category set.
     */
    public void testRandomEvaluate()
    {
        System.out.println( "random evaluate" );

        Categorizer<InputType,CategoryType> instance = this.createInstance();

        for( int n = 0; n < NUM_TESTS; n++ )
        {
            InputType input = this.createRandomInput();
            CategoryType category = instance.evaluate(input);
            assertNotNull( category );
            assertTrue( instance.getCategories().contains(category) );
        }
    }

    /**
     * Test of getCategories method, of class Categorizer.
     */
    public void testGetCategories()
    {
        System.out.println("getCategories");
        Categorizer<InputType,CategoryType> instance = this.createInstance();

        assertNotNull( instance.getCategories() );
        assertTrue( instance.getCategories().size() > 0 );
    }

}
