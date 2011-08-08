/*
 * File:                RingAveragerTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 7, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.math.RingAverager;
import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.math.RingAccumulator;
import java.util.Collection;
import java.util.LinkedList;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit tests for RingAveragerTest
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class RingAveragerTest extends TestCase
{

    /**
     * 
     * @param testName
     */
    public RingAveragerTest( String testName )
    {
        super( testName );
    }

    /**
     * 
     * @return
     */
    public static Test suite()
    {
        TestSuite suite = new TestSuite( RingAveragerTest.class );

        return suite;
    }

    /**
     * Test of create method, of class gov.sandia.isrc.learning.util.data.RingAverager.
     */
    public void testCreateObject()
    {
        System.out.println( "createObject" );

        Collection<ComplexNumber> data = new LinkedList<ComplexNumber>();
        int N = (int) (Math.random() * 100) + 10;
        for (int i = 0; i < N; i++)
        {
            data.add( new ComplexNumber( Math.random(), Math.random() ) );
        }

        RingAverager<ComplexNumber> instance =
            new RingAverager<ComplexNumber>();
        ComplexNumber average = instance.summarize( data );

        RingAccumulator<ComplexNumber> acc =
            new RingAccumulator<ComplexNumber>( data );

        assertEquals( acc.getMean(), average );

    }

}
