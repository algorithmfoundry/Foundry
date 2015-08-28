/*
 * File:                AbstractRandomized.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 26, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.util;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import java.util.Random;

/**
 * The {@code AbstractRandomized} abstract class implements the 
 * {@code Randomized} interface by containing the random object in a protected
 * field.
 *
 * @author Justin Basilico
 * @since  2.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-10-02",
            changesNeeded=false,
            comments={
                "Cleaned up javadoc.",
                "Moved previous code review to CodeReview annotation.",
                "Otherwise, looks fine."
            }
        )
        ,
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2007-11-25",
            changesNeeded=false,
            comments="Looks fine."
        )
    }
)
public abstract class AbstractRandomized
    extends AbstractCloneableSerializable
    implements Randomized
{

    /** The random number generator for this object to use. */
    protected Random random;

    // Note: This class does not have a default constructor to make sure that
    // a proper Random object is given to it.
    
    /**
     * Creates a new instance of AbstractRandomized with the given Random 
     * object.
     *
     * @param  random The random number generator to use.
     */
    public AbstractRandomized(
        final Random random)
    {
        super();

        this.setRandom(random);
    }

    @Override
    public AbstractRandomized clone()
    {
        final AbstractRandomized result = (AbstractRandomized) super.clone();
        result.setRandom( ObjectUtil.deepCopy(this.getRandom()) );
        return result;
    }

    @Override
    public Random getRandom()
    {
        return this.random;
    }

    @Override
    public void setRandom(
        final Random random)
    {
        this.random = random;
    }

}
