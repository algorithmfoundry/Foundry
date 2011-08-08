/*
 * File:                XStreamSerializationHandlerExample.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright September 11, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package examples;

import gov.sandia.cognition.io.XStreamSerializationHandler;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Provides a simple example of using the {@code XStreamSerializationHandler}
 * to read and write an object.
 * 
 * @author  Justin Basilico
 * @since   2.1
 */
public class XStreamSerializationHandlerExample
    extends Object
{

    /**
     * Runs the example.
     * 
     * @param   args Ignored. The example has no arguments.
     * @throws  java.io.IOException If there is an IO error.
     */
    public static void main(
        final String... args)
        throws IOException
    {
        // Create an object to write out.
        final ExampleObject original = new ExampleObject(14, "example");

        // Write out the object using the serialization handler.
        final StringWriter writer = new StringWriter();
        XStreamSerializationHandler.write(writer, original);
        writer.close();

        // Show what the XML looks like.
        System.out.println("XStream version:");
        System.out.println(writer.toString());

        // Now read the object back in.
        final StringReader reader = new StringReader(writer.toString());
        final ExampleObject rebuilt = (ExampleObject) XStreamSerializationHandler.read(reader);
        reader.close();

        // The read in object should be equal to the original.
        System.out.print("Are they equal? ");
        if (original.equals(rebuilt))
        {
            // The answer will be yes.
            System.out.println("Yes");
        }
        else
        {
            System.out.println("No");
        }

        // The read in object is not the exact same object (not the same pointer).
        System.out.print("But are they the exact same object? ");
        if (original == rebuilt)
        {
            System.out.println("Yes");
        }
        else
        {
            // The answer will be no.
            System.out.println("No");
        }
    }

    /**
     * An example object to read in. The important part about it is that it
     * implements {@code Serializable}. This is what lets XStream serialize it.
     */
    public static class ExampleObject
        implements Serializable // Having this is very important!
    {

        /** An integer. */
        protected int number;

        /** A string. */
        protected String name;

        /**
         * Creates a new {@code ExampleObject}.
         * 
         * @param   number An integer.
         * @param   name A string.
         */
        public ExampleObject(
            int number,
            String name)
        {
            this.number = number;
            this.name = name;
        }

        @Override
        public boolean equals(
            final Object object)
        {
            ExampleObject other = (ExampleObject) object;
            return this.number == other.number && this.name.equals(other.name);
        }

    }

}
