/*
 * File:                DefaultPartitionedDatasetTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright October 5, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.data;

import java.util.Collection;
import java.util.LinkedList;
import junit.framework.*;

/**
 *
 * @author jdbasil
 */
public class DefaultPartitionedDatasetTest extends TestCase
{
    
    public DefaultPartitionedDatasetTest(String testName)
    {
        super(testName);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(DefaultPartitionedDatasetTest.class);
        
        return suite;
    }

    /**
     * Test of getTrainingSet method, of class gov.sandia.isrc.learning.util.data.PartitionedDataset.
     */
    public void testGetTrainingSet()
    {
        System.out.println("getTrainingSet");
        
        Collection<Object> trainingSet = new LinkedList<Object>();
        
        PartitionedDataset<Object> instance = 
            new DefaultPartitionedDataset<Object>(trainingSet, null);
        
        assertSame(trainingSet, instance.getTrainingSet());
    }

    /**
     * Test of getTestingSet method, of class gov.sandia.isrc.learning.util.data.PartitionedDataset.
     */
    public void testGetTestingSet()
    {
        System.out.println("getTestingSet");        
        
        Collection<Object> testingSet = new LinkedList<Object>();
        
        testingSet.add(new Object());
        PartitionedDataset<Object> instance = 
            new DefaultPartitionedDataset<Object>(null, testingSet);
        
        assertSame(testingSet, instance.getTestingSet());
    }

    /**
     * Test of setTrainingSet method, of class gov.sandia.isrc.learning.util.data.PartitionedDataset.
     */
    public void testSetTrainingSet()
    {
        System.out.println("setTrainingSet");
        
        Collection<Object> trainingSet = new LinkedList<Object>();
        
        DefaultPartitionedDataset<Object> instance =
            new DefaultPartitionedDataset<Object>(trainingSet, null);
        
        assertNotNull( trainingSet );
        
        instance.setTrainingSet( null );
        assertNull( instance.getTrainingSet() );
        
        instance.setTrainingSet( trainingSet );
        assertSame(trainingSet, instance.getTrainingSet());
    }

    /**
     * Test of setTestingSet method, of class gov.sandia.isrc.learning.util.data.PartitionedDataset.
     */
    public void testSetTestingSet()
    {
        System.out.println("setTestingSet");
        
        Collection<Object> testingSet = new LinkedList<Object>();
        
        testingSet.add(new Object());
        DefaultPartitionedDataset<Object> instance =
            new DefaultPartitionedDataset<Object>(null, testingSet);

        assertNotNull( instance.getTestingSet() );
        
        instance.setTestingSet( null );
        assertNull( instance.getTestingSet() );
        
        instance.setTestingSet( testingSet );
        assertSame( testingSet, instance.getTestingSet() );

    }
    
}
