/*
 * File:                DummyModuleFactory.java
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

/**
 * This class is a dummy module factory for testing.
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class DummyModuleFactory
    extends java.lang.Object
    implements CognitiveModuleFactory
{
    /**
     * Creates a new instance of DummyModuleFactory.
     */
    public DummyModuleFactory()
    {
        super();
    }
    
    /**
     * {@inheritDoc}
     *
     * @param model {@inheritDoc}
     * @return {@inheritDoc}
     */
    public CognitiveModule createModule(
        CognitiveModel model)
    {
        return new DummyModule();
    }
}

