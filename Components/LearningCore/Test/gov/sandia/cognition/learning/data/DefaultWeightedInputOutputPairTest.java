/*
 * File:                DefaultWeightedInputOutputPairTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 5, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;


/**
 *
 * @author jdbasil
 */
public class DefaultWeightedInputOutputPairTest extends TestCase
{
    
    public DefaultWeightedInputOutputPairTest(String testName)
    {
        super(testName);
    }

    public void testConstants()
    {
        assertEquals(1.0, DefaultWeightedInputOutputPair.DEFAULT_WEIGHT);
    }
    
    public void testConstructors()
    {
        WeightedInputOutputPair<Object, Object> instance = 
            new DefaultWeightedInputOutputPair<Object, Object>();
        assertNull(instance.getInput());
        assertNull(instance.getOutput());
        assertEquals(DefaultWeightedInputOutputPair.DEFAULT_WEIGHT, instance.getWeight());
        
        Object input = new Object();
        Object output = new Object();
        double weight = Math.random();
        instance = new DefaultWeightedInputOutputPair<Object, Object>(input, output, weight);
        assertSame(input, instance.getInput());
        assertSame(output, instance.getOutput());
        assertEquals(weight, instance.getWeight());
        
        weight = Math.random();
        instance = new DefaultWeightedInputOutputPair<Object, Object>(instance, weight);
        assertSame(input, instance.getInput());
        assertSame(output, instance.getOutput());
        assertEquals(weight, instance.getWeight());
    }

    public void testCreate()
    {
        System.out.println( "Create" );

        Double input = 1.0;
        Integer output = 2;
        double weight = 3.1415;
        WeightedInputOutputPair<Double, Integer> instance = DefaultWeightedInputOutputPair.create(input,output,weight);
        assertSame( input, instance.getInput() );
        assertSame( output, instance.getOutput() );
        assertEquals( weight, instance.getWeight() );
    }

    /**
     * Test of getWeight method, of class gov.sandia.isrc.learning.util.data.WeightedInputOutputPair.
     */
    public void testGetWeight()
    {
        double weight = Math.random();
        WeightedInputOutputPair<Object, Object> instance = 
            new DefaultWeightedInputOutputPair<Object, Object>(null, null, weight);
        
        assertEquals(weight, instance.getWeight());
    }

    /**
     * Test of setWeight method, of class gov.sandia.isrc.learning.util.data.WeightedInputOutputPair.
     */
    public void testSetWeight()
    {
        double weight = Math.random();
        DefaultWeightedInputOutputPair<Object, Object> instance =
            new DefaultWeightedInputOutputPair<Object, Object>(null, null, weight);
        
        assertEquals(weight, instance.getWeight());
        
        double newWeight = Math.random();
        
        instance.setWeight(newWeight);
        assertEquals(newWeight, instance.getWeight());
    }

    public void testMergeCollections()
    {
        System.out.println( "mergeCollections" );

        List<Double> inputs = Arrays.asList( 1.0, 2.0, 3.0 );
        List<Integer> outputs = Arrays.asList( 0, 1, 2 );
        List<Double> weights = Arrays.asList( 10.0, 11.0, 12.0 );

        ArrayList<DefaultWeightedInputOutputPair<Double,Integer>> pairs =
            DefaultWeightedInputOutputPair.mergeCollections(inputs, outputs,weights);
        assertEquals( inputs.size(), pairs.size() );
        for( int i = 0; i < pairs.size(); i++ )
        {
            assertSame( inputs.get(i), pairs.get(i).getInput() );
            assertSame( outputs.get(i), pairs.get(i).getOutput() );
            assertEquals( weights.get(i), pairs.get(i).getWeight() );
        }

        weights = new ArrayList<Double>(1);
        try
        {
            DefaultWeightedInputOutputPair.mergeCollections(inputs, outputs,weights);
            fail( "Not the same size!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    
}
