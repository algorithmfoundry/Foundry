/*
 * File:                NumericalDifferentiatorDoubleJacobianTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 1, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.math.DifferentiableEvaluator;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 *
 * @author Kevin R. Dixon
 * @since 2.1
 */
public class NumericalDifferentiatorDoubleJacobianTest 
    extends NumericalDifferentiatorTestHarness<Double,Double,Double>
{
        
    public static class InternalFunction
        extends AbstractCloneableSerializable
        implements DifferentiableEvaluator<Double,Double,Double>
    {

        private static final double scaleFactor = 10.0;
        
        public Double differentiate( Double input )
        {
            return scaleFactor;
        }

        public Double evaluate( Double input )
        {
            return input * scaleFactor;
        }

    }
    
    /** 
     * Creates a new instance of NumericalDifferentiatorVectorJacobianTest 
     */
    public NumericalDifferentiatorDoubleJacobianTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public void testConstructors()
    {
        System.out.println( "Constuctors" );
        NumericalDifferentiator<?,?,?> f = new NumericalDifferentiator.DoubleJacobian();
        assertNotNull( f );
        assertNull( f.getInternalFunction() );
        assertEquals( NumericalDifferentiator.DEFAULT_DELTA, f.getDelta() );
    }


    @Override
    public NumericalDifferentiator.DoubleJacobian createInstance()
    {
        return new NumericalDifferentiator.DoubleJacobian(
            new InternalFunction() );
    }

    @Override
    public Double createRandomInput()
    {
        return random.nextGaussian();
    }

    @Override
    public void assertDerivativeEquals( Double target, Double estimate )
    {
        assertEquals( target, estimate, TOLERANCE );
    }

    public void testStaticDifferentiate()
    {
        System.out.println( "Static differentiate" );

        NumericalDifferentiator.DoubleJacobian f = this.createInstance();
        Double input = this.createRandomInput();
        double sr = NumericalDifferentiator.DoubleJacobian.differentiate(
            input, f.getInternalFunction() );
        assertEquals( sr, f.differentiate(input) );
    }

}
