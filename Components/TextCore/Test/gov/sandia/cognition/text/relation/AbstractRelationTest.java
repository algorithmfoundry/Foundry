/*
 * File:                AbstractRelationTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 18, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.relation;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class AbsractRelation.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class AbstractRelationTest
{
    /**
     * Creates a new test.
     */
    public AbstractRelationTest()
    {
    }

    /**
     * Test of constructors of class AbstractRelation.
     */
    @Test
    public void testConstructors()
    {
        String source = null;
        Integer target = null;
        AbstractRelation<String, Integer> instance = new DummyRelation();
        assertSame(source, instance.getSource());
        assertSame(target, instance.getTarget());

        source = "test";
        target = 4;
        instance = new DummyRelation(source, target);
        assertSame(source, instance.getSource());
        assertSame(target, instance.getTarget());
    }

    /**
     * Test of getSource method, of class AbstractRelation.
     */
    @Test
    public void testGetSource()
    {
        this.testSetSource();
    }

    /**
     * Test of setSource method, of class AbstractRelation.
     */
    @Test
    public void testSetSource()
    {
        String source = null;
        AbstractRelation<String, Integer> instance = new DummyRelation();
        assertSame(source, instance.getSource());

        source = "test";
        instance.setSource(source);
        assertSame(source, instance.getSource());

        source = "test2";
        instance.setSource(source);
        assertSame(source, instance.getSource());

        source = null;
        instance.setSource(source);
        assertSame(source, instance.getSource());
    }

    /**
     * Test of getTarget method, of class AbstractRelation.
     */
    @Test
    public void testGetTarget()
    {
        this.testSetTarget();
    }


    /**
     * Test of setTarget method, of class AbstractRelation.
     */
    @Test
    public void testSetTarget()
    {
        Integer target = null;
        AbstractRelation<String, Integer> instance = new DummyRelation();
        assertSame(target, instance.getTarget());

        target = 4;
        instance.setTarget(target);
        assertSame(target, instance.getTarget());
        
        target = 6;
        instance.setTarget(target);
        assertSame(target, instance.getTarget());

        target = null;
        instance.setTarget(target);
        assertSame(target, instance.getTarget());
    }

    public class DummyRelation
        extends AbstractRelation<String, Integer>
    {
        public DummyRelation()
        {
            super();
        }

        public DummyRelation(
            final String source,
            final Integer target)
        {
            super(source, target);
        }
    }

}