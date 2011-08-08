/*
 * File:                CognitiveModelFactory.java
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
import java.util.Collection;

/**
 * The CognitiveModelFactory interface defines an interface for creating a
 * new CognitiveModel using a predefined set of CognitiveModules, as created
 * by CognitiveModuleFactories. The reason it exists is to provide an easy way
 * for multiple models to be created using the same set of modules and
 * (potentially) share parameters and modules.  This could conserve memory is
 * large-scale simulations and such.  If you're not a large-scale simulation
 * or don't need to conserve memory, don't worry: nothing changes for you.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @author Zachary Benz
 * @since  1.0
 */
public interface CognitiveModelFactory
    extends Serializable
{
    /**
     * Creates a CognitiveModel from the factory.
     *
     * @return A new CognitiveModel
     */
    public CognitiveModel createModel();
    
    /**
     * Gets the CognitiveModuleFactories that are used to create a model.
     *
     * @return The Collection of CognitiveModuleFactories used to create
     *         a model.
     */
    public Collection<? extends CognitiveModuleFactory> getModuleFactories();
}
