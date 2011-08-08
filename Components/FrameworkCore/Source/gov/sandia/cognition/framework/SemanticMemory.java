/*
 * File:                SemanticMemory.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright February 10, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework;

/**
 * The SemanticMemory interface defines the general functionality required of
 * a module in the CognitiveModel that is a semantic memory.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public interface SemanticMemory
    extends CognitiveModule
{
    /**
     * Gets the underlying SemanticNetwork for the SemanticMemory.
     *
     * @return The SemanticNetwork that have the structure of the 
     *         SemanticMemory.
     */
    public SemanticNetwork getNetwork();
}
