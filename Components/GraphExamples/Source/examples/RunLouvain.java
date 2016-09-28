/*
 * File:                RunLouvain.java
 * Authors:             Jeremy D. Wendt
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

import examples.io.GraphFileIo;
import gov.sandia.cognition.graph.DirectedNodeEdgeGraph;
import gov.sandia.cognition.graph.community.Louvain;

/**
 * A very simple application that runs Louvain on the graph stored in an input
 * file
 *
 * @author jdwendt
 */
public class RunLouvain
{

    /**
     * Arguments for this example
     */
    private static class Arguments
    {

        String inputFilename;

        Long randomSeed;

        boolean printCsv;

        boolean reverse;

        public Arguments()
        {
            inputFilename = "example.dot";
            randomSeed = null;
            printCsv = false;
            reverse = false;
        }

    };

    /**
     * Takes in the command line args and sets up the flags for running this
     *
     * @param args
     * @return
     */
    private static Arguments handleArgs(String[] args)
    {
        Arguments ret = new Arguments();
        if (args.length == 1)
        {
            ret.inputFilename = args[0];
        }
        if (ret.inputFilename == null)
        {
            System.err.println("Input filename required");
            return null;
        }
        return ret;
    }

    private static boolean headerLine = true;

    /**
     * Prints the results for a single node
     *
     * @param level
     * @param node
     * @param commId
     * @param a
     */
    private static void print(int level,
        String node,
        int commId,
        Arguments a)
    {
        if (a.printCsv)
        {
            if (headerLine)
            {
                System.out.println("CommLevel,NodeId,CommunityId");
                headerLine = false;
            }
            System.out.println(level + "," + node + "," + commId);
        }
        else
        {
            System.out.println("At level " + level + ", node \"" + node
                + "\" in community " + commId);
        }
    }

    public static void main(String[] args)
    {
        Arguments a = handleArgs(args);
        if (a == null)
        {
            return;
        }

        // Read in the graph file based on the extension specified
        DirectedNodeEdgeGraph<String> graph;
        String extension = a.inputFilename.substring(
            a.inputFilename.lastIndexOf("."));
        switch (extension.toLowerCase())
        {
            case ".dot":
                graph = GraphFileIo.readDotFile(a.inputFilename);
                break;
            case ".csv":
            case ".el":
            case ".txt":
                graph = GraphFileIo.readEdgeListFile(a.inputFilename);
                break;
            default:
                throw new RuntimeException("Unknown file extension: "
                    + extension);
        }

        // Run Louvain
        Louvain<String> louvain = new Louvain<>(graph, 1000, 1e-5);
        if (a.randomSeed != null)
        {
            louvain.setRandomSet(a.randomSeed);
        }
        Louvain.LouvainHierarchy<String> output = louvain.solveCommunities();

        // Print the results
        for (int i = 0; i < output.numLevels(); ++i)
        {
            for (int j = 0; j < graph.numNodes(); ++j)
            {
                String node = graph.getNode(j);
                int level = a.reverse ? output.numLevels() - i - 1 : i;
                print(level, node,
                    output.getCommunityForNodeAtLevel(node, i), a);
            }
        }
    }

}
