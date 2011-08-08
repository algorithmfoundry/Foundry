/*
 * File:                CSVDefaultCognitiveModelLiteHandler.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 2, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.io;

import gov.sandia.cognition.framework.CognitiveModule;
import gov.sandia.cognition.framework.CognitiveModuleFactory;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.DefaultSemanticNetwork;
import gov.sandia.cognition.framework.SemanticLabel;
import gov.sandia.cognition.framework.SemanticNetwork;
import gov.sandia.cognition.framework.lite.ArrayBasedPerceptionModuleFactory;
import gov.sandia.cognition.framework.lite.CognitiveModelLiteFactory;
import gov.sandia.cognition.framework.lite.MutableSemanticMemoryLiteFactory;
import gov.sandia.cognition.framework.lite.SharedSemanticMemoryLiteFactory;
import gov.sandia.cognition.framework.lite.SimplePatternRecognizer;
import gov.sandia.cognition.io.CSVParseException;
import gov.sandia.cognition.io.CSVUtility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * The CSVDefaultCognitiveModelLiteHandler class implements a format handler for
 * CSV files that specify a default setup for a CognitiveModelLite. It is
 * implemented by calling static methods to parse a file or stream and then an
 * object is instantiated to read from the stream to parse it into the proper
 * model factory.
 * 
 * It also supports writing to the CSV file format.
 * 
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since 1.0
 */
public class CSVDefaultCognitiveModelLiteHandler 
    extends java.lang.Object
{
    /** The name of the format that the file parses. */
    public static final String FORMAT_NAME = "CSVDefaultCognitiveModelLite";

    /** The current version that is parsed. */
    public static final double FORMAT_VERSION = 1.0;

    /** The network being read in. */
    private DefaultSemanticNetwork network = null;

    /** The mapping of String label names to the actual labels. */
    private HashMap<String, DefaultSemanticLabel> labels = null;

    /**
     * Creates a new instance of CommaSeparatedValueHandler.
     */
    protected CSVDefaultCognitiveModelLiteHandler()
    {
        super();

        setNetwork(new DefaultSemanticNetwork());
        setLabels(new HashMap<String, DefaultSemanticLabel>());
    }

    /**
     * The main parsing function. It reads through the CSV values in the stream
     * and handles them.
     * 
     * @param br
     *            The reader to read the CSV values from.
     * @return True if the parsing was successful.
     * @throws CSVParseException
     *             If there is an error in the CSV formatting.
     * @throws IOException
     *             If there is an IO error.
     */
    protected boolean parse(
        BufferedReader br) 
        throws CSVParseException, IOException
    {
        // First parse the header. The only thing we really need from it is
        // the version number.
        final double version = parseHeader(br);

        if (version <= 0.0)
        {
            // Not a valid version number.
            throw new CSVParseException("Unknown version number: " + version);
        }

        // Now that we've read the header, read the actual data in this loop
        // until there is no more data.
        String[] entries = null;
        while ((entries = CSVUtility.nextNonEmptyLine(br)) != null)
        {
            if (entries.length <= 0)
            {
                // No data on this line.
                continue;
            }

            // Figure out what to do based on the first entry now that we know
            // there is at least one.
            final String first = entries[0].trim();
            final int numEntries = entries.length;

            if (first.length() <= 0)
            {
                // This is really just a blank line.
                continue;
            } else if (first.equalsIgnoreCase("node"))
            {
                // It is a node directive.

                if (numEntries < 2)
                {
                    // Error: Not enough parameters.
                    throw new CSVParseException("Node specified with no name.");
                }

                // Add the node.
                final String name = entries[1];
                addNode(name);
            } else if (first.equalsIgnoreCase("link"))
            {
                if (numEntries < 4)
                {
                    // Error: Not enough parameters.
                    throw new CSVParseException(
                        "Link specified without enough arguments: 4 expected.");
                }

                // Parse the arguments.
                final String from = entries[1];
                final String to = entries[2];
                final String linkString = entries[3];
                double link = 0.0;

                try
                {
                    link = Double.parseDouble(linkString);
                } catch (final NumberFormatException nfe)
                {
                    throw new CSVParseException(nfe);
                }

                // Add a link.
                addLink(from, to, link);
            } else
            {
                // Treat it as a parameter.
                throw new CSVParseException("Unknown directive " + first);
            }
        }

        return true;
    }

    /**
     * Parses the header and returns the version number. It throws an exception
     * if there is any issue with the header so the returned version number is
     * always valid.
     * 
     * @param br
     *            The reader to read the CSV values from.
     * @return True if the parsing was successful.
     * @throws CSVParseException
     *             If there is an error in the CSV formatting.
     * @throws IOException
     *             If there is an IO error.
     */
    protected double parseHeader(
        BufferedReader br) 
        throws CSVParseException, IOException
    {
        // Read the first non-empty line.
        String[] entries = CSVUtility.nextNonEmptyLine(br);
        if (entries == null || entries.length <= 0)
        {
            throw new CSVParseException("No CSV data given.");
        }

        // Make sure that this is the proper format.
        if (entries.length < 1 || !entries[0].equalsIgnoreCase(FORMAT_NAME))
        {
            throw new CSVParseException("Format " + FORMAT_NAME
                    + " not specified.");
        }

        // Next read the version line.
        entries = CSVUtility.nextNonEmptyLine(br);
        if (entries == null || entries.length <= 0)
        {
            throw new CSVParseException("No CSV data given.");
        }

        if (entries.length < 2 || !entries[0].equalsIgnoreCase("Version"))
        {
            throw new CSVParseException("Version not specified.");
        }

        // Ensure this is the proper version.
        final String versionString = entries[1];
        double version = -1.0;

        try
        {
            version = Double.parseDouble(versionString);
        } catch (final NumberFormatException e)
        {
            version = -1.0;
        }

        // See if we can handle this version.
        if (version != FORMAT_VERSION)
        {
            throw new CSVParseException("Not a valid version number: "
                    + versionString);
        }

        // The version is valid.
        return version;
    }

    /**
     * Called when the "node" directive is seen. It adds a node to the network
     * 
     * @param name
     *            The name of the node to add.
     * @throws CSVParseException
     *             If there is an error in adding the node, such as it already
     *             existing.
     */
    protected void addNode(
        String name)
        throws CSVParseException
    {
        // Get the label for the node.
        DefaultSemanticLabel label = getLabel(name);

        if (label != null)
        {
            // Error: One already exists.
            throw new CSVParseException("Label " + name + " already exists.");
        }

        // Create a label for the node.
        label = new DefaultSemanticLabel(name);

        // Add the label to the mapping.
        labels.put(name, label);

        // Add the label to the network.
        network.addNode(label);
    }

    /**
     * Called when the "link" directive is seen. It adds a link to the network
     * 
     * @param fromName
     *            The node the link is from.
     * @param toName
     *            The node the link is to.
     * @param weight
     *            The weight for the link.
     * @throws CSVParseException
     *             If there is an error in adding the link, such as one of the
     *             nodes not existing.
     */
    protected void addLink(
        String fromName,
        String toName,
        double weight)
        throws CSVParseException
    {
        // Get the labels for the nodes.
        final DefaultSemanticLabel fromLabel = getLabel(fromName);
        final DefaultSemanticLabel toLabel = getLabel(toName);

        if (fromLabel == null)
        {
            // Error: No from node.
            throw new CSVParseException("Node " + fromName + " does not exist.");
        }

        if (toLabel == null)
        {
            // Error: No to node.
            throw new CSVParseException("Node " + toName + " does not exist.");
        }

        // Add the association to the network.
        network.setAssociation(fromLabel, toLabel, weight);
    }

    /**
     * Gets the sharable module factory that has been read in.
     * 
     * @return The module factory that has been read in.
     */
    protected CognitiveModuleFactory getSharedModuleFactory()
    {
        return new SharedSemanticMemoryLiteFactory(new SimplePatternRecognizer(
                network));
    }

    /**
     * Gets the sharable module factory that has been read in.
     * 
     * @return The module factory that has been read in.
     */
    protected CognitiveModuleFactory getMutableModuleFactory()
    {
        return new MutableSemanticMemoryLiteFactory(
                new SimplePatternRecognizer(network));
    }

    /**
     * Takes a name and returns its DefaultSemanticLabel or null if there is
     * none.
     * 
     * @param name
     *            The name of the label to get.
     * @return The DefaultSemanticLabel associated with that name, if one
     *         exists, otherwise null.
     */
    protected DefaultSemanticLabel getLabel(
        String name)
    {
        return labels.get(name);
    }

    /**
     * Gets the underlying network.
     * 
     * @return The underlying semantic network.
     */
    protected DefaultSemanticNetwork getNetwork()
    {
        return network;
    }

    /**
     * Set the network being parsed.
     * 
     * @param network
     *            The new network being parsed.
     */
    protected void setNetwork(
        DefaultSemanticNetwork network)
    {
        this.network = network;
    }

    /**
     * Sets the label mapping.
     * 
     * @param labels
     *            The new label mapping.
     */
    protected void setLabels(
        HashMap<String, DefaultSemanticLabel> labels)
    {
        this.labels = labels;
    }

    /**
     * Reads a default CognitiveModuleFactory from the given CSV file and
     * returns it.
     * 
     * @param fileName
     *            The file to read from.
     * @param mutable
     *            True to create a mutable module.
     * @return The default CognitiveModuleFactory read from the file.
     * @throws CSVParseException
     *             If the CSV file is not properly formatted.
     * @throws IOException
     *             If there is an IO error, such as the file not existing.
     */
    public static CognitiveModuleFactory parseCSVToModuleFactory(
        String fileName, 
        boolean mutable)
        throws CSVParseException, IOException
    {
        // Create the stream and parse it.
        return parseCSVToModuleFactory(
            new BufferedReader(new FileReader(fileName)), mutable);
    }

    /**
     * Reads a default CognitiveModuleFactory from the given buffered reader and
     * then returns it.
     * 
     * @param br
     *            The reader to read from.
     * @param mutable
     *            True to create a mutable module.
     * @return The default CognitiveModuleFactory read from the reader.
     * @throws CSVParseException
     *             If the CSV file is not properly formatted.
     * @throws IOException
     *             If there is an IO error, such as the file not existing.
     */
    public static CognitiveModuleFactory parseCSVToModuleFactory(
        BufferedReader br, 
        boolean mutable) 
        throws CSVParseException,
        IOException
    {
        // Create the handler.
        final CSVDefaultCognitiveModelLiteHandler handler = 
            new CSVDefaultCognitiveModelLiteHandler();

        // Parse the network.
        handler.parse(br);

        if (mutable)
        {
            // Return the mutable factory.
            return handler.getMutableModuleFactory();
        } else
        {
            // Return the shared factory.
            return handler.getSharedModuleFactory();
        }
    }

    /**
     * Reads a default CognitiveModuleFactory from the given file and puts it in
     * a default CognitiveModelFactory.
     * 
     * @param fileName
     *            The file to read from.
     * @param mutable
     *            True to create a mutable module.
     * @return The default CognitiveModuleFactory read from the reader.
     * @throws CSVParseException
     *             If the CSV file is not properly formatted.
     * @throws IOException
     *             If there is an IO error, such as the file not existing.
     */
    public static CognitiveModelLiteFactory parseCSVToModelFactory(
        String fileName, 
        boolean mutable)
        throws CSVParseException, IOException
    {
        // Create the stream and parse it.
        return parseCSVToModelFactory(new BufferedReader(new FileReader(
                fileName)), mutable);
    }

    /**
     * Reads a default CognitiveModuleFactory from the given buffered reader and
     * puts it in a default CognitiveModelFactory.
     * 
     * @param br
     *            The reader to read from.
     * @param mutable
     *            True to create a mutable model factory.
     * @return The default CognitiveModuleFactory read from the reader.
     * @throws CSVParseException
     *             If the CSV file is not properly formatted.
     * @throws IOException
     *             If there is an IO error, such as the file not existing.
     */
    public static CognitiveModelLiteFactory parseCSVToModelFactory(
        BufferedReader br, 
        boolean mutable) 
        throws CSVParseException, IOException
    {
        // Create a CognitiveModelFactory from the module factory.
        return createModelFactory(parseCSVToModuleFactory(br, mutable));
    }

    /**
     * Creates a default CognitiveModelLiteFactory for the given module factory
     * and returns it. It primarily just adds a perception module to the model.
     * 
     * @param moduleFactory
     *            The module factory to wrap in a model factory.
     * @return A CognitiveModelLiteFactory using the given module factory.
     */
    private static CognitiveModelLiteFactory createModelFactory(
        CognitiveModuleFactory moduleFactory)
    {
        // Create the model factory.
        final CognitiveModelLiteFactory modelFactory = 
            new CognitiveModelLiteFactory();

        // Add a default perception factory.
        modelFactory.addModuleFactory(new ArrayBasedPerceptionModuleFactory());

        // Add the given module factory.
        modelFactory.addModuleFactory(moduleFactory);

        // Return the model factory.
        return modelFactory;
    }

    /**
     * Writes a SemanticNetwork to the given file using the CSV format.
     * 
     * @param fileName
     *            The name of the file to write the network.
     * @param network
     *            The network to write.
     * @throws FileNotFoundException
     *             If the file cannot be written to.
     */
    public static void write(
        String fileName,
        SemanticNetwork network)
        throws FileNotFoundException
    {
        write(new File(fileName), network);
    }

    /**
     * Writes a SemanticNetwork to the given file using the CSV format.
     * 
     * @param file
     *            The file to write the network.
     * @param network
     *            The network to write.
     * @throws FileNotFoundException
     *             If the file cannot be written to.
     */
    public static void write(
        File file, 
        SemanticNetwork network)
        throws FileNotFoundException
    {
        final PrintWriter pw = new PrintWriter(file);
        write(pw, network);
        pw.close();
    }

    /**
     * Writes a SemanticNetwork to the given stream using the CSV format.
     * 
     * @param pw
     *            The place to write the network.
     * @param network
     *            The network to write.
     */
    public static void write(
        PrintWriter pw, 
        SemanticNetwork network)
    {
        // Write the header.
        pw.println(FORMAT_NAME);
        pw.println("Version," + FORMAT_VERSION);
        pw.println();

        // Write the nodes.
        for (final SemanticLabel node : network.getNodes())
        {
            pw.println("node," + node.getName());
        }

        // Put a space between the nodes and the links.
        pw.println();

        // Write the links.
        for (final SemanticLabel from : network.getNodes())
        {
            for (final SemanticLabel to : network.getOutLinks(from))
            {
                final double value = network.getAssociation(from, to);
                pw.println("link," + from.getName() + "," + to.getName() + ","
                        + value);
            }
        }
    }
}
