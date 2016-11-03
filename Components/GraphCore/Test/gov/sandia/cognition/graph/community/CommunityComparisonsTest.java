/*
 * File:                CommunityComparisonsTest.java
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the CommunityComparisons class
 *
 * @author jdwendt
 */
public class CommunityComparisonsTest
{

    /**
     * Simple implementation of the interface for the purposes of this test only
     */
    private static class TestPartitioning
        implements NodePartitioning<String>
    {

        private final List<Set<String>> partitions;

        private final Set<String> allMembers;

        private TestPartitioning(String[][] partitions)
        {
            allMembers = new HashSet<>();
            this.partitions = new ArrayList<>(partitions.length);
            for (String[] partition : partitions)
            {
                Collection<String> p = Arrays.asList(partition);
                allMembers.addAll(p);
                this.partitions.add(new HashSet<>(p));
            }
        }

        @Override
        public int getNumPartitions()
        {
            return partitions.size();
        }

        @Override
        public Set<String> getPartitionMembers(int i)
        {
            return Collections.unmodifiableSet(partitions.get(i));
        }

        @Override
        public Set<String> getAllMembers()
        {
            return Collections.unmodifiableSet(allMembers);
        }

        @Override
        public int getPartition(String node)
        {
            for (int i = 0; i < partitions.size(); ++i)
            {
                if (partitions.get(i).contains(node))
                {
                    return i;
                }
            }
            throw new IllegalArgumentException("Input node " + node
                + " not in any partition");
        }

        @Override
        public Double getModularity()
        {
            return null;
        }

        @Override
        public int getPartitionById(int nodeId)
        {
            // I realize this is a hack, but it should be stable enough for a unit test
            int cnt = 0;
            for (String node : allMembers)
            {
                if (cnt == nodeId)
                {
                    return getPartition(node);
                }
                ++cnt;
            }
            throw new IllegalArgumentException("Unable to find a node with id "
                + nodeId);
        }

    }

    /**
     * Simplest test: A 50/50 splitting paired with no split at all. All values
     * in the test were computed by hand and calculator.
     */
    @Test
    public void basicTest()
    {
        String[][] p1s =
        {
            {
                "a", "b"
            },
            {
                "c", "d"
            }
        };
        TestPartitioning p1 = new TestPartitioning(p1s);
        String[][] p2s =
        {
            {
                "a", "b", "c", "d"
            }
        };
        TestPartitioning p2 = new TestPartitioning(p2s);
        CommunityComparisons<String> cc = new CommunityComparisons<>(p1, p2);
        assertEquals(0.6931, cc.getEntropyU(), 1e-4);
        assertEquals(0, cc.getEntropyV(), 1e-10);
        assertEquals(cc.getEntropyU(), cc.getConditionalEntropyUGivenV(), 1e-10);
        assertEquals(0, cc.getConditionalEntropyVGivenU(), 1e-10);
        assertEquals(0, cc.getAmiMax(), 1e-10);
        assertEquals(Double.NaN, cc.getAmiMin(), 1e-10); // it's 0/0
        assertEquals(Double.NaN, cc.getAmiSqrt(), 1e-10); // it's 0/0
        assertEquals(0, cc.getAmiSum(), 1e-10);
        assertEquals(0, cc.getExpectedMutualInformation(), 1e-10);
        assertEquals(cc.getEntropyU(), cc.getJointEntropy(), 1e-10);
        assertEquals(0, cc.getMutualInformation(), 1e-10);
        assertEquals(0, cc.getNmiJoint(), 1e-10);
        assertEquals(0, cc.getNmiMax(), 1e-10);
        assertEquals(Double.NaN, cc.getNmiMin(), 1e-10); // it's 0/0
        assertEquals(Double.NaN, cc.getNmiSqrt(), 1e-10); // it's 0/0
        assertEquals(0, cc.getNmiSum(), 1e-10);
    }

    /**
     * A slightly more complicated test: a 50/50 split compared to a 50/25/25
     * split. All values below computed by hand and calculator.
     */
    @Test
    public void basicTest2()
    {
        String[][] p1s =
        {
            {
                "a", "b"
            },
            {
                "c", "d"
            }
        };
        TestPartitioning p1 = new TestPartitioning(p1s);
        String[][] p2s =
        {
            {
                "a", "b"
            },
            {
                "c"
            },
            {
                "d"
            }
        };
        TestPartitioning p2 = new TestPartitioning(p2s);
        CommunityComparisons<String> cc = new CommunityComparisons<>(p1, p2);
        assertEquals(0.6931, cc.getEntropyU(), 1e-4);
        assertEquals(1.0397, cc.getEntropyV(), 1e-4);
        assertEquals(cc.getEntropyV(), cc.getJointEntropy(), 1e-10);
        assertEquals(0, cc.getConditionalEntropyUGivenV(), 1e-10);
        assertEquals(0.34657, cc.getConditionalEntropyVGivenU(), 1e-4);
        assertEquals(0.6931, cc.getMutualInformation(), 1e-4);
        assertEquals(0.4904, cc.getExpectedMutualInformation(), 1e-4);
        assertEquals(0.6931 / 1.0397, cc.getNmiJoint(), 1e-4);
        assertEquals(0.6931 / 1.0397, cc.getNmiMax(), 1e-4);
        assertEquals((2 * 0.6931) / (1.0397 + 0.6931), cc.getNmiSum(), 1e-4);
        assertEquals(0.6931 / Math.sqrt(1.0397 * 0.6931), cc.getNmiSqrt(), 1e-4);
        assertEquals(0.6931 / 0.6931, cc.getNmiMin(), 1e-4);
        assertEquals((0.6931 - 0.4904) / (1.0397 - 0.4904), cc.getAmiMax(), 1e-4);
        assertEquals((0.6931 - 0.4904) / (0.5 * (0.6931 + 1.0397) - 0.4904),
            cc.getAmiSum(), 1e-4);
        assertEquals((0.6931 - 0.4904) / (Math.sqrt(0.6931 * 1.0397) - 0.4904),
            cc.getAmiSqrt(), 1e-4);
        assertEquals((0.6931 - 0.4904) / (0.69310 - 0.4904), cc.getAmiMin(),
            1e-4);
    }

}
