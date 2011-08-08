/*
 * File:                ParallelUtil.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Oct 10, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.algorithm;

import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.NamedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility methods for creating parallel algorithms.
 *
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class ParallelUtil
    extends Object
{

    /**
     * Indicates to the createThreadPool() method to estimate the "optimal"
     * number of threads for the computer currently executing the codes.
     */
    public static final int OPTIMAL_THREADS = -1;

    /**
     * Number of threads to use during a default call to createThreadPool()
     */
    private static int DEFAULT_NUM_THREADS = OPTIMAL_THREADS;

    /**
     * Protected constructor since this is a utility class.
     */
    protected ParallelUtil()
    {
        super();
    }

    /**
     * Gets the current default number of threads to use when called using the
     * default createThreadPool() method
     * @return
     * Number of threads to use during a default call to createThreadPool()
     */
    public static int getDefaultNumThreads()
    {
        return DEFAULT_NUM_THREADS;
    }

    /**
     * Sets the current default number of threads to use when calling the
     * default createThreadPool() method
     * @param defaultNumThreads
     * Number of threads to use during a default call to createThreadPool()
     */
    public static void setDefaultNumThreads(
        final int defaultNumThreads)
    {
        DEFAULT_NUM_THREADS = defaultNumThreads;
    }

    /**
     * Creates a thread pool with the "optimal" number of threads. The thread
     * pool creates daemon threads, so if all the non-daemon threads are
     * finished, then the application will not wait for the thread pool to be
     * cleaned up.
     *
     * @return
     * Thread pool with the "optimal" number of threads
     */
    public static ThreadPoolExecutor createThreadPool()
    {
        return createThreadPool(getDefaultNumThreads());
    }

    /**
     * Creates a thread pool with the given number of threads. The thread
     * pool creates daemon threads, so if all the non-daemon threads are
     * finished, then the application will not wait for the thread pool to be
     * cleaned up.
     * 
     * @param   numRequestedThreads
     *      Number of threads to create, or OPTIMAL_THREADS an "optimal" number
     *      of threads for the current computer.
     * @return
     *      Thread pool with the desired size that creates daemon threads.
     */
    public static ThreadPoolExecutor createThreadPool(
        int numRequestedThreads)
    {

        // Figure out how many cores or hyperthreads we have
        int numProcessors = Runtime.getRuntime().availableProcessors();

        int numThreads;

        // If the user wants the "optimal" thread count, then let's roll
        if (numRequestedThreads == OPTIMAL_THREADS)
        {
            // If we've got a two processors or less, then use all we got
            if (numProcessors <= 2)
            {
                numThreads = numProcessors;
            }
            // If we've got more than 2 processors, then leave one thread for
            // the GUI thread and the Operating System
            else
            {
                numThreads = numProcessors - 1;
            }
        }
        // If the user wants a specific number of treads, then let them
        // eat cake!!
        else
        {
            numThreads = numRequestedThreads;
        }

        // Make sure the user will get at least one thread
        if (numThreads < 1)
        {
            numThreads = 1;
        }

        return (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads,
            defaultDaemonThreadFactory());
    }

    /**
     * Gets the number of threads in a ParallelAlgorithm by querying the
     * thread pool 
     * @param algorithm
     * ParallelAlgorithm to consider
     * @return
     * Number of threads used by the ParallelAlgorithm's thread pool
     */
    public static int getNumThreads(
        final ParallelAlgorithm algorithm)
    {
        return (algorithm == null) ? 0 : getNumThreads(algorithm.getThreadPool());
    }

    /**
     * Gets the number of threads in a ThreadPoolExecutor
     * @param threadPool
     * ThreadPoolExecutor to consider
     * @return
     * Number of threads used by the thread pool
     */
    public static int getNumThreads(
        final ThreadPoolExecutor threadPool)
    {
        return (threadPool == null) ? 0 : threadPool.getMaximumPoolSize();
    }

    /**
     * Executes the given Callable tasks in parallel using a default thread
     * pool
     * @param <ResultType> Type of results returned by the Callable tasks.
     * @param tasks
     * Callable tasks to be split across multiple cores
     * @return
     * Collection of results from the Callables
     * @throws java.lang.InterruptedException
     * If interrupted
     * @throws java.util.concurrent.ExecutionException
     * If the Callable task can't execute its method
     */
    public static <ResultType> ArrayList<ResultType> executeInParallel(
        final Collection<? extends Callable<ResultType>> tasks)
        throws InterruptedException, ExecutionException
    {
        return executeInParallel(tasks, ParallelUtil.createThreadPool());
    }

    /**
     * Executes the given Callable tasks in parallel using a given thread
     * pool
     * @param <ResultType> Type of results returned by the Callable tasks.
     * @param tasks
     * Callable tasks to be split across multiple cores
     * @param threadPool 
     * Thread pool to use for the parallelization
     * @return
     * Collection of results from the Callables
     * @throws java.lang.InterruptedException
     * If interrupted
     * @throws java.util.concurrent.ExecutionException
     * If the Callable task can't execute its method
     */
    public static <ResultType> ArrayList<ResultType> executeInParallel(
        Collection<? extends Callable<ResultType>> tasks,
        ThreadPoolExecutor threadPool)
        throws InterruptedException, ExecutionException
    {

        Collection<Future<ResultType>> futures = threadPool.invokeAll(tasks);
        ArrayList<ResultType> results =
            new ArrayList<ResultType>(futures.size());
        for (Future<ResultType> future : futures)
        {
            results.add(future.get());
        }

        return results;

    }

    /**
     * Executes the given Callable tasks sequentially in series.
     * @param <ResultType> Type of results returned by the Callable tasks.
     * @param tasks
     * Callable tasks to execute sequentially
     * @return
     * Results from the sequential execution
     * @throws java.lang.Exception
     * If one of the Callables can't execute its method.
     */
    public static <ResultType> ArrayList<ResultType> executeInSequence(
        Collection<? extends Callable<ResultType>> tasks)
        throws Exception
    {
        ArrayList<ResultType> results = new ArrayList<ResultType>(tasks.size());
        for (Callable<ResultType> task : tasks)
        {
            results.add(task.call());
        }

        return results;

    }

    /**
     * Compares the times needed by running the tasks sequentially versus 
     * parallel.
     * @param <ResultType> Result types of the Callable tasks
     * @param tasks
     * Callable tasks to execute in parallel or sequentially
     * @return
     * Named value with the name being a report of the time taken for parallel
     * execution, sequential execution, the parallel to sequential ratio, and
     * the number of threads used in the parallel execution.  The value
     * is the parallel to sequential ratio.  That is, 1.0 means it takes just
     * as long to execute in parallel as sequentially.  A value greater than
     * 1.0 means it takes longer in parallel than sequentially.  A value less
     * than 1.0 means it takes longer sequentially than in parallel.
     */
    public static <ResultType> NamedValue<Double> compareTimes(
        Collection<? extends Callable<ResultType>> tasks)
    {
        return compareTimes(tasks, createThreadPool());
    }

    /**
     * Compares the times needed by running the tasks sequentially versus 
     * parallel.
     * @param <ResultType> Result types of the Callable tasks
     * @param tasks
     * Callable tasks to execute in parallel or sequentially
     * @param threadPool 
     * Thread pool to use for the parallelization
     * @return
     * Named value with the name being a report of the time taken for parallel
     * execution, sequential execution, the parallel to sequential ratio, and
     * the number of threads used in the parallel execution.  The value
     * is the parallel to sequential ratio.  That is, 1.0 means it takes just
     * as long to execute in parallel as sequentially.  A value greater than
     * 1.0 means it takes longer in parallel than sequentially.  A value less
     * than 1.0 means it takes longer sequentially than in parallel.
     */
    public static <ResultType> NamedValue<Double> compareTimes(
        Collection<? extends Callable<ResultType>> tasks,
        ThreadPoolExecutor threadPool)
    {
        try
        {
            // First, let's try things in series
            long seriesStart = System.currentTimeMillis();
            executeInSequence(tasks);
            long seriesStop = System.currentTimeMillis();

            long parallelStart = System.currentTimeMillis();
            executeInParallel(tasks, threadPool);
            long parallelStop = System.currentTimeMillis();

            double seriesTime = (seriesStop - seriesStart) / 1000.0;
            double parallelTime = (parallelStop - parallelStart) / 1000.0;
            double parallelToSeriesRatio = parallelTime / seriesTime;
            String name = "Series time = " + seriesTime + ", Parallel time = " +
                parallelTime + ", Parallel/Series ratio = " +
                parallelToSeriesRatio + " using " + ParallelUtil.getNumThreads(
                threadPool) + " threads.";
            return new DefaultNamedValue<Double>(name, parallelToSeriesRatio);
        }
        catch (Exception ex)
        {
            Logger.getLogger(ParallelUtil.class.getName()).log(Level.SEVERE,
                null, ex);
        }

        return null;

    }

    /**
     * Creates a version of the default thread factory from
     * {@code Executors.defaultThreadFactory()} that creates daemon threads.
     * Daemon threads mean that the application can exit even if the threads
     * are still alive.
     *
     * @return
     *      A default thread factory that creates daemon threads.
     */
    public static ThreadFactory defaultDaemonThreadFactory()
    {
        return new DaemonThreadFactory(Executors.defaultThreadFactory());
    }

    /**
     * Implements a thread factory for daemon threads. It just uses another
     * factory to actually create the threads and then calls
     * {@code setDaemon(true)}.
     */
    private static class DaemonThreadFactory
        implements ThreadFactory
    {

        /** The base factory to actually create the threads with. */
        protected ThreadFactory baseFactory;

        /**
         * Creates a new {@code DaemonThreadFactory}
         *
         * @param   baseFactory
         *      The base factory to actually create threads with.
         */
        public DaemonThreadFactory(
            final ThreadFactory baseFactory)
        {
            super();

            this.baseFactory = baseFactory;
        }

        public Thread newThread(
            final Runnable r)
        {
            // Create the thread.
            final Thread thread = this.baseFactory.newThread(r);

            // Set the thread to be a daemon, if we need to.
            if (!thread.isDaemon())
            {
                thread.setDaemon(true);
            }

            // Return the created thread.
            return thread;
        }
    }

}
