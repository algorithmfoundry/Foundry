/*
 * File:                DefaultTriple.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright September 26, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * The {@code DefaultTriple} class implements a simple structure for a triple
 * of three objects, potentially of different types.
 * 
 * @param   <FirstType> The type of the first object in the triple.
 * @param   <SecondType> The type of second object in the triple.
 * @param   <ThirdType> The type of the third object in the triple.
 * @author  Justin Basilico
 * @since   2.1
 */
public class DefaultTriple<FirstType, SecondType, ThirdType>
    extends AbstractCloneableSerializable
    implements Triple<FirstType, SecondType, ThirdType>
{
    
    /** The first object. */
    protected FirstType first;

    /** The second object. */
    protected SecondType second;

    /** The third type. */
    protected ThirdType third;
    
    /**
     * Creates a new instance of {@code DefaultTriple} with initial null values.
     */
    public DefaultTriple()
    {
        this(null, null, null);
    }

    /**
     * Creates a new instance of {@code DefaultTriple}.
     * 
     * @param   first The first object.
     * @param   second The second object.
     * @param   third The third object.
     */
    public DefaultTriple(
        final FirstType first,
        final SecondType second,
        final ThirdType third)
    {
        super();
        
        this.setFirst(first);
        this.setSecond(second);
        this.setThird(third);
    }
    
    @Override
    public DefaultTriple<FirstType, SecondType, ThirdType> clone()
    {
        @SuppressWarnings("unchecked")
        final DefaultTriple<FirstType, SecondType, ThirdType> result = 
            (DefaultTriple<FirstType, SecondType, ThirdType>) super.clone();
        result.first = ObjectUtil.cloneSmart(result.first);
        result.second = ObjectUtil.cloneSmart(result.second);
        result.third = ObjectUtil.cloneSmart(result.third);
        return result;
    }

    public FirstType getFirst()
    {
        return this.first;
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

    public SecondType getSecond()
    {
        return this.second;
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

    public ThirdType getThird()
    {
        return this.third;
    }
    
    /**
     * Sets the third object. 
     *
     * @param   third The new value for the third object.
     */
    public void setThird(
        final ThirdType third)
    {
        this.third = third;
    }
    
    /**
     * Takes three collections of data of the same size and creates a new single
     * {@code ArrayList<DefaultTriple>} out of their elements.
     * 
     * @param   <FirstType> 
     *          The type of the first element.
     * @param   <SecondType>
     *          The type of the second element.
     * @param   <ThirdType>
     *          The type of the third element.
     * @param   firsts A collection of data to transform into the first 
     *          element of the triple. Must have the same size as seconds and 
     *          thirds.
     * @param   seconds A collection of data to transform into the second 
     *          element of the triple. Must have the same size as firsts and
     *          thirds.
     * @param   thirds A collection of data to transform into the third 
     *          element of the triple. Must have the same size as firsts and
     *          seconds.
     * @return  A new array list of triples of the same time as the first, 
     *          second, and third collections.
     */
    public static <FirstType, SecondType, ThirdType> 
    ArrayList<DefaultTriple<FirstType, SecondType, ThirdType>> 
    mergeCollections(
        final Collection<FirstType> firsts,
        final Collection<SecondType> seconds,
        final Collection<ThirdType> thirds)
    {
        // Ensure that the two collections are of the same size.
        final int count = firsts.size();
        if (count != seconds.size() || count != thirds.size())
        {
            throw new IllegalArgumentException(
                "Collections are not the same size.");
        }

        // Create the result list.
        final ArrayList<DefaultTriple<FirstType, SecondType, ThirdType>> result 
            = new ArrayList<DefaultTriple<FirstType, SecondType, ThirdType>>(
                count);
        final Iterator<SecondType> i2 = seconds.iterator();
        final Iterator<ThirdType> i3 = thirds.iterator();
        for (FirstType d1 : firsts)
        {
            result.add(new DefaultTriple<FirstType, SecondType, ThirdType>(
                d1, i2.next(), i3.next()));
        }

        return result;
    }
    
}
