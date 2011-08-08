/*
 * File:                Semimetric.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 26, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;

/**
 * A semimetric is a divergence function that takes inputs from the same
 * set (domain) and is positive definite and symmetric.  That is, a semimetric
 * is almost a metric but does not obey the Triangle Inequality.
 * @param <InputType> Type of input to the semimetric.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Wikipedia",
            title="Semimetrics",
            type=PublicationType.WebPage,
            year=2009,
            url="http://en.wikipedia.org/wiki/Metric_(mathematics)#Semimetrics"
        )
        ,
        @PublicationReference(
            author={
                "PlantMath",
                "Koro"
            },
            title="semimetric",
            type=PublicationType.WebPage,
            year=2004,
            url="http://planetmath.org/encyclopedia/Semimetric.html"
        )
    }
)
public interface Semimetric<InputType>
    extends DivergenceFunction<InputType,InputType>
{    
}
