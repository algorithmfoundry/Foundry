/*
 * File:                FisherLinearDiscriminantClassifierTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 9, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.function.scalar.LinearDiscriminant;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.MultivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;

/**
 *
 * @author Kevin R. Dixon
 */
public class FisherLinearDiscriminantBinaryCategorizerTest
    extends ThresholdBinaryCategorizerTestHarness<Vector>
{
    
    /**
     * Constructor
     * @param testName Name
     */
    public FisherLinearDiscriminantBinaryCategorizerTest(
        String testName)
    {
        super(testName);
    }

    public FisherLinearDiscriminantBinaryCategorizer createInstance()
    {
        Vector weights = this.createRandomInput();
        double threshold = RANDOM.nextDouble();
        
        return new FisherLinearDiscriminantBinaryCategorizer( weights, threshold );
        
    }

    public void testConstructors()
    {
        System.out.println( "constructors" );

        FisherLinearDiscriminantBinaryCategorizer instance =
            new FisherLinearDiscriminantBinaryCategorizer();
        assertNotNull( instance );
        assertEquals( FisherLinearDiscriminantBinaryCategorizer.DEFAULT_THRESHOLD, instance.getThreshold() );
        assertNotNull( instance.getEvaluator() );
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.function.FisherLinearDiscriminantClassifier.
     */
    public void testCloneLocal()
    {
        System.out.println("clone local");

        FisherLinearDiscriminantBinaryCategorizer instance = this.createInstance();
        FisherLinearDiscriminantBinaryCategorizer clone = instance.clone();
        
        assertNotSame( instance, clone );
        assertNotSame( instance.getEvaluator(), clone.getEvaluator() );
        assertNotSame( ((LinearDiscriminant) instance.getEvaluator()).getWeightVector(),
            ((LinearDiscriminant) clone.getEvaluator()).getWeightVector() );
        assertEquals( ((LinearDiscriminant) instance.getEvaluator()).getWeightVector(),
            ((LinearDiscriminant) clone.getEvaluator()).getWeightVector() );
        
        assertEquals( instance.getThreshold(), clone.getThreshold() );
    }

    /**
     * Test of getEvaluator method, of class gov.sandia.cognition.learning.util.function.FisherLinearDiscriminantClassifier.
     */
    public void testGetEvaluator()
    {
        System.out.println("getEvaluator");
        
        FisherLinearDiscriminantBinaryCategorizer instance = this.createInstance();
        assertNotNull( instance.getEvaluator() );
        
        LinearDiscriminant d = (LinearDiscriminant) instance.getEvaluator();
        instance.setEvaluator( null );
        assertNull( instance.getEvaluator() );
        
        instance.setEvaluator( d );
        assertSame( d, instance.getEvaluator() );
        
    }
    
    /**
     * Learn
     */
    public void testLearn()
    {
        System.out.println( "ClosedFormSolver.learn" );
        
        FisherLinearDiscriminantBinaryCategorizer.ClosedFormSolver learner =
            new FisherLinearDiscriminantBinaryCategorizer.ClosedFormSolver( 0.0 );

        int num = 1000;
        double r = 1e-0;
        Vector m0 = this.createRandomInput();
        int M = m0.getDimensionality();
        Matrix A0 = MatrixFactory.getDefault().createUniformRandom( M, M, -r, r, RANDOM );
        ArrayList<Vector> d0 = MultivariateGaussian.sample( m0, A0, RANDOM, num );
        
        Vector m1 = this.createRandomInput();
        Matrix A1 = MatrixFactory.getDefault().createUniformRandom( M, M, -r, r, RANDOM );
        ArrayList<Vector> d1 = MultivariateGaussian.sample( m1, A1, RANDOM, num );
     
        ArrayList<InputOutputPair<Vector,Boolean>> data = 
            new ArrayList<InputOutputPair<Vector,Boolean>>( d0.size() + d1.size() );
        for( int i = 0; i < d1.size(); i++ )
        {
            data.add( new DefaultInputOutputPair<Vector,Boolean>( d1.get(i), true ) );
        }
        for( int i = 0; i < d0.size(); i++ )
        {
            data.add( new DefaultInputOutputPair<Vector,Boolean>( d0.get(i), false ) );
        }
        
        FisherLinearDiscriminantBinaryCategorizer f = learner.learn( data );
        assertEquals( M, ((LinearDiscriminant) f.getEvaluator()).getWeightVector().getDimensionality() );

        Pair<Vector,Matrix> r0 =
            MultivariateStatisticsUtil.computeMeanAndCovariance(d0);
        Vector m0hat = r0.getFirst();
        Matrix c0hat = r0.getSecond();

        Pair<Vector,Matrix> r1 =
            MultivariateStatisticsUtil.computeMeanAndCovariance(d1);
        Vector m1hat = r1.getFirst();
        Matrix c1hat = r1.getSecond();
        
        Vector what = c1hat.plus( c0hat ).inverse().times( m1hat.minus( m0hat ) );
        
        if( what.equals( ((LinearDiscriminant) f.getEvaluator()).getWeightVector(), TOLERANCE ) == false )
        {
            assertEquals( what, ((LinearDiscriminant) f.getEvaluator()).getWeightVector() );
        }
        
    }

    @Override
    public void testEvaluateWithoutThreshold()
    {
        System.out.println( "evaluateWithoutThreshold" );

        Vector input = this.createRandomInput();
        FisherLinearDiscriminantBinaryCategorizer f = this.createInstance();
        assertEquals( input.dotProduct(((LinearDiscriminant) f.getEvaluator()).getWeightVector() ), f.evaluateWithoutThreshold(input), TOLERANCE );
    }

    @Override
    public Vector createRandomInput()
    {
        return Vector3.createRandom(RANDOM);
    }
    
}
