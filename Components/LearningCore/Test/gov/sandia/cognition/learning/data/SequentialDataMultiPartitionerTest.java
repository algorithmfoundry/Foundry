/*
 * File:                SequentialDataMultiPartitionerTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Sep 22, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.learning.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import junit.framework.TestCase;

/**
 * JUnit tests for class SequentialDataMultiPartitionerTest
 * @author Kevin R. Dixon
 */
public class SequentialDataMultiPartitionerTest
    extends TestCase
{

    /**
     * Entry point for JUnit tests for class SequentialDataMultiPartitionerTest
     * @param testName name of this test
     */
    public SequentialDataMultiPartitionerTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Random
     */
    public static final Random RANDOM = new Random( 1 );
    
    /**
     * Test of createPartition method, of class SequentialDataMultiPartitioner.
     */
    public void testCreatePartition1()
    {
        System.out.println( "createPartition1" );
        int numData = 100;
        ArrayList<Double> data = new ArrayList<Double>( numData );
        for( int i = 0; i < numData; i++ )
        {
            data.add( new Double( RANDOM.nextDouble() ) );
        }
        
        int numPartitions = 1;
        
        ArrayList<ArrayList<Double>> r1 =
            SequentialDataMultiPartitioner.create( data, numPartitions );
        assertEquals( 1, r1.size() );
        assertEquals( numData, r1.get(0).size() );
        for( int i = 0; i < numData; i++ )
        {
            assertEquals( data.get(i), r1.get(0).get(i) );
        }
        
        numPartitions = 3;
        ArrayList<ArrayList<Double>> r2 =
            SequentialDataMultiPartitioner.create( data, numPartitions );
        assertEquals( numPartitions, r2.size() );
        assertEquals( numData/numPartitions, r2.get(0).size() );
        assertEquals( numData/numPartitions, r2.get(1).size() );
        assertEquals( 34, r2.get(2).size() );
        Iterator<Double> id = data.iterator();
        int index = 0;
        for( ArrayList<Double> partition : r2 )
        {
            for( int p = 0; p < partition.size(); p++ )
            {
                assertEquals( id.next(), partition.get(p) );
                index++;
            }
        }
        
        
    }

}
