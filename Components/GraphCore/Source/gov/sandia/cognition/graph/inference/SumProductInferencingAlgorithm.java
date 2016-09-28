/*
 * File:                SumProductInferencingAlgorithm.java
 * Authors:             Tu-Thach Quach, Jeremy D. Wendt
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright 2016, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.graph.inference;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * Base class for Sum-Product inferencing algorithms on graphs/energy functions
 *
 * @author jdwendt, tong
 * @param <LabelType> The type for labels
 */
@PublicationReference(author
    = "Jonahtan S. Yedidia, William T. Freeman, and Yair Weiss",
    title = "Understanding Belief Propagation and its Generalizations",
    type = PublicationType.TechnicalReport,
    year = 2001, notes =
    {
        "Institution: Mitsubishi Electric Research Laboratories"
    })
abstract public class SumProductInferencingAlgorithm<LabelType>
    implements EnergyFunctionSolver<LabelType>
{

    /**
     * The default stopping epsilon that will be used
     */
    public static final double DEFAULT_EPS = 0.001;

    /**
     * The default maximum number of iterations that will be run
     */
    public static final int DEFAULT_MAX_ITERATIONS = 20;

    /**
     * The default number of threads that will be used
     */
    public static final int DEFAULT_NUM_THREADS = 4;

    /**
     * The actual stopping epsilon that will be used
     */
    private double eps;

    /**
     * The actual maximum number of iterations that will be run
     */
    private int maxNumIterations;

    /**
     * The actual number of threads that will be used
     */
    private int numThreads;

    /**
     * This internally stores the nodes with their values for the learning
     */
    protected List<Node<LabelType>> nodes;

    /**
     * The input energy function to learn against
     */
    protected EnergyFunction<LabelType> fn;

    /**
     * The edges as split for multi-threading
     */
    private ConcurrentLinkedQueue<List<Integer>> edgeGroups;

    /**
     * The nodes as split for multi-threading
     */
    private ConcurrentLinkedQueue<List<Node<LabelType>>> nodeGroups;

    /**
     * The splitting of the edges in the graph for the multithreading
     */
    private List<List<Integer>> edgeGroupsMaster;

    /**
     * The splitting of the nodes in the graph for the multithreading
     */
    private List<List<Node<LabelType>>> nodeGroupsMaster;

    /**
     * Creates a new solver with the specified settings.
     *
     * @param maxNumIterations The maximum number of iterations that will be run
     * @param eps The stopping epsilon that will be used
     * @param numThreads The number of threads that will be used
     */
    public SumProductInferencingAlgorithm(int maxNumIterations,
        double eps,
        int numThreads)
    {
        assert (maxNumIterations > 0);
        this.maxNumIterations = maxNumIterations;
        this.eps = eps;
        this.numThreads = numThreads;
        fn = null;
    }

    /**
     * Creates a new solver with the default settings excepting max number of
     * iterations.
     *
     * @param maxNumIterations The maximum number of iterations that will be run
     */
    public SumProductInferencingAlgorithm(int maxNumIterations)
    {
        this(maxNumIterations, DEFAULT_EPS, DEFAULT_NUM_THREADS);
    }

    /**
     * Creates a new solver with the default settings.
     */
    public SumProductInferencingAlgorithm()
    {
        this(DEFAULT_MAX_ITERATIONS, DEFAULT_EPS, DEFAULT_NUM_THREADS);
    }

    /**
     * Internal enum that maintains what portion of the solve computation is
     * being run
     */
    private enum SolverSetting
    {

        COMPUTE_MESSAGES,
        NORMALIZE_NODES,
        COMPUTE_BELIEFS;

    };

    /**
     * This class actually implements the solver steps. It's a distinct class as
     * that's required for threading in Java.
     */
    private class SolveThread
        implements Runnable
    {

        /**
         * The maximally changed value from this latest iteration that was
         * computed by this thread
         */
        private double delta;

        /**
         * This setting allows for changing what computation will occur with the
         * next data that is seen
         */
        private SolverSetting setting;

        @Override
        public void run()
        {
            delta = 0;
            switch (setting)
            {
                case COMPUTE_MESSAGES:
                    computeMesssages();
                    break;
                case NORMALIZE_NODES:
                    normalizeNodes();
                    break;
                case COMPUTE_BELIEFS:
                    computeBeliefs();
                    break;
                default:
                    throw new RuntimeException("Unhandled case, setting = "
                        + setting);
            }
        }

        /**
         * Computes the messages that are passed between neighboring nodes for
         * the assigned edges in the graph
         */
        private void computeMesssages()
        {
            while (true)
            {
                List<Integer> edges = edgeGroups.poll();
                // Null is only returned on edgeGroups.isEmpty()
                if (edges == null)
                {
                    return;
                }
                // Compute messages.
                for (int edge : edges)
                {
                    computeTemporaryMessage(edge);
                }
            }
        }

        /**
         * Normalizes the messages for each node in the assigned portion of the
         * graph
         */
        private void normalizeNodes()
        {
            while (true)
            {
                List<Node<LabelType>> nodes = nodeGroups.poll();
                // Null is only returned on nodeGroups.isEmpty()
                if (nodes == null)
                {
                    return;
                }
                // Normalize and update the messages.
                for (Node<LabelType> node : nodes)
                {
                    node.normalizeMessagesForSumProductAlgorithm();
                    delta = Math.max(delta, node.update());
                }
            }
        }

        /**
         * Computes the final belief after all messages have converged or max
         * iterations are reached
         */
        private void computeBeliefs()
        {
            while (true)
            {
                List<Node<LabelType>> nodes = nodeGroups.poll();
                // Null is only returned on nodeGroups.isEmpty()
                if (nodes == null)
                {
                    return;
                }
                // Compute beliefs for these nodes
                for (Node<LabelType> node : nodes)
                {
                    node.computeBeliefsForSumProductAlgorithm(fn);
                }
            }
        }

        /**
         * Returns the amount of change seen by this thread during this
         * iteration
         *
         * @return the amount of change seen by this thread during this
         * iteration
         */
        public double getDelta()
        {
            return delta;
        }

    };

    @Override
    public boolean solve()
    {
        boolean converged = false;
        edgeGroups.clear();
        nodeGroups.clear();
        int iterCount = 0;

        // Create the factory that creates the threads
        final ThreadFactory threadFactory = new ThreadFactory()
        {
            private final String baseName = "BpSolver-";

            private int counter = 0;

            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, baseName + counter++);
            }

        };

        final ExecutorService executorService = Executors.newFixedThreadPool(
            numThreads, threadFactory);

        List<SolveThread> threads = new ArrayList<>(numThreads);

        for (int i = 0; i < numThreads; ++i)
        {
            SolveThread thread = new SolveThread();
            threads.add(thread);
        }

        List<Future<?>> futures = new ArrayList<>(numThreads);

        while (!converged && iterCount < maxNumIterations)
        {
            // Initialize the per-thread queues ... they're emptied each
            // iteration
            copyFromMasters();

            // First update and run solvers for all of the messages
            loadAndStartFutures(futures, executorService, threads,
                SolverSetting.COMPUTE_MESSAGES);
            waitForThreadsToComplete(futures);

            // Now update and run solvers for the normalizing nodes step
            loadAndStartFutures(futures, executorService, threads,
                SolverSetting.NORMALIZE_NODES);
            waitForThreadsToComplete(futures);

            // Check to see if we're converged
            double delta = 0;
            for (int i = 0; i < numThreads; ++i)
            {
                delta = Math.max(delta, threads.get(i).getDelta());
            }
            if (delta < eps)
            {
                converged = true;
            }

            iterCount++;
        }

        // Compute final beliefs.
        copyFromMasters();

        // Now update and run solvers for the normalizing nodes step
        loadAndStartFutures(futures, executorService, threads,
            SolverSetting.COMPUTE_BELIEFS);
        waitForThreadsToComplete(futures);

        executorService.shutdown();

        return converged;

    }

    /**
     * Private helper that initializes and starts the multi-threading for a new
     * portion of the sum-product algorithm
     *
     * @param futures The Java object for threads
     * @param executorService The Java object for maintaining the threads
     * @param threads The local class that contains per-thread logic
     * @param setting The setting for the threads
     */
    private void loadAndStartFutures(List<Future<?>> futures,
        ExecutorService executorService,
        List<SolveThread> threads,
        SolverSetting setting)
    {
        for (int i = 0; i < numThreads; ++i)
        {
            threads.get(i).setting = setting;
            futures.add(executorService.submit(threads.get(i)));
        }
    }

    /**
     * Private helper that waits for the threads to all complete.
     *
     * @param futures The Java object for the threads
     */
    private void waitForThreadsToComplete(List<Future<?>> futures)
    {
        for (int i = 0; i < numThreads; ++i)
        {
            try
            {
                // This is a blocking call
                futures.get(i).get();
            }
            catch (ExecutionException | InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }

        futures.clear();
    }

    /**
     * Private helper that computes the temporary message for the specified edge
     * for the current iteration going in the specified direction (a different
     * message for each direction)
     *
     * @param edge The edge to compute the temporary message for (you can't
     * replace the current message yet as the current message may be needed for
     * other edges).
     */
    abstract protected void computeTemporaryMessage(int edge);

    /**
     * Private helper that copies the contents of the per-thread start/stop
     * values into the per-thread-run queues (that will be emptied)
     */
    private void copyFromMasters()
    {
        if (!(edgeGroups.isEmpty() && nodeGroups.isEmpty()))
        {
            throw new RuntimeException(
                "Can't copy if the destinations aren't empty");
        }
        for (List<Integer> edgeIds : edgeGroupsMaster)
        {
            edgeGroups.add(edgeIds);
        }
        for (List<Node<LabelType>> masterNodes : nodeGroupsMaster)
        {
            nodeGroups.add(masterNodes);
        }
    }

    /**
     * Children classes must implement this such that it adds the connections in
     * the correct direction for the input edge. The nodes object should be
     * updated by the child in this method.
     *
     * @param edgePair The two endpoints of the specified edge
     */
    abstract void initMessages(Pair<Integer, Integer> edgePair);

    @Override
    public void init(EnergyFunction<LabelType> f)
    {
        // Add nodes.
        nodes = new ArrayList<>(f.numNodes());
        for (int i = 0; i < f.numNodes(); i++)
        {
            Node<LabelType> node = new Node<>(i, f.getPossibleLabels(i));
            nodes.add(node);
        }

        // Add messages to node.
        for (int edge = 0; edge < f.numEdges(); edge++)
        {
            initMessages(f.getEdge(edge));
        }

        // Set all message values to 1.
        for (Node<LabelType> node : nodes)
        {
            node.resetToOne();
        }
        this.fn = f;

        // Initialize the multi-threading queues
        // Create the data queues
        edgeGroupsMaster = new ArrayList<>();
        int numPieces = (numThreads * 10);
        int numPerPiece = f.numEdges() / numPieces;
        int startAt = 0;
        for (int i = 0; i < numPieces - 1; ++i)
        {
            List<Integer> l = new ArrayList<>(numPerPiece);
            for (int j = 0; j < numPerPiece; ++j)
            {
                l.add(j + startAt);
            }
            edgeGroupsMaster.add(l);
            startAt += numPerPiece;
        }
        // Last piece may be different sized than all others
        List<Integer> l = new ArrayList<>(f.numEdges() - startAt);
        for (int i = startAt; i < f.numEdges(); ++i)
        {
            l.add(i);
        }
        edgeGroupsMaster.add(l);

        nodeGroupsMaster = new ArrayList<>();
        numPerPiece = f.numNodes() / numPieces;
        List<Node<LabelType>> labels = new ArrayList<>(numPerPiece);
        for (Node<LabelType> node : nodes)
        {
            labels.add(node);
            if (labels.size() == numPerPiece)
            {
                nodeGroupsMaster.add(labels);
                labels = new ArrayList<>(numPerPiece);
            }
        }
        if (!labels.isEmpty())
        {
            nodeGroupsMaster.add(labels);
        }
        nodeGroups = new ConcurrentLinkedQueue<>();
        edgeGroups = new ConcurrentLinkedQueue<>();
    }

    @Override
    public double getBelief(int i,
        int label)
    {
        return nodes.get(i).getBelief(label);
    }

}
