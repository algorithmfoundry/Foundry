/*
 * File:                NumericalDifferentiatorMatrixJacobianTest.java
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
import java.util.Random;

/**
 *
 * @author Kevin R. Dixon
 * @since 2.1
 */
public class NumericalDifferentiatorMatrixJacobianTest 
    extends NumericalDifferentiatorTestHarness<Vector,Vector,Matrix>
{

    public static class InternalFunction
        extends AbstractCloneableSerializable
        implements DifferentiableEvaluator<Vector,Vector,Matrix>
    {

        private static final double scaleFactor = 10.0;
        
        public Matrix differentiate( Vector input )
        {
            int M = input.getDimensionality();
            return MatrixFactory.getDefault().createIdentity( M, M ).scale( scaleFactor );
        }

        public Vector evaluate( Vector input )
        {
            return input.scale( scaleFactor );
        }

    }

    public class InternalFunctionMM
        extends AbstractCloneableSerializable
        implements DifferentiableEvaluator<Vector,Vector,Matrix>
    {

        Matrix A;

        public InternalFunctionMM(
            int numInputs,
            int numOutputs )
        {
            this.A = MatrixFactory.getDefault().createUniformRandom(
                numOutputs, numInputs, -2.0, 2.0, random );
        }

        public Matrix differentiate( Vector input )
        {
            return this.A;
        }

        public Vector evaluate( Vector input )
        {
            return this.A.times( input );
        }

    }

    /** 
     * Creates a new instance of NumericalDifferentiatorVectorJacobianTest 
     */
    public NumericalDifferentiatorMatrixJacobianTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public void testConstructors()
    {
        System.out.println( "Constuctors" );
        NumericalDifferentiator<?,?,?> f = new NumericalDifferentiator.MatrixJacobian();
        assertNotNull( f );
        assertNull( f.getInternalFunction() );
        assertEquals( NumericalDifferentiator.DEFAULT_DELTA, f.getDelta() );
    }


    @Override
    public NumericalDifferentiator.MatrixJacobian createInstance()
    {
        return new NumericalDifferentiator.MatrixJacobian(
            new InternalFunction() );
    }

    @Override
    public Vector createRandomInput()
    {
        return Vector3.createRandom(random);
    }

    @Override
    public void assertDerivativeEquals( Matrix target, Matrix estimate )
    {
        if( !target.equals( estimate, TOLERANCE ) )
        {
            assertEquals( target, estimate );
        }
    }

    public void testStaticDifferentiate()
    {
        System.out.println( "Static differentiate" );

        NumericalDifferentiator.MatrixJacobian f = this.createInstance();
        Vector input = this.createRandomInput();
        Matrix sr = NumericalDifferentiator.MatrixJacobian.differentiate(
            input, f.getInternalFunction() );
        assertEquals( sr, f.differentiate(input) );
    }

    public void testStaticDifferentiate2()
    {
        System.out.println( "Static differentiate2" );

        InternalFunctionMM f = new InternalFunctionMM(3, 2);

        Vector input = this.createRandomInput();
        Matrix sr = NumericalDifferentiator.MatrixJacobian.differentiate(
            input, f );
        Matrix actual = f.differentiate(input);
        if( !actual.equals( sr, TOLERANCE ) )
        {
            assertEquals( actual, sr );
        }
    }

}
