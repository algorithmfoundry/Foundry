/*
 * File:                SemimetricTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 26, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.distance;

import gov.sandia.cognition.math.Semimetric;

/**
 * Tests the properties of a semimetric.
 * @param <InputType> Type of input to the Semimetric
 * @author Kevin R. Dixon
 * @since 3.0
 */
public abstract class SemimetricTestHarness<InputType>
    extends DivergenceFunctionTestHarness<InputType,InputType>
{

    /** 
     * Creates a new instance of SemimetricTestHarness 
     * @param testName Name
     */
    public SemimetricTestHarness(
        String testName )
    {
        super(testName);
    }

    public abstract Semimetric<InputType> createInstance();


    @Override
    public InputType generateRandomSecondType()
    {
        return this.generateRandomFirstType();
    }

    /**
     * Tests to make sure f(a,b) == 0.0 iff a==b.
     */
    public void testIndescernible()
    {
        System.out.println( "Indescernible" );

        Semimetric<InputType> f = this.createInstance();
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            InputType a = this.generateRandomFirstType();
            InputType b = this.generateRandomSecondType();
            assertEquals( 0.0, f.evaluate(a, a), TOLERANCE );
            assertEquals( 0.0, f.evaluate(b, b), TOLERANCE );
            assertTrue( f.evaluate(a, b) > 0.0 );
        }

    }

    /**
     * Tests that f(a,b) == f(b,a)
     */
    public void testSymmetry()
    {
        System.out.println( "Symmetry" );
        Semimetric<InputType> f = this.createInstance();
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            InputType a = this.generateRandomFirstType();
            InputType b = this.generateRandomSecondType();
            assertEquals( f.evaluate(a, b), f.evaluate(b, a), TOLERANCE );
        }

    }

}
