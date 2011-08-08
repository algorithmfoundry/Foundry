/*
 * File:                CognitiveModuleFactoryLearner.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright June 20, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.learning;

import gov.sandia.cognition.framework.CognitiveModel;
import gov.sandia.cognition.framework.CognitiveModelInput;
import gov.sandia.cognition.framework.CognitiveModuleFactory;
import gov.sandia.cognition.util.CloneableSerializable;
import java.util.Collection;

/**
 * The CognitiveModuleFactoryLearner is an interface defining the functionality
 * of an Object that can learn a CognitiveModuleFactory from a collection of
 * input data.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  2.0
 */
public interface CognitiveModuleFactoryLearner
    extends CloneableSerializable
{
    /**
     * Learns a new CognitiveModuleFactory for the given CognitiveModuleFactory
     * containing all of the modules that will be used before the created
     * module factory along with the example data used to learn the factory 
     * from.
     *
     * @param  model CognitiveModel to learn the new CognitiveModuleFactory for.
     * Note that this has to be a CognitiveModel, not a CognitiveModelFactory,
     * as the CognitiveModelInputs (needed for the dataset) needs
     * SemanticIdentifier, which is specific to a CognitiveModel.  Since the
     * dataset must be created before this method call, the CognitiveModel
     * used to give the SemanticIdentifiers to the dataset must also be the
     * parameter here.
     * @param  datasets The datasets containing the inputs to the CognitiveModel
     *         for one or more sequences of data
     * @return The CognitiveModuleFactory learned based on the given model
     *         factory and input data.
     */
    public CognitiveModuleFactory learn(
        CognitiveModel model,
        Collection<? extends Collection<? extends CognitiveModelInput>> datasets);
}
