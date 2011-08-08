/*
 * File:                RegressionTreeTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 30, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.math.matrix.Vectorizable;
import junit.framework.*;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     RegressionTree
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class RegressionTreeTest
    extends TestCase
{
    /**
     * Creates a new RegressionTreeTest.
     *
     * @param  testName The test name.
     */
    public RegressionTreeTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Tests the constructors of RegressionTree.
     */
    public void testConstructors()
    {
        RegressionTree<Vectorizable> instance = 
            new RegressionTree<Vectorizable>();
        assertNull(instance.getRootNode());
        
        RegressionTreeNode<Vectorizable, Object> rootNode =
            new RegressionTreeNode<Vectorizable, Object>();
        instance = new RegressionTree<Vectorizable>(rootNode);
        assertSame(rootNode, instance.getRootNode());
    }
}
