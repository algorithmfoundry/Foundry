/*
 * File:                VectorizableCrossoverFunctionTest.java
 * Authors:             Jonathan McClain
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 8, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.genetic.reproducer;

import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Random;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     VectorizableCrossoverFunction
 *
 * @author Jonathan McClain
 * @since 1.0
 */
public class VectorizableCrossoverFunctionTest
    extends TestCase
{
    
    /** The random number generator for the tests. */
    private Random random = new Random(1);
    
    /**
     * Creates a new instance of VectorizableCrossoverFunctionTest.
     *
     * @param testName The name of the test.
     */
    public VectorizableCrossoverFunctionTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of crossover method, of class 
     * gov.sandia.isrc.learning.reinforcement.VectorizableCrossoverFunction.
     */
    public void testCrossover() {
        System.out.println("crossover");
        
        Vector vector1 = VectorFactory.getDefault().createUniformRandom(10, -10, 10, random);
        Vector vectorizable1 = vector1.clone();
        Vector vector2 = VectorFactory.getDefault().createUniformRandom(10, -10, 10, random);
        Vector vectorizable2 = vector2.clone();
        double probabilityCrossover = 0.5;
        VectorizableCrossoverFunction vcf =
            new VectorizableCrossoverFunction( probabilityCrossover );
        Vectorizable result1 = vcf.crossover(vectorizable1, vectorizable2);
        this.verifyCrossover(vectorizable1, vectorizable2, result1);
        Vectorizable result2 = vcf.crossover(vectorizable1, vectorizable2);
        this.verifyCrossover(vectorizable1, vectorizable2, result2);
        assertTrue(
                "Crossover produced the same result twice",
                !result1.convertToVector().equals(result2.convertToVector()));
    }
    
    /**
     * Verifies that the child is the result of crossing over the parents
     * correctly. Throws AssertionFailsErrors if incorrect.
     *
     * @param parent1 The first parent.
     * @param parent2 The second parent.
     * @param child The child.
     */
    public void verifyCrossover(
            Vectorizable parent1, 
            Vectorizable parent2, 
            Vectorizable child)
    {
        Vector parentVector1 = parent1.convertToVector();
        Vector parentVector2 = parent2.convertToVector();
        Vector childVector = child.convertToVector();
        assertTrue(
                "Parent size does not match child size",
                parentVector1.getDimensionality() == 
                childVector.getDimensionality());
        assertTrue(
                "Parent size does not match child size",
                parentVector2.getDimensionality() == 
                childVector.getDimensionality());
        for(int i = 0; i < parentVector1.getDimensionality(); i++)
        {
            assertTrue("A value was in the child that was not in the parents",
                    (childVector.getElement(i) == parentVector1.getElement(i)) 
                    || 
                    (childVector.getElement(i) == parentVector2.getElement(i)));
        }
    }

    /**
     * Test of getProbabilityCrossover method, of class gov.sandia.isrc.learning.reinforcement.VectorizableCrossoverFunction.
     */
    public void testGetProbabilityCrossover()
    {
        System.out.println("getProbabilityCrossover");
        
        double probabilityCrossover = random.nextDouble();
        VectorizableCrossoverFunction vcf =
            new VectorizableCrossoverFunction( probabilityCrossover );

        assertEquals( probabilityCrossover, vcf.getProbabilityCrossover() );
        
    }

    /**
     * Test of setProbabilityCrossover method, of class gov.sandia.isrc.learning.reinforcement.VectorizableCrossoverFunction.
     */
    public void testSetProbabilityCrossover()
    {
        System.out.println("setProbabilityCrossover");
        
        double probabilityCrossover = random.nextDouble();
        VectorizableCrossoverFunction vcf =
            new VectorizableCrossoverFunction( probabilityCrossover );

        assertEquals( probabilityCrossover, vcf.getProbabilityCrossover() );
        
        probabilityCrossover = random.nextDouble();
        assertTrue( probabilityCrossover != vcf.getProbabilityCrossover() );
        
        vcf.setProbabilityCrossover( probabilityCrossover );
        assertEquals( probabilityCrossover, vcf.getProbabilityCrossover() );
        
    }

    /**
     * Test of getRandom method, of class gov.sandia.isrc.learning.genetic.reproducer.VectorizableCrossoverFunction.
     */
    public void testGetRandom()
    {
        System.out.println("getRandom");
        
        VectorizableCrossoverFunction instance = new VectorizableCrossoverFunction();
        assertNotNull( instance.getRandom() );
    }

    /**
     * Test of setRandom method, of class gov.sandia.isrc.learning.genetic.reproducer.VectorizableCrossoverFunction.
     */
    public void testSetRandom()
    {
        System.out.println("setRandom");
        
        Random r2 = new Random(2);
        VectorizableCrossoverFunction instance = new VectorizableCrossoverFunction();
        assertNotNull( instance.getRandom() );
        assertNotSame( r2, instance.getRandom() );
        instance.setRandom( r2 );
        assertSame( r2, instance.getRandom() );
    }
    
}
