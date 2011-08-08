/*
 * File:                FunctionMinimizerTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 8, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.minimization;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.statistics.method.StudentTConfidence;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.scalar.AtanFunction;
import gov.sandia.cognition.learning.function.vector.ElementWiseDifferentiableVectorFunction;
import gov.sandia.cognition.math.DifferentiableEvaluator;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Test harness for unconstrained nonlinear optimization
 * @param <EvaluatorType> Type of Evaluator to minimize
 * @author Kevin R. Dixon
 */
public abstract class FunctionMinimizerTestHarness<EvaluatorType extends Evaluator<? super Vector, ? extends Double>> extends TestCase
{

    /**
     * Random number generator
     */
    public Random random = new Random( 0 );
    
    /**
     * Creates a new test
     * @param testName
     * Name of test
     */
    public FunctionMinimizerTestHarness(String testName)
    {
        super(testName);
    }

    /**
     * Function containing function/gradient counts
     */
    public abstract static class AbstractMinimizationTestFunction
        implements DifferentiableEvaluator<Vector, Double, Vector>
    {

        /**
         * Number of function evaluations
         */
        public int FUNCTION_EVALUATIONS = 0;
        
        /**
         * Number of gradient evaluations
         */
        public int GRADIENT_EVALUATIONS = 0;
        
        @Override
        public AbstractMinimizationTestFunction clone()
        {
            return this;
        }
        
    }
    
    /**
     * 2-norm function
     */
    public static class VectorFunctionQuadratic
        extends AbstractMinimizationTestFunction
    {

        public Double evaluate(Vector input)
        {
            this.FUNCTION_EVALUATIONS++;
            return input.norm2();
        }

        public Vector differentiate(
            Vector input)
        {
            this.GRADIENT_EVALUATIONS++;
            return input.scale(2.0);
        }

    }

    /**
     * 2-norm of element-wise atan function
     */
    public static class VectorFunctionAtanQuadratic
        extends AbstractMinimizationTestFunction
    {

        /**
         * Element-wise atan function
         */
        ElementWiseDifferentiableVectorFunction f =
            new ElementWiseDifferentiableVectorFunction(new AtanFunction(1.0));

        public Double evaluate(Vector input)
        {
            this.FUNCTION_EVALUATIONS++;
            return f.evaluate(input).norm2Squared();
        }

        public Vector differentiate(
            Vector input)
        {
            this.GRADIENT_EVALUATIONS++;
            
            // This is g(f(x)), where g(x) == x^2
            // By the chain rule, we have f'(x)*2.0*f(x)
            return f.differentiate( input ).times( f.evaluate( input ) ).scale( 2.0 );
//            return f.differentiate(input).times(input).scale(2.0);
        }

    }

    /**
     * The "Rosenbrock Banana Function" commonly used to test 
     * minimization alorithms.
     * Minimum is at xstar = ones() and f(ones()) = 0.0;
     */
    public static class VectorRosenbrockFunction
        extends AbstractMinimizationTestFunction
    {

        public Vector differentiate( Vector input )
        {
            this.GRADIENT_EVALUATIONS++;            
            Vector gradient = VectorFactory.getDefault().createVector(
                input.getDimensionality() );
            for( int i = 0; i < input.getDimensionality()-1; i++ )
            {
                double xi = input.getElement( i );
                double xip1 = input.getElement( i+1 );
                double gi = -2.0*(1.0-xi) - 400.0*(xip1-xi*xi)*xi;
                double gip1 = 200.0*(xip1-xi*xi);
                gradient.setElement( i, gradient.getElement(i) + gi );
                gradient.setElement( i+1, gradient.getElement(i+1) + gip1 );
            }
            
            return gradient;
            
        }

        public Double evaluate( Vector input )
        {
            this.FUNCTION_EVALUATIONS++;
            double sum = 0.0;
            for( int i = 0; i < input.getDimensionality()-1; i++ )
            {
                double xi = input.getElement( i );
                double xip1 = input.getElement( i+1 );
                double t1 = 1.0-xi;
                double t2 = 10.0*(xip1-xi*xi);
                sum += t1*t1 + t2*t2;
            }
            return sum;
        }
    }

    public static class VectorFreudensteinFunction
        extends AbstractMinimizationTestFunction
    {

        private Vector innerFunction( Vector input )
        {
            assertEquals( 2, input.getDimensionality() );
            double x0 = input.getElement(0);
            double x1 = input.getElement(1);
            
            double y0 = x0 - x1*(2.0-x1*(5.0-x1))-13.0;
            double y1 = x0 - x1*(14.0-x1*(1.0+x1))-29.0;
            return VectorFactory.getDefault().copyValues( y0, y1 );
        }
        
        public Vector differentiate( Vector input )
        {
            return null;
        }

        public Double evaluate( Vector input )
        {
            return this.innerFunction( input ).norm2Squared();
        }
        
    }
        
    
    /**
     * Creates a set of pre-defined initialGuess coordinates
     * @param dim
     * Dimensionality of the guesses
     * @param num
     * Number of guesses to generate
     * @return
     * ArrayList of initialGuesses
     */
    public ArrayList<Vector> createInitialGuesses(
        int dim,
        int num)
    {
        ArrayList<Vector> guesses = new ArrayList<Vector>(num);
        double a = 5.0;
//        double a = 0.0;
        for (int n = 0; n < num; n++)
        {
            Vector v = VectorFactory.getDefault().createVector(dim);
            for (int i = 0; i < v.getDimensionality(); i++)
            {
                v.setElement(i, this.random.nextDouble() * 2 * a - a);
            }
            guesses.add(v);
        }

        return guesses;

    }

    /**
     * Tolerance to use
     */
    public double TOLERANCE = 1e-5;

    public double REQUIRED_ACCURACY = 1e-4;
    
    public int MAX_ITERATIONS = 100;
    
    /**
     * Creates a new instance of the minimizer
     * @return
     * new instance of the minimizer to test
     */
    public abstract FunctionMinimizer<Vector, Double, EvaluatorType> createInstance();

    /**
     * Test the performance of the minimizer against this function
     * @param f
     * Function to test
     */
    @SuppressWarnings("unchecked")
    public void functionBakeOff(
        AbstractMinimizationTestFunction f,
        int M )
    {
        ArrayList<Vector> guesses = this.createInitialGuesses(M, 100);

        FunctionMinimizer<Vector, Double, EvaluatorType> mini = this.createInstance();
        assertTrue(mini.getTolerance() > 0.0);
        assertNull(mini.getInitialGuess());

        // Test that the minimizer doesn't alter the initial guess
        mini.setInitialGuess( guesses.get(0) );
        assertEquals( mini.getInitialGuess(), guesses.get(0) );
        Vector clone = mini.getInitialGuess().clone();
        InputOutputPair<Vector, Double> result = mini.learn( (EvaluatorType) f );
        assertEquals(clone, mini.getInitialGuess());


        LinkedList<Double> i1 = new LinkedList<Double>();
        LinkedList<Double> e1 = new LinkedList<Double>();
        LinkedList<Double> g1 = new LinkedList<Double>();
        LinkedList<Double> t1 = new LinkedList<Double>();
        LinkedList<Double> a1 = new LinkedList<Double>();

        int which = 0;
        for (Vector initialGuess : guesses)
        {
            f.FUNCTION_EVALUATIONS = 0;
            f.GRADIENT_EVALUATIONS = 0;
            long start = System.currentTimeMillis();
            mini.setInitialGuess( initialGuess );
            
            result = mini.learn((EvaluatorType) f);
            long stop = System.currentTimeMillis();
            i1.add( (double) mini.getIteration() );
            e1.add( (double) f.FUNCTION_EVALUATIONS );
            g1.add( (double) f.GRADIENT_EVALUATIONS );
            t1.add((double) (stop - start));
            
            double feval = f.evaluate( result.getInput() );
            assertEquals( feval, result.getOutput() );
            double accuracy = Math.abs(feval);
            assertEquals( which + ": Accuracy Failed at Iteration: " + mini.getIteration(), 0.0, accuracy, REQUIRED_ACCURACY );
            
            a1.add(accuracy);
            which++;
        }

        StudentTConfidence ttest = new StudentTConfidence();
        double confidence = 0.95;
        System.out.println("==============" + mini.getClass() + ", Function: " + f.getClass() + "==============");
        System.out.println(
            "Iterations: " + ttest.computeConfidenceInterval(i1, confidence) +
            "\nEvaluations: " + ttest.computeConfidenceInterval(e1, confidence) +
            "\nGradients: " +  ttest.computeConfidenceInterval(g1, confidence) +
            "\nTime:        " + ttest.computeConfidenceInterval(t1, confidence) +
            "\nAccuracy:    " + ttest.computeConfidenceInterval(a1, confidence));


    }

    /**
     * Test of learn method, of class gov.sandia.cognition.learning.algorithm.minimization.FunctionMinimizer.
     */
    public void testMinimizeQuadratic()
    {
        System.out.println( "Minimize Quadratic" );
        this.functionBakeOff( new VectorFunctionQuadratic(), 3 );
    }

    /**
     * Test of learn method, of class gov.sandia.cognition.learning.algorithm.minimization.FunctionMinimizer.
     */
    public void testMinimizeAtanQuadratic()
    {
        System.out.println( "Minimize Atan Quadratic" );
        this.REQUIRED_ACCURACY = 1e-3;
        this.TOLERANCE = 1e-5;
        this.functionBakeOff( new VectorFunctionAtanQuadratic(), 3 );
    }

    /**
     * Test of learn method, of class gov.sandia.cognition.learning.algorithm.minimization.FunctionMinimizer.
     */
    @PublicationReference(
        author="R. Fletcher",
        title="Practical Methods of Optimization, Second Edition",
        type=PublicationType.Book,
        year=1987,
        pages={70,71,84,114},
        notes={
            "Fletcher gives his performance against the Rosenbrock function in several tables in his book.",
            "I believe Fletcher is reporting his results as starting from (0,0) with accuracy <1e-8",
            "Section 3.5, Table 3.5.1, Table 3.5.2, Table 3.5.3",
            "Section 4.1, Table 4.1.2",
            "Section 6.1, Table 6.1.2"
        }
    )
    public void testMinimizeRosenbrock()
    {
        System.out.println( "Minimize Rosenbrock" );
        this.TOLERANCE = 1e-5;
        this.REQUIRED_ACCURACY = 1e-1;
        this.functionBakeOff( new VectorRosenbrockFunction(), 2 );
    }
    
}
