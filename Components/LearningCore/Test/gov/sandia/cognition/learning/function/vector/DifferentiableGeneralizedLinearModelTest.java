/*
 * File:                DifferentiableGeneralizedLinearModelTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 28, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */
package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.learning.algorithm.gradient.GradientDescendableTestHarness;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * Unit tests for DifferentiableGeneralizedLinearModelTest
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class DifferentiableGeneralizedLinearModelTest
    extends GeneralizedLinearModelTest
{
    
    public DifferentiableGeneralizedLinearModelTest(
        String testName)
    {
        super(testName);
    }

    public void testConstructors()
    {
        System.out.println( "Constructors" );

        DifferentiableGeneralizedLinearModel f =
            new DifferentiableGeneralizedLinearModel();
        assertEquals( 1, f.getInputDimensionality() );
        assertEquals( 1, f.getOutputDimensionality() );
        assertNotNull( f.getSquashingFunction() );

    }

    /**
     * Test of getSquashingFunction method, of class gov.sandia.isrc.learning.util.function.DifferentiableGeneralizedLinearModelTest.
     */
    public void testDiffyGetSquashingFunction()
    {
        System.out.println("getSquashingFunction");
        
        DifferentiableGeneralizedLinearModel instance = this.createRandom();
        assertNotNull( instance.getSquashingFunction() );
    }

    /**
     * Test of computeParameterGradient method, of class gov.sandia.isrc.learning.util.function.DifferentiableGeneralizedLinearModelTest.
     */
    public void testComputeParameterGradient()
    {
        System.out.println("computeParameterGradient");
        double A = 1.0;
        for( int i = 0; i < 10; i++ )
        {
            DifferentiableGeneralizedLinearModel instance = this.createRandom();
            int N = instance.getDiscriminant().getDiscriminant().getNumColumns();
            Vector input = VectorFactory.getDefault().createUniformRandom( N, -A, A, random );
            GradientDescendableTestHarness.testGradient( instance, input );
        }
        

    }
    
}
