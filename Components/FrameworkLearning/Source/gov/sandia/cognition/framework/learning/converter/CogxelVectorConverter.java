/*
 * File:                VectorCogxelMap.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright June 21, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.learning.converter;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.framework.Cogxel;
import gov.sandia.cognition.framework.CogxelFactory;
import gov.sandia.cognition.framework.CogxelState;
import gov.sandia.cognition.framework.DefaultCogxelFactory;
import gov.sandia.cognition.framework.SemanticIdentifier;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.framework.SemanticLabel;
import gov.sandia.cognition.math.matrix.DefaultVectorFactoryContainer;
import gov.sandia.cognition.math.matrix.DimensionalityMismatchException;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorFactoryContainer;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * The CogxelVectorConverter implements a converter to convert Cogxels to and
 * from Vector objects.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class CogxelVectorConverter
    extends DefaultVectorFactoryContainer
    implements CogxelConverter<Vector>
{
    /** The labels for each of the elements in the vector. */
    private ArrayList<SemanticLabel> labels;
    
    /** The current SemanticIdentifierMap. */
    private SemanticIdentifierMap semanticIdentifierMap;
    
    /** The list of SemanticIdentifiers, whose positions correspond to
     *  positions in the vector. */
    private ArrayList<SemanticIdentifier> identifiers;
    
    /** The mapping of SemanticIdentifiers to vector indices. */
    private HashMap<SemanticIdentifier, Integer> identifierToIndexMap;
    
    /** The CogxelFactory to use. */
    private CogxelFactory cogxelFactory;
    
    /**
     * Creates a new, empty instance of VectorCogxelMap.
     */
    public CogxelVectorConverter()
    {
        super();
        
        this.setLabels(new ArrayList<SemanticLabel>());
        this.setSemanticIdentifierMap(null);
        this.setIdentifiers(new ArrayList<SemanticIdentifier>());
        this.setIdentifierToIndexMap(new HashMap<SemanticIdentifier, Integer>());
        this.setVectorFactory(VectorFactory.getDefault());
        this.setCogxelFactory(DefaultCogxelFactory.INSTANCE);
    }
    
    /**
     * Creates a new CogxelVectorConverter from the given labels.
     *
     * @param  labels The labels to use.
     */
    public CogxelVectorConverter(
        SemanticLabel... labels)
    {
        this(labels, null);
    }
    
    /**
     * Creates a new CogxelVectorConverter from the given labels.
     *
     * @param  labels The labels to use.
     */
    public CogxelVectorConverter(
        Iterable<SemanticLabel> labels)
    {
        this(labels, null);
    }
    
    /**
     * Creates a new CogxelVectorConverter from the given SemanticIdentifierMap
     * and SemanticLabels.
     *
     * @param  labels The labels to use.
     * @param  semanticIdentifierMap The SemanticIdentifierMap to use.
     */
    public CogxelVectorConverter(
        SemanticLabel[] labels,
        SemanticIdentifierMap semanticIdentifierMap)
    {
        this(Arrays.asList(labels), semanticIdentifierMap);
    }
    
    /**
     * Creates a new CogxelVectorConverter from the given SemanticIdentifierMap
     * and SemanticLabels.
     *
     * @param  labels The labels to use.
     * @param  semanticIdentifierMap The SemanticIdentifierMap to use.
     */
    public CogxelVectorConverter(
        Iterable<SemanticLabel> labels,
        SemanticIdentifierMap semanticIdentifierMap)
    {
        this();
        
        this.addLabels(labels);
        this.setSemanticIdentifierMap(semanticIdentifierMap);
    }
    
    /**
     * Creates a new CogxelVectorConverter
     *
     * @param  labels The labels to use.
     * @param  semanticIdentifierMap The SemanticIdentifierMap to use.
     * @param  vectorFactory The VectorFactory to use.
     * @param  cogxelFactory The CogxelFactory to use.
     */
    public CogxelVectorConverter(
        SemanticLabel[] labels,
        SemanticIdentifierMap semanticIdentifierMap,
        VectorFactory<?> vectorFactory,
        CogxelFactory cogxelFactory)
    {
        this(Arrays.asList(labels), semanticIdentifierMap, vectorFactory, 
            cogxelFactory);
    }
    
    /**
     * Creates a new CogxelVectorConverter.
     *
     * @param  labels The labels to use.
     * @param  semanticIdentifierMap The SemanticIdentifierMap to use.
     * @param  vectorFactory The VectorFactory to use.
     * @param  cogxelFactory The CogxelFactory to use.
     */
    public CogxelVectorConverter(
        Iterable<SemanticLabel> labels,
        SemanticIdentifierMap semanticIdentifierMap,
        VectorFactory<?> vectorFactory,
        CogxelFactory cogxelFactory)
    {
        this(labels, semanticIdentifierMap);
        
        this.setVectorFactory(vectorFactory);
        this.setCogxelFactory(cogxelFactory);
    }
        
    /**
     * Creates a new copy of the given CogxelVectorConverter.
     *
     * @param  other The CogxeLVectorConverter to copy.
     */
    public CogxelVectorConverter(
        CogxelVectorConverter other)
    {
        this(new ArrayList<SemanticLabel>(other.getLabels()),
            other.getSemanticIdentifierMap(), 
            other.getVectorFactory(),
            other.getCogxelFactory());
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public CogxelVectorConverter clone()
    {
        return new CogxelVectorConverter(this);
    }
    
    /**
     * Adds the given SemanticLabel to the list of labels used by the
     * converter.
     *
     * @param  label The label to add.
     */
    public void addLabel(
        SemanticLabel label)
    {
        // Validate the parameters.
        if ( label == null )
        {
            throw new IllegalArgumentException("label is null");
        }
        
        // Add the label.
        this.getLabels().add(label);
        this.addLabelToIdentifierCache(label);
    }
    
    /**
     * Adds all of the given labels to the converter.
     *
     * @param  labels The labels to add to the converter.
     */
    public void addLabels(
        Iterable<SemanticLabel> labels)
    {
        for ( SemanticLabel label : labels )
        {
            this.addLabel(label);
        }
    }
    
    /**
     * Adds all of the given labels to the converter.
     *
     * @param  labels The labels to add to the converter.
     */
    public void addLabels(
        SemanticLabel[] labels)
    {
        for ( SemanticLabel label : labels )
        {
            this.addLabel(label);
        }
    }
    
    /**
     * Rebuilds the cache of SemanticIdentifier objects. Should only need to
     * be called after the SemanticIdentifierMap is changed.
     */
    protected void buildIdentifierCache()
    {
        // Clear out the semantic identifier containers.
        this.setIdentifiers(
            new ArrayList<SemanticIdentifier>());
        this.setIdentifierToIndexMap(
            new HashMap<SemanticIdentifier, Integer>());
        
        // Add each label to the cache.
        SemanticIdentifierMap identifierMap = this.getSemanticIdentifierMap();
        if ( identifierMap != null )
        {
            for ( SemanticLabel label : this.getLabels() )
            {
                this.addLabelToIdentifierCache(label);
            }
        }
        // else - There is no map so we don't put anything in the cache.
    }
    
    /**
     * Adds the given label to the cache of SemanticIdentifiers. This is used
     * so that the fast SemanticIdentifiers can be used by the converter 
     * instead of the SemanticLabel.
     *
     * @param  label The label to add.
     */
    protected void addLabelToIdentifierCache(
        SemanticLabel label)
    {
        if ( this.semanticIdentifierMap == null )
        {
            // No identifier map is specified.
            return;
        }
        
        // Get the identifier for the label and add it to the cache.
        SemanticIdentifier identifier = 
            this.getSemanticIdentifierMap().addLabel(label);
        int index = this.getIdentifiers().size();
        this.getIdentifiers().add(identifier);
        this.getIdentifierToIndexMap().put(identifier, index);
    }
    
    /**
     * {@inheritDoc}
     *
     * @param  cogxels {@inheritDoc}
     * @return {@inheritDoc}
     */
    public Vector fromCogxels(
        CogxelState cogxels)
    {
        // Validate the parameters.
        if ( cogxels == null )
        {
            throw new IllegalArgumentException("cogxels is null");
        }
        
        // Validate the state.
        if ( this.getSemanticIdentifierMap() == null )
        {
            throw new IllegalStateException(
                "The semanticIdentifierMap is not set.");
        }
        
        // Create the vectors to store the inputs and outputs.
        Vector vector = this.createEmptyVector();
        
        // Choose which way to iterate over the cogxels by figuring out how
        // many there are and how big the vector is.
        if ( cogxels.getNumCogxels() > this.getDimensionality() )
        {
            // This is optimized for the case when the number of labels in the
            // converter is less than the number of cogxels.
            int index = 0;
            for ( SemanticIdentifier identifier : this.getIdentifiers() )
            {
                double value = cogxels.getCogxelActivation(identifier);
                vector.setElement(index, value);
                index++;
            }
        }
        else
        {
            // This is optimized for the case where the majority of the Cogxels 
            // are expected to be zero.
            for ( Cogxel cogxel : cogxels )
            {
                // Get the index of the identifier.
                SemanticIdentifier identifier = cogxel.getSemanticIdentifier();

                int index = this.findIndexForIdentifier(identifier);

                if ( index < 0 )
                {
                    // Not a part of our vector.
                    continue;
                }

                // Get the cogxel.
                double value = cogxel.getActivation();
                vector.setElement(index, value);
            }
        }

        // Return the created vector.
        return vector;  
    }

    /**
     * {@inheritDoc}
     *
     * @param  data {@inheritDoc}
     * @param  cogxels {@inheritDoc}
     */
    public void toCogxels(
        Vector data,
        CogxelState cogxels)
    {
        // Validate the parameters.
        if ( data == null )
        {
            throw new IllegalArgumentException("data is null");
        }
        else if ( cogxels == null )
        {
            throw new IllegalArgumentException("cogxels is null");
        }

        // Validate the state.
        if ( this.getSemanticIdentifierMap() == null )
        {
            throw new IllegalStateException(
                "The semanticIdentifierMap is not set.");
        }
        
        // Make sure the given Vector is of the proper dimensionality.
        int dimensionality = data.getDimensionality();
        if ( dimensionality != this.getDimensionality() )
        {
            throw new DimensionalityMismatchException(dimensionality,
                this.getDimensionality());
        }
        
        // Copy the values into the CogxelState.
        CogxelFactory factory = this.getCogxelFactory();
        for (int i = 0; i < dimensionality; i++)
        {
            // Get the value from the i-th element of the vector.
            double value = data.getElement(i);
            
            // Get the i-th identifier.
            SemanticIdentifier identifier = this.getIdentifiers().get(i);
            
            // Get or create the Cogxel.
            Cogxel cogxel = 
                cogxels.getOrCreateCogxel(identifier, factory);
            
            // Set the Cogxel's activation.
            cogxel.setActivation(value);
        }
    }
    
    /**
     * Finds the vector index for the given SemanticIdentifier.
     *
     * @param  identifier The SemanticIdentifier to find the integer index for.
     * @return The integer index for the given SemanticIdentifier, if it is
     *         an identifier this converter is using; otherwise, -1.
     */
    protected int findIndexForIdentifier(
        SemanticIdentifier identifier)
    {
        // look for th index.
        Integer index = this.getIdentifierToIndexMap().get(identifier);
        
        if ( index == null )
        {
            // The index does not exist.
            return -1;
        }
        else
        {
            // The index was located.
            return index;
        }       
    }
    
    /**
     * {@inheritDoc}
     *
     * @param  other {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean equals(
        Object other)
    {
        if ( other instanceof CogxelVectorConverter )
        {
            return this.equals((CogxelVectorConverter) other);
        }
        else
        {   
            return false;
        }
    }
    
    /**
     * Returns true if the two converters have the same labels.
     *
     * @param  other Another CogxelVectorConverter.
     * @return True if the two converters have the same labels.
     */
    public boolean equals(
        CogxelVectorConverter other)
    {
        if ( other == null || other.getDimensionality() != this.getDimensionality() )
        {
            return false;
        }
        
        int length = this.getDimensionality();
        for (int i = 0; i < length; i++)
        {
            if ( !this.getLabels().get(i).equals(other.getLabels().get(i)) )
            {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        if (!CollectionUtil.isEmpty(this.labels))
        {
            for (SemanticLabel label : this.labels)
            {
                hash = 43 * hash + ObjectUtil.hashCodeSafe(label);
            }
        }
        return hash;
    }
    
    /**
     * Creates an empty Vector for the converter, of the proper dimensionality.
     *
     * @return An empty Vector for the converter to use.
     */
    public Vector createEmptyVector()
    {
        return this.getVectorFactory().createVector(this.getDimensionality());
    }
    
    /**
     * Gets the dimensionality of the Vector created by the converter.
     *
     * @return The dimensionality of the Vector created by the converter.
     */
    public int getDimensionality()
    {
        return this.getLabels().size();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public SemanticIdentifierMap getSemanticIdentifierMap()
    {
        return semanticIdentifierMap;
    }
    
    /**
     * {@inheritDoc}
     *
     * @param  semanticIdentifierMap {@inheritDoc}
     */
    public void setSemanticIdentifierMap(
        SemanticIdentifierMap semanticIdentifierMap)
    {
        this.semanticIdentifierMap = semanticIdentifierMap;
        this.buildIdentifierCache();
    }

    /**
     * Gets the labels used by the converter, each one corresponding to one
     * dimension in the converted Vector.
     *
     * @return The labels used by the converter.
     */
    public ArrayList<SemanticLabel> getLabels()
    {
        return this.labels;
    }
    
    /**
     * Sets the labels to be used by the converter, each one corresponding to 
     * one dimension in the converted Vector.
     *
     * @param labels The labels to be used by the converter.
     */
    public void setLabels(
        ArrayList<SemanticLabel> labels)
    {
        this.labels = labels;
        this.buildIdentifierCache();
    }
    
    /**
     * Gets the list of cached SemanticIdentifiers.
     *
     * @return The list of cached SemanticIdentifiers.
     */
    protected ArrayList<SemanticIdentifier> getIdentifiers()
    {
        return this.identifiers;
    }
    
    /**
     * Sets the list of cached SemanticIdentifiers.
     *
     * @param  identifiers The list of cached SemanticIdentifiers.
     */
    protected void setIdentifiers(
        ArrayList<SemanticIdentifier> identifiers)
    {
        this.identifiers = identifiers;
    }
    
    /**
     * Gets the cached mapping of SemanticIdentifier to vector index.
     *
     * @return The cached mapping of SemanticIdentifier to vector index.
     */
    protected HashMap<SemanticIdentifier, Integer> getIdentifierToIndexMap()
    {
        return this.identifierToIndexMap;
    }
    
    /**
     * Sets the cached mapping of SemanticIdentifier to vector index.
     *
     * @param   identifierToIndexMap The cached mapping of SemanticIdentifier 
     *          to vector index.
     */
    protected void setIdentifierToIndexMap(
        HashMap<SemanticIdentifier, Integer> identifierToIndexMap)
    {
        this.identifierToIndexMap = identifierToIndexMap;
    }

    /**
     * Gets the CogxelFactory used to create the Cogxels used by the converter.
     *
     * @return The CogxelFactory used to create the Cogxels used by the 
     *         converter.
     */
    public CogxelFactory getCogxelFactory()
    {
        return this.cogxelFactory;
    }
    
    /**
     * Gets the CogxelFactory used to create the Cogxels used by the converter.
     *
     * @param  cogxelFactory The CogxelFactory used to create the Cogxels used  
     *         by the converter.
     */
    public void setCogxelFactory(
        CogxelFactory cogxelFactory)
    {
        this.cogxelFactory = cogxelFactory;
    }
}
