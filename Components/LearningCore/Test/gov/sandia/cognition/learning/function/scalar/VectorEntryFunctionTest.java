/*
 * File:                VectorEntryFunctionTest.java
 * Authors:             Dan Morrow
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Oct 16, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import junit.framework.TestCase;

/**
 * Test code for the VectorEntryFunction test
 *
 * @author jdmorr
 */
public class VectorEntryFunctionTest
    extends TestCase
{
    /**
     * Constructor
     * @param testName Test name.
     */
    public VectorEntryFunctionTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of clone method, of class VectorEntryFunction.
     */
    public void testClone()
    {
        System.out.println("clone");
        VectorEntryFunction instance = new VectorEntryFunction();
        VectorEntryFunction expResult = instance;
        VectorEntryFunction result = instance.clone();
        assertNotSame( expResult, result );
        assertEquals( expResult.getIndex(), result.getIndex());
    }

    /**
     * Test of evaluate method, of class VectorEntryFunction.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        Vector input = VectorFactory.getDefault().
                createVector(5,0.0);
        VectorEntryFunction instance = new VectorEntryFunction();
        Double expResult = 3.5;
        input.setElement(0, expResult);

        Double result = instance.evaluate(input);
        assertEquals(expResult, result);
    }

    /**
     * Test of convertToVector method, of class VectorEntryFunction.
     */
    public void testConvertToVector()
    {
        System.out.println("convertToVector");
        VectorEntryFunction instance = new VectorEntryFunction();
        Vector expResult = VectorFactory.getDefault().createVector(
            1, 0);
        Vector result = instance.convertToVector();
        assertEquals(expResult, result);
    }

    /**
     * Test of convertFromVector method, of class VectorEntryFunction.
     */
    public void testConvertFromVector()
    {
        final int ix_value = 4;
        System.out.println("convertFromVector");
        Vector parameters = VectorFactory.getDefault().createVector(
            1, ix_value);
        VectorEntryFunction instance = new VectorEntryFunction();
        instance.convertFromVector(parameters);

        assertEquals( ix_value, instance.getIndex() );

        parameters = VectorFactory.getDefault().createVector(3, 0.0);

        try {
            instance.convertFromVector(parameters);
            fail("Did not catch parameter dimension exception");
        } catch ( RuntimeException e ) {
            System.out.println("Good: caught runtimeexception '" +
                    e.getMessage() +"'");
        }

    }

    /**
     * Test of getIndex method, of class VectorEntryFunction.
     */
    public void testGetIndex()
    {
        System.out.println("getIndex");
        VectorEntryFunction instance = new VectorEntryFunction();
        int expResult = 0;
        int result = instance.getIndex();
        assertEquals(expResult, result);
    }

    /**
     * Test of setIndex method, of class VectorEntryFunction.
     */
    public void testSetIndex()
    {
        System.out.println("setIndex");
        int index = 3;
        VectorEntryFunction instance = new VectorEntryFunction();
        instance.setIndex(index);

        assertEquals( index, instance.getIndex() );

    }

}
