/*
 * File:                LouvainTest.java
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

package gov.sandia.cognition.graph.community;

import gov.sandia.cognition.graph.DenseMemoryGraph;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Tests Louvain against various graphs.
 *
 * @author jdwendt
 */
public class LouvainTest
{

    /**
     * Runs the community detection and tests that modularity strictly increases
     * after each pass of the Louvain algorithm.
     */
    private <NodeNameType> Louvain.LouvainHierarchy<NodeNameType> communityDetect(
        DenseMemoryGraph<NodeNameType> graph)
    {
        Louvain<NodeNameType> louvain = new Louvain<>(graph, 10000, 0);
        Louvain.LouvainHierarchy<NodeNameType> results
            = louvain.solveCommunities();
        // Make sure the modularity always increases
        for (int j = 0; j < results.numLevels() - 1; ++j)
        {
            assertTrue(results.getModularity(j)
                < results.getModularity(j + 1));
        }
        return results;
    }

    /**
     * This helper makes the test code easier to read. It insures the two input
     * nodes are in the same community as each other.
     */
    private static <NodeNameType> void testSameCommunity(NodeNameType v1,
        NodeNameType v2,
        Louvain.LouvainHierarchy<NodeNameType> results)
    {
        assertEquals(results.getPartition(v1), results.getPartition(v2));
    }

    @Test
    public void exactTest()
    {
        DenseMemoryGraph<Integer> graph
            = new DenseMemoryGraph<>(8, 15);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(0, 3);
        graph.addEdge(3, 4);
        graph.addEdge(4, 5);
        graph.addEdge(4, 6);
        graph.addEdge(4, 7);

        // Run community detection
        Louvain.LouvainHierarchy<Integer> results = communityDetect(graph);

        testSameCommunity(0, 1, results);
        testSameCommunity(0, 2, results);
        testSameCommunity(0, 3, results);
        testSameCommunity(4, 5, results);
        testSameCommunity(4, 6, results);
        testSameCommunity(4, 7, results);
        assertEquals(0.357143, results.getModularity(
            results.numLevels() - 1), 1e-6);
        assertEquals(8, results.getAllMembers().size());
        assertEquals(2, results.numPartitions());
        Set<Integer> p0 = results.getPartitionMembers(0);
        Set<Integer> p1 = results.getPartitionMembers(1);
        // Swap the two so I know which partition is which
        if (!p0.contains(0))
        {
            Set<Integer> tmp = p0;
            p0 = p1;
            p1 = tmp;
        }
        assertEquals(4, p0.size());
        assertTrue(p0.contains(0));
        assertTrue(p0.contains(1));
        assertTrue(p0.contains(2));
        assertTrue(p0.contains(3));
        assertEquals(4, p1.size());
        assertTrue(p1.contains(4));
        assertTrue(p1.contains(5));
        assertTrue(p1.contains(6));
        assertTrue(p1.contains(7));
    }

    @Test
    public void threeCommunityExactTest()
    {
        DenseMemoryGraph<Integer> graph
            = new DenseMemoryGraph<>(13, 33);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(0, 3);
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);
        graph.addEdge(4, 5);
        graph.addEdge(4, 6);
        graph.addEdge(4, 7);
        graph.addEdge(4, 8);
        graph.addEdge(5, 6);
        graph.addEdge(5, 7);
        graph.addEdge(5, 8);
        graph.addEdge(6, 7);
        graph.addEdge(6, 8);
        graph.addEdge(7, 8);
        graph.addEdge(7, 10);
        graph.addEdge(7, 11);
        graph.addEdge(7, 12);
        graph.addEdge(8, 9);
        graph.addEdge(8, 10);
        graph.addEdge(8, 11);
        graph.addEdge(9, 10);
        graph.addEdge(9, 11);
        graph.addEdge(9, 12);
        graph.addEdge(9, 13);
        graph.addEdge(10, 11);
        graph.addEdge(10, 12);
        graph.addEdge(10, 13);
        graph.addEdge(11, 12);
        graph.addEdge(11, 13);
        graph.addEdge(12, 13);

        // Run community detection
        Louvain.LouvainHierarchy<Integer> results = communityDetect(graph);
        testSameCommunity(0, 1, results);
        testSameCommunity(0, 2, results);
        testSameCommunity(0, 3, results);
        testSameCommunity(4, 5, results);
        testSameCommunity(4, 6, results);
        testSameCommunity(4, 7, results);
        testSameCommunity(4, 8, results);
        testSameCommunity(9, 10, results);
        testSameCommunity(9, 11, results);
        testSameCommunity(9, 12, results);
        testSameCommunity(9, 13, results);
        assertEquals(0.426538, results.getModularity(
            results.numLevels() - 1), 1e-6);
        assertEquals(14, results.getAllMembers().size());
        assertEquals(3, results.numPartitions());
        Set<Integer> p0 = results.getPartitionMembers(0);
        Set<Integer> p1 = results.getPartitionMembers(1);
        Set<Integer> p2 = results.getPartitionMembers(2);
        // Swap the two so I know which partition is which
        if (p1.contains(0))
        {
            Set<Integer> tmp = p0;
            p0 = p1;
            p1 = tmp;
        }
        else if (p2.contains(0))
        {
            Set<Integer> tmp = p0;
            p0 = p2;
            p2 = tmp;
        }
        if (p2.contains(4))
        {
            Set<Integer> tmp = p1;
            p1 = p2;
            p2 = tmp;
        }
        assertEquals(4, p0.size());
        assertTrue(p0.contains(0));
        assertTrue(p0.contains(1));
        assertTrue(p0.contains(2));
        assertTrue(p0.contains(3));
        assertEquals(5, p1.size());
        assertTrue(p1.contains(4));
        assertTrue(p1.contains(5));
        assertTrue(p1.contains(6));
        assertTrue(p1.contains(7));
        assertTrue(p1.contains(8));
        assertEquals(5, p2.size());
        assertTrue(p2.contains(9));
        assertTrue(p2.contains(10));
        assertTrue(p2.contains(11));
        assertTrue(p2.contains(12));
        assertTrue(p2.contains(13));
    }

    /**
     * This tests creates two 4-node, fully-connected communities and then
     * connects them with one edge. It then tests that the two fully-connected
     * components form their own communities in three cases: default Louvain,
     * correct-partition initialized Louvain, and incorrect-partition
     * initialized Louvain.
     */
    @Test
    public void initialPartitionTest()
    {
        DenseMemoryGraph<Integer> graph
            = new DenseMemoryGraph<>(8, 15);
        graph.addEdge(0, 1);
        graph.addEdge(0, 3);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(2, 3);
        graph.addEdge(3, 1);
        graph.addEdge(2, 2);
        graph.addEdge(3, 4);
        graph.addEdge(4, 5);
        graph.addEdge(4, 6);
        graph.addEdge(4, 7);
        graph.addEdge(5, 6);
        graph.addEdge(5, 7);
        graph.addEdge(7, 6);

        // Now try seeding it with a correct answer
        Louvain<Integer> comm = new Louvain<>(graph,
            (int) 1e6, 0);
        Map<Integer, Integer> truth = new HashMap<>();
        truth.put(0, 0);
        truth.put(1, 0);
        truth.put(2, 0);
        truth.put(3, 0);
        truth.put(4, 1);
        truth.put(5, 1);
        truth.put(6, 1);
        truth.put(7, 1);
        comm.initialPartition(truth);
        Louvain.LouvainHierarchy<Integer> result = comm.solveCommunities();
        int level = result.numLevels() - 1;
        assertEquals(0, result.getCommunityForNodeAtLevel(0, level));
        assertEquals(0, result.getCommunityForNodeAtLevel(1, level));
        assertEquals(0, result.getCommunityForNodeAtLevel(2, level));
        assertEquals(0, result.getCommunityForNodeAtLevel(3, level));
        assertEquals(1, result.getCommunityForNodeAtLevel(4, level));
        assertEquals(1, result.getCommunityForNodeAtLevel(5, level));
        assertEquals(1, result.getCommunityForNodeAtLevel(6, level));
        assertEquals(1, result.getCommunityForNodeAtLevel(7, level));
        assertEquals(8, result.getAllMembers().size());
        assertEquals(2, result.numPartitions());
        Set<Integer> p0 = result.getPartitionMembers(0);
        Set<Integer> p1 = result.getPartitionMembers(1);
        // Swap the two so I know which partition is which
        if (!p0.contains(0))
        {
            Set<Integer> tmp = p0;
            p0 = p1;
            p1 = tmp;
        }
        assertEquals(4, p0.size());
        assertTrue(p0.contains(0));
        assertTrue(p0.contains(1));
        assertTrue(p0.contains(2));
        assertTrue(p0.contains(3));
        assertEquals(4, p1.size());
        assertTrue(p1.contains(4));
        assertTrue(p1.contains(5));
        assertTrue(p1.contains(6));
        assertTrue(p1.contains(7));

        // Now a wrong seed ... should still give the correct end result
        comm = new Louvain<>(graph, (int) 1e6, 0);
        truth.put(2, 1);
        truth.put(4, 0);
        truth.put(5, 0);
        comm.initialPartition(truth);
        result = comm.solveCommunities();
        level = result.numLevels() - 1;
        assertEquals(0, result.getCommunityForNodeAtLevel(0, level));
        assertEquals(0, result.getCommunityForNodeAtLevel(1, level));
        assertEquals(0, result.getCommunityForNodeAtLevel(2, level));
        assertEquals(0, result.getCommunityForNodeAtLevel(3, level));
        assertEquals(1, result.getCommunityForNodeAtLevel(4, level));
        assertEquals(1, result.getCommunityForNodeAtLevel(5, level));
        assertEquals(1, result.getCommunityForNodeAtLevel(6, level));
        assertEquals(1, result.getCommunityForNodeAtLevel(7, level));
        assertEquals(8, result.getAllMembers().size());
        assertEquals(2, result.numPartitions());
        p0 = result.getPartitionMembers(0);
        p1 = result.getPartitionMembers(1);
        // Swap the two so I know which partition is which
        if (!p0.contains(0))
        {
            Set<Integer> tmp = p0;
            p0 = p1;
            p1 = tmp;
        }
        assertEquals(4, p0.size());
        assertTrue(p0.contains(0));
        assertTrue(p0.contains(1));
        assertTrue(p0.contains(2));
        assertTrue(p0.contains(3));
        assertEquals(4, p1.size());
        assertTrue(p1.contains(4));
        assertTrue(p1.contains(5));
        assertTrue(p1.contains(6));
        assertTrue(p1.contains(7));
    }

    /**
     * A more complicated test in that it takes a significantly larger graph
     * (and a "real" one at that!) and makes sure that various pairs of nodes
     * are in the same communities after community detection completes.
     */
    @Test
    public void lesMisTest()
    {
        DenseMemoryGraph<String> graph = new DenseMemoryGraph<>();
        graph.addEdge("Napoleon", "Myriel");
        graph.addEdge("MlleBaptistine", "Myriel");
        graph.addEdge("MmeMagloire", "Myriel");
        graph.addEdge("MmeMagloire", "MlleBaptistine");
        graph.addEdge("CountessDeLo", "Myriel");
        graph.addEdge("Geborand", "Myriel");
        graph.addEdge("Champtercier", "Myriel");
        graph.addEdge("Cravatte", "Myriel");
        graph.addEdge("Count", "Myriel");
        graph.addEdge("OldMan", "Myriel");
        graph.addEdge("Valjean", "Myriel");
        graph.addEdge("Valjean", "MlleBaptistine");
        graph.addEdge("Valjean", "MmeMagloire");
        graph.addEdge("Valjean", "Labarre");
        graph.addEdge("Marguerite", "Valjean");
        graph.addEdge("MmeDeR", "Valjean");
        graph.addEdge("Isabeau", "Valjean");
        graph.addEdge("Gervais", "Valjean");
        graph.addEdge("Listolier", "Tholomyes");
        graph.addEdge("Fameuil", "Tholomyes");
        graph.addEdge("Fameuil", "Listolier");
        graph.addEdge("Blacheville", "Tholomyes");
        graph.addEdge("Blacheville", "Listolier");
        graph.addEdge("Blacheville", "Fameuil");
        graph.addEdge("Favourite", "Tholomyes");
        graph.addEdge("Favourite", "Listolier");
        graph.addEdge("Favourite", "Fameuil");
        graph.addEdge("Favourite", "Blacheville");
        graph.addEdge("Dahlia", "Tholomyes");
        graph.addEdge("Dahlia", "Listolier");
        graph.addEdge("Dahlia", "Fameuil");
        graph.addEdge("Dahlia", "Blacheville");
        graph.addEdge("Dahlia", "Favourite");
        graph.addEdge("Zephine", "Tholomyes");
        graph.addEdge("Zephine", "Listolier");
        graph.addEdge("Zephine", "Fameuil");
        graph.addEdge("Zephine", "Blacheville");
        graph.addEdge("Zephine", "Favourite");
        graph.addEdge("Zephine", "Dahlia");
        graph.addEdge("Fantine", "Valjean");
        graph.addEdge("Fantine", "Marguerite");
        graph.addEdge("Fantine", "Tholomyes");
        graph.addEdge("Fantine", "Listolier");
        graph.addEdge("Fantine", "Fameuil");
        graph.addEdge("Fantine", "Blacheville");
        graph.addEdge("Fantine", "Favourite");
        graph.addEdge("Fantine", "Dahlia");
        graph.addEdge("Fantine", "Zephine");
        graph.addEdge("MmeThenardier", "Valjean");
        graph.addEdge("MmeThenardier", "Fantine");
        graph.addEdge("Thenardier", "Valjean");
        graph.addEdge("Thenardier", "Fantine");
        graph.addEdge("Thenardier", "MmeThenardier");
        graph.addEdge("Cosette", "Valjean");
        graph.addEdge("Cosette", "Tholomyes");
        graph.addEdge("Cosette", "MmeThenardier");
        graph.addEdge("Cosette", "Thenardier");
        graph.addEdge("Javert", "Valjean");
        graph.addEdge("Javert", "Fantine");
        graph.addEdge("Javert", "MmeThenardier");
        graph.addEdge("Javert", "Thenardier");
        graph.addEdge("Javert", "Cosette");
        graph.addEdge("Fauchelevent", "Valjean");
        graph.addEdge("Fauchelevent", "Javert");
        graph.addEdge("Bamatabois", "Valjean");
        graph.addEdge("Bamatabois", "Fantine");
        graph.addEdge("Bamatabois", "Javert");
        graph.addEdge("Perpetue", "Fantine");
        graph.addEdge("Simplice", "Valjean");
        graph.addEdge("Simplice", "Fantine");
        graph.addEdge("Simplice", "Javert");
        graph.addEdge("Simplice", "Perpetue");
        graph.addEdge("Scaufflaire", "Valjean");
        graph.addEdge("Woman1", "Valjean");
        graph.addEdge("Woman1", "Javert");
        graph.addEdge("Judge", "Valjean");
        graph.addEdge("Judge", "Bamatabois");
        graph.addEdge("Champmathieu", "Valjean");
        graph.addEdge("Champmathieu", "Bamatabois");
        graph.addEdge("Champmathieu", "Judge");
        graph.addEdge("Brevet", "Valjean");
        graph.addEdge("Brevet", "Bamatabois");
        graph.addEdge("Brevet", "Judge");
        graph.addEdge("Brevet", "Champmathieu");
        graph.addEdge("Chenildieu", "Valjean");
        graph.addEdge("Chenildieu", "Bamatabois");
        graph.addEdge("Chenildieu", "Judge");
        graph.addEdge("Chenildieu", "Champmathieu");
        graph.addEdge("Chenildieu", "Brevet");
        graph.addEdge("Cochepaille", "Valjean");
        graph.addEdge("Cochepaille", "Bamatabois");
        graph.addEdge("Cochepaille", "Judge");
        graph.addEdge("Cochepaille", "Champmathieu");
        graph.addEdge("Cochepaille", "Brevet");
        graph.addEdge("Cochepaille", "Chenildieu");
        graph.addEdge("Pontmercy", "Thenardier");
        graph.addEdge("Boulatruelle", "Thenardier");
        graph.addEdge("Eponine", "MmeThenardier");
        graph.addEdge("Eponine", "Thenardier");
        graph.addEdge("Anzelma", "MmeThenardier");
        graph.addEdge("Anzelma", "Thenardier");
        graph.addEdge("Anzelma", "Eponine");
        graph.addEdge("Woman2", "Valjean");
        graph.addEdge("Woman2", "Cosette");
        graph.addEdge("Woman2", "Javert");
        graph.addEdge("MotherInnocent", "Valjean");
        graph.addEdge("MotherInnocent", "Fauchelevent");
        graph.addEdge("Gribier", "Fauchelevent");
        graph.addEdge("MmeBurgon", "Jondrette");
        graph.addEdge("Gavroche", "Valjean");
        graph.addEdge("Gavroche", "Thenardier");
        graph.addEdge("Gavroche", "Javert");
        graph.addEdge("Gavroche", "MmeBurgon");
        graph.addEdge("Gillenormand", "Valjean");
        graph.addEdge("Gillenormand", "Cosette");
        graph.addEdge("Magnon", "MmeThenardier");
        graph.addEdge("Magnon", "Gillenormand");
        graph.addEdge("MlleGillenormand", "Valjean");
        graph.addEdge("MlleGillenormand", "Cosette");
        graph.addEdge("MlleGillenormand", "Gillenormand");
        graph.addEdge("MmePontmercy", "Pontmercy");
        graph.addEdge("MmePontmercy", "MlleGillenormand");
        graph.addEdge("MlleVaubois", "MlleGillenormand");
        graph.addEdge("LtGillenormand", "Cosette");
        graph.addEdge("LtGillenormand", "Gillenormand");
        graph.addEdge("LtGillenormand", "MlleGillenormand");
        graph.addEdge("Marius", "Valjean");
        graph.addEdge("Marius", "Tholomyes");
        graph.addEdge("Marius", "Thenardier");
        graph.addEdge("Marius", "Cosette");
        graph.addEdge("Marius", "Pontmercy");
        graph.addEdge("Marius", "Eponine");
        graph.addEdge("Marius", "Gavroche");
        graph.addEdge("Marius", "Gillenormand");
        graph.addEdge("Marius", "MlleGillenormand");
        graph.addEdge("Marius", "LtGillenormand");
        graph.addEdge("BaronessT", "Gillenormand");
        graph.addEdge("BaronessT", "Marius");
        graph.addEdge("Mabeuf", "Eponine");
        graph.addEdge("Mabeuf", "Gavroche");
        graph.addEdge("Mabeuf", "Marius");
        graph.addEdge("Enjolras", "Valjean");
        graph.addEdge("Enjolras", "Javert");
        graph.addEdge("Enjolras", "Gavroche");
        graph.addEdge("Enjolras", "Marius");
        graph.addEdge("Enjolras", "Mabeuf");
        graph.addEdge("Combeferre", "Gavroche");
        graph.addEdge("Combeferre", "Marius");
        graph.addEdge("Combeferre", "Mabeuf");
        graph.addEdge("Combeferre", "Enjolras");
        graph.addEdge("Prouvaire", "Gavroche");
        graph.addEdge("Prouvaire", "Enjolras");
        graph.addEdge("Prouvaire", "Combeferre");
        graph.addEdge("Feuilly", "Gavroche");
        graph.addEdge("Feuilly", "Marius");
        graph.addEdge("Feuilly", "Mabeuf");
        graph.addEdge("Feuilly", "Enjolras");
        graph.addEdge("Feuilly", "Combeferre");
        graph.addEdge("Feuilly", "Prouvaire");
        graph.addEdge("Courfeyrac", "Eponine");
        graph.addEdge("Courfeyrac", "Gavroche");
        graph.addEdge("Courfeyrac", "Marius");
        graph.addEdge("Courfeyrac", "Mabeuf");
        graph.addEdge("Courfeyrac", "Enjolras");
        graph.addEdge("Courfeyrac", "Combeferre");
        graph.addEdge("Courfeyrac", "Prouvaire");
        graph.addEdge("Courfeyrac", "Feuilly");
        graph.addEdge("Bahorel", "Gavroche");
        graph.addEdge("Bahorel", "Marius");
        graph.addEdge("Bahorel", "Mabeuf");
        graph.addEdge("Bahorel", "Enjolras");
        graph.addEdge("Bahorel", "Combeferre");
        graph.addEdge("Bahorel", "Prouvaire");
        graph.addEdge("Bahorel", "Feuilly");
        graph.addEdge("Bahorel", "Courfeyrac");
        graph.addEdge("Bossuet", "Valjean");
        graph.addEdge("Bossuet", "Gavroche");
        graph.addEdge("Bossuet", "Marius");
        graph.addEdge("Bossuet", "Mabeuf");
        graph.addEdge("Bossuet", "Enjolras");
        graph.addEdge("Bossuet", "Combeferre");
        graph.addEdge("Bossuet", "Prouvaire");
        graph.addEdge("Bossuet", "Feuilly");
        graph.addEdge("Bossuet", "Courfeyrac");
        graph.addEdge("Bossuet", "Bahorel");
        graph.addEdge("Joly", "Gavroche");
        graph.addEdge("Joly", "Marius");
        graph.addEdge("Joly", "Mabeuf");
        graph.addEdge("Joly", "Enjolras");
        graph.addEdge("Joly", "Combeferre");
        graph.addEdge("Joly", "Prouvaire");
        graph.addEdge("Joly", "Feuilly");
        graph.addEdge("Joly", "Courfeyrac");
        graph.addEdge("Joly", "Bahorel");
        graph.addEdge("Joly", "Bossuet");
        graph.addEdge("Grantaire", "Gavroche");
        graph.addEdge("Grantaire", "Enjolras");
        graph.addEdge("Grantaire", "Combeferre");
        graph.addEdge("Grantaire", "Prouvaire");
        graph.addEdge("Grantaire", "Feuilly");
        graph.addEdge("Grantaire", "Courfeyrac");
        graph.addEdge("Grantaire", "Bahorel");
        graph.addEdge("Grantaire", "Bossuet");
        graph.addEdge("Grantaire", "Joly");
        graph.addEdge("MotherPlutarch", "Mabeuf");
        graph.addEdge("Gueulemer", "Valjean");
        graph.addEdge("Gueulemer", "MmeThenardier");
        graph.addEdge("Gueulemer", "Thenardier");
        graph.addEdge("Gueulemer", "Javert");
        graph.addEdge("Gueulemer", "Eponine");
        graph.addEdge("Gueulemer", "Gavroche");
        graph.addEdge("Babet", "Valjean");
        graph.addEdge("Babet", "MmeThenardier");
        graph.addEdge("Babet", "Thenardier");
        graph.addEdge("Babet", "Javert");
        graph.addEdge("Babet", "Eponine");
        graph.addEdge("Babet", "Gavroche");
        graph.addEdge("Babet", "Gueulemer");
        graph.addEdge("Claquesous", "Valjean");
        graph.addEdge("Claquesous", "MmeThenardier");
        graph.addEdge("Claquesous", "Thenardier");
        graph.addEdge("Claquesous", "Javert");
        graph.addEdge("Claquesous", "Eponine");
        graph.addEdge("Claquesous", "Enjolras");
        graph.addEdge("Claquesous", "Gueulemer");
        graph.addEdge("Claquesous", "Babet");
        graph.addEdge("Montparnasse", "Valjean");
        graph.addEdge("Montparnasse", "Thenardier");
        graph.addEdge("Montparnasse", "Javert");
        graph.addEdge("Montparnasse", "Eponine");
        graph.addEdge("Montparnasse", "Gavroche");
        graph.addEdge("Montparnasse", "Gueulemer");
        graph.addEdge("Montparnasse", "Babet");
        graph.addEdge("Montparnasse", "Claquesous");
        graph.addEdge("Toussaint", "Valjean");
        graph.addEdge("Toussaint", "Cosette");
        graph.addEdge("Toussaint", "Javert");
        graph.addEdge("Child1", "Gavroche");
        graph.addEdge("Child2", "Gavroche");
        graph.addEdge("Child2", "Child1");
        graph.addEdge("Brujon", "Thenardier");
        graph.addEdge("Brujon", "Eponine");
        graph.addEdge("Brujon", "Gavroche");
        graph.addEdge("Brujon", "Gueulemer");
        graph.addEdge("Brujon", "Babet");
        graph.addEdge("Brujon", "Claquesous");
        graph.addEdge("Brujon", "Montparnasse");
        graph.addEdge("MmeHucheloup", "Gavroche");
        graph.addEdge("MmeHucheloup", "Enjolras");
        graph.addEdge("MmeHucheloup", "Courfeyrac");
        graph.addEdge("MmeHucheloup", "Bahorel");
        graph.addEdge("MmeHucheloup", "Bossuet");
        graph.addEdge("MmeHucheloup", "Joly");
        graph.addEdge("MmeHucheloup", "Grantaire");

        // Louvain is a randomized algorithm and this graph is sufficiently
        // complex that there are various possible results.  Run Louvain lots
        // of times to see the various results are all within parameters.
        for (int i = 0; i < 50; ++i)
        {
            Louvain.LouvainHierarchy<String> results = communityDetect(graph);

            // Test that various people that should be in the same community are
            // These are the communities assigned in d3 example on http://bl.ocks.org/mbostock/4062045
            // Except those that were in individual communities and a few outliers
            // (commented out) that are debatable and w/ a random algorithm are
            // likely to flip between communities on different runs.
            testSameCommunity("Myriel", "Napoleon", results);
            //testSameCommunity("Myriel", "MlleBaptistine", results);
            //testSameCommunity("Myriel", "MmeMagloire", results);
            testSameCommunity("Myriel", "CountessDeLo", results);
            testSameCommunity("Myriel", "Geborand", results);
            testSameCommunity("Myriel", "Champtercier", results);
            testSameCommunity("Myriel", "Cravatte", results);
            testSameCommunity("Myriel", "Count", results);
            testSameCommunity("Myriel", "OldMan", results);

            testSameCommunity("Valjean", "Labarre", results);
            testSameCommunity("Valjean", "MmeDeR", results);
            testSameCommunity("Valjean", "Isabeau", results);
            testSameCommunity("Valjean", "Gervais", results);
            //testSameCommunity("Valjean", "Simplice", results);
            testSameCommunity("Valjean", "Scaufflaire", results);
            //testSameCommunity("Valjean", "Woman1", results);
            // I'm splitting this group in two (Labarre/Valjean) from the fully connected component below
            testSameCommunity("Judge", "Bamatabois", results);
            testSameCommunity("Judge", "Champmathieu", results);
            testSameCommunity("Judge", "Brevet", results);
            testSameCommunity("Judge", "Chenildieu", results);
            testSameCommunity("Judge", "Cochepaille", results);

            // I had to switch the person connecting to all the rest as
            // the old version of this file used a person tenuously connected
            // to the group to check all of the others
            testSameCommunity("Tholomyes", "Listolier", results);
            testSameCommunity("Tholomyes", "Fameuil", results);
            testSameCommunity("Tholomyes", "Blacheville", results);
            testSameCommunity("Tholomyes", "Favourite", results);
            testSameCommunity("Tholomyes", "Dahlia", results);
            testSameCommunity("Tholomyes", "Zephine", results);
            testSameCommunity("Tholomyes", "Fantine", results);

            testSameCommunity("Claquesous", "MmeThenardier", results);
            testSameCommunity("Claquesous", "Thenardier", results);
            //testSameCommunity("Claquesous", "Javert", results);
            //testSameCommunity("Claquesous", "Pontmercy", results);
            testSameCommunity("Claquesous", "Eponine", results);
            testSameCommunity("Claquesous", "Anzelma", results);
            testSameCommunity("Claquesous", "Gueulemer", results);
            testSameCommunity("Claquesous", "Babet", results);
            testSameCommunity("Claquesous", "Montparnasse", results);
            testSameCommunity("Claquesous", "Brujon", results);

            //testSameCommunity("Gillenormand", "Magnon", results);
            testSameCommunity("Gillenormand", "MlleGillenormand", results);
            //testSameCommunity("Gillenormand", "MmePontmercy", results);
            testSameCommunity("Gillenormand", "MlleVaubois", results);
            testSameCommunity("Gillenormand", "LtGillenormand", results);
            testSameCommunity("Gillenormand", "BaronessT", results);
            //testSameCommunity("Gillenormand", "Toussaint", results);
            //testSameCommunity("Gillenormand", "Woman2", results);
            //testSameCommunity("Gillenormand", "Cosette", results);

            testSameCommunity("Jondrette", "MmeBurgon", results);

            //testSameCommunity("Courfeyrac", "Marius", results);
            testSameCommunity("Courfeyrac", "MmeHucheloup", results);
            testSameCommunity("Courfeyrac", "Mabeuf", results);
            testSameCommunity("Courfeyrac", "Enjolras", results);
            testSameCommunity("Courfeyrac", "Combeferre", results);
            testSameCommunity("Courfeyrac", "Prouvaire", results);
            testSameCommunity("Courfeyrac", "Feuilly", results);
            //testSameCommunity("Courfeyrac", "Gavroche", results);
            testSameCommunity("Courfeyrac", "Bahorel", results);
            testSameCommunity("Courfeyrac", "Bossuet", results);
            testSameCommunity("Courfeyrac", "Joly", results);
            testSameCommunity("Courfeyrac", "Grantaire", results);

            testSameCommunity("Child1", "Child2", results);
            assertTrue(results.getModularity(
                results.numLevels() - 1) > 0.53);
            assertTrue(results.getModularity(
                results.numLevels() - 1) < 0.57);
        }
    }

}
