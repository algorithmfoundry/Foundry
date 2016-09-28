/*
 * File:                GraphFileIo.java
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

package examples.io;

import gov.sandia.cognition.graph.DenseMemoryGraph;
import gov.sandia.cognition.graph.DirectedNodeEdgeGraph;
import gov.sandia.cognition.graph.DirectedWeightedNodeEdgeGraph;
import gov.sandia.cognition.graph.WeightedDenseMemoryGraph;
import gov.sandia.cognition.util.Pair;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * This class reads in graph files and returns the graph stored in the file.
 *
 * @author jdwendt
 */
public class GraphFileIo
{

    /**
     * This is the attribute name used for node labels for dot-file graphs
     */
    private static final String NODE_LABEL_ATTR = "label";

    /**
     * This is the attribute name used for edge weights for dot-file graphs. If
     * the graph contains this attribute for _any_ edges, the resulting read-in
     * graph will be a WeightedDenseMemoryGraph with the double-value of the
     * input weights on the edges (1.0 where none are provided).
     */
    private static final String EDGE_WEIGHT_ATTR = "weight";

    /**
     * The most basic of graph formats: Each line defines an edge. If two values
     * are on the line, the line is considered a weight 1 edge between the nodes
     * defined by the two values (considered strings that define a name). If the
     * line contains three values, the third value must be a floating point or
     * integer value that defines the weight on the edge.
     *
     * @param filename The file to parse
     * @return The graph the file contained. NOTE: If the file contained three
     * values on _any_ of the lines, this will return a
     * DirectedWeightedNodeEdgeGraph.
     */
    public static DirectedNodeEdgeGraph<String> readEdgeListFile(String filename)
    {
        boolean addedWeights = false;
        DirectedWeightedNodeEdgeGraph<String> ret
            = new WeightedDenseMemoryGraph<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
            new FileInputStream(filename))))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] vals = line.trim().split("\\s+");
                // Java breaks empty strings into one empty component for some reason
                if (vals.length == 1 && vals[0].isEmpty())
                {
                    continue;
                }

                switch (vals.length)
                {
                    case 0:
                        continue;
                    case 2:
                        ret.addEdge(vals[0], vals[1]);
                        break;
                    case 3:
                        ret.addEdge(vals[0], vals[1],
                            Double.parseDouble(vals[2]));
                        addedWeights = true;
                        break;
                    default:
                        throw new RuntimeException(
                            "Unable to read current line \"" + line + "\" with "
                            + vals.length
                            + " separate chunks (only 2 or 3 (with weights) are "
                            + "supported).");
                }
            }
            if (!addedWeights)
            {
                DirectedNodeEdgeGraph<String> noWeights
                    = new DenseMemoryGraph<>(ret.numNodes(), ret.numEdges());
                for (int i = 0; i < ret.numEdges(); ++i)
                {
                    Pair<Integer, Integer> e = ret.getEdgeEndpointIds(i);
                    noWeights.addEdge(ret.getNode(e.getFirst()), ret.getNode(
                        e.getSecond()));
                }
                return noWeights;
            }
            // If weights were added to the edges, let's return those!
            return ret;
        }
        catch (IOException ioe)
        {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Simple helper that identifies single-line comments that fill the whole
     * line
     *
     * @param line The line to test for if it's a comment
     * @return True if the whole line is a comment, else false
     */
    private static boolean isDotCommentLine(String line)
    {
        line = line.trim();
        return line.isEmpty() || line.startsWith("//") || line.startsWith("#");
        // TODO: I should figure out how to handle multi-line comments /* */
        // TODO: Also, this doesn't handle // comments that aren't at the beginning of the line
    }

    /**
     * Identifies if the input line is a close-line for dot files
     *
     * @param line The line to test
     * @return True if the whole line is a close-line for dot files
     */
    private static boolean isDotCloseLine(String line)
    {
        return line.trim().equals("}");
    }

    /**
     * Cleans the input string to get it down to the "core" text (removes
     * edge-whitespace, surrounding quotes)
     *
     * @param s The string to clean
     * @return A cleaned version of the string
     */
    private static String clean(String s)
    {
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\""))
        {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }

    /**
     * Reads the input node's attributes looking for the "label" attribute to
     * assign that to the nodes name. Otherwise, it's the integer used by the
     * dot file for this node's name.
     *
     * @param node The integer value supplied by the dot file
     * @param nodeAttributes The attributes from the dot file for this node
     * @return the appropriate name (see above)
     */
    private static String toNodeName(String node,
        Map<String, String> nodeAttributes)
    {
        if ((nodeAttributes != null) && nodeAttributes.containsKey(
            NODE_LABEL_ATTR))
        {
            return nodeAttributes.get(NODE_LABEL_ATTR);
        }
        return node;
    }

    /**
     * Returns the weight for the edge whose attributes are passed in. 1.0 if no
     * "weight" attribute is included.
     *
     * @param edgeAttrs The edge's attributes from the Dot file.
     * @return The appropriate weight (see above)
     */
    private static double getEdgeWeight(Map<String, String> edgeAttrs)
    {
        for (String key : edgeAttrs.keySet())
        {
            if (key.toLowerCase().equals(EDGE_WEIGHT_ATTR))
            {
                return Double.parseDouble(edgeAttrs.get(key));
            }
        }
        return 1.0;
    }

    /**
     * Returns the graph found by reading the input graph file. No attributes
     * will be returned.
     *
     * @param filename The file to read
     * @return the graph found in that file.
     */
    public static DirectedNodeEdgeGraph<String> readDotFile(String filename)
    {
        return readDotFile(filename, null, null);
    }

    /**
     * Returns the graph found by reading in the input graph file. Attributes
     * will be returned in the input maps (if not null; one or both can be null
     * if those attributes aren't wanted). The maps will be
     * Node/EdgeName-to-Map-of-AttributeName-to-AttributeValue. EdgeName is
     * SrcNodeName,DstNodeName.
     *
     * @param filename The filename to read
     * @param nodeAttrs The map to return the node attributes from the dot file
     * in
     * @param edgeAttrs The map to return the edge attributes from the dot file
     * in
     * @return The graph read at the filename
     */
    public static DirectedNodeEdgeGraph<String> readDotFile(String filename,
        Map<String, Map<String, String>> nodeAttrs,
        Map<String, Map<String, String>> edgeAttrs)
    {
        try
        {
            return readDotFile(new InputStreamReader(new FileInputStream(
                filename)), nodeAttrs, edgeAttrs);
        }
        catch (FileNotFoundException fnfe)
        {
            throw new RuntimeException(fnfe);
        }
    }

    /**
     * Returns the graph found by reading the input graph. No attributes will be
     * returned.
     *
     * @param file The file to read
     * @return the graph found in that file.
     */
    public static DirectedNodeEdgeGraph<String> readDotFile(Reader file)
    {
        return readDotFile(file, null, null);
    }

    /**
     * Returns the graph found by reading in the input graph file. Attributes
     * will be returned in the input maps (if not null; one or both can be null
     * if those attributes aren't wanted). The maps will be
     * Node/EdgeName-to-Map-of-AttributeName-to-AttributeValue. EdgeName is
     * SrcNodeName,DstNodeName.
     *
     * @param file The file to read
     * @param nodeAttrsOut The data-structure for outputing the nodes'
     * attributes
     * @param edgeAttrsOut The data-structure for outputing the edges'
     * attributes
     * @return The read-in graph
     */
    public static DirectedNodeEdgeGraph<String> readDotFile(Reader file,
        Map<String, Map<String, String>> nodeAttrsOut,
        Map<String, Map<String, String>> edgeAttrsOut)
    {
        // Just make sure there's nothing already there polluting things
        if (nodeAttrsOut != null)
        {
            nodeAttrsOut.clear();
        }
        if (edgeAttrsOut != null)
        {
            edgeAttrsOut.clear();
        }

        // Initialize temporary locations for things.
        Set<String> nodes = new HashSet<>();
        Map<String, Map<String, String>> nodeAttributes = new HashMap<>();
        Map<String, Map<String, String>> edgeAttributes = new HashMap<>();
        Set<String> edges = new HashSet<>();
        Pattern fullStatement = Pattern.compile(".*;\\s*$");
        Pattern hasAttributes = Pattern.compile(".*\\[.*\\].*");
        boolean edgeWeightsFound = false;
        // Read the file and load into temporary locations
        try (BufferedReader br = new BufferedReader(file))
        {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null)
            {
                if (isDotCommentLine(line))
                {
                    continue;
                }
                if (firstLine)
                {
                    line = line.toLowerCase();
                    boolean isGraphFormat = (line.contains("graph")
                        || line.contains("digraph")) && line.contains("{");
                    if (!isGraphFormat)
                    {
                        throw new RuntimeException("Expected graph to start "
                            + "with graph or digraph and an open brace");
                    }
                    firstLine = false;
                    continue;
                }
                if (isDotCloseLine(line))
                {
                    break;
                }

                // We are now not a comment or the first line
                if (!fullStatement.matcher(line).matches())
                {
                    throw new RuntimeException("Unable to handle input line: "
                        + line + ".  Not a statement line.");
                }
                // If I don't strip the semicolon from the end, weird things happen when there are no attributes on this line
                line = line.substring(0, line.lastIndexOf(";"));
                // Handle attributes if they exist
                boolean attrWeight = false;
                Map<String, String> attributes = new HashMap<>();
                if (hasAttributes.matcher(line).matches())
                {
                    int start = line.indexOf("[");
                    String attrs = line.substring(start + 1, line.indexOf("]"));
                    String[] attrList = attrs.split(";|,");
                    for (String attr : attrList)
                    {
                        if (attr.trim().isEmpty())
                        {
                            continue;
                        }
                        String[] vs = attr.split("=");
                        if (vs.length != 2)
                        {
                            System.out.println("Weird attribute: " + attr);
                            continue;
                        }
                        String key = clean(vs[0]);
                        if (key.toLowerCase().equals(EDGE_WEIGHT_ATTR))
                        {
                            attrWeight = true;
                        }
                        attributes.put(key, clean(vs[1]));
                    }
                    line = line.substring(0, start);
                }
                boolean containsEdgeOp = line.contains("--") || line.contains(
                    "->");
                if (containsEdgeOp)
                {
                    // Then this is an edge
                    String[] endpts = line.split("--|->");
                    for (int i = 0; i < endpts.length - 1; ++i)
                    {
                        String src = endpts[i].trim();
                        String dst = endpts[i + 1].trim();
                        String edge = toEdgeName(src, dst);
                        edges.add(edge);
                        edgeAttributes.put(edge, attributes);
                        if (attrWeight)
                        {
                            edgeWeightsFound = true;
                        }
                    }
                }
                else
                {
                    // Then this is a node
                    line = line.trim();
                    nodes.add(line);
                    nodeAttributes.put(line, attributes);
                }
            }
        }
        catch (IOException ioe)
        {
            throw new RuntimeException(ioe);
        }

        // Now, write to the output datastructures
        DirectedNodeEdgeGraph<String> ret;
        if (edgeWeightsFound)
        {
            ret = new WeightedDenseMemoryGraph<>(nodes.size(), edges.size());
        }
        else
        {
            ret = new DenseMemoryGraph<>(nodes.size(), edges.size());
        }
        for (String node : nodes)
        {
            String nodeName = toNodeName(node, nodeAttributes.get(node));
            ret.addNode(nodeName);
            if (nodeAttrsOut != null && !nodeAttributes.get(node).isEmpty())
            {
                nodeAttrsOut.put(nodeName, new HashMap<>());
                for (Map.Entry<String, String> e
                    : nodeAttributes.get(node).entrySet())
                {
                    if (e.getKey().equals(NODE_LABEL_ATTR))
                    {
                        continue;
                    }
                    nodeAttrsOut.get(nodeName).put(e.getKey(), e.getValue());
                }
            }
        }
        for (String edge : edges)
        {
            String[] endpts = edge.split(",");
            if (endpts.length != 2)
            {
                throw new RuntimeException("It seems a node name contains a "
                    + "comma or something.  Somehow, I created this edge w/o "
                    + "exactly two endpoints: " + edge);
            }
            String src = toNodeName(endpts[0], nodeAttributes.get(endpts[0]));
            String dst = toNodeName(endpts[1], nodeAttributes.get(endpts[1]));
            if (edgeWeightsFound)
            {
                double w = getEdgeWeight(edgeAttributes.get(edge));
                ((WeightedDenseMemoryGraph<String>) ret).addEdge(src, dst, w);
            }
            else
            {
                ret.addEdge(src, dst);
            }
            if (edgeAttrsOut != null)
            {
                String externalKey = toEdgeName(src, dst);
                edgeAttrsOut.put(externalKey, new HashMap<>());
                for (Map.Entry<String, String> e
                    : edgeAttributes.get(edge).entrySet())
                {
                    if (e.getKey().equals(EDGE_WEIGHT_ATTR))
                    {
                        continue;
                    }
                    edgeAttrsOut.get(externalKey).put(e.getKey(), e.getValue());
                }
            }
        }
        return ret;
    }

    /**
     * Appends the attributes to the input string builder in the dot-file graph
     * format
     *
     * @param sb The StringBuilder to add to
     * @param attrs The attributes to process
     * @param predecessorExists True if any attributes have been added before
     * this
     */
    private static void appendAttrs(StringBuilder sb,
        Map<String, String> attrs,
        boolean predecessorExists)
    {
        if (attrs != null)
        {
            for (Map.Entry<String, String> e : attrs.entrySet())
            {
                if (predecessorExists)
                {
                    sb.append("; ");
                }
                predecessorExists = true;
                sb.append(e.getKey()).append("=\"").append(e.getValue()).append(
                    "\"");
            }
        }
    }

    /**
     * Helper that creates a node's dot-format line for the node id, name, and
     * attributes
     *
     * @param id The node's id
     * @param nodeName The node's name
     * @param attrs The node's parameters (can be null)
     * @return The string representation of the input node in dot-file format
     */
    private static String toNodeLine(int id,
        String nodeName,
        Map<String, String> attrs)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("    ").append(id).append(" [ label=\"").append(nodeName).append(
            "\"");
        appendAttrs(sb, attrs, true);
        sb.append(" ] ;\n");

        return sb.toString();
    }

    /**
     * Helper that creates an edge's dot-format line for the input edge, weight
     * and attributes.
     *
     * @param edge The source and destination node's ids for the edge
     * @param weight The edge's weight (can be null)
     * @param attrs The edge's attributes (can be null)
     * @return The string representation of the input edge in dot-file format
     */
    private static String toEdgeLine(Pair<Integer, Integer> edge,
        Double weight,
        Map<String, String> attrs)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("    ").append(edge.getFirst()).append(" -> ").append(
            edge.getSecond());
        boolean addAttributes = weight != null || attrs != null;
        if (addAttributes)
        {
            sb.append(" [ ");
        }
        boolean predecessorAttr = false;
        if (weight != null)
        {
            sb.append(EDGE_WEIGHT_ATTR).append("=\"").append(weight).append("\"");
            predecessorAttr = true;
        }
        appendAttrs(sb, attrs, predecessorAttr);
        if (addAttributes)
        {
            sb.append(" ]");
        }
        sb.append(" ;\n");

        return sb.toString();
    }

    /**
     * Writes the input graph to a dot-file-format with the given graph (which
     * may be weighted) and attributes for the nodes and edges.
     *
     * @param filename The file to write the output to
     * @param graph The graph to write out
     * @param nodeAttrs The nodes' attributes to write (can be null)
     * @param edgeAttrs The edges' attributes to write (can be null)
     */
    public static void writeDotFile(String filename,
        DirectedNodeEdgeGraph<String> graph,
        Map<String, Map<String, String>> nodeAttrs,
        Map<String, Map<String, String>> edgeAttrs)
    {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename)))
        {
            bw.write("digraph agraph {\n");
            for (int i = 0; i < graph.numNodes(); ++i)
            {
                String node = graph.getNode(i);
                bw.write(toNodeLine(i, node, nodeAttrs == null ? null
                    : nodeAttrs.get(node)));
            }
            bw.write("\n\n\n");
            for (int i = 0; i < graph.numEdges(); ++i)
            {
                Double weight = null;
                if (graph instanceof DirectedWeightedNodeEdgeGraph)
                {
                    weight
                        = ((DirectedWeightedNodeEdgeGraph) graph).getEdgeWeight(
                            i);
                }
                Pair<Integer, Integer> edge = graph.getEdgeEndpointIds(i);
                bw.write(toEdgeLine(edge, weight, edgeAttrs == null ? null
                    : edgeAttrs.get(toEdgeName(graph.getNode(edge.getFirst()),
                        graph.getNode(edge.getSecond())))));
            }
            bw.write("}\n");
        }
        catch (IOException ioe)
        {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Simple helper that forms the edge name for the edge between src and dst
     * to be used in storing the edge attribute maps.
     *
     * @param src The source node's name
     * @param dst The destination node's name
     * @return A standard-format name for the edge src,dst
     */
    public static String toEdgeName(String src,
        String dst)
    {
        return src + "," + dst;
    }

}
