/*
 * File:                OccurrenceInText.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 19, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text;

/**
 * An interface for a marker that some data occurred in some span of text.
 *
 * @param   <DataType> The type of the data that occurred.
 * @author  Justin Basilico
 * @since   3.0
 */
public interface OccurrenceInText<DataType>
{
    /**
     * Gets the data that occurred in the text.
     *
     * @return
     *      The data that occurred.
     */
    public DataType getData();

    /**
     * Gets the starting index of the occurrence in the text.
     *
     * @return
     *      The starting index of the occurrence in the text.
     */
    public int getStart();

    /**
     * Gets the length of text where the occurrence appears.
     *
     * @return
     *      The length of text where the occurrence appears.
     */
    public int getLength();
}
