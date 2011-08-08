/*
 * File:                LogLocalTermWeighterTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 29, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.vector.weighter.local;

import gov.sandia.cognition.math.matrix.SparseVectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class LogLocalTermWeighter.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class LogLocalTermWeighterTest
{
    /**
     * Creates a new test.
     */
    public LogLocalTermWeighterTest()
    {
    }

    /**
     * Test of constructors of class LogLocalTermWeighter.
     */
    @Test
    public void testConstructors()
    {
        VectorFactory<? extends Vector> vectorFactory =
            SparseVectorFactory.getDefault();
        LogLocalTermWeighter instance = new LogLocalTermWeighter();
        assertSame(vectorFactory, instance.getVectorFactory());

        vectorFactory = VectorFactory.getDefault();
        instance = new LogLocalTermWeighter(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
    }

    /**
     * Test of computeLocalWeights method, of class LogLocalTermWeighter.
     */
    @Test
    public void testComputeLocalWeights()
    {
        LogLocalTermWeighter instance = new LogLocalTermWeighter();

        Vector input = new Vector3(3.0, 0.0, 1.0);
        Vector inputClone = input.clone();
        Vector expected = new Vector3(Math.log(4.0), 0.0, Math.log(2.0));
        assertEquals(expected, instance.computeLocalWeights(
            input));
        assertEquals(inputClone, input);

        input = new Vector3();
        inputClone = input.clone();
        expected = new Vector3();
        assertEquals(expected, instance.computeLocalWeights(
            input));
        assertEquals(inputClone, input);

        // These are really just testing boundary cases.
        input = new Vector3(0.0, -1.0, 2.3);
        inputClone = input.clone();
        expected = new Vector3(0.0, 0.0, Math.log(3.3));
        assertEquals(expected, instance.computeLocalWeights(
            input));
        assertEquals(inputClone, input);
    }

}