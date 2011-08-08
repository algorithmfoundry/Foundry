/*
 * File:                ObjectUtilTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 28, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.util;

import gov.sandia.cognition.io.ObjectSerializationHandler;
import gov.sandia.cognition.math.Ring;
import gov.sandia.cognition.math.matrix.AbstractVector;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.DenseVector;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     ObjectUtil
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class ObjectUtilTest
    extends TestCase
{

    /**
     * Random number generator
     */
    Random r = new Random( 1 );

    /**
     * 
     * @param testName
     */
    public ObjectUtilTest(
        String testName )
    {
        super( testName );
    }

    /**
     * Test of hidden constructor
     */
    public void testConstructor()
    {
        System.out.println( "Constructor" );
        ObjectUtil ou = new ObjectUtil();
        assertNotNull( ou );
    }

    /**
     * Test of equalsSafe method, of class gov.sandia.cognition.util.ObjectUtil.
     */
    public void testEqualsSafe()
    {
        assertTrue( ObjectUtil.equalsSafe( "a", "a" ) );
        assertFalse( ObjectUtil.equalsSafe( "a", "b" ) );
        assertFalse( ObjectUtil.equalsSafe( "b", "a" ) );

        assertTrue( ObjectUtil.equalsSafe( null, null ) );
        assertFalse( ObjectUtil.equalsSafe( "a", null ) );
        assertFalse( ObjectUtil.equalsSafe( null, "a" ) );
    }

    /**
     * Test of cloneSafe method, of class gov.sandia.cognition.util.ObjectUtil.
     */
    public void testCloneSafe()
    {
        Vector vNull = null;
        assertNull( ObjectUtil.cloneSafe( vNull ) );

        Vector v = Vector3.createRandom(r);
        Vector clone = ObjectUtil.cloneSafe( v );

        assertNotNull( clone );
        assertNotSame( v, clone );
        assertEquals( v, clone );
    }

    /**
     * Class that is not Cloneable, but has a clone method.
     */
    public class PublicClone
    {
        @Override
        public PublicClone clone()
        {
            return new PublicClone();
        }

    }

    /**
     * Test of cloneSmart()
     */
    public void testCloneSmart()
    {

        Vector v = null;
        assertNull( ObjectUtil.cloneSmart(v) );

        v = Vector3.createRandom(r);
        Vector clone = ObjectUtil.cloneSmart(v);
        assertNotNull( clone );
        assertNotSame( v, clone );
        assertEquals( v, clone );

        Double d = new Double( r.nextGaussian() );
        Double dc = ObjectUtil.cloneSmart(d);
        assertNotNull( dc );
        assertSame( d, dc );

        PublicClone pc = new PublicClone();
        PublicClone pcc = pc.clone();
        assertNotNull( pcc );
        assertNotSame( pc, pcc );

    }

    /**
     * cloneSmartElementsAsArrayList
     */
    public void testCloneSmartElementsAsArrayList()
    {
        System.out.println( "cloneSmartElementsAsArrayList" );

        final int num = 10;
        ArrayList<Vector> data = null;
        assertNull( ObjectUtil.cloneSmartElementsAsArrayList(data) );
        data = new ArrayList<Vector>(num);
        ArrayList<Vector> clone = ObjectUtil.cloneSmartElementsAsArrayList(data);
        assertNotNull( clone );
        assertNotSame( data, clone );
        assertEquals( data.size(), clone.size() );
        for( int n = 0; n < num; n++ )
        {
            data.add( Vector3.createRandom(r) );
        }

        clone = ObjectUtil.cloneSmartElementsAsArrayList(data);
        assertNotNull( clone );
        assertNotSame( data, clone );
        assertEquals( data.size(), clone.size() );
        for( int n = 0; n < num; n++ )
        {
            assertNotNull( clone.get(n) );
            assertNotSame( data.get(n), clone.get(n) );
            assertEquals( data.get(n), clone.get(n) );
        }

    }

    /**
     * cloneSmartElementsAsLinkedList
     */
    public void testCloneSmartElementsAsLinkedList()
    {
        System.out.println( "cloneSmartElementsAsLinkedList" );
        final int num = 10;
        ArrayList<Vector> data = null;
        assertNull( ObjectUtil.cloneSmartElementsAsLinkedList(data) );
        data = new ArrayList<Vector>(num);
        LinkedList<Vector> clone = ObjectUtil.cloneSmartElementsAsLinkedList(data);
        assertNotNull( clone );
        assertNotSame( data, clone );
        assertEquals( data.size(), clone.size() );
        for( int n = 0; n < num; n++ )
        {
            data.add( Vector3.createRandom(r) );
        }

        clone = ObjectUtil.cloneSmartElementsAsLinkedList(data);
        assertNotNull( clone );
        assertNotSame( data, clone );
        assertEquals( data.size(), clone.size() );
        for( int n = 0; n < num; n++ )
        {
            assertNotNull( clone.get(n) );
            assertNotSame( data.get(n), clone.get(n) );
            assertEquals( data.get(n), clone.get(n) );
        }

    }

    /**
     * cloneSmartArrayAndElements
     */
    public void testCloneSmartArrayAndElements()
    {
        System.out.println( "cloneSmartArrayAndElements" );
        final int num = 10;
        Vector[] data = null;
        assertNull( ObjectUtil.cloneSmartArrayAndElements(data) );
        data = new Vector[num];
        Vector[] clone = ObjectUtil.cloneSmartArrayAndElements(data);
        assertNotNull( clone );
        assertNotSame( data, clone );
        assertEquals( data.length, clone.length );
        for( int n = 0; n < num; n++ )
        {
            data[n] = Vector3.createRandom(r);
        }

        clone = ObjectUtil.cloneSmartArrayAndElements(data);
        assertNotNull( clone );
        assertNotSame( data, clone );
        assertEquals( data.length, clone.length );
        for( int n = 0; n < num; n++ )
        {
            assertNotNull( clone[n] );
            assertNotSame( data[n], clone[n] );
            assertEquals( data[n], clone[n] );
        }

    }

    /**
     * deepCopy
     */
    public void testDeepCopy()
    {
        System.out.println( "deepCopy" );

        Vector v = null;
        assertNull( ObjectUtil.deepCopy(v) );
        v = Vector3.createRandom(r);
        Vector v2 = ObjectUtil.deepCopy(v);
        assertNotNull( v2 );
        assertNotSame( v, v2 );
        assertEquals( v, v2 );

    }

    /**
     * Test of inspector method, of class gov.sandia.cognition.util.ObjectUtil.
     */
    public void testInspector()
    {
        System.out.println( "inspector" );


        DefaultPair<Double, Double> p = new DefaultPair<Double, Double>( 1.0, 2.0 );
        String s = ObjectUtil.inspector( p );
        System.out.println( s );

        System.out.println( ObjectUtil.inspector( DefaultPair.class ) );

        WeightedValue<DefaultPair<Double, Double>> w =
            new DefaultWeightedValue<DefaultPair<Double, Double>>( p, r.nextGaussian() );
        System.out.println( ObjectUtil.inspector( w ) );
        System.out.println( ObjectUtil.inspector( Ring.class ) );

        System.out.println( "From String SHOULD FAIL!" );
        System.out.println( ObjectUtil.inspector( "FiniteCapacityBuffer" ) );

        System.out.println( "From String SHOULD succeed:" );
        System.out.println( ObjectUtil.inspector( "gov.sandia.cognition.util.FiniteCapacityBuffer" ) );
    }

    /**
     * Test of inspectMemberValues method, of class gov.sandia.cognition.util.ObjectUtil.
     */
    public void testInspectMemberValues()
    {
        System.out.println( "inspectMemberValues" );

        DefaultPair<Double, Double> p = new DefaultPair<Double, Double>( 1.0, 2.0 );

        System.out.println( ObjectUtil.inspectFieldValues( p ) );

    }

    /**
     * Test of inspectAPI method, of class gov.sandia.cognition.util.ObjectUtil.
     */
    public void testInspectAPI()
    {
        System.out.println( "inspectAPI" );

        System.out.println( ObjectUtil.inspectAPI( DenseVector.class ) );
        System.out.println( ObjectUtil.inspectAPI( AbstractVector.class ) );
    }

    /**
     * Test of getShortClassName method, of class ObjectUtil.
     */
    public void testGetShortClassName()
    {
        System.out.println( "getShortClassName" );
        Object o = this;
        String expResult = "ObjectUtilTest";
        String result = ObjectUtil.getShortClassName( o );
        assertEquals( expResult, result );
        
        String e2 = "ObjectUtilTest$InternalClass";
        o = new InternalClass();
        result = ObjectUtil.getShortClassName(o);
        assertEquals( e2, result );

    }

    /**
     * toString
     */
    public void testToString()
    {
        System.out.println( "toString" );

        Object o = new DefaultWeightedValue<String>( "Hello", Math.PI );
        System.out.println( "This: " + ObjectUtil.toString(o) );

    }

    /**
     * toString(Array)
     */
    public void testToStringArray()
    {
        System.out.println( "toString(Array)" );

        final int num = 5;
        WeightedValue[] values = new DefaultWeightedValue[num];
        for( int i = 0; i < num; i++ )
        {
            values[i] = new DefaultWeightedValue<String>( "Num " + i, r.nextGaussian() );
        }
        System.out.println( "Array: " + ObjectUtil.toString(values) );

    }

    /**
     * toString(Iterable)
     */
    public void testToStringIterable()
    {
        System.out.println( "toString(Iterable)" );
        LinkedList<WeightedValue> list = new LinkedList<WeightedValue>();
        for( int i = 0; i < 4; i++ )
        {
            list.add( new DefaultWeightedValue<String>( "Num " + i, r.nextGaussian() ) );
        }
        System.out.println( "Iterable: " + ObjectUtil.toString(list) );
    }

    /**
     * getBytes
     */
    public void testGetBytes()
        throws Exception
    {
        System.out.println( "getBytes" );

        String s1 = "The quick brown fox jumps over the lazy dog.";
        byte[] b = ObjectUtil.getBytes(s1);
        String s2 = (String) ObjectSerializationHandler.convertFromBytes(b);
        assertEquals( s1, s2 );

        Object o = new Object();
        byte[] b2 = ObjectUtil.getBytes(o);
        assertNull( b2 );

    }

    /**
     * Internal class for testing the getShortClassName() method
     */
    private class InternalClass
    {
    }

}
