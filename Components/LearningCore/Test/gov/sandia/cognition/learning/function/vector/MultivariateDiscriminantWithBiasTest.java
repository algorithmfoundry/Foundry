/*
 * File:                MultivariateDiscriminantWithBiasTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Oct 3, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;

/**
 * Tests for class MultivariateDiscriminantWithBiasTest.
 * @author krdixon
 */
public class MultivariateDiscriminantWithBiasTest
    extends MultivariateDiscriminantTest
{

    /**
     * Default Constructor
     */
    public MultivariateDiscriminantWithBiasTest(
        String testName )
    {
        super( testName );
    }

    @Override
    public MultivariateDiscriminantWithBias createRandom()
    {
        double A = 1.0;
        int M = random.nextInt( 10 ) + 1;
        int N = random.nextInt( 10 ) + 1;
        Matrix m = MatrixFactory.getDefault().createUniformRandom(M, N, -A, A, random);
        Vector b = VectorFactory.getDefault().createUniformRandom(M, -A, A, random);
        return new MultivariateDiscriminantWithBias( m, b );
    }



    /**
     * Tests the constructors of class MultivariateDiscriminantWithBiasTest.
     */
    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        MultivariateDiscriminantWithBias instance =
            new MultivariateDiscriminantWithBias();
        assertEquals( 1, instance.getInputDimensionality() );
        assertEquals( 1, instance.getOutputDimensionality() );
        assertEquals( 1, instance.getDiscriminant().getNumColumns() );
        assertEquals( 1, instance.getDiscriminant().getNumRows() );
        assertEquals( 1.0, instance.getDiscriminant().getElement(0,0) );
        assertEquals( 1, instance.getBias().getDimensionality() );
        assertEquals( 0.0, instance.getBias().getElement(0) );

        int M = random.nextInt(10)+1;
        int N = random.nextInt(10)+1;
        instance = new MultivariateDiscriminantWithBias( N, M );
        assertEquals( N, instance.getInputDimensionality() );
        assertEquals( M, instance.getOutputDimensionality() );
        assertEquals( N, instance.getDiscriminant().getNumColumns() );
        assertEquals( M, instance.getDiscriminant().getNumRows() );
        assertEquals( MatrixFactory.getDefault().createIdentity(M, N),
            instance.getDiscriminant() );
        assertEquals( VectorFactory.getDefault().createVector(M),
            instance.getBias() );

    }

    /**
     * Tests the clone method of class MultivariateDiscriminantWithBiasTest.
     */
    @Override
    public void testClone()
    {
        System.out.println( "Clone" );
        MultivariateDiscriminantWithBias instance = this.createRandom();
        MultivariateDiscriminantWithBias clone = instance.clone();
        assertNotSame( instance.getDiscriminant(), clone.getDiscriminant() );
        assertNotSame( instance.getBias(), clone.getBias() );

        assertEquals( instance.convertToVector(), clone.convertToVector() );

        clone.getDiscriminant().setElement(0,0, random.nextGaussian() );
        clone.getBias().setElement(0, random.nextGaussian() );

        assertFalse( instance.convertToVector().equals( clone.convertToVector() ) );
    }

    /**
     * Test of evaluate method, of class MultivariateDiscriminantWithBias.
     */
    @Override
    public void testEvaluate()
    {
        System.out.println("evaluate");
        MultivariateDiscriminantWithBias instance = this.createRandom();
        Vector input = VectorFactory.getDefault().createUniformRandom(
            instance.getInputDimensionality(), -100, 100, random );

        Vector expected = instance.getDiscriminant().times( input ).plus(
            instance.getBias() );

        assertEquals( expected, instance.evaluate(input) );
    }

    /**
     * Test of getBias method, of class MultivariateDiscriminantWithBias.
     */
    public void testGetBias()
    {
        System.out.println("getBias");
        MultivariateDiscriminantWithBias instance = this.createRandom();
        assertEquals( instance.getOutputDimensionality(),
            instance.getBias().getDimensionality() );
    }

    /**
     * Test of setBias method, of class MultivariateDiscriminantWithBias.
     */
    public void testSetBias()
    {
        System.out.println("setBias");
        MultivariateDiscriminantWithBias instance = this.createRandom();
        Vector bias = VectorFactory.getDefault().createVector(instance.getOutputDimensionality());
        instance.setBias(bias);
        assertSame( bias, instance.getBias() );

        bias = VectorFactory.getDefault().createVector( instance.getOutputDimensionality()+1 );
        try
        {
            instance.setBias(bias);
            fail( "Wrong dim" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of convertToVector method, of class MultivariateDiscriminantWithBias.
     */
    @Override
    public void testConvertToVector()
    {
        System.out.println("convertToVector");
        MultivariateDiscriminantWithBias instance = this.createRandom();
        int M = instance.getOutputDimensionality();
        int N = instance.getInputDimensionality();
        Vector p = instance.convertToVector();
        assertEquals( M*(N+1), p.getDimensionality() );

        Vector pd = p.subVector(0, M*N-1);
        Vector pb = p.subVector(M*N, M*(N+1)-1 );
        assertEquals( instance.getDiscriminant().convertToVector(), pd );
        assertEquals( instance.getBias(), pb );
    }


}
