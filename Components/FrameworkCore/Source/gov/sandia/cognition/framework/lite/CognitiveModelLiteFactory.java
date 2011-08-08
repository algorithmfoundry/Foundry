/*
 * File:                CognitiveModelLiteFactory.java
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

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.AbstractCognitiveModelFactory;
import gov.sandia.cognition.framework.CognitiveModuleFactory;
import java.util.Collection;

/**
 * The CognitiveModelLiteFactory defines a CognitiveModelFactory for creating
 * CognitiveModelLite objects.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class CognitiveModelLiteFactory
    extends AbstractCognitiveModelFactory
{
    /** 
     * Creates a new instance of CognitiveModelLiteFactory.
     */
    public CognitiveModelLiteFactory()
    {
        super();
    }
    
    /**
     * Creates a new instance of {@code CognitiveModelLiteFactory}.
     * 
     * @param   moduleFactories The initial set of module factories.
     * @since   2.1
     */
    public CognitiveModelLiteFactory(
        final Collection<CognitiveModuleFactory> moduleFactories)
    {
        super(moduleFactories);
    }
    
    /**
     * Creates a CognitiveModelLite using the CognitiveModuleFactories
     * that are part of the model factory.
     *
     * @return A new CognitiveModelLite using the module factories on
     *         this factory
     */
    public CognitiveModelLite createModel()
    {
        return new CognitiveModelLite(this.getModuleFactories());
    }
}
