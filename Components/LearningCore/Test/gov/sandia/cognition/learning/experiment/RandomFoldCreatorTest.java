/*
 * File:                RandomFoldCreatorTest.java
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

package gov.sandia.cognition.learning.experiment;

import gov.sandia.cognition.learning.data.RandomDataPartitioner;
import gov.sandia.cognition.learning.data.PartitionedDataset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class RandomFoldCreatorTest
    extends TestCase
{

    private Random RANDOM = new Random(2347);

    public RandomFoldCreatorTest(
        String testName)
    {
        super(testName);
    }
    
    
    public void testConstants()
    {
        assertEquals(10, RandomFoldCreator.DEFAULT_NUM_FOLDS);
    }
    
    public void testConstructors()
    {
        RandomFoldCreator<Double> instance = new RandomFoldCreator<Double>();
        assertEquals(RandomFoldCreator.DEFAULT_NUM_FOLDS, instance.getNumFolds());
        assertNull(instance.getPartitioner());
        
        int numFolds = RandomFoldCreator.DEFAULT_NUM_FOLDS * 10;
        RandomDataPartitioner<Double> partitioner = 
            new RandomDataPartitioner<Double>();
        instance = new RandomFoldCreator<Double>(numFolds, partitioner);
        assertEquals(numFolds, instance.getNumFolds());
        assertSame(partitioner, instance.getPartitioner());
    }

    /**
     * Test of createFolds method, of class gov.sandia.cognition.learning.experiment.RandomFoldCreator.
     */
    public void testCreateFolds()
    {
        Collection<Double> data = new ArrayList<Double>();
        
        int numFolds = 7;
        RandomDataPartitioner<Double> partitioner = 
            new RandomDataPartitioner<Double>();
        RandomFoldCreator<Double> instance = 
            new RandomFoldCreator<Double>(numFolds, partitioner);
        List<PartitionedDataset<Double>> folds = null;
            
        int count = 25;
        for (int i = 0; i < count; i++)
        {
            data.add(RANDOM.nextDouble());
            
            if ( i > 1 )
            {
                folds = instance.createFolds(data);
                checkFolds(data, numFolds, folds);
            }
        }
    }
    
    public void checkFolds(
        Collection<Double> data,
        int numFolds,
        List<PartitionedDataset<Double>> folds)
    {
        int count = data.size();
        
        assertEquals(numFolds, folds.size());
        
        for ( PartitionedDataset<Double> fold : folds )
        {
            int trainSize = fold.getTrainingSet().size();
            int testSize = fold.getTestingSet().size();
            assertTrue(trainSize > 0);
            assertTrue(testSize > 0);
            assertEquals(count, trainSize + testSize);
        }
        
        for ( Double value : data )
        {
            int trainCount = 0;
            int testCount = 0;
            
            for ( PartitionedDataset<Double> fold : folds )
            {
                boolean inTrain = fold.getTrainingSet().contains(value);
                boolean inTest  = fold.getTestingSet().contains(value);
                
                assertTrue(inTrain ^ inTest);
                
                if ( inTrain ) trainCount++;
                if ( inTest ) testCount++;
            }
            
            assertEquals(numFolds, trainCount + testCount);
        }
    }

    /**
     * Test of getNumFolds method, of class gov.sandia.cognition.learning.experiment.RandomFoldCreator.
     */
    public void testGetNumFolds()
    {
        this.testSetNumFolds();
    }

    /**
     * Test of setNumFolds method, of class gov.sandia.cognition.learning.experiment.RandomFoldCreator.
     */
    public void testSetNumFolds()
    {
        RandomFoldCreator<Double> instance = new RandomFoldCreator<Double>();
        
        int numFolds = 100;
        instance.setNumFolds(numFolds);
        assertEquals(numFolds, instance.getNumFolds());
        
        numFolds = numFolds + 1;
        instance.setNumFolds(numFolds);
        assertEquals(numFolds, instance.getNumFolds());
        
        boolean exceptionThrown = false;
        try
        {
            instance.setNumFolds(0);
        }
        catch ( IllegalArgumentException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
        exceptionThrown = false;
        try
        {
            instance.setNumFolds(-1);
        }
        catch ( IllegalArgumentException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

    /**
     * Test of getPartitioner method, of class gov.sandia.cognition.learning.experiment.RandomFoldCreator.
     */
    public void testGetPartitioner()
    {
        this.testSetPartitioner();
    }

    /**
     * Test of setPartitioner method, of class gov.sandia.cognition.learning.experiment.RandomFoldCreator.
     */
    public void testSetPartitioner()
    {
        RandomFoldCreator<Double> instance = new RandomFoldCreator<Double>();
        assertNull(instance.getPartitioner());
        
        RandomDataPartitioner<Double> partitioner = 
            new RandomDataPartitioner<Double>();
        instance.setPartitioner(partitioner);
        assertSame(partitioner, instance.getPartitioner());
        
        instance.setPartitioner(null);
        assertNull(instance.getPartitioner());
    }
}
