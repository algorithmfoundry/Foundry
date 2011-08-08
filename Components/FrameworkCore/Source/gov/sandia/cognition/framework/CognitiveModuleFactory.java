/*
 * File:                CognitiveModuleFactory.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright February 24, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework;

import java.io.Serializable;

/**
 * The CognitiveModuleFactory interface defines the functionality required by
 * something that creates new CognitiveModules for a CognitiveModel. A
 * CognitiveModel does not accept CognitiveModule objects to be given to it,
 * instead it only will accept a CognitiveModuleFactory to be given to it from
 * which it can create a new CognitiveModule for itself. The reason that this
 * is used is so that CognitiveModules can contain information about the
 * CognitiveModels that they belong to and to ensure that the same 
 * CognitiveModule is not being shared between models. Note, however, that a
 * factory can provide modules that share some model-independent components of
 * their state in order to conserve memory.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @author Zachary Benz
 * @since  1.0
 * @see    CognitiveModule
 */
public interface CognitiveModuleFactory
    extends Serializable
{
    /**
     * Creates a new CognitiveModule for the given CognitiveModel.
     *
     * @param  model The model to create a new module for
     * @return A new CognitiveModule for the given model
     */
    public CognitiveModule createModule(
        CognitiveModel model);
}
