/*
 * File:                AbstractFileSerializationHandler.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright October 16, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.io.serialization;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.io.File;
import java.io.IOException;

/**
 * An abstract implementation of {@code FileSerializationHandler}. Takes care
 * of converting a file name to a File object.
 *
 * @param   <SerializedType>
 *      The type of object that can be serialized.
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractFileSerializationHandler<SerializedType>
    extends AbstractCloneableSerializable
    implements FileSerializationHandler<SerializedType>
{
    /**
     * Creates a new {@code AbstractFileSerializationHandler}.
     */
    public AbstractFileSerializationHandler()
    {
        super();
    }

    public void writeToFile(
        final String fileName,
        final SerializedType object)
        throws IOException
    {
        // Turn the file name into a file.
        this.writeToFile(new File(fileName), object);
    }


    public Object readFromFile(
        final String fileName)
        throws IOException, ClassNotFoundException
    {
        // Turn the file name into a file.
        return this.readFromFile(new File(fileName));
    }

}
