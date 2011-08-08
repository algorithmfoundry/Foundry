/*
 * File:                Strings.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 23, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.util;

/**
 * The {@code Strings} class implements static utility methods for dealing with
 * {@code String} objects.
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class StringUtil
    extends Object
{

    /**
     * Returns true if the given String is null or empty.
     *
     * @param  s A string.
     * @return True if the given String is null or empty.
     */
    public static boolean isEmpty(
        final String s)
    {
        return (s == null) || (s.length() <= 0);
    }

    /**
     * Returns true if the given String is null or all whitespace.
     *
     * @param  s A string.
     * @return True if the given String is null or all whitespace.
     */
    public static boolean isWhitespace(
        final String s)
    {
        // Trim is used to remove whitespace from the String.
        return (s == null) || (s.trim().length() <= 0);
    }
    
    /**
     * Capitalizes the first character of the given string.
     * 
     * @param   s The string to capitalize.
     * @return  The given string with the first character capitalized.
     */
    public static String capitalizeFirstCharacter(
        final String s)
    {
        if (s == null)
        {
            return null;
        }
        else if (s.length() <= 0)
        {
            return "";
        }
        else
        {
            return s.substring(0, 1).toUpperCase() + s.substring(1);
        }
    }

}
