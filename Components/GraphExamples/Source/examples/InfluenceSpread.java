/*
 * File:                InfluenceSpread.java
 * Authors:             Tu-Thach Quach
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

package examples;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.graph.DirectedNodeEdgeGraph;
import gov.sandia.cognition.graph.DirectedWeightedNodeEdgeGraph;
import gov.sandia.cognition.graph.WeightedDenseMemoryGraph;
import gov.sandia.cognition.graph.inference.GraphWrappingEnergyFunction;
import gov.sandia.cognition.graph.inference.SumProductDirectedPropagation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Use this to calculate the influence spread of seed sets. See the main method
 * for all the parameters to pass into the program.
 *
 * @author Tu-Thach Quach
 *
 */
@PublicationReference(author
    = "Tu-Thach Quach and Jeremy D. Wendt",
    title
    = "A diffusion model for maximizing influence spread in large networks",
    type = PublicationType.Conference,
    publication
    = "Proceedings of the International Conference on Social Informatics", year
    = 2016)
public class InfluenceSpread
{

    public static final int MAX_NUM_ITERATIONS = 20;

    /**
     * Seed file is one line per seed set. Each line is
     * <p>
     * movie_name node_1 node_2 ... node_n
     * </p>
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public static ArrayList<ArrayList<String>> readSeeds(String filename)
        throws IOException
    {
        ArrayList<ArrayList<String>> seeds = new ArrayList<>();
        try (BufferedReader reader
            = new BufferedReader(new FileReader(filename)))
        {
            String line = reader.readLine();
            while (line != null)
            {
                String[] items = line.split("\\s+");
                ArrayList<String> list = new ArrayList<>();
                for (int i = 0; i < items.length; i++)
                {
                    list.add(items[i].trim());
                }
                seeds.add(list);

                line = reader.readLine();
            }
        }
        return seeds;
    }

    /**
     * Graph file stores edges and potentials per line:
     * <p>
     * node_i node_j potential_ij potential_ji
     * </p>
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public static DirectedWeightedNodeEdgeGraph<String> createFromEdgeList(
        String filename)
        throws IOException
    {
        DirectedWeightedNodeEdgeGraph<String> graph
            = new WeightedDenseMemoryGraph<>();

        try (BufferedReader reader
            = new BufferedReader(new FileReader(filename)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                line = line.trim();
                if (line.isEmpty())
                {
                    continue;
                }
                else if (line.startsWith("#"))
                {
                    continue;
                }
                String[] items = line.split("\\s+");
                String nodei = items[0].trim();
                String nodej = items[1].trim();
                double pij = Double.parseDouble(items[2]);
                double pji = Double.parseDouble(items[3]);

                graph.addEdge(nodei, nodej, pij);
                graph.addEdge(nodej, nodei, pji);
            }
        }

        return graph;
    }

    /**
     * Unary file stores unary potentials per line:
     * <p>
     * node_i potential_i
     * </p>
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public static Map<String, Double> readUnaryPotentials(String filename)
        throws IOException
    {
        Map<String, Double> unaryPotentials = new HashMap<>();
        try (BufferedReader reader
            = new BufferedReader(new FileReader(filename)))
        {
            String line = reader.readLine();
            while (line != null)
            {
                String[] items = line.split("\\s+");
                String node = items[0].trim();
                double p = Double.parseDouble(items[1]);
                unaryPotentials.put(node, p);

                line = reader.readLine();
            }
        }
        return unaryPotentials;
    }

    private static double propagate(
        GraphWrappingEnergyFunction<Integer, String> f,
        SumProductDirectedPropagation<Integer> solver,
        ArrayList<String> seeds,
        int numCores)
    {
        // Important to clear all seeds and stored costs.
        f.clearLabels();

        // Set seeds: the first element is the propagation name, not a seed
        // node.
        for (int i = 1; i < seeds.size(); i++)
        {
            String node = seeds.get(i);
            f.setLabel(node, 1);
        }

        boolean converged = solver.solve();
        if (!converged)
        {
            System.out.println("Warning: seeds for propagation " + seeds.get(0)
                + " did not converge.");
        }

        double prop = 0;
        for (int i = 0; i < f.numNodes(); i++)
        {
            prop += solver.getBelief(i, 1);
        }
        return prop;
    }

    /**
     * Arguments for this example
     */
    private static class Arguments
    {

        String inputFilename;

        String unaryPotentialsFilename;

        String seedsFilename;

        double minimumInfluenceProbability;

        int numCores;

        int batchSize;

        int sequence;

        int offset;

        String outputFilename;

        public Arguments()
        {
            // The graph filename.  Each line is source, destination, influence_src_to_dst, influence_dst_to_src
            inputFilename = "example.txt";
            // This file defines how likely each node is to adopt independent of incoming messages
            unaryPotentialsFilename = "unary.txt";
            // This specifies which nodes to set as seeds.  Each line is a whitespace-delimited list for a separate run
            seedsFilename = "seeds.txt";
            // If any entry in unaryPotentialsFilename is less than this, it's increased to this
            minimumInfluenceProbability = 0.001;
            // The number of cores to run the parallel code on
            numCores = 4;
            // The number of rows of seeds to run this run
            batchSize = 3;
            // Which number to start on within the current batch
            offset = 0;
            // Which batch to run (for if there are lots of seed rows and you're running different seed tests on different machines)
            sequence = 0;
            // Where to write results to
            outputFilename = "results.txt";
        }

    };

    /**
     * The program takes the following parameters (we will use flixster as an
     * example):
     * <p>
     * 1: the graph file storing directed edges with edge potentials p_ij in the
     * paper. This file has the following format on each line: nodei nodej p_ij
     * p_ji
     * </p>
     * <p>
     * 2: the unary file storing the unary potential \rho_i in the paper. This
     * file has the following format on each line: nodei \rho_i
     * </p>
     * <p>
     * 3: the file storing the propagations to propagate. Each line as the
     * following format: movie_id node_1 node_2 node_3 ... movie_n etc. The
     * nodes correspond to the seed nodes that are first to review the movie
     * among their friends. The program will then calculate the spread of
     * influence starting with these seed nodes.
     * </p>
     * <p>
     * 4: the minimum edge weight p_ij just in case some edge potentials are too
     * small. This is usually not needed and can be set to 0.
     * </p>
     * <p>
     * 5: the number of compute cores to use to compute the spread.
     * </p>
     * <p>
     * 6: the batch size. The program can be ran in parallel across many
     * machines using sbatch or whatever your favorite parallel job management
     * utility. Each machine is assigned a batch number and a size. A typical
     * batch size is the number of lines in s divided by the number of machines.
     * For a small network that does not need to be ran in parallel, set bs to
     * the number of lines in s and off (see below) to 0.
     * </p>
     * <p>
     * 7: the offset for this batch. It determines which line in s to start
     * processing. For example, if bs = 10 and off = 100, then this batch will
     * start processing line 100 and do so for the next 10 lines.
     * </p>
     * <p>
     * 8: the output file to write out. The format of each line of the output
     * file will be: movie_id predicted_spread.
     * </p>
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args)
        throws IOException
    {
        Arguments a = new Arguments();
        File graphFile = new File(a.inputFilename);
        File unaryFile = new File(a.unaryPotentialsFilename);
        File seedFile = new File(a.seedsFilename);

        if (!graphFile.exists())
        {
            System.out.println(graphFile.getPath() + " does not exist.");
            System.exit(0);
        }
        if (!unaryFile.exists())
        {
            System.out.println(unaryFile.getPath() + " does not exist.");
            System.exit(0);
        }
        if (!seedFile.exists())
        {
            System.out.println(seedFile.getPath() + " does not exist.");
            System.exit(0);
        }

        InfluencePotentialHandler.MINIMUM_EDGE_POTENTIAL
            = a.minimumInfluenceProbability;

        int start = a.offset + a.sequence * a.batchSize;

        System.out.println("Parameters:");
        System.out.println("Graph file: " + graphFile.getPath());
        System.out.println("Unary file: " + unaryFile.getPath());
        System.out.println("Seed file: " + seedFile.getPath());
        System.out.println("Minimum influence probability: "
            + InfluencePotentialHandler.MINIMUM_EDGE_POTENTIAL);
        System.out.println("Number of threads: " + a.numCores);
        System.out.println("Output file: " + a.outputFilename);
        System.out.println("Sequence ID: " + a.sequence);
        System.out.println("Batch size: " + a.batchSize);
        System.out.println("Offset: " + a.offset);
        System.out.println("Start: " + start);

        System.out.println("Reading unary potentials...");
        Map<String, Double> unaryPotentials = readUnaryPotentials(
            unaryFile.getPath());
        System.out.println("Number of unary potentials: "
            + unaryPotentials.size());

        // We originally use Integer instead of String to store the nodes. I
        // think that is more efficient (memory and possibly speed), but using
        // String is more flexible.
        System.out.println("Reading graph...");
        DirectedNodeEdgeGraph<String> graph = createFromEdgeList(
            graphFile.getPath());
        graph.getEdgeEndpointIds(0);
        System.out.println("Number of nodes: " + graph.getNumNodes());
        System.out.println("Number of edges: " + graph.getNumEdges());
        GraphWrappingEnergyFunction<Integer, String> f
            = new GraphWrappingEnergyFunction<>(graph,
                new InfluencePotentialHandler(graph, unaryPotentials));

        // Every node must have a unary potential.
        assert (unaryPotentials.size() == graph.getNumNodes());

        System.out.println("Reading seeds...");
        ArrayList<ArrayList<String>> allSeeds = readSeeds(seedFile.getPath());
        System.out.println("Number of seeds: " + allSeeds.size());

        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
            a.outputFilename)));
        SumProductDirectedPropagation<Integer> solver
            = new SumProductDirectedPropagation<>(MAX_NUM_ITERATIONS, 0.001,
                a.numCores);
        solver.init(f);
        try
        {
            for (int i = 0; i < a.batchSize; i++)
            {
                int index = start + i;
                if (index < allSeeds.size())
                {
                    ArrayList<String> propagationSeeds = allSeeds.get(index);
                    // The first entry is the propagation name, e.g.,
                    // product/movie name.
                    String propagationName = propagationSeeds.get(0);
                    System.out.println("Propagating " + propagationName
                        + " with " + (propagationSeeds.size() - 1) + " seeds");
                    double tic = System.currentTimeMillis();
                    double prop = propagate(f, solver, propagationSeeds,
                        a.numCores);
                    double toc = System.currentTimeMillis();
                    System.out.println("Time elapsed: " + (toc - tic) / 1000);
                    System.out.println(propagationName + '\t' + String.valueOf(
                        prop));

                    writer.write(propagationName + '\t' + String.valueOf(prop));
                    writer.newLine();
                    writer.flush();
                }
            }
        }
        finally
        {
            writer.close();
        }
        System.out.println("Done");
        System.out.println("Memory usage: "
            + (Runtime.getRuntime().totalMemory()
            - Runtime.getRuntime().freeMemory()));
    }

}
