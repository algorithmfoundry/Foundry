/*
 * File:                DefaultClusterCreatorTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 22, 2006, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering.cluster;

import java.util.Collection;
import java.util.LinkedList;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     DefaultClusterCreator
 *
 * @author Justin Basilico
 * @since  2.1
 */
public class DefaultClusterCreatorTest 
    extends TestCase
{

    /**
     * Creates a new test.
     * 
     * @param testName The test name.
     */
    public DefaultClusterCreatorTest(
        final String testName)
    {
        super(testName);
    }
    
    /**
     * Test of the constructors of class DefaultClusterCreator.
     */
    public void testConstructors()
    {
        assertNotNull(new DefaultClusterCreator<String>());
    }

    /**
     * Test of createCluster method, of class DefaultClusterCreator.
     */
    public void testCreateCluster()
    {
        DefaultClusterCreator<String> instance = 
            new DefaultClusterCreator<String>();
        Collection<String> members = new LinkedList<String>();
        DefaultCluster<String> result = instance.createCluster(members);
        assertTrue(result.getMembers().isEmpty());
        assertSame(result.getMembers(), result.getMembers());
        
        members.add("a");
        result = instance.createCluster(members);
        assertEquals(1, result.getMembers().size());
        assertTrue(result.getMembers().contains("a"));
        
        assertNotSame(result, instance.createCluster(members));
        
        members.add("b");
        
        result = instance.createCluster(members);
        assertEquals(2, result.getMembers().size());
        assertTrue(result.getMembers().contains("a"));
        assertTrue(result.getMembers().contains("b"));
        
        boolean exceptionThrown = false;
        try
        {
            instance.createCluster(null);
        }
        catch (NullPointerException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

}
