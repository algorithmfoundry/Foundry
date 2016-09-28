/*
 * File:                GraphMetrics.java
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

package gov.sandia.cognition.graph;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.util.DefaultKeyValuePair;
import gov.sandia.cognition.util.DoubleVector;
import gov.sandia.cognition.util.IntVector;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * This class is intended to allow programmers to get any number of graph
 * metrics for any input graph without bloating the intentionally spartan graph
 * implementations and interfaces. This class computes all values of a given
 * type when one of them is requested as the runtime costs for all is often
 * quite similar to the runtime costs for one -- although at the cost of
 * increased storage. However, if no instances of a metric are ever requested
 * (e.g., no Jaccard similarities are requested), then they are never computed
 * or stored (unless required as a prerequisite for some other requested
 * metric).
 *
 * NOTE: If the graph is changed after any metric is computed, this has no way
 * of knowing intrinsically. Thus, if you have altered the graph by adding a
 * node or an edge, you need to call the clear method herein to remove old
 * cached values.
 *
 * @author jdwendt
 * @param <NodeNameType> The type for node names in the input graph
 */
public class GraphMetrics<NodeNameType>
{

    /**
     * The graph whose metrics are computed and stored by this class
     */
    private final DirectedNodeEdgeGraph<NodeNameType> graph;

    /**
     * The set of neighbor (undirected) node ids for all nodes in the graph
     */
    private List<Set<Integer>> allNodeNeighbors;

    /**
     * The set of successor (directed) node ids for all nodes in the graph
     */
    private List<Set<Integer>> allNodeSuccessors;

    /**
     * The list of degrees for all nodes in the graph
     */
    private IntVector allNodeDegrees;

    /**
     * The other two node ids for all triangles that each node in the graph is
     * in
     */
    private List<Set<Pair<Integer, Integer>>> allNodeTriangles;

    /**
     * The assortativity for the whole graph
     */
    private double degreeAssortativity;

    /**
     * The Jaccard similarity for all edges in the graph -- That is, the Jaccard
     * similarity of the neighbors (undirected) for the endpoints on each edge
     */
    private DoubleVector perEdgeJaccardSimilarity;

    /**
     * The other node involved in all triangles that any edge is in
     */
    private List<Set<Integer>> allEdgeTriangles;

    /**
     * The percentage of closed triangles compared to the total number that any
     * edge could be in
     */
    private DoubleVector perEdgeTriangleDensity;

    /**
     * The eccentricities of all nodes in the unweighted graph
     */
    private IntVector perNodeEccentricity;

    private int radius;

    private int diameter;

    private Boolean isWcc;

    /**
     * Initialize this as an empty metrics class surrounding the input graph.
     * Note that if you alter the input graph after creating this class, you
     * should call the clear method.
     *
     * @param graph The graph whose metrics will be computed and returned by
     * this
     */
    public GraphMetrics(DirectedNodeEdgeGraph<NodeNameType> graph)
    {
        this.graph = graph;
        allNodeNeighbors = null;
        allNodeSuccessors = null;
        allNodeDegrees = null;
        allNodeTriangles = null;
        degreeAssortativity = -Double.MAX_VALUE;
        perEdgeJaccardSimilarity = null;
        allEdgeTriangles = null;
        perEdgeTriangleDensity = null;
        radius = diameter = Integer.MAX_VALUE;
        perNodeEccentricity = null;
        isWcc = null;
    }

    /**
     * Clears all cached metrics for the originally input graph. This method
     * created as it is possible that the graph is modified after being passed
     * into the constructor of this class. If the graph is changed, this class
     * has no way of knowing. This method resets all previously computed
     * metrics.
     */
    public void clear()
    {
        allNodeNeighbors = null;
        allNodeSuccessors = null;
        allNodeDegrees = null;
        allNodeTriangles = null;
        degreeAssortativity = -Double.MAX_VALUE;
        perEdgeJaccardSimilarity = null;
        allEdgeTriangles = null;
        perEdgeTriangleDensity = null;
        radius = diameter = Integer.MAX_VALUE;
        perNodeEccentricity = null;
        isWcc = null;
    }

    /**
     * Returns the number of nodes in the graph. (O(1) on all calls)
     *
     * @return the number of nodes in the graph
     */
    public int numNodes()
    {
        return graph.numNodes();
    }

    /**
     * Returns the number of edges in the graph. (O(1) on all calls)
     *
     * @return the number of edges in the graph
     */
    public int numEdges()
    {
        return graph.numEdges();
    }

    /**
     * Private helper that tests whether node degrees has been initialized. O(1)
     *
     * @return true if has been initialized, else false
     */
    private boolean isInitializedNodeDegrees()
    {
        return allNodeDegrees != null;
    }

    /**
     * Initializes the unweighted degree values for all nodes in the graph. Note
     * that repeated edges count once for each repeat. Furthermore, self-loops
     * increase the degree by 2. O(n + m)
     */
    public void initializeNodeDegrees()
    {
        int n = numNodes();
        allNodeDegrees = new IntVector(n);
        for (int i = 0; i < n; ++i)
        {
            allNodeDegrees.add(0);
        }
        int m = numEdges();
        for (int i = 0; i < m; ++i)
        {
            Pair<Integer, Integer> edge = graph.getEdgeEndpointIds(i);
            allNodeDegrees.plusEquals(edge.getFirst(), 1);
            allNodeDegrees.plusEquals(edge.getSecond(), 1);
        }
    }

    /**
     * Return the degree for the input nodeId. O(n+m) on first call to either
     * degree method, O(1) on all further calls.
     *
     * @param nodeId The node id of the node whose degree is wanted
     * @return the degree for the input nodeId
     */
    public int degree(int nodeId)
    {
        if (!isInitializedNodeDegrees())
        {
            initializeNodeDegrees();
        }
        return allNodeDegrees.get(nodeId);
    }

    /**
     * Return the degree for the input node. O(n+m) on the first call to either
     * degree method, O(1) on all further calls.
     *
     * @param nodeName The name of the node whose degree is wanted
     * @return the degree for the input nodeName
     */
    public int degree(NodeNameType nodeName)
    {
        return degree(graph.getNodeId(nodeName));
    }

    /**
     * Private helper that tests if the node neighbors have been computed. O(1)
     *
     * @return true if initialized
     */
    private boolean isInitializedNodeNeighbors()
    {
        return allNodeNeighbors != null;
    }

    /**
     * Initializes the neighbors (undirected) for all nodes in the graph at
     * once. O(n + m)
     */
    public void initializeNodeNeighbors()
    {
        if (!isInitializedNodeDegrees())
        {
            initializeNodeDegrees();
        }
        int n = numNodes();
        allNodeNeighbors = new ArrayList<>(n);
        for (int i = 0; i < n; ++i)
        {
            int di = degree(i);
            allNodeNeighbors.add(new HashSet<>(di));
        }
        int m = numEdges();
        for (int i = 0; i < m; ++i)
        {
            Pair<Integer, Integer> e = graph.getEdgeEndpointIds(i);
            allNodeNeighbors.get(e.getFirst()).add(e.getSecond());
            allNodeNeighbors.get(e.getSecond()).add(e.getFirst());
        }
    }

    /**
     * Returns the number of neighbors for the input node id. This is different
     * from degree as repeated edges and self loops don't increase the count.
     * O(n+m) for first neighbor method called, O(1) for all later.
     *
     * @param nodeId The node whose number of neighbors is wanted
     * @return The number of neighbors for the input node
     */
    public int numNeighbors(int nodeId)
    {
        if (!isInitializedNodeNeighbors())
        {
            initializeNodeNeighbors();
        }
        return allNodeNeighbors.get(nodeId).size();
    }

    /**
     * Returns the number of neighbors for the input node name. This is
     * different from degree as repeated edges and self loops don't increase the
     * count. O(n+m) for the first neighbor method called, O(1) for all later.
     *
     * @param nodeName The node whose number of neighbors is wanted
     * @return the number of neighbors for the input node
     */
    public int numNeighbors(NodeNameType nodeName)
    {
        return numNeighbors(graph.getNodeId(nodeName));
    }

    /**
     * Returns the ids of all neighbors for the input node id. Note that
     * neighbors connected multiple times by repeated edges are included only
     * once. O(n+m) for the first neighbor method called, O(1) for all later.
     *
     * @param nodeId The ndoe whose neighbor ids are wanted
     * @return the ids of all neighbors.
     */
    public Set<Integer> neighborIds(int nodeId)
    {
        if (!isInitializedNodeNeighbors())
        {
            initializeNodeNeighbors();
        }
        return Collections.unmodifiableSet(allNodeNeighbors.get(nodeId));
    }

    /**
     * Returns the ids of all neighbors for the input node name. Note that
     * neighbors connected multiple times by repeated edges are included only
     * once. O(n+m) for the first neighbor method called, O(1) for all later.
     *
     * @param nodeName The node whose neighbor ids are wanted
     * @return the ids of all neighbors.
     */
    public Set<Integer> neighborIds(NodeNameType nodeName)
    {
        return neighborIds(graph.getNodeId(nodeName));
    }

    /**
     * Returns the names of all neighbors for the input node id. Note that
     * neighbors connected multiple times by repeated edges are included only
     * once. O(n+m) for the first neighbor method called, O(1) for all later.
     *
     * @param nodeId The node whose neighbor names are wanted
     * @return the names of all neighbors
     */
    public Set<NodeNameType> neighbors(int nodeId)
    {
        Set<Integer> ids = neighborIds(nodeId);
        Set<NodeNameType> ret = new HashSet<>(ids.size());
        for (Integer id : ids)
        {
            ret.add(graph.getNode(id));
        }

        return ret;
    }

    /**
     * Returns the names of all neighbors for the input node name. Note that
     * neighbors connected multiple times by repeated edges are included only
     * once. O(n+m) for the first neighbor method called, O(1) for all later.
     *
     * @param nodeName The node whose neighbor names are wanted
     * @return the names of all neighbors
     */
    public Set<NodeNameType> neighbors(NodeNameType nodeName)
    {
        return neighbors(graph.getNodeId(nodeName));
    }

    /**
     * Private helper that returns if node successors has been initialized. O(1)
     *
     * @return true if initialized; else false.
     */
    private boolean isInitializedNodeSuccessors()
    {
        return allNodeSuccessors != null;
    }

    /**
     * Initializes the node successors (directed version of neighbors). O(n+m).
     */
    public void initializeNodeSuccessors()
    {
        int n = numNodes();
        allNodeSuccessors = new ArrayList<>(n);
        for (int i = 0; i < n; ++i)
        {
            allNodeSuccessors.add(new HashSet<>());
        }
        int m = numEdges();
        for (int i = 0; i < m; ++i)
        {
            Pair<Integer, Integer> e = graph.getEdgeEndpointIds(i);
            allNodeSuccessors.get(e.getFirst()).add(e.getSecond());
        }
    }

    /**
     * Returns the number of direct successors for the input node. This is
     * different from degree in two ways: First, it doesn't count repeated
     * edges. Second, it is directed; it counts nodes that can be reached by an
     * edge originating at this node. O(m+n) for first successors method called,
     * O(1) for later.
     *
     * @param nodeId The node whose successor count is desired
     * @return the number of successors
     */
    public int numSuccessors(int nodeId)
    {
        if (!isInitializedNodeSuccessors())
        {
            initializeNodeSuccessors();
        }
        return allNodeSuccessors.get(nodeId).size();
    }

    /**
     * Returns the number of direct successors for the input node. This is
     * different from degree in two ways: First, it doesn't count repeated
     * edges. Second, it is directed; it counts nodes that can be reached by an
     * edge originating at this node. O(m+n) for first successors method called,
     * O(1) for later.
     *
     * @param nodeName The node whose successor count is desired
     * @return the number of successors
     */
    public int numSuccessors(NodeNameType nodeName)
    {
        return numSuccessors(graph.getNodeId(nodeName));
    }

    /**
     * Returns the id for all direct successors for the input node. This is
     * different from neighbors as it is directed; it returns nodes that can be
     * reached by an edge originating at this node. O(m+n) for first successors
     * method called, O(1) for later.
     *
     * @param nodeId The node whose successors' ids is desired
     * @return the ids of successors for this node
     */
    public Set<Integer> successorIds(int nodeId)
    {
        if (!isInitializedNodeSuccessors())
        {
            initializeNodeSuccessors();
        }
        return Collections.unmodifiableSet(allNodeSuccessors.get(nodeId));
    }

    /**
     * Returns the ids of the direct successors for the input node. This is
     * different from neighbors as it is directed; it returns nodes that can be
     * reached by an edge originating at this node. O(m+n) for first successors
     * method called, O(1) for later.
     *
     * @param nodeName The node whose successors' ids is desired
     * @return the ids of successors for this node
     */
    public Set<Integer> successorIds(NodeNameType nodeName)
    {
        return successorIds(graph.getNodeId(nodeName));
    }

    /**
     * Returns the node names for all direct successors to the input node. This
     * is different from neighbors as it is directed; it returns nodes that can
     * be reached by an edge originating at this node. O(m+n) for first
     * successors method called, O(1) for later.
     *
     * @param nodeId The node whose successors' names is desired
     * @return the names of successors for this node
     */
    public Set<NodeNameType> successors(int nodeId)
    {
        Set<Integer> ids = successorIds(nodeId);
        Set<NodeNameType> ret = new HashSet<>(ids.size());
        for (Integer id : ids)
        {
            ret.add(graph.getNode(id));
        }

        return ret;
    }

    /**
     * Returs the node names for all direct successors to the input node. This
     * is different from neighbors as it is directed; it returns nodes that can
     * be reached by an edge originating at this node. O(m+n) for first
     * successors method called, O(1) for later.
     *
     * @param nodeName The node whose successors' names is desired
     * @return the names of successors for this node
     */
    public Set<NodeNameType> successors(NodeNameType nodeName)
    {
        return successors(graph.getNodeId(nodeName));
    }

    /**
     * Private helper that tests if node triangles are initialized.
     *
     * @return true if initialized, else false
     */
    private boolean isInitializedNodeTriangles()
    {
        return allNodeTriangles != null;
    }

    /**
     * Initializes the datastructure for all triangles that all nodes and edges
     * participate in. Note that this implementation uses the neighbors for each
     * node, so does not allow triangles with only two nodes (where the third
     * edge is a self-loop) nor does it create repeated triangles for nodes with
     * repeated edges. According to the publication, this is O(m^(3/2)) in the
     * worst case and O(m) at best.
     */
    @PublicationReference(author = "Siddharth Suri and Sergei Vassilvitskii",
        title = "Counting Triangles and the Curse of the Last Reducer",
        year = 2011,
        publication = "Proceedings of the World Wide Web Conference (WWW)",
        type = PublicationType.Conference)
    public void initializeNodeTriangles()
    {
        if (!isInitializedNodeDegrees())
        {
            initializeNodeDegrees();
        }
        if (!isInitializedNodeNeighbors())
        {
            initializeNodeNeighbors();
        }
        int n = numNodes();
        int m = numEdges();
        // First I need to create a strict ordering on the nodes based on degree
        List<Pair<Integer, Integer>> degreeList = new ArrayList<>(n);
        for (int i = 0; i < n; ++i)
        {
            degreeList.add(new DefaultKeyValuePair<>(i, degree(i)));
        }
        Collections.sort(degreeList, new Comparator<Pair<Integer, Integer>>()
        {

            @Override
            public int compare(Pair<Integer, Integer> o1,
                Pair<Integer, Integer> o2)
            {
                return Integer.compare(o1.getSecond(), o2.getSecond());
            }

        });
        List<Integer> nodeOrder = new ArrayList<>(n);
        for (int i = 0; i < n; ++i)
        {
            nodeOrder.add(0);
        }
        for (int i = 0; i < n; ++i)
        {
            nodeOrder.set(degreeList.get(i).getFirst(), i);
        }
        // Next I need to make a map from edge pairs to edge index
        Map<Pair<Integer, Integer>, Set<Integer>> edgeMap = new HashMap<>(2 * m);
        allEdgeTriangles = new ArrayList<>(m);
        for (int i = 0; i < m; ++i)
        {
            Pair<Integer, Integer> edge = graph.getEdgeEndpointIds(i);
            allEdgeTriangles.add(new HashSet<>());
            if (!edgeMap.containsKey(edge))
            {
                edgeMap.put(edge, new HashSet<>());
            }
            edgeMap.get(edge).add(i);
            Pair<Integer, Integer> otherOrder = new DefaultKeyValuePair<>(
                edge.getSecond(), edge.getFirst());
            if (!edgeMap.containsKey(otherOrder))
            {
                edgeMap.put(otherOrder, new HashSet<>());
            }
            edgeMap.get(otherOrder).add(i);
        }
        allNodeTriangles = new ArrayList<>(n);
        for (int i = 0; i < n; ++i)
        {
            allNodeTriangles.add(new HashSet<>());
        }
        for (int i = 0; i < n; ++i)
        {
            if (degree(i) <= 1)
            {
                continue;
            }
            int iidx = nodeOrder.get(i);
            int jcnt = 0;
            for (Integer neighborj : allNodeNeighbors.get(i))
            {
                ++jcnt;
                // Ignore self loops
                if (i == neighborj)
                {
                    continue;
                }
                if (iidx > nodeOrder.get(neighborj))
                {
                    continue;
                }
                int kcnt = 0;
                for (Integer neighbork : allNodeNeighbors.get(i))
                {
                    ++kcnt;
                    // Ignore self loops
                    if (i == neighbork)
                    {
                        continue;
                    }
                    if (kcnt <= jcnt)
                    {
                        continue;
                    }
                    if (iidx > nodeOrder.get(neighbork))
                    {
                        continue;
                    }
                    if (allNodeNeighbors.get(neighborj).contains(neighbork))
                    {
                        allNodeTriangles.get(i).add(new DefaultKeyValuePair<>(
                            neighborj, neighbork));
                        allNodeTriangles.get(neighborj).add(
                            new DefaultKeyValuePair<>(i, neighbork));
                        allNodeTriangles.get(neighbork).add(
                            new DefaultKeyValuePair<>(i, neighborj));
                        Pair<Integer, Integer> edge = new DefaultKeyValuePair<>(
                            i, neighborj);
                        for (int edgeId : edgeMap.get(edge))
                        {
                            allEdgeTriangles.get(edgeId).add(neighbork);
                        }
                        edge = new DefaultKeyValuePair<>(i, neighbork);
                        for (int edgeId : edgeMap.get(edge))
                        {
                            allEdgeTriangles.get(edgeId).add(neighborj);
                        }
                        edge = new DefaultKeyValuePair<>(neighborj, neighbork);
                        for (int edgeId : edgeMap.get(edge))
                        {
                            allEdgeTriangles.get(edgeId).add(i);
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the number of triangles the node participates in. Note that this
     * implementation does not permit triangles with only two nodes (where the
     * third edge is a self-loop), nor does it count repeated triangles for
     * nodes with repeated edges. O(m^(3/2)) for the first call to any triangle
     * method, O(1) for later calls.
     *
     * @param nodeId The node whose number of triangles is desired
     * @return The number of triangles which use this node
     */
    public int numNodeTriangles(int nodeId)
    {
        if (!isInitializedNodeTriangles())
        {
            initializeNodeTriangles();
        }
        return allNodeTriangles.get(nodeId).size();
    }

    /**
     * Returns the number of triangles the node participates in. Note that this
     * implementation does not permit triangles with only two nodes (where the
     * third edge is a self-loop), nor does it count repeated triangles for
     * nodes with repeated edges. O(m^(3/2)) for the first call to any triangle
     * method, O(1) for later calls.
     *
     * @param nodeName The node whose number of triangles is desired
     * @return The number of triangles which use this node
     */
    public int numNodeTriangles(NodeNameType nodeName)
    {
        return numNodeTriangles(graph.getNodeId(nodeName));
    }

    /**
     * Returns the other two endpoint ids for all triangles the node
     * participates in. Note that this implementation does not permit triangles
     * with only two nodes (where the third edge is a self-loop), nor does it
     * count repeated triangles for nodes with repeated edges. O(m^(3/2)) for
     * the first call to any triangle method, O(1) for later calls.
     *
     * @param nodeId The node whose triangles are requested
     * @return The other two endpoint ids for all triangles which use this node
     */
    public Set<Pair<Integer, Integer>> getNodeTriangleEndpointIds(int nodeId)
    {
        if (!isInitializedNodeTriangles())
        {
            initializeNodeTriangles();
        }
        return Collections.unmodifiableSet(allNodeTriangles.get(nodeId));
    }

    /**
     * Returns the other two endpoint ids for all triangles the node
     * participates in. Note that this implementation does not permit triangles
     * with only two nodes (where the third edge is a self-loop), nor does it
     * count repeated triangles for nodes with repeated edges. O(m^(3/2)) for
     * the first call to any triangle method, O(1) for later calls.
     *
     * @param nodeName The node whose triangles are requested
     * @return The other two endpoint ids for all triangles which use this node
     */
    public Set<Pair<Integer, Integer>> getNodeTriangleEndpointIds(
        NodeNameType nodeName)
    {
        return getNodeTriangleEndpointIds(graph.getNodeId(nodeName));
    }

    /**
     * Returns the other two endpoint names for all triangles the node
     * participates in. Note that this implementation does not permit triangles
     * with only two nodes (where the third edge is a self-loop), nor does it
     * count repeated triangles for nodes with repeated edges. O(m^(3/2)) for
     * the first call to any triangle method, O(1) for later calls.
     *
     * @param nodeId The node whose triangles are requested
     * @return The other two endpoint names for all triangles which use this
     * node
     */
    public Set<Pair<NodeNameType, NodeNameType>> getNodeTriangleEndpoints(
        int nodeId)
    {
        Set<Pair<Integer, Integer>> endpointIds = getNodeTriangleEndpointIds(
            nodeId);
        Set<Pair<NodeNameType, NodeNameType>> ret = new HashSet<>(
            endpointIds.size());
        for (Pair<Integer, Integer> endpointId : endpointIds)
        {
            ret.add(new DefaultKeyValuePair<>(graph.getNode(
                endpointId.getFirst()), graph.getNode(endpointId.getSecond())));
        }

        return ret;
    }

    /**
     * Returns the other two endpoint names for all triangles the node
     * participates in. Note that this implementation does not permit triangles
     * with only two nodes (where the third edge is a self-loop), nor does it
     * count repeated triangles for nodes with repeated edges. O(m^(3/2)) for
     * the first call to any triangle method, O(1) for later calls.
     *
     * @param nodeName The node whose triangles are requested
     * @return The other two endpoint names for all triangles which use this
     * node
     */
    public Set<Pair<NodeNameType, NodeNameType>> getNodeTriangleEndpoints(
        NodeNameType nodeName)
    {
        return getNodeTriangleEndpoints(graph.getNodeId(nodeName));
    }

    /**
     * Private helper that tests if degree assortativity has been initialized
     *
     * @return true if initialized, else false
     */
    private boolean isInitializedDegreeAssortativity()
    {
        return (degreeAssortativity != -Double.MAX_VALUE);
    }

    /**
     * Initialize the degree assortativity for the whole graph. O(m)
     */
    @PublicationReference(author = "M. E. J. Newman",
        title = "Assortative mixing in networks",
        type = PublicationType.Journal,
        year = 2002,
        publication = "Physical Review Letters")
    public void initializeDegreeAssortativity()
    {
        if (!isInitializedNodeDegrees())
        {
            initializeNodeDegrees();
        }

        int m = numEdges();
        double mInv = 1.0 / m;
        double numerProduct, normalizeSum, normalizeSumSquares;
        numerProduct = normalizeSum = normalizeSumSquares = 0;
        boolean allDegreesEqual = true;
        for (int i = 0; i < m; ++i)
        {
            Pair<Integer, Integer> edge = graph.getEdgeEndpointIds(i);
            double di = degree(edge.getFirst());
            double dj = degree(edge.getSecond());
            allDegreesEqual &= (di == dj);
            numerProduct += di * dj;
            normalizeSum += di + dj;
            normalizeSumSquares += di * di + dj * dj;
        }
        // Special case can lead to degeneracies... specifically, if all not only
        // have the same degree on both sides of all edges, but that all nodes
        // have the exact same degree (at which point, this computes 0 / 0 below).
        if (allDegreesEqual)
        {
            degreeAssortativity = 1.0;
            return;
        }
        normalizeSum *= normalizeSum;
        normalizeSum *= mInv * 0.25;
        degreeAssortativity = ((numerProduct) - normalizeSum) / ((0.5
            * normalizeSumSquares) - normalizeSum);
    }

    /**
     * Returns the whole-graph degree assortativity score. O(m) the first time
     * called; O(1) any repeats.
     *
     * @return the whole-graph degree assortativity
     */
    public double degreeAssortativity()
    {
        if (!isInitializedDegreeAssortativity())
        {
            initializeDegreeAssortativity();
        }

        return degreeAssortativity;
    }

    /**
     * Private helper that tests if per-edge Jaccard similarity has been
     * initialized
     *
     * @return true if initialized; else false
     */
    private boolean isInitializedPerEdgeJaccardSimilarity()
    {
        return perEdgeJaccardSimilarity != null;
    }

    /**
     * Initializes the Jaccard similarity for each edge in the graph. O(n+m)
     */
    @PublicationReference(title = "Jaccard index",
        type = PublicationType.WebPage,
        year = 2015,
        author = "Wikipedia",
        url = "https://en.wikipedia.org/wiki/Jaccard_index")
    public void initializePerEdgeJaccardSimilarity()
    {
        if (!isInitializedNodeNeighbors())
        {
            initializeNodeNeighbors();
        }
        int m = numEdges();
        perEdgeJaccardSimilarity = new DoubleVector(m);
        for (int i = 0; i < m; ++i)
        {
            Pair<Integer, Integer> edge = graph.getEdgeEndpointIds(i);
            Set<Integer> iNeighbors = new HashSet<>(
                neighborIds(edge.getFirst()));
            Set<Integer> jNeighbors = neighborIds(edge.getSecond());
            int iSize = iNeighbors.size();
            int jSize = jNeighbors.size();
            iNeighbors.retainAll(jNeighbors);
            int intersectSize = iNeighbors.size();
            perEdgeJaccardSimilarity.add(((double) intersectSize)
                / ((double) (iSize + jSize - intersectSize)));
        }
    }

    /**
     * Returns the Jaccard Similarity for each edge of the graph.
     *
     * @param edgeId The [0..m) edge index
     * @return The Jaccard Similarity for the neighbors of the endpoints of the
     * edge.
     */
    public double getEdgeJaccardSimilarity(int edgeId)
    {
        if (!isInitializedPerEdgeJaccardSimilarity())
        {
            initializePerEdgeJaccardSimilarity();
        }

        return perEdgeJaccardSimilarity.get(edgeId);
    }

    /**
     * Private helper that tests if the all-edges triangles have been
     * initialized
     *
     * @return true if initialized, else false
     */
    private boolean isInitializedEdgeTriangles()
    {
        return allEdgeTriangles != null;
    }

    /**
     * Initializes the datastructure for all triangles that all nodes and edges
     * participate in. Note that this implementation uses the neighbors for each
     * node, so does not allow triangles with only two nodes (where the third
     * edge is a self-loop) nor does it create repeated triangles for nodes with
     * repeated edges. According to the publication, this is O(m^(3/2)) in the
     * worst case and O(m) at best.
     */
    public void initializeEdgeTriangles()
    {
        initializeNodeTriangles();
    }

    /**
     * Returns the number of triangles the input edge participates in.
     *
     * @param edgeId The edge whose triangle count is wanted
     * @return the number of triangles the input edge participates in
     */
    public int numEdgeTriangles(int edgeId)
    {
        if (!isInitializedEdgeTriangles())
        {
            initializeEdgeTriangles();
        }
        return allEdgeTriangles.get(edgeId).size();
    }

    /**
     * Returns the ids for the third nodes for all triangles this edge
     * participates in.
     *
     * @param edgeId The edge whose triangles' third nodes are requested
     * @return the ids for the third nodes for all triangles this edge
     * participates in
     */
    public Set<Integer> getEdgeTriangleOtherEndpointIds(int edgeId)
    {
        if (!isInitializedEdgeTriangles())
        {
            initializeEdgeTriangles();
        }
        return Collections.unmodifiableSet(allEdgeTriangles.get(edgeId));
    }

    /**
     * Returns the names for the third nodes for all triangles this edge
     * participates in.
     *
     * @param edgeId The edge whose triangles' third names are requested
     * @return the names for the third nodes for all triangles this edge
     * participates in
     */
    public Set<NodeNameType> getEdgeTriangleOtherEndpointNames(int edgeId)
    {
        Set<Integer> ids = getEdgeTriangleOtherEndpointIds(edgeId);
        Set<NodeNameType> ret = new HashSet<>(ids.size());
        for (Integer id : ids)
        {
            ret.add(graph.getNode(id));
        }

        return ret;
    }

    /**
     * Private helper that tests if per-edge triangle density has been
     * initialized.
     *
     * @return true if initialized, else false
     */
    private boolean isInitializedPerEdgeTriangleDensity()
    {
        return perEdgeTriangleDensity != null;
    }

    /**
     * Initializes the per-edge triangle density. This measure is based on the
     * degrees of the two endpoints of the edge. Intuitively, this looks at how
     * many of the possible connections that come off the endpoints pair off
     * into triangles. If edgeTriangles and nodeDegrees not already initialized,
     * this initializes those. This method requires O(m), but may require
     * O(m^3/2)/O(m) (edgeTriangles) or O(n+m) (degrees).
     */
    public void initializePerEdgeTriangleDensity()
    {
        if (!isInitializedEdgeTriangles())
        {
            initializeEdgeTriangles();
        }
        if (!isInitializedNodeDegrees())
        {
            initializeNodeDegrees();
        }

        int m = numEdges();
        perEdgeTriangleDensity = new DoubleVector(m);
        for (int i = 0; i < m; ++i)
        {
            Pair<Integer, Integer> edge = graph.getEdgeEndpointIds(i);
            int di = degree(edge.getFirst());
            int dj = degree(edge.getSecond());
            double density = (2.0 * numEdgeTriangles(i)) / ((double) (di + dj
                - 2));
            perEdgeTriangleDensity.add(density);
        }
    }

    /**
     * Returns the per-edge triangle density for the input edge
     *
     * @param edgeId The edge whose density is requested
     * @return the per-edge triangle density for the input edge
     */
    public double getPerEdgeTriangleDensity(int edgeId)
    {
        if (!isInitializedPerEdgeTriangleDensity())
        {
            initializePerEdgeTriangleDensity();
        }
        return perEdgeTriangleDensity.get(edgeId);
    }

    /**
     * Private helper that tests if per-node eccentricity has been initialized.
     *
     * @return true if initialized, else false
     */
    private boolean isInitializedPerNodeEccentricity()
    {
        return perNodeEccentricity != null;
    }

    /**
     * Initializes the per-node eccentricity.
     */
    @PublicationReference(author = "Frank W. Takes and Walter A. Kosters", title
        = "Computing the Eccentricity Distribution of Large Graphs", type
        = PublicationType.Journal, publication
        = "Algorithms - Open Access Journal", year = 2013, pages =
        {
            100, 118
        },
        url = "http://www.mdpi.com/1999-4893/6/1/100")
    public void initializePerNodeEccentricity()
    {
        if (!isInitializedNodeNeighbors())
        {
            initializeNodeNeighbors();
        }
        if (!isInitializedNodeDegrees())
        {
            initializeNodeDegrees();
        }

        int n = numNodes();
        perNodeEccentricity = new IntVector(n);
        int[] minEccentricity = new int[n];
        int[] maxEccentricity = new int[n];
        radius = Integer.MAX_VALUE;
        diameter = Integer.MIN_VALUE;
        isWcc = true;
        Set<Integer> unvisitedNodes = new HashSet<>(n);
        // Initialize all values
        for (int i = 0; i < n; ++i)
        {
            perNodeEccentricity.add(Integer.MAX_VALUE);
            minEccentricity[i] = Integer.MIN_VALUE;
            maxEccentricity[i] = Integer.MAX_VALUE;
            // I'll handle degree 1 nodes elsewhere
            if (degree(i) > 1)
            {
                unvisitedNodes.add(i);
            }
        }
        // Handle special cases and get out of here
        if (n == 1)
        {
            perNodeEccentricity.set(0, 0);
        }
        else if (n == 2)
        {
            if (numEdges() >= 1)
            {
                perNodeEccentricity.set(0, 1);
                perNodeEccentricity.set(1, 1);
            }
        }

        while (!unvisitedNodes.isEmpty())
        {
            // Can't keep the iterator as I'll possibly be removing other nodes during this pass
            int v = unvisitedNodes.iterator().next();
            unvisitedNodes.remove(v);
            int[] allDistances = computeAllDistancesForNode(v);
            int e_v = 0;
            for (int j = 0; j < n; ++j)
            {
                if (allDistances[j] == Integer.MAX_VALUE)
                {
                    isWcc = false;
                    continue;
                }
                e_v = Math.max(e_v, allDistances[j]);
            }
            if (e_v > maxEccentricity[v] || e_v < minEccentricity[v])
            {
                throw new RuntimeException(
                    "This should be impossible.  Please report bug.");
            }
            perNodeEccentricity.set(v, e_v);
            radius = Math.min(radius, e_v);
            diameter = Math.max(diameter, e_v);
            for (int neighbor : neighborIds(v))
            {
                // Ignore self loops
                if (neighbor == v)
                {
                    continue;
                }
                if (degree(neighbor) == 1)
                {
                    perNodeEccentricity.set(neighbor, e_v + 1);
                    diameter = Math.max(diameter, e_v + 1);
                }
            }
            for (Iterator<Integer> iter = unvisitedNodes.iterator();
                iter.hasNext();)
            {
                int node = iter.next();
                // No way to update for unreachable nodes
                if (allDistances[node] == Integer.MAX_VALUE)
                {
                    continue;
                }

                minEccentricity[node] = Math.max(minEccentricity[node],
                    Math.max(e_v - allDistances[node], allDistances[node]));
                maxEccentricity[node] = Math.min(maxEccentricity[node], e_v
                    + allDistances[node]);
                if (minEccentricity[node] == maxEccentricity[node])
                {
                    perNodeEccentricity.set(node, minEccentricity[node]);
                    radius = Math.min(radius, minEccentricity[node]);
                    diameter = Math.max(diameter, minEccentricity[node]);
                    iter.remove();
                }
            }
        }

        for (int i = 0; i < perNodeEccentricity.size(); ++i)
        {
            // This node was never visited
            if (perNodeEccentricity.get(i) == Integer.MAX_VALUE)
            {
                // In this case, it's in a 2-node disconnected barbell
                if ((degree(i) == 1) && (neighborIds(i).iterator().next() != i))
                {
                    perNodeEccentricity.set(i, 1);
                    continue;
                }
                // In this case, it's in a 1-node spot
                if (degree(i) == 0)
                {
                    perNodeEccentricity.set(i, 0);
                    continue;
                }
                throw new RuntimeException("Found node " + i + " has degree "
                    + degree(i) + ", but never visited?  Please report bug "
                    + "with this exception and example graph");
            }
        }
        if (!isWcc)
        {
            radius = Integer.MAX_VALUE;
            diameter = Integer.MAX_VALUE;
        }
    }

    /**
     * Helper which computes Dijkstra's Algorithm for the input node and returns
     * all of the distances to all other nodes from this node. This node's value
     * is returned as 0. This assumes an unweighted graph and each edge costs 1.
     * If the graph is disconnected (so no path exists between at least one pair
     * of nodes), this returns Integer.MAX_VALUE for any unreachable nodes.
     *
     * @param nodeId The node from which to start the search
     * @return the distances to all other nodes
     */
    @PublicationReference(author = "Wikipedia", title = "Dijkstra's algorithm",
        type = PublicationType.WebPage, url
        = "https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm", year = 2016)
    public int[] computeAllDistancesForNode(int nodeId)
    {
        PriorityQueue<Pair<Integer, Integer>> queue = new PriorityQueue<>(
            new Comparator<Pair<Integer, Integer>>()
        {
            @Override
            public int compare(Pair<Integer, Integer> o1,
                Pair<Integer, Integer> o2)
            {
                return Integer.compare(o1.getSecond(), o2.getSecond());
            }

        });
        int n = numNodes();
        int[] allDistances = new int[n];
        boolean[] allDone = new boolean[n];
        for (int i = 0; i < n; ++i)
        {
            allDistances[i] = Integer.MAX_VALUE;
            allDone[i] = false;
        }
        int current = nodeId;
        allDistances[current] = 0;
        queue.add(new DefaultKeyValuePair<>(current, allDistances[current]));

        while (!queue.isEmpty())
        {
            Pair<Integer, Integer> curr = queue.poll();
            // Skip those that were already done (but left in their old positions to speed up computation)
            // Combined with the block commented code below, this is a departure from the algorithm
            while (allDone[curr.getFirst()])
            {
                if (queue.isEmpty())
                {
                    break;
                }
                curr = queue.poll();
            }
            if (queue.isEmpty() && allDone[curr.getFirst()])
            {
                break;
            }

            // Mark this one as done, now
            allDone[curr.getFirst()] = true;

            int newLen = curr.getSecond() + 1;
            for (int neighbor : neighborIds(curr.getFirst()))
            {
                if (allDistances[neighbor] == Integer.MAX_VALUE)
                {
                    allDistances[neighbor] = newLen;
                    queue.add(new DefaultKeyValuePair<>(neighbor,
                        allDistances[neighbor]));
                }
                // We want to do this only if it will improve the result
                else if (allDistances[neighbor] > newLen)
                {
                    // NOTE: I don't need to remove it if I simply throw out extra copies of a node found later (see allDone variable)
                    // This code preserved as a long comment as it's a slight departure from the algorithm in the paper
                    // 
                    // Need to remove it from the queue and this is unfortunately O(n)
                    // boolean found = false;
                    // for (Iterator<Pair<Integer, Integer>> iter
                    //     = queue.iterator(); iter.hasNext();)
                    // {
                    //     Pair<Integer, Integer> v = iter.next();
                    //     if (v.getFirst().equals(neighbor))
                    //     {
                    //         iter.remove();
                    //         found = true;
                    //     }
                    // }
                    // if (!found)
                    // {
                    //     throw new RuntimeException("Unable to find a node that "
                    //         + "was added to the queue, but not removed.  Please "
                    //         + "report this bug");
                    // }
                    allDistances[neighbor] = newLen;
                    queue.add(new DefaultKeyValuePair<>(neighbor,
                        allDistances[neighbor]));
                }
            }
        }

        return allDistances;
    }

    /**
     * Returns the per-node eccentricity for the input node
     *
     * @param nodeId The node whose eccentricity is requested
     * @return the per-node eccentricity for the input node
     */
    public int getPerNodeEccentricityById(int nodeId)
    {
        if (!isInitializedPerNodeEccentricity())
        {
            initializePerNodeEccentricity();
        }
        return perNodeEccentricity.get(nodeId);
    }

    /**
     * Returns the per-node eccentricity for the input node
     *
     * @param node The node whose eccentricity is requested
     * @return the per-node eccentricity for the input node
     */
    public int getPerNodeEccentricity(NodeNameType node)
    {
        if (!isInitializedPerNodeEccentricity())
        {
            initializePerNodeEccentricity();
        }
        return perNodeEccentricity.get(graph.getNodeId(node));
    }

    /**
     * Returns the radius for this graph. NOTE: If the graph is not a single
     * weakly connected component (WCC), the radius is ill-defined (for which
     * component do you want the radius?), so this returns Integer.MAX_VALUE
     *
     * @return the radius for the graph if it is WCC, else Integer.MAX_VALUE
     */
    public int getRadius()
    {
        if (!isInitializedPerNodeEccentricity())
        {
            initializePerNodeEccentricity();
        }
        return radius;
    }

    /**
     * Returns the diameter for this graph. NOTE: If the graph is not a single
     * weakly connected component (WCC), the diameter is ill-defined (for which
     * component do you want the diameter?), so this returns Integer.MAX_VALUE
     *
     * @return the diameter for the graph if it is WCC, else Integer.MAX_VALUE
     */
    public int getDiameter()
    {
        if (!isInitializedPerNodeEccentricity())
        {
            initializePerNodeEccentricity();
        }
        return diameter;
    }

    /**
     * Returns true if the graph is a single weakly connected component (WCC),
     * else false.
     *
     * @return true if this is WCC
     */
    public boolean isWcc()
    {
        if (!isInitializedPerNodeEccentricity())
        {
            initializePerNodeEccentricity();
        }
        return isWcc;
    }

}
