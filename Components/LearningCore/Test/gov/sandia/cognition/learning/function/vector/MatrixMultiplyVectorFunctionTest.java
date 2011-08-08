/*
 * File:                MatrixVectorMultiplyFunctionTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 5, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.learning.algorithm.gradient.GradientDescendableTestHarness;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.DefaultWeightedInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     MatrixVectorMultiplyFunction
 *
 * @author Kevin R. Dixon
 * @since 1.0
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2006-10-06",
    changesNeeded=false,
    comments="Test class looks fine."
)
public class MatrixMultiplyVectorFunctionTest 
    extends TestCase
{
    /** The random number generator for the tests. */
    public Random random = new Random(1);
    
    /**
     * Creates a new instance of MatrixVectorMultiplyFunctionTest.
     *
     * @param  testName The test name.
     */
    public MatrixMultiplyVectorFunctionTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates the test suite.
     *
     * @return The test suite.
     */
    public static Test suite()
    {
        TestSuite suite = new TestSuite(MatrixMultiplyVectorFunctionTest.class);
        
        return suite;
    }

    public MatrixMultiplyVectorFunction createRandom()
    {
        double A = 1.0;
        int M = random.nextInt( 10 ) + 1;
        int N = random.nextInt( 10 ) + 1;
        Matrix m = MatrixFactory.getDefault().createUniformRandom(M, N, -A, A, random);
        return new MatrixMultiplyVectorFunction( m );
    }


    /**
     * Test of clone method, of class MatrixVectorMultiplyFunction.
     */
    public void testClone()
    {
        System.out.println("clone");
        
        MatrixMultiplyVectorFunction instance = this.createRandom();
        MatrixMultiplyVectorFunction copy = instance.clone();
        
        assertEquals( instance.getInternalMatrix(), copy.getInternalMatrix() );
        
        copy.getInternalMatrix().setElement(5,4, 3);
        assertFalse( instance.getInternalMatrix().equals(
            copy.getInternalMatrix() ) );
    }

    /**
     * Test of getInternalMatrix method, of class MatrixVectorMultiplyFunction.
     */
    public void testGetInternalMatrix()
    {
        System.out.println("getInternalMatrix");

        Matrix internalMatrix = MatrixFactory.getDefault().createUniformRandom( 10, 15, -100, 100, random );
        
        MatrixMultiplyVectorFunction instance = 
            new MatrixMultiplyVectorFunction( internalMatrix );

        assertEquals( instance.getInternalMatrix(), internalMatrix );
    }

    /**
     * Test of setInternalMatrix method, of class MatrixVectorMultiplyFunction.
     */
    public void testSetInternalMatrix()
    {
        System.out.println("setInternalMatrix");
        
        int M = 10;
        int N = 15;
        
        Matrix internalMatrix = MatrixFactory.getDefault().createUniformRandom( M, N, -100, 100, random );
        
        MatrixMultiplyVectorFunction instance =
            new MatrixMultiplyVectorFunction( internalMatrix );

        assertEquals( instance.getInternalMatrix(), internalMatrix );

        Matrix r1 = MatrixFactory.getDefault().createMatrix( M, N );
        instance.setInternalMatrix( r1 );
        assertFalse( instance.getInternalMatrix().equals( internalMatrix ) );
        
    }

    /**
     * Test of convertToVector method, of class MatrixVectorMultiplyFunction.
     */
    public void testConvertToVector()
    {
        System.out.println("convertToVector");
        
        MatrixMultiplyVectorFunction instance = this.createRandom();
        assertEquals( instance.getInternalMatrix().convertToVector(), instance.convertToVector() );
    }

    /**
     * Test of convertFromVector method, of class MatrixVectorMultiplyFunction.
     */
    public void testConvertFromVector()
    {
        System.out.println("convertFromVector");
        MatrixMultiplyVectorFunction instance = this.createRandom();

        int N = instance.getInputDimensionality();
        int M = instance.getOutputDimensionality();
        Matrix m2 = MatrixFactory.getDefault().createUniformRandom( M, N, -100, 100, random );
        
        assertFalse( instance.convertToVector().equals( m2.convertToVector() ) );
        assertFalse( instance.getInternalMatrix().equals( m2 ) );
        Vector v2 = m2.convertToVector();
        
        instance.convertFromVector( v2 );
        assertEquals( instance.convertToVector(), v2 );
        assertEquals( instance.getInternalMatrix(), m2 );
        
    }

    /**
     * Test of evaluate method, of class MatrixVectorMultiplyFunction.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        
        int M = 10;
        int N = 15;
        
        Matrix internalMatrix = MatrixFactory.getDefault().createUniformRandom( M, N, -100, 100, random );
        Vector input = VectorFactory.getDefault().createUniformRandom( N, -100, 100, random );
        Vector expected = internalMatrix.times( input );
        
        MatrixMultiplyVectorFunction instance =
            new MatrixMultiplyVectorFunction( internalMatrix );
        
        assertEquals( expected, instance.evaluate( input ) );
    }

    /**
     * Test of differentiate method, of class MatrixVectorMultiplyFunction.
     */
    public void testDifferentiate()
    {
        System.out.println("differentiate");
        
        int M = 20;
        int N = 15;
        
        Matrix internalMatrix = MatrixFactory.getDefault().createUniformRandom( M, N, -100, 100, random );
        Vector input = VectorFactory.getDefault().createUniformRandom( N, -100, 100, random );
        MatrixMultiplyVectorFunction instance =
            new MatrixMultiplyVectorFunction( internalMatrix );

        assertEquals( internalMatrix, instance.differentiate( input ) );
        
        for( int i = 0; i < 10; i++ )
        {
            M = random.nextInt( 100 ) + 1;
            N = random.nextInt( 100 ) + 1;
            internalMatrix = MatrixFactory.getDefault().createUniformRandom( M, N, -100, 100, random );
            input = VectorFactory.getDefault().createUniformRandom( N, -100, 100, random );
            instance = new MatrixMultiplyVectorFunction( internalMatrix );
            GradientDescendableTestHarness.testDifferentiate( instance, input, random );
        }
        
        
    }


    /**
     * Test of computeParameterGradient method, of class gov.sandia.isrc.learning.util.function.MatrixVectorMultiplyFunction.
     */
    public void testComputeParameterGradient()
    {
        System.out.println("computeParameterGradient");
                
        for( int i = 0; i < 10; i++ )
        {
            int M = random.nextInt( 10 ) + 1;
            int N = random.nextInt( 10 ) + 1;
            Matrix m1 = MatrixFactory.getDefault().createUniformRandom( M, N, -100, 100, random );
            MatrixMultiplyVectorFunction i1 = new MatrixMultiplyVectorFunction( m1 );
            Vector x1 = VectorFactory.getDefault().createUniformRandom( N, -100, 100, random );
            GradientDescendableTestHarness.testGradient( i1, x1 );
        }
    }
    
    public void testClosedFormSolver()
    {
        System.out.println( "ClosedFormSolver" );
        
        int M = random.nextInt( 5 ) + 1;
        int N = random.nextInt( 5 ) + 1;
        
        double r = 1.0;
        Matrix A = MatrixFactory.getDefault().createUniformRandom( M, N, -r, r, random );
        MatrixMultiplyVectorFunction f = new MatrixMultiplyVectorFunction( A );
        
        int num = random.nextInt( 100 ) + (M*N);
        Collection<InputOutputPair<Vector,Vector>> dataset =
            new LinkedList<InputOutputPair<Vector,Vector>>();
        for( int i = 0; i < num; i++ )
        {
            Vector input = VectorFactory.getDefault().createUniformRandom( N, -r, r, random );
            Vector output = f.evaluate( input );
            dataset.add( new DefaultInputOutputPair<Vector,Vector>( input, output ) );
        }
        
        MatrixMultiplyVectorFunction.ClosedFormSolver learner =
            new MatrixMultiplyVectorFunction.ClosedFormSolver();
        MatrixMultiplyVectorFunction fhat = learner.learn( dataset );

        assertTrue( A.equals( fhat.getInternalMatrix(), 1e-5 ) );
        
    }
    
    
    
    public void testWeightedClosedFormSolver()
    {
        System.out.println( "WeightedClosedFormSolver" );
        
        int M = random.nextInt( 5 ) + 1;
        int N = random.nextInt( 5 ) + 1;
        
        double r = 1.0;
        Matrix A = MatrixFactory.getDefault().createUniformRandom( M, N, -r, r, random );
        MatrixMultiplyVectorFunction f = new MatrixMultiplyVectorFunction( A );
        
        int num = random.nextInt(100) + (M*N);
        Collection<InputOutputPair<Vector,Vector>> dataset =
            new LinkedList<InputOutputPair<Vector,Vector>>();
        for( int i = 0; i < num; i++ )
        {
            double weight = random.nextDouble();
            Vector input = VectorFactory.getDefault().createUniformRandom( N, -r, r, random );
            Vector output = f.evaluate( input );
            dataset.add( new DefaultWeightedInputOutputPair<Vector,Vector>( input, output, weight ) );
        }
        
        MatrixMultiplyVectorFunction.ClosedFormSolver learner =
            new MatrixMultiplyVectorFunction.ClosedFormSolver();
        MatrixMultiplyVectorFunction fhat = learner.learn( dataset );

        assertTrue( A.equals( fhat.getInternalMatrix(), 1e-5 ) );
        
    }

    public void testInputDimensionality()
    {
        System.out.println( "getInputDimensionality" );
        MatrixMultiplyVectorFunction f = this.createRandom();
        assertEquals( f.getInternalMatrix().getNumRows(), f.getOutputDimensionality() );
        assertEquals( f.getInternalMatrix().getNumColumns(), f.getInputDimensionality() );

        final double r = 10.0;
        Vector x1 = VectorFactory.getDefault().createUniformRandom(
            f.getInputDimensionality(), -r, r, random);
        Vector y1 = f.evaluate(x1);
        assertEquals( x1.getDimensionality(), f.getInputDimensionality() );
        assertEquals( y1.getDimensionality(), f.getOutputDimensionality() );

    }


}
