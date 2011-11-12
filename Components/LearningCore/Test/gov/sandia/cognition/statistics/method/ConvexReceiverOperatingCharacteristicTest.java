/*
 * File:                ConvexReceiverOperatingCharacteristicTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Oct 17, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Tests for class ConvexReceiverOperatingCharacteristicTest.
 * @author krdixon
 */
public class ConvexReceiverOperatingCharacteristicTest
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
     * Default number of samples to test against, {@value}.
     */
    public final int NUM_SAMPLES = 1000;


    /**
     * Default Constructor
     */
    public ConvexReceiverOperatingCharacteristicTest(
        String testName )
    {
        super( testName );
    }

    /**
     * Tests the constructors of class ConvexReceiverOperatingCharacteristicTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
    }

    public static ConvexReceiverOperatingCharacteristic createKnownInstance()
    {

        LinkedList<InputOutputPair<Double,Boolean>> data =
            new LinkedList<InputOutputPair<Double, Boolean>>();

        // From http://www.icml-2011.org/papers/385_icmlpaper.pdf
        // Note that Figure 1 on page 3 IS WRONG!!!!!!
        // However, their AUC and AUCH are correct
        data.add( DefaultInputOutputPair.create( 0.95, true ) );
        data.add( DefaultInputOutputPair.create( 0.9,  true ) );
        data.add( DefaultInputOutputPair.create( 0.8,  false ) );
        data.add( DefaultInputOutputPair.create( 0.7,  true ) );
        data.add( DefaultInputOutputPair.create( 0.65, true ) );
        data.add( DefaultInputOutputPair.create( 0.6,  true ) );
        data.add( DefaultInputOutputPair.create( 0.5,  true ) );
        data.add( DefaultInputOutputPair.create( 0.4,  false ) );
        data.add( DefaultInputOutputPair.create( 0.3,  true ) );
        data.add( DefaultInputOutputPair.create( 0.2,  true ) );
        data.add( DefaultInputOutputPair.create( 0.1,  false ) );
        data.add( DefaultInputOutputPair.create( 0.05, false ) );

        ReceiverOperatingCharacteristic roc =
            ReceiverOperatingCharacteristic.create(data);
        return ConvexReceiverOperatingCharacteristic.computeConvexNull(roc);
    }

    /**
     * Tests the clone method of class ConvexReceiverOperatingCharacteristicTest.
     */
    public void testClone()
    {
        System.out.println( "Clone" );
        ConvexReceiverOperatingCharacteristic croc = createKnownInstance();
        ConvexReceiverOperatingCharacteristic clone = croc.clone();
        assertNotSame( croc.getConvexHull(), clone.getConvexHull() );
        assertEquals( croc.computeAreaUnderConvexHull(), clone.computeAreaUnderConvexHull() );
    }

    /**
     * Test of evaluate method, of class ConvexReceiverOperatingCharacteristic.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");

        ConvexReceiverOperatingCharacteristic croc = createKnownInstance();

        assertEquals( 0.25, croc.evaluate(0.0), TOLERANCE );
        assertEquals( 0.45, croc.evaluate(0.1), TOLERANCE );
        assertEquals( 0.65, croc.evaluate(0.2), TOLERANCE );
        assertEquals( 0.80, croc.evaluate(0.3), TOLERANCE );
        assertEquals( 0.90, croc.evaluate(0.4), TOLERANCE );
        assertEquals( 1.00, croc.evaluate(0.5), TOLERANCE );
        assertEquals( 1.00, croc.evaluate(0.6), TOLERANCE );
        assertEquals( 1.00, croc.evaluate(0.7), TOLERANCE );
        assertEquals( 1.00, croc.evaluate(0.8), TOLERANCE );
        assertEquals( 1.00, croc.evaluate(0.9), TOLERANCE );
        assertEquals( 1.00, croc.evaluate(1.0), TOLERANCE );

        assertEquals( 0.00, croc.evaluate(-0.5), TOLERANCE );
        assertEquals( 1.00, croc.evaluate(1.5), TOLERANCE );

    }

    /**
     * Test of computeAreaUnderConvexHull method, of class ConvexReceiverOperatingCharacteristic.
     */
    public void testComputeAreaUnderConvexHull()
    {
        System.out.println("computeAreaUnderConvexHull");

        ReceiverOperatingCharacteristic roc =
            ReceiverOperatingCharacteristicTest.createKnownInstance();

        ConvexReceiverOperatingCharacteristic croc = createKnownInstance();
        double auch = croc.computeAreaUnderConvexHull();
        System.out.println( "AUCH = " + auch );
        assertEquals( 0.84375, auch, TOLERANCE );
    }


}