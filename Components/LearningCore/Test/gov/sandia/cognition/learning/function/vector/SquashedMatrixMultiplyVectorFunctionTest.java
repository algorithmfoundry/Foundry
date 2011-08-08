/*
 * File:                SquashedMatrixMultiplyVectorFunctionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 28, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.learning.function.scalar.AtanFunction;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for SquashedMatrixMultiplyVectorFunctionTest
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class SquashedMatrixMultiplyVectorFunctionTest
    extends TestCase
{

    /** The random number generator for the tests. */
    public Random random = new Random(1);
    
    public SquashedMatrixMultiplyVectorFunctionTest(
        String testName)
    {
        super(testName);
    }

    public DifferentiableSquashedMatrixMultiplyVectorFunction createRandom()
    {
        double A = 1.0;
        int M = random.nextInt(10) + 1;
        int N = random.nextInt(10) + 1;

        Matrix m = MatrixFactory.getDefault().createUniformRandom(M, N, -A, A, random);

        return new DifferentiableSquashedMatrixMultiplyVectorFunction(
            new MatrixMultiplyVectorFunction(m),
            new AtanFunction());

    }

    public void testConstructors()
    {
        System.out.println( "Constructors" );

        SquashedMatrixMultiplyVectorFunction f =
            new SquashedMatrixMultiplyVectorFunction();
        assertEquals( 1, f.getInputDimensionality() );
        assertEquals( 1, f.getOutputDimensionality() );
        assertNotNull( f.getSquashingFunction() );

    }

    /**
     * Test of getMatrixMultiply method, of class gov.sandia.isrc.learning.util.function.SquashedMatrixMultiplyVectorFunction.
     */
    public void testGetMatrixMultiply()
    {
        System.out.println("getMatrixMultiply");

        SquashedMatrixMultiplyVectorFunction instance = this.createRandom();

        assertNotNull(instance.getMatrixMultiply());

    }

    /**
     * Test of setMatrixMultiply method, of class gov.sandia.isrc.learning.util.function.SquashedMatrixMultiplyVectorFunction.
     */
    public void testSetMatrixMultiply()
    {
        System.out.println("setMatrixMultiply");

        SquashedMatrixMultiplyVectorFunction instance = this.createRandom();

        assertNotNull(instance.getMatrixMultiply());

        Matrix m = MatrixFactory.getDefault().createUniformRandom(2, 3, -1, 1, random);
        MatrixMultiplyVectorFunction mult =
            new MatrixMultiplyVectorFunction(m);

        assertNotSame(mult, instance.getMatrixMultiply());

        instance.setMatrixMultiply(mult);
        assertSame(mult, instance.getMatrixMultiply());
    }

    /**
     * Test of getSquashingFunction method, of class gov.sandia.isrc.learning.util.function.SquashedMatrixMultiplyVectorFunction.
     */
    public void testGetSquashingFunction()
    {
        System.out.println("getSquashingFunction");

        SquashedMatrixMultiplyVectorFunction instance = this.createRandom();
        assertNotNull(instance.getSquashingFunction());

    }

    /**
     * Test of setSquashingFunction method, of class gov.sandia.isrc.learning.util.function.SquashedMatrixMultiplyVectorFunction.
     */
    public void testSetSquashingFunction()
    {
        System.out.println("setSquashingFunction");

        SquashedMatrixMultiplyVectorFunction instance = this.createRandom();

        LinearVectorFunction f = new LinearVectorFunction(random.nextGaussian());
        assertNotSame(f, instance.getSquashingFunction());

        instance.setSquashingFunction(f);
        assertSame(f, instance.getSquashingFunction());

    }

    /**
     * Test of convertToVector method, of class gov.sandia.isrc.learning.util.function.SquashedMatrixMultiplyVectorFunction.
     */
    public void testConvertToVector()
    {
        System.out.println("convertToVector");

        SquashedMatrixMultiplyVectorFunction instance = this.createRandom();

        Vector result = instance.convertToVector();
        Vector expected = instance.getMatrixMultiply().convertToVector();
        assertEquals(expected, result);

    }

    /**
     * Test of convertFromVector method, of class gov.sandia.isrc.learning.util.function.SquashedMatrixMultiplyVectorFunction.
     */
    public void testConvertFromVector()
    {
        System.out.println("convertFromVector");

        SquashedMatrixMultiplyVectorFunction instance = this.createRandom();

        Vector p1 = instance.convertToVector();

        Vector p2 = p1.scale(random.nextGaussian());
        instance.convertFromVector(p2);

        Vector p3 = instance.convertToVector();
        assertEquals(p2, p3);

    }

    /**
     * Test of evaluate method, of class gov.sandia.isrc.learning.util.function.SquashedMatrixMultiplyVectorFunction.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");

        SquashedMatrixMultiplyVectorFunction instance = this.createRandom();

        int M = instance.getMatrixMultiply().getInternalMatrix().getNumRows();
        int N = instance.getMatrixMultiply().getInternalMatrix().getNumColumns();

        Vector input = VectorFactory.getDefault().createUniformRandom(N, -10.0, 10.0, random);

        Vector result = instance.evaluate(input);
        assertEquals(M, result.getDimensionality());

        Vector expected = instance.getSquashingFunction().evaluate(instance.getMatrixMultiply().getInternalMatrix().times(input));
        assertEquals(expected.getDimensionality(), result.getDimensionality());

        assertEquals(expected, result);

    }

    /**
     * Test of clone method, of class gov.sandia.isrc.learning.util.function.SquashedMatrixMultiplyVectorFunction.
     */
    public void testClone()
    {
        System.out.println("clone");

        SquashedMatrixMultiplyVectorFunction instance = this.createRandom();

        SquashedMatrixMultiplyVectorFunction clone = instance.clone();

        assertEquals(instance.getMatrixMultiply().getInternalMatrix(), instance.getMatrixMultiply().getInternalMatrix());

        int N = instance.getMatrixMultiply().getInternalMatrix().getNumColumns();
        Vector input = VectorFactory.getDefault().createUniformRandom(N, -1.0, 1.0, random);

        Vector v1 = instance.evaluate(input);
        assertEquals(v1, clone.evaluate(input));

        clone.getMatrixMultiply().getInternalMatrix().setElement(0, 0, random.nextGaussian());
        assertEquals(v1, instance.evaluate(input));

        assertFalse(v1.equals(clone.evaluate(input)));

    }

    public void testDimensionality()
    {

        SquashedMatrixMultiplyVectorFunction instance = this.createRandom();
        assertEquals( instance.getMatrixMultiply().getInputDimensionality(), instance.getInputDimensionality() );
        assertEquals( instance.getMatrixMultiply().getOutputDimensionality(), instance.getOutputDimensionality() );

        double r = 2.0;
        Vector x = VectorFactory.getDefault().createUniformRandom(
            instance.getInputDimensionality(), -r, r, random);
        Vector y = instance.evaluate(x);
        assertEquals( x.getDimensionality(), instance.getInputDimensionality() );
        assertEquals( y.getDimensionality(), instance.getOutputDimensionality() );

    }
}
