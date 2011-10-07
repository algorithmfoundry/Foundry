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
import gov.sandia.cognition.learning.function.scalar.LinearDiscriminant;
import gov.sandia.cognition.learning.function.scalar.LinearDiscriminantWithBias;
import gov.sandia.cognition.learning.function.scalar.PolynomialFunction;
import gov.sandia.cognition.learning.function.vector.ScalarBasisSet;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
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
    public static LinearRegression createInstance()
    {
        /*
        LinearCombinationScalarFunction<Double> f = new LinearCombinationScalarFunction<Double>(
        PolynomialFunction.createPolynomials( 0.0, 1.0, 2.0 ),
        VectorFactory.getDefault().createUniformRandom( 3, -1, 1 ) );
         */
        return new LinearRegression();
//            PolynomialFunction.createPolynomials( 0.0, 1.0, 2.0 ) );

    }

    /**
     * 
     * @param basis
     * @param f
     * @return
     */
    public static Collection<InputOutputPair<Vector, Double>> createDataset(
        Evaluator<Double, Vector> basis,
        LinearDiscriminant f )
    {
        Collection<InputOutputPair<Vector, Double>> retval =
            new LinkedList<InputOutputPair<Vector, Double>>();
        int num = random.nextInt(100) + 10;
        for (int i = 0; i < num; i++)
        {
            double x = random.nextGaussian();
            Vector phi = basis.evaluate(x);
            double y = f.evaluate( phi );
            retval.add( DefaultInputOutputPair.create(phi, y) );
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
        LinearRegression f = new LinearRegression();
        assertNotNull( f );
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.regression.LinearRegression.
     */
    public void testClone()
    {
        System.out.println( "clone" );

        LinearRegression instance = LinearRegressionTest.createInstance();
        assertTrue( instance.getUsePseudoInverse() );
        instance.setUsePseudoInverse( false );
        assertFalse( instance.getUsePseudoInverse() );

        ScalarBasisSet<Double> polynomials = new ScalarBasisSet<Double>(
            PolynomialFunction.createPolynomials( 1.0, 2.0 ) );
        LinearDiscriminant f = new LinearDiscriminant(
            VectorFactory.getDefault().createUniformRandom( 2, -1, 1, random ) );

        Collection<InputOutputPair<Vector, Double>> data = LinearRegressionTest.createDataset( polynomials, f );
        instance.learn( data );

        LinearRegression clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertFalse( instance.getUsePseudoInverse() );
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
        
        ArrayList<WeightedInputOutputPair<Vector,Double>> vectorData =
            new ArrayList<WeightedInputOutputPair<Vector, Double>>( data.size() );
        for( WeightedInputOutputPair<Double,Double> pair : data )
        {
            vectorData.add( DefaultWeightedInputOutputPair.create(
                VectorFactory.getDefault().copyValues( pair.getInput() ), pair.getOutput(), pair.getWeight() ) );
        }

        for( WeightedInputOutputPair<Vector,Double> d : vectorData )
        {
            System.out.println( "Input: " + d.getInput() + ", Output: " + d.getOutput() + ", Weight: " + d.getWeight() );
        }


        LinearRegression regression = new LinearRegression();
        regression.setUsePseudoInverse(false);
        LinearDiscriminantWithBias result = regression.learn(vectorData);
        System.out.println( "Weights: " + result.convertToVector() );

        // I computed this result by hand in octave
        assertEquals(  2.4210526316, result.getWeightVector().getElement(0), EPS );
        assertEquals( -3.3684210526, result.getBias(), EPS );
        
    }    
    
    /**
     * Test of learn method, of class gov.sandia.cognition.learning.regression.LinearRegression.
     */
    public void testLearn()
    {
        System.out.println( "learn" );

        LinearRegression instance = LinearRegressionTest.createInstance();
        ScalarBasisSet<Double> polynomials = new ScalarBasisSet<Double>(
            PolynomialFunction.createPolynomials( 1.0, 2.0 ) );
        LinearDiscriminantWithBias f = new LinearDiscriminantWithBias(
            VectorFactory.getDefault().createUniformRandom( 2, -1, 1, random ), random.nextGaussian() );

        Collection<InputOutputPair<Vector, Double>> data = LinearRegressionTest.createDataset( polynomials, f );

        LinearDiscriminantWithBias result = instance.learn( data );

        if (!result.convertToVector().equals( f.convertToVector(), EPS ))
        {
            assertEquals( f.convertToVector(), result.convertToVector() );
        }

    }
    
    /**
     * This tests Justin's pathological example that kills the LU solver.
     */
    public void testLearn2()
    {
     
        System.out.println( "learn2" );
        
        LinearRegression regressionLearner = new LinearRegression();
        regressionLearner.setUsePseudoInverse(true);
        
        ArrayList<InputOutputPair<Vector, Double>> data = 
            new ArrayList<InputOutputPair<Vector, Double>>();
        
        // Make sure we're using pseudoinverse and not LU, which can't
        // handle this test case
        assertTrue( regressionLearner.getUsePseudoInverse() );
                
        // This is a rank-one matrix, which the third dimension has the regression equation
        // y = -0.642857*x + 1.85714 (according to Octave)
        data.add(DefaultInputOutputPair.create(VectorFactory.getDefault().copyValues(1.0, 2.0, 3.0 ), 0.0));
        data.add(DefaultInputOutputPair.create(VectorFactory.getDefault().copyValues(1.0, 2.0, 1.0 ), 1.0));
        data.add(DefaultInputOutputPair.create(VectorFactory.getDefault().copyValues(1.0, 2.0, 0.0 ), 2.0));
        
        LinearDiscriminantWithBias result = regressionLearner.learn( data );
        System.out.println( "SVD weights: " + result.convertToVector() );
        
        // These are the results, as validated by octave's backslash "\" command
        assertEquals( -0.071429, result.evaluate( data.get(0).getInput() ), EPS );
        assertEquals(  1.214286, result.evaluate( data.get(1).getInput() ), EPS );
        assertEquals(  1.857143, result.evaluate( data.get(2).getInput() ), EPS );
        
        // Now use LU solver, which will return numerically unstable results for this example
        regressionLearner.setUsePseudoInverse( false );

        LinearDiscriminantWithBias resultLU = regressionLearner.learn( data );
        Vector v1 = resultLU.convertToVector();
        System.out.println( "LU  weights: " + v1 );
        
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
        assertEquals( -33.67857142857143, resultLU.evaluate( data.get(0).getInput() ), EPS );
        assertEquals( -32.392857142857146, resultLU.evaluate( data.get(1).getInput() ), EPS );
        assertEquals( -31.75, resultLU.evaluate( data.get(2).getInput() ), EPS );
        
        regressionLearner.setRegularization(1e-3);
        LinearDiscriminantWithBias resultLUr = regressionLearner.learn(data);
        Vector v2 = resultLUr.convertToVector();
        System.out.println( "LUr weights: " + v2 );
        for( int i = 0; i  < 3; i++ )
        {
            System.out.println( i + ": " + resultLUr.evaluate( data.get(i).getInput() ) );
        }
        assertEquals( -0.0711990287795472, resultLUr.evaluate( data.get(0).getInput() ), EPS );
        assertEquals(  1.2142398057559096, resultLUr.evaluate( data.get(1).getInput() ), EPS );
        assertEquals(  1.856959223023638, resultLUr.evaluate( data.get(2).getInput() ), EPS );


    }

    /**
     * 
     * @param lr1
     * @param lr2
     * @return
     */
    public LinearRegression.Statistic createStatisticInstance(
        LinearDiscriminant f1,
        LinearDiscriminant f2 )
    {
        
        LinkedList<Double> targets = new LinkedList<Double>();
        LinkedList<Double> estimates = new LinkedList<Double>();
        MultivariateGaussian g = new MultivariateGaussian( f1.getInputDimensionality() );
        ArrayList<Vector> samples = g.sample(random, 100);
        for( Vector x : samples )
        {
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


        LinearDiscriminant f1 = new LinearDiscriminant( VectorFactory.getDefault().createUniformRandom(3, -1, 1, random) );
        LinearDiscriminant f2 = new LinearDiscriminant( VectorFactory.getDefault().createUniformRandom(3, -1, 1, random) );
        LinearRegression.Statistic instance = this.createStatisticInstance( f1, f2 );

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

        LinearDiscriminant f1 = new LinearDiscriminant( VectorFactory.getDefault().createUniformRandom(3, -1, 1, random) );
        LinearDiscriminant f2 = new LinearDiscriminant( VectorFactory.getDefault().createUniformRandom(3, -1, 1, random) );
        LinearRegression.Statistic instance = this.createStatisticInstance( f1, f2 );
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

        LinearDiscriminant f1 = new LinearDiscriminant( VectorFactory.getDefault().createUniformRandom(3, -1, 1, random) );
        LinearDiscriminant f2 = new LinearDiscriminant( VectorFactory.getDefault().createUniformRandom(3, -1, 1, random) );
        LinearRegression.Statistic instance = this.createStatisticInstance( f1, f2 );

        double v = instance.getTargetEstimateCorrelation();
        assertTrue( v != 0.0 );

    }

    /**
     * Test of getUnpredictedErrorFraction method, of class gov.sandia.cognition.learning.regression.LinearRegression.Statistic.
     */
    public void testStatisticGetUnpredictedErrorFraction()
    {
        System.out.println( "Statistic.getUnpredictedErrorFraction" );

        LinearDiscriminant f1 = new LinearDiscriminant( VectorFactory.getDefault().createUniformRandom(3, -1, 1, random) );
        LinearDiscriminant f2 = new LinearDiscriminant( VectorFactory.getDefault().createUniformRandom(3, -1, 1, random) );
        LinearRegression.Statistic instance = this.createStatisticInstance( f1, f2 );

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

        LinearDiscriminant f1 = new LinearDiscriminant( VectorFactory.getDefault().createUniformRandom(3, -1, 1, random) );
        LinearDiscriminant f2 = new LinearDiscriminant( VectorFactory.getDefault().createUniformRandom(3, -1, 1, random) );
        LinearRegression.Statistic instance = this.createStatisticInstance( f1, f2 );
        assertTrue( instance.getNumSamples() > 0.0 );
    }

    /**
     * Test of getDegreesOfFreedom method, of class gov.sandia.cognition.learning.regression.LinearRegression.Statistic.
     */
    public void testStatisticGetDegreesOfFreedom()
    {
        System.out.println( "Statistic.getDegreesOfFreedom" );

        LinearDiscriminant f1 = new LinearDiscriminant( VectorFactory.getDefault().createUniformRandom(3, -1, 1, random) );
        LinearDiscriminant f2 = new LinearDiscriminant( VectorFactory.getDefault().createUniformRandom(3, -1, 1, random) );
        LinearRegression.Statistic instance = this.createStatisticInstance( f1, f2 );

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

        LinearDiscriminant f1 = new LinearDiscriminant( VectorFactory.getDefault().createUniformRandom(3, -1, 1, random) );
        LinearDiscriminant f2 = new LinearDiscriminant( VectorFactory.getDefault().createUniformRandom(3, -1, 1, random) );
        LinearRegression.Statistic instance = this.createStatisticInstance( f1, f2 );
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
