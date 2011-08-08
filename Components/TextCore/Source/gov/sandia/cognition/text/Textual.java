/*
 * File:                Textual.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Text Core
 * 
 * Copyright February 01, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.text;

import java.io.Reader;

/**
 * Interface for a class that contains text. Specifies methods for getting
 * the text as a String or as a Reader object.
 * 
 * @author  Justin Basilico
 * @since   3.1
 */
public interface Textual
{

    /**
     * Get the text part of this object.
     *
     * @return
     *      The text part of this object.
     */
    public String getText();

    /**
     * Returns a new text reader for the text in this object.
     *
     * @return
     *      A new reader to read the text.
     */
    public Reader readText();

}
