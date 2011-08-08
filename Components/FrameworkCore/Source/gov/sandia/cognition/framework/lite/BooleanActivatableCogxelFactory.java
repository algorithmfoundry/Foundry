/*
 * File:                BooleanActivatableCogxelFactory.java
 * Authors:             Jonathan McClain
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright July 3, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.Cogxel;
import gov.sandia.cognition.framework.CogxelFactory;
import gov.sandia.cognition.framework.SemanticIdentifier;
import java.io.Serializable;

/**
 * This class implements a CogxelFactory, which creates ActivatableCogxels.
 *
 * @author Jonathan McClain
 * @since 1.0
 */
public class BooleanActivatableCogxelFactory
        extends Object
        implements CogxelFactory, Serializable
{
    /**
     * An instance of the factory since it has no internal state.
     */
    public static final BooleanActivatableCogxelFactory INSTANCE = 
        new BooleanActivatableCogxelFactory();
    
    /**
     * Creates a new instance of BooleanActivatableCogxelFactory.
     */
    public BooleanActivatableCogxelFactory()
    {
        super();
    }

    /**
     * {@inheritDoc}
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
            return new BooleanActivatableCogxel(identifier);
        }
    }
}
