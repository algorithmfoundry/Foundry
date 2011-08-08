/*
 * File:                AbstractConcurrentCognitiveModule.java
 * Authors:             Zachary Benz
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 * 
 * Copyright Jan 9, 2008, Sandia Corporation.  Under the terms of Contract 
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by 
 * or on behalf of the U.S. Government. Export of this program may require a 
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 * 
 */
 
package gov.sandia.cognition.framework.concurrent;

import gov.sandia.cognition.framework.CognitiveModelState;
import gov.sandia.cognition.framework.CognitiveModuleState;

/**
 * The AbstractConcurrentCognitiveModule class is an abstract class that
 * implements common functionality of classes that implement the
 * ConcurrentCognitiveModule interface.
 *
 * @author Zachary Benz
 * @since 2.0
 */
public abstract class AbstractConcurrentCognitiveModule
    implements ConcurrentCognitiveModule
{
    /**
     * This method provides backwards compatibility with the basic,
     * non-concurrent CognitiveModule interface. It calls readState, evaluate,
     * and writeState in sequence to update the state of the model in one step
     * by modifying  the given CognitiveModelState object.  As such, no 
     * concurrency of module evaluation is possible when calling this method. 
     * To achieve concurrent evaluation, readState, evaluate, and writeState 
     * should be called separately in the context of the update method of a 
     * concurrent implementation of the CognitiveModel interface (see, for 
     * example, the MutlithreadedCognitiveModel implementation)
     * 
     * Since a module is not to store any local state information, it is given
     * its previous CognitiveModuleState object in order to provide the 
     * information about its state. It then returns its updated 
     * CognitiveModuleState as the result of the update method.
     * 
     * @param modelState {@inheritDoc}
     * @param previousModuleState {@inheritDoc}
     * @return {@inheritDoc}
     */
    public CognitiveModuleState update(
        CognitiveModelState modelState,
        final CognitiveModuleState previousModuleState)
    {
        this.readState(modelState, previousModuleState);
        this.evaluate();
        return this.writeState(modelState);
    }
}
