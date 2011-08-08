/*
 * File:                KDTreeTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 29, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.geometry;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.math.Metric;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.DefaultPair;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for KDTreeTest.
 *
 * @author krdixon
 */
public class KDTreeTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public final double TOLERANCE = 1e-5;

    /**
     * Example from http://en.wikipedia.org/wiki/Kd-tree#Construction
     */
    @SuppressWarnings("unchecked")
    public static List<DefaultPair<Vector,Integer>> points = Arrays.asList(
        DefaultPair.create( VectorFactory.getDefault().copyValues(2,3), 0 ),
        DefaultPair.create( VectorFactory.getDefault().copyValues(5,4), 1 ),
        DefaultPair.create( VectorFactory.getDefault().copyValues(9,6), 2 ),
        DefaultPair.create( VectorFactory.getDefault().copyValues(4,7), 3 ),
        DefaultPair.create( VectorFactory.getDefault().copyValues(8,1), 4 ),
        DefaultPair.create( VectorFactory.getDefault().copyValues(7,2), 5 )
    );


    /**
     * Tests for class KDTreeTest.
     * @param testName Name of the test.
     */
    public KDTreeTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Creates an instance
     * @return
     * instance.
     */
    public KDTree<Vector,Integer,DefaultPair<Vector,Integer>> createInstance()
    {
        return KDTree.createBalanced( points );
    }


    /**
     * Tests the constructors of class KDTreeTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        KDTree<Vector,Integer,DefaultPair<Vector,Integer>> tree =
            new KDTree<Vector,Integer,DefaultPair<Vector,Integer>>();
        assertNull( tree.comparator );
        assertNull( tree.value );
        assertNull( tree.leftChild );
        assertNull( tree.rightChild );
        assertNull( tree.parent );

        tree = this.createInstance();
        assertEquals( points.size(), tree.size() );
        assertSame( points.get(0), tree.leftChild.leftChild.value );
        assertSame( points.get(1), tree.leftChild.value );
        assertSame( points.get(2), tree.rightChild.value );
        assertSame( points.get(3), tree.leftChild.rightChild.value );
        assertSame( points.get(4), tree.rightChild.leftChild.value );
        assertSame( points.get(5), tree.value );

        try
        {
            tree = new KDTree<Vector, Integer,DefaultPair<Vector,Integer>>( new ArrayList<DefaultPair<Vector,Integer>>(), null, 0, null );
            fail( "Cannot give empty points!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of clone method, of class KDTree.
     */
    public void testClone()
    {
        System.out.println("clone");

        KDTree<Vector,Integer,DefaultPair<Vector,Integer>> tree = this.createInstance();
        KDTree<Vector,Integer,DefaultPair<Vector,Integer>> clone = tree.clone();
        assertNotNull( clone );
        assertNotSame( tree, clone );
        assertSame( tree.comparator, clone.comparator );
        assertSame( tree.parent, clone.parent );
        assertNotSame( tree.value, clone.value );
        assertEquals( tree.value.getFirst(), clone.value.getFirst() );
        assertNotSame( tree.leftChild, clone.leftChild );
        assertEquals( tree.leftChild.value.getFirst(), clone.leftChild.value.getFirst() );
        assertNotSame( tree.rightChild, clone.rightChild );
        assertEquals( tree.rightChild.value.getFirst(), clone.rightChild.value.getFirst() );
    }

    /**
     * Test of createBalanced method, of class KDTree.
     */
    public void testCreateBalanced()
    {
        System.out.println("createBalanced");
        KDTree<Vector,Integer,DefaultPair<Vector,Integer>> tree = KDTree.createBalanced(points);
        System.out.println( "Tree:\n" + tree );

        assertEquals( points.size(), tree.size() );
        assertSame( points.get(0), tree.leftChild.leftChild.value );
        assertSame( points.get(1), tree.leftChild.value );
        assertSame( points.get(2), tree.rightChild.value );
        assertSame( points.get(3), tree.leftChild.rightChild.value );
        assertSame( points.get(4), tree.rightChild.leftChild.value );
        assertSame( points.get(5), tree.value );
    }

    /**
     * Test of reblanace method, of class KDTree.
     */
    public void testReblanace()
    {
        System.out.println("reblanace");

        KDTree<Vector,Integer,DefaultPair<Vector,Integer>> tree =
            new KDTree<Vector, Integer,DefaultPair<Vector,Integer>>();
        for( DefaultPair<Vector,Integer> point : points )
        {
            tree.add( point );
        }

        System.out.println( "Tree:\n" + tree );
        KDTree<Vector,Integer,DefaultPair<Vector,Integer>> deepCopy = ObjectUtil.deepCopy(tree);

        KDTree<Vector,Integer,DefaultPair<Vector,Integer>> balanced = tree.reblanace();
        assertEquals( deepCopy.toString(), tree.toString() );

        KDTree<Vector,Integer,DefaultPair<Vector,Integer>> originalBalanced = KDTree.createBalanced(points);
        assertEquals( originalBalanced.toString(), balanced.toString() );



    }

    /**
     * Test of add method, of class KDTree.
     */
    public void testAdd()
    {
        System.out.println("add");
        KDTree<Vector,Integer,DefaultPair<Vector,Integer>> tree =
            new KDTree<Vector, Integer,DefaultPair<Vector,Integer>>();
        tree.add( points.get(0) );
        assertSame( points.get(0), tree.value );
        assertNull( tree.leftChild );
        assertNull( tree.rightChild );
        assertEquals( 0, tree.comparator.comparator.getIndex() );

        tree.add( points.get(1) );
        assertSame( tree.value, points.get(0) );
        assertSame( points.get(1), tree.rightChild.value );
        assertNull( tree.leftChild );
        assertEquals( 1, tree.rightChild.comparator.comparator.getIndex() );

        tree.add( points.get(2) );
        assertSame( points.get(2), tree.rightChild.rightChild.value );
        assertEquals( 0, tree.rightChild.rightChild.comparator.comparator.getIndex() );

        tree.add( points.get(3) );
        assertSame( points.get(3), tree.rightChild.rightChild.leftChild.value );
        assertEquals( 1, tree.rightChild.rightChild.leftChild.comparator.comparator.getIndex() );

    }

    /**
     * Test of size method, of class KDTree.
     */
    public void testSize()
    {
        System.out.println("size");

        KDTree<Vector,Integer,DefaultPair<Vector,Integer>> tree =
            new KDTree<Vector, Integer, DefaultPair<Vector,Integer>>();
        assertEquals( 0, tree.size() );
        for( int i = 0; i < points.size(); i++ )
        {
            tree.add( points.get(i) );
            assertEquals( i+1, tree.size() );
        }
    }

    /**
     * Test of iterator method, of class KDTree.
     */
    public void testIterator()
    {
        System.out.println("iterator");

        KDTree<Vector,Integer,DefaultPair<Vector,Integer>> tree =
            new KDTree<Vector, Integer, DefaultPair<Vector,Integer>>();
        Iterator<DefaultPair<Vector,Integer>> iterator = tree.iterator();
        assertFalse( iterator.hasNext() );
        try
        {
            iterator.next();
            fail( "No next" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        tree = this.createInstance();
        for( Pair<? extends Vector,?> pair : tree )
        {
            System.out.println( pair.getFirst() + " -> " + pair.getSecond() );
        }


        iterator = tree.iterator();
        assertTrue( iterator.hasNext() );
        assertSame( points.get(0), iterator.next() );
        assertSame( points.get(1), iterator.next() );
        assertSame( points.get(3), iterator.next() );
        assertSame( points.get(5), iterator.next() );
        assertSame( points.get(4), iterator.next() );
        assertSame( points.get(2), iterator.next() );

        assertFalse( iterator.hasNext() );

        try
        {
            iterator.next();
            fail( "Nothing left!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    private static class EuclideanDistanceMetric
        extends AbstractCloneableSerializable
        implements Metric<Vectorizable>
    {

        public static final EuclideanDistanceMetric INSTANCE = new EuclideanDistanceMetric();

        public static int NUM_EVALS = 0;

        public EuclideanDistanceMetric()
        {
        }

        public double evaluate(
            Vectorizable first,
            Vectorizable second)
        {
            NUM_EVALS++;
            Vector delta = first.convertToVector().minus( second.convertToVector() );
            return delta.norm2();
        }

        

    }

    /**
     * findNearest
     */
    public void testFindNearest()
    {
        System.out.println( "findNearest" );

        KDTree<Vector,Integer,DefaultPair<Vector,Integer>> tree = this.createInstance();

        EuclideanDistanceMetric.NUM_EVALS = 0;
        Collection<? extends Pair<? extends Vector, Integer>> nearest =
            tree.findNearest(points.get(0).getFirst(), 1, EuclideanDistanceMetric.INSTANCE);

        assertEquals( 1, nearest.size() );
        System.out.println( "Evals: " + EuclideanDistanceMetric.NUM_EVALS );
        System.out.println( "Found: " + ObjectUtil.toString(CollectionUtil.getFirst(nearest)) );
        assertEquals( points.get(0).getFirst(), CollectionUtil.getFirst(nearest).getFirst() );

        EuclideanDistanceMetric.NUM_EVALS = 0;
        nearest = tree.findNearest(points.get(1).getFirst(), 1, EuclideanDistanceMetric.INSTANCE);
        assertEquals( 1, nearest.size() );
        System.out.println( "Evals: " + EuclideanDistanceMetric.NUM_EVALS );
        assertEquals( points.get(1).getFirst(), CollectionUtil.getFirst(nearest).getFirst() );

        EuclideanDistanceMetric.NUM_EVALS = 0;
        nearest = tree.findNearest(points.get(5).getFirst(), 2, EuclideanDistanceMetric.INSTANCE);
        System.out.println( "Evals: " + EuclideanDistanceMetric.NUM_EVALS );
        assertEquals( 2, nearest.size() );
        Iterator<? extends Pair<? extends Vector,Integer>> iterator = nearest.iterator();
        assertEquals( points.get(4).getFirst(), iterator.next().getFirst() );
        assertEquals( points.get(5).getFirst(), iterator.next().getFirst() );

        EuclideanDistanceMetric.NUM_EVALS = 0;
        nearest = tree.findNearest(points.get(3).getFirst(), 3, EuclideanDistanceMetric.INSTANCE);
        System.out.println( "Evals: " + EuclideanDistanceMetric.NUM_EVALS );
        for( Pair<? extends Vector,Integer> neighbor : nearest )
        {
            System.out.println( "Neighbor:\n" + ObjectUtil.toString(neighbor) );
        }
        assertEquals( 3, nearest.size() );
        iterator = nearest.iterator();
        assertEquals( points.get(0).getFirst(), iterator.next().getFirst() );
        assertEquals( points.get(3).getFirst(), iterator.next().getFirst() );
        assertEquals( points.get(1).getFirst(), iterator.next().getFirst() );

        assertSame( tree, tree.findNearest(points.get(0).getFirst(), tree.size(), EuclideanDistanceMetric.INSTANCE ) );

    }

    /**
     * Neighbor.equals
     */
    public void testNeighborEquals()
    {
        
        System.out.println( "Neighbor.equals" );

        KDTree.Neighborhood<Vector,Integer,DefaultPair<Vector,Integer>> neighborhood =
            new KDTree.Neighborhood<Vector, Integer, DefaultPair<Vector, Integer>>(1);

        KDTree.Neighborhood<Vector,Integer,DefaultPair<Vector,Integer>>.Neighbor<Vector,Integer,DefaultPair<Vector,Integer>> neighbor =
            neighborhood.new Neighbor<Vector,Integer,DefaultPair<Vector,Integer>>( points.get(0), RANDOM.nextDouble() );
        

        assertFalse( neighbor.equals( null ) );

        assertFalse( neighbor.equals( new Double( RANDOM.nextDouble() ) ) );
        
        assertFalse( neighbor.equals( points.get(0) ) );

        assertFalse( neighbor.equals( points.get(0).getFirst() ) );

        assertTrue( neighbor.equals(
            neighborhood.new Neighbor<Vector,Integer,DefaultPair<Vector,Integer>>( points.get(0), RANDOM.nextDouble() ) ) );

    }

    /**
     * Internal iterator
     */
    public void testInternalIterator()
    {

        System.out.println( "iterator" );

        KDTree tree = this.createInstance();
        Iterator iterator = tree.iterator();
        assertTrue( iterator.hasNext() );
        try
        {
            iterator.remove();
            fail( "Remove isn't implemented" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }


    public void testPathologicalExample()
    {

        System.out.println( "Pathological Example" );

        @SuppressWarnings("unchecked")
        List<DefaultPair<Vector,Integer>> pathological = Arrays.asList(
            DefaultPair.create( VectorFactory.getDefault().copyValues(0,0), 0 ),
            DefaultPair.create( VectorFactory.getDefault().copyValues(0,0), 1 ),
            DefaultPair.create( VectorFactory.getDefault().copyValues(0,0), 2 ),
            DefaultPair.create( VectorFactory.getDefault().copyValues(0,0), 3 ),
            DefaultPair.create( VectorFactory.getDefault().copyValues(0,0), 4 ),
            DefaultPair.create( VectorFactory.getDefault().copyValues(0,0), 5 )
        );

        KDTree<Vector,Integer,DefaultPair<Vector,Integer>> tree =
            KDTree.createBalanced(pathological);

        System.out.println( "Pathological Tree:\n" + tree );

        assertSame( pathological.get(3), tree.value );
        assertSame( pathological.get(4), tree.leftChild.value );
        assertSame( pathological.get(0), tree.leftChild.leftChild.value );
        assertSame( pathological.get(2), tree.leftChild.rightChild.value );
        assertSame( pathological.get(5), tree.rightChild.value );
        assertSame( pathological.get(1), tree.rightChild.leftChild.value );

        EuclideanDistanceMetric.NUM_EVALS = 0;
        Collection<? extends Pair<? extends Vector, Integer>> nearest =
            tree.findNearest(pathological.get(1).getFirst(), 1, EuclideanDistanceMetric.INSTANCE);

        System.out.println( "Evals: " + EuclideanDistanceMetric.NUM_EVALS );
        System.out.println( "1 Nearest:\n" + ObjectUtil.toString(nearest) );

        assertEquals( pathological.get(1).getFirst(),
            CollectionUtil.getFirst(nearest).getFirst() );

        EuclideanDistanceMetric.NUM_EVALS = 0;
        nearest = tree.findNearest(pathological.get(1).getFirst(), 3, EuclideanDistanceMetric.INSTANCE);
        System.out.println( "Evals: " + EuclideanDistanceMetric.NUM_EVALS );
        System.out.println( "3 Nearest:\n" + ObjectUtil.toString(nearest) );
        for( Pair<? extends Vector,Integer> neighbor : nearest )
        {
            assertEquals( pathological.get(0).getFirst(), neighbor.getFirst() );
        }

        nearest = tree.findNearest(pathological.get(1).getFirst(), pathological.size()+1, EuclideanDistanceMetric.INSTANCE);
        assertEquals( tree.size(), nearest.size() );
        for( Pair<? extends Vector,Integer> neighbor : nearest )
        {
            assertEquals( pathological.get(0).getFirst(), neighbor.getFirst() );
        }



    }

     public void testSelfLookup()
     {
         List<Vector> data = new ArrayList<Vector>();
         data.add(new Vector3(0.0, 1.0, 2.0));
         data.add(new Vector3(0.0, 1.1, 2.2));
         data.add(new Vector3(0.0, -1.0, -2.0));
         data.add(new Vector3(0.0, -1.1, -2.2));

         List<DefaultPair<Vector, Vector>> pairs = new
 ArrayList<DefaultPair<Vector, Vector>>(
             data.size());
         for (Vector item : data)
         {
             pairs.add(DefaultPair.create(item, item));
         }

         KDTree<Vector, Vector, DefaultPair<Vector, Vector>> tree =
 KDTree.createBalanced(pairs);

         int neighborCount = 3;
         for (Vector item : data)
         {
             if( item.getElement(2) == -2.2 )
             {
                 System.out.println( "Here!!" );
             }
             Collection<DefaultPair<Vector, Vector>> neighbors =
                 tree.findNearest(item, neighborCount,
 EuclideanDistanceMetric.INSTANCE);

             assertEquals(neighborCount, neighbors.size());

             System.out.println("Neighbors of " + item);
             boolean hasSelf = false;
             for (DefaultPair<?, ?> neighbor : neighbors)
             {
                 System.out.println(neighbor.getSecond());
                 hasSelf = hasSelf || neighbor.getSecond().equals(item);
             }
             assertTrue(hasSelf);
         }
     }



}
