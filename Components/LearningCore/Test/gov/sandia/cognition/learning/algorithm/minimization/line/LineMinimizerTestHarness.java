/*
 * File:                LineMinimizerTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 2, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.minimization.line;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.AbstractDifferentiableUnivariateScalarFunction;
import gov.sandia.cognition.math.DifferentiableEvaluator;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.method.StudentTConfidence;
import gov.sandia.cognition.util.CloneableSerializable;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * JUnit tests for class LineMinimizerTestHarness
 * @author Kevin R. Dixon
 */
public abstract class LineMinimizerTestHarness<EvaluatorType extends Evaluator<Double,Double>>
    extends TestCase
{

    /**
     * Generates a (know) sequence of random numbers
     */
    public Random random = new Random( 1 );

    /**
     * Function prototpe to use for testing
     */
    public abstract class TestScalarFunction
        extends AbstractDifferentiableUnivariateScalarFunction
    {

        /**
         * Number of function evaluations
         */
        int NUM_FS = 0;
        
        /**
         * Number of gradient evaluations
         */
        int NUM_GRADS = 0;
        
        /**
         * Gets the minimum function value: f(xmin)
         * @return
         * minimum functino value: f(xmin)
         */
        public abstract double getFunctionMin();

    }

    /**
     * Cosine function: x = (2*n+1)*pi is a minimizer, f(minx) == -1.0
     */
    class FunctionCosine
        extends TestScalarFunction
    {

        @Override
        public FunctionCosine clone()
        {
            return new FunctionCosine();
        }

        public double evaluate( double input )
        {
            NUM_FS++;
            return Math.cos( input );
        }

        public double differentiate( double input )
        {
            NUM_GRADS++;
            return -Math.sin( input );
        }

        @Override
        public double getFunctionMin()
        {
            return -1.0;
        }

    }

    /**
     * Flat function: any "x" is a minimizer as f(x) = 1.0
     */
    class FunctionFlat
        extends TestScalarFunction
    {

        @Override
        public FunctionFlat clone()
        {
            return new FunctionFlat();
        }

        public double evaluate(
            double input )
        {
            NUM_FS++;
            return 1.0;
        }

        public double differentiate(
            double input )
        {
            NUM_GRADS++;
            return 0.0;
        }

        @Override
        public double getFunctionMin()
        {
            return 1.0;
        }

    }

    /**
     * Polynomial function: |(x-9)(x-1)|
     * Has minima at xmin={1,9}, where f(xmin) = 0.0
     */
    class FunctionAbsPoly
        extends TestScalarFunction
    {

        @Override
        public FunctionAbsPoly clone()
        {
            return new FunctionAbsPoly();
        }

        public double evaluate( double input )
        {
            NUM_FS++;
            double x = input;
            return Math.abs( (x - 9) * (x - 1) );
        }

        public double differentiate(
            double input )
        {
            double x = input;

            NUM_GRADS++;
            double sign;
            if ((x - 9) * (x - 1) > 0.0)
            {
                sign = 1.0;
            }
            else
            {
                sign = -1.0;
            }

            return sign * ((x - 9) + (x - 1));
        }

        @Override
        public double getFunctionMin()
        {
            return 0.0;
        }
        
        

    }

    /**
     * Quadratic function, minimum at vec(0.0)
     */
    class VectorFunctionQuadratic
        implements Evaluator<Vector, Double>,
        DifferentiableEvaluator<Vector, Double, Vector>
    {
        /**
         * Number of function evaluations
         */
        int NUM_EVALS = 0;

        /**
         * Number of gradient evaluations
         */
        int NUM_GRADS = 0;
        
        public Double evaluate(
            Vector input )
        {
            NUM_EVALS++;
            return input.norm2();
        }

        public Vector differentiate(
            Vector input )
        {
            NUM_GRADS++;
            return input;
        }

        @Override
        public CloneableSerializable clone()
        {
            return new VectorFunctionQuadratic();
        }

    }   
    
    /**
     * Entry point for JUnit tests for class LineMinimizerTestHarness
     * @param testName name of this test
     */
    public LineMinimizerTestHarness(
        String testName )
    {
        super( testName );
    }
    
    /**
     * Creates a new LineMinimizer
     * @return
     * LineMinimizer to test
     */
    public abstract AbstractAnytimeLineMinimizer<EvaluatorType> createInstance();
    
    /**
     * Test of bracketMinimum method, of class gov.sandia.isrc.learning.vector.LineBracket.
     */
    @SuppressWarnings("unchecked")
    public void testBracketMinimum()
    {
        System.out.println("bracketMinimum");
        
        AbstractAnytimeLineMinimizer<EvaluatorType> mini = this.createInstance();
        
        FunctionAbsPoly f = new FunctionAbsPoly();
            
        int maxIter = 100;
        int num = 100;
        double r = 20.0;
        ArrayList<Double> iters = new ArrayList<Double>( num );
        ArrayList<Double> grads = new ArrayList<Double>( num );
        ArrayList<Double> evals = new ArrayList<Double>( num );
        
        final double EPS = 1e-5;
        
        final double xmin1 = 1.0;
        final double xmin2 = 9.0;
        
        // Make sure our xmins really are mins:
        assertEquals( f.getFunctionMin(), f.evaluate( xmin1 ), EPS );
        assertEquals( f.getFunctionMin(), f.evaluate( xmin2 ), EPS );
            
        mini.setData( (EvaluatorType) f );
        
        for( int n = 0; n < num; n++ )
        {
            double x = (random.nextDouble() * 2.0 * r) - r;
            mini.setInitialGuess( x );
            
            f.NUM_FS = 0;
            f.NUM_GRADS = 0;
            
            boolean keepGoing = true;
            mini.initializeAlgorithm();
            int iteration;
            for( iteration = 0; keepGoing; iteration++ )
            {
                boolean validBracket = mini.bracketingStep();
                if( validBracket )
                {
                    keepGoing = false;
                }
                if( iteration > maxIter )
                {
                    keepGoing = false;
                }

            }
            
            iters.add( (double) iteration );
            evals.add( (double) f.NUM_FS );
            grads.add( (double) f.NUM_GRADS );

            InputOutputSlopeTriplet a = mini.getBracket().getLowerBound();
            InputOutputSlopeTriplet c = mini.getBracket().getUpperBound();
//            assertTrue( a.getInput() < c.getInput() );
            
            // Make sure the bracket includes either +1.0 or +9.0
//            assertTrue( (a.getInput() < xmin1) && (xmin1 < c.getInput()) ||
//                (a.getInput() < xmin2) && (xmin2 < c.getInput()) );
        }
        
        final double confidence = 0.95;
        StudentTConfidence ttest = new StudentTConfidence();
        
        System.out.println( "Iters: " + ttest.computeConfidenceInterval( iters, confidence ) );
        System.out.println( "Evals: " + ttest.computeConfidenceInterval( evals, confidence ) );
        System.out.println( "Grads: " + ttest.computeConfidenceInterval( grads, confidence ) );
        
    }   
    
    
    public static double REQUIRED_REDUCTION = 0.75;
//    public static double TARGET_TOLERANCE = 2.0;
    public void functionTester(
        TestScalarFunction f )
    {
        LineMinimizer<EvaluatorType> mini = this.createInstance();
        double range = 5.0;
        int num = 1000;
        final double EPS = 1e-5;
        ArrayList<Double> times = new ArrayList<Double>( num );
        ArrayList<Double> iters = new ArrayList<Double>( num );
        ArrayList<Double> grads = new ArrayList<Double>( num );
        ArrayList<Double> evals = new ArrayList<Double>( num );
        ArrayList<Double> fs = new ArrayList<Double>( num );
        for (int n = 0; n < num; n++)
        {
            f.NUM_FS = 0;
            f.NUM_GRADS = 0;
            long start = System.currentTimeMillis();

            double x = this.random.nextDouble() * 2.0 * range - range;
            mini.setInitialGuess( x );
            @SuppressWarnings("unchecked")
            InputOutputPair<Double,Double> result = mini.learn( (EvaluatorType) f );
            
            long stop = System.currentTimeMillis();
            iters.add( (double) mini.getIteration() );
            grads.add( (double) f.NUM_GRADS );
            evals.add( (double) f.NUM_FS );
            times.add( (stop - start) / 1000.0 );
            if( result != null )
            {
                // Make sure the returned function value actually is what it's
                // supposed to be
                double actualF = f.evaluate( result.getInput() );
                assertEquals( actualF, result.getOutput(), EPS );
                fs.add( actualF );
                double dnow = result.getOutput() - f.getFunctionMin();
                double forig = f.evaluate( x );
                double dorg = forig - f.getFunctionMin();
//            assertEquals( 0.0, dnow / dorg, REQUIRED_REDUCTION );
//            assertEquals( fmin, result.getOutput(), TARGET_TOLERANCE ); 
            }

        }

        final double confidence = 0.95;
        StudentTConfidence ttest = new StudentTConfidence();
        
        System.out.println( "Iters: " + ttest.computeConfidenceInterval( iters, confidence ) );
        System.out.println( "Evals: " + ttest.computeConfidenceInterval( evals, confidence ) );
        System.out.println( "Grads: " + ttest.computeConfidenceInterval( grads, confidence ) );
        System.out.println( "Times: " + ttest.computeConfidenceInterval( times, confidence ) );
        System.out.println( "Fs:    " + ttest.computeConfidenceInterval( fs, confidence ) );
    }

    public void testFunctionCosine()
    {
        System.out.println( "Minimize cosine" );
        this.functionTester( new FunctionCosine() );
    }

    public void testFunctionFlat()
    {
        System.out.println( "Flat function" );
        this.functionTester( new FunctionFlat() );
    }

    public void testFunctionAbsPoly()
    {
        System.out.println( "Minimize Polynomial" );
        this.functionTester( new FunctionAbsPoly() );
    }

    /**
     * Test of minimizeAlongDirection method, of class LineMinimizer.
     */
    public void testMinimizeAlongDirection()
    {

        System.out.println( "minimizeAlongDirection" );
        int dim = 3;
        double r = 1.0;
        int num = 1000;
        VectorFunctionQuadratic f = new VectorFunctionQuadratic();
        LineMinimizer<?> instance = this.createInstance();
        ArrayList<Double> iters = new ArrayList<Double>( num );
        ArrayList<Double> grads = new ArrayList<Double>( num );
        ArrayList<Double> evals = new ArrayList<Double>( num );
        ArrayList<Double> fs = new ArrayList<Double>( num );
        
        for (int n = 0; n < num; n++)
        {
            f.NUM_EVALS = 0;
            f.NUM_GRADS = 0;
            Vector vectorOffset = VectorFactory.getDefault().createUniformRandom( dim, -r, r, random );
            Vector direction = VectorFactory.getDefault().createUniformRandom( dim, -r, r, random );
            DirectionalVectorToDifferentiableScalarFunction function =
                new DirectionalVectorToDifferentiableScalarFunction( f, vectorOffset, direction );

            double functionValue = function.evaluate(0.0);
            InputOutputPair<Vector, Double> result = instance.minimizeAlongDirection( 
                function, functionValue, f.differentiate( vectorOffset ) );
//        assertEquals( 0.0, result.getOutput() );
//            System.out.println( "original = " + function.evaluate( 0.0 ) + ", result = " + result.getOutput() );
            evals.add( (double) f.NUM_EVALS );
            grads.add( (double) f.NUM_GRADS );
            iters.add( (double) instance.getIteration() );
            fs.add( result.getOutput() );
        }
        final double confidence = 0.95;
        StudentTConfidence ttest = new StudentTConfidence();
        System.out.println( "Iters: " + ttest.computeConfidenceInterval( iters, confidence ) );
        System.out.println( "Evals: " + ttest.computeConfidenceInterval( evals, confidence ) );
        System.out.println( "Grads: " + ttest.computeConfidenceInterval( grads, confidence ) );
        System.out.println( "Fs:    " + ttest.computeConfidenceInterval( fs, confidence ) );

    }

}
