/*
 * File:                package-info.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright December 13, 2007, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 *
 * 
 */

/**
 * Provides a lightweight implementation of the Cognitive Framework. This 
 * implementation is geared towards small models and makes use of dense data
 * structures. It is designed to be able to run many multiple models within a 
 * single process, such as a simulation in a high-performance computing 
 * environment. This means having compact data structures that can potentially
 * be shared and a fast update loop. It also means that the model does not 
 * support the dynamic addition and removal of modules at runtime.
 *
 * @author  Justin Basilico
 * @since   1.0
 */
@gov.sandia.cognition.annotation.Documentation
package gov.sandia.cognition.framework.lite;

