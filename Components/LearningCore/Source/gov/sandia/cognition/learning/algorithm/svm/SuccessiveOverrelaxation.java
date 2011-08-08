/*
 * File:                SuccessiveOverrelaxation.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 13, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.svm;

import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeSupervisedBatchLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.KernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.NamedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * The {@code SuccessiveOverrelaxation} class implements the Successive 
 * Overrelaxation (SOR) algorithm for learning a Support Vector Machine (SVM).
 * 
 * @param   <InputType> The type of the input data.
 * @author  Justin Basilico
 * @since   2.1
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-23",
    changesNeeded=false,
    comments={
        "Minor cosmetic to javadoc.",
        "Great looking code."
    }
)
@PublicationReference(
    author={"Olvi L. Mangasarian", "David R. Musicant"},
    title="Successive Overrelaxation for Support Vector Machines",
    type=PublicationType.Journal,
    year=1999,
    publication="IEEE Transactions on Neural Networks",
    pages={1032, 1037},
    url="ftp://ftp.cs.wisc.edu/math-prog/tech-reports/98-18.ps"
)
public class SuccessiveOverrelaxation<InputType>
    extends AbstractAnytimeSupervisedBatchLearner<InputType, Boolean, KernelBinaryCategorizer<InputType, DefaultWeightedValue<InputType>>>
    implements MeasurablePerformanceAlgorithm
{

    /** The default maximum number of iterations, {@value}. */
    public static final int DEFAULT_MAX_ITERATIONS = 1000;
    
    /** The default maximum weight is {@value}. */
    public static final double DEFAULT_MAX_WEIGHT = 100.0;
    
    /** The default overrelaxation is {@value}. */
    public static final double DEFAULT_OVERRELAXATION = 1.3;
    
    /** The default minimum change is {@value}. */
    public static final double DEFAULT_MIN_CHANGE = 1e-4;

    /** The kernel to use. */
    protected Kernel<? super InputType> kernel;
    
    /** The maximum weight for a support vector. Must be greater than zero. */
    protected double maxWeight;
    
    /** The overrelaxation parameter. Must be in (0, 2), exclusive. */
    protected double overrelaxation;
    
    /**
     * The minimum change to allow for the algorithm to keep going. If the
     * Total change is below this, then the algorithm will stop. Must be
     * greater than zero.
     */
    protected double minChange;

    /** The result categorizer. */
    protected KernelBinaryCategorizer<InputType, DefaultWeightedValue<InputType>> result;

    /** The total change on the most recent pass. */
    protected double totalChange;

    /** The entry information that the algorithm keeps. */
    protected ArrayList<Entry> entries;
    
    /**
     * The mapping of weight objects to non-zero weighted examples 
     * (support vectors).
     */
    protected LinkedHashMap<InputOutputPair<? extends InputType, ? extends Boolean>, Entry> 
        supportsMap;
    
    /**
     * Creates a new instance of {@code SuccessiveOverrelaxation}.
     */
    public SuccessiveOverrelaxation()
    {
        this(null);
    }
    
    /**
     * Creates a new instance of {@code SuccessiveOverrelaxation}.
     * 
     * @param   kernel
     *      The kernel function to use.
     */
    public SuccessiveOverrelaxation(
        final Kernel<? super InputType> kernel)
    {
        this(kernel, DEFAULT_MAX_WEIGHT, DEFAULT_OVERRELAXATION, 
            DEFAULT_MIN_CHANGE, DEFAULT_MAX_ITERATIONS);
    }
    
    /**
     * Creates a new instance of {@code SuccessiveOverrelaxation}.
     * 
     * @param   kernel
     *      The kernel function to use.
     * @param   maxWeight
     *      The maximum weight allowed for a support vector. Must be positive.
     * @param   overrelaxation
     *      The overrelaxation parameter. Must be in (0, 2), exclusive.
     * @param   minChange
     *      The minimum change to allow for the algorithm to continue. Must
     *      be positive.
     * @param   maxIterations
     *      The maximum number of iterations to run for.
     */
    public SuccessiveOverrelaxation(
        final Kernel<? super InputType> kernel,
        final double maxWeight,
        final double overrelaxation,
        final double minChange,
        final int maxIterations)
    {
        super(maxIterations);
        
        this.setKernel(kernel);
        this.setMaxWeight(maxWeight);
        this.setOverrelaxation(overrelaxation);
        this.setMinChange(minChange);
        
        this.setEntries(null);
        this.setResult(null);
        this.setTotalChange(0.0);
        this.setSupportsMap(null);
    }

    protected boolean initializeAlgorithm()
    {
        if (this.getData() == null)
        {
            // Error: No data to learn on.
            return false;
        }

        // Count the number of valid examples.
        int validCount = 0;
        for (InputOutputPair<? extends InputType, ? extends Boolean> example 
            : this.getData())
        {
            if (example != null)
            {
                validCount++;
            }
        }

        if (validCount <= 0)
        {
            // Nothing to perform learning on.
            return false;
        }

        // Set up the learning variables.
        this.setTotalChange(1.0);
        
        // Set the entries that we use to keep track of the data. We make
        // sure to ignore null examples in doing this.
        this.setEntries(new ArrayList<Entry>(validCount));
        for (InputOutputPair<? extends InputType, ? extends Boolean> example 
            : this.getData())
        {
            if (example != null && example.getOutput() != null)
            {
                this.entries.add(new Entry(example));
            }
        }
        
        this.setSupportsMap(
            new LinkedHashMap<InputOutputPair<? extends InputType, ? extends Boolean>, Entry>());
        
        // We set up the binary categorizer we are building to use the
        // support vectors data structure as the basis for categorization. We
        // will then manipulate those support vectors during the learning 
        // process.
        final Collection<DefaultWeightedValue<InputType>> supports =
            Collections.unmodifiableCollection((Collection<? extends DefaultWeightedValue<InputType>>) this.getSupportsMap().values());
        this.setResult(new KernelBinaryCategorizer<InputType, DefaultWeightedValue<InputType>>(
            this.getKernel(), supports, 0.0));

        return true;
    }

    protected boolean step()
    {
        // Reset the number of errors for the new iteration.
        this.setTotalChange(0.0);
        
        // Part 1: Relaxation pass over all the data.
        // Step 1.1: Sort all the instances by their weight.
        
        // Sort the entries in ascending order.
        Collections.sort(this.entries, Collections.reverseOrder());
            
        // Step 2.1: Loop over all the training instances.
        for (Entry entry : this.entries)
        {
            // Save the weight from the previous step, which is used at the end
            // of the step to calculate the total change.
            entry.previousStepWeight = entry.getWeight();
            
            // Update the entry.
            this.update(entry);
        }
        
        // Part 2: Relaxation pass over interior weights. 
        // (i.e.: Ones that are non-zero).

// TODO: There is an optimization to pre-calculate the values for the pinned 
// maximal support vectors.
        
        // Step 2.1: Compute the number of interior updates to do to make sure
        // it takes about half as long as the full pass just completed. We
        // always do at least one pass here.
        final int numInstances = this.entries.size();
        final int numSupports = this.supportsMap.size();
        final int numNotPinned = numSupports;
        final double interiorIterationsGuess = 
            0.5 * (numInstances + 1.0) + (numSupports + 1.0)
            / ((numNotPinned + 1.0) * (numNotPinned + 1.0));
        final int interiorIterations = 
            Math.max((int) interiorIterationsGuess, 1);
        
        
        // Step 2.3: Sort the supports in ascending order.
        // We make this list because the supports map can be updated while
        // we are iterating over it. Also because we want to sort them in
        // ascending order.
        final ArrayList<Entry> currentSupports = new ArrayList<Entry>(
                this.supportsMap.values());
        Collections.sort(currentSupports);
        
        // Step 2.4: Update the supports.
        for (int i = 0; i < interiorIterations; i++)
        {
            for (Entry support : currentSupports)
            {
                this.update(support);
            }
        }
        
        // Part 3: Compute the total change for the step.
        double changeSum = 0.0;
        for (Entry entry : this.entries)
        {
            final double change = entry.getWeight() - entry.previousStepWeight;
            changeSum += change * change;
        }

        this.setTotalChange(Math.sqrt(changeSum));
        
        // Keep going while the total change is not the minimum change.
        return this.getTotalChange() > this.getMinChange();
    }
    
    /**
     * Performs an update step on the given entry using the successive 
     * overrelaxation procedure. If the entry becomes a support vector, it
     * will be added to the supports data structure.
     * 
     * @param   entry
     *      The entry to update.
     */
    protected void update(
        final Entry entry)
    {
        // Compute the predicted classification and get the actual
        // classification.
        final InputType input = entry.getInput();
        final double actualDouble = entry.outputDouble;
        final double prediction = this.result.evaluateAsDouble(input);
        
        // We are going to update the weight for this example and the
        // global bias.
        double oldWeight = entry.getWeight();
        double bias = this.result.getBias();
        
        // We multiply by the actual double because this implementation
        // combines the weight and the label (actualDouble) into one value
        // to avoid it having to be computed at runtime. However, this means
        // that here we need to premultiply and postmultiply the weight
        // so that the math works out.
        double newWeight = actualDouble * oldWeight
            - (this.overrelaxation / (entry.selfKernel + 1.0)) 
            * (actualDouble * prediction - 1.0);
        newWeight = Math.max(0.0, Math.min(this.maxWeight, newWeight));
        newWeight *= actualDouble;
        
        entry.setWeight(newWeight);
        
        // This is the book-keeping for the support vectors.
        if (newWeight != 0.0)
        {
            if (!entry.supportInserted)
            {
                // The entry is now a support, so add it to the map.
                this.supportsMap.put(entry.example, entry);
                entry.supportInserted = true;
            }
            // else - Entry is already a support.
        }
        else
        {
            if (entry.supportInserted)
            {
                // The entry is no longer a support, so remove it from the
                // map.
                this.supportsMap.remove(entry.example);
                entry.supportInserted = false;
            }
            // else - Entry is already not a support.
        }
        
        // Compute the change in weight in order to update the bias.
        final double change = newWeight - oldWeight;
        this.result.setBias(bias + change);
    }

    protected void cleanupAlgorithm()
    {
        if (this.getSupportsMap() != null)
        {
            // Make the result object have a more efficient backing collection
            // at the end.
            ArrayList<DefaultWeightedValue<InputType>> supports =
                new ArrayList<DefaultWeightedValue<InputType>>(
                    this.supportsMap.size());
            for (Entry entry : this.supportsMap.values())
            {
                supports.add(new DefaultWeightedValue<InputType>(entry));
            }
            
            this.getResult().setExamples(supports);

            this.setSupportsMap(null);
        }
    }

    /**
     * Gets the kernel to use.
     *
     * @return The kernel to use.
     */
    public Kernel<? super InputType> getKernel()
    {
        return this.kernel;
    }

    /**
     * Sets the kernel to use.
     *
     * @param  kernel The kernel to use.
     */
    public void setKernel(
        final Kernel<? super InputType> kernel)
    {
        this.kernel = kernel;
    }
    
    /**
     * Gets the maximum weight allowed on an instance (support vector). It
     * must be positive.
     * 
     * @return The maximum weight allowed on an instance.
     */
    public double getMaxWeight()
    {
        return this.maxWeight;
    }
    
    /**
     * Sets the maximum weight allowed on an instance (support vector). It
     * must be positive.
     * 
     * @param   maxWeight The maximum weight allowed on an instance.
     */
    public void setMaxWeight(
        final double maxWeight)
    {
        if (maxWeight <= 0.0)
        {
            throw new IllegalArgumentException("maxWeight must be positive");
        }
        
        this.maxWeight = maxWeight;
    }

    /**
     * Gets the overrelaxation parameter for the algorithm. It controls the
     * size of the update step. It must be within the range  (0, 2), exclusive.
     * 
     * @return  The overrelaxation parameter for the algorithm.
     */
    public double getOverrelaxation()
    {
        return this.overrelaxation;
    }
    
    /**
     * Gets the overrelaxation parameter for the algorithm. It controls the
     * size of the update step. It must be within the range  (0, 2), exclusive.
     * 
     * @param   overrelaxation  The overrelaxation parameter for the algorithm.
     */
    public void setOverrelaxation(
        final double overrelaxation)
    {
        if (overrelaxation <= 0.0 || overrelaxation >= 2.0)
        {
            throw new IllegalArgumentException(
                "overrelaxation must be in (0.0, 2.0), exclusive.");
        }
        
        this.overrelaxation = overrelaxation;
    }
    
    /**
     * Gets the minimum total weight change allowed for the algorithm to 
     * continue. Must be positive.
     * 
     * @return  The minimum total weight change allowed for the algorithm to
     *      continue.
     */
    public double getMinChange()
    {
        return minChange;
    }
    
    /**
     * Sets the minimum total weight change allowed for the algorithm to 
     * continue. Must be positive.
     * 
     * @param   minChange  The minimum total weight change allowed for the 
     *      algorithm to continue.
     */
    public void setMinChange(
        final double minChange)
    {
        if (minChange < 0.0)
        {
            throw new IllegalArgumentException("minChange must be positive");
        }
        
        this.minChange = minChange;
    }

    public KernelBinaryCategorizer<InputType, DefaultWeightedValue<InputType>> getResult()
    {
        return this.result;
    }

    /**
     * Sets the object currently being result.
     *
     * @param  result The object currently being result.
     */
    protected void setResult(
        final KernelBinaryCategorizer<InputType, DefaultWeightedValue<InputType>> result)
    {
        this.result = result;
    }
    
    /**
     * Gets the data that the algorithm keeps for each training instance.
     * 
     * @return  The data kept for each training instance.
     */
    protected ArrayList<Entry> getEntries()
    {
        return entries;
    }
    
    /**
     * Gets the data that the algorithm keeps for each training instance.
     * 
     * @param   entries  The data kept for each training instance.
     */
    protected void setEntries(
        final ArrayList<Entry> entries)
    {
        this.entries = entries;
    }
    
    /**
     * Gets the mapping of examples to weight objects (support vectors).
     *
     * @return The mapping of examples to weight objects.
     */
    protected LinkedHashMap<InputOutputPair<? extends InputType, ? extends Boolean>, Entry> 
        getSupportsMap()
    {
        return supportsMap;
    }

    /**
     * Gets the mapping of examples to weight objects (support vectors).
     *
     * @param  supportsMap The mapping of examples to weight objects.
     */
    protected void setSupportsMap(
        final LinkedHashMap<InputOutputPair<? extends InputType, ? extends Boolean>, Entry> 
            supportsMap)
    {
        this.supportsMap = supportsMap;
    }

    /**
     * Gets the total change in weight from the most recent step of the 
     * algorithm.
     *
     * @return  The total change in weight from the most recent step.
     */
    public double getTotalChange()
    {
        return this.totalChange;
    }
    
    /**
     * Gets the total change in weight from the most recent step of the 
     * algorithm.
     *
     * @param   totalChange  The total change in weight from the most recent 
     *      step.
     */
    protected void setTotalChange(
        final double totalChange)
    {
        this.totalChange = totalChange;
    }
    
    /**
     * Gets the performance, which is the total change on the last iteration.
     * 
     * @return The performance of the algorithm.
     */
    public NamedValue<Double> getPerformance()
    {
        return new DefaultNamedValue<Double>("change", this.getTotalChange());
    }

    /**
     * The {@code Entry} class represents the data that the algorithm keeps
     * about each training example.
     */
    protected class Entry
        extends DefaultWeightedValue<InputType>
        implements Comparable<Entry>
    {
        /** The example the data pertains to. */
        protected InputOutputPair<? extends InputType, ? extends Boolean> example;
        
        /**
         * The output represented as a raw boolean, to enforce that the label
         *  exists.
         */
        protected boolean output;
        
        /** The output converted to a double form (+1.0 or -1.0). */
        protected double outputDouble;
        
        /**
         * Indicates if the support vector has been inserted into the map of
         * support vectors or not. This allows us to keep the supports map
         * to only contain the entries whose weights are non-zero.
         */
        protected boolean supportInserted;
        
        /**
         * This is the value of the kernel applied to the example and itself.
         * We use this value in the update step, so we can cache it for a 
         * performance boost.
         */
        protected double selfKernel;
        
        /**
         * The weight of the entry on the previous step. This is used at the 
         * end of the step to calculate the total change of weights in the
         * step.
         */
        protected double previousStepWeight;
        
        /**
         * Creates a new {@code Entry} for the given example.
         * 
         * @param   example The example to create the entry for.
         */
        protected Entry(
            final InputOutputPair<? extends InputType, ? extends Boolean> example)
        {
            super(example.getInput(), 0.0);
            
            InputType input = example.getInput();
            this.example = example;
            this.output = example.getOutput();
            this.outputDouble = this.output ? +1.0 : -1.0;
            this.supportInserted = false;
            this.selfKernel = kernel.evaluate(input, input);
            this.previousStepWeight = 0.0;
        }
        
        /**
         * Gets the input that the data belongs to.
         * 
         * @return  The input.
         */
        public InputType getInput()
        {
            return this.value;
        }
        
        /**
         * Gets the output value of the entry as a boolean.
         * 
         * @return  The output.
         */
        public boolean getOutput()
        {
            return this.output;
        }
        
        /**
         * Sets the unlabeled weight. This means that the label is applied to
         * the weight when it is set. Must be non-negative.
         * 
         * @param   unlabeledWeight The unlabeled weight.
         */
        public void setUnlabeledWeight(
            final double unlabeledWeight)
        {
            this.weight = this.output ? +unlabeledWeight : -unlabeledWeight;
        }
        
        /**
         * Gets the unlabeled weight. This means that the label part of the
         * weight value is removed. This means a non-negative value is
         * returned.
         * 
         * @return  The unlabeled weight.
         */
        public double getUnlabeledWeight()
        {
            return this.output ? +this.weight : -this.weight;
        }

        /**
         * Compares this entry to another one by comparing the weights.
         * 
         * @param   other   The entry to compare to.
         * @return  The comparison based on weight.
         */
        public int compareTo(
            final Entry other)
        {
            return Double.compare(this.getUnlabeledWeight(), 
                other.getUnlabeledWeight());
        }
            
    }
}
