/*
 * File:                KRandomFoldCreatorTest.java
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

import gov.sandia.cognition.learning.data.PartitionedDataset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes: CrossFoldCreator
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class CrossFoldCreatorTest
    extends TestCase
{

    Random random = new Random(211);

    public CrossFoldCreatorTest(
        String testName)
    {
        super(testName);
    }
    
    public void testConstants()
    {
        assertEquals(10, CrossFoldCreator.DEFAULT_NUM_FOLDS);
    }
    
    public void testConstructors()
    {
        CrossFoldCreator<Double> instance = new CrossFoldCreator<Double>();
        assertEquals(CrossFoldCreator.DEFAULT_NUM_FOLDS, instance.getNumFolds());
        assertNotNull(instance.getRandom());
        
        int numFolds = CrossFoldCreator.DEFAULT_NUM_FOLDS * (1 + random.nextInt(10));
        instance = new CrossFoldCreator<Double>(numFolds);
        assertEquals(numFolds, instance.getNumFolds());
        assertNotNull(instance.getRandom());

        instance = new CrossFoldCreator<Double>(numFolds, random);
        assertEquals(numFolds, instance.getNumFolds());
        assertSame(random, instance.getRandom());
    }

    /**
     * Test of createFolds method, of class gov.sandia.cognition.learning.experiment.KRandomFoldCreator.
     */
    public void testCreateFolds()
    {
        Collection<Double> data = new ArrayList<Double>();
        
        int numFolds = 7;
        CrossFoldCreator<Double> instance = 
            new CrossFoldCreator<Double>(numFolds, this.random);
        List<PartitionedDataset<Double>> folds = null;
            
        int count = 25;
        for (int i = 0; i < count; i++)
        {
            data.add(this.random.nextDouble());
            
            if ( i > 1 )
            {
                folds = instance.createFolds(data);
                checkFolds(data, numFolds, folds);
            }
        }
        
        data.clear();
        
        boolean exceptionThrown = false;
        try
        {
            folds = instance.createFolds(data);
        }
        catch ( IllegalArgumentException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
        data.add(random.nextDouble());
        exceptionThrown = false;
        try
        {
            folds = instance.createFolds(data);
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
    
    public void checkFolds(
        Collection<Double> data,
        int numRequestedFolds,
        List<PartitionedDataset<Double>> folds)
    {
        int count = data.size();
        
        int numFolds = Math.min(count, numRequestedFolds);
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
                
                if ( inTrain )
                {
                    trainCount++;
                }
                if ( inTest )
                {
                    testCount++;
                }
            }
            
            assertEquals(numFolds - 1, trainCount);
            assertEquals(1, testCount);
        }
    }

    /**
     * Test of getNumFolds method, of class gov.sandia.cognition.learning.experiment.KRandomFoldCreator.
     */
    public void testGetNumFolds()
    {
        this.testSetNumFolds();
    }

    /**
     * Test of setNumFolds method, of class gov.sandia.cognition.learning.experiment.KRandomFoldCreator.
     */
    public void testSetNumFolds()
    {
        CrossFoldCreator<Double> instance = new CrossFoldCreator<Double>();
        assertEquals(CrossFoldCreator.DEFAULT_NUM_FOLDS, instance.getNumFolds());
        
        int numFolds = CrossFoldCreator.DEFAULT_NUM_FOLDS * 10;
        instance.setNumFolds(numFolds);
        assertEquals(numFolds, instance.getNumFolds());
        
        numFolds = numFolds + 1;
        instance.setNumFolds(numFolds);
        assertEquals(numFolds, instance.getNumFolds());
        
        boolean exceptionThrown = false;
        try
        {
            instance.setNumFolds(1);
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
}
