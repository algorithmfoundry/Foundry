/*
 * File:                LinearRegressionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 6, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.DefaultWeightedInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.data.WeightedInputOutputPair;
import gov.sandia.cognition.learning.function.scalar.AtanFunction;
import gov.sandia.cognition.learning.function.scalar.PolynomialFunction;
import gov.sandia.cognition.learning.function.scalar.VectorFunctionLinearDiscriminant;
import gov.sandia.cognition.learning.function.vector.VectorizableVectorConverterWithBias;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-09-02",
    changesNeeded=false,
    comments={
        "Added more rigorous check against Justin's pathological example.",
        "Looks fine now."
    }
)
public class LinearRegressionTest
    extends TestCase
{
    /** The random number generator for the tests. */
    public static Random random = new Random(1);

    /**
     * Tolerance for equality
     */
    private final double EPS = 1e-5;

    /**
     * 
     * @param testName
     */
    public LinearRegressionTest(
        String testName )
    {
        super( testName );
    }

    /**
     * 
     * @return
     */
    public static LinearRegression<Double> createInstance()
    {
        /*
        LinearCombinationScalarFunction<Double> f = new LinearCombinationScalarFunction<Double>(
        PolynomialFunction.createPolynomials( 0.0, 1.0, 2.0 ),
        VectorFactory.getDefault().createUniformRandom( 3, -1, 1 ) );
         */
        return new LinearRegression<Double>(
            PolynomialFunction.createPolynomials( 0.0, 1.0, 2.0 ) );

    }

    /**
     * 
     * @param f 
     * @return
     */
    public static Collection<InputOutputPair<Double, Double>> createDataset(
        Evaluator<Double, Double> f )
    {
        Collection<InputOutputPair<Double, Double>> retval =
            new LinkedList<InputOutputPair<Double, Double>>();
        int num = random.nextInt(100) + 10;
        for (int i = 0; i < num; i++)
        {
            double weight = random.nextDouble();
            double x = random.nextGaussian();
            double y = f.evaluate( x );
            retval.add( new DefaultWeightedInputOutputPair<Double, Double>( x, y, weight ) );
        }
        return retval;
    }

    /**
     * Tests unused constructors
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        @SuppressWarnings("unchecked")
        LinearRegression<Double> f =
            new LinearRegression<Double>( new AtanFunction() );
        assertNotNull( f );
        assertNotNull( f.getInputToVectorMap() );
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.regression.LinearRegression.
     */
    public void testClone()
    {
        System.out.println( "clone" );

        LinearRegression<Double> instance = LinearRegressionTest.createInstance();
        assertTrue( instance.getUsePseudoInverse() );
        instance.setUsePseudoInverse( false );
        assertFalse( instance.getUsePseudoInverse() );

        VectorFunctionLinearDiscriminant<Double> f = new VectorFunctionLinearDiscriminant<Double>(
            instance.getInputToVectorMap(), VectorFactory.getDefault().createUniformRandom( 3, -1, 1, random ) );
        Collection<InputOutputPair<Double, Double>> data = LinearRegressionTest.createDataset( f );
        instance.learn( data );

        LinearRegression<Double> clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotSame( instance.getInputToVectorMap(), clone.getInputToVectorMap() );
        assertNotNull( clone.getLearned() );
        assertNotSame( instance.getLearned(), clone.getLearned() );
        assertFalse( instance.getUsePseudoInverse() );
    }

    /**
     * Test of getLearned method, of class gov.sandia.cognition.learning.regression.LinearRegression.
     */
    public void testGetLearned()
    {
        System.out.println( "getLearned" );

        LinearRegression<Double> instance = LinearRegressionTest.createInstance();
        assertNull( instance.getLearned() );

    }

    /**
     * Test of setLearned method, of class gov.sandia.cognition.learning.regression.LinearRegression.
     */
    public void testSetLearned()
    {
        System.out.println( "setLearned" );

        LinearRegression<Double> instance = LinearRegressionTest.createInstance();
        VectorFunctionLinearDiscriminant<Double> f = instance.getLearned();
        assertNull( f );
        instance.setLearned( null );
        assertNull( instance.getLearned() );

        instance.setLearned( f );
        assertSame( f, instance.getLearned() );

    }

    /**
     * Test of getInputToVectorMap method, of class LinearRegression.
     */
    public void testGetInputToVectorMap()
    {
        System.out.println( "getInputToVectorMap" );
        LinearRegression<Double> instance = LinearRegressionTest.createInstance();
        Evaluator<? super Double,Vector> f = instance.getInputToVectorMap();
        assertNotNull( f );

    }

    /**
     * Test of setInputToVectorMap method, of class LinearRegression.
     */
    public void testSetInputToVectorMap()
    {
        System.out.println( "setInputToVectorMap" );
        LinearRegression<Double> instance = LinearRegressionTest.createInstance();
        Evaluator<? super Double,Vector> f = instance.getInputToVectorMap();
        assertNotNull( f );
        
        instance.setInputToVectorMap(null);
        assertNull( instance.getInputToVectorMap() );
        instance.setInputToVectorMap(f);
        assertSame( f, instance.getInputToVectorMap() );
    }

    
    /**
     * Tests a known non-pathological function
     */
    public void testKnownClosedFormWeighted()
    {
        System.out.println( "learn known non-pathological function" );
        
        LinkedList<WeightedInputOutputPair<Double,Double>> data =
            new LinkedList<WeightedInputOutputPair<Double,Double>>();
        data.add( new DefaultWeightedInputOutputPair<Double, Double>( 1.0, 0.0, 1.0 ) );
        data.add( new DefaultWeightedInputOutputPair<Double, Double>( 2.0, 1.0, 2.0 ) );
        data.add( new DefaultWeightedInputOutputPair<Double, Double>( 3.0, 4.0, 3.0 ) );
        
        LinearRegression<Double> regression = 
            new LinearRegression<Double>( PolynomialFunction.createPolynomials(0.0,1.0) );
        
        VectorFunctionLinearDiscriminant<Double> result = regression.learn(data);
        System.out.println( "Weights: " + result.getWeightVector() );

        // I computed this result by hand in octave
        assertEquals( -3.3684210526, result.getWeightVector().getElement(0), EPS );
        assertEquals(  2.4210526316, result.getWeightVector().getElement(1), EPS );
        
    }    
    
    /**
     * Test of learn method, of class gov.sandia.cognition.learning.regression.LinearRegression.
     */
    public void testLearn()
    {
        System.out.println( "learn" );

        LinearRegression<Double> instance = LinearRegressionTest.createInstance();
        VectorFunctionLinearDiscriminant<Double> f = new VectorFunctionLinearDiscriminant<Double>(
            instance.getInputToVectorMap(), VectorFactory.getDefault().createUniformRandom( 3, -1, 1, random ) );
        Collection<InputOutputPair<Double, Double>> data = LinearRegressionTest.createDataset( f );

        VectorFunctionLinearDiscriminant<Double> result = instance.learn( data );

        if (!result.convertToVector().equals( f.convertToVector().convertToVector(), EPS ))
        {
            assertEquals( f.convertToVector().convertToVector(), result.convertToVector() );
        }

    }
    
    /**
     * This tests Justin's pathological example that kills the LU solver.
     */
    public void testLearn2()
    {
     
        System.out.println( "learn2" );
        
        LinearRegression<Vectorizable> regressionLearner = 
            new LinearRegression<Vectorizable>(
                new VectorizableVectorConverterWithBias());
        
        ArrayList<InputOutputPair<Vector3, Double>> data = 
            new ArrayList<InputOutputPair<Vector3, Double>>();
        
        // Make sure we're using pseudoinverse and not LU, which can't
        // handle this test case
        assertTrue( regressionLearner.getUsePseudoInverse() );
                
        // This is a rank-one matrix, which the third dimension has the regression equation
        // y = -0.642857*x + 1.85714 (according to Octave)
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 2.0, 3.0 ), 0.0));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 2.0, 1.0 ), 1.0));        
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 2.0, 0.0 ), 2.0));
        
        VectorFunctionLinearDiscriminant<Vectorizable> result = regressionLearner.learn( data );
        System.out.println( "Result weights: " + result.getWeightVector() );
        
        // These are the results, as validated by octave's backslash "\" command
        assertEquals( -0.071429, result.evaluate( data.get(0).getInput() ), EPS );
        assertEquals(  1.214286, result.evaluate( data.get(1).getInput() ), EPS );
        assertEquals(  1.857143, result.evaluate( data.get(2).getInput() ), EPS );
        
        // Now use LU solver, which will return numerically unstable results for this example
        regressionLearner.setUsePseudoInverse( false );

        VectorFunctionLinearDiscriminant<Vectorizable> resultLU = regressionLearner.learn( data );
        System.out.println( "Result LU weights: " + resultLU.getWeightVector() );
        
        // These results aren't quite right, especially when you see that the
        // LU weights from this unit test are ~1e14
        // This is because the LU decomposition used in the solve() method
        // doesn't handle the singular matrix as well as the pseudoinverse does
        // But this is a pathological example, and I will probably continue to
        // happily use the LU solver. -- krdixon, 2008-09-03
        for( int i = 0; i  < 3; i++ )
        {
            System.out.println( i + ": " + resultLU.evaluate( data.get(i).getInput() ) );
        }
        assertEquals( -0.34375, resultLU.evaluate( data.get(0).getInput() ), EPS );
        assertEquals(  1.28125, resultLU.evaluate( data.get(1).getInput() ), EPS );
        assertEquals(  2.09375, resultLU.evaluate( data.get(2).getInput() ), EPS );
        
    }

    /**
     * 
     * @param lr1
     * @param lr2
     * @return
     */
    public LinearRegression.Statistic createStatisticInstance(
        LinearRegression<Double> lr1,
        LinearRegression<Double> lr2 )
    {
        // First create the dataset
        VectorFunctionLinearDiscriminant<Double> f1 = new VectorFunctionLinearDiscriminant<Double>(
            lr1.getInputToVectorMap(), VectorFactory.getDefault().createUniformRandom( 3, -1, 1, random ) );

        VectorFunctionLinearDiscriminant<Double> f2 = new VectorFunctionLinearDiscriminant<Double>(
            lr2.getInputToVectorMap(), VectorFactory.getDefault().createUniformRandom( 3, -1, 1, random ) );
        
        LinkedList<Double> targets = new LinkedList<Double>();
        LinkedList<Double> estimates = new LinkedList<Double>();
        for( int i = 0; i < 100; i++ )
        {
            double x = random.nextGaussian();
            targets.add( f1.evaluate(x) );
            estimates.add( f2.evaluate(x) );
        }

        return new LinearRegression.Statistic( targets, estimates, 3 );
    }

    /**
     * Test of getRootMeanSquaredError method, of class gov.sandia.cognition.learning.regression.LinearRegression.Statistic.
     */
    public void testStatisticGetRootMeanSquaredError()
    {
        System.out.println( "Statistic.getRootMeanSquaredError" );


        LinearRegression<Double> lr1 = LinearRegressionTest.createInstance();
        LinearRegression<Double> lr2 = LinearRegressionTest.createInstance();
        LinearRegression.Statistic instance = this.createStatisticInstance( lr1, lr2 );

        double v = instance.getRootMeanSquaredError();
        assertTrue( v > 0.0 );
        assertEquals( v * v * instance.getNumSamples(), instance.getChiSquare(), EPS );
    }

    /**
     * Statistic.clone
     */
    public void testStatisticClone()
    {
        System.out.println( "Statistic.clone" );

        LinearRegression<Double> lr1 = LinearRegressionTest.createInstance();
        LinearRegression<Double> lr2 = LinearRegressionTest.createInstance();
        LinearRegression.Statistic instance = this.createStatisticInstance( lr1, lr2 );
        LinearRegression.Statistic clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );

        String s1 = instance.toString();
        String s2 = clone.toString();
        System.out.println( "Instance: " + s1 );
        System.out.println( "Clone:    " + s2 );
        assertEquals( s1, s2 );


    }

    /**
     * Test of getTargetEstimateCorrelation method, of class gov.sandia.cognition.learning.regression.LinearRegression.Statistic.
     */
    public void testStatisticGetTargetEstimateCorrelation()
    {
        System.out.println( "Statistic.getTargetEstimateCorrelation" );

        LinearRegression.Statistic instance = this.createStatisticInstance(
            LinearRegressionTest.createInstance(), LinearRegressionTest.createInstance() );

        double v = instance.getTargetEstimateCorrelation();
        assertTrue( v != 0.0 );

    }

    /**
     * Test of getUnpredictedErrorFraction method, of class gov.sandia.cognition.learning.regression.LinearRegression.Statistic.
     */
    public void testStatisticGetUnpredictedErrorFraction()
    {
        System.out.println( "Statistic.getUnpredictedErrorFraction" );

        LinearRegression.Statistic instance = this.createStatisticInstance(
            LinearRegressionTest.createInstance(), LinearRegressionTest.createInstance() );

        double v = instance.getUnpredictedErrorFraction();
        assertTrue( 0.0 < v );
        assertTrue( v < 1.0 );
        double c = instance.getTargetEstimateCorrelation();
        assertEquals( 1.0 - c * c, v, EPS );
    }

    /**
     * Test of getNumSamples method, of class gov.sandia.cognition.learning.regression.LinearRegression.Statistic.
     */
    public void testStatisticGetNumSamples()
    {
        System.out.println( "Statistic.getNumSamples" );

        LinearRegression.Statistic instance = this.createStatisticInstance(
            LinearRegressionTest.createInstance(), LinearRegressionTest.createInstance() );
        assertTrue( instance.getNumSamples() > 0.0 );
    }

    /**
     * Test of getDegreesOfFreedom method, of class gov.sandia.cognition.learning.regression.LinearRegression.Statistic.
     */
    public void testStatisticGetDegreesOfFreedom()
    {
        System.out.println( "Statistic.getDegreesOfFreedom" );

        LinearRegression.Statistic instance = this.createStatisticInstance(
            LinearRegressionTest.createInstance(), LinearRegressionTest.createInstance() );

        double v = instance.getDegreesOfFreedom();
        assertTrue( v > 0.0 );
        assertEquals( (double) (instance.getNumSamples() - instance.getNumParameters()), v );
    }

    /**
     * Statistic.getMeanL1Error
     */
    public void testStatisticGetMeanL1Error()
    {
        System.out.println( "Statistic.getMeanL1Error" );

        LinearRegression.Statistic instance = this.createStatisticInstance(
            LinearRegressionTest.createInstance(), LinearRegressionTest.createInstance() );
        assertTrue( instance.getMeanL1Error() > 0.0 );

    }
    
    /**
     * Degenerate stuff
     */
    public void testStatisticsDegenerateConstructor()
    {
        System.out.println( "Statistic degenerate" );

        LinearRegression.Statistic stat;

        try
        {
            stat = new LinearRegression.Statistic(
                Arrays.asList( random.nextGaussian() ),
                Arrays.asList( random.nextGaussian(), random.nextGaussian() ),
                Arrays.asList( random.nextDouble() ),
                random.nextInt(10) + 1 );
            fail( "Collections do not match!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        stat = new LinearRegression.Statistic(
            Arrays.asList( random.nextGaussian() ),
            Arrays.asList( random.nextGaussian() ),
            Arrays.asList( 0.0 ),
            0 );
        assertEquals( 0.0, stat.getMeanL1Error() );
        assertEquals( 1.0, stat.getDegreesOfFreedom() );

    }

}
