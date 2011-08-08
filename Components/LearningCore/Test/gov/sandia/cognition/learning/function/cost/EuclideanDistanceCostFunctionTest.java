/*
 * File:                EuclideanDistanceCostFunctionTest.java
 * Authors:             Jonathan McClain
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 9, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.cost;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Random;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     EuclideanDistanceCostFunctionTest
 *
 * @author Jonathan McClain
 * @since 1.0
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2006-10-04",
    changesNeeded=true,
    comments={
        "Existing tests look fine however not all methods have tests.",
        "Test subs need to be implemented."
    },
    response=@CodeReviewResponse(
        respondent="Justin Basilico",
        date="2007-08-24",
        moreChangesNeeded=false,
        comments="Improved unit test coverage."
    )
)
public class EuclideanDistanceCostFunctionTest
    extends TestCase
{
    
    /** The random number generator for the tests. */
    protected Random random = new Random(1);
    
    /**
     * Creates a new instance of EuclideanDistanceCostFunctionTest.
     */
    public EuclideanDistanceCostFunctionTest(
        String testName)
{
        super(testName);
    }

    public void testtClone()
    {
        
        Vector goal = VectorFactory.getDefault().createUniformRandom(2, -1.0, 1.0, random);
        EuclideanDistanceCostFunction instance =
                new EuclideanDistanceCostFunction(goal);
        EuclideanDistanceCostFunction clone = instance.clone();
        assertEquals(goal, clone.getCostParameters());
        assertNotSame(goal, clone.getCostParameters());
    }

    /**
     * Test of evaluate method, of class 
     * gov.sandia.isrc.learning.vector.EuclideanDistanceCostFunction.
     */
    public void testEvaluate() 
    {
        System.out.println("evaluate");
        
        Vector goalVector = VectorFactory.getDefault().createVector(2);
        goalVector.setElement(0, 0.0);
        goalVector.setElement(1, 0.0);
        Vector goal = goalVector.clone();
        
        Vector targetVector = VectorFactory.getDefault().createVector(2);
        targetVector.setElement(0, 1.0);
        targetVector.setElement(1, 1.0);
        Vector target = targetVector.clone();
        
        EuclideanDistanceCostFunction costFunction =
                new EuclideanDistanceCostFunction(goal);
        
        double expected = Math.sqrt(2.0);
        double actual = costFunction.evaluate(target);
        
        assertEquals(
                "Euclidean distance between 1,1 and 0,0 was incorrect", 
                expected, 
                actual, 
                0.0);
    }

    /**
     * Test of setCostParameters method, of class gov.sandia.isrc.learning.util.function.cost.EuclideanDistanceCostFunction.
     */
    public void testSetCostParameters()
    {
        Vector costParameters = VectorFactory.getDefault().createVector(3);
        EuclideanDistanceCostFunction instance = 
            new EuclideanDistanceCostFunction(costParameters);
        
        assertSame(costParameters, instance.getCostParameters());
        
        Vector newCostParameters = VectorFactory.getDefault().createVector(3);
        instance.setCostParameters(newCostParameters);
        assertSame(newCostParameters, instance.getCostParameters());

    }

    /**
     * Test of getCostParameters method, of class gov.sandia.isrc.learning.util.function.cost.EuclideanDistanceCostFunction.
     */
    public void testGetCostParameters()
    {
        EuclideanDistanceCostFunction instance = 
            new EuclideanDistanceCostFunction();
        assertNull(instance.getCostParameters());
    }
}
