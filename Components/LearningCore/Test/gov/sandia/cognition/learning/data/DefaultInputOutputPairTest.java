/*
 * File:                DefaultInputOutputPairTest.java
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
import java.util.LinkedList;
import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author jdbasil
 */
public class DefaultInputOutputPairTest extends TestCase
{
    
    public DefaultInputOutputPairTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(DefaultInputOutputPairTest.class);
        
        return suite;
    }
    
    public void testConstructors()
    {
        InputOutputPair<Object, Object> instance =
            new DefaultInputOutputPair<Object, Object>();
        assertNull(instance.getInput());
        assertNull(instance.getOutput());
        
        Object input = new Object();
        Object output = new Object();
        instance = new DefaultInputOutputPair<Object, Object>(input, output); 
        assertSame(input, instance.getInput());
        assertSame(output, instance.getOutput());

        InputOutputPair<Object, Object> p2 =
            new DefaultInputOutputPair<Object, Object>( instance );
        assertNotSame( p2, instance );
        assertSame( p2.getInput(), instance.getInput() );
        assertSame( p2.getOutput(), instance.getOutput() );

    }

    public void testCreate()
    {
        System.out.println( "Create" );

        Double input = 1.0;
        Integer output = 2;
        InputOutputPair<Double, Integer> instance = DefaultInputOutputPair.create(input,output);
        assertSame( input, instance.getInput() );
        assertSame( output, instance.getOutput() );

    }

    /**
     * Test of getInput method, of class gov.sandia.isrc.learning.util.data.InputOutputPair.
     */
    public void testGetInput()
    {
        Object input = new Object();
        Object output = new Object();
        InputOutputPair<Object, Object> instance = 
            new DefaultInputOutputPair<Object, Object>(input, output); 
        
        assertSame(input, instance.getInput());
    }

    /**
     * Test of getOutput method, of class gov.sandia.isrc.learning.util.data.InputOutputPair.
     */
    public void testGetOutput()
    {
        Object input = new Object();
        Object output = new Object();
        InputOutputPair<Object, Object> instance = 
            new DefaultInputOutputPair<Object, Object>(input, output); 
        
        assertSame(output, instance.getOutput());
    }

    /**
     * Test of setInput method, of class gov.sandia.isrc.learning.util.data.InputOutputPair.
     */
    public void testSetInput()
    {
        Object input = new Object();
        Object output = new Object();
        DefaultInputOutputPair<Object, Object> instance =
            new DefaultInputOutputPair<Object, Object>(input, output); 
        
        assertSame(input, instance.getInput());
        
        Object newInput = new Object();
        instance.setInput(newInput);
        assertSame(newInput, instance.getInput());
        
        instance.setInput(null);
        assertNull(instance.getInput());
    }

    /**
     * Test of setOutput method, of class gov.sandia.isrc.learning.util.data.InputOutputPair.
     */
    public void testSetOutput()
    {
        Object input = new Object();
        Object output = new Object();
        DefaultInputOutputPair<Object, Object> instance =
            new DefaultInputOutputPair<Object, Object>(input, output); 
        
        assertSame(output, instance.getOutput());
        
        Object newOutput = new Object();
        instance.setOutput(newOutput);
        assertSame(newOutput, instance.getOutput());
        
        instance.setOutput(null);
        assertNull(instance.getOutput());
    }

    /**
     * Test of mergeCollections method, of class InputOutputPair.
     */
    public void testMergeCollections()
    {
        System.out.println( "mergeCollections" );
        int num = 100;
        Random random = new Random( 1 );
        ArrayList<Double> inputs = new ArrayList<Double>(num);
        ArrayList<Integer> outputs = new ArrayList<Integer>(num);
        for( int n = 0; n < num; n++ )
        {
            inputs.add( random.nextDouble() );
            outputs.add( random.nextInt() );
        }
        
        ArrayList<DefaultInputOutputPair<Double,Integer>> result =
            DefaultInputOutputPair.mergeCollections( inputs, outputs );
        assertEquals( num, result.size() );
        
        for( int n = 0; n < num; n++ )
        {
            assertSame( result.get(n).getInput(), inputs.get(n) );
            assertSame( result.get(n).getOutput(), outputs.get(n) );
        }

        try
        {
            DefaultInputOutputPair.mergeCollections( inputs, new LinkedList<Integer>() );
            fail( "Collections must be same size!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of labelCollection method, of class DefaultInputOutputPair.
     */
    public void testLabelCollection()
    {
        int num = 100;
        Random random = new Random(211);
        ArrayList<Double> inputs = new ArrayList<Double>(num);
        for (int n = 0; n < num; n++)
        {
            inputs.add(random.nextDouble());
        }

        Integer output = random.nextInt();
        ArrayList<DefaultInputOutputPair<Double, Integer>> result =
            DefaultInputOutputPair.labelCollection(inputs, output);
        assertEquals(num, result.size());

        for (int n = 0; n < num; n++)
        {
            assertSame(inputs.get(n), result.get(n).getInput());
            assertSame(output, result.get(n).getOutput());
        }

        assertTrue(DefaultInputOutputPair.labelCollection(new LinkedList<Boolean>(), null).isEmpty());
    }

    /**
     * Test of toString method, of class InputOutputPair.
     */
    public void testToString()
    {
        System.out.println( "toString" );
        InputOutputPair<?,?> instance = new DefaultInputOutputPair<String,String>();
        System.out.println( "Pair: " + instance );
    }

    /**
     * Test of getFirst method, of class InputOutputPair.
     */
    public void testGetFirst()
    {
        System.out.println( "getFirst" );
        InputOutputPair<Double,Double> instance = 
            new DefaultInputOutputPair<Double,Double>(1.0,2.0);
        assertSame( instance.getInput(), instance.getFirst() );

    }

    /**
     * Test of getSecond method, of class InputOutputPair.
     */
    public void testGetSecond()
    {
        System.out.println( "getSecond" );
        InputOutputPair<Double,Double> instance = 
            new DefaultInputOutputPair<Double,Double>(1.0,2.0);
        assertSame( instance.getOutput(), instance.getSecond() );

    }
}
