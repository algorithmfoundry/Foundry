/*
 * File:                GraphStats.java
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
import gov.sandia.cognition.graph.GraphMetrics;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class shows how you can perform various statistics on an input graph.
 *
 * @author jdwendt
 */
public class GraphStats
{

    /**
     * Specifies the arguments this code requires to run
     */
    private static class Arguments
    {

        private String inputFilename;

        private boolean numNodes;

        private boolean numEdges;

        private boolean assortativity;

        private boolean graphAll;

        private boolean nodeDegree;

        private boolean nodeNumNeighbors;

        private boolean nodeNumSuccessors;

        private boolean nodeNumTriangles;

        private boolean nodeAll;

        private boolean edgeJaccardSimilarity;

        private boolean edgeNumTriangles;

        private boolean edgeTriangleDensity;

        private boolean edgeAll;

        private boolean all;

        private boolean printAttrs;

        private boolean printCsv;

        public Arguments()
        {
            // Set this to the appropriate filename
            inputFilename = "example.dot";
            // You may set each of these separately true ....
            numNodes = false;
            numEdges = false;
            assortativity = false;
            nodeDegree = false;
            nodeNumNeighbors = false;
            nodeNumSuccessors = false;
            nodeNumTriangles = false;
            edgeJaccardSimilarity = false;
            edgeNumTriangles = false;
            edgeTriangleDensity = false;

            // .... or you may set groups of them true
            // All per-node metrics
            nodeAll = true;
            // All per-edge metrics
            edgeAll = false;
            // All graph-wide metrics (numNodes, numEdges)
            graphAll = false;
            // All metrics (per-node, per-edge, graph-wide)
            all = false;

            // Affects what gets output and how
            printCsv = false;
            printAttrs = false;
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
            System.err.println("Can't run without filename set");
            return null;
        }
        return ret;
    }

    /**
     * Writes out the results to stdout
     *
     * @param output
     * @param verticalCsv
     */
    private static void print(List<List<String>> output,
        boolean verticalCsv)
    {
        if (verticalCsv)
        {
            int maxLen = 0;
            for (List<String> row : output)
            {
                maxLen = Math.max(maxLen, row.size());
            }
            for (int i = 0; i < maxLen; ++i)
            {
                String comma = "";
                for (List<String> row : output)
                {
                    System.out.print(comma);
                    if (i < row.size())
                    {
                        System.out.print(row.get(i));
                    }
                    comma = ",";
                }
                System.out.println();
            }
        }
        else
        {
            for (List<String> row : output)
            {
                String comma = "";
                for (int i = 0; i < row.size(); ++i)
                {
                    System.out.print(comma + row.get(i));
                    comma = ", ";
                }
                System.out.println();
            }
        }
    }

    public static Set<String> getAllPossibleAttributes(
        Map<String, Map<String, String>> attrs)
    {
        Set<String> ret = new HashSet<>();
        for (Map.Entry<String, Map<String, String>> e : attrs.entrySet())
        {
            ret.addAll(e.getValue().keySet());
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

        // Read in the graph
        DirectedNodeEdgeGraph<String> graph;
        Map<String, Map<String, String>> nodeAttrs = new HashMap<>();;
        Map<String, Map<String, String>> edgeAttrs = new HashMap<>();
        String extension = a.inputFilename.substring(
            a.inputFilename.lastIndexOf("."));
        switch (extension.toLowerCase())
        {
            case ".dot":
                graph = GraphFileIo.readDotFile(a.inputFilename, nodeAttrs,
                    edgeAttrs);
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

        // This object stores a link to the graph and computes/stores metrics as requested
        GraphMetrics<String> metrics = new GraphMetrics<>(graph);
        List<List<String>> output = new ArrayList<>();
        List<String> tmp;
        // We now go through the various flags and add results to the output
        if (a.numNodes || a.graphAll || a.all)
        {
            tmp = new ArrayList<>();
            tmp.add("Graph NumNodes");
            tmp.add(Integer.toString(metrics.numNodes()));
            output.add(tmp);
        }
        if (a.numEdges || a.graphAll || a.all)
        {
            tmp = new ArrayList<>();
            tmp.add("Graph NumEdges");
            tmp.add(Integer.toString(metrics.numEdges()));
            output.add(tmp);
        }
        if (a.assortativity || a.graphAll || a.all)
        {
            tmp = new ArrayList<>();
            tmp.add("Graph Assortativity");
            tmp.add(Double.toString(metrics.degreeAssortativity()));
            output.add(tmp);
        }
        if (a.nodeDegree || a.nodeNumNeighbors || a.nodeNumSuccessors
            || a.nodeNumTriangles || a.nodeAll || a.printAttrs || a.all)
        {
            // Print the names of all nodes in order
            tmp = new ArrayList<>();
            tmp.add("Node Names");
            for (int i = 0; i < graph.numNodes(); ++i)
            {
                tmp.add(graph.getNode(i));
            }
            output.add(tmp);
        }
        if (a.printAttrs || a.nodeAll || a.all)
        {
            Set<String> attrNames = getAllPossibleAttributes(nodeAttrs);
            if (attrNames.size() > 0)
            {
                for (String attr : attrNames)
                {
                    tmp = new ArrayList<>();
                    tmp.add(attr);
                    for (int i = 0; i < graph.numNodes(); ++i)
                    {
                        String v = nodeAttrs.get(graph.getNode(i)).get(attr);
                        v = (v == null) ? "" : v;
                        tmp.add(v);
                    }
                    output.add(tmp);
                }
            }
        }
        if (a.nodeDegree || a.nodeAll || a.all)
        {
            tmp = new ArrayList<>();
            tmp.add("Node Degrees");
            for (int i = 0; i < graph.numNodes(); ++i)
            {
                tmp.add(Integer.toString(metrics.degree(i)));
            }
            output.add(tmp);
        }
        if (a.nodeNumNeighbors || a.nodeAll || a.all)
        {
            tmp = new ArrayList<>();
            tmp.add("Node NumNeighbors");
            for (int i = 0; i < graph.numNodes(); ++i)
            {
                tmp.add(Integer.toString(metrics.numNeighbors(i)));
            }
            output.add(tmp);
        }
        if (a.nodeNumSuccessors || a.nodeAll || a.all)
        {
            tmp = new ArrayList<>();
            tmp.add("Node NumSuccessors");
            for (int i = 0; i < graph.numNodes(); ++i)
            {
                tmp.add(Integer.toString(metrics.numSuccessors(i)));
            }
            output.add(tmp);
        }
        if (a.nodeNumTriangles || a.nodeAll || a.all)
        {
            tmp = new ArrayList<>();
            tmp.add("Node NumTriangles");
            for (int i = 0; i < graph.numNodes(); ++i)
            {
                tmp.add(Integer.toString(metrics.numNodeTriangles(i)));
            }
            output.add(tmp);
        }
        if (a.edgeJaccardSimilarity || a.edgeNumTriangles
            || a.edgeTriangleDensity || a.printAttrs || a.edgeAll || a.all)
        {
            // First print all edges in order
            tmp = new ArrayList<>();
            tmp.add("Edge Names");
            for (int i = 0; i < graph.numEdges(); ++i)
            {
                Pair<Integer, Integer> e = graph.getEdgeEndpointIds(i);
                String edge = "(" + graph.getNode(e.getFirst()) + "-"
                    + graph.getNode(e.getSecond()) + ")";
                tmp.add(edge);
            }
            output.add(tmp);
        }
        if (a.printAttrs || a.edgeAll || a.all)
        {
            Set<String> attrNames = getAllPossibleAttributes(edgeAttrs);
            if (attrNames.size() > 0)
            {
                for (String attr : attrNames)
                {
                    tmp = new ArrayList<>();
                    tmp.add(attr);
                    for (int i = 0; i < graph.numEdges(); ++i)
                    {
                        Pair<Integer, Integer> ee = graph.getEdgeEndpointIds(i);
                        String src = graph.getNode(ee.getFirst());
                        String dst = graph.getNode(ee.getSecond());
                        String edge = GraphFileIo.toEdgeName(src, dst);

                        String v = edgeAttrs.get(edge).get(attr);
                        v = (v == null) ? "" : v;
                        tmp.add(v);
                    }
                    output.add(tmp);
                }
            }
        }
        if (a.edgeJaccardSimilarity || a.edgeAll || a.all)
        {
            tmp = new ArrayList<>();
            tmp.add("Edge JaccardSimilarity");
            for (int i = 0; i < graph.numEdges(); ++i)
            {
                tmp.add(Double.toString(
                    metrics.getEdgeJaccardSimilarity(i)));
            }
            output.add(tmp);
        }
        if (a.edgeNumTriangles || a.edgeAll || a.all)
        {
            tmp = new ArrayList<>();
            tmp.add("Edge NumTriangles");
            for (int i = 0; i < graph.numEdges(); ++i)
            {
                tmp.add(Integer.toString(metrics.numEdgeTriangles(i)));
            }
            output.add(tmp);
        }
        if (a.edgeTriangleDensity || a.edgeAll || a.all)
        {
            tmp = new ArrayList<>();
            tmp.add("Edge TriangleDensity");
            for (int i = 0; i < graph.numEdges(); ++i)
            {
                tmp.add(Double.toString(
                    metrics.getPerEdgeTriangleDensity(i)));
            }
            output.add(tmp);
        }

        print(output, a.printCsv);
    }

}
