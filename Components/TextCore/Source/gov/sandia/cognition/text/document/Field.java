/*
 * File:                Field.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 10, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.document;

import gov.sandia.cognition.text.Textual;
import java.io.Reader;

/**
 * Defines the interface for a field in the document. A field has a name plus
 * some textual content.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public interface Field
    extends Textual
{

    /**
     * Gets the name of the field.
     *
     * @return
     *      The name of the field.
     */
    public String getName();

    /**
     * Gets a new reader for the content of the field.
     *
     * @return
     *      A new reader for the content of the field.
     */
    public Reader readText();

    /**
     * Gets the full text of the field.
     *
     * @return
     *      The full text of the field.
     */
    public String getText();

}
