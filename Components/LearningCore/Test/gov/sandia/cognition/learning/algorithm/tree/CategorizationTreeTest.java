/*
 * File:                CategorizationTreeTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 13, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.math.matrix.Vectorizable;
import java.util.Set;
import java.util.TreeSet;
import junit.framework.*;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     CategorizationTree
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class CategorizationTreeTest
    extends TestCase
{
    public CategorizationTreeTest(
        String testName)
    {
        super(testName);
    }
    
    public void testConstructors()
    {
        CategorizationTree<Vectorizable, Boolean> instance = 
            new CategorizationTree<Vectorizable, Boolean>();
        assertNull(instance.getRootNode());
        
        Set<Boolean> categories = new TreeSet<Boolean>();
        CategorizationTreeNode<Vectorizable, Boolean, Object> rootNode =
            new CategorizationTreeNode<Vectorizable, Boolean, Object>();
        instance = new CategorizationTree<Vectorizable, Boolean>(rootNode,
            categories);
        assertSame(rootNode, instance.getRootNode());
        assertSame(categories, instance.getCategories());
    }

    /**
     * Test of getCategories method, of class CategorizationTree.
     */
    public void testGetCategories()
    {
        this.testSetCategories();
    }

    /**
     * Test of setCategories method, of class CategorizationTree.
     */
    public void testSetCategories()
    {
        CategorizationTree<Vectorizable, Boolean> instance = 
            new CategorizationTree<Vectorizable, Boolean>();
        assertNull(instance.getCategories());
        
        Set<Boolean> categories = new TreeSet<Boolean>();
        instance.setCategories(categories);
        assertSame(categories, instance.getCategories());
        
        categories = null;
        instance.setCategories(categories);
        assertNull(instance.getCategories());
    }
}
