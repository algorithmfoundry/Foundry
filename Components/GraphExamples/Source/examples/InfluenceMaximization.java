/*
 * File:                InfluenceMaximization.java
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
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.graph.DirectedNodeEdgeGraph;
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
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Use this to do influence maximization: find the set of seed nodes that
 * maximizes influence spread. This is an implementation of the CELF algorithm.
 * See the main method for explanation on the required parameters.
 *
 * @author Tu-Thach Quach
 *
 */
@PublicationReferences(references =
{
    @PublicationReference(author
        = "Tu-Thach Quach and Jeremy D. Wendt",
        title
        = "A diffusion model for maximizing influence spread in large networks",
        type = PublicationType.Conference,
        publication
        = "Proceedings of the International Conference on Social Informatics",
        year
        = 2016),
    @PublicationReference(author
        = "Jure Leskovec, A. Krause, C. Guestrin, C. Faloutsos, J. VanBriesen, and N. Glance",
        title = "Cost-effective outbreak detection in networks", type
        = PublicationType.Conference, publication
        = "Proceedings of the 13th ACM SIGKDD International Conference on Knowledge Discovery and Data Mining",
        year = 2007)
})
public class InfluenceMaximization
{

    /**
     * The CELF maximization routine.
     *
     * @param f The energy function to maximize.
     * @param nodeInfluenceMap The influence spread of each node alone.
     * @param k The number of nodes to find, e.g., seed size.
     * @param numCores The number of processing cores to use.
     * @return The list of computed influence.
     */
    public List<NodeMarginalInfluence> maximize(
        GraphWrappingEnergyFunction<Integer, String> f,
        Map<String, Double> nodeInfluenceMap,
        int k,
        int numCores)
    {
        // Calculate the base spread when without any seed node.
        SumProductDirectedPropagation<Integer> solver
            = new SumProductDirectedPropagation<>(
                InfluenceSpread.MAX_NUM_ITERATIONS, 0.001, numCores);
        solver.init(f);
        solver.solve();
        double baseSpread = 0;
        for (int node = 0; node < f.numNodes(); node++)
        {
            baseSpread += solver.getBelief(node, 1);
        }

        // Add all nodes to queue.
        PriorityQueue<NodeMarginalInfluence> queue = new PriorityQueue<>();
        for (Map.Entry<String, Double> entry : nodeInfluenceMap.entrySet())
        {
            NodeMarginalInfluence ui = new NodeMarginalInfluence();
            ui.id = entry.getKey();
            ui.gain = entry.getValue() - baseSpread;
            ui.spread = entry.getValue();
            ui.iteration = 0;
            queue.add(ui);
        }

        int numExamined = 0;
        double spread = 0; // Keeps track of the current spread achieved.
        List<NodeMarginalInfluence> topNodeList = new ArrayList<>();
        while (topNodeList.size() < k && !queue.isEmpty())
        {
            NodeMarginalInfluence ui = queue.poll();
            numExamined++;
            if (ui.iteration == topNodeList.size())
            {
                ui.numExamined = numExamined;
                topNodeList.add(ui);
                f.setLabel(ui.id, 1);
                spread = ui.spread;
            }
            else
            {
                f.setLabel(ui.id, 1);
                solver.solve();
                f.setLabel(ui.id, 0);

                double prop = 0;
                for (int node = 0; node < f.numNodes(); node++)
                {
                    prop += solver.getBelief(node, 1);
                }

                ui.gain = prop - spread;
                ui.spread = prop;
                ui.iteration = topNodeList.size();
                queue.add(ui);
            }

            System.out.println("Seed size: " + topNodeList.size()
                + "; examined: " + numExamined);
        }

        return topNodeList;
    }

    public static Map<String, Double> readInitialInfluence(String filename)
        throws IOException
    {
        Map<String, Double> nodeInfluenceMap = new HashMap<>();
        try (BufferedReader reader
            = new BufferedReader(new FileReader(filename)))
        {
            String line = reader.readLine();
            while (line != null)
            {
                String[] items = line.split("\\s+");
                String node = items[0].trim();
                double influence = Double.parseDouble(items[1]);
                nodeInfluenceMap.put(node, influence);

                line = reader.readLine();
            }
        }
        return nodeInfluenceMap;
    }

    /**
     * Arguments for this example
     */
    private static class Arguments
    {

        String inputFilename;

        String unaryPotentialsFilename;

        String influenceFilename;

        double minimumInfluenceProbability;

        int numCores;

        String outputFilename;

        int numInfluencers;

        public Arguments()
        {
            // The graph file.  Each line is source, dest, influence_src_to_dst, influence_dst_to_src
            inputFilename = "example.txt";
            // Each node's probability of adopting without external influence
            unaryPotentialsFilename = "unary.txt";
            // How much each node-as-a-seed results in overall propagation
            influenceFilename = "influence.txt";
            // The minimum unary potential.  If a line in unary.txt is lower than this, it will be increased to this.
            minimumInfluenceProbability = 0.001;
            // The number of cores to run the parallel codes on
            numCores = 4;
            // Where to write results.  Each line is selected node, resulting spread number of nodes examined by CELF to find this one, order of nodes chosen
            outputFilename = "results.txt";
            // The number of inlfluencers you want this to find
            numInfluencers = 3;
        }

    };

    /**
     * This is the main program that will find the top k seeds that maximizes
     * the spread. The parameters are
     * <p>
     * 1: the graph file that is exactly the same as the one in InfluenceSpread.
     * This file has the following format on each line: nodei nodej p_ij p_ji
     * </p>
     * <p>
     * 2: the unary file. Same as the one from InfluenceSpread. Each file is
     * node_id \rho_id.
     * </p>
     * <p>
     * 3: the influence file that stores the influence spread of each node as a
     * seed node. The format of each line is node_id spread. This file can be
     * obtained by running InfluenceSpread on a seed file where each line is
     * node_id node_id. The result of such run is an output file that stores the
     * spread of each node as seed node. This output file can then be used as
     * this influence file.
     * </p>
     * <p>
     * 4: the base influence spread without any seed node.
     * </p>
     * <p>
     * 5: the minimum pairwise term. This can be set to zero in most cases,
     * e.g., -p 0
     * </p>
     * <p>
     * 6: the number of compute cores to use, e.g., -n 4
     * </p>
     * <p>
     * 7: the output file name, e.g., -o output_spread.txt
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
        File nodeInfluenceFile = new File(a.influenceFilename);

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

        if (!nodeInfluenceFile.exists())
        {
            System.out.println(nodeInfluenceFile.getPath() + " does not exist.");
            System.exit(0);
        }

        InfluencePotentialHandler.MINIMUM_EDGE_POTENTIAL
            = a.minimumInfluenceProbability;

        System.out.println("Parameters:");
        System.out.println("Graph file: " + graphFile.getPath());
        System.out.println("Unary file: " + unaryFile.getPath());
        System.out.println("Influence file: " + nodeInfluenceFile.getPath());
        System.out.println("Minimum influence probability: "
            + InfluencePotentialHandler.MINIMUM_EDGE_POTENTIAL);
        System.out.println("Number of threads: " + a.numCores);
        System.out.println("Output file: " + a.outputFilename);
        System.out.println("Number of seeds: " + a.numInfluencers);

        System.out.println("Reading unary potentials...");
        Map<String, Double> unaryPotentials
            = InfluenceSpread.readUnaryPotentials(unaryFile.getPath());
        System.out.println("Number of unary potentials: "
            + unaryPotentials.size());

        System.out.println("Reading graph...");
        DirectedNodeEdgeGraph<String> graph
            = InfluenceSpread.createFromEdgeList(graphFile.getPath());
        System.out.println("Number of nodes: " + graph.numNodes());
        System.out.println("Number of edges: " + graph.numEdges());
        GraphWrappingEnergyFunction<Integer, String> f
            = new GraphWrappingEnergyFunction<>(graph,
                new InfluencePotentialHandler(graph, unaryPotentials));

        // Every node must have a unary potential.
        assert (unaryPotentials.size() == graph.numNodes());

        System.out.println("Reading individual node influence...");
        Map<String, Double> nodeInfluenceMap = readInitialInfluence(
            nodeInfluenceFile.getPath());

        System.out.println("Performing maximization...");
        InfluenceMaximization maximizer = new InfluenceMaximization();
        List<NodeMarginalInfluence> seeds = maximizer.maximize(f,
            nodeInfluenceMap, a.numInfluencers, a.numCores);

        // Write results to file.
        // NOTE: If you are running this with the default graph, you'll notice
        // that the best influence is found by grouping the influencers into
        // one community.  This is likely due to the very odd nature of this
        // very simple and contrived input graph.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
            a.outputFilename))))
        {
            for (NodeMarginalInfluence ui : seeds)
            {
                writer.write(ui.id + "\t" + ui.spread + "\t" + ui.numExamined
                    + "\t" + ui.iteration);
                writer.newLine();
            }
            writer.flush();
        }
        System.out.println("Done");
    }

    public class NodeMarginalInfluence
        implements Comparable<NodeMarginalInfluence>
    {

        public String id;

        public double gain;

        public double spread;

        public int iteration;

        public int numExamined;

        @Override
        public int compareTo(NodeMarginalInfluence o)
        {
            if (gain == o.gain)
            {
                return 0;
            }
            else if (gain < o.gain)
            {
                return 1;
            }
            else
            {
                return -1;
            }
        }

    }

}
