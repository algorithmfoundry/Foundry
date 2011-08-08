/*
 * File:                AbstractConfidenceStatisticTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 26, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *  
 * 
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.statistics.method.AbstractConfidenceStatistic;
import junit.framework.TestCase;

/**
 * JUnit tests for class AbstractConfidenceStatisticTest
 * @author Kevin R. Dixon
 */
public class AbstractConfidenceStatisticTest
    extends TestCase
{

    public static class ACS
        extends AbstractConfidenceStatistic
    {

        public ACS( double prob )
        {
            super( prob );
        }

        @Override
        public ACS clone()
        {
            return new ACS( this.getNullHypothesisProbability() );
        }

    }

    public ACS createInstance()
    {
        return new ACS( Math.random() );
    }

    /**
     * Entry point for JUnit tests for class AbstractConfidenceStatisticTest
     * @param testName name of this test
     */
    public AbstractConfidenceStatisticTest(
        String testName )
    {
        super( testName );
    }

    /**
     * Test of clone method, of class AbstractConfidenceStatistic.
     */
    public void testClone()
    {
        System.out.println( "clone" );
        ACS instance = this.createInstance();
        ACS clone = instance.clone();
        assertNotSame( instance, clone );
        assertEquals( instance.getNullHypothesisProbability(), clone.getNullHypothesisProbability() );
    }

    /**
     * Test of getNullHypothesisProbability method, of class AbstractConfidenceStatistic.
     */
    public void testGetNullHypothesisProbability()
    {
        System.out.println( "getNullHypothesisProbability" );
        double prob = Math.random();
        ACS instance = new ACS( prob );
        assertEquals( prob, instance.getNullHypothesisProbability() );

    }

    /**
     * Test of setNullHypothesisProbability method, of class AbstractConfidenceStatistic.
     */
    public void testSetNullHypothesisProbability()
    {
        System.out.println( "setNullHypothesisProbability" );
        double prob = Math.random();
        ACS instance = new ACS( prob );
        assertEquals( prob, instance.getNullHypothesisProbability() );

        prob *= 0.5;
        instance.setNullHypothesisProbability( prob );
        assertEquals( prob, instance.getNullHypothesisProbability() );

        instance.setNullHypothesisProbability( 0.0 );
        instance.setNullHypothesisProbability( 1.0 );

        try
        {
            instance.setNullHypothesisProbability( -1.0 );
            fail( "Prob must be [0,1]" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            instance.setNullHypothesisProbability( 2.0 );
            fail( "Prob must be [0,1]" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }


    }

}
