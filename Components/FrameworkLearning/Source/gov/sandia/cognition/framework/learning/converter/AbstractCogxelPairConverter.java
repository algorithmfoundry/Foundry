/*
 * File:                AbstractCogxelPairConverter.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 18, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.framework.learning.converter;

import gov.sandia.cognition.framework.CogxelState;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.Pair;

/**
 * Partial implementation of CogxelConverters based on a Pair
 * @param <FirstType> Type of the first element of the pair
 * @param <SecondType> Type of the second element of the pair
 * @param <PairType> Type of the pair
 * @author Kevin R. Dixon
 * @since 3.0
 */
public abstract class AbstractCogxelPairConverter<FirstType,SecondType,PairType extends Pair<FirstType,SecondType>>
    extends AbstractCogxelConverter<PairType>
{
    
    /**
     * The CogxelConverter for the first element of the pair.
     */
    private CogxelConverter<FirstType> firstConverter;
    
    /**
     * The CogxelConverter for the second element of the pair.
     */
    private CogxelConverter<SecondType> secondConverter;
    
    /** 
     * Creates a new instance of AbstractCogxelPairConverter 
     */
    public AbstractCogxelPairConverter()
    {
        this( null, null );
    }
    
    /**
     * Creates a new instance of AbstractCogxelPairConverter 
     * @param firstConverter
     * The CogxelConverter for the first element of the pair.
     * @param secondConverter
     * The CogxelConverter for the second element of the pair.
     */
    public AbstractCogxelPairConverter(
        CogxelConverter<FirstType> firstConverter,
        CogxelConverter<SecondType> secondConverter )
    {
        this( firstConverter, secondConverter, null );
    }
    
    /**
     * Creates a new instance of AbstractCogxelPairConverter 
     * @param firstConverter
     * The CogxelConverter for the first element of the pair.
     * @param secondConverter
     * The CogxelConverter for the second element of the pair.
     * @param semanticIdentifierMap 
     * The SemanticIdentifierMap for the converter.
     */
    public AbstractCogxelPairConverter(
        CogxelConverter<FirstType> firstConverter,
        CogxelConverter<SecondType> secondConverter,
        SemanticIdentifierMap semanticIdentifierMap )
    {
        this.setFirstConverter( firstConverter );
        this.setSecondConverter( secondConverter );
        this.setSemanticIdentifierMap( semanticIdentifierMap );
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractCogxelPairConverter<FirstType,SecondType,PairType> clone()
    {
        AbstractCogxelPairConverter<FirstType,SecondType,PairType> clone =
            (AbstractCogxelPairConverter<FirstType,SecondType,PairType>) super.clone();
        
        clone.firstConverter = ObjectUtil.cloneSafe( this.firstConverter );
        clone.secondConverter = ObjectUtil.cloneSafe( this.secondConverter );
        return clone;
        
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(
        final Object other)
    {
        return (other instanceof AbstractCogxelPairConverter)
            && this.equals((AbstractCogxelPairConverter) other);
    }

    /**
     * Returns true if the two converters have equal internal converters.
     *
     * @param  other InputOutputPairCogxelConverter.
     * @return True if the two converters have the same internal converters.
     */
    protected boolean equals(
        AbstractCogxelPairConverter<?, ?, ?> other)
    {
        return other != null
            && ObjectUtil.equalsSafe(this.getFirstConverter(),
                other.getFirstConverter())
            && ObjectUtil.equalsSafe(this.getSecondConverter(),
                other.getSecondConverter());
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 89 * hash + ObjectUtil.hashCodeSafe(this.firstConverter);
        hash = 89 * hash + ObjectUtil.hashCodeSafe(this.secondConverter);
        return hash;
    }
    
    /**
     * Creates a Pair from the needed data
     * @param first First element of the Pair
     * @param second Second element of the Pair
     * @return Pair with the given parameters
     */
    public abstract PairType createPair(
        FirstType first,
        SecondType second );
    
    public PairType fromCogxels(
        CogxelState cogxels )
    {        
        FirstType first = this.getFirstConverter().fromCogxels( cogxels );
        SecondType second = this.getSecondConverter().fromCogxels( cogxels );
        return this.createPair( first, second );
    }

    public void toCogxels(
        PairType data,
        CogxelState cogxels )
    {
        this.getFirstConverter().toCogxels( data.getFirst(), cogxels);
        this.getSecondConverter().toCogxels( data.getSecond(), cogxels);
    }

    @Override
    public void setSemanticIdentifierMap(
        SemanticIdentifierMap semanticIdentifierMap )
    {
        super.setSemanticIdentifierMap( semanticIdentifierMap );
        
        // Pass the map to the two sub converters.
        if ( this.getFirstConverter() != null )
        {
            this.getFirstConverter().setSemanticIdentifierMap(
                semanticIdentifierMap);
        }
        
        if ( this.getSecondConverter() != null )
        {
            this.getSecondConverter().setSemanticIdentifierMap(
                semanticIdentifierMap);
        }        
        
    }
    
    /**
     * Getter for firstConverter
     * @return
     * The CogxelConverter for the first element of the pair.
     */
    public CogxelConverter<FirstType> getFirstConverter()
    {
        return this.firstConverter;
    }

    /**
     * Setter for firstConverter
     * @param firstConverter
     * The CogxelConverter for the first element of the pair.
     */
    public void setFirstConverter(
        CogxelConverter<FirstType> firstConverter )
    {
        this.firstConverter = firstConverter;
        
        if( this.firstConverter != null )
        {
            this.firstConverter.setSemanticIdentifierMap(
                this.getSemanticIdentifierMap() );
        }
        
    }

    /**
     * Getter for secondConverter
     * @return
     * The CogxelConverter for the second element of the pair.
     */
    public CogxelConverter<SecondType> getSecondConverter()
    {
        return this.secondConverter;
    }

    /**
     * Setter for secondConverter
     * @param secondConverter
     * The CogxelConverter for the second element of the pair.
     */
    public void setSecondConverter(
        CogxelConverter<SecondType> secondConverter )
    {
        this.secondConverter = secondConverter;
        
        if( this.secondConverter != null )
        {
            this.secondConverter.setSemanticIdentifierMap(
                this.getSemanticIdentifierMap() );
        }
    }
    
}
