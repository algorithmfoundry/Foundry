/*
 * File:                CogxelStateLiteTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 28, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.DefaultCogxelFactory;
import junit.framework.*;
import gov.sandia.cognition.framework.Cogxel;
import gov.sandia.cognition.framework.DefaultCogxel;
import gov.sandia.cognition.framework.DefaultSemanticIdentifierMap;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.SemanticIdentifier;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     <!-- TO DO: Description: -->
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class CogxelStateLiteTest
    extends TestCase
{
    public CogxelStateLiteTest(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(CogxelStateLiteTest.class);
        
        return suite;
    }

    /**
     * Test of clear method, of class gov.sandia.isrc.cognition.framework.lite.CogxelStateLite.
     */
    public void testClear()
    {
        CogxelStateLite instance = new CogxelStateLite();        
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        SemanticIdentifier aID = map.addLabel(a);
        
        instance.getOrCreateCogxel(aID, DefaultCogxelFactory.INSTANCE);
        
        instance.clear();
        assertNotNull(instance.getCogxels());
        assertEquals(0, instance.getNumCogxels());
    }

    /**
     * Test of addCogxel method, of class gov.sandia.isrc.cognition.framework.lite.CogxelStateLite.
     */
    public void testAddCogxel()
    {
        CogxelStateLite instance = new CogxelStateLite();        
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        SemanticIdentifier idA = map.addLabel(a);
        Cogxel cogxelA = new DefaultCogxel(idA, 1.0);
        
        instance.addCogxel(cogxelA);
        
        assertEquals(1, instance.getNumCogxels());
        assertSame(cogxelA, instance.getCogxel(idA));
        assertEquals(1.0, instance.getCogxel(idA).getActivation());
        
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        SemanticIdentifier idB = map.addLabel(b);
        Cogxel cogxelB = new DefaultCogxel(idB, 2.0);
        
        instance.addCogxel(cogxelB);
        assertEquals(2, instance.getNumCogxels());
        assertSame(cogxelB, instance.getCogxel(idB));
        assertEquals(2.0, instance.getCogxel(idB).getActivation());
        
        Cogxel newCogxelA = new DefaultCogxel(idA, 47.0);
        instance.addCogxel(newCogxelA);
        assertEquals(2, instance.getNumCogxels());
        assertSame(newCogxelA, instance.getCogxel(idA));
        assertEquals(47.0, instance.getCogxel(idA).getActivation());
        
        boolean exceptionThrown = false;
        try
        {
            instance.addCogxel(null);
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
            instance.addCogxel(new DefaultCogxel((SemanticIdentifier) null));
        }
        catch ( NullPointerException e )
        {
            exceptionThrown = true;
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
     * Test of hasCogxel method, of class gov.sandia.isrc.cognition.framework.lite.CogxelStateLite.
     */
    public void testHasCogxel()
    {
        CogxelStateLite instance = new CogxelStateLite();
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        SemanticIdentifier a = map.addLabel(new DefaultSemanticLabel("a"));
        Cogxel cogxelA = new DefaultCogxel(a, 1.0);
        
        assertFalse(instance.hasCogxel(a));
        
        instance.addCogxel(cogxelA);
        assertTrue(instance.hasCogxel(a));
        
        SemanticIdentifier b = map.addLabel(new DefaultSemanticLabel("b"));
        Cogxel cogxelB = new DefaultCogxel(b, 2.0);
        instance.addCogxel(cogxelB);
        
        assertTrue(instance.hasCogxel(b));
        
        assertFalse(instance.hasCogxel(null));
    }

    /**
     * Test of getCogxel method, of class gov.sandia.isrc.cognition.framework.lite.CogxelStateLite.
     */
    public void testGetCogxel()
    {
        CogxelStateLite instance = new CogxelStateLite();
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        SemanticIdentifier a = map.addLabel(new DefaultSemanticLabel("a"));
        Cogxel cogxelA = new DefaultCogxel(a, 1.0);
        
        Cogxel outA = instance.getCogxel(a);
        assertNull(outA);
        
        instance.addCogxel(cogxelA);
        outA = instance.getCogxel(a);
        assertNotNull(outA);
        assertSame(cogxelA, outA); 

        Cogxel badCogxel = instance.getCogxel(null);
        assertNull( badCogxel );

    }

    /**
     * Test of getOrCreateCogxel method, of class gov.sandia.isrc.cognition.framework.lite.CogxelStateLite.
     */
    public void testGetOrCreateCogxel()
    {
        CogxelStateLite instance = new CogxelStateLite();
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        SemanticIdentifier a = map.addLabel(new DefaultSemanticLabel("a"));
        Cogxel cogxelA = new DefaultCogxel(a, 1.0);
        
        Cogxel outA1 = instance.getOrCreateCogxel(a, DefaultCogxelFactory.INSTANCE);
        assertNotNull(outA1);
        assertEquals(0.0, outA1.getActivation());
        
        Cogxel outA2 = instance.getOrCreateCogxel(a, DefaultCogxelFactory.INSTANCE);
        assertSame(outA1, outA2);
        
        boolean exceptionThrown = false;
        try
        {
            Cogxel badCogxel = instance.getOrCreateCogxel(null, DefaultCogxelFactory.INSTANCE);
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
     * Test of removeCogxel method, of class gov.sandia.isrc.cognition.framework.lite.CogxelStateLite.
     */
    public void testRemoveCogxel()
    {
        CogxelStateLite instance = new CogxelStateLite();
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        SemanticIdentifier a = map.addLabel(new DefaultSemanticLabel("a"));
        Cogxel cogxelA = new DefaultCogxel(a, 1.0);
        
        assertFalse(instance.removeCogxel(a));
        assertFalse(instance.removeCogxel(cogxelA));
        
        instance.addCogxel(cogxelA);
        assertTrue(instance.removeCogxel(a));
        assertEquals(0, instance.getNumCogxels());
        
        instance.addCogxel(cogxelA);
        assertTrue(instance.removeCogxel(cogxelA));
        assertEquals(0, instance.getNumCogxels());
        instance.addCogxel(cogxelA);
        
        SemanticIdentifier b = map.addLabel(new DefaultSemanticLabel("b"));
        assertFalse(instance.removeCogxel(b));
        
        boolean exceptionThrown = false;
        try
        {
            boolean bad = instance.removeCogxel((SemanticIdentifier) null);
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
            boolean bad = instance.removeCogxel((Cogxel) null);
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
     * Test of getCogxels method, of class gov.sandia.isrc.cognition.framework.lite.CogxelStateLite.
     */
    public void testGetCogxels()
    {
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        CogxelStateLite instance = new CogxelStateLite();
        
        Collection<Cogxel> result = instance.getCogxels();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(result, instance.getCogxels());
        
        SemanticIdentifier a = map.addLabel(new DefaultSemanticLabel("a"));
        Cogxel cogxelA = new DefaultCogxel(a, 1.0);
        instance.addCogxel(cogxelA);
        
        assertEquals(1, result.size());
        assertTrue(result.contains(cogxelA));
        
        SemanticIdentifier b = map.addLabel(new DefaultSemanticLabel("b"));
        Cogxel cogxelB = new DefaultCogxel(b, 2.0);
        instance.addCogxel(cogxelB);
        
        assertEquals(2, result.size());
        assertTrue(result.contains(cogxelB));
    }
    /**
     * Test of getNumCogxels method, of class gov.sandia.isrc.cognition.framework.lite.CogxelStateLite.
     */
    public void testGetNumCogxels()
    {
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        CogxelStateLite instance = new CogxelStateLite();
        
        assertEquals(0, instance.getNumCogxels());
        
        SemanticIdentifier a = map.addLabel(new DefaultSemanticLabel("a"));
        Cogxel cogxelA = new DefaultCogxel(a, 1.0);
        instance.addCogxel(cogxelA);
        
        assertEquals(1, instance.getNumCogxels());
        
        SemanticIdentifier b = map.addLabel(new DefaultSemanticLabel("b"));
        Cogxel cogxelB = new DefaultCogxel(b, 2.0);
        instance.addCogxel(cogxelB);
        
        assertEquals(2, instance.getNumCogxels());
    }
    
    /**
     * Test of iterator method, of class gov.sandia.isrc.cognition.framework.lite.CogxelStateLite.
     */
    public void testIterator()
    {
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        CogxelStateLite instance = new CogxelStateLite();
        
        Iterator<Cogxel> it = instance.iterator();
        assertFalse(it.hasNext());
        
        SemanticIdentifier a = map.addLabel(new DefaultSemanticLabel("a"));
        Cogxel cogxelA = new DefaultCogxel(a, 1.0);
        instance.addCogxel(cogxelA);
        
        it = instance.iterator();
        assertTrue(it.hasNext());
        assertSame(cogxelA, it.next());
        assertFalse(it.hasNext());
        
        SemanticIdentifier b = map.addLabel(new DefaultSemanticLabel("b"));
        Cogxel cogxelB = new DefaultCogxel(b, 2.0);
        instance.addCogxel(cogxelB);
        
        it = instance.iterator();
        assertTrue(it.hasNext());
        assertSame(cogxelA, it.next());
        assertTrue(it.hasNext());
        assertSame(cogxelB, it.next());
        assertFalse(it.hasNext());
    }

    /**
     * Test of clone method, of class gov.sandia.isrc.cognition.framework.lite.CogxelStateLite.
     */
    public void testClone()
    {
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        CogxelStateLite instance = new CogxelStateLite();
        SemanticIdentifier a = map.addLabel(new DefaultSemanticLabel("a"));
        Cogxel cogxelA = new DefaultCogxel(a, 1.0);
        instance.addCogxel(cogxelA);
        
        SemanticIdentifier b = map.addLabel(new DefaultSemanticLabel("b"));
        Cogxel cogxelB = new DefaultCogxel(b, 2.0);
        instance.addCogxel(cogxelB);
        
        CogxelStateLite clone = instance.clone();
        assertNotNull(clone);
        assertNotSame(instance, clone);
        
        assertEquals(instance.getNumCogxels(), clone.getNumCogxels());
        
        Cogxel copyA = clone.getCogxel(a);
        Cogxel copyB = clone.getCogxel(b);
        assertNotNull(copyA);
        assertNotNull(copyB);
        assertNotSame(cogxelA, copyA);
        assertNotSame(cogxelB, copyB);
        assertSame(cogxelA.getSemanticIdentifier(), copyA.getSemanticIdentifier());
        assertSame(cogxelB.getSemanticIdentifier(), copyB.getSemanticIdentifier());
        assertEquals(cogxelA.getActivation(), copyA.getActivation());
        assertEquals(cogxelB.getActivation(), copyB.getActivation());
    }

    /**
     * Test of getCogxelActivation method, of class gov.sandia.isrc.cognition.framework.lite.CogxelStateLite.
     */
    public void testGetCogxelActivation()
    {
        CogxelStateLite instance = new CogxelStateLite();
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        SemanticIdentifier a = map.addLabel(new DefaultSemanticLabel("a"));
        Cogxel cogxelA = new DefaultCogxel(a, 1.0);
        
        assertEquals(0.0, instance.getCogxelActivation(a));
        
        instance.addCogxel(cogxelA);
        assertEquals(1.0, instance.getCogxelActivation(a));
        
        double badl = instance.getCogxelActivation(null);
        assertEquals( 0.0, badl );

    }
}
