/*
 * File:                CosineSimilarityFunctionTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 19, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.vector;

import gov.sandia.cognition.learning.function.distance.CosineDistanceMetric;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class CosineSimilarityFunction.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class CosineSimilarityFunctionTest
{
    /**
     * Creates a new test.
     */
    public CosineSimilarityFunctionTest()
    {
    }

    /**
     * Test of constructors of class CosineSimilarityFunction.
     */
    @Test
    public void testConstructors()
    {
        CosineSimilarityFunction instance = new CosineSimilarityFunction();
    }

    /**
     * Test of getInstance method, of class CosineSimilarityFunction.
     */
    @Test
    public void testGetInstance()
    {
        CosineSimilarityFunction result = CosineSimilarityFunction.getInstance();
        assertNotNull(result);
        assertSame(result, CosineSimilarityFunction.getInstance());
    }

    /**
     * Test of evaluate method, of class CosineSimilarityFunction.
     */
    @Test
    public void testEvaluate()
    {
        CosineSimilarityFunction instance = new CosineSimilarityFunction();
        double EPSILON = 0.00001;
        
        Vector x = new Vector3(0.5, 0.0, 0.0);
        Vector y = new Vector3(0.2, 0.5, 0.0);
        Vector z = new Vector3();
        double cosXY = x.cosine(y);
        assertEquals(1.0, instance.evaluate(x, x), EPSILON);
        assertEquals(cosXY, instance.evaluate(x, y), EPSILON);
        assertEquals(cosXY, instance.evaluate(y, x), EPSILON);
        assertEquals(1.0, instance.evaluate(y, y), EPSILON);
        assertEquals(0.0, instance.evaluate(x, z), EPSILON);
        assertEquals(0.0, instance.evaluate(z, x), EPSILON);
        assertEquals(0.0, instance.evaluate(y, z), EPSILON);
        assertEquals(0.0, instance.evaluate(z, y), EPSILON);
        assertEquals(0.0, instance.evaluate(z, z), EPSILON);
    }

    /**
     * Test of asDivergence method, of class CosineSimilarityFunction.
     */
    @Test
    public void testAsDivergence()
    {
        CosineSimilarityFunction instance = new CosineSimilarityFunction();
        CosineDistanceMetric result = instance.asDivergence();
        assertNotNull(result);
    }

}