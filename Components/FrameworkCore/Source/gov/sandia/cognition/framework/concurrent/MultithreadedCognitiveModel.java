/*
 * File:                MultithreadedCognitiveModel.java
 * Authors:             Zachary Benz
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 * 
 * Copyright Jan 9, 2008, Sandia Corporation.  Under the terms of Contract 
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by 
 * or on behalf of the U.S. Government. Export of this program may require a 
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 * 
 */
 
package gov.sandia.cognition.framework.concurrent;

import gov.sandia.cognition.framework.CognitiveModelInput;
import gov.sandia.cognition.framework.CognitiveModelState;
import gov.sandia.cognition.framework.CognitiveModule;
import gov.sandia.cognition.framework.CognitiveModuleFactory;
import gov.sandia.cognition.framework.CognitiveModuleState;
import gov.sandia.cognition.framework.DefaultSemanticIdentifierMap;
import gov.sandia.cognition.framework.lite.AbstractCognitiveModelLite;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class provides a multithreaded implementation of the CognitiveModel
 * interface.  The implementation is lite, in that modules cannot be dynamically
 * added or removed from the model.  Multihtreading is employed at the level
 * of the evaluation of modules during an update call.  During update, each
 * module sequentially performs a readState in preparation for evaluation, and
 * then evaluation of the modules is performed concurrently using a threadpool.
 * The change in state of each module is written out sequentially following
 * the completion of all module evaluations.
 *
 * @author Zachary Benz
 * @since 2.0
 */
public class MultithreadedCognitiveModel
    extends AbstractCognitiveModelLite
{   
    /**
     * Implements a Callable class for executing the evaluation of a 
     * CognitiveModule on a thread
     * 
     * @author Zachary Benz
     * @since 2.0
     */
    protected static class ModuleEvaluator
        implements Callable<Exception>
    {  
        /** The module to run the evaluation on */
        private ConcurrentCognitiveModule module;
        
        /** CognitiveModelState to use when evaluating module */
        private CognitiveModelState modelState;
        
        /** CognitiveModuleState to use when evaluating module */
        private CognitiveModuleState previousModuleState;
        
        /**
         * Creates a new instance of ModuleEvaluator that can be supplied to a
         * thread for executing a module evaluation
         * 
         * @param module The module to the run the evaluation on
         * @param modelState CognitiveModelState to use when evaluating module
         * @param previousModuleState CognitiveModuleState to use when
         *        evaluating the module
         */
        ModuleEvaluator(
            ConcurrentCognitiveModule module,
            CognitiveModelState modelState,
            CognitiveModuleState previousModuleState)
        {
            this.module = module;
            this.modelState = modelState;
            this.previousModuleState = previousModuleState;
        }
 
        /**
         * {@inheritDoc}
         */
        public Exception call() 
        {              
            try
            {
                // Evaluate the module
                module.evaluate();
            }            
            catch (Exception e)
            {
                // Caught an exception
                return e;
            }
            
            // No exception caught, so return null
            return null;                
        }
     }    
    
    /** The CognitiveModules that are part of this model. */
    private ConcurrentCognitiveModule[] modules = null;
    
    /** A thread pool for managing concurrent evaluation of modules */
    private ExecutorService fixedThreadPool;
    
    /**
     * Creates a new instance of MultithreadedCognitiveModel.  It instantiates
     * new CognitiveModules from all of the given CognitiveModuleFactories
     * 
     * @param numThreadsInPool Number of threads to use in thread pool
     * @param moduleFactories The CognitiveModuleFactories used to create the
     *        CognitiveModules for this model
     */
    public MultithreadedCognitiveModel(
        final int numThreadsInPool, 
        CognitiveModuleFactory... moduleFactories)
    {
        this(numThreadsInPool, Arrays.asList(moduleFactories));
    }
    
    /**
     * Creates a new instance of MultithreadedCognitiveModel.  It instantiates
     * new CognitiveModules from all of the given CognitiveModuleFactories
     * 
     * @param numThreadsInPool Number of threads to use in thread pool
     * @param moduleFactories The CognitiveModuleFactories used to create the
     *        CognitiveModules for this model
     */
    public MultithreadedCognitiveModel(
        final int numThreadsInPool, 
        Iterable<? extends CognitiveModuleFactory> moduleFactories)
    {
        super();
        
        this.setSemanticIdentifierMap(
            new DefaultSemanticIdentifierMap());
        
        // Instantiate the CognitiveModules from the given factories.
        LinkedList<ConcurrentCognitiveModule> moduleList = 
            new LinkedList<ConcurrentCognitiveModule>();
        
        for ( CognitiveModuleFactory factory : moduleFactories )
        {
            if( factory != null )
            {
                CognitiveModule module = factory.createModule(this);
                if (module instanceof ConcurrentCognitiveModule)
                {
                    moduleList.add((ConcurrentCognitiveModule)module);
                }
                else
                {
                    throw new IllegalArgumentException(
                        "MultithreadedCognitiveModel: All modules must " +
                        "conform to ConcurrentCognitiveModule interface, " +
                        "but " + factory.getClass().toString() + " created an" +
                        " object of type " + module.getClass().toString() + 
                        ", which doesn't conform.");
                }
            }
        }
        
        // Set the modules.
        this.setModules(moduleList);
        this.resetCognitiveState();
        
        // Configure thread pool for execution
        this.setFixedThreadPool(Executors.newFixedThreadPool(numThreadsInPool));
    }    
    
    /**
     * Updates the state of the model from the new input.
     *
     * @param input The input to the model.     
     */     
    public void update(CognitiveModelInput input)
    {
        // Set the input on the state.
        this.state.setInput(input);
        
        CognitiveModuleState[] moduleStates = this.state.getModuleStatesArray();
        
        // ReadState doesn't support concurrency, as state can be
        // dynamically filled in on read due to sparse nature of state
        // (i.e. attempting to read something that doesn't exist yet will
        // cause it to become initialized, thus changing the state)
        for (int i = 0; i < this.numModules; i++)
        {                                    
            this.modules[i].readState(this.state, moduleStates[i]);
        }
        
        // Evaluate can occur concurrently across modules
        Collection<Callable<Exception>> moduleEvaluators = 
            new ArrayList<Callable<Exception>>(this.numModules);
        for (int i = 0; i < this.numModules; i++)
        {                                                    
            moduleEvaluators.add(new ModuleEvaluator(this.modules[i], 
                this.state, moduleStates[i]));
        }
        // Invoke the evaluators and block until all are completed
        List<Future<Exception>> results;
        try
        {
            results = this.getFixedThreadPool().invokeAll(moduleEvaluators);        
        }
        catch (InterruptedException e)
        {            
            throw new IllegalThreadStateException(e.toString());
        }
        
        // Check for exceptions in the results
        Exception firstException = null;
        int exceptionCounter = 0;
        for (Future<Exception> result : results)
        {
            Exception exception;
            try
            {
                // Attempting to obtain the result can fail
                exception = result.get();
            }
            catch (Exception e)
            {
                // Set the exception to the exception generated when attempting
                // to get the result exception
                exception = e;
            }
            if (exception != null)
            {
                exceptionCounter++;
                if (firstException == null)
                {
                    firstException = exception;
                }
            }
        }
        if (exceptionCounter > 0)
        {            
            String errorMessage = "MultithreadedCognitiveModel.update: " +
                exceptionCounter + " exceptions encountered during module " +
                "evaluation";
            System.out.println(errorMessage + "; printing stack trace of " +
                "first exception encountered:");
            firstException.printStackTrace();
            throw new IllegalArgumentException(errorMessage + "; throwing " +
                "first exception encountered", firstException);            
        }
        
        // WriteState happens sequentially at end after concurrent evaluation
        // of modules has occurred.
        for (int i = 0; i < this.numModules; i++)
        {     
            // We are operating by side effect with this line of code... it's
            // changing the this.state.getModuleStatesArray return members.
            moduleStates[i] = 
                this.modules[i].writeState(this.state);
        }
        
        this.fireModelStateChangedEvent();
    }
    
    /**
     * Gets the modules in the model.
     *
     * @return The modulese that are part of the model.
     */
    public List<ConcurrentCognitiveModule> getModules()
    {
        return Arrays.asList(this.modules);
    }   

    /**
     * Sets the modules to use in the model.
     *
     * Note: This is declared private because it cannot be changed from its
     * initial value without breaking the model.
     *
     * This function should be called exactly once.
     *
     * @param moduleCollection The modules to use in the model
     */
    private void setModules(
        Collection<? extends ConcurrentCognitiveModule> moduleCollection)
    {
        this.numModules = moduleCollection.size();
        this.modules = moduleCollection.toArray(
            new ConcurrentCognitiveModule[this.numModules]);
    }

    /**
     * Returns the thread pool for executing module evaluations
     * 
     * @return The thread pool for executing module evaluations
     */
    private ExecutorService getFixedThreadPool()
    {
        return fixedThreadPool;
    }

    /**
     * Sets the thread pool for executing module evaluations
     * 
     * @param fixedThreadPool The thread pool to use for executing module
     *        evaluations
     */
    private void setFixedThreadPool(
        final ExecutorService fixedThreadPool)
    {
        this.fixedThreadPool = fixedThreadPool;
    }
}
