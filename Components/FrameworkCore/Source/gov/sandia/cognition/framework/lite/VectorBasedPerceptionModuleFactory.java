/*
 * File:                VectorBasedPerceptionModuleFactory.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright June 25, 2007, Sandia Corporation.  Under the terms of Contract
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
 * Factory for a VectorBasedPerceptionModule
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
public class VectorBasedPerceptionModuleFactory
    implements CognitiveModuleFactory, Serializable
{
    
    /**
     * CogxelFactory for the module to use in creating Cogxels
     */
    private CogxelFactory cogxelFactory;
    
    
    /** Creates a new instance of VectorBasedPerceptionModuleFactory */
    public VectorBasedPerceptionModuleFactory()
    {
        this( DefaultCogxelFactory.INSTANCE );
    }
    
    /**
     * Creates a new instance of VectorBasedPerceptionModuleFactory
     * @param cogxelFactory 
     * CogxelFactory for the module to use in creating Cogxels
     */
    public VectorBasedPerceptionModuleFactory(
        CogxelFactory cogxelFactory )
    {
        this.setCogxelFactory( cogxelFactory );
    }

    /**
     * Getter for cogxelFactory
     * @return 
     * CogxelFactory for the module to use in creating Cogxels
     */
    public CogxelFactory getCogxelFactory()
    {
        return this.cogxelFactory;
    }

    /**
     * Setter for cogxelFactory
     * @param cogxelFactory 
     * CogxelFactory for the module to use in creating Cogxels
     */
    public void setCogxelFactory(
        CogxelFactory cogxelFactory)
    {
        this.cogxelFactory = cogxelFactory;
    }

    /**
     * {@inheritDoc}
     * @param model {@inheritDoc}
     * @return {@inheritDoc}
     */
    public VectorBasedPerceptionModule createModule(
        CognitiveModel model)
    {
        return new VectorBasedPerceptionModule( this.getCogxelFactory() );
    }
    
}
