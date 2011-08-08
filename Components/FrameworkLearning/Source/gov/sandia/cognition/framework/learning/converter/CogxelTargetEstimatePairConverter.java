/*
 * File:                CogxelTargetEstimatePairConverter.java
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

import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.learning.data.DefaultTargetEstimatePair;
import gov.sandia.cognition.learning.data.TargetEstimatePair;

/**
 * CogxelConverter based on a TargetEstimatePair.
 * 
 * @param <TargetType> Type of the targets
 * @param <EstimateType> Type of the estimates
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class CogxelTargetEstimatePairConverter<TargetType, EstimateType>
    extends AbstractCogxelPairConverter<TargetType, EstimateType, TargetEstimatePair<TargetType, EstimateType>>
{

    /**
     * Creates a new CogxelTargetEstimatePairConverter.
     */
    public CogxelTargetEstimatePairConverter()
    {
        this(null, null);
    }

    /**
     * Creates a new CogxelTargetEstimatePairConverter with the given
     * converters for each element of the pair.
     *
     * @param  targetConverter
     *      The CogxelConverter for the target element of the pair.
     * @param  estimateConverter
     *      The CogxelConverter for the estimate element of the pair.
     */
    public CogxelTargetEstimatePairConverter(
        final CogxelConverter<TargetType> targetConverter,
        final CogxelConverter<EstimateType> estimateConverter)
    {
        this(targetConverter, estimateConverter, null);
    }

    /**
     * Creates a new CogxelTargetEstimatePairConverter with the given
     * converters for each element of the pair.
     *
     * @param  targetConverter
     *      The CogxelConverter for the target element of the pair.
     * @param  estimateConverter
     *      The CogxelConverter for the estimate element of the pair.
     * @param semanticIdentifierMap 
     *      The SemanticIdentifierMap for the converter.
     */
    public CogxelTargetEstimatePairConverter(
        final CogxelConverter<TargetType> targetConverter,
        final CogxelConverter<EstimateType> estimateConverter,
        final SemanticIdentifierMap semanticIdentifierMap)
    {
        super(targetConverter, estimateConverter, semanticIdentifierMap);
    }

    @Override
    public TargetEstimatePair<TargetType, EstimateType> createPair(
        final TargetType first,
        final EstimateType second)
    {
        return DefaultTargetEstimatePair.create(first, second);
    }

    @Override
    public boolean equals(
        final Object other)
    {
        return other instanceof CogxelTargetEstimatePairConverter
            && super.equals(other);
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    /**
     * Gets the converter for the target value.
     *
     * @return
     *      The target converter.
     */
    public CogxelConverter<TargetType> getTargetConverter()
    {
        return this.getFirstConverter();
    }

    /**
     * Sets the converter for the target value.
     *
     * @param   targetConverter
     *      The target converter.
     */
    public void setTargetConverter(
        final CogxelConverter<TargetType> targetConverter)
    {
        this.setFirstConverter(targetConverter);
    }

    /**
     * Gets the converter for the estimate value.
     *
     * @return
     *      The estimate converter.
     */
    public CogxelConverter<EstimateType> getEstimateConverter()
    {
        return this.getSecondConverter();
    }

    /**
     * Sets the converter for the estimate value.
     *
     * @param   estimateConverter
     *      The estimate converter.
     */
    public void setEstimateConverter(
        final CogxelConverter<EstimateType> estimateConverter)
    {
        this.setSecondConverter(estimateConverter);
    }

}
