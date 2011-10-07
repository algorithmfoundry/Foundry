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
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Random;
import junit.framework.TestCase;

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
public class MultivariateDiscriminantTest
    extends TestCase
{
    /** The random number generator for the tests. */
    public Random random = new Random(1);
    
    /**
     * Creates a new instance of MatrixVectorMultiplyFunctionTest.
     *
     * @param  testName The test name.
     */
    public MultivariateDiscriminantTest(
        String testName)
    {
        super(testName);
    }

    public MultivariateDiscriminant createRandom()
    {
        double A = 1.0;
        int M = random.nextInt( 10 ) + 1;
        int N = random.nextInt( 10 ) + 1;
        Matrix m = MatrixFactory.getDefault().createUniformRandom(M, N, -A, A, random);
        return new MultivariateDiscriminant( m );
    }

    /**
     * Tests the constructors of class MultivariateDiscriminantWithBiasTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        MultivariateDiscriminant instance =
            new MultivariateDiscriminant();
        assertEquals( 1, instance.getInputDimensionality() );
        assertEquals( 1, instance.getOutputDimensionality() );
        assertEquals( 1, instance.getDiscriminant().getNumColumns() );
        assertEquals( 1, instance.getDiscriminant().getNumRows() );
        assertEquals( 1.0, instance.getDiscriminant().getElement(0,0) );

        int M = random.nextInt(10)+1;
        int N = random.nextInt(10)+1;
        instance = new MultivariateDiscriminant( N, M );
        assertEquals( N, instance.getInputDimensionality() );
        assertEquals( M, instance.getOutputDimensionality() );
        assertEquals( N, instance.getDiscriminant().getNumColumns() );
        assertEquals( M, instance.getDiscriminant().getNumRows() );
        assertEquals( MatrixFactory.getDefault().createIdentity(M, N),
            instance.getDiscriminant() );



    }

    /**
     * Test of clone method, of class MatrixVectorMultiplyFunction.
     */
    public void testClone()
    {
        System.out.println("clone");
        
        MultivariateDiscriminant instance = this.createRandom();
        MultivariateDiscriminant clone = instance.clone();
        assertNotSame( instance.getDiscriminant(), clone.getDiscriminant() );
        assertEquals( instance.convertToVector(), clone.convertToVector() );
        clone.getDiscriminant().setElement(0,0, random.nextGaussian() );
        assertFalse( instance.convertToVector().equals( clone.convertToVector() ) );
    }

    /**
     * Test of getDiscriminant method, of class MatrixVectorMultiplyFunction.
     */
    public void testGetInternalMatrix()
    {
        System.out.println("getInternalMatrix");

        Matrix internalMatrix = MatrixFactory.getDefault().createUniformRandom( 10, 15, -100, 100, random );
        
        MultivariateDiscriminant instance =
            new MultivariateDiscriminant( internalMatrix );

        assertEquals( instance.getDiscriminant(), internalMatrix );
    }

    /**
     * Test of setDiscriminant method, of class MatrixVectorMultiplyFunction.
     */
    public void testSetInternalMatrix()
    {
        System.out.println("setInternalMatrix");
        
        int M = 10;
        int N = 15;
        
        Matrix internalMatrix = MatrixFactory.getDefault().createUniformRandom( M, N, -100, 100, random );
        
        MultivariateDiscriminant instance =
            new MultivariateDiscriminant( internalMatrix );

        assertEquals( instance.getDiscriminant(), internalMatrix );

        Matrix r1 = MatrixFactory.getDefault().createMatrix( M, N );
        instance.setDiscriminant( r1 );
        assertFalse( instance.getDiscriminant().equals( internalMatrix ) );
        
    }

    /**
     * Test of convertToVector method, of class MatrixVectorMultiplyFunction.
     */
    public void testConvertToVector()
    {
        System.out.println("convertToVector");
        
        MultivariateDiscriminant instance = this.createRandom();
        int M = instance.getOutputDimensionality();
        int N = instance.getInputDimensionality();
        Vector p = instance.convertToVector();
        assertEquals( M*N, p.getDimensionality() );
        assertEquals( instance.getDiscriminant().convertToVector(), p );
    }

    /**
     * Test of convertFromVector method, of class MatrixVectorMultiplyFunction.
     */
    public void testConvertFromVector()
    {
        System.out.println("convertFromVector");
        MultivariateDiscriminant instance = this.createRandom();

        Vector p = instance.convertToVector();
        instance.convertFromVector(p);

        p.scaleEquals( random.nextGaussian() );
        instance.convertFromVector(p);
        assertEquals( p, instance.convertToVector() );
        
    }

    /**
     * Test of evaluate method, of class MatrixVectorMultiplyFunction.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        
        int M = 10;
        int N = 15;

        MultivariateDiscriminant instance = this.createRandom();
        Matrix internalMatrix = instance.getDiscriminant();
        Vector input = VectorFactory.getDefault().createUniformRandom(
            instance.getInputDimensionality(), -100, 100, random );
        Vector expected = internalMatrix.times( input );        
        
        assertEquals( expected, instance.evaluate( input ) );
    }

    /**
     * Test of differentiate method, of class MatrixVectorMultiplyFunction.
     */
    public void testDifferentiate()
    {
        System.out.println("differentiate");
        
        MultivariateDiscriminant instance = this.createRandom();
        Matrix internalMatrix = instance.getDiscriminant();
        Vector input = VectorFactory.getDefault().createUniformRandom(
            instance.getInputDimensionality(), -100, 100, random );

        assertEquals( internalMatrix, instance.differentiate( input ) );
        
        for( int i = 0; i < 10; i++ )
        {
            instance = this.createRandom();
            input = VectorFactory.getDefault().createUniformRandom(
                instance.getInputDimensionality(), -100, 100, random );
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
            MultivariateDiscriminant i1 = this.createRandom();
            Vector x1 = VectorFactory.getDefault().createUniformRandom(
                i1.getInputDimensionality(), -100, 100, random );
            GradientDescendableTestHarness.testGradient( i1, x1 );
        }
    }
    
    public void testInputDimensionality()
    {
        System.out.println( "getInputDimensionality" );
        MultivariateDiscriminant f = this.createRandom();
        assertEquals( f.getDiscriminant().getNumRows(), f.getOutputDimensionality() );
        assertEquals( f.getDiscriminant().getNumColumns(), f.getInputDimensionality() );

        final double r = 10.0;
        Vector x1 = VectorFactory.getDefault().createUniformRandom(
            f.getInputDimensionality(), -r, r, random);
        Vector y1 = f.evaluate(x1);
        assertEquals( x1.getDimensionality(), f.getInputDimensionality() );
        assertEquals( y1.getDimensionality(), f.getOutputDimensionality() );

    }


}
