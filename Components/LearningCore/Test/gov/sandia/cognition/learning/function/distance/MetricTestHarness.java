/*
 * File:                MetricTestHarness.java
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

import gov.sandia.cognition.math.Metric;

/**
 * Tests the properties of Metrics
 * @param <InputType> Input type
 * @author Kevin R. Dixon
 * @since 3.0
 */
public abstract class MetricTestHarness<InputType>
    extends SemimetricTestHarness<InputType>
{

    /** 
     * Creates a new instance of MetricTestHarness 
     * @param testName Name
     */
    public MetricTestHarness(
        String testName )
    {
        super( testName );
    }

    public abstract Metric<InputType> createInstance();

    /**
     * Tests the triangle inequality.
     */
    public void testTriangleInequality()
    {
        System.out.println( "Triangle Inequality" );

        Metric<InputType> f = this.createInstance();
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            InputType a = this.generateRandomFirstType();
            InputType b = this.generateRandomFirstType();
            InputType c = this.generateRandomFirstType();

            double fab = f.evaluate(a, b);
            double fac = f.evaluate(a, c);
            double fbc = f.evaluate(b, c);
            /*
            System.out.println( "\na = " + a );
            System.out.println( "b = " + b );
            System.out.println( "c = " + c );
            System.out.println( "fab = " + fab );
            System.out.println( "fbc = " + fbc );
            System.out.println( "fac = " + fac );
             */
            

            // fab+fab >= fac
            assertTrue( "fab + fbc (" + (fab+fbc) + ") must be >= fac (" + fac + ")", (fab + fbc + TOLERANCE) >= fac );
        }

    }
    
}
