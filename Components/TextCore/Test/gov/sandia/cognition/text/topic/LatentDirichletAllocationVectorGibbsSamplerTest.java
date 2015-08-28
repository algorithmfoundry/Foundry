/*
 * File:                LatentDirichletAllocationVectorGibbsSamplerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright October 22, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.topic;

import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import static gov.sandia.cognition.math.ProbabilityUtil.*;
import gov.sandia.cognition.math.matrix.optimized.DenseVector;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class LatentDirichletAllocationVectorGibbsSampler.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class LatentDirichletAllocationVectorGibbsSamplerTest
{

    protected Random random = new Random(211);

    /**
     * Creates a new test.
     */
    public LatentDirichletAllocationVectorGibbsSamplerTest()
    {
    }

    /**
     * Test of constructors of class LatentDirichletAllocationVectorGibbsSampler.
     */
    @Test
    public void testConstructors()
    {
        int topicCount = LatentDirichletAllocationVectorGibbsSampler.DEFAULT_TOPIC_COUNT;
        double alpha = LatentDirichletAllocationVectorGibbsSampler.DEFAULT_ALPHA;
        double beta = LatentDirichletAllocationVectorGibbsSampler.DEFAULT_BETA;
        int maxIterations = LatentDirichletAllocationVectorGibbsSampler.DEFAULT_MAX_ITERATIONS;
        int burnInIterations = LatentDirichletAllocationVectorGibbsSampler.DEFAULT_BURN_IN_ITERATIONS;
        int iterationsPerSample = LatentDirichletAllocationVectorGibbsSampler.DEFAULT_ITERATIONS_PER_SAMPLE;

        LatentDirichletAllocationVectorGibbsSampler instance =
            new LatentDirichletAllocationVectorGibbsSampler();
        assertEquals(topicCount, instance.getTopicCount());
        assertEquals(alpha, instance.getAlpha(), 0.0);
        assertEquals(beta, instance.getBeta(), 0.0);
        assertEquals(maxIterations, instance.getMaxIterations());
        assertEquals(burnInIterations, instance.getBurnInIterations());
        assertEquals(iterationsPerSample, instance.getIterationsPerSample());
        assertNotNull(instance.getRandom());

        topicCount = 1 + random.nextInt(100);
        alpha = random.nextDouble() * 10.0;
        beta = random.nextDouble() * 10.0;
        maxIterations = 1 + random.nextInt(100000);
        burnInIterations = random.nextInt(1000);
        iterationsPerSample = random.nextInt(100);
        instance = new LatentDirichletAllocationVectorGibbsSampler(topicCount,
            alpha, beta, maxIterations, burnInIterations, iterationsPerSample,
            random);
        assertEquals(topicCount, instance.getTopicCount());
        assertEquals(alpha, instance.getAlpha(), 0.0);
        assertEquals(beta, instance.getBeta(), 0.0);
        assertEquals(maxIterations, instance.getMaxIterations());
        assertEquals(burnInIterations, instance.getBurnInIterations());
        assertEquals(iterationsPerSample, instance.getIterationsPerSample());
        assertSame(random, instance.getRandom());
    }

    /**
     * Test of learn method, of class LatentDirichletAllocationVectorGibbsSampler.
     */
    @Test
    public void testLearn()
        throws Exception
    {
        final VectorFactory<?> factory = VectorFactory.getSparseDefault();
        final ArrayList<Vector> data = new ArrayList<Vector>();
        
        data.add(factory.copyValues(0, 0, 4, 2, 5, 6, 0, 3, 0));
        data.add(factory.copyValues(0, 0, 0, 8, 0, 3, 0, 0, 0));
        data.add(factory.copyValues(4, 0, 6, 0, 0, 0, 3, 5, 0));
        data.add(factory.copyValues(1, 0, 0, 3, 2, 0, 3, 8, 0));
        data.add(factory.copyValues(3, 0, 5, 3, 0, 5, 6, 0, 0));
        data.add(factory.copyValues(0, 0, 0, 1, 3, 3, 3, 2, 0));
        
        int termCount = 9;
        
        int topicCount = 3;
        LatentDirichletAllocationVectorGibbsSampler instance =
            new LatentDirichletAllocationVectorGibbsSampler(
                topicCount, 2.0, 0.5, 50, 20, 10, random);
        assertNull(instance.learn(null));
        assertNull(instance.learn(new ArrayList<Vector>()));

        LatentDirichletAllocationVectorGibbsSampler.Result result =
            instance.learn(data);

        assertEquals(topicCount, result.getTopicCount());
        assertEquals(topicCount, result.topicTermProbabilities.length);
        assertEquals(data.size(), result.getDocumentCount());
        assertEquals(data.size(), result.documentTopicProbabilities.length);
        assertEquals(termCount, result.getTermCount());

        for (int i = 0; i < topicCount; i++)
        {
            assertEquals(termCount, result.topicTermProbabilities[i].length);
            for (int j = 0; j < termCount; j++)
            {
                assertIsProbability(result.topicTermProbabilities[i][j]);
            }
        }

        for (int i = 0; i < data.size(); i++)
        {
            assertEquals(topicCount, result.documentTopicProbabilities[i].length);
            for (int j = 0; j < topicCount; j++)
            {
                assertIsProbability(result.documentTopicProbabilities[i][j]);
            }
        }
    }

    /**
     * A test that first shows a bug in the code as it was, and then makes sure the issue doesn't come back.
     */
    @Test
    public void testVectorWithDoubles()
    {
        double[] d1 = {0, 0.4, 1.1, 1.5, 2.0 };
        double[] d2 = {1.2, 1.5, 2.3, 0, 1.2 };
        double[] d3 = {0, 2.5, 3.3, 1.0, 1.2 };
        
        final ArrayList<Vector> data = new ArrayList<Vector>();
        data.add(new DenseVector(d1));
        data.add(new DenseVector(d2));
        data.add(new DenseVector(d3));
        LatentDirichletAllocationVectorGibbsSampler lda = new LatentDirichletAllocationVectorGibbsSampler();
        // This used to throw an exception, but doesn't anymore
        // Refer to issue MachineLearning/FoundryLearning#9 for details
        lda.learn(data);
    }

    /**
     * Test of learn method, of class LatentDirichletAllocationVectorGibbsSampler.
     */
    @Test
    public void testDoubleReuse()
        throws Exception
    {
        final VectorFactory<?> factory = VectorFactory.getSparseDefault();
        final ArrayList<Vector> data = new ArrayList<Vector>();
        
        data.add(factory.copyValues(0, 0, 4, 2, 5, 6, 0, 3, 0));
        data.add(factory.copyValues(0, 0, 0, 8, 0, 3, 0, 0, 0));
        data.add(factory.copyValues(4, 0, 6, 0, 0, 0, 3, 5, 0));
        data.add(factory.copyValues(1, 0, 0, 3, 2, 0, 3, 8, 0));
        data.add(factory.copyValues(3, 0, 5, 3, 0, 5, 6, 0, 0));
        data.add(factory.copyValues(0, 0, 0, 1, 3, 3, 3, 2, 0));
        
        int termCount = 9;
        
        int topicCount = 3;
        
        Random randDoubleReuse = new DoubleReuseRandom(1000);
        
        LatentDirichletAllocationVectorGibbsSampler instance =
            new LatentDirichletAllocationVectorGibbsSampler(
                topicCount, 2.0, 0.5, 50, 20, 10, randDoubleReuse);
        assertNull(instance.learn(null));
        assertNull(instance.learn(new ArrayList<Vector>()));

        LatentDirichletAllocationVectorGibbsSampler.Result result =
            instance.learn(data);

        assertEquals(topicCount, result.getTopicCount());
        assertEquals(topicCount, result.topicTermProbabilities.length);
        assertEquals(data.size(), result.getDocumentCount());
        assertEquals(data.size(), result.documentTopicProbabilities.length);
        assertEquals(termCount, result.getTermCount());

        for (int i = 0; i < topicCount; i++)
        {
            assertEquals(termCount, result.topicTermProbabilities[i].length);
            for (int j = 0; j < termCount; j++)
            {
                assertIsProbability(result.topicTermProbabilities[i][j]);
            }
        }

        for (int i = 0; i < data.size(); i++)
        {
            assertEquals(topicCount, result.documentTopicProbabilities[i].length);
            for (int j = 0; j < topicCount; j++)
            {
                assertIsProbability(result.documentTopicProbabilities[i][j]);
            }
        }
    }

    
    /**
     * Test of learn method, of class LatentDirichletAllocationVectorGibbsSampler.
     */
    @Test
    public void testLearnSmallTermCount()
        throws Exception
    {
        final VectorFactory<?> factory = VectorFactory.getSparseDefault();
        final ArrayList<Vector> data = new ArrayList<Vector>();
        
        data.add(factory.copyValues(1, 0));
        data.add(factory.copyValues(1, 0));
        data.add(factory.copyValues(0, 1));
        data.add(factory.copyValues(0, 1));
        data.add(factory.copyValues(1, 1));
        data.add(factory.copyValues(1, 1));
        
        int termCount = 2;

        int topicCount = 3;
        LatentDirichletAllocationVectorGibbsSampler instance =
            new LatentDirichletAllocationVectorGibbsSampler(
                topicCount, 2.0, 0.5, 50, 20, 10, random);
        assertNull(instance.learn(null));
        assertNull(instance.learn(new ArrayList<Vector>()));

        LatentDirichletAllocationVectorGibbsSampler.Result result =
            instance.learn(data);

        assertEquals(topicCount, result.getTopicCount());
        assertEquals(topicCount, result.topicTermProbabilities.length);
        assertEquals(data.size(), result.getDocumentCount());
        assertEquals(data.size(), result.documentTopicProbabilities.length);
        assertEquals(termCount, result.getTermCount());

        for (int i = 0; i < topicCount; i++)
        {
            assertEquals(termCount, result.topicTermProbabilities[i].length);
            for (int j = 0; j < termCount; j++)
            {
                assertIsProbability(result.topicTermProbabilities[i][j]);
            }
        }

        for (int i = 0; i < data.size(); i++)
        {
            assertEquals(topicCount, result.documentTopicProbabilities[i].length);
            for (int j = 0; j < topicCount; j++)
            {
                assertIsProbability(result.documentTopicProbabilities[i][j]);
            }
        }
    }

    public static ArrayList<String> readVocab(
        final String fileName)
        throws IOException
    {
        final ArrayList<String> result = new ArrayList<String>();
        final BufferedReader in = new BufferedReader(
            new FileReader(fileName));
        try
        {
            String line;
            while ((line = in.readLine()) != null)
            {
                result.add(line);
            }
        }
        finally
        {
            in.close();
        }
        return result;
    }

    public static ArrayList<Vector> readDataAsVector(
        final String fileName,
        final int dimensionality)
        throws IOException
    {
        final ArrayList<Vector> result = new ArrayList<Vector>();
        final BufferedReader in = new BufferedReader(
            new FileReader(fileName));
        try
        {
            String line;
            while ((line = in.readLine()) != null)
            {
                final String[] parts = line.split("\\s");
                final int elements = Integer.parseInt(parts[0]);
                final Vector v = VectorFactory.getSparseDefault().createVectorCapacity(
                    dimensionality, elements);
                for (int i = 1; i < parts.length; i++)
                {
                    final String part = parts[i];
                    final int split = part.indexOf(':');
                    final int index = Integer.parseInt(part.substring(0, split));
                    final int value = Integer.parseInt(part.substring(split + 1));
                    v.setElement(index, value);
                }
                result.add(v);
            }
        }
        finally
        {
            in.close();
        }
        return result;
    }

    public static int[][] readDataAsArray(
        final String fileName)
        throws IOException
    {
        final ArrayList<int[]> result = new ArrayList<int[]>();
        final BufferedReader in = new BufferedReader(
            new FileReader(fileName));
        try
        {
            String line;
            while ((line = in.readLine()) != null)
            {
                final String[] parts = line.split("\\s");
                final int elements = Integer.parseInt(parts[0]);
                final ArrayList<Integer> wordsList = new ArrayList<Integer>(elements);
                for (int i = 1; i < parts.length; i++)
                {
                    final String part = parts[i];
                    final int split = part.indexOf(':');
                    final int index = Integer.parseInt(part.substring(0, split));
                    final int count = Integer.parseInt(part.substring(split + 1));

                    for (int j = 0; j < count; j++)
                    {
                        wordsList.add(index);
                    }
                }

                int[] wordsArray = new int[wordsList.size()];
                for (int i = 0; i < wordsList.size(); i++)
                {
                    wordsArray[i] = wordsList.get(i);
                }
                result.add(wordsArray);
            }
        }
        finally
        {
            in.close();
        }

        final int[][] resultArray = new int[result.size()][];
        for (int i = 0; i < result.size(); i++)
        {
            resultArray[i] = result.get(i);
        }
        return resultArray;
    }

    /**
     * Test of getResult method, of class LatentDirichletAllocationVectorGibbsSampler.
     */
    @Test
    public void testGetResult()
    {
        LatentDirichletAllocationVectorGibbsSampler instance =
            new LatentDirichletAllocationVectorGibbsSampler();
        assertNull(instance.getResult());
    }

    /**
     * Test of getRandom method, of class LatentDirichletAllocationVectorGibbsSampler.
     */
    @Test
    public void testGetRandom()
    {
        this.testSetRandom();
    }

    /**
     * Test of setRandom method, of class LatentDirichletAllocationVectorGibbsSampler.
     */
    @Test
    public void testSetRandom()
    {
        LatentDirichletAllocationVectorGibbsSampler instance =
            new LatentDirichletAllocationVectorGibbsSampler();
        assertNotNull(instance.getRandom());

        Random random = new Random();
        instance.setRandom(random);
        assertSame(random, instance.getRandom());

        random = new Random();
        instance.setRandom(random);
        assertSame(random, instance.getRandom());

        random = null;
        instance.setRandom(random);
        assertSame(random, instance.getRandom());
        
        random = new Random();
        instance.setRandom(random);
        assertSame(random, instance.getRandom());
    }

    /**
     * Test of getTopicCount method, of class LatentDirichletAllocationVectorGibbsSampler.
     */
    @Test
    public void testGetTopicCount()
    {
        this.testSetTopicCount();
    }

    /**
     * Test of setTopicCount method, of class LatentDirichletAllocationVectorGibbsSampler.
     */
    @Test
    public void testSetTopicCount()
    {
        int topicCount = LatentDirichletAllocationVectorGibbsSampler.DEFAULT_TOPIC_COUNT;
        LatentDirichletAllocationVectorGibbsSampler instance =
            new LatentDirichletAllocationVectorGibbsSampler();
        assertEquals(topicCount, instance.getTopicCount());

        topicCount = 77;
        instance.setTopicCount(topicCount);
        assertEquals(topicCount, instance.getTopicCount());


        boolean exceptionThrown = false;
        try
        {
            instance.setTopicCount(0);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(topicCount, instance.getTopicCount());

        exceptionThrown = false;
        try
        {
            instance.setTopicCount(-1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(topicCount, instance.getTopicCount());
    }

    /**
     * Test of getAlpha method, of class LatentDirichletAllocationVectorGibbsSampler.
     */
    @Test
    public void testGetAlpha()
    {
        this.testSetAlpha();
    }

    /**
     * Test of setAlpha method, of class LatentDirichletAllocationVectorGibbsSampler.
     */
    @Test
    public void testSetAlpha()
    {
        double alpha = LatentDirichletAllocationVectorGibbsSampler.DEFAULT_ALPHA;
        LatentDirichletAllocationVectorGibbsSampler instance =
            new LatentDirichletAllocationVectorGibbsSampler();
        assertEquals(alpha, instance.getAlpha(), 0.0);

        alpha = 1.1;
        instance.setAlpha(alpha);
        assertEquals(alpha, instance.getAlpha(), 0.0);

        boolean exceptionThrown = false;
        try
        {
            instance.setAlpha(0.0);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(alpha, instance.getAlpha(), 0.0);

        exceptionThrown = false;
        try
        {
            instance.setAlpha(-0.1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(alpha, instance.getAlpha(), 0.0);
    }

    /**
     * Test of getBeta method, of class LatentDirichletAllocationVectorGibbsSampler.
     */
    @Test
    public void testGetBeta()
    {
        this.testSetBeta();
    }

    /**
     * Test of setBeta method, of class LatentDirichletAllocationVectorGibbsSampler.
     */
    @Test
    public void testSetBeta()
    {
        double beta = LatentDirichletAllocationVectorGibbsSampler.DEFAULT_BETA;
        LatentDirichletAllocationVectorGibbsSampler instance =
            new LatentDirichletAllocationVectorGibbsSampler();
        assertEquals(beta, instance.getBeta(), 0.0);

        beta = 1.1;
        instance.setBeta(beta);
        assertEquals(beta, instance.getBeta(), 0.0);
        
        boolean exceptionThrown = false;
        try
        {
            instance.setBeta(0.0);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(beta, instance.getBeta(), 0.0);

        exceptionThrown = false;
        try
        {
            instance.setBeta(-0.1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(beta, instance.getBeta(), 0.0);
    }

    /**
     * Test of getBurnInIterations method, of class LatentDirichletAllocationVectorGibbsSampler.
     */
    @Test
    public void testGetBurnInIterations()
    {
        this.testSetBurnInIterations();
    }

    /**
     * Test of setBurnInIterations method, of class LatentDirichletAllocationVectorGibbsSampler.
     */
    @Test
    public void testSetBurnInIterations()
    {
        int burnInIterations = LatentDirichletAllocationVectorGibbsSampler.DEFAULT_BURN_IN_ITERATIONS;
        LatentDirichletAllocationVectorGibbsSampler instance =
            new LatentDirichletAllocationVectorGibbsSampler();
        assertEquals(burnInIterations, instance.getBurnInIterations());

        burnInIterations = 0;
        instance.setBurnInIterations(burnInIterations);
        assertEquals(burnInIterations, instance.getBurnInIterations());

        burnInIterations = 101;
        instance.setBurnInIterations(burnInIterations);
        assertEquals(burnInIterations, instance.getBurnInIterations());


        boolean exceptionThrown = false;
        try
        {
            instance.setBurnInIterations(-1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(burnInIterations, instance.getBurnInIterations());
    }

    /**
     * Test of getIterationsPerSample method, of class LatentDirichletAllocationVectorGibbsSampler.
     */
    @Test
    public void testGetIterationsPerSample()
    {
        this.testSetIterationsPerSample();
    }

    /**
     * Test of setIterationsPerSample method, of class LatentDirichletAllocationVectorGibbsSampler.
     */
    @Test
    public void testSetIterationsPerSample()
    {
        int iterationsPerSample = LatentDirichletAllocationVectorGibbsSampler.DEFAULT_ITERATIONS_PER_SAMPLE;
        LatentDirichletAllocationVectorGibbsSampler instance =
            new LatentDirichletAllocationVectorGibbsSampler();
        assertEquals(iterationsPerSample, instance.getIterationsPerSample());

        iterationsPerSample = 1;
        instance.setIterationsPerSample(iterationsPerSample);
        assertEquals(iterationsPerSample, instance.getIterationsPerSample());

        iterationsPerSample = 12;
        instance.setIterationsPerSample(iterationsPerSample);
        assertEquals(iterationsPerSample, instance.getIterationsPerSample());

        boolean exceptionThrown = false;
        try
        {
            instance.setIterationsPerSample(0);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(iterationsPerSample, instance.getIterationsPerSample());

        exceptionThrown = false;
        try
        {
            instance.setIterationsPerSample(-1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(iterationsPerSample, instance.getIterationsPerSample());
    }

}