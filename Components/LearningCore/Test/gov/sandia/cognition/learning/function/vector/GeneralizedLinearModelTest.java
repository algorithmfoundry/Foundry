/*
 * File:                GeneralizedLinearModelTest.java
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
import gov.sandia.cognition.learning.function.scalar.IdentityScalarFunction;
import gov.sandia.cognition.learning.function.scalar.SigmoidFunction;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFunction;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for GeneralizedLinearModelTest
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class GeneralizedLinearModelTest
    extends TestCase
{

    /** The random number generator for the tests. */
    public Random random = new Random(1);
    
    public GeneralizedLinearModelTest(
        String testName)
    {
        super(testName);
    }

    public DifferentiableGeneralizedLinearModel createRandom()
    {
        double A = 1.0;
        int M = random.nextInt(10) + 1;
        int N = random.nextInt(10) + 1;

        Matrix m = MatrixFactory.getDefault().createUniformRandom(M, N, -A, A, random);

        return new DifferentiableGeneralizedLinearModel(
            new MultivariateDiscriminant(m),
            new AtanFunction());

    }

    public void testConstructors()
    {
        System.out.println( "Constructors" );

        GeneralizedLinearModel f =
            new GeneralizedLinearModel();
        assertEquals( 1, f.getInputDimensionality() );
        assertEquals( 1, f.getOutputDimensionality() );
        assertNotNull( f.getSquashingFunction() );
        assertTrue(f.getSquashingFunction() instanceof ElementWiseVectorFunction);
        assertTrue(((ElementWiseVectorFunction) f.getSquashingFunction()).getScalarFunction() instanceof IdentityScalarFunction);

        SigmoidFunction s = new SigmoidFunction();
        f = new GeneralizedLinearModel(5, 2, s);
        assertEquals(5, f.getInputDimensionality());
        assertEquals(2, f.getOutputDimensionality());
        assertTrue(f.getSquashingFunction() instanceof ElementWiseVectorFunction);
        assertSame(s, ((ElementWiseVectorFunction) f.getSquashingFunction()).getScalarFunction());

        MultivariateDiscriminant d = new MultivariateDiscriminant(3, 4);
        f = new GeneralizedLinearModel(d, s);
        assertEquals(3, f.getInputDimensionality());
        assertEquals(4, f.getOutputDimensionality());
        assertSame(d, f.getDiscriminant());
        assertTrue(f.getSquashingFunction() instanceof ElementWiseVectorFunction);
        assertSame(s, ((ElementWiseVectorFunction) f.getSquashingFunction()).getScalarFunction());

        VectorFunction v = new LinearVectorFunction();
        f = new GeneralizedLinearModel(d, v);
        assertEquals(3, f.getInputDimensionality());
        assertEquals(4, f.getOutputDimensionality());
        assertSame(d, f.getDiscriminant());
        assertSame(v, f.getSquashingFunction());

        f = new GeneralizedLinearModel(f);
        assertEquals(3, f.getInputDimensionality());
        assertEquals(4, f.getOutputDimensionality());
        assertNotSame(d, f.getDiscriminant());
        assertEquals(d.convertToVector(), f.getDiscriminant().convertToVector());
        assertSame(v, f.getSquashingFunction());

    }

    /**
     * Test of getDiscriminant method, of class gov.sandia.isrc.learning.util.function.GeneralizedLinearModel.
     */
    public void testGetMatrixMultiply()
    {
        System.out.println("getMatrixMultiply");

        GeneralizedLinearModel instance = this.createRandom();

        assertNotNull(instance.getDiscriminant());

    }

    /**
     * Test of setDiscriminant method, of class gov.sandia.isrc.learning.util.function.GeneralizedLinearModel.
     */
    public void testSetMatrixMultiply()
    {
        System.out.println("setMatrixMultiply");

        GeneralizedLinearModel instance = this.createRandom();

        assertNotNull(instance.getDiscriminant());

        Matrix m = MatrixFactory.getDefault().createUniformRandom(2, 3, -1, 1, random);
        MultivariateDiscriminant mult =
            new MultivariateDiscriminant(m);

        assertNotSame(mult, instance.getDiscriminant());

        instance.setDiscriminant(mult);
        assertSame(mult, instance.getDiscriminant());
    }

    /**
     * Test of getSquashingFunction method, of class gov.sandia.isrc.learning.util.function.GeneralizedLinearModel.
     */
    public void testGetSquashingFunction()
    {
        System.out.println("getSquashingFunction");

        GeneralizedLinearModel instance = this.createRandom();
        assertNotNull(instance.getSquashingFunction());

    }

    /**
     * Test of setSquashingFunction method, of class gov.sandia.isrc.learning.util.function.GeneralizedLinearModel.
     */
    public void testSetSquashingFunction()
    {
        System.out.println("setSquashingFunction");

        GeneralizedLinearModel instance = this.createRandom();

        LinearVectorFunction f = new LinearVectorFunction(random.nextGaussian());
        assertNotSame(f, instance.getSquashingFunction());

        instance.setSquashingFunction(f);
        assertSame(f, instance.getSquashingFunction());

    }

    /**
     * Test of convertToVector method, of class gov.sandia.isrc.learning.util.function.GeneralizedLinearModel.
     */
    public void testConvertToVector()
    {
        System.out.println("convertToVector");

        GeneralizedLinearModel instance = this.createRandom();

        Vector result = instance.convertToVector();
        Vector expected = instance.getDiscriminant().convertToVector();
        assertEquals(expected, result);

    }

    /**
     * Test of convertFromVector method, of class gov.sandia.isrc.learning.util.function.GeneralizedLinearModel.
     */
    public void testConvertFromVector()
    {
        System.out.println("convertFromVector");

        GeneralizedLinearModel instance = this.createRandom();

        Vector p1 = instance.convertToVector();

        Vector p2 = p1.scale(random.nextGaussian());
        instance.convertFromVector(p2);

        Vector p3 = instance.convertToVector();
        assertEquals(p2, p3);

    }

    /**
     * Test of evaluate method, of class gov.sandia.isrc.learning.util.function.GeneralizedLinearModel.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");

        GeneralizedLinearModel instance = this.createRandom();

        int M = instance.getDiscriminant().getDiscriminant().getNumRows();
        int N = instance.getDiscriminant().getDiscriminant().getNumColumns();

        Vector input = VectorFactory.getDefault().createUniformRandom(N, -10.0, 10.0, random);

        Vector result = instance.evaluate(input);
        assertEquals(M, result.getDimensionality());

        Vector expected = instance.getSquashingFunction().evaluate(instance.getDiscriminant().getDiscriminant().times(input));
        assertEquals(expected.getDimensionality(), result.getDimensionality());

        assertEquals(expected, result);

    }

    /**
     * Test of clone method, of class gov.sandia.isrc.learning.util.function.GeneralizedLinearModel.
     */
    public void testClone()
    {
        System.out.println("clone");

        GeneralizedLinearModel instance = this.createRandom();

        GeneralizedLinearModel clone = instance.clone();

        assertEquals(instance.getDiscriminant().getDiscriminant(), instance.getDiscriminant().getDiscriminant());

        int N = instance.getDiscriminant().getDiscriminant().getNumColumns();
        Vector input = VectorFactory.getDefault().createUniformRandom(N, -1.0, 1.0, random);

        Vector v1 = instance.evaluate(input);
        assertEquals(v1, clone.evaluate(input));

        clone.getDiscriminant().getDiscriminant().setElement(0, 0, random.nextGaussian());
        assertEquals(v1, instance.evaluate(input));

        assertFalse(v1.equals(clone.evaluate(input)));

    }

    public void testDimensionality()
    {

        GeneralizedLinearModel instance = this.createRandom();
        assertEquals( instance.getDiscriminant().getInputDimensionality(), instance.getInputDimensionality() );
        assertEquals( instance.getDiscriminant().getOutputDimensionality(), instance.getOutputDimensionality() );

        double r = 2.0;
        Vector x = VectorFactory.getDefault().createUniformRandom(
            instance.getInputDimensionality(), -r, r, random);
        Vector y = instance.evaluate(x);
        assertEquals( x.getDimensionality(), instance.getInputDimensionality() );
        assertEquals( y.getDimensionality(), instance.getOutputDimensionality() );

    }
}
