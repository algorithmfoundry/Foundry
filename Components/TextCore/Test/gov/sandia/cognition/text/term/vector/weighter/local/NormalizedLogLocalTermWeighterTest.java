/*
 * File:                NormalizedLogLocalTermWeighterTest.java
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
 * Unit tests for class NormalizedLogLocalTermWeighter.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class NormalizedLogLocalTermWeighterTest
{
    /**
     * Creates a new test.
     */
    public NormalizedLogLocalTermWeighterTest()
    {
    }

    /**
     * Test of constructors of class NormalizedLogLocalTermWeighter.
     */
    @Test
    public void testConstructors()
    {
        VectorFactory<? extends Vector> vectorFactory =
            SparseVectorFactory.getDefault();
        NormalizedLogLocalTermWeighter instance = new NormalizedLogLocalTermWeighter();
        assertSame(vectorFactory, instance.getVectorFactory());

        vectorFactory = VectorFactory.getDefault();
        instance = new NormalizedLogLocalTermWeighter(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
    }

    /**
     * Test of computeLocalWeights method, of class NormalizedLogLocalTermWeighter.
     */
    @Test
    public void testComputeLocalWeights()
    {
        final double EPSILON = 1.0e-5;
        NormalizedLogLocalTermWeighter instance = new NormalizedLogLocalTermWeighter();
        Vector input = new Vector3(3.0, 0.0, 1.0);
        Vector inputClone = input.clone();
        double normalizer = Math.log(1.0 + 4.0 / 3.0);
        Vector expected = new Vector3(Math.log(4.0) / normalizer, 0.0, Math.log(2.0) / normalizer);
        assertTrue(expected.equals(instance.computeLocalWeights(
            input), EPSILON));
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
        normalizer = Math.log(1.0 + 3.3 / 3.0);
        expected = new Vector3(0.0, 0.0, Math.log(3.3) / normalizer);
        assertTrue(expected.equals(instance.computeLocalWeights(
            input), EPSILON));
        assertEquals(inputClone, input);
    }

}