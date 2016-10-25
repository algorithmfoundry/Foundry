/*
 * File:                RunPpr.java
 * Authors:             Jeremy D. Wendt
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright 2016, Sandia Corporation. Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 */

package examples;

import examples.io.GraphFileIo;
import gov.sandia.cognition.collection.DoubleArrayList;
import gov.sandia.cognition.graph.DirectedNodeEdgeGraph;
import gov.sandia.cognition.graph.community.PersonalizedPageRank;
import gov.sandia.cognition.util.DefaultKeyValuePair;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Runs node-centric community identification via Personalized Page Rank
 *
 * @author jdwendt
 */
public class RunPpr
{

    /**
     * Arguments for this example
     */
    private static class Arguments
    {

        String inputFilename;

        String probeNodes;

        boolean scoresOnly;

        Long randomSeed;

        int numRunsScores = 20;

        int numRunsComm = 10;

        double tolerance = 0.01;

        public Arguments()
        {
            inputFilename = "example.dot";
            probeNodes = "A";
            randomSeed = null;
            scoresOnly = false;
        }

        List<String> getProbeNodes()
        {
            return Arrays.asList(probeNodes.split(","));
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
            System.err.println("Requires an input filename");
            return null;
        }

        return ret;
    }

    public static void main(String[] args)
    {
        Arguments a = handleArgs(args);
        if (a == null)
        {
            return;
        }

        // Read in the specified file
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

        // Run personalized page rank ego-community detection
        PersonalizedPageRank<String> ppr = new PersonalizedPageRank<>(graph,
            a.tolerance);
        if (a.randomSeed != null)
        {
            ppr.setRandomSet(a.randomSeed);
        }
        if (a.scoresOnly)
        {
            DoubleArrayList scores = ppr.getScoresForAllNodesMultirun(
                a.getProbeNodes(), a.numRunsScores);
            List<Pair<String, Double>> sorted = new ArrayList<>(scores.size());
            for (int i = 0; i < scores.size(); ++i)
            {
                sorted.add(new DefaultKeyValuePair<>(graph.getNode(i),
                    scores.get(i)));
            }
            Collections.sort(sorted, new Comparator<Pair<String, Double>>()
            {

                @Override
                public int compare(Pair<String, Double> o1,
                    Pair<String, Double> o2)
                {
                    return Double.compare(o2.getSecond(), o1.getSecond());
                }

            });

            // Print out resulting ppr scores
            System.out.println("NodeId,PprSimilarityScore");
            for (Pair<String, Double> node : sorted)
            {
                System.out.println(node.getFirst() + "," + node.getSecond());
            }
        }
        else
        {
            Set<Integer> ids = ppr.getCommunityForNodes(a.getProbeNodes(),
                a.numRunsScores,
                a.numRunsComm);

            // Print out the resulting nodes in probe's communitys
            System.out.println("Node Names in Community");
            for (int id : ids)
            {
                System.out.println(graph.getNode(id));
            }
        }
    }

}
