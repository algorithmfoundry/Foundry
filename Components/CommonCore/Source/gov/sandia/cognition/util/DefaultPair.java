/*
 * File:                DefaultPair.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 22, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.util;

import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.annotation.CodeReview;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * The <code>DefaultPair</code> class implements a simple structure for a pair 
 * of two objects, potentially of different types.
 *
 * @param   <FirstType>
 *      Type of the first object in the pair.
 * @param   <SecondType>
 *      Type of the second object in the pair.
 * @author  Justin Basilico
 * @since   2.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-10-02",
            changesNeeded=false,
            comments={
                "Moved previous code reviews to CodeReview annotations.",
                "Otherwise, still looks fine."
            }
        )
        ,
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2007-11-25",
            changesNeeded=false,
            comments="Still looks fine."
        )
        ,
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2006-07-18",
            changesNeeded=false,
            comments="Looks fine."
        )
    }
)
public class DefaultPair<FirstType, SecondType>
    extends AbstractCloneableSerializable
    implements Pair<FirstType, SecondType>
{

    /** The first object. */
    protected FirstType first;

    /** The second object. */
    protected SecondType second;

    /**
     * Creates a new instance of DefaultPair with no members.
     */
    public DefaultPair()
    {
        this(null, null);
    }

    /**
     * Creates a new instance of DefaultPair.
     *
     * @param first The first object.
     * @param second The second object.
     */
    public DefaultPair(
        final FirstType first,
        final SecondType second)
    {
        super();

        this.first = first;
        this.second = second;
    }

    /**
     * Copy constructor.
     * @param other Pair to copy.
     */
    public DefaultPair(
        final Pair<? extends FirstType,? extends SecondType> other )
    {
        this.setFirst(other.getFirst());
        this.setSecond(other.getSecond());
    }

    @Override
    public DefaultPair<FirstType, SecondType> clone()
    {
        @SuppressWarnings("unchecked")
        final DefaultPair<FirstType, SecondType> result = 
            (DefaultPair<FirstType, SecondType>) super.clone();
        result.first = ObjectUtil.cloneSmart(result.first);
        result.second = ObjectUtil.cloneSmart(result.second);
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(
        final Object other)
    {
        return other instanceof Pair 
            && this.equals((Pair<FirstType, SecondType>) other);
    }

    public boolean equals(
        final Pair<FirstType, SecondType> other)
    {
        return other != null
            && ObjectUtil.equalsSafe(this.getFirst(),  other.getFirst())
            && ObjectUtil.equalsSafe(this.getSecond(), other.getSecond());
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 23 * hash + ObjectUtil.hashCodeSafe(this.getFirst());
        hash = 23 * hash + ObjectUtil.hashCodeSafe(this.getSecond());
        return hash;
    }

    /**
     * Creates a new, empty {@code DefaultPair} with both values being null.
     *
     * @param   <FirstType>
     *      Type of the first object in the pair.
     * @param   <SecondType>
     *      Type of the second object in the pair.
     * @return
     *      A new, empty {@code DefaultPair}.
     */
    public static <FirstType, SecondType> DefaultPair<FirstType, SecondType> create()
    {
        return new DefaultPair<FirstType, SecondType>();
    }
    
    /**
     * Creates a new {@code DefaultPair} from the given values.
     *
     * @param   <FirstType>
     *      Type of the first object in the pair.
     * @param   <SecondType>
     *      Type of the second object in the pair.
     * @param   first
     *      The first value.
     * @param   second
     *      The second value.
     * @return
     *      A new DefaultPair containing the two values.
     */
    public static <FirstType, SecondType> DefaultPair<FirstType, SecondType> create(
        final FirstType first,
        final SecondType second)
    {
        return new DefaultPair<FirstType, SecondType>(first, second);
    }

    /**
     * Gets the first object.
     *
     * @return The first object.
     */
    public FirstType getFirst()
    {
        return this.first;
    }

    /**
     * Gets the second object.
     *
     * @return The second object.
     */
    public SecondType getSecond()
    {
        return this.second;
    }

    /**
     * Sets the first object. 
     *
     * @param first The new value for the first object.
     */
    public void setFirst(
        final FirstType first)
    {
        this.first = first;
    }

    /**
     * Sets the second object. 
     *
     * @param second The new value for the second object.
     */
    public void setSecond(
        final SecondType second)
    {
        this.second = second;
    }

    /**
     * Takes two collections of data of the same size and creates a new single
     * {@code ArrayList<DefaultPair>} out of their elements.
     * 
     * @param   <FirstType> 
     *          The type of the first element.
     * @param   <SecondType>
     *          The type of the second element.
     * @param   firsts A collection of the data to transform into the first 
     *          element of the pair. Must have the same size as seconds.
     * @param   seconds A collection of the data to transform into the second 
     *          element of the pair. Must have the same size as firsts
     * @return  A new array list of pairs of the same time as the first and
     *          second collections.
     */
    public static <FirstType, SecondType> ArrayList<DefaultPair<FirstType, SecondType>> mergeCollections(
        final Collection<FirstType> firsts,
        final Collection<SecondType> seconds)
    {
        // Ensure that the two collections are of the same size.
        int count = firsts.size();
        if (count != seconds.size())
        {
            throw new IllegalArgumentException(
                "Collections are not the same size.");
        }

        // Create the result list.
        ArrayList<DefaultPair<FirstType, SecondType>> result =
            new ArrayList<DefaultPair<FirstType, SecondType>>(count);
        Iterator<SecondType> i2 = seconds.iterator();
        for (FirstType d1 : firsts)
        {
            result.add(new DefaultPair<FirstType, SecondType>(d1, i2.next()));
        }

        return result;
    }

}
