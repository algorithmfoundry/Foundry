/*
 * File:                ParallelLatentDirichletAllocationVectorGibbsSampler.java
 * Authors:             Justin Basilico, Jason Shepherd
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

import gov.sandia.cognition.algorithm.ParallelAlgorithm;
import gov.sandia.cognition.algorithm.ParallelUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorEntry;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

public class ParallelLatentDirichletAllocationVectorGibbsSampler
    extends LatentDirichletAllocationVectorGibbsSampler
    implements ParallelAlgorithm
{    
    /**
     * Thread pool used for parallelization.
     */
    private transient ThreadPoolExecutor threadPool;
    
    /**
     * Creates a new {@code ParallelLatentDirichletAllocationVectorGibbsSampler} with
     * default parameters.
     */
    public ParallelLatentDirichletAllocationVectorGibbsSampler()
    {
        super();
    }

    /**
     * Creates a new {@code ParallelLatentDirichletAllocationVectorGibbsSampler} with
     * the given parameters.
     *
     * @param   topicCount
     *      The number of topics for the algorithm to create. Must be positive.
     * @param   alpha
     *      The alpha parameter controlling the Dirichlet distribution for the
     *      document-topic probabilities. It acts as a prior weight assigned to
     *      the document-topic counts. Must be positive.
     * @param   beta
     *      The beta parameter controlling the Dirichlet distribution for the
     *      topic-term probabilities. It acts as a prior weight assigned to
     *      the topic-term counts.
     * @param   maxIterations
     *      The maximum number of iterations to run for. Must be positive.
     * @param   burnInIterations
     *      The number of burn-in iterations for the Markov Chain Monte Carlo
     *      algorithm to run before sampling begins.
     * @param   iterationsPerSample
     *      The number of iterations to the Markov Chain Monte Carlo algorithm
     *      between samples (after the burn-in iterations).
     * @param   random
     *      The random number generator to use.
     */
    public ParallelLatentDirichletAllocationVectorGibbsSampler(
        final int topicCount,
        final double alpha,
        final double beta,
        final int maxIterations,
        final int burnInIterations,
        final int iterationsPerSample,
        final Random random)
    {
        super(topicCount, alpha, beta, maxIterations, burnInIterations, iterationsPerSample, random);
    }

    @Override
    protected boolean step()
    {
        // We create this array to be used as a workspace to avoid having to
        // recreate it inside the sampling function.
        int document = 0;
        int occurrence = 0;
        
        //Create the task list:
        ArrayList<DocumentSampleTask> samplingTaskList = new ArrayList<DocumentSampleTask>(this.documentCount);
        for (Vectorizable m : this.data ) 
        {
            Vector av = m.convertToVector();
            samplingTaskList.add( new DocumentSampleTask( av, document, occurrence));
            document++;
            occurrence += av.norm1();
        }
        
        try
        {
            ParallelUtil.executeInParallel(samplingTaskList, this.getThreadPool());
        }
        catch( Exception ex )
        {
            throw new RuntimeException( ex );
        }

        if (this.iteration >= this.burnInIterations
            && (this.iteration - this.burnInIterations)
                % this.iterationsPerSample == 0)
        {
            this.readParameters();
        }

        return true;
    }

    @Override
    protected void cleanupAlgorithm()
    {
        this.getThreadPool().shutdown();
        super.cleanupAlgorithm();
    }

    /**
     * A document sampling task
     */
    protected class DocumentSampleTask
        extends AbstractCloneableSerializable
        implements Callable<Boolean>
    {
        /**
         * The term vector for a document.
         */
        Vector vector;
        
        /**
         * The document address
         */
        int document;
        
        /**
         * The occurrence address
         */
        int occurrence;

        /**
         * Creates a new instance of DocumentSampleTask
         * @param v - term frequency vector for a single document
         * @param doc - document address in sample arrays
         * @param occ - occurrence address in sample occurrence array
         */
        public DocumentSampleTask(Vector v, int doc, int occ )
        {
            super();
            this.vector = v;
            this.document = doc;
            this.occurrence = occ;
        }

        @Override
        public Boolean call() throws Exception
        {
            final double[] topicCumulativeProportions = new double[ParallelLatentDirichletAllocationVectorGibbsSampler.this.topicCount];
            for (VectorEntry v : this.vector)
            {
                final int term = v.getIndex();
                final int count = (int) v.getValue();

                for (int i = 1; i <= count; i++)
                {
                    // Get the old topic assignment.
                    final int oldTopic = ParallelLatentDirichletAllocationVectorGibbsSampler.this.occurrenceTopicAssignments[this.occurrence];

                    // Remove the topic assignment .
                    ParallelLatentDirichletAllocationVectorGibbsSampler.this.documentTopicCount[document][oldTopic] -= 1;
                    ParallelLatentDirichletAllocationVectorGibbsSampler.this.documentTopicSum[document] -= 1;
                    
                    synchronized(ParallelLatentDirichletAllocationVectorGibbsSampler.this.topicTermCount)
                    {
                        ParallelLatentDirichletAllocationVectorGibbsSampler.this.topicTermCount[oldTopic][term] -= 1;
                    }
                    synchronized(ParallelLatentDirichletAllocationVectorGibbsSampler.this.topicTermSum)
                    {
                        ParallelLatentDirichletAllocationVectorGibbsSampler.this.topicTermSum[oldTopic] -= 1;
                    }

                    // Sample a new topic.
                    final int newTopic = ParallelLatentDirichletAllocationVectorGibbsSampler.this.sampleTopic(document, term, topicCumulativeProportions);

                    // Add the new topic assignment.
                    ParallelLatentDirichletAllocationVectorGibbsSampler.this.occurrenceTopicAssignments[this.occurrence] = newTopic;
                    ParallelLatentDirichletAllocationVectorGibbsSampler.this.documentTopicCount[document][newTopic] += 1;
                    ParallelLatentDirichletAllocationVectorGibbsSampler.this.documentTopicSum[document] += 1;
                    
                    synchronized(ParallelLatentDirichletAllocationVectorGibbsSampler.this.topicTermCount)
                    {
                        ParallelLatentDirichletAllocationVectorGibbsSampler.this.topicTermCount[newTopic][term] += 1;
                    }
                    synchronized(ParallelLatentDirichletAllocationVectorGibbsSampler.this.topicTermSum)
                    {
                        ParallelLatentDirichletAllocationVectorGibbsSampler.this.topicTermSum[newTopic] += 1;
                    }
                    
                    this.occurrence++;
                }
            }

            return true;
        }
    }

    @Override
    public ThreadPoolExecutor getThreadPool()
    {
        if (this.threadPool == null)
        {
            this.setThreadPool(ParallelUtil.createThreadPool());
        }

        return this.threadPool;
    }

    @Override
    public void setThreadPool(final ThreadPoolExecutor threadPool)
    {
        this.threadPool = threadPool;
    }

    @Override
    public int getNumThreads()
    {
        return ParallelUtil.getNumThreads(this);
    }
}
