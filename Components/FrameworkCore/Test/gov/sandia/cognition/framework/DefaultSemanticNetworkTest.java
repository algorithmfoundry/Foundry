/*
 * File:                DefaultSemanticNetworkTest.java
 * Authors:             Justin Basilico 
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 14, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */
package gov.sandia.cognition.framework;

import junit.framework.*;

/**
 * This class implements JUnit tests for the following classes:
 *
 *    DefaultSemanticNetwork
 *
 * @author Justin Basilico
 * @since  1.0
 */
public class DefaultSemanticNetworkTest 
    extends TestCase
{
    /**
     * Creates a new instance of DefaultSemanticNetworkTest.
     *
     * @param  testName The test name.
     */
    public DefaultSemanticNetworkTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Tests the creation of a DefaultSemanticNetwork.
     *
     * If this test fails, contact Justin Basilico.
     */
    public void testCreation() 
    {
        // Create a new network.
        DefaultSemanticNetwork net = new DefaultSemanticNetwork();
        
        // The network should start out empty.
        assertEquals(0, net.getNumNodes());
        assertEquals(0, net.getNodes().size());
        
        // We wil use three labels in the network.
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        DefaultSemanticLabel c = new DefaultSemanticLabel("c");
        
        // Assert that they are not nodes.
        assertFalse(net.isNode(a));
        assertFalse(net.isNode(b));
        assertFalse(net.isNode(c));
        assertEquals(null, net.getOutLinks(a));
        assertEquals(0.0, net.getAssociation(a, a));
        assertEquals(0.0, net.getAssociation(a, b));
        
        // Add node a.
        net.addNode(a);
        
        // There should now be one node (a) in the network but no links.
        assertEquals(1, net.getNumNodes());
        assertTrue(net.getNodes().contains(a));
        assertTrue(net.isNode(a));
        assertTrue(net.isNode(new DefaultSemanticLabel("a")));
        assertFalse(net.isNode(b));
        assertEquals(0, net.getOutLinks(a).size());
        assertEquals(0.0, net.getAssociation(a, a));
        assertEquals(0.0, net.getAssociation(a, b));
        
        // Add b and c.
        net.addNode(b);
        net.addNode(c);
        
        // There should now be 3 nodes in the network (a, b, c) but still no
        // links.
        assertEquals(net.getNumNodes(), 3);
        assertTrue(net.getNodes().contains(a));
        assertTrue(net.getNodes().contains(b));
        assertTrue(net.getNodes().contains(c));
        assertTrue(net.isNode(a));
        assertTrue(net.isNode(b));
        assertTrue(net.isNode(c));
        assertEquals(0.0, net.getAssociation(b, c));
        
        // Add a link between a and b.
        net.setAssociation(a, b, 1.0);
        assertEquals(1.0, net.getAssociation(a, b));
        assertEquals(0.0, net.getAssociation(b, a));
        
        // Add a link between b and a.
        net.setAssociation(b, a, 2.0);
        assertEquals(1.0, net.getAssociation(a, b));
        assertEquals(2.0, net.getAssociation(b, a));
        
        // There should now be one link out of a and one link out of b.
        assertEquals(1, net.getOutLinks(a).size());
        assertEquals(b, net.getOutLinks(a).toArray()[0]);
        assertEquals(1, net.getOutLinks(b).size());
        assertEquals(a, net.getOutLinks(b).toArray()[0]);
        
        // Add a link between a and itself.
        net.setAssociation(a, a, 1.0);
        assertEquals(1.0, net.getAssociation(a, a), 1.0);
        
        // Set some empty associations between b and c.
        net.setAssociation(b, c, 0.0);
        net.setAssociation(c, b, 0.0);
        assertEquals(1, net.getOutLinks(b).size());
        assertEquals(0, net.getOutLinks(c).size());
        
        // See how the network handles getting associations between null
        // labels.
        assertEquals(0.0, net.getAssociation(a, null));
        assertEquals(0,0, net.getAssociation(null, a));
        assertEquals(0,0, net.getAssociation(null, null));
    }
    
    /**
     * Tests node removal in a DefaultSemanticNetwork.
     *
     * If this test fails, contact Justin Basilico.
     */
    public void testNodeRemoval() 
    {
        // Create a new network.
        DefaultSemanticNetwork net = new DefaultSemanticNetwork();
        
        // We will make use of three nodes.
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        DefaultSemanticLabel c = new DefaultSemanticLabel("c");
        
        // Add the three nodes to the network.
        net.addNode(a);
        net.addNode(b);
        net.addNode(c);
        assertEquals(3, net.getNumNodes());
        
        // Remove c.
        net.removeNode(c);
        
        // Node c should no longer be in the network.
        assertFalse(net.isNode(c));
        assertEquals(2, net.getNumNodes());
        assertFalse(net.getNodes().contains(c));
        
        // Add c again.
        net.addNode(c);
        assertEquals(3, net.getNumNodes());
        
        // Remove c again using a different semantic label object.
        net.removeNode(new DefaultSemanticLabel("c"));
        assertEquals(2, net.getNumNodes());
        
        // Remove a and b.
        net.removeNode(b);
        net.removeNode(a);
        
        // The network should now be empty.
        assertEquals(0, net.getNumNodes());
    }
    
    /**
     * Tests link removal in a DefaultSemanticNetwork.
     *
     * If this test fails, contact Justin Basilico.
     */
    public void testLinkRemoval() 
    {
        // Create a new network.
        DefaultSemanticNetwork net = new DefaultSemanticNetwork();
        
        // We will make use of three nodes.
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        DefaultSemanticLabel c = new DefaultSemanticLabel("c");
        
        // Add the three nodes to the network and make some associations 
        // between them.
        net.addNode(a);
        net.addNode(b);
        net.addNode(c);
        net.setAssociation(a, b, 1.0);
        net.setAssociation(b, c, 2.0);
        net.setAssociation(c, c, 3.0);
        net.setAssociation(c, a, 4.0);
        
        // Remove the association between a and b.
        net.setAssociation(a, b, 0.0);
        assertEquals(0.0, net.getAssociation(a, b));
        assertEquals(0, net.getOutLinks(a).size());
        
        // Add the association between an and b back in.
        net.setAssociation(a, b, 1.0);
        assertEquals(1.0, net.getAssociation(a, b));
        assertEquals(1, net.getOutLinks(a).size());
        
        // Remove the association between c and a.
        net.setAssociation(c, a, 0.0);
        assertEquals(0.0, net.getAssociation(c, a));
        assertFalse(net.getOutLinks(c).contains(a));
        
        // Remove the node b.
        net.removeNode(b);
        
        // Since b was removed all the associations with b should now be
        // zero.
        assertFalse(net.getOutLinks(a).contains(b));
        assertEquals(0.0, net.getAssociation(a, b));
        assertEquals(0.0, net.getAssociation(b, c));
        
        // Add b back in.
        net.addNode(b);
        
        // The associations with b should still be zero.
        assertFalse(net.getOutLinks(a).contains(b));
        assertEquals(0.0, net.getAssociation(a, b));
        assertEquals(0.0, net.getAssociation(b, c));
        
        // Remove a and b.
        net.removeNode(a);
        net.removeNode(b);
        
        // Make sure that a self loop with c still is in the network.
        assertEquals(3.0, net.getAssociation(c, c));
        assertEquals(1, net.getOutLinks(c).size());
    }
}
