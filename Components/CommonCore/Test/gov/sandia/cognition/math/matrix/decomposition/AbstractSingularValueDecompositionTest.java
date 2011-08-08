/*
 * File:                AbstractSingularValueDecompositionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 17, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.math.matrix.decomposition;


import gov.sandia.cognition.math.matrix.mtj.DenseMatrix;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.decomposition.SingularValueDecompositionMTJ;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     AbstractSingularValueDecomposition
 * 
 * @author Kevin R. Dixon
 * @since 1.0
 */
public class AbstractSingularValueDecompositionTest extends TestCase
{

    /**
     * 
     * @param testName
     */
    public AbstractSingularValueDecompositionTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Returns the test.
     * @return 
     */
    public static Test suite()
    {
        TestSuite suite = new TestSuite(AbstractSingularValueDecompositionTest.class);

        return suite;
    }

    /**
     * Test of norm2 method, of class 
     * gov.sandia.isrc.math.matrix.AbstractSingularValueDecomposition.
     */
    public void testNorm2()
    {
        System.out.println("norm2");

        DenseMatrix m2 = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(3, 4);
        m2.setElement(0, 0, 1.0);
        m2.setElement(0, 1, 2.0);
        m2.setElement(0, 2, 3.0);
        m2.setElement(0, 3, 4.0);

        m2.setElement(1, 0, 5.0);
        m2.setElement(1, 1, 6.0);
        m2.setElement(1, 2, -1.0);
        m2.setElement(1, 3, -2.0);

        m2.setElement(2, 0, -3.0);
        m2.setElement(2, 1, -4.0);
        m2.setElement(2, 2, -5.0);
        m2.setElement(2, 3, -6.0);

        AbstractSingularValueDecomposition instance =
            SingularValueDecompositionMTJ.create(m2);

        double e1 = 11.124;
        assertEquals(e1, instance.norm2(), 0.001);
    }

    /**
     * Test of conditionNumber method, of class 
     * gov.sandia.isrc.math.matrix.AbstractSingularValueDecomposition.
     */
    public void testConditionNumber()
    {
        System.out.println("conditionNumber");

        DenseMatrix m2 = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(3, 4);
        m2.setElement(0, 0, 1.0);
        m2.setElement(0, 1, 2.0);
        m2.setElement(0, 2, 3.0);
        m2.setElement(0, 3, 4.0);

        m2.setElement(1, 0, 5.0);
        m2.setElement(1, 1, 6.0);
        m2.setElement(1, 2, -1.0);
        m2.setElement(1, 3, -2.0);

        m2.setElement(2, 0, -3.0);
        m2.setElement(2, 1, -4.0);
        m2.setElement(2, 2, -5.0);
        m2.setElement(2, 3, -6.0);

        AbstractSingularValueDecomposition instance =
            SingularValueDecompositionMTJ.create(m2);

        double e1 = 32.099;
        assertEquals(e1, instance.conditionNumber(), 0.001);
    }

    /**
     * Test of rank method, of class 
     * gov.sandia.isrc.math.matrix.AbstractSingularValueDecomposition.
     */
    public void testRank()
    {
        System.out.println("rank");

        DenseMatrix m2 = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(3, 4);
        m2.setElement(0, 0, 1.0);
        m2.setElement(0, 1, 2.0);
        m2.setElement(0, 2, 3.0);
        m2.setElement(0, 3, 4.0);

        m2.setElement(1, 0, 5.0);
        m2.setElement(1, 1, 6.0);
        m2.setElement(1, 2, -1.0);
        m2.setElement(1, 3, -2.0);

        m2.setElement(2, 0, -3.0);
        m2.setElement(2, 1, -4.0);
        m2.setElement(2, 2, -5.0);
        m2.setElement(2, 3, -6.0);

        AbstractSingularValueDecomposition instance =
            SingularValueDecompositionMTJ.create(m2);

        assertEquals(3, instance.rank());

        instance = SingularValueDecompositionMTJ.create(
            DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 6));
        assertEquals(0, instance.rank());
    }

    /**
     * Test of effectiveRank method, of class 
     * gov.sandia.isrc.math.matrix.AbstractSingularValueDecomposition.
     */
    public void testEffectiveRank()
    {
        System.out.println("effectiveRank");

        DenseMatrix m2 = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(3, 4);
        m2.setElement(0, 0, 1.0);
        m2.setElement(0, 1, 2.0);
        m2.setElement(0, 2, 3.0);
        m2.setElement(0, 3, 4.0);

        m2.setElement(1, 0, 5.0);
        m2.setElement(1, 1, 6.0);
        m2.setElement(1, 2, -1.0);
        m2.setElement(1, 3, -2.0);

        m2.setElement(2, 0, -3.0);
        m2.setElement(2, 1, -4.0);
        m2.setElement(2, 2, -5.0);
        m2.setElement(2, 3, -6.0);

        AbstractSingularValueDecomposition instance =
            SingularValueDecompositionMTJ.create(m2);

        assertEquals(3, instance.effectiveRank(0.0));
        assertEquals(2, instance.effectiveRank(0.5));
        assertEquals(2, instance.effectiveRank(1.0));
        assertEquals(1, instance.effectiveRank(10.0));
        assertEquals(0, instance.effectiveRank(100.0));
    }

    /**
     * Test of pseudoInverse method, of class 
     * gov.sandia.isrc.math.matrix.AbstractSingularValueDecomposition.
     */
    public void testPseudoInverse()
    {
        System.out.println("pseudoInverse");

        DenseMatrix m2 = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(3, 4);
        m2.setElement(0, 0, 1.0);
        m2.setElement(0, 1, 2.0);
        m2.setElement(0, 2, 3.0);
        m2.setElement(0, 3, 4.0);

        m2.setElement(1, 0, 5.0);
        m2.setElement(1, 1, 6.0);
        m2.setElement(1, 2, -1.0);
        m2.setElement(1, 3, -2.0);

        m2.setElement(2, 0, -3.0);
        m2.setElement(2, 1, -4.0);
        m2.setElement(2, 2, -5.0);
        m2.setElement(2, 3, -6.0);

        AbstractSingularValueDecomposition instance =
            SingularValueDecompositionMTJ.create(m2);

        DenseMatrix e1 = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 3);
        e1.setElement(0, 0, -1.611111);
        e1.setElement(0, 1, -0.111111);
        e1.setElement(0, 2, -1.000000);

        e1.setElement(1, 0, 1.407407);
        e1.setElement(1, 1, 0.240741);
        e1.setElement(1, 2, 0.833333);

        e1.setElement(2, 0, -0.981481);
        e1.setElement(2, 1, -0.148148);
        e1.setElement(2, 2, -0.666667);

        e1.setElement(3, 0, 0.685185);
        e1.setElement(3, 1, 0.018519);
        e1.setElement(3, 2, 0.333333);

        assertTrue(e1.equals(instance.pseudoInverse(0.0), 0.00001));

        DenseMatrix e2 = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 3);
        e2.setElement(0, 0, 9.8276e-04);
        e2.setElement(0, 1, 7.1082e-02);
        e2.setElement(0, 2, -1.4753e-02);

        e2.setElement(1, 0, 5.1640e-03);
        e2.setElement(1, 1, 8.2264e-02);
        e2.setElement(1, 2, -2.3662e-02);

        e2.setElement(2, 0, 3.2495e-02);
        e2.setElement(2, 1, -3.3552e-02);
        e2.setElement(2, 2, -4.6965e-02);

        e2.setElement(3, 0, 4.2464e-02);
        e2.setElement(3, 1, -5.4120e-02);
        e2.setElement(3, 2, -5.9472e-02);

        assertTrue(e2.equals(instance.pseudoInverse(0.5), 0.0001));
    }

}
