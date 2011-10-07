/*
 * File:                LogisticRegressionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 27, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.learning.algorithm.regression.LogisticRegression.Function;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.DefaultWeightedInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * JUnit tests for class LogisticRegressionTest
 * @author Kevin R. Dixon
 */
public class LogisticRegressionTest
    extends TestCase
{

    /** The random number generator for the tests. */
    public final Random random = new Random(1);

    /**
     * Tolerance for quality
     */
    public final double TOLERANCE = 1e-4;

    /**
     * Entry point for JUnit tests for class LogisticRegressionTest
     * @param testName name of this test
     */
    public LogisticRegressionTest(
        String testName )
    {
        super( testName );
    }

    /**
     * Learn
     */
    public void testLearn()
    {
        System.out.println( "learn" );

        LogisticRegression instance = new LogisticRegression();
        
        
        // From http://faculty.vassar.edu/lowry/logreg1.html
        LinkedList<InputOutputPair<Vector,Double>> data =
            new LinkedList<InputOutputPair<Vector, Double>>();
        data.add( new DefaultWeightedInputOutputPair<Vector, Double>(
            VectorFactory.getDefault().copyValues( 28.0 ), 1.0/3.0, 6 ) );
        data.add( new DefaultWeightedInputOutputPair<Vector, Double>(
            VectorFactory.getDefault().copyValues( 29.0 ), 2.0/5.0, 5 ) );
        data.add( new DefaultWeightedInputOutputPair<Vector, Double>(
            VectorFactory.getDefault().copyValues( 30.0 ), 7.0/9.0, 9 ) );
        data.add( new DefaultWeightedInputOutputPair<Vector, Double>(
            VectorFactory.getDefault().copyValues( 31.0 ), 7.0/9.0, 9 ) );
        data.add( new DefaultWeightedInputOutputPair<Vector, Double>(
            VectorFactory.getDefault().copyValues( 32.0 ), 16.0/20.0, 20 ) );
        data.add( new DefaultWeightedInputOutputPair<Vector, Double>(
            VectorFactory.getDefault().copyValues( 33.0 ), 14.0/15.0, 15 ) );

        LogisticRegression.Function f = instance.learn( data );
        Vector w = f.convertToVector();
        assertEquals( 2, w.getDimensionality() );
        
        assertEquals( 0.5769, f.getFirst().getWeightVector().getElement(0), TOLERANCE );
        assertEquals( -16.7198, f.getFirst().getBias(), TOLERANCE );
        
        assertSame( f, instance.getResult() );
        assertNotSame( instance.getObjectToOptimize(), instance.getResult() );
        
        Vector wclone = w.clone();
        
        LogisticRegression.Function f2 = instance.learn( data );
        Vector w2 = f2.convertToVector();
        
        assertEquals( wclone, w2 );

        LogisticRegression.Function fclone = f2.clone();
        assertNotNull( fclone );
        assertNotSame( f2, fclone );
        assertEquals( f2.convertToVector(), fclone.convertToVector() );


        LogisticRegression clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotNull( clone.getResult() );
        assertNotSame( instance.getResult(), clone.getResult() );
        assertNotNull( clone.getObjectToOptimize() );
        assertNotSame( instance.getObjectToOptimize(), clone.getObjectToOptimize() );
        
    }

    /**
     * Learn
     */
    public void testLearn2()
    {
        System.out.println( "learn2" );

        // http://luna.cas.usf.edu/~mbrannic/files/regression/Logistic.html
        LinkedList<InputOutputPair<Vector,Double>> data =
            new LinkedList<InputOutputPair<Vector, Double>>();
        data.add( DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(1.0, 70), 1.0 ) );
        data.add( DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(1.0, 80), 1.0 ) );
        data.add( DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(1.0, 50), 1.0 ) );
        data.add( DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(0.0, 60), 1.0 ) );
        data.add( DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(0.0, 40), 1.0 ) );
        data.add( DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(0.0, 65), 1.0 ) );
        data.add( DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(0.0, 75), 1.0 ) );
        data.add( DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(0.0, 80), 1.0 ) );
        data.add( DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(0.0, 70), 1.0 ) );
        data.add( DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(0.0, 60), 1.0 ) );
        data.add( DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(1.0, 65), 0.0 ) );
        data.add( DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(1.0, 50), 0.0 ) );
        data.add( DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(1.0, 45), 0.0 ) );
        data.add( DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(1.0, 35), 0.0 ) );
        data.add( DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(1.0, 40), 0.0 ) );
        data.add( DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(1.0, 50), 0.0 ) );
        data.add( DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(0.0, 55), 0.0 ) );
        data.add( DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(0.0, 45), 0.0 ) );
        data.add( DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(0.0, 50), 0.0 ) );
        data.add( DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(0.0, 60), 0.0 ) );

        final double r1 = 0.1;
        LogisticRegression instance = new LogisticRegression(r1);
        assertEquals( r1, instance.getRegularization() );
        LogisticRegression.Function f = instance.learn( data );
        Vector w1 = f.convertToVector();
        System.out.println( "R1: " + w1 );

        // As we increase the regularization term, that will decrease the
        // L2 norm of the resulting weight vector.
        final double r2 = 1.0;
        instance.setRegularization(r2);
        assertEquals( r2, instance.getRegularization() );
        f = instance.learn(data);
        Vector w2 = f.convertToVector();
        System.out.println( "R2: " + w2 );

        assertTrue( w2.norm2() < w1.norm1() );

        
        
    }

    /**
     * Test of getObjectToOptimize method, of class LogisticRegression.
     */
    public void testGetObjectToOptimize()
    {
        System.out.println( "getObjectToOptimize" );
        LogisticRegression instance = new LogisticRegression();
        assertNull( instance.getObjectToOptimize() );
    }

    /**
     * Test of setObjectToOptimize method, of class LogisticRegression.
     */
    public void testSetObjectToOptimize()
    {
        System.out.println( "setObjectToOptimize" );
        Function objectToOptimize = new Function( 2 );
        LogisticRegression instance = new LogisticRegression();
        assertNull( instance.getObjectToOptimize() );
        instance.setObjectToOptimize( objectToOptimize );
        assertSame( objectToOptimize, instance.getObjectToOptimize() );
    }

    /**
     * Test of getTolerance method, of class LogisticRegression.
     */
    public void testGetTolerance()
    {
        System.out.println( "getTolerance" );
        LogisticRegression instance = new LogisticRegression();
        assertEquals( LogisticRegression.DEFAULT_TOLERANCE, instance.getTolerance() );
    }

    /**
     * Test of setTolerance method, of class LogisticRegression.
     */
    public void testSetTolerance()
    {
        System.out.println( "setTolerance" );
        LogisticRegression instance = new LogisticRegression();
        assertEquals( LogisticRegression.DEFAULT_TOLERANCE, instance.getTolerance() );
        double tolerance = instance.getTolerance() + 1.0;
        instance.setTolerance( tolerance );
        assertEquals( tolerance, instance.getTolerance() );
    }

}
