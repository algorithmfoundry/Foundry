/*
 * File:            FeatureHashing.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2015 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.data.feature;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.hash.HashFunction;
import gov.sandia.cognition.hash.HashFunctionUtil;
import gov.sandia.cognition.hash.Murmur32Hash;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorEntry;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorFactoryContainer;
import gov.sandia.cognition.math.matrix.VectorFunction;
import gov.sandia.cognition.math.matrix.VectorOutputEvaluator;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ArgumentChecker;

/**
 * Implements a function that applies vector feature hashing. This is also known
 * as the "hashing trick". It takes in a  vector (typically sparse) and then 
 * applies hashing to the non-zero values of the vector to produce another 
 * vector, which is usually of a much lower dimensionality. It does this by 
 * hashing each index and then using the sign of the hashed value to either
 * increment or decrement the hashed index by the value.
 * 
 * @author  Justin Basilico
 * @since   3.4.2
 */
@PublicationReference(
    title="Feature Hashing for Large Scale Multitask Learning",
    author={"Kilian Weinberger", "Anirban Dasgupta", "Josh Attenberg", "John Langford", "Alex Smola"},
    year=2009,
    type=PublicationType.Conference,
    publication="Proceedings of the 26th Annual International Conference on Machine Learning (ICML)",
    url="http://arxiv.org/pdf/0902.2206.pdf")
public class FeatureHashing
    extends AbstractCloneableSerializable
    implements VectorFunction, VectorOutputEvaluator<Vector, Vector>, 
        VectorFactoryContainer
{

    /** The default output dimensionality is {@value}. */
    public static final int DEFAULT_OUTPUT_DIMENSIONALITY = 100;

    /** The output size of the hash. */
    protected int outputDimensionality;
    
    /** The hashing function to use. */
    protected HashFunction hashFunction;
    
    /** Vector factory to use. */
    protected VectorFactory<?> vectorFactory;
    
    /**
     * Creates a new {@link FeatureHashing}.
     */
    public FeatureHashing()
    {
        this(DEFAULT_OUTPUT_DIMENSIONALITY);
    }
    
    /**
     * Creates a new {@link FeatureHashing} with the given output size.
     * 
     * @param   outputDimensionality 
     *      The output dimensionality. Cannot be negative.
     */
    public FeatureHashing(
        final int outputDimensionality)
    {
        this(outputDimensionality, new Murmur32Hash(), 
            VectorFactory.getSparseDefault());
    }

    /**
     * Creates a new {@link FeatureHashing} with the given parameters.
     * 
     * @param   outputDimensionality 
     *      The output dimensionality. Cannot be negative.
     * @param hashFunction
     *      The hash function to use.
     * @param vectorFactory
     *      The vector factory to use.
     */
    public FeatureHashing(
        final int outputDimensionality,
        final HashFunction hashFunction,
        final VectorFactory<?> vectorFactory)
    {
        super();
        
        this.setOutputDimensionality(outputDimensionality);
        this.setHashFunction(hashFunction);
        this.setVectorFactory(vectorFactory);
    }
    
    @Override
    public Vector evaluate(
        final Vector input)
    {
// TODO: For a sparse vector the insertion order may make a bad runtime for this. Think through other ways to build up the new vector.
// --jbasilico (2015-07-22)
        final Vector output = this.vectorFactory.createVectorCapacity(
            this.outputDimensionality, input.getEntryCount());
        for (final VectorEntry entry : input)
        {
            // Compute the hash for the current index to get the new one by
            // putting it into the output space size through the modulo 
            // operator.
            final int hashed = this.hash(entry.getIndex());
            final int newIndex = hashed % this.outputDimensionality;
            
            // Use the sign bit of the hash to determine if this value will
            // be treated as a positive or negaive update.
            if (hashed >= 0)
            {
                // Treat as positive.
                output.increment(newIndex, entry.getValue());
            }
            else
            {
                // Treat as negative. Also means the index will be negative,
                // so flip its sign to get the proper index.
                output.increment(-newIndex, -entry.getValue());
            }
        }
        return output;
    }
    
    /**
     * Applies the hashing function to the index.
     * 
     * @param   index
     *      The index to hash.
     * @return 
     *      The hashed value.
     */
    protected int hash(
        final int index)
    {
// TODO: Can we avoid creating the byte arrays?
// -- jbasilico (2015-10-30)
        final byte[] inputBytes = HashFunctionUtil.toByteArray(index);
        final byte[] hashedBytes = this.hashFunction.evaluate(inputBytes);
        return HashFunctionUtil.toInteger(hashedBytes);
    }

    @Override
    public int getOutputDimensionality()
    {
        return this.outputDimensionality;
    }

    /**
     * Sets the output dimensionality, which is the size of the output vector
     * that the input is hashed into.
     * 
     * @param   outputDimensionality 
     *      The output dimensionality. Cannot be negative.
     */
    public void setOutputDimensionality(
        final int outputDimensionality)
    {
        ArgumentChecker.assertIsNonNegative("outputDimensionality", outputDimensionality);
        this.outputDimensionality = outputDimensionality;
    }

    /**
     * Gets the hash function to use.
     * 
     * @return 
     *      The hash function to use.
     */
    public HashFunction getHashFunction()
    {
        return this.hashFunction;
    }

    /**
     * Gets the hash function to use.
     * 
     * @param hashFunction 
     *      The hash function to use.
     */
    public void setHashFunction(
        final HashFunction hashFunction)
    {
        this.hashFunction = hashFunction;
    }

    @Override
    public VectorFactory<?> getVectorFactory()
    {
        return vectorFactory;
    }

    /**
     * Sets the vector factory to use.
     * 
     * @param   vectorFactory 
     *      The vector factory.
     */
    public void setVectorFactory(
        final VectorFactory<?> vectorFactory)
    {
        this.vectorFactory = vectorFactory;
    }
    
}
