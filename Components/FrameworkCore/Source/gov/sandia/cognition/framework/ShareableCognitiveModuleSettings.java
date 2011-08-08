/*
 * File:                ShareableCognitiveModuleSettings.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright February 27, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework;

/**
 * The ShareableCognitiveModuleSettings is an interface for module settings
 * that can be shared between two instances of a CognitiveModule. This is 
 * typically used in order to save memory when multiple CognitiveModules are
 * using the same parameters for modules.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public interface ShareableCognitiveModuleSettings
    extends CognitiveModuleSettings
{
}
