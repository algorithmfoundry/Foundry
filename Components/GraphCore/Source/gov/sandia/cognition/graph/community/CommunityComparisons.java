/*
 * File:                CommunityComparisons.java
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

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import java.util.HashSet;
import java.util.Set;

/**
 * This class implements various comparisons of partitionings of a graph (or the
 * resulting communities)
 *
 * @author jdwendt
 * @param <NodeNameType> The type used to name nodes
 */
@PublicationReference(author = "Nguyen Xuan Vinh, Julien Epps, and James Bailey",
    title
    = "Information Theoretic Measures of Clusterings Comparison: Variants, Properties, Normalization and Correction for Chance",
    type = PublicationType.Journal, year = 2010, publication
    = "Journal of Machine Learning Research", pages = 2837 - 2854)
public class CommunityComparisons<NodeNameType>
{

    /**
     * The u-index/v-index contingency table that shows the overlap of u's
     * clustering and v's clustering for each pair of clusters
     */
    private final int[][] contingencyTable;

    /**
     * The size of each of u's clusters. Also the sum across contingency table's
     * rows.
     */
    private final int[] uSums;

    /**
     * The size of each of v's clusters. Also the sum across contingency table's
     * columns.
     */
    private final int[] vSums;

    /**
     * The number of nodes in the original set (also the sum of uSums or vSums
     * or all entries in contingencyTable)
     */
    private final int n;

    /**
     * The number of rows (nu) and columns (nv). uSums.length and vSums.length
     */
    private final int nu, nv;

    /**
     * Once computed, the entropy of u (h(u)) is stored here
     */
    private double entropyU;

    /**
     * Once computed, the entropy of v (h(v)) is stored here
     */
    private double entropyV;

    /**
     * Once computed, the joint entropy (h(u, v)) is stored here
     */
    private double jointEntropy;

    /**
     * Once computed, h(u|v) is stored here
     */
    private double condEntropyUGivenV;

    /**
     * Once computed, h(v|u) is stored here
     */
    private double condEntropyVGivenU;

    /**
     * Once computed, the mutual information (I(u, v)) is stored here
     */
    private double mutualInformation;

    /**
     * Once computed, the expected mutual information (E(I(u, v))) is stored
     * here
     */
    private double expectedMutualInformation;

    /**
     * Initializes this with the input partitionings of the same underlying set
     *
     * @param u The first partitioning
     * @param v The second partitioning
     */
    public CommunityComparisons(NodePartitioning<NodeNameType> u,
        NodePartitioning<NodeNameType> v)
    {
        // First, make sure they partition the same space
        Set<NodeNameType> su = u.getAllMembers();
        Set<NodeNameType> sv = v.getAllMembers();
        if (su.size() != sv.size())
        {
            throw new IllegalArgumentException(
                "Input partitionings do not contain the same number of members: "
                + su.size() + " != " + sv.size());
        }
        Set<NodeNameType> tmp = new HashSet<>(su);
        tmp.retainAll(sv);
        if (tmp.size() != sv.size())
        {
            throw new IllegalArgumentException(
                "Input partitionings don't contain the same set of nodes: original size = "
                + su.size() + " != intersection size " + tmp.size());
        }
        entropyU = entropyV = jointEntropy = condEntropyUGivenV
            = condEntropyVGivenU = mutualInformation = expectedMutualInformation
            = Double.MAX_VALUE;
        n = sv.size();
        nu = u.numPartitions();
        nv = v.numPartitions();
        contingencyTable = new int[nu][];
        uSums = new int[nu];
        vSums = new int[nv];
        for (int i = 0; i < nu; ++i)
        {
            contingencyTable[i] = new int[nv];
            uSums[i] = 0;
        }
        for (int i = 0; i < nv; ++i)
        {
            vSums[i] = 0;
        }

        for (NodeNameType node : su) {
            int i = u.getPartition(node);
            int j = v.getPartition(node);
            ++contingencyTable[i][j];
            ++uSums[i];
            ++vSums[j];
        }
    }

    /**
     * This returns 0 if 0 is passed in. This is valid only because every time
     * this is called with zero, the returned value will be multiplied by zero
     * and the result should be zero.
     *
     * @param d The double whose log is sought
     * @return the natural log of d unless d is zero (then zero)
     */
    private static double safeLog(double d)
    {
        return (d == 0) ? 0 : Math.log(d);
    }

    /**
     * Helper that computes the entropy of the input marginals where N is the
     * sum of the entries in marginals
     *
     * @param N The sum of the entries in marginals
     * @param marginals The partitioning of the values
     * @return the entropy of the input marginals
     */
    private static double getEntropy(int N,
        int[] marginals)
    {
        double ret = 0;
        for (int marginal : marginals)
        {
            double f = ((double) marginal) / ((double) N);
            ret += f * safeLog(f);
        }

        return -ret;
    }

    /**
     * Returns the entropy of the u partitioning (computing and storing it if
     * this is the first time called)
     *
     * @return the entropy of the u partitioning
     */
    public double getEntropyU()
    {
        if (entropyU == Double.MAX_VALUE)
        {
            entropyU = getEntropy(n, uSums);
        }

        return entropyU;
    }

    /**
     * Returns the entropy of the v partitioning (computing and storing it if
     * this is the first time called)
     *
     * @return the entropy of the v partitioning
     */
    public double getEntropyV()
    {
        if (entropyV == Double.MAX_VALUE)
        {
            entropyV = getEntropy(n, vSums);
        }

        return entropyV;
    }

    /**
     * Returns the joint entropy (H(u,v)), computing it if this is the first
     * time called
     *
     * @return the joint entropy
     */
    public double getJointEntropy()
    {
        if (jointEntropy == Double.MAX_VALUE)
        {
            jointEntropy = 0;
            for (int[] row : contingencyTable)
            {
                for (int nij : row)
                {
                    double f = ((double) nij) / (double) n;
                    jointEntropy += f * safeLog(f);
                }
            }
            jointEntropy *= -1;
        }
        return jointEntropy;
    }

    /**
     * Returns H(u|v), computing and storing it if this is the first time called
     *
     * @return H(u|v)
     */
    public double getConditionalEntropyUGivenV()
    {
        if (condEntropyUGivenV == Double.MAX_VALUE)
        {
            condEntropyUGivenV = 0;
            for (int i = 0; i < nu; ++i)
            {
                for (int j = 0; j < nv; ++j)
                {
                    double numer = ((double) contingencyTable[i][j])
                        / (double) n;
                    double denom = ((double) vSums[j]) / (double) n;
                    condEntropyUGivenV += numer * safeLog(numer / denom);
                }
            }
            condEntropyUGivenV *= -1;
        }

        return condEntropyUGivenV;
    }

    /**
     * Returns H(v|u), computing and storing it if this is the first time called
     *
     * @return H(v|u)
     */
    public double getConditionalEntropyVGivenU()
    {
        if (condEntropyVGivenU == Double.MAX_VALUE)
        {
            condEntropyVGivenU = 0;
            for (int i = 0; i < nu; ++i)
            {
                for (int j = 0; j < nv; ++j)
                {
                    double numer = ((double) contingencyTable[i][j])
                        / (double) n;
                    double denom = ((double) uSums[i]) / (double) n;
                    condEntropyVGivenU += numer * safeLog(numer / denom);
                }
            }
            condEntropyVGivenU *= -1;
        }

        return condEntropyVGivenU;
    }

    /**
     * Returns the mutual information (I(u, v)) for the two partitionings,
     * computing and storing it if this is the first time called
     *
     * @return the mutual information
     */
    public double getMutualInformation()
    {
        if (mutualInformation == Double.MAX_VALUE)
        {
            mutualInformation = 0;
            for (int i = 0; i < nu; ++i)
            {
                for (int j = 0; j < nv; ++j)
                {
                    double numer = ((double) contingencyTable[i][j])
                        / (double) n;
                    double denom = ((double) uSums[i] * vSums[j]) / (double) (n
                        * n);
                    mutualInformation += numer * safeLog(numer / denom);
                }
            }
            // TODO: Really?  No negation?
        }

        return mutualInformation;
    }

    /**
     * Returns the joint normalized mutual information (see Table 2 of cited
     * paper)
     *
     * @return the joint normalized mutual information
     */
    public double getNmiJoint()
    {
        return getMutualInformation() / getJointEntropy();
    }

    /**
     * Returns the max normalized mutual information (see Table 2 of cited
     * paper)
     *
     * @return the max normalized mutual information
     */
    public double getNmiMax()
    {
        return getMutualInformation() / Math.max(getEntropyU(), getEntropyV());
    }

    /**
     * Returns the sum normalized mutual information (see Table 2 of cited
     * paper)
     *
     * @return the sum normalized mutual information
     */
    public double getNmiSum()
    {
        return 2 * getMutualInformation() / (getEntropyU() + getEntropyV());
    }

    /**
     * Returns the sqrt normalized mutual information (see Table 2 of cited
     * paper)
     *
     * @return the sqrt normalized mutual information
     */
    public double getNmiSqrt()
    {
        return getMutualInformation() / Math.sqrt(getEntropyU() * getEntropyV());
    }

    /**
     * Returns the min normalized mutual information (see Table 2 of cited
     * paper)
     *
     * @return the min normalized mutual information
     */
    public double getNmiMin()
    {
        return getMutualInformation() / Math.min(getEntropyU(), getEntropyV());
    }

    /**
     * Returns the approximate expected value of the mutual information for the
     * two partitionings. Specifically, the middle value from Equation 4 of the
     * cited paper. Computes and stores the value if this is the first time
     * called.
     *
     * @return the approximate expected value of the mutual information
     */
    public double getExpectedMutualInformation()
    {
        if (expectedMutualInformation == Double.MAX_VALUE)
        {
            expectedMutualInformation = 0;
            double N = n;
            for (int i = 0; i < nu; ++i)
            {
                double ai = uSums[i];
                for (int j = 0; j < nv; ++j)
                {
                    double bj = vSums[j];
                    expectedMutualInformation += (ai * bj) / (N * N) * Math.log(
                        (N * (ai - 1) * (bj - 1)) / ((N - 1) * ai * bj) + (N
                        / (ai * bj)));
                }
            }
            // TODO: Really again no negation?
        }

        return expectedMutualInformation;
    }

    /**
     * Returns the max adjusted-for-chance mutual information (see Table 2 of
     * cited paper)
     *
     * @return the max adjusted-for-chance mutual information
     */
    public double getAmiMax()
    {
        return (getMutualInformation() - getExpectedMutualInformation())
            / (Math.max(getEntropyU(), getEntropyV())
            - getExpectedMutualInformation());
    }

    /**
     * Returns the sum adjusted-for-chance mutual information (see Table 2 of
     * cited paper)
     *
     * @return the sum adjusted-for-chance mutual information
     */
    public double getAmiSum()
    {
        return (getMutualInformation() - getExpectedMutualInformation()) / (0.5
            * (getEntropyU() + getEntropyV()) - getExpectedMutualInformation());
    }

    /**
     * Returns the sqrt adjusted-for-chance mutual information (see Table 2 of
     * cited paper)
     *
     * @return the sqrt adjusted-for-chance mutual information
     */
    public double getAmiSqrt()
    {
        return (getMutualInformation() - getExpectedMutualInformation())
            / (Math.sqrt(getEntropyU() * getEntropyV())
            - getExpectedMutualInformation());
    }

    /**
     * Returns the min adjusted-for-chance mutual information (see Table 2 of
     * cited paper)
     *
     * @return the min adjusted-for-chance mutual information
     */
    public double getAmiMin()
    {
        return (getMutualInformation() - getExpectedMutualInformation())
            / (Math.min(getEntropyU(), getEntropyV())
            - getExpectedMutualInformation());
    }

}
