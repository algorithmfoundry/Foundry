/*
 * File:                HolmCorrectionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 21, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for class HolmCorrectionTest.
 * @author krdixon
 */
public class HolmCorrectionTest
    extends MultipleHypothesisComparisonTestHarness
{

    /**
     * Default Constructor
     */
    public HolmCorrectionTest()
    {
    }

    @Override
    public HolmCorrection createInstance()
    {
        return new HolmCorrection();
    }

    /**
     * Tests the constructors of class HolmCorrectionTest.
     */
    @Test
    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        HolmCorrection instance = new HolmCorrection();
        assertNotNull( instance.getPairwiseTest() );
    }

    @Override
    public void testKnownValues()
    {
    }

}