/*
 * File:                DecoupledVectorLinearRegressionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 11, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.vector.DecoupledVectorFunction;
import gov.sandia.cognition.learning.function.scalar.PolynomialFunction;
import gov.sandia.cognition.learning.function.scalar.VectorFunctionLinearDiscriminant;
import gov.sandia.cognition.learning.function.vector.ScalarBasisSet;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class DecoupledVectorLinearRegressionTest extends TestCase
{
    /** The random number generator for the tests. */
    public final Random random = new Random(1);

    /**
     * Tolerance for equality
     */
    public final double TOLERANCE = 1e-5;
    
    public DecoupledVectorLinearRegressionTest( String testName )
    {
        super( testName );
    }

    public static DecoupledVectorLinearRegression createInstance()
    {
        return new DecoupledVectorLinearRegression( 3,
            PolynomialFunction.createPolynomials( 0.0, 1.0, 2.0 ) );
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.regression.DecoupledVectorLinearRegression.
     */
    public void testClone()
    {
        System.out.println( "clone" );

        DecoupledVectorLinearRegression instance = DecoupledVectorLinearRegressionTest.createInstance();
        DecoupledVectorLinearRegression clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertEquals( instance.getDimensionality(), clone.getDimensionality() );
        assertNotSame( instance.getElementFunctions(), clone.getElementFunctions() );
    }

    /**
     * Test of getElementFunctions method, of class gov.sandia.cognition.learning.regression.DecoupledVectorLinearRegression.
     */
    public void testGetElementFunctions()
    {
        System.out.println( "getElementFunctions" );

        DecoupledVectorLinearRegression instance = DecoupledVectorLinearRegressionTest.createInstance();
        assertNotNull( instance.getElementFunctions() );

    }

    /**
     * Test of setElementFunctions method, of class gov.sandia.cognition.learning.regression.DecoupledVectorLinearRegression.
     */
    public void testSetElementFunctions()
    {
        System.out.println( "setElementFunctions" );

        DecoupledVectorLinearRegression instance = DecoupledVectorLinearRegressionTest.createInstance();
        assertNotNull( instance.getElementFunctions() );
        Collection<ScalarBasisSet<Double>> f2 =
            new ArrayList<ScalarBasisSet<Double>>( instance.getElementFunctions() );
        assertNotSame( f2, instance.getElementFunctions() );

        instance.setElementFunctions( f2 );
        assertSame( f2, instance.getElementFunctions() );

        try
        {
            instance.setElementFunctions( null );
            fail( "Functions can't be null" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            instance.setElementFunctions( new LinkedList<ScalarBasisSet<Double>>() );
            fail( "Must be at least one function!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getDimensionality method, of class gov.sandia.cognition.learning.regression.DecoupledVectorLinearRegression.
     */
    public void testGetDimensionality()
    {
        System.out.println( "getDimensionality" );

        DecoupledVectorLinearRegression instance = DecoupledVectorLinearRegressionTest.createInstance();
        assertEquals( instance.getElementFunctions().size(), instance.getDimensionality() );

    }

    /**
     * Test of learn method, of class gov.sandia.cognition.learning.regression.DecoupledVectorLinearRegression.
     */
    public void testLearn()
    {
        System.out.println( "learn" );

        int dim = random.nextInt(3) + 2;
        ArrayList<VectorFunctionLinearDiscriminant<Double>> f =
            new ArrayList<VectorFunctionLinearDiscriminant<Double>>( dim );
        ArrayList<ScalarBasisSet<Double>> f2 =
            new ArrayList<ScalarBasisSet<Double>>( dim );
        for (int i = 0; i < dim; i++)
        {
            ScalarBasisSet<Double> s = new ScalarBasisSet<Double>(
                PolynomialFunction.createPolynomials( i, i + 1, i + 2 ) );
            f2.add( s );
             f.add( new VectorFunctionLinearDiscriminant<Double>(
                 s, Vector3.createRandom(random) ) );
        }

        DecoupledVectorFunction target = new DecoupledVectorFunction( f );

        LinkedList<InputOutputPair<Vector, Vector>> dataset =
            new LinkedList<InputOutputPair<Vector, Vector>>();
        for (int i = 0; i < 100; i++)
        {
            Vector x = VectorFactory.getDefault().createUniformRandom( dim, 0, 1, random );
            Vector y = target.evaluate( x );
            dataset.add( new DefaultInputOutputPair<Vector, Vector>( x, y ) );
        }

        DecoupledVectorLinearRegression instance =
            new DecoupledVectorLinearRegression( f2 );

        assertEquals( 3, instance.getNumParameters() );
        DecoupledVectorFunction estimate = instance.learn( dataset );
        int i = 0;
        for( Evaluator<? super Double,Double> fhat : estimate.getScalarFunctions() )
        {
            @SuppressWarnings("unchecked")
            VectorFunctionLinearDiscriminant<Double> vf =
                (VectorFunctionLinearDiscriminant<Double>) fhat;
            VectorFunctionLinearDiscriminant<Double> v = f.get(i);
            
            assertTrue( vf.getWeightVector().equals( v.getWeightVector(), TOLERANCE ) );
            i++;
        }

        DecoupledVectorLinearRegression.Statistic stat =
            instance.computeStatistics(dataset);
        assertEquals( dim, stat.getElementStatistics().size() );
        assertNotNull( stat.getJointErrorStatistics() );

        DecoupledVectorLinearRegression.Statistic clone = stat.clone();
        assertNotNull( clone );
        assertNotSame( stat, clone );
        assertNotNull( clone.getElementStatistics() );
        assertNotSame( stat.getElementStatistics(), clone.getElementStatistics() );
        assertNotNull( clone.getJointErrorStatistics() );
        assertNotSame( stat.getJointErrorStatistics(), clone.getJointErrorStatistics() );

    }

}
