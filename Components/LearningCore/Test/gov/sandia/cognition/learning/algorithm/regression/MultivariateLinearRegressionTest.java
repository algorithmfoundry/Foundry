/*
 * File:                MultivariateLinearRegressionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 22, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.learning.data.DefaultWeightedInputOutputPair;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.util.LinkedList;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.vector.MultivariateDiscriminant;
import gov.sandia.cognition.learning.function.vector.MultivariateDiscriminantWithBias;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Collection;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for class MultivariateLinearRegressionTest.
 * @author krdixon
 */
public class MultivariateLinearRegressionTest
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public final double TOLERANCE = 1e-5;

    /**
     * Default number of samples to test against, {@value}.
     */
    public final int NUM_SAMPLES = 1000;


    /**
     * Default Constructor
     */
    public MultivariateLinearRegressionTest()
    {
    }

    /**
     * Tests the constructors of class MultivariateLinearRegressionTest.
     */
    @Test
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        MultivariateLinearRegression instance = new MultivariateLinearRegression();
        assertTrue( instance.getUsePseudoInverse() );
    }

    /**
     * Tests the clone method of class MultivariateLinearRegressionTest.
     */
    @Test
    public void testClone()
    {
        System.out.println( "Clone" );
        MultivariateLinearRegression instance = new MultivariateLinearRegression();
        instance.setUsePseudoInverse(false);
        MultivariateLinearRegression clone = instance.clone();
        assertNotSame( instance, clone );
        assertEquals( instance.getUsePseudoInverse(), clone.getUsePseudoInverse() );
    }

    /**
     * Test of learn method, of class MultivariateLinearRegression.
     */
    @Test
    public void testLearn()
    {
        System.out.println("learn");
        int M = RANDOM.nextInt( 5 ) + 1;
        int N = RANDOM.nextInt( 5 ) + 1;

        double r = 1.0;
        Matrix A = MatrixFactory.getDefault().createUniformRandom( M, N, -r, r, RANDOM );
        Vector bias = VectorFactory.getDefault().createUniformRandom( M, -r, r, RANDOM);
        MultivariateDiscriminantWithBias f = new MultivariateDiscriminantWithBias( A, bias );

        int num = RANDOM.nextInt( 100 ) + (M*N);
        Collection<InputOutputPair<Vector,Vector>> dataset =
            new LinkedList<InputOutputPair<Vector,Vector>>();
        for( int i = 0; i < num; i++ )
        {
            Vector input = VectorFactory.getDefault().createUniformRandom( N, -r, r, RANDOM );
            Vector output = f.evaluate( input );
            dataset.add( new DefaultInputOutputPair<Vector,Vector>( input, output ) );
        }

        MultivariateLinearRegression learner =
            new MultivariateLinearRegression();
        learner.setUsePseudoInverse(true);
        MultivariateDiscriminantWithBias fhat = learner.learn( dataset );
        System.out.println( "fhat: " + fhat.convertToVector() );
        System.out.println( "f:    " + f.convertToVector() );
        assertTrue( A.equals( fhat.getDiscriminant(), 1e-5 ) );

        learner.setUsePseudoInverse(false);
        fhat = learner.learn( dataset );
        Vector p1 = fhat.convertToVector();
        assertTrue( A.equals( fhat.getDiscriminant(), 1e-5 ) );
        System.out.println( "p1: " + p1.norm2() );
        
        learner.setRegularization(0.1);
        fhat = learner.learn( dataset );
        Vector p2 = fhat.convertToVector();
        System.out.println( "p2: " + p2.norm2() );
        assertTrue( p1.norm2() > p2.norm2() );

        learner.setRegularization(1.0);
        fhat = learner.learn( dataset );
        Vector p3 = fhat.convertToVector();
        System.out.println( "p3: " + p3.norm2() );
        assertTrue( p2.norm2() > p3.norm2() );
        
        
    }

    /**
     * weighted learn
     */
    @Test
    public void testWeightedLearn()
    {
        System.out.println( "weightedLearn" );

        int M = RANDOM.nextInt( 5 ) + 1;
        int N = RANDOM.nextInt( 5 ) + 1;

        double r = 1.0;
        Matrix A = MatrixFactory.getDefault().createUniformRandom( M, N, -r, r, RANDOM );
        MultivariateDiscriminant f = new MultivariateDiscriminant( A );

        int num = RANDOM.nextInt(100) + (M*N);
        Collection<InputOutputPair<Vector,Vector>> dataset =
            new LinkedList<InputOutputPair<Vector,Vector>>();
        for( int i = 0; i < num; i++ )
        {
            double weight = RANDOM.nextDouble();
            Vector input = VectorFactory.getDefault().createUniformRandom( N, -r, r, RANDOM );
            Vector output = f.evaluate( input );
            dataset.add( DefaultWeightedInputOutputPair.create( input, output, weight ) );
        }

        MultivariateLinearRegression learner = new MultivariateLinearRegression();
        MultivariateDiscriminant fhat = learner.learn( dataset );
        assertTrue( A.equals( fhat.getDiscriminant(), 1e-5 ) );
    }


}
