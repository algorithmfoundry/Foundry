/*
 * File:                ProbabilityUtil.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright June 16, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math;

/**
 * Utility methods for dealing with probabilities.
 * 
 * @author  Justin Basilico
 * @author  Kevin R. Dixon
 * @since   3.0
 */
public class ProbabilityUtil
{
    /**
     * Checks to make sure that probability is between 0.0 and 1.0.
     *
     * @param  probability The probability, must be [0,1].
     */
    public static void assertIsProbability(
        final double probability)
    {
        if ( probability < 0.0 || probability > 1.0 )
        {
            throw new IllegalArgumentException(
                "Probability (" + probability + ") must be in [0.0, 1.0]");
        }
    }

}
