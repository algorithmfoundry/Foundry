/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.sandia.cognition.learning.function.kernel;

import junit.framework.TestCase;

/**
 *
 * @author jdmorr
 */
public class GeneralizedScalarRadialBasisKernelTest extends TestCase {

    private final double TOLERANCE=1.e-4;

    public GeneralizedScalarRadialBasisKernelTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of evaluate method, of class GeneralizedScalarRadialBasisKernel.
     * note that default constant is 1.0
     * default exponent is 1.0
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        Double x = 1.0;
        Double y = 0.0;
        GeneralizedScalarRadialBasisKernel instance =
            new GeneralizedScalarRadialBasisKernel();
        double expResult = Math.exp(
                -GeneralizedScalarRadialBasisKernel.DEFAULT_CONSTANT *
                Math.pow((x-y),GeneralizedScalarRadialBasisKernel.DEFAULT_EXPONENT) ) ;
        double result = instance.evaluate(x, y);
        assertEquals(expResult, result, TOLERANCE);
    }

    /**
     * Test of getExponent method, of class GeneralizedScalarRadialBasisKernel.
     */
    public void testGetExponent()
    {
        System.out.println("getExponent");
        GeneralizedScalarRadialBasisKernel instance =
            new GeneralizedScalarRadialBasisKernel();
        double expResult = GeneralizedScalarRadialBasisKernel.DEFAULT_EXPONENT;
        double result = instance.getExponent();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of setExponent method, of class GeneralizedScalarRadialBasisKernel.
     */
    public void testSetExponent()
    {
        System.out.println("setExponent");
        double exponent = 3.0;
        GeneralizedScalarRadialBasisKernel instance =
            new GeneralizedScalarRadialBasisKernel();
        instance.setExponent(exponent);

        assertEquals(instance.getExponent(), exponent, TOLERANCE);
    }

    /**
     * Test of getConstant method, of class GeneralizedScalarRadialBasisKernel.
     */
    public void testGetConstant()
    {
        System.out.println("getConstant");
        GeneralizedScalarRadialBasisKernel instance =
            new GeneralizedScalarRadialBasisKernel();
        double expResult = GeneralizedScalarRadialBasisKernel.DEFAULT_CONSTANT;
        double result = instance.getConstant();
        assertEquals(expResult, result, TOLERANCE);
    }

    /**
     * Test of setConstant method, of class GeneralizedScalarRadialBasisKernel.
     */
    public void testSetConstant()
    {
        System.out.println("setConstant");
        double constant = 2.5;
        GeneralizedScalarRadialBasisKernel instance =
            new GeneralizedScalarRadialBasisKernel();
        instance.setConstant(constant);

        assertEquals( instance.getConstant(), constant, TOLERANCE );
    }

}
