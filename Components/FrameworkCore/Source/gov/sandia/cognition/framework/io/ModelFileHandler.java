/*
 * File:                ModelIOHandler.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright July 24, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.io;

import gov.sandia.cognition.framework.CognitiveModel;
import gov.sandia.cognition.framework.CognitiveModelFactory;
import gov.sandia.cognition.io.CSVParseException;
import gov.sandia.cognition.io.FileUtil;
import gov.sandia.cognition.io.ObjectSerializationHandler;
import gov.sandia.cognition.io.XStreamSerializationHandler;
import java.io.File;
import java.io.IOException;

/**
 * The <code>ModelFileHandler</code> class is an entry point for reading files
 * that contain a <code>CognitiveModel</code> or 
 * <code>CognitiveModelFactory</code> objects. It handles the various file 
 * formats that the Framework supports.
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class ModelFileHandler
    extends Object
{
    /** The extension for binary serialized models. */
    public static final String BINARY_SERIALIZED_EXTENSION = "jcm";
    
    /** The extension for XML serialized models. */
    public static final String XML_SERIALIZED_EXTENSION = "xml";
    
    /** The extension for comma-separated-value models. */
    public static final String CSV_EXTENSION = "csv";
    
    /**
     * Creates a new instance of ModelIOHandler
     */
    protected ModelFileHandler()
    {
        super();
    }
    
    /**
     * Attempts to read a CognitiveModel from the given file name.
     *
     * @param  fileName The name of the file.
     * @return The CognitiveModel read from the file or null if it does not
     *         contain a model.
     * @throws IOException If there is an IOException when reading the model.
     */
    public static CognitiveModel readModel(
        String fileName)
        throws IOException
    {
        return readModel(new File(fileName));
    }
    
    /**
     * Attempts to read a CognitiveModel from the given file.
     *
     * @param  file The file.
     * @return The CognitiveModel read from the file.
     * @throws IOException If there is an IOException when reading the model
     *         or if the data contained in the file is not a model.
     */
    public static CognitiveModel readModel(
        File file)
        throws IOException
    {
        // Use the extension as a best guess.
        String extension = FileUtil.getExtension(file);
        
        if ( extension == null )
        {
            return readModelXMLSerialized(file);
        }
        else if ( extension.equalsIgnoreCase(XML_SERIALIZED_EXTENSION) )
        {
            return readModelXMLSerialized(file);
        }
        else if ( extension.equalsIgnoreCase(BINARY_SERIALIZED_EXTENSION) )
        {
            return readModelBinarySerialized(file);
        }
        else if ( extension.equalsIgnoreCase(CSV_EXTENSION) )
        {
            return readModelCSV(file);
        }
        else
        {
            return readModelXMLSerialized(file);
        }
    }
    
    /**
     * Attempts to cast the given Object to a CognitiveModel. If it is not
     * a CognitiveModel or CognitiveModelFactory it throws an exception. If
     * the object is a CognitiveModelFactory it creates a model from the 
     * factory and returns it.
     *
     * @param  o The Object to cast to a model.
     * @return A casted or created CognitiveModel.
     * @throws IOException If the object is not a CognitiveModel or 
     *         CognitiveModelFactory.
     */
    public static CognitiveModel castOrCreateModel(
        Object o)
        throws IOException
    {
        if ( o == null )
        {
            return null;
        }
        else if ( o instanceof CognitiveModel )
        {
            return (CognitiveModel) o;
        }
        else if ( o instanceof CognitiveModelFactory )
        {
            return ((CognitiveModelFactory) o).createModel();
        }
        else
        {
            throw new IOException(
                "Not a CognitiveModel object: " + o.getClass().getName());
        }
    }
    
    /**
     * Attempts to read a CognitiveModel from the given file in XML serialized
     * format.
     *
     * @param  file The file.
     * @return The CognitiveModel read from the file.
     * @throws IOException If there is an IOException when reading the model
     *         or if the data contained in the file is not a model.
     */
    public static CognitiveModel readModelXMLSerialized(
        File file)
        throws IOException
    {
        Object o = XStreamSerializationHandler.readFromFile(file);
        return castOrCreateModel(o);
    }
    
    /**
     * Attempts to read a CognitiveModel from the given file in binary
     * serialized format.
     *
     * @param  file The file.
     * @return The CognitiveModel read from the file.
     * @throws IOException If there is an IOException when reading the model
     *         or if the data contained in the file is not a model.
     */
    public static CognitiveModel readModelBinarySerialized(
        File file)
        throws IOException
    {
        try
        {
            Object o = ObjectSerializationHandler.readFromFile(file);
            return castOrCreateModel(o);
        }
        catch ( ClassNotFoundException ex )
        {
            throw new IOException(ex.getMessage());
        }
    }
    
    /**
     * Attempts to read a CognitiveModel from the given file in CSV
     * format.
     *
     * @param  file The file.
     * @return The CognitiveModel read from the file.
     * @throws IOException If there is an IOException when reading the model
     *         or if the data contained in the file is not a model.
     */
    public static CognitiveModel readModelCSV(
        File file)
        throws IOException
    {
        try
        {
            CognitiveModelFactory factory =
                CSVDefaultCognitiveModelLiteHandler.parseCSVToModelFactory(
                    file.getAbsolutePath(), false);

            if ( factory == null )
            {
                return null;
            }
            else
            {
                return factory.createModel();
            }
        }
        catch ( CSVParseException ex )
        {
            throw new IOException(ex.getMessage());
        }
    }
    
    /**
     * Attempts to read a CognitiveModelFactory from the given file name.
     *
     * @param  fileName The file name.
     * @return The CognitiveModelFactory read from the file.
     * @throws IOException If there is an IOException when reading the model
     *         or if the data contained in the file is not a model.
     */
    public static CognitiveModelFactory readModelFactory(
        String fileName)
        throws IOException
    {
        return readModelFactory(new File(fileName));
    }
    
    /**
     * Attempts to read a CognitiveModelFactory from the given file.
     *
     * @param  file The file.
     * @return The CognitiveModelFactory read from the file.
     * @throws IOException If there is an IOException when reading the model
     *         or if the data contained in the file is not a model.
     */
    public static CognitiveModelFactory readModelFactory(
        File file)
        throws IOException
    {
        String extension = FileUtil.getExtension(file);
        
        if ( extension == null )
        {
            return readModelFactoryXMLSerialized(file);
        }
        else if ( extension.equalsIgnoreCase(XML_SERIALIZED_EXTENSION) )
        {
            return readModelFactoryXMLSerialized(file);
        }
        else if ( extension.equalsIgnoreCase(BINARY_SERIALIZED_EXTENSION) )
        {
            return readModelFactoryBinarySerialized(file);
        }
        else if ( extension.equalsIgnoreCase(CSV_EXTENSION) )
        {
            return readModelFactoryCSV(file);
        }
        else
        {
            return readModelFactoryXMLSerialized(file);
        }
    }
    
    /**
     * Attempts to read a CognitiveModelFactory from the given file in XML 
     * serialized format.
     *
     * @param  file The file.
     * @return The CognitiveModelFactory read from the file.
     * @throws IOException If there is an IOException when reading the model
     *         factory or if the data contained in the file is not a model 
     *         factory.
     */
    public static CognitiveModelFactory readModelFactoryXMLSerialized(
        File file)
        throws IOException
    {
        return (CognitiveModelFactory)
            XStreamSerializationHandler.readFromFile(file);
    }
    
    /**
     * Attempts to read a CognitiveModelFactory from the given file in binary 
     * serialized format.
     *
     * @param  file The file.
     * @return The CognitiveModelFactory read from the file.
     * @throws IOException If there is an IOException when reading the model
     *         factory or if the data contained in the file is not a model 
     *         factory.
     */
    public static CognitiveModelFactory readModelFactoryBinarySerialized(
        File file)
        throws IOException
    {
        try
        {
            return (CognitiveModelFactory)
                ObjectSerializationHandler.readFromFile(file);
        }
        catch ( ClassNotFoundException ex )
        {
            throw new IOException(ex.getMessage());
        }
    }
    
    /**
     * Attempts to read a CognitiveModelFactory from the given file in CSV 
     * format.
     *
     * @param  file The file.
     * @return The CognitiveModelFactory read from the file.
     * @throws IOException If there is an IOException when reading the model
     *         factory or if the data contained in the file is not a model 
     *         factory.
     */
    public static CognitiveModelFactory readModelFactoryCSV(
        File file)
        throws IOException
    {
        try
        {
            return CSVDefaultCognitiveModelLiteHandler.parseCSVToModelFactory(
                file.getAbsolutePath(), false);
        }
        catch ( CSVParseException ex )
        {
            throw new IOException(ex.getMessage());
        }
    }
}
