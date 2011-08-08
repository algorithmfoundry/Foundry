/*
 * File:                VectorizableIndexComparator.java
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

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Comparator;

/**
 * Compares the given index of two Vectorizables.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class VectorizableIndexComparator 
    extends AbstractCloneableSerializable
    implements Comparator<Vectorizable>
{

    /**
     * Index to compare against.
     */
    private int index;

    /** 
     * Creates a new instance of VectorizableIndexComparator 
     */
    public VectorizableIndexComparator()
    {
        this( 0 );
    }

    /**
     * Creates a new instance of VectorizableIndexComparator
     * @param index
     * Index to compare against.
     */
    public VectorizableIndexComparator(
        int index )
    {
        this.setIndex(index);
    }

    @Override
    public VectorizableIndexComparator clone()
    {
        return (VectorizableIndexComparator) super.clone();
    }

    public int compare(
        Vectorizable o1,
        Vectorizable o2)
    {
        final int i = this.getIndex();
        final double v1 = o1.convertToVector().getElement( i );
        final double v2 = o2.convertToVector().getElement( i );
        return Double.compare(v1, v2);
    }

    /**
     * Getter for index.
     * @return
     * Index to compare against.
     */
    public int getIndex()
    {
        return this.index;
    }

    /**
     * Setter for index.
     * @param index
     * Index to compare against.
     */
    public void setIndex(
        int index)
    {
        this.index = index;
    }
    
}
