/*
 * File:                DefaultComparator.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright September 30, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.collection;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Comparator;

/**
 * A default comparator that just calls compare on the comparable generic
 * it uses. Thus, it creates the natural ordering according to the compareTo
 * method on the {@code Comparable} objects it is given.
 * 
 * @param   <T> The type being compared. Must be {@code Comparable}.
 * @author  Justin Basilico
 * @since   2.1
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-12-02",
    changesNeeded=false,
    comments="Looks good."
)
public class DefaultComparator<T extends Comparable<? super T>>
    extends AbstractCloneableSerializable
    implements Comparator<T>
{
    
    /**
     * Creates a new {@code DefaultComparator}.
     */
    public DefaultComparator()
    {
        super();
    }
    
    public int compare(
        final T o1, 
        final T o2)
    {
        return o1.compareTo(o2);
    }
    
}
