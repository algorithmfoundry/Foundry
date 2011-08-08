/*
 * File:                LinearDynamicalSystemTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 26, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.signals;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for LinearDynamicalSystemTest.
 *
 * @author krdixon
 */
public class LinearDynamicalSystemTest
    extends TestCase
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
     * Tests for class LinearDynamicalSystemTest.
     * @param testName Name of the test.
     */
    public LinearDynamicalSystemTest(
        String testName)
    {
        super(testName);
    }


    public LinearDynamicalSystem createInstance()
    {

        LinearDynamicalSystem lds = new LinearDynamicalSystem(3,2,1);
        Vector p = lds.convertToVector();
        Vector p2 = VectorFactory.getDefault().createUniformRandom(
            p.getDimensionality(),-0.5, 0.5, RANDOM);
        lds.convertFromVector(p2);
        return lds;
    }

    /**
     * Tests the constructors of class LinearDynamicalSystemTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        LinearDynamicalSystem instance = new LinearDynamicalSystem();
        assertEquals( 1, instance.getInputDimensionality() );
        assertEquals( 1, instance.getOutputDimensionality() );
        assertEquals( 1, instance.getStateDimensionality() );

        int M = RANDOM.nextInt(10) + 1;
        int N = RANDOM.nextInt(10) + M + 1;
        Matrix A = MatrixFactory.getDefault().createUniformRandom(M, M, -1.0, 1.0, RANDOM);
        Matrix B = MatrixFactory.getDefault().createUniformRandom(M, N, -1.0, 1.0, RANDOM);
        instance = new LinearDynamicalSystem( A, B );

        assertEquals( instance.getStateDimensionality(), instance.getOutputDimensionality() );

        try
        {
            instance = new LinearDynamicalSystem(B, B );
            fail( "Bad dimensionality" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of clone method, of class LinearDynamicalSystem.
     */
    public void testClone()
    {
        System.out.println("clone");
        LinearDynamicalSystem instance = this.createInstance();
        LinearDynamicalSystem clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotSame( instance.getA(), clone.getA() );
        assertEquals( instance.getA(), clone.getA() );
        assertNotSame( instance.getB(), clone.getB() );
        assertEquals( instance.getB(), clone.getB() );
        assertNotSame( instance.getC(), clone.getC() );
        assertEquals( instance.getC(), clone.getC() );

        assertNotSame( instance.getState(), clone.getState() );
        assertEquals( instance.getState(), clone.getState() );

    }

    /**
     * Test of createDefaultState method, of class LinearDynamicalSystem.
     */
    public void testCreateDefaultState()
    {
        System.out.println("createDefaultState");
        LinearDynamicalSystem instance = this.createInstance();

        Vector state = instance.createDefaultState();
        assertEquals( instance.getA().getNumRows(), state.getDimensionality() );
        for( int i = 0; i < state.getDimensionality(); i++ )
        {
            assertEquals( 0.0, state.getElement(i));
        }

    }

    /**
     * Test of evaluate method, of class LinearDynamicalSystem.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        LinearDynamicalSystem instance = this.createInstance();
        int M = instance.getStateDimensionality();
        instance.setState( VectorFactory.getDefault().createUniformRandom( M, -1.0, 1.0, RANDOM ) );
        Vector x0 = instance.getState();
        Vector u0 = VectorFactory.getDefault().createUniformRandom(
            instance.getInputDimensionality(), -1.0, 1.0, RANDOM );

        Vector y0 = instance.evaluate(u0);

        assertEquals( instance.getC().times( instance.getA().times( x0 ).plus( instance.getB().times( u0 ))), y0 );

    }

    /**
     * Test of convertToVector method, of class LinearDynamicalSystem.
     */
    public void testConvertToVector()
    {
        System.out.println("convertToVector");
        LinearDynamicalSystem instance = this.createInstance();
        Vector p = instance.convertToVector();
        int ad = instance.getA().getNumRows() * instance.getA().getNumColumns();
        int bd = instance.getB().getNumRows() * instance.getB().getNumColumns();
        assertEquals( ad+bd, p.getDimensionality() );

        assertEquals( instance.getA().convertToVector(), p.subVector(0, ad-1) );
        assertEquals( instance.getB().convertToVector(), p.subVector(ad, ad+bd-1));
    }

    /**
     * Test of convertFromVector method, of class LinearDynamicalSystem.
     */
    public void testConvertFromVector()
    {
        System.out.println("convertFromVector");
        LinearDynamicalSystem instance = this.createInstance();
        Vector p = instance.convertToVector();
        instance.convertFromVector(p);
        assertEquals( p, instance.convertToVector() );

        Vector p2 = p.scale(RANDOM.nextGaussian());
        instance.convertFromVector(p2);
        assertEquals( p2, instance.convertToVector() );

        Vector pbad = VectorFactory.getDefault().createVector(p.getDimensionality()-1);
        try
        {
            instance.convertFromVector(pbad);
            fail( "Wrong dimensions" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getInputDimensionality method, of class LinearDynamicalSystem.
     */
    public void testGetInputDimensionality()
    {
        System.out.println("getInputDimensionality");
        LinearDynamicalSystem instance = this.createInstance();
        assertEquals( instance.getB().getNumColumns(), instance.getInputDimensionality() );
    }

    /**
     * Test of getOutputDimensionality method, of class LinearDynamicalSystem.
     */
    public void testGetOutputDimensionality()
    {
        System.out.println("getOutputDimensionality");
        LinearDynamicalSystem instance = this.createInstance();
        assertEquals( instance.getC().getNumRows(), instance.getOutputDimensionality() );
    }

    /**
     * Test of getA method, of class LinearDynamicalSystem.
     */
    public void testGetA()
    {
        System.out.println("getA");
        LinearDynamicalSystem instance = this.createInstance();
        assertTrue( instance.getA().isSquare() );
    }

    /**
     * Test of setA method, of class LinearDynamicalSystem.
     */
    public void testSetA()
    {
        System.out.println("setA");
        LinearDynamicalSystem instance = this.createInstance();
        Matrix A = instance.getA();
        assertNotNull( A );
        instance.setA(null);
        assertNull(instance.getA());
        instance.setA(A);
        assertSame(A,instance.getA());
    }

    /**
     * Test of getB method, of class LinearDynamicalSystem.
     */
    public void testGetB()
    {
        System.out.println("getB");
        LinearDynamicalSystem instance = this.createInstance();
        Matrix B = instance.getB();
        assertNotNull( B );
    }

    /**
     * Test of setB method, of class LinearDynamicalSystem.
     */
    public void testSetB()
    {
        System.out.println("setB");
        LinearDynamicalSystem instance = this.createInstance();
        Matrix B = instance.getB();
        assertNotNull( B );
        instance.setB(null);
        assertNull(instance.getB());
        instance.setB(B);
        assertSame(B,instance.getB());
    }

    /**
     * Test of getC method, of class LinearDynamicalSystem.
     */
    public void testGetC()
    {
        System.out.println("getC");
        LinearDynamicalSystem instance = this.createInstance();
        Matrix C = instance.getC();
        assertNotNull( C );
    }

    /**
     * Test of setC method, of class LinearDynamicalSystem.
     */
    public void testSetC()
    {
        System.out.println("setC");
        LinearDynamicalSystem instance = this.createInstance();
        Matrix C = instance.getC();
        assertNotNull( C );
        instance.setC(null);
        assertNull(instance.getC());
        instance.setC(C);
        assertSame(C,instance.getC());
    }

    /**
     * toString
     */
    public void testToString()
    {
        System.out.println( "toString" );
        LinearDynamicalSystem lds = this.createInstance();
        String s = lds.toString();
        assertNotNull( s );
        assertTrue( s.length() > 0 );
        System.out.println( "LDS:\n" + s );
    }

}
