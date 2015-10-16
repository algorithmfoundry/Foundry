/*
 * File:                Combinations.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 11, 2012, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.math;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.CloneableSerializable;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Enumerates all the combinations on a given number of items sampled from a
 * larger set without considering order, that is, (1,2) is the same as (2,1).
 * For example, there are 3 ways to choose 2 objects from a set of 3:
 * (1,2), (1,3), (2,3).
 * @author Kevin R. Dixon
 * @since  3.4.2
 */
public class Combinations 
    extends AbstractSet<Vector>
    implements CloneableSerializable
{

    /**
     * Universe set size
     */
    private int N;
    
    /**
     * Number of objects to choose from the universe set.
     */
    private int k;


    /**
     * Creates a new instance of Combinations
     * @param N
     * Universe set size
     * @param k
     * Number of objects to choose from the universe set.
     */
    public Combinations(
        int N,
        int k )
    {
        ArgumentChecker.assertIsPositive("N", N );
        ArgumentChecker.assertIsNonNegative("k", k );
        ArgumentChecker.assertIsInRangeInclusive("0<=k<=N", k, 0, N);
        this.N = N;
        this.k = k;
    }

    @Override
    public Combinations clone()
    {
        try
        {
            return (Combinations) super.clone();
        }
        catch (CloneNotSupportedException ex)
        {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public Iterator<Vector> iterator()
    {
        return new IndexIterator(this.N, this.k);
    }
    
    @Override
    public int size()
    {
        return MathUtil.binomialCoefficient(N, k);
    }

    /**
     * Getter for N
     * @return
     * Universe set size
     */
    public int getN()
    {
        return this.N;
    }

    /**
     * Getter for k
     * @return the k
     * Number of objects to choose from the universe set.
     */
    public int getK()
    {
        return this.k;
    }

    /**
     * Partial implementation of a CombinationsIterator.
     * @param <IteratorType> 
     * Type of iterator given by the implementing subclass
     * @param <ClassType> 
     * Type of parameter returned by the next() method.
     */
    protected abstract static class AbstractCombinationsIterator<IteratorType extends AbstractCombinationsIterator<IteratorType,ClassType>, ClassType>
        extends AbstractCloneableSerializable
        implements Iterator<ClassType>
    {

        /**
         * Universe set size
         */
        protected int N;
        
        /**
         * Number of objects to choose from the universe set.
         */
        protected int k;
        
        /**
         * 
         */
        protected int size;

        /**
         * Index into the universal set.
         */
        protected int index;

        /**
         * Child iterator for recursion
         */
        protected IteratorType child;        
        
        /**
         * Creates a new instance of AbstractCombinationsIterator
         * @param N
         * Universe set size
         * @param k
         * Number of objects to choose from the universe set.
         */
        public AbstractCombinationsIterator(
            int N,
            int k )
        {
            ArgumentChecker.assertIsPositive("N", N);
            ArgumentChecker.assertIsNonNegative("k", k);
            ArgumentChecker.assertIsNonNegative("N-k", N-k);
            this.N = N;
            this.k = k;
            this.size = MathUtil.binomialCoefficient(N, k);
            this.index = 0;
        }

        @Override
        public boolean hasNext()
        {
            return this.index < this.size;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
    
    /**
     * Iterator that returns the index into a set: 0, 1, 2, ... N.
     */
    public class IndexIterator
        extends AbstractCombinationsIterator<IndexIterator, Vector>
    {
        
        /**
         * Value to return
         */
        private double value = -1.0;
        
        /**
         * Creates a new instance of IndexIterator
         * @param N
         * Universe set size
         * @param k
         */
        public IndexIterator(
            int N,
            int k)
        {
            super( N, k );
            this.value = 1.0;
            if( (k > 0) && (N > k) )
            {
                this.child = new IndexIterator(N-1, k-1);
            }
        }

        @Override
        public Vector next()
        {

            this.index++;
            Vector retval;
            if( this.k == 0 )
            {
                retval = VectorFactory.getDefault().createVector(this.N);
            }
            else if( this.k == this.N )
            {
                retval = VectorFactory.getDefault().createVector(this.N, 1.0);
            }
            else
            {
                Vector subvector;
                if( this.child.hasNext() )
                {
                    subvector = this.child.next();
                }
                else if( this.value == 1.0 )
                {
                    this.value = 0.0;
                    this.child = new IndexIterator(this.N-1, this.k);
                    subvector = this.child.next();
                }
                else
                {
                    throw new IllegalArgumentException( "Nothing left: index = " + this.index + ", size = " + this.size );
                }
                retval = VectorFactory.getDefault().createVector(this.N);
                retval.setElement(0, this.value);
                for( int i = 1; i < retval.getDimensionality(); i++ )
                {
                    retval.setElement(i, subvector.getElement(i-1));
                }
                                
            }
            
            return retval;
        }

    }

    /**
     * Creates a new instance of SubsetIterator, one that returns the elements
     * from a given set
     * @param <ClassType> 
     * Class type of the subset
     */
    public static class SubsetIterator<ClassType>
        extends AbstractCombinationsIterator<SubsetIterator<ClassType>, LinkedList<ClassType>>
    {

        /**
         * Universal set into which to index.
         */
        private ArrayList<ClassType> set;
        
        /**
         * Flag to include the current element
         */
        private boolean include;
        
        /**
         * Base index into the universal set from which we recursed
         */
        private int baseIndex;
        
        /**
         * Creates a new instance of SubsetIterator
         * @param N
         * Universe set size
         * @param k
         * Number of objects to choose from the universe set.
         * @param set
         * Universal set into which to index.
         */
        public SubsetIterator(
            int N,
            int k,
            ArrayList<ClassType> set )
        {
            this( N, k, set, 0 );
        }

        /**
         * Creates a new instance of SubsetIterator
         * @param N
         * Universe set size
         * @param k
         * Number of objects to choose from the universe set.
         * @param set
         * Universal set into which to index.
         * @param baseIndex
         * Base index into the universal set from which we recursed
         */
        private SubsetIterator(
            int N,
            int k,
            ArrayList<ClassType> set,
            int baseIndex )
        {
            super( N, k );
            this.include = true;
            this.baseIndex = baseIndex;
            this.set = set;
            if( (k > 0) && (N > k) )
            {
                this.child = new SubsetIterator<ClassType>(
                    this.N-1, this.k-1, this.set, this.baseIndex+1);
            }            
        }
        
        @Override
        public LinkedList<ClassType> next()
        {
            this.index++;
            LinkedList<ClassType> retval = new LinkedList<ClassType>();
            if( this.k == 0 )
            {
            }
            else if( this.k == this.N )
            {
                retval.addAll( this.set.subList(this.baseIndex,this.set.size()));
            }
            else
            {
                LinkedList<ClassType> sublist;
                if( this.child.hasNext() )
                {
                    sublist = this.child.next();
                }
                else if( this.include )
                {
                    this.include = false;
                    this.child = new SubsetIterator<ClassType>(
                        this.N-1, this.k, this.set, this.baseIndex+1);
                    sublist = this.child.next();
                }
                else
                {
                    throw new IllegalArgumentException( "Nothing left: index = " + this.index + ", size = " + this.size );
                }
                
                if( this.include )
                {
                    retval.add( this.set.get(this.baseIndex) );
                }
                
                retval.addAll( sublist );
            }
            
            return retval;
        }

    }
 
}    
