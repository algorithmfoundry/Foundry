/*
 * File:                PrototypeFactoryTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 01, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.factory;

import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Justin Basilico
 */
public class PrototypeFactoryTest extends TestCase
{
    protected Random random;
    public PrototypeFactoryTest(
        final String testName)
    {
        super(testName);
        
        this.random = new Random(1);
    }
    
    public void testConstructors()
    {
        Vector3 prototype = null;
        PrototypeFactory<Vector3> instance = new PrototypeFactory<Vector3>();
        assertEquals(prototype, instance.getPrototype());
        
        prototype = Vector3.createRandom(this.random);
        instance = new PrototypeFactory<Vector3>(prototype);
        assertEquals(prototype, instance.getPrototype());
        assertNotSame(prototype, instance.getPrototype());
        
        PrototypeFactory<Vector3> copy = new PrototypeFactory<Vector3>(instance);
        assertEquals(prototype, copy.getPrototype());
        assertNotSame(prototype, instance.getPrototype());
        assertNotSame(instance.getPrototype(), copy.getPrototype());
        
        instance.getPrototype().setX(4.7);
        assertFalse(prototype.equals(instance.getPrototype()));
        assertEquals(prototype, copy.getPrototype());
    }

    /**
     * Test of clone method, of class PrototypeFactory.
     */
    public void testClone()
    {
        Vector3 prototype = Vector3.createRandom(this.random);
        PrototypeFactory<Vector3> instance = new PrototypeFactory<Vector3>(prototype);
        PrototypeFactory<Vector3> clone = instance.clone();
        assertNotNull(clone);
        assertNotSame(instance, clone);
        assertEquals(prototype, clone.getPrototype());
        assertEquals(instance.getPrototype(), clone.getPrototype());
        assertNotSame(prototype, clone.getPrototype());
        assertNotSame(instance.getPrototype(), clone.getPrototype());
    }

    /**
     * Test of create method, of class PrototypeFactory.
     */
    public void testCreate()
    {
        Vector3 prototype = Vector3.createRandom(this.random);
        PrototypeFactory<Vector3> instance = new PrototypeFactory<Vector3>(prototype);
        Vector3 created = instance.create();
        assertEquals(created, prototype);
        assertNotSame(created, instance.getPrototype());
        
        created.setX(-4.2);
        assertFalse(created.equals(instance.getPrototype()));

        instance = new PrototypeFactory<Vector3>( (Vector3) null );
        assertNull( instance.create() );

    }

    /**
     * Test of getPrototype method, of class PrototypeFactory.
     */
    public void testGetPrototype()
    {
        this.testSetPrototype();
    }

    /**
     * Test of setPrototype method, of class PrototypeFactory.
     */
    public void testSetPrototype()
    {
        Vector3 prototype = null;
        PrototypeFactory<Vector3> instance = new PrototypeFactory<Vector3>();
        assertEquals(prototype, instance.getPrototype());
        
        prototype = new Vector3();
        instance.setPrototype(prototype);
        assertEquals(prototype, instance.getPrototype());
        assertNotSame(prototype, instance.getPrototype());
        assertSame(instance.getPrototype(), instance.getPrototype());
        
        prototype = Vector3.createRandom(this.random);
        instance.setPrototype(prototype);
        assertEquals(prototype, instance.getPrototype());
        assertNotSame(prototype, instance.getPrototype());
        assertSame(instance.getPrototype(), instance.getPrototype());
        
        prototype = null;
        instance.setPrototype(prototype);
        assertEquals(prototype, instance.getPrototype());
    }

    /**
     * Test of createFactory method, of class PrototypeFactory.
     */
    public void testCreateFactory()
    {
        Vector3 prototype = null;
        PrototypeFactory<Vector3> instance = PrototypeFactory.createFactory(prototype);
        assertEquals(prototype, instance.getPrototype());

        prototype = new Vector3();
        instance = PrototypeFactory.createFactory(prototype);
        assertEquals(prototype, instance.getPrototype());
        assertNotSame(prototype, instance.getPrototype());

        prototype = Vector3.createRandom(this.random);
        instance = PrototypeFactory.createFactory(prototype);
        assertEquals(prototype, instance.getPrototype());
        assertNotSame(prototype, instance.getPrototype());
    }

}
