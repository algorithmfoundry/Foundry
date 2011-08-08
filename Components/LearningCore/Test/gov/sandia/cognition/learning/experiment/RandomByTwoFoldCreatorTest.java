/*
 * File:                RandomByTwoFoldCreatorTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright January 20, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 */

package gov.sandia.cognition.learning.experiment;

import gov.sandia.cognition.learning.data.PartitionedDataset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for class RandomByTwoFoldCreator.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class RandomByTwoFoldCreatorTest
    extends TestCase
{
    protected Random random;

    public RandomByTwoFoldCreatorTest(
        final String testName)
    {
        super(testName);

        this.random = new Random(1);
    }

    /**
     * Test of constants of class RandomByTwoFoldCreator.
     */
    public void testConstants()
    {
        assertEquals(5, RandomByTwoFoldCreator.DEFAULT_NUM_SPLITS);
    }

    /**
     * Test of constructors of class RandomByTwoFoldCreator.
     */
    public void testConstructors()
    {
        int numSplits = RandomByTwoFoldCreator.DEFAULT_NUM_SPLITS;
        RandomByTwoFoldCreator<Double> instance = new RandomByTwoFoldCreator<Double>();
        assertEquals(numSplits, instance.getNumSplits());
        assertNotNull(instance.getRandom());

        numSplits = numSplits * 10;
        instance = new RandomByTwoFoldCreator<Double>(numSplits, this.random);
        assertEquals(numSplits, instance.getNumSplits());
        assertSame(this.random, instance.getRandom());
    }

    /**
     * Test of createFolds method, of class RandomByTwoFoldCreator.
     */
    public void testCreateFolds()
    {
        int numSplits = 7;
        RandomByTwoFoldCreator<Double> instance = new RandomByTwoFoldCreator<Double>(
            numSplits, this.random);

        Collection<Double> data = new ArrayList<Double>();
        List<PartitionedDataset<Double>> folds = null;
        int count = 25;
        for (int i = 0; i < count; i++)
        {
            data.add(random.nextDouble());
            
            if (i > 1)
            {
                folds = instance.createFolds(data);
                checkFolds(data, numSplits, folds);
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

        data.add(this.random.nextDouble());
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

    /**
     * Checks that the folds are correct.
     *
     * @param   data
     *      The data.
     * @param   numRequestedSplits
     *      The requested number of splits.
     * @param   folds
     *      The folds that were created.
     */
    public static void checkFolds(
        final Collection<Double> data,
        final int numRequestedSplits,
        final List<PartitionedDataset<Double>> folds)
    {
        int dataSize = data.size();
        int halfDataSize = dataSize / 2;

        int numFolds = Math.min(2 * dataSize, 2 * numRequestedSplits);
        assertEquals(numFolds, folds.size());

        for (PartitionedDataset<Double> fold : folds)
        {
            int trainSize = fold.getTrainingSet().size();
            int testSize = fold.getTestingSet().size();
            assertTrue(trainSize > 0);
            assertTrue(testSize > 0);
            assertEquals(dataSize, trainSize + testSize);
        }

        for (PartitionedDataset<Double> fold : folds)
        {
            int trainCount = 0;
            int testCount = 0;

            for (Double value : data)
            {
                boolean inTrain = fold.getTrainingSet().contains(value);
                boolean inTest = fold.getTestingSet().contains(value);

                assertTrue(inTrain ^ inTest);

                if (inTrain)
                {
                    trainCount++;
                }
                if (inTest)
                {
                    testCount++;
                }
            }
            
            assertEquals(dataSize, trainCount + testCount);
            assertTrue(
                  trainCount >= halfDataSize
               && trainCount <= halfDataSize + 1);
            assertTrue(
                  testCount >= halfDataSize
               && testCount <= halfDataSize + 1);
        }
    }

    /**
     * Test of getNumSplits method, of class RandomByTwoFoldCreator.
     */
    public void testGetNumSplits()
    {
        this.testSetNumSplits();
    }

    /**
     * Test of setNumSplits method, of class RandomByTwoFoldCreator.
     */
    public void testSetNumSplits()
    {
        int numSplits = RandomByTwoFoldCreator.DEFAULT_NUM_SPLITS;
        RandomByTwoFoldCreator<Double> instance = new RandomByTwoFoldCreator<Double>();
        assertEquals(numSplits, instance.getNumSplits());

        numSplits = numSplits * 10;
        instance.setNumSplits(numSplits);
        assertEquals(numSplits, instance.getNumSplits());

        numSplits = 1;
        instance.setNumSplits(numSplits);
        assertEquals(numSplits, instance.getNumSplits());

        numSplits = this.random.nextInt(147);
        instance.setNumSplits(numSplits);
        assertEquals(numSplits, instance.getNumSplits());
        
        boolean exceptionThrown = false;
        try
        {
            instance.setNumSplits(0);
        }
        catch ( IllegalArgumentException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(numSplits, instance.getNumSplits());

        exceptionThrown = false;
        try
        {
            instance.setNumSplits(-1);
        }
        catch ( IllegalArgumentException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(numSplits, instance.getNumSplits());
    }

}
