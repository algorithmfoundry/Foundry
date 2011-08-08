/*
 * File:                ArrayBasedPerceptionModuleFactory.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 29, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.CognitiveModel;
import gov.sandia.cognition.framework.CognitiveModuleFactory;
import gov.sandia.cognition.framework.CogxelFactory;
import gov.sandia.cognition.framework.DefaultCogxelFactory;
import java.io.Serializable;

/**
 * The ArrayBasedPerceptionModuleFactory class implements a 
 * CognitiveModuleFactory for ArrayBasedPerceptionModules.
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class ArrayBasedPerceptionModuleFactory
    extends java.lang.Object
    implements CognitiveModuleFactory, Serializable
{
    /** The CogxelFactory for the module to use in creating Cogxels. */
    private CogxelFactory cogxelFactory = null;
    
    /**
     * Creates a new instance of ArrayBasedPerceptionModuleFactory. It uses
     * the DefaultCogxelFactory to give the module for creating Cogxels.
     */
    public ArrayBasedPerceptionModuleFactory()
    {
        this(DefaultCogxelFactory.INSTANCE);
    }
    
    /**
     * Creates a new instance of ArrayBasedPerceptionModuleFactory.
     *
     * @param  cogxelFactory The CogxelFactory for the module to use.
     */
    public ArrayBasedPerceptionModuleFactory(
        CogxelFactory cogxelFactory)
    {
        super();
        
        this.setCogxelFactory(cogxelFactory);
    }
    
    /**
     * {@inheritDoc}
     *
     * @param  model {@inheritDoc}
     * @return {@inheritDoc}
     */
    public ArrayBasedPerceptionModule createModule(
        CognitiveModel model)
    {
        return new ArrayBasedPerceptionModule(
            model.getSemanticIdentifierMap(),
            this.cogxelFactory);
    }

    /**
     * Gets the CogxelFactory to be used by the module.
     *
     * @return The CogxelFactory to be used by the module.
     */
    public CogxelFactory getCogxelFactory()
    {
        return this.cogxelFactory;
    }
    
    /**
     * Sets the cogxel factory for the module to use.
     *
     * @param cogxelFactory The new cogxel factory for the module to use.
     */
    private void setCogxelFactory(
        CogxelFactory cogxelFactory)
    {
        if ( cogxelFactory == null )
        {
            // Error: Bad factory.
            throw new NullPointerException("The cogxelFactory cannot be null.");
        }
        
        this.cogxelFactory = cogxelFactory;
    }
}

