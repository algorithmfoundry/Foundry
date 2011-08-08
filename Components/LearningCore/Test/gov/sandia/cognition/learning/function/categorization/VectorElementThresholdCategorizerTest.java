/*
 * File:                VectorElementThresholdCategorizerTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 30, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.Vector3;

/**
 * Unit tests for VectorElementThresholdCategorizerTest.
 *
 * @author krdixon
 */
public class VectorElementThresholdCategorizerTest
    extends ThresholdBinaryCategorizerTestHarness<Vectorizable>
{

    /**
     * Tests for class VectorElementThresholdCategorizerTest.
     * @param testName Name of the test.
     */
    public VectorElementThresholdCategorizerTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class VectorElementThresholdCategorizerTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        VectorElementThresholdCategorizer f = new VectorElementThresholdCategorizer();
        assertEquals( VectorElementThresholdCategorizer.DEFAULT_INDEX, f.getIndex() );
        assertEquals( VectorElementThresholdCategorizer.DEFAULT_THRESHOLD, f.getThreshold() );

        int index = RANDOM.nextInt(3);
        double threshold = RANDOM.nextGaussian();
        f = new VectorElementThresholdCategorizer( index, threshold );

        VectorElementThresholdCategorizer copy =
            new VectorElementThresholdCategorizer( f );
        assertNotNull( copy );
        assertNotSame( f, copy );
        assertEquals( f.getIndex(), copy.getIndex() );
        assertEquals( f.getThreshold(), copy.getThreshold() );
    }

    /**
     * Test of evaluate method, of class VectorElementThresholdCategorizer.
     */
    public void testEvaluateWithoutThreshold()
    {
        System.out.println("evaluate");
        for( int n = 0; n < NUM_TESTS; n++ )
        {
            Vector input = this.createRandomInput();
            VectorElementThresholdCategorizer instance = this.createInstance();
            assertEquals( input.getElement(instance.getIndex()), instance.evaluateWithoutThreshold(input) );
        }
    }

    /**
     * Test of getIndex method, of class VectorElementThresholdCategorizer.
     */
    public void testGetIndex()
    {
        System.out.println("getIndex");
        int index = RANDOM.nextInt(3);
        double threshold = RANDOM.nextGaussian();
        VectorElementThresholdCategorizer instance =
            new VectorElementThresholdCategorizer( index, threshold );
        assertEquals( index, instance.getIndex() );
    }

    /**
     * Test of setIndex method, of class VectorElementThresholdCategorizer.
     */
    public void testSetIndex()
    {
        System.out.println("setIndex");
        int index = RANDOM.nextInt(3);
        double threshold = RANDOM.nextGaussian();
        VectorElementThresholdCategorizer instance =
            new VectorElementThresholdCategorizer( index, threshold );
        index++;
        instance.setIndex(index);
        assertEquals( index, instance.getIndex() );
    }

    @Override
    public VectorElementThresholdCategorizer createInstance()
    {
        return new VectorElementThresholdCategorizer(RANDOM.nextInt(3),RANDOM.nextGaussian());
    }

    @Override
    public Vector createRandomInput()
    {
        return Vector3.createRandom(RANDOM);
    }

}
