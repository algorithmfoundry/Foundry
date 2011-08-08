/*
 * File:                RandomDataPartitionerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 26, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class RandomDataPartitionerTest
    extends TestCase
{
    public RandomDataPartitionerTest(
        String testName)
    {
        super(testName);
    }

    public void testConstants()
    {
        assertEquals(0.5, RandomDataPartitioner.DEFAULT_TRAINING_PERCENT);
    }
    
    public void testConstructors()
    {
        RandomDataPartitioner<Double> instance = 
            new RandomDataPartitioner<Double>();
        assertEquals(RandomDataPartitioner.DEFAULT_TRAINING_PERCENT, 
            instance.getTrainingPercent());
        assertNotNull(instance.getRandom());
        
        double trainingPercent = Math.random();
        Random random = new Random();
        instance = new RandomDataPartitioner<Double>(trainingPercent, random);
        assertEquals(trainingPercent, instance.getTrainingPercent());
        assertSame(random, instance.getRandom());
    }
    
    /**
     * Test of partition method, of class gov.sandia.cognition.learning.util.data.RandomDataPartitioner.
     */
    public void testCreatePartition()
    {
        Collection<Double> data = new ArrayList<Double>();
        
        double trainingPercent = 0.66;
        RandomDataPartitioner<Double> instance = 
            new RandomDataPartitioner<Double>(trainingPercent, new Random());
            
        int count = 25;
        for (int i = 0; i < count; i++)
        {
            data.add(Math.random());
            
            if ( i > 1 )
            {
                checkPartition(data, instance.createPartition(data));
            }
        }
        
        data.clear();
        
        boolean exceptionThrown = false;
        try
        {
            instance.createPartition(data);
        }
        catch ( IllegalArgumentException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assert(exceptionThrown);
        }
        
        data.add(Math.random());
        exceptionThrown = false;
        try
        {
            instance.createPartition(data);
        }
        catch ( IllegalArgumentException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assert(exceptionThrown);
        }
    }
    
    public void checkPartition(
        Collection<Double> data,
        PartitionedDataset<Double> partition)
    {
        int count = data.size();
        
        int trainSize = partition.getTrainingSet().size();
        int testSize = partition.getTestingSet().size();
        assertTrue(trainSize > 0);
        assertTrue(testSize > 0);
        assertEquals(count, trainSize + testSize);
        
        for ( Double value : data )
        {
            boolean inTrain = partition.getTrainingSet().contains(value);
            boolean inTest  = partition.getTestingSet().contains(value);

            assertTrue(inTrain ^ inTest);
        }
    }

    /**
     * Test of getTrainingPercent method, of class gov.sandia.cognition.learning.util.data.RandomDataPartitioner.
     */
    public void testGetTrainingPercent()
    {
        this.testSetTrainingPercent();
    }

    /**
     * Test of setTrainingPercent method, of class gov.sandia.cognition.learning.util.data.RandomDataPartitioner.
     */
    public void testSetTrainingPercent()
    {
        RandomDataPartitioner<Double> instance = 
            new RandomDataPartitioner<Double>();
        assertEquals(RandomDataPartitioner.DEFAULT_TRAINING_PERCENT, 
            instance.getTrainingPercent());
        
        double trainingPercent = Math.random();
        instance.setTrainingPercent(trainingPercent);
        assertEquals(trainingPercent, instance.getTrainingPercent());
        
        trainingPercent = Math.random();
        instance.setTrainingPercent(trainingPercent);
        assertEquals(trainingPercent, instance.getTrainingPercent());
        
        boolean exceptionThrown = false;
        try
        {
            instance.setTrainingPercent(0.0);
        }
        catch ( IllegalArgumentException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assert(exceptionThrown);
        }
        
        exceptionThrown = false;
        try
        {
            instance.setTrainingPercent(1.0);
        }
        catch ( IllegalArgumentException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assert(exceptionThrown);
        }
    }
}
