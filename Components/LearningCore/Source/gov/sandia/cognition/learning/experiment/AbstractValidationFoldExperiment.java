/*
 * File:                AbstractValidationFoldExperiment.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 1, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.experiment;

import gov.sandia.cognition.learning.data.PartitionedDataset;
import java.util.Collection;

/**
 * The {@code AbstractValidationFoldExperiment} class implements a common way 
 * of structuring an experiment around a {@code ValidationFoldCreator} object
 * where the fold creator is used to create each of the individual trials of
 * the experiment.
 *
 * @param   <InputDataType> The type of data used in the experiment. It is what
 *          is passed to the fold creator to create folds from.
 * @param   <FoldDataType> The type of data in the folds of the experiment, as
 *          defined by the fold creator. It is typically the same as the
 *          InputDataType, but does not have to be.
 * @author  Justin Basilico
 * @since   2.0
 */
public abstract class AbstractValidationFoldExperiment<InputDataType, FoldDataType>
    extends AbstractLearningExperiment
{
    /** The method to use to create the validation folds. */
    protected ValidationFoldCreator<InputDataType, FoldDataType> foldCreator;
    
    /** The number of trials in the experiment, which is the number of folds
     *  in the experiment. */
    protected int numTrials;
    
    /**
     * Creates a new instance of AbstractValidationFoldExperiment.
     */
    public AbstractValidationFoldExperiment()
    {
        this(null);
    }
    
    /**
     * Creates a new instance of AbstractValidationFoldExperiment.
     *
     * @param  foldCreator The fold creator to use.
     */
    public AbstractValidationFoldExperiment(
        final ValidationFoldCreator<InputDataType, FoldDataType> foldCreator)
    {
        super();
        
        this.setFoldCreator(foldCreator);
        this.setNumTrials(-1);
    }
    
    /**
     * Runs the underlying validation fold experiment using the given data. It
     * takes care of firing off the appropriate events for the learner.
     *
     * @param  folds The fold to run the experiment on.
     */
    protected void runExperiment(
        final Collection<PartitionedDataset<FoldDataType>> folds)
    {   
        // The number of trials is the number of folds.
        this.setNumTrials(folds.size());
        
        this.fireExperimentStarted();
        
        // Go through the folds and run the trial for each fold.
        for (PartitionedDataset<FoldDataType> fold : folds)
        {
            this.fireTrialStarted();
            
            // Run the trial on this fold.
            this.runTrial(fold);
            
            this.fireTrialEnded();
        }
                
        this.fireExperimentEnded();
    }
    
    /**
     * Runs a single trial of the experiment on one fold of the data.
     *
     * @param  fold The fold to run the trial of the experiment on.
     */
    protected abstract void runTrial(
        PartitionedDataset<FoldDataType> fold);
    
    /** 
     * Gets the fold creator.
     *
     * @return The fold creator.
     */
    public ValidationFoldCreator<InputDataType, FoldDataType> getFoldCreator()
    {
        return this.foldCreator;
    }

    /**
     * Sets the fold creator.
     *
     * @param  foldCreator The fold creator.
     */
    public void setFoldCreator(
        final ValidationFoldCreator<InputDataType, FoldDataType> foldCreator)
    {
        this.foldCreator = foldCreator;
    }
    
    public int getNumTrials()
    {
        return numTrials;
    }

    /**
     * Sets the number of trials in the experiment.
     *
     * @param  numTrials The number of trials in the experiment.
     */
    protected void setNumTrials(
        final int numTrials)
    {
        this.numTrials = numTrials;
    }
}
