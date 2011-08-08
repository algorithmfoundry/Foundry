/*
 * File:                ThresholdBinaryCategorizerTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Jun 29, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;


/**
 *
 * @author Kevin R. Dixon
 */
public class ThresholdBinaryCategorizerTest
    extends ThresholdBinaryCategorizerTestHarness<Double>
{
    
    /**
     * Constructor
     * @param testName name
     */
    public ThresholdBinaryCategorizerTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.function.BinaryClassificationFunction.
     */
    public void testCloneLocal()
    {
        System.out.println("clone");
        
        double threshold = RANDOM.nextGaussian();
        ThresholdBinaryCategorizer instance = new ThresholdBinaryCategorizer( threshold );
        ThresholdBinaryCategorizer clone = instance.clone();
        assertNotSame( instance, clone );
        assertEquals( instance.getThreshold(), clone.getThreshold() );
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.util.function.BinaryClassificationFunction.
     */
    public void testEvaluateAsDouble()
    {
        System.out.println("evaluateAsDouble");
        
        for( int i = 0; i < 10; i++ )
        {
            ThresholdBinaryCategorizer instance = this.createInstance();
            double threshold = instance.getThreshold();
            assertTrue( instance.evaluate( threshold ) );
            assertFalse( instance.evaluate( threshold - 1.0 ) );
            for( int j = 0; j < 1000; j++ )
            {
                double value = this.createRandomInput();
                assertEquals( value, instance.evaluateAsDouble(value) );
            }
        }
        
        
    }

    /**
     * Test of convertToVector method, of class gov.sandia.cognition.learning.util.function.BinaryClassificationFunction.
     */
    public void testConvertToVector()
    {
        System.out.println("convertToVector");
        
        double threshold = RANDOM.nextGaussian();
        ThresholdBinaryCategorizer instance = new ThresholdBinaryCategorizer( threshold );

        Vector parameters = instance.convertToVector();
        assertEquals( 1, parameters.getDimensionality() );
        assertEquals( threshold, parameters.getElement(0) );
    }

    /**
     * Test of convertFromVector method, of class gov.sandia.cognition.learning.util.function.BinaryClassificationFunction.
     */
    public void testConvertFromVector()
    {
        System.out.println("convertFromVector");
        
        double threshold = RANDOM.nextGaussian();
        ThresholdBinaryCategorizer instance = new ThresholdBinaryCategorizer( threshold );

        assertEquals( threshold, instance.getThreshold() );

        double t2 = threshold + RANDOM.nextDouble();
        Vector parameters = VectorFactory.getDefault().copyValues( t2 );
        instance.convertFromVector( parameters );
        assertEquals( t2, instance.getThreshold() );
        
        
        
    }

    @Override
    public ThresholdBinaryCategorizer createInstance()
    {
        return new ThresholdBinaryCategorizer( RANDOM.nextGaussian() );
    }

    @Override
    public Double createRandomInput()
    {
        return RANDOM.nextGaussian();
    }

    @Override
    public void testConstructors()
    {
        ThresholdBinaryCategorizer instance = new ThresholdBinaryCategorizer();
        assertEquals( ThresholdBinaryCategorizer.DEFAULT_THRESHOLD, instance.getThreshold() );

        double threshold = RANDOM.nextGaussian();
        instance = new ThresholdBinaryCategorizer( threshold );
        assertEquals( threshold, instance.getThreshold() );

        ThresholdBinaryCategorizer copy = new ThresholdBinaryCategorizer( instance );
        assertNotNull( copy );
        assertNotSame( instance, copy );
        assertEquals( instance.getThreshold(), copy.getThreshold() );
    }
    
}
