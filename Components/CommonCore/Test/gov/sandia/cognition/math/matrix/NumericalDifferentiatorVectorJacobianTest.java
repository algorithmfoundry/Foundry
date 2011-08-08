/*
 * File:                NumericalDifferentiatorVectorJacobianTest.java
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
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 *
 * @author Kevin R. Dixon
 * @since 2.1
 */
public class NumericalDifferentiatorVectorJacobianTest 
    extends NumericalDifferentiatorTestHarness<Vector,Double,Vector>
{
        
    public static class InternalFunction
        extends AbstractCloneableSerializable
        implements DifferentiableEvaluator<Vector,Double,Vector>
    {

        public Vector differentiate( Vector input )
        {
            return input.scale( 2.0 );
        }

        public Double evaluate( Vector input )
        {
            return input.norm2Squared();
        }

    }
    
    /** 
     * Creates a new instance of NumericalDifferentiatorVectorJacobianTest 
     */
    public NumericalDifferentiatorVectorJacobianTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public void testConstructors()
    {
        System.out.println( "Constuctors" );
        NumericalDifferentiator f = new NumericalDifferentiator.VectorJacobian();
        assertNotNull( f );
        assertNull( f.getInternalFunction() );
        assertEquals( NumericalDifferentiator.DEFAULT_DELTA, f.getDelta() );
    }



    @Override
    public NumericalDifferentiator.VectorJacobian createInstance()
    {
        return new NumericalDifferentiator.VectorJacobian(
            new InternalFunction() );
    }

    @Override
    public Vector createRandomInput()
    {
        return Vector3.createRandom(random);
    }

    @Override
    public void assertDerivativeEquals( Vector target, Vector estimate )
    {
        if( !target.equals( estimate, TOLERANCE ) )
        {
            assertEquals( target, estimate );
        }
    }

    public void testStaticDifferentiate()
    {
        System.out.println( "Static differentiate" );

        NumericalDifferentiator.VectorJacobian f = this.createInstance();
        Vector input = this.createRandomInput();
        Vector sr = NumericalDifferentiator.VectorJacobian.differentiate(
            input, f.getInternalFunction() );
        assertEquals( sr, f.differentiate(input) );
    }


}
