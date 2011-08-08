/*
 * File:                DummyCogxelFactory.java
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

package gov.sandia.cognition.framework;

/**
 * Dummy CogxelFactory for testing purposes
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class DummyCogxelFactory
    extends java.lang.Object
    implements CogxelFactory
{
    /**
     * Creates a new instance of DummyCogxelFactory.
     */
    public DummyCogxelFactory()
    {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @param  identifier {@inheritDoc}
     * @return {@inheritDoc}
     */
    public Cogxel createCogxel(
        SemanticIdentifier identifier)
    {
        return null;
    }
}

