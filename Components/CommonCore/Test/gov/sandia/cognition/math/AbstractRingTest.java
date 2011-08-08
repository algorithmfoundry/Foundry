/*
 * File:                AbstractRingTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 16, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This class assumes that ComplexNumber has been THOROUGHLY tested and that
 * the inline versions of the functions here work, as does clone
 *
 * @author Kevin R. Dixon
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2006-07-18",
            changesNeeded=false,
            comments="Looks fine."
        )
        ,
        @CodeReview(
            reviewer="Jonathan McClain",
            date="2006-05-16",
            changesNeeded=false,
            comments={
                "Added proper file header.",
                "Added documentation for a few functions.",
                "Just my own preference, but I prefer to have a message for each \"assert\" statement indicating what the problem is if it fails.",
                "This way I don't have to go into the code to see what has gone wrong."
            }
        )
    }
)
public class AbstractRingTest
    extends RingTestHarness<ComplexNumber>
{


    
    protected ComplexNumber createRandom()
    {
        double r = (RANDOM.nextDouble() * 2.0 * RANGE) - RANGE;
        double i = (RANDOM.nextDouble() * 2.0 * RANGE) - RANGE;
        return new ComplexNumber( r, i );
    }

    
    public void testScaleEquals()
    {
        // this is not implemented, but that's OK since it's not in AbstractRing
    }

    public void testPlusEquals()
    {
        // this is not implemented, but that's OK since it's not in AbstractRing
    }

    public void testDotTimesEquals()
    {
        // this is not implemented, but that's OK since it's not in AbstractRing        
    }    
    
    /**
     * Creates a new instance of AbstractRingTest.
     *
     * @param testName The name of the test.
     */
    public AbstractRingTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Returns the test.
     * @return Stuff
     */
    public static Test suite()
    {
        TestSuite suite = new TestSuite(AbstractRingTest.class);

        return suite;
    }



}
