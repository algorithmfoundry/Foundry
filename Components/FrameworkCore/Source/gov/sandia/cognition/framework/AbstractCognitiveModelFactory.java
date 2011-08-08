/*
 * File:                AbstractCognitiveModelFactory.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 1, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The AbstractCognitiveModelFactory class defines common functionality
 * among CognitiveModelFactory implementations. Specifically, it allows
 * CognitiveModuleFactories to be added to the model factory.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @author Zachary Benz
 * @since 1.0
 */
public abstract class AbstractCognitiveModelFactory
    extends java.lang.Object
    implements CognitiveModelFactory
{
    /** The list of CognitiveModuleFactories to use for the model. */
    private ArrayList<CognitiveModuleFactory> moduleFactories = null;
    
    /**
     * Creates a new instance of AbstractCognitiveModelFactory.
     */
    public AbstractCognitiveModelFactory()
    {
        this(new ArrayList<CognitiveModuleFactory>());
    }
    
    /**
     * Creates a new instance of {@code AbstractCognitiveModelFactory}.
     * 
     * @param   moduleFactories The initial set of module factories.
     */
    public AbstractCognitiveModelFactory(
        final Collection<CognitiveModuleFactory> moduleFactories)
    {
        super();
        
        this.setModuleFactories(
            new ArrayList<CognitiveModuleFactory>(moduleFactories));
    }
    
    /**
     * Adds a CognitiveModuleFactory to be used by this factory when creating
     * a new CognitiveModel. Specifically, the module factories will be given
     * to the new model to instantiate CognitiveModules from.
     *
     * @param factory The CognitiveModuleFactory to add
     */
    public void addModuleFactory(
        CognitiveModuleFactory factory)
    {
        if ( factory == null )
        {
            // Error: Bad factory.
            throw new NullPointerException("The factory cannot be null.");
        }
        
        this.getModuleFactories().add(factory);
    }
    
    /**
     * Gets the CognitiveModuleFactories that are used to create a model.
     *
     * @return The Collection of CognitiveModuleFactories used to create
     *         a model.
     */
    public ArrayList<CognitiveModuleFactory> getModuleFactories()
    {
        return this.moduleFactories;
    }
    
    /**
     * Sets the list of module factories to use.
     *
     * @param moduleFactories The new list of module factories
     */
    public void setModuleFactories(
        ArrayList<CognitiveModuleFactory> moduleFactories)
    {
        if ( moduleFactories == null )
        {
            // Error: Bad module factories.
            throw new NullPointerException(
                "The module factories cannot be null.");
        }
        
        this.moduleFactories = moduleFactories;
    }
}

