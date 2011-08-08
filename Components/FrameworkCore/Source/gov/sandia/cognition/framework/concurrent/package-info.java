/*
 * File:                package-info.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 30, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

/**
 * Provides a concurrent implementation of teh Cognitive Framework. This
 * implementation is geared towards large models that need to make use of 
 * multiple processors or cores. It is designed to run a large model in a
 * single process using threads that run on the different cores. To provide a
 * simple method for parallelization, it defines a module interface,
 * {@code ConcurrentCognitiveModule} that breaks the module update into three
 * pars: a syhcronous read, asynchronous processing, and synchronous write.
 *
 * @author  Justin Basilico
 * @since   2.1
 */
@gov.sandia.cognition.annotation.Documentation
package gov.sandia.cognition.framework.concurrent;

