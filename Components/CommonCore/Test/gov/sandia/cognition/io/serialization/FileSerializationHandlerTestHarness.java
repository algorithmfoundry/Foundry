/*
 * File:                FileSerializationHandlerTestHarness.java
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

import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.math.Ring;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Test harness for FileSerializationHandler interface.
 *
 * @param   <SerializedType>
 *      The type of object that can be serialized.
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class FileSerializationHandlerTestHarness<SerializedType>
    extends TestCase
{

    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public FileSerializationHandlerTestHarness(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates an instance to test on.
     *
     * @return
     *      A new instance to test on.
     */
    public abstract FileSerializationHandler<SerializedType> createInstance();

    /**
     * Creates a test object list.
     *
     * @return
     *      A test object list.
     */
    public abstract List<SerializedType> createTestObjectList();

    /**
     * Test of writeToFile method, of class FileSerializationHandler.
     *
     * @throws  Exception
     */
    public void testWriteToFile()
        throws Exception
    {
        final File file = File.createTempFile(
            "TEMP-" + this.getClass().getSimpleName() + "-testWriteToFile",
            "temp");
        final String fileName = file.getAbsolutePath();
        file.deleteOnExit();

        final FileSerializationHandler<SerializedType> instance =
            this.createInstance();

        for (SerializedType original : this.createTestObjectList())
        {
            
            // Write using the file object.
            instance.writeToFile(file, original);
            Object deserialized = instance.readFromFile(file);
            assertEquals(original, deserialized);
            assertNotNull(deserialized);

            // Write using the file name.
            instance.writeToFile(fileName, original);
            deserialized = instance.readFromFile(fileName);
            assertEquals(original, deserialized);
            assertNotNull(deserialized);
        }
        file.delete();
    }

    /**
     * Test of readFromFile method, of class FileSerializationHandler.
     *
     * @throws  Exception
     */
    public void testReadFromFile()
        throws Exception
    {
        // Tested by writing to a file.
        this.testWriteToFile();
    }


    /**
     * Creates a list of serializable object to test serialization on.
     *
     * @param   random
     *      A random number generator to use.
     * @return
     *      A list of serializable objects to test.
     */
    public static List<Serializable> createSerializableTestObjectList(
        final Random random)
    {
        final LinkedList<Serializable> result = new LinkedList<Serializable>();

        result.add(Boolean.FALSE);
        result.add(Boolean.TRUE);
        result.add(new Integer(4));
        result.add(new Long(7L));
        result.add(new Float(4.1f));
        result.add(new Double(1.4));
        result.add(new Character('b'));
        result.add("this is a test string");
        result.add("");
        result.add(new Date());

        int N = random.nextInt(100) + 3;
        LinkedList<Ring<?>> list = new LinkedList<Ring<?>>();
        for (int i = 0; i < N; i++)
        {
            if (random.nextBoolean())
            {
                list.add(MatrixFactory.getDefault().createUniformRandom(
                    2, 3, -3, 3, random));
            }
            else
            {
                list.add(new ComplexNumber(
                    random.nextGaussian(), random.nextGaussian()));
            }
        }

        result.add(list);

        final Matrix matrix = MatrixFactory.getDefault().createUniformRandom(
            3, 4, -3.0, 3.0, random);
        result.add(matrix);

        return result;
    }
}
