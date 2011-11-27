/*
 * File:                AffinityPropagation.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 7, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering;

import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeBatchLearner;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.learning.function.distance.DivergenceFunctionContainer;
import gov.sandia.cognition.math.DivergenceFunction;
import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.NamedValue;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * The <code>AffinityPropagation</code> algorithm requires three parameters:
 * a divergence function, a value to use for self-divergence, and a damping
 * factor (called lambda in the paper; 0.5 is the default). It clusters by
 * passing messages between each point to determine the best exemplar for the
 * point.
 * <BR><BR>
 * This implementation takes a divergence function instead of a similarity 
 * function and sets the similarity value to the negative of the divergence 
 * value, as described in the paper for Euclidean distance.
 * <BR><BR>
 * The self-divergence value is what controls how many clusters are generated.
 * Typically this value is set to the mean or median of all the divergence
 * values or the maximum divergence. In general, a smaller value will mean more 
 * clusters and a larger value will mean less clusters. In the paper this is 
 * called self-similarity (s(k,k)) but since this implementation uses a 
 * divergence metric, we use self-divergence instead. 
 *
 * @param   <DataType> The type of data the algorithm is to cluster, which it
 *          passes to the divergence function. For example, this could be 
 *          {@code Vector} or {@code String}.
 * @author  Justin Basilico
 * @since   2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-22",
    changesNeeded=false,
    comments={
        "Removed transient declaration on members.",
        "Fixed a few typos in javadoc.",
        "Added PublicationReference annotation.",
        "Added comment about use of direct-member access.",
        "Code generally looked fine."
    }
)
@PublicationReference(
    author={
        "Brendan J. Frey",
        "Delbert Dueck"
    },
    title="Clustering by Passing Messages Between Data Points.",
    type=PublicationType.Journal,
    publication="Science",
    notes="Volume 315, number 5814",
    pages={972,976},
    year=2007
)
public class AffinityPropagation<DataType>
    extends AbstractAnytimeBatchLearner
        <Collection<? extends DataType>, Collection<CentroidCluster<DataType>>>
    implements BatchClusterer<DataType, CentroidCluster<DataType>>,
        MeasurablePerformanceAlgorithm,
        DivergenceFunctionContainer<DataType, DataType>
{

    // NOTE: This class makes extensive use of direct-member access to improve
    // speed.
    
    /** The default maximum number of iterations is {@value}. */
    public static final int DEFAULT_MAX_ITERATIONS = 100;

    /** The default self similarity is {@value}. */
    public static final double DEFAULT_SELF_DIVERGENCE = 0.0;

    /** The default damping factor (lambda) is {@value}. */
    public static final double DEFAULT_DAMPING_FACTOR = 0.5;

    /** The divergence function to use. */
    protected DivergenceFunction<? super DataType, ? super DataType> divergence;

    /** The value to use for self-divergence. Controls the number of clusters 
     *  created. */
    private double selfDivergence;

    /** The damping factor (lambda). It must be between 0.0 and 1.0. */
    protected double dampingFactor;

    /** The cached value of one minus the damping factor. */
    protected double oneMinusDampingFactor;

    /** The number of examples. */
    protected transient int exampleCount;

    /** The examples. */
    protected ArrayList<DataType> examples;

    /** The array of example-example similarities. */
    protected double[][] similarities;

    /** The array of example-example responsibilities. */
    protected double[][] responsibilities;

    /** The array of example-example availabilities. */
    protected double[][] availabilities;

    /** The assignments of each example to an exemplar (cluster). */
    protected int[] assignments;

    /** The number of examples that have changed assignments in the last 
     *  iteration. */
    protected int changedCount;

    /** The clusters that have been found so far. It is a sparse mapping since
     *  we expect there to be few clusters. */
    protected HashMap<Integer, CentroidCluster<DataType>> clusters;

    /**
     * Creates a new instance of AffinityPropagation.
     */
    public AffinityPropagation()
    {
        this(null, DEFAULT_SELF_DIVERGENCE);
    }

    /**
     * Creates a new instance of AffinityPropagation.
     *
     * @param  divergence The divergence function to use to determine the
     *         divergence between two examples.
     * @param  selfDivergence The value for self-divergence to use, which
     *         controls the number of clusters created.
     */
    public AffinityPropagation(
        DivergenceFunction<? super DataType, ? super DataType> divergence,
        double selfDivergence)
    {
        this(divergence, selfDivergence, DEFAULT_DAMPING_FACTOR);
    }

    /**
     * Creates a new instance of AffinityPropagation.
     *
     * @param  divergence The divergence function to use to determine the
     *         divergence between two examples.
     * @param  selfDivergence The value for self-divergence to use, which
     *         controls the number of clusters created.
     * @param  dampingFactor The damping factor (lambda). Must be between 0.0
     *         and 1.0.
     */
    public AffinityPropagation(
        DivergenceFunction<? super DataType, ? super DataType> divergence,
        double selfDivergence,
        double dampingFactor)
    {
        this(divergence, selfDivergence, dampingFactor, DEFAULT_MAX_ITERATIONS);
    }

    /**
     * Creates a new instance of AffinityPropagation.
     *
     * @param  divergence The divergence function to use to determine the
     *         divergence between two examples.
     * @param  selfDivergence The value for self-divergence to use, which
     *         controls the number of clusters created.
     * @param  dampingFactor The damping factor (lambda). Must be between 0.0
     *         and 1.0.
     * @param  maxIterations The maximum number of iterations.
     */
    public AffinityPropagation(
        DivergenceFunction<? super DataType, ? super DataType> divergence,
        double selfDivergence,
        double dampingFactor,
        int maxIterations)
    {
        super(maxIterations);

        this.setDivergence(divergence);
        this.setSelfDivergence(selfDivergence);
        this.setDampingFactor(dampingFactor);
    }
    
    @Override
    public AffinityPropagation<DataType> clone()
    {
        @SuppressWarnings("unchecked")
        final AffinityPropagation<DataType> result = 
            (AffinityPropagation<DataType>) super.clone();
        result.divergence = ObjectUtil.cloneSafe(this.divergence);
        result.exampleCount = 0;
        result.examples = null;
        result.similarities = null;
        result.responsibilities = null;
        result.availabilities = null;
        result.assignments = null;
        result.changedCount = 0;
        result.clusters = null;
        
        return result;
    }
    
    
    protected boolean initializeAlgorithm()
    {
        if (this.getData() == null || this.getData().size() <= 0)
        {
            // Make sure that the data is valid.
            return false;
        }

        // Initialize the main data for the algorithm.
        this.setExamples(new ArrayList<DataType>(this.getData()));
        this.setSimilarities(new double[this.exampleCount][this.exampleCount]);
        this.setResponsibilities(new double[this.exampleCount][this.exampleCount]);
        this.setAvailabilities(new double[this.exampleCount][this.exampleCount]);

        // Compute the similarity matrix.
        for (int i = 0; i < this.exampleCount; i++)
        {
            final DataType exampleI = this.examples.get(i);

            for (int j = 0; j < this.exampleCount; j++)
            {
                // We compute similarity, which is the negative of divergence,
                // since a lower divergence means a higher similarity.
                final DataType exampleJ = this.examples.get(j);
                final double similarity = -this.divergence.evaluate(
                    exampleI, exampleJ);
                this.similarities[i][j] = similarity;
            }
        }

        // Set the self similarity based on the self divergence.
        for (int i = 0; i < this.exampleCount; i++)
        {
            this.similarities[i][i] = -this.selfDivergence;
        }

        // Initialize the assignments to -1, the changed count, and the 
        // clusters.
        this.setAssignments(new int[this.exampleCount]);
        this.setChangedCount(this.exampleCount);
        this.setClusters(new HashMap<Integer, CentroidCluster<DataType>>());
        for (int i = 0; i < this.exampleCount; i++)
        {
            this.assignments[i] = -1;
        }

        // Ready to learn.
        return true;
    }

    protected boolean step()
    {
        // Update the responsibility values.
        this.updateResponsibilities();

        // Update the availibility values.
        this.updateAvailabilities();

        // Update the assignments based on the responsibility and availability
        // values. 
        this.setChangedCount(0);
        this.updateAssignments();

        // Keep going until there are no more changes in assignments.
        return this.getChangedCount() > 0;
    }

    /**
     * Updates the responsibilities matrix using the similarity values and the
     * current availability values.
     */
    protected void updateResponsibilities()
    {
        // Calculate the new responsibility matrix:
        //     r(i,k) = s(i,k) - max_(k'!=k) { a(i,k') + s(i,k') }
        for (int i = 0; i < this.exampleCount; i++)
        {
            for (int k = 0; k < this.exampleCount; k++)
            {

// TODO: Optimize this. It doesn't need to be a third loop and instead the top
// two maximal values can be computed.
                double max = Double.NEGATIVE_INFINITY;
                for (int c = 0; c < this.exampleCount; c++)
                {
                    if (c == k)
                    {
                        continue;
                    }

                    final double value =
                        this.availabilities[i][c] + this.similarities[i][c];

                    if (value > max)
                    {
                        max = value;
                    }
                }

                // Compute the new responsibility using the damping factor
                // in association with the old responsibility.
                final double responsibility = this.similarities[i][k] - max;
                final double oldResponsibility = this.responsibilities[i][k];
                this.responsibilities[i][k] =
                      this.dampingFactor * oldResponsibility 
                    + this.oneMinusDampingFactor * responsibility;
            }
        }
    }

    /**
     * Updates the availabilities matrix based on the current responsibility
     * values.
     */
    protected void updateAvailabilities()
    {
        // Calculate the new availabilities matrix. Self-availability is 
        // computed differently than other availability.
        //     a(i,k) = min{0, r(k,k) + sum_(j != i,k) max{0, r(j,k)}} 
        //     a(i,i) = sum_(j != i) max{0, r(j,k)}
        for (int i = 0; i < this.exampleCount; i++)
        {
            for (int k = 0; k < this.exampleCount; k++)
            {
                // Both the self-availability and other availability computes
                // the sum of max{0, r(j, k)}, so we compute that first.
                double availability = 0.0;

                for (int j = 0; j < this.exampleCount; j++)
                {
                    if (j == i || j == k)
                    {
                        // We do not include this in the maximum. Note that for
                        // self-availibility i == k.
                        continue;
                    }

                    final double responsibility = this.responsibilities[j][k];

                    if (responsibility > 0.0)
                    {
                        availability += responsibility;
                    }
                }

                if (i != k)
                {
                    // This is not self-availability so we add the 
                    // responsibility and take the minimum with 0.0:
                    // a(i,k) = min{0, r(k,k) + sum_(j != i,k) max{0, r(j,k)}} 
                    availability += this.responsibilities[k][k];
                    availability = Math.min(0.0, availability);
                }
                // else - We used the already computed value for availability:
                //        a(i,i) = sum_(j != i) max{0, r(j,k)}

                // Compute the new availibility using the damping factor
                // in association with the old availibility.
                final double oldAvailability = this.availabilities[i][k];
                this.availabilities[i][k] =
                      this.dampingFactor * oldAvailability 
                    + this.oneMinusDampingFactor * availability;
            }
        }
    }

    /**
     * Updates the assignments of all the examples to their exemplars (clusters)
     * using the current availability and responsibility values. An example
     * i is assigned to the cluster k that maximizes the sum a(i,k) + r(i,k).
     */
    protected void updateAssignments()
    {
        // Compute the new assignments and determine how many assignments have
        // changed. We use a HashMap to do the assignments since we expect a
        // small number of clusters.
        this.setClusters(new HashMap<Integer, CentroidCluster<DataType>>());
        for (int i = 0; i < this.exampleCount; i++)
        {
            // Assign the example to the cluster that maximizes a(i,k) + r(i,k).
            int assignment = -1;
            double maximum = Double.NEGATIVE_INFINITY;

            for (int k = 0; k < this.exampleCount; k++)
            {
                // The total likelihood that example i is assigned to example k
                // is the sum of the availability and responsibility scores.
                final double value =
                    this.availabilities[i][k] + this.responsibilities[i][k];

                if (assignment < 0 || value > maximum)
                {
                    // This is the best value found so far.
                    assignment = k;
                    maximum = value;
                }
            }

            // Assign the example to its new cluster.
            this.assignCluster(i, assignment);
        }
    }

    /**
     * Assigns example "i" to the new cluster index. If the assignment for the
     * example has changed, the method updates all of the information about 
     * cluster membership for this new assignment.
     *
     * @param  i The index of the example to assign to the cluster.
     * @param  newAssignment The new assignment for "i".
     */
    protected void assignCluster(
        final int i,
        final int newAssignment)
    {
        // First determine if the assignment has changed.
        final double oldAssignment = this.assignments[i];

        if (newAssignment != oldAssignment)
        {
            // This cluster assignment has changed.
            this.changedCount++;
        }

        this.assignments[i] = newAssignment;

        // Update the cluster memberships.
        final DataType example = this.examples.get(i);

        // Add the example to the new cluster.
        CentroidCluster<DataType> newCluster = this.clusters.get(newAssignment);
        if (newCluster == null)
        {
            // The new cluster does not yet exist so create it.
            final DataType exemplar = this.examples.get(newAssignment);
            newCluster = new CentroidCluster<DataType>(exemplar);
            newCluster.setIndex(newAssignment);
            this.clusters.put(newAssignment, newCluster);
        }

        // Add the example to the new cluster.
        newCluster.getMembers().add(example);
    }

    protected void cleanupAlgorithm()
    {
        this.setExamples(null);
        this.setSimilarities(null);
        this.setResponsibilities(null);
        this.setAvailabilities(null);
    }

    public ArrayList<CentroidCluster<DataType>> getResult()
    {
        if (this.getClusters() == null)
        {
            return null;
        }
        else
        {
            return new ArrayList<CentroidCluster<DataType>>(
                this.getClusters().values());
        }
    }

    /**
     * Gets the divergence function used by the algorithm.
     *
     * @return The divergence function.
     */
    public DivergenceFunction<? super DataType, ? super DataType> 
        getDivergence()
    {
        return this.divergence;
    }

    /**
     * Sets the divergence function used by the algorithm.
     *
     * @param  divergence The divergence function.
     */
    public void setDivergence(
        final DivergenceFunction<? super DataType, ? super DataType> divergence)
    {
        this.divergence = divergence;
    }

    /**
     * Gets the value used for self-divergence, which controls how many 
     * clusters are generated. A smaller value usually means more clusters
     * and a larger value means less. The range of the value should be within
     * the range of divergence values generated by the divergence function
     *
     * @return The value for self-divergence.
     */
    public double getSelfDivergence()
    {
        return this.selfDivergence;
    }

    /**
     * Sets the value used for self-divergence, which controls how many 
     * clusters are generated. A smaller value usually means more clusters
     * and a larger value means less. The range of the value should be within
     * the range of divergence values generated by the divergence function.
     *
     * @param  selfDivergence The value for self-divergence.
     */
    public void setSelfDivergence(
        final double selfDivergence)
    {
        this.selfDivergence = selfDivergence;
    }

    /**
     * Gets the damping factor.
     *
     * @return The damping factor.
     */
    public double getDampingFactor()
    {
        return this.dampingFactor;
    }

    /**
     * Sets the damping factor.
     *
     * @param  dampingFactor The damping factor. Must be between 0.0 and 1.0.
     */
    public void setDampingFactor(
        final double dampingFactor)
    {
        if (dampingFactor < 0.0 || dampingFactor > 1.0)
        {
            throw new IllegalArgumentException(
                "The damping factor must be between 0.0 and 1.0.");
        }

        this.dampingFactor = dampingFactor;
        this.oneMinusDampingFactor = 1.0 - this.dampingFactor;
    }

    /**
     * Gets the array list of examples to cluster.
     *
     * @return The array list of examples to cluster.
     */
    protected ArrayList<DataType> getExamples()
    {
        return this.examples;
    }

    /**
     * Sets the array list of examples to cluster.
     *
     * @param  examples The array list of examples to cluster.
     */
    protected void setExamples(
        final ArrayList<DataType> examples)
    {
        this.examples = examples;
        this.exampleCount = examples == null ? 0 : examples.size();
    }

    /**
     * Gets the array of similarities.
     *
     * @return The array of similarities.
     */
    protected double[][] getSimilarities()
    {
        return this.similarities;
    }

    /**
     * Sets the array of similarities.
     *
     * @param  similarities The array of similarities.
     */
    protected void setSimilarities(
        final double[][] similarities)
    {
        this.similarities = similarities;
    }

    /**
     * Gets the responsibility values.
     *
     * @return The responsibilities.
     */
    protected double[][] getResponsibilities()
    {
        return this.responsibilities;
    }

    /**
     * Sets the responsibility values.
     *
     * @param  responsibilities The responsibilities.
     */
    protected void setResponsibilities(
        final double[][] responsibilities)
    {
        this.responsibilities = responsibilities;
    }

    /**
     * Gets the availability values.
     *
     * @return The availabilities.
     */
    protected double[][] getAvailabilities()
    {
        return this.availabilities;
    }

    /**
     * Sets the availability values.
     *
     * @param  availabilities The availabilities.
     */
    protected void setAvailabilities(
        final double[][] availabilities)
    {
        this.availabilities = availabilities;
    }

    /**
     * Gets the assignments of examples to exemplars (clusters).
     *
     * @return The assignments of examples to exemplars (clusters).
     */
    protected int[] getAssignments()
    {
        return this.assignments;
    }

    /**
     * Sets the assignments of examples to exemplars (clusters).
     *
     * @param  assignments The assignments of examples to exemplars (clusters).
     */
    protected void setAssignments(
        final int[] assignments)
    {
        this.assignments = assignments;
    }

    /**
     * Gets the number of cluster assignments that have changed in the most
     * recent iteration.
     *
     * @return The number of changed cluster assignments.
     */
    public int getChangedCount()
    {
        return this.changedCount;
    }

    /**
     * Sets the number of cluster assignments that have changed in the most
     * recent iteration.
     *
     * @param  changedCount The number of changed cluster assignments.
     */
    protected void setChangedCount(
        final int changedCount)
    {
        this.changedCount = changedCount;
    }

    /**
     * Gets the current clusters, which is a sparse mapping of exemplar 
     * identifier to cluster object.
     *
     * @return The current clusters.
     */
    protected HashMap<Integer, CentroidCluster<DataType>> getClusters()
    {
        return this.clusters;
    }

    /**
     * Sets the current clusters, which is a sparse mapping of exemplar 
     * identifier to cluster object.
     *
     * @param  clusters The current clusters.
     */
    protected void setClusters(
        final HashMap<Integer, CentroidCluster<DataType>> clusters)
    {
        this.clusters = clusters;
    }

    @Override
    public DivergenceFunction<? super DataType, ? super DataType> getDivergenceFunction()
    {
        return this.getDivergence();
    }
    
    /**
     * Gets the performance, which is the number changed on the last iteration.
     * 
     * @return The performance of the algorithm.
     */
    public NamedValue<Integer> getPerformance()
    {
        return new DefaultNamedValue<Integer>("number changed", this.getChangedCount());
    }
}
