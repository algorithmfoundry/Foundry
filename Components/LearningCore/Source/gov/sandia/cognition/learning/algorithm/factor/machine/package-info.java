/*
 * File:            package-info.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2014 Cognitive Foundry. All rights reserved.
 */

/**
 * Provides factorization machine algorithms. Factorization machines are a 
 * combination of linear and factorized (reduced-dimensionality) pair-wise 
 * interactions between variables. As such, they are a combination of methods
 * typically used for machine learning (linear methods, support vector machines)
 * with those also used for recommendation systems (matrix factorization). The
 * typical use is with sparse input vectors to turn a matrix factorization
 * problem into a standard machine learning problem with an input vector. They
 * can also be extended to higher-order interactions besides pairwise, but
 * those are the most common.
 *
 * @author  Justin Basilico
 * @since   3.4.0
 * @see     gov.sandia.cognition.learning.algorithm.factor.machine.FactorizationMachine
 */
@PublicationReference(
    title="Factorization Machines",
    author={"Steffen Rendle"},
    year=2010,
    type=PublicationType.Conference,
    publication="Proceedings of the 10th IEEE International Conference on Data Mining (ICDM)",
    url="http://www.inf.uni-konstanz.de/~rendle/pdf/Rendle2010FM.pdf")
@gov.sandia.cognition.annotation.Documentation
package gov.sandia.cognition.learning.algorithm.factor.machine;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;

