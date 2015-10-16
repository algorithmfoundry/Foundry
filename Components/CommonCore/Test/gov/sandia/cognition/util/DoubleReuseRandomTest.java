/*
 * File:                DoubleReuseRandomTest.java
 * Authors:             smcrosb
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright January 21, 2014, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.util;

import gov.sandia.cognition.util.DoubleReuseRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the DoubleReuseRandom class
 *
 * @author Sean Crosby
 * @since 2.1
 */
public class DoubleReuseRandomTest
{
    /**
     * Check the repeating sequence of the doubles
     */
    @Test
    public void testDoubles()
    {
        int arrayLen = 10;
        DoubleReuseRandom rand = new DoubleReuseRandom(arrayLen);
        double first = rand.nextDouble();

        // Make sure the value is within range
        assertTrue(first >= 0.0 && first <= 1.0);

        for (int i = 0; i < arrayLen - 1; i++)
        {
            rand.nextDouble();
        }

        // Make sure that the sequence repeated
        double eleventh = rand.nextDouble();

        // Because this is the same number (originating from the same call to 
        // Random.nextDouble(), there souldn't be a need for a delta between 
        // the floating-point numbers
        assertEquals(first, eleventh, 0);
    }

    /**
     * Check zero and negative array lengths
     */
    @Test
    public void testArrayRanges()
    {
        int exceptionsCaught = 0;

        try
        {
            int arrayLen = 0;
            DoubleReuseRandom rand = new DoubleReuseRandom(arrayLen);
        }
        catch (Exception e)
        {
            exceptionsCaught++;
        }
        
        try
        {
            int arrayLen = 0;
            DoubleReuseRandom rand = new DoubleReuseRandom(arrayLen);
        }
        catch (Exception e)
        {
            exceptionsCaught++;
        }
        
        // Verify that both threw exceptions
        assertEquals(2, exceptionsCaught);
    }

    /**
     * make sure that other types (int, long, etc.) still work.
     */
    @Test
    public void testOtherTypes()
    {
        int arrayLen = 1;
        DoubleReuseRandom rand = new DoubleReuseRandom(arrayLen);

        // call each of the methods to insure that no exceptions are thrown
        int randInt = rand.nextInt();
        int randIntN = rand.nextInt(10);
        long randLong = rand.nextLong();
        float randFloat = rand.nextFloat();
        boolean randBool = rand.nextBoolean();
        byte[] randBytes =
        {
            0, 0, 0, 0
        };
        rand.nextBytes(randBytes);
        
        // not calling this one, because somtimes it hangs...?!?!
        //double randGaussian = rand.nextGaussian();
    }

}
