/*
 * File:                NumberComparator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 2, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.collection;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Comparator;

/**
 * Compares two Numbers (base class of Double, Integer, etc.) for sorting.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class NumberComparator
    extends AbstractCloneableSerializable
    implements Comparator<Number>
{

    /**
     * Instance of the NumberComparator.
     */
    public static final NumberComparator INSTANCE = new NumberComparator();
    
    /** 
     * Creates a new instance of NumberComparator 
     */
    public NumberComparator()
    {
    }

    public int compare(
        Number o1,
        Number o2 )
    {
        double v1 = o1.doubleValue();
        double v2 = o2.doubleValue();
        if( v1 < v2 )
        {
            return -1;
        }
        else if( v1 > v2 )
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }
    
}
