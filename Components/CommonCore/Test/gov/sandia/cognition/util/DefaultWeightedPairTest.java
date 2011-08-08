/*
 * File:                DefaultWeightedPairTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 19, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */
package gov.sandia.cognition.util;

/**
 *
 * @author Kevin R. Dixon
 */
public class DefaultWeightedPairTest extends DefaultPairTest
{

    public DefaultWeightedPairTest(String testName)
    {
        super(testName);
    }

    /**
     * Test of getWeight method, of class gov.sandia.cognition.util.DefaultWeightedPair.
     */
    public void testGetWeight()
    {
        System.out.println("getWeight");

        DefaultWeightedPair<Double, Double> instance = new DefaultWeightedPair<Double, Double>();
        assertNull(instance.getFirst());
        assertNull(instance.getSecond());
        assertEquals(0.0, instance.getWeight());

        double weight = Math.random();
        instance = new DefaultWeightedPair<Double, Double>(Math.random(), Math.random(), weight);
        assertEquals(weight, instance.getWeight());

    }

    /**
     * Test of setWeight method, of class gov.sandia.cognition.util.DefaultWeightedPair.
     */
    public void testSetWeight()
    {
        System.out.println("setWeight");

        double weight = Math.random();
        DefaultWeightedPair<Double, Double> instance = new DefaultWeightedPair<Double, Double>(Math.random(), Math.random(), weight);
        assertEquals(weight, instance.getWeight());

        double w2 = weight + 1.0;
        instance.setWeight(w2);
        assertEquals(w2, instance.getWeight());

    }

}
