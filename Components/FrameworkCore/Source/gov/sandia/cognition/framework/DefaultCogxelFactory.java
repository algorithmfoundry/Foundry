/*
 * File:                DefaultCogxelFactory.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 16, 2006, Sandia Corporation.  Under the terms of Contract
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
 * This class implements a CogxelFactory that returns the default type of 
 * DefaultCogxel.
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class DefaultCogxelFactory
    extends java.lang.Object
    implements CogxelFactory, Serializable
{
    /**
     * An instance of the factory since it has no internal state.
     */
    public static final DefaultCogxelFactory INSTANCE = 
        new DefaultCogxelFactory();
    
    /**
     * Creates a new instance of DefaultCogxelFactory.
     */
    public DefaultCogxelFactory()
    {
        super();
    }
    
    /**
     * {@inheritDoc}
     *
     * @param identifier {@inheritDoc}
     * @return {@inheritDoc}
     */
    public Cogxel createCogxel(
        SemanticIdentifier identifier)
    {
        if ( identifier == null )
        {
            // Error: Bad identifier.
            return null;
        }
        else
        {
            // Create the new Cogxel.
            return new DefaultCogxel(identifier);
        }
    }
}

