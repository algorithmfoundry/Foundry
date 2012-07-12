/*
 * File:                KDTree.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 28, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.geometry;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.math.Metric;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.VectorizableIndexComparator;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.CloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.Pair;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Implementation of a kd-tree.  Every node in a KDTree has a k-dimensional
 * Vectorizable point and an associated value as a generic "DataType."  The
 * At each depth in the KDTree, the KDTree partitions the Vectorizables into
 * two sets along a particular dimension according to the Vectorizable stored
 * in the node value.  The dimension used to partition at a particular depth
 * is (depth % k).  Vectorizables with values less than or equal to the
 * node-value-dimension are placed into the left subtree, and those greater
 * than the node-value-dimension are stored into the right subtree.  This
 * makes average-case nearest-neighbor lookup into a balanced KDTree with "N"
 * points of O(log(N)), rather than the typical "N" time for linear search.
 * Construction of a balanced KDTree with N points takes average-case
 * O(N log(N)).
 *
 * @param <VectorType> Type of Vectorizable, the first values
 * @param <DataType> Type of data in the Pair, the second values
 * @param <PairType> Type of Pair to use in the KDTree.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Andrew W. Moore",
            title="An intoductory tutorial on kd-trees",
            type=PublicationType.TechnicalReport,
            publication="University of Cambridge Computer Laboratory Technical Report No. 209",
            year=1991,
            url="http://www.autonlab.org/autonweb/14665.html?branch=1&language=2"
        )
        ,
        @PublicationReference(
            author="Wikipedia",
            title="kd-tree",
            type=PublicationType.WebPage,
            year=2009,
            url="http://en.wikipedia.org/wiki/Kd-tree"
        )
    }
)
public class KDTree<VectorType extends Vectorizable,DataType,PairType extends Pair<? extends VectorType,DataType>>
    extends AbstractCollection<PairType>
    implements CloneableSerializable
{

    /**
     * Number of elements in this subtree.
     */
    protected int num;

    /**
     * VectorType,DataType value for this node of the subtree.
     */
    protected PairType value;

    /**
     * Parent of this node of the subtree.
     */
    protected KDTree<VectorType,DataType,PairType> parent;

    /**
     * Left child of this subtree
     */
    protected KDTree<VectorType,DataType,PairType> leftChild;

    /**
     * Right child of this subtree.
     */
    protected KDTree<VectorType,DataType,PairType> rightChild;

    /**
     * Comparator of this node to determine less than, greater than, or equality.
     */
    protected PairFirstVectorizableIndexComparator comparator;

    /**
     * Default constructor
     */
    public KDTree()
    {
        this( null, null, null );
        this.num = 0;
    }

    /**
     * Creates a balanced KDTree from the given points.
     * @param points
     * Points to load into the KDTree.
     */
    public KDTree(
        Collection<? extends PairType> points )
    {
        this( CollectionUtil.asArrayList(points),
            new PairFirstVectorizableIndexComparator( 0 ),
            CollectionUtil.getFirst(points).getFirst().convertToVector().getDimensionality(),
            null );
    }

    /**
     * Creates a KDTree subtree for recursion purposes.
     * @param value
     * Value of the head of the subtree.
     * @param comparator
     * Comparator to use for the Vectorizables.
     * @param parent
     * Parent node of this subtree.
     */
    protected KDTree(
        PairType value,
        PairFirstVectorizableIndexComparator comparator,
        KDTree<VectorType,DataType,PairType> parent )
    {
        this.num = 1;
        this.value = value;
        this.comparator = comparator;
        this.parent = parent;
        this.leftChild = null;
        this.rightChild = null;
    }

    /**
     * Creates a balanced KDTree subtree for recursion purposes from the given
     * ArrayList of points.
     * This is an O(n log n) operation for "n" points because we use a clever
     * linear-time kth selection algorithm in CollectionUtil.findKthLargest().
     * @param points
     * Points to load into the subtree.
     * @param dimensionality
     * Dimensionality of the Vectorizables.
     * @param comparator
     * Comparator to use for the Vectorizables.
     * @param parent
     * Parent node of this subtree.
     */
    protected KDTree(
        ArrayList<? extends PairType> points,
        PairFirstVectorizableIndexComparator comparator,
        int dimensionality,
        KDTree<VectorType,DataType,PairType> parent )
    {

        this.parent = parent;
        this.comparator = comparator;
        this.num = points.size();
        if( num <= 0 )
        {
            throw new IllegalArgumentException( "No points!" );
        }
        else if( num == 1 )
        {
            this.value = points.get(0);
        }
        else
        {
            final int medianIndex = this.num / 2;
            int[] indices = CollectionUtil.findKthLargest(
                medianIndex, points, comparator );

            // This is the median of the axis.
            this.value = points.get( indices[medianIndex] );

            final int childAxis = (this.comparator.comparator.getIndex()+1) % dimensionality;
            PairFirstVectorizableIndexComparator childComparator =
                new PairFirstVectorizableIndexComparator( childAxis );

            // Left child recursion
            final int leftNum = medianIndex;
            if( leftNum > 0 )
            {
                ArrayList<PairType> leftPoints =
                    new ArrayList<PairType>( leftNum );
                for( int i = 0; i < leftNum; i++ )
                {
                    leftPoints.add( points.get( indices[i] ) );
                }
                this.leftChild = new KDTree<VectorType,DataType,PairType>(
                    leftPoints, childComparator, dimensionality, this );
            }

            // Right child recursion
            final int rightNum = num-medianIndex-1;
            if( rightNum > 0 )
            {
                ArrayList<PairType> rightPoints =
                    new ArrayList<PairType>( rightNum );
                for( int i = medianIndex+1; i < this.num; i++ )
                {
                    rightPoints.add( points.get( indices[i] ) );
                }
                this.rightChild = new KDTree<VectorType,DataType,PairType>(
                    rightPoints, childComparator, dimensionality, this );
            }

        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public KDTree<VectorType,DataType,PairType> clone()
    {
        KDTree<VectorType,DataType,PairType> clone;
        try
        {
            clone = (KDTree<VectorType,DataType,PairType>) super.clone();
            clone.leftChild = ObjectUtil.cloneSafe(this.leftChild);
            clone.rightChild = ObjectUtil.cloneSafe(this.rightChild);
            clone.value = ObjectUtil.cloneSmart(this.value);
            clone.parent = this.parent;
            clone.comparator = this.comparator;
        }
        catch (CloneNotSupportedException ex)
        {
            clone = null;
        }

        return clone;

    }

    /**
     * Creates a balanced KDTree based on the given collection of Pairs.
     * This is an O(n log n) operation for "n" points because we use a clever
     * linear-time kth selection algorithm in CollectionUtil.findKthLargest().
     *
     * @param <VectorType> Type of Vectorizable, the first values.
     * @param <DataType> Type of data in the Pair, the second values.
     * @param <PairType> Type of Pair to use in the KDTree.
     * @param points Points to load into the tree.
     * @return
     * Balanced KDTree that contains all the given points.
     */
    public static <VectorType extends Vectorizable,DataType,PairType extends Pair<? extends VectorType,DataType>>
            KDTree<VectorType,DataType,PairType> createBalanced(
                Collection<? extends PairType> points )
    {
        return new KDTree<VectorType,DataType,PairType>( points );
    }

    /**
     * Rebalances the KDTree.  Does not modify this KDTree.
     * @return
     * Balanced representation of this KDTree.
     */
    public KDTree<VectorType,DataType,PairType> reblanace()
    {
        return createBalanced( this );
    }

    @Override
    public boolean add(
        PairType point )
    {

        if( this.value == null )
        {
            this.num = 1;
            this.value = point;
            this.comparator = new PairFirstVectorizableIndexComparator(0);
        }
        else
        {
            this.num++;
            int comparison = this.comparator.compare(point,this.value);
            if( comparison <= 0 )
            {
                if( this.leftChild == null )
                {
                    int dimension = point.getFirst().convertToVector().getDimensionality();
                    int childAxis = (this.comparator.comparator.getIndex() + 1) % dimension;
                    PairFirstVectorizableIndexComparator childComparator =
                        new PairFirstVectorizableIndexComparator( childAxis );
                    this.leftChild = new KDTree<VectorType,DataType,PairType>(
                        point, childComparator, this );
                }
                else
                {
                    this.leftChild.add( point );
                }
            }
            else
            {
                if( this.rightChild == null )
                {
                    int dimension = point.getFirst().convertToVector().getDimensionality();
                    int childAxis = (this.comparator.comparator.getIndex() + 1) % dimension;
                    PairFirstVectorizableIndexComparator childComparator =
                        new PairFirstVectorizableIndexComparator( childAxis );
                    this.rightChild = new KDTree<VectorType,DataType,PairType>(
                        point, childComparator, this );
                }
                else
                {
                    this.rightChild.add( point );
                }
            }
        }

        return true;

    }

    @Override
    public int size()
    {
        return this.num;
    }

    /**
     * Iterates through the KDTree using "inorder", also known as "symmetric
     * traversal", of the tree.  That is, the recursion proceeds as
     * traverse the left subtree, visit the node, traverse the right subtree.
     * @return
     * Inorder iterator of the KDTree.
     */
    @PublicationReference(
        author="Wikipedia",
        title="Tree traversal",
        type=PublicationType.WebPage,
        year=2009,
        url="http://en.wikipedia.org/wiki/Tree_traversal#Traversal"
    )
    @Override
    public Iterator<PairType> iterator()
    {
        return new InOrderKDTreeIterator<VectorType,DataType,PairType>( this );
    }

    @Override
    public String toString()
    {
        return this.toString( "Head->" );
    }

    /**
     * Recursively prints out the tree "inorder" by printing out the left
     * subtree, then the node, then the right subtree.
     * @param prefix
     * Prefix to tack onto the recursion values.
     * @return
     * String representation of the KDTree.
     */
    protected String toString(
        String prefix )
    {

        String retval = prefix + " (" + this.value.getFirst() + " -> " + this.value.getSecond() + ")\n";

        if( this.leftChild != null )
        {
            retval += this.leftChild.toString( prefix + "L" );
        }

        if( this.rightChild != null )
        {
            retval += this.rightChild.toString( prefix + "R" );
        }

        return retval;

    }

    /**
     * Finds the "num" nearest neighbors to the given "key" stored in the
     * KDTree.
     * @param key
     * Vector to find the nearest neighbors of.
     * @param k
     * Number of neighbors to find.
     * @param metric
     * Metric to use to evaluate the nearness of other points.
     * @return
     * Collection of nearest points to the "key" query.  If "num" is greater
     * than or equal to the number of points in the KDTRee, then the KDTree
     * is returned.
     */
    public Collection<PairType> findNearest(
        VectorType key,
        int k,
        Metric<? super VectorType> metric )
    {

        if( k < this.size() )
        {
            Neighborhood<VectorType,DataType,PairType> neighborhood =
                new Neighborhood<VectorType, DataType, PairType>( k );
            this.findNearest(key, k, neighborhood, metric );
            return neighborhood;
        }
        else
        {
            return this;
        }
        
    }

    /**
     * Finds the "num" nearest neighbors to the given "key" stored in the
     * KDTree.
     * @param key
     * Vector to find the nearest neighbors of.
     * @param k
     * Number of neighbors to find.
     * @param neighborhood
     * PriorityQueue to store the current nearest neighbors.
     * @param metric
     * Metric to use to evaluate the nearness of other points.
     */
    protected void findNearest(
        VectorType key,
        int k,
        Neighborhood<VectorType,DataType,PairType> neighborhood,
        Metric<? super VectorType> metric )
    {

        KDTree<VectorType,DataType,PairType> closer = null;
        KDTree<VectorType,DataType,PairType> further = null;

        // If we've got children, then see which child is closer
        if( (this.leftChild != null) || (this.rightChild != null) )
        {
            int comparison = this.comparator.comparator.compare(
                key, this.value.getFirst() );

            if( comparison <= 0 )
            {
                closer = this.leftChild;
                further = this.rightChild;
            }
            else
            {
                closer = this.rightChild;
                further = this.leftChild;
            }

            // recurse into the closer subtree if it exists.
            if( closer != null )
            {
                closer.findNearest(key, k, neighborhood, metric);
            }

        }

        // If there's space in the queue, then add our value.
        if( !neighborhood.isFull() )
        {
            // Compute our distance to the key
            double distance = metric.evaluate( this.value.getFirst(), key );
            neighborhood.add( this.value, distance );

            // If there's still space, then recurse to the further tree.
            if( further != null )
            {
//                if( !neighborhood.isFull() )
                {
                    further.findNearest(key, k, neighborhood, metric);
//                    further.findNearest(key, num, neighborhood, metric);
                }
            }

        }

        // The queue is full, so we need to see if we're closer than
        // the furthest neighbor.
        else
        {

            // We need to see if it's possible that the BOTH us and the
            // further subtree could contain a better point than the furthest
            // neighbor so far.
            double minimumDistance = this.computeMinimumDifference(key);
            if( minimumDistance < neighborhood.getFurthestNeighborDistance() )
            {
                double distance = metric.evaluate( this.value.getFirst(), key );
                neighborhood.offer( this.value, distance );
                if( further != null )
                {
                    further.findNearest(key, num, neighborhood, metric);
                }

            }
        }

    }

    /**
     * Computes the minimum absolute difference between the given key and
     * the "first" value stored in this subtree for the index given by
     * the embedded comparator.  That is, the minimum distance
     * "this is done by intersecting the splitting hyperplane with a hypersphere
     * around the search node [key] that has a radius equal to the current
     * nearest distance. Since the hyperplanes are all axis-aligned this is
     * implemented as a simple comparison to see whether the difference between
     * the splitting coordinate and the search point is less than the distance
     * from the search point to the current best."
     * @param key
     * Vector to compare against.
     * @return
     * Minimum absolute difference for the given index between the key and
     * the first value stored in this subtree.
     */
    protected double computeMinimumDifference(
        VectorType key )
    {
        int index = this.comparator.comparator.getIndex();
        double delta = key.convertToVector().getElement(index) -
            this.value.getFirst().convertToVector().getElement(index);
        return Math.abs( delta );
    }


    /**
     * Comparator for Pairs that have a Vectorizable as its first parameter.
     */
    protected static class PairFirstVectorizableIndexComparator
        extends AbstractCloneableSerializable
        implements Comparator<Pair<? extends Vectorizable,?>>
    {

        /**
         * Embedded comparator for the Vectorizable argument.
         */
        public VectorizableIndexComparator comparator;

        /**
         * Creates a new instance of PairFirstVectorizableIndexComparator
         * @param index
         * Index of the Vectorizable to compare against.
         */
        public PairFirstVectorizableIndexComparator(
            int index )
        {
            this.comparator = new VectorizableIndexComparator( index );
        }

        public int compare(
            Pair<? extends Vectorizable, ?> o1,
            Pair<? extends Vectorizable, ?> o2 )
        {
            return this.comparator.compare( o1.getFirst(), o2.getFirst() );
        }
        
    }

    /**
     * Iterates through the KDTree using "inorder", also known as "symmetric
     * traversal", of the tree.  That is, the recursion proceeds as
     * traverse the left subtree, visit the node, traverse the right subtree.
     * @param <VectorType> Type of Vectorizable, the first values
     * @param <DataType> Type of data in the Pair, the second values
     * @param <PairType> Type of Pair to use in the KDTree.
     */
    @PublicationReference(
        author="Wikipedia",
        title="Tree traversal",
        type=PublicationType.WebPage,
        year=2009,
        url="http://en.wikipedia.org/wiki/Tree_traversal#Traversal"
    )
    protected static class InOrderKDTreeIterator<VectorType extends Vectorizable,DataType,PairType extends Pair<? extends VectorType,DataType>>
        implements Iterator<PairType>
    {

        /**
         * Value of the node
         */
        public PairType nodeValue;

        /**
         * Iterator for the left subtree.
         */
        public InOrderKDTreeIterator<VectorType,DataType,PairType> leftIterator;

        /**
         * Iterator for the right subtree.
         */
        public InOrderKDTreeIterator<VectorType,DataType,PairType> rightIterator;

        /**
         * Creates a new instance of InOrderKDTreeIterator
         * @param node
         * Node from which to iterate.
         */
        public InOrderKDTreeIterator(
            KDTree<VectorType,DataType,PairType> node )
        {
            if( node.leftChild != null )
            {
                this.leftIterator = new InOrderKDTreeIterator<VectorType,DataType,PairType>( node.leftChild );
            }
            if( node.rightChild != null )
            {
                this.rightIterator = new InOrderKDTreeIterator<VectorType,DataType,PairType>( node.rightChild );
            }

            this.nodeValue = node.value;

        }

        public boolean hasNext()
        {
            return (this.nodeValue != null) ||
                ((this.rightIterator != null) && (this.rightIterator.hasNext())) ||
                ((this.leftIterator != null) && (this.leftIterator.hasNext()));
        }

        public PairType next()
        {

            PairType retval = null;

            if( (this.leftIterator != null)
                && this.leftIterator.hasNext() )
            {
                retval = this.leftIterator.next();
            }
            else
            {
                this.leftIterator = null;

                if( this.nodeValue != null )
                {
                    retval = this.nodeValue;
                    this.nodeValue = null;
                }
                else if( this.rightIterator != null )
                {
                    if( this.rightIterator.hasNext() )
                    {
                        retval = this.rightIterator.next();
                    }
                    else
                    {
                        this.rightIterator = null;
                    }
                }
            }

            if( retval == null )
            {
                throw new IllegalArgumentException(
                    "Should not have called null since we have no values to iterate!" );
            }

            return retval;

        }

        public void remove()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }


    /**
     * A Collection of nearby pairs.
     * @param <VectorType> Type of Vectorizable.
     * @param <DataType> Type of output data.
     * @param <PairType> Type of Pair.
     */
    protected static class Neighborhood<VectorType extends Vectorizable, DataType, PairType extends Pair<? extends VectorType,DataType>>
        extends AbstractCollection<PairType>
    {

        /**
         * Maximum number of Neighbors in the Neighborhood.
         */
        private int k;

        /**
         * PriorityQueue to store the neighbors.
         */
        PriorityQueue<Neighbor<VectorType,DataType,PairType>> priorityQueue;

        /**
         * Creates a new Neighborhood.
         * @param k
         * Maximum number of Neighbors in the Neighborhood.
         */
        public Neighborhood(
            int k )
        {
            this.priorityQueue =
                new PriorityQueue<Neighbor<VectorType, DataType, PairType>>(k);
            this.k = k;
        }

        /**
         * Returns true if the Neighborhood is full.
         * @return
         * True if the Neighborhood is full, false if not full.
         */
        public boolean isFull()
        {
            return this.size() >= this.k;
        }

        /**
         * Returns the distance of the furthest Neighbor.
         * @return
         * Distance of the furthest Neighbor.
         */
        public double getFurthestNeighborDistance()
        {
            return this.priorityQueue.peek().distance;
        }

        /**
         * Adds the neighbor to the priority queue.
         * @param value
         * Value to add.
         * @param distance
         * Distance to associate with the neighbor to the queue.
         */
        public void add(
            PairType value,
            double distance )
        {

            while( this.isFull() )
            {
                this.priorityQueue.remove();
            }

            this.priorityQueue.add(
                new Neighbor<VectorType,DataType,PairType>(value,distance) );

        }

        /**
         * Offers the neighbor if there is space or it's closer than the
         * furthest neighbor.
         * @param value
         * Value of the neighbor.
         * @param distance
         * Distance to the key value.
         * @return
         * True if added, false if not added.
         */
        public boolean offer(
            PairType value,
            double distance )
        {

            // If we're full, then see if we're closer than the furthest
            // neighbor.
            if( this.isFull() )
            {
                if( distance < this.getFurthestNeighborDistance() )
                {
                    this.priorityQueue.remove();
                }
            }

            // If we find there is space, then add the new neighbor.
            if( !this.isFull() )
            {
                this.add( value, distance );
                return true;
            }
            else
            {
                return false;
            }

        }

        @Override
        public Iterator<PairType> iterator()
        {
            return new NeighborhoodIterator();
        }

        @Override
        public int size()
        {
            return this.priorityQueue.size();
        }        

        /**
         * Holds neighbor information used during the evaluate method and is put
         * into a priority queue.
         * @param <VectorType> Type of Vectorizable, the first values
         * @param <DataType> Type of data in the Pair, the second values
         * @param <PairType> Type of Pair to use in the KDTree.
         */
        protected class Neighbor<VectorType extends Vectorizable, DataType, PairType extends Pair<? extends VectorType,DataType>>
            extends AbstractCloneableSerializable
            implements Comparable<Neighbor<VectorType, DataType, PairType>>
        {
            // Note: This class does not follow the get/set pattern in order to
            // make it as fast as possible, because it is used within the evaluate
            // method. Also, its a private internal class, so no one else should
            // use it.

            /**
             * Pair to store.
             */
            PairType pair;

            /**
             * Distance associated with this value.
             */
            private double distance;

            /**
             * Creates a new neighbor.
             *
             * @param   value
             *      The value associated with the neighbor.
             * @param distance
             * Distance associated with this value.
             */
            public Neighbor(
                final PairType value,
                final double distance)
            {
                this.pair = value;
                this.distance = distance;
            }

            public int compareTo(
                final Neighbor<VectorType, DataType, PairType> other)
            {
                // We reverse the comparison so that the item at the head of the
                // priority queue is the furthest neighbor
                return -Double.compare(this.distance, other.distance);
            }

            @Override
            @SuppressWarnings("unchecked")
            public boolean equals(
                Object obj)
            {

                if( obj == null )
                {
                    return false;
                }
                else if( obj instanceof Neighbor )
                {
                    return ((Neighbor<?,?,?>) obj).pair.getFirst().convertToVector().equals(
                        this.pair.getFirst().convertToVector() );
                }
                else
                {
                    return false;
                }

            }

        }


        /**
         * Iterator for the Neighborhood.
         */
        protected class NeighborhoodIterator
            implements Iterator<PairType>
        {

            /**
             * PriorityQueue iterator.
             */
            Iterator<Neighbor<VectorType,DataType,PairType>> priorityQueueIterator;

            /**
             * Default constructor.
             */
            public NeighborhoodIterator()
            {
                this.priorityQueueIterator = priorityQueue.iterator();
            }

            public boolean hasNext()
            {
                return this.priorityQueueIterator.hasNext();
            }

            public PairType next()
            {
                Neighbor<VectorType,DataType,PairType> next =
                    this.priorityQueueIterator.next();
                return next.pair;
            }

            public void remove()
            {
                throw new UnsupportedOperationException("Not supported yet.");
            }

        }

    }

}
