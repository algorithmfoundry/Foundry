/*
 * File:                LeaveOneOutFoldCreatorTest.java
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
 * This class implements JUnit tests for the following classes:
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class LeaveOneOutFoldCreatorTest
    extends TestCase
{

    Random random = new Random(1);

    public LeaveOneOutFoldCreatorTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of createFolds method, of class gov.sandia.cognition.learning.experiment.LeaveOneOutFoldCreator.
     */
    public void testCreateFolds()
    {
        Collection<Double> data = new ArrayList<Double>();
        
        LeaveOneOutFoldCreator<Double> instance = 
            new LeaveOneOutFoldCreator<Double>();
        List<PartitionedDataset<Double>> folds = null;
            
        int count = 25;
        for (int i = 0; i < count; i++)
        {
            data.add(this.random.nextDouble());
            
            if ( i > 1 )
            {
                folds = instance.createFolds(data);
                checkFolds(data, folds);
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
            assert(exceptionThrown);
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
            assert(exceptionThrown);
        }
        
    }
    
    public void checkFolds(
        Collection<Double> data,
        List<PartitionedDataset<Double>> folds)
    {
        int count = data.size();
        
        assertEquals(count, folds.size());
        
        for ( PartitionedDataset<Double> fold : folds )
        {
            assertEquals(count - 1, fold.getTrainingSet().size());
            assertEquals(1, fold.getTestingSet().size());
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
            
            assertEquals(count - 1, trainCount);
            assertEquals(1, testCount);
        }
    }
}
