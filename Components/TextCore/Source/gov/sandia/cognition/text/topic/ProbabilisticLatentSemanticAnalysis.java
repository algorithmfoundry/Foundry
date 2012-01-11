/*
 * File:                ProbabilisticLatentSemanticAnalysis.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 18, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.topic;

import gov.sandia.cognition.algorithm.event.AbstractIterativeAlgorithmListener;
import gov.sandia.cognition.algorithm.IterativeAlgorithm;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeBatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorFactoryContainer;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.math.matrix.VectorOutputEvaluator;
import gov.sandia.cognition.math.matrix.VectorUtil;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.Randomized;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Random;

/**
 * An implementation of the Probabilistic Latent Semantic Analysis (PLSA)
 * algorithm.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Thomas Hofmann",
            title="Probabilistic Latent Semantic Analysis",
            year=1999,
            type=PublicationType.Conference,
            publication="Proceedings of the Fifteenth Conference on Uncertainty in Artificial Intelligence (UAI)",
            pages={289, 296},
            url="http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.33.1187"
        ),
        @PublicationReference(
            author="Thomas Hofmann",
            title="Probabilistic Latent Semantic Indexing",
            year=1999,
            type=PublicationType.Conference,
            publication="Proceedings of the 22nd Conference of the ACM Special Interest Group on Information Retreival (SIGIR)",
            pages={50, 57},
            url="http://portal.acm.org/citation.cfm?id=312649"
        ),
        @PublicationReference(
            author="Thomas Hofmann",
            title="Unsupervised Learning by Probabilistic Latent Semantic Analysis",
            year=2001,
            type=PublicationType.Journal,
            publication="Machine Learning",
            pages={177, 196},
            url="http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.130.6341"
        )
    }
)
public class ProbabilisticLatentSemanticAnalysis
    extends AbstractAnytimeBatchLearner<Collection<? extends Vectorizable>, ProbabilisticLatentSemanticAnalysis.Result>
    implements Randomized, VectorFactoryContainer
{
    // TODO: Make use of sparseness.

    /** The default requested rank is {@value}. */
    public static final int DEFAULT_REQUESTED_RANK = 10;

    /** The default maximum number of iterations is {@value}. */
    public static final int DEFAULT_MAX_ITERATIONS = 250;

    /** The default minimum change is {@value}. */
    public static final double DEFAULT_MINIMUM_CHANGE = 1e-10;

    /** The requested rank to reduce the dimensionality to. */
    protected int requestedRank;

    /** The minimum change required in log-likelihood to continue iterating.
     *  Used for a stopping criteria. */
    protected double minimumChange;

    /** The random number generator to use. */
    protected Random random;

    /** The vector factory. */
    protected VectorFactory<? extends Vector> vectorFactory;

    /** The matrix factory. */
    protected MatrixFactory<? extends Matrix> matrixFactory;

    /** The document-by-term matrix. */
    protected transient Matrix documentsByTerms;

    /** The number of terms. */
    protected transient int termCount;

    /** The number of documents. */
    protected transient int documentCount;

    /** The number of latent variables. */
    protected transient int latentCount;

    /** The information about each of the latent variables. */
    protected transient LatentData[] latents;

    /** The current log-likelihood of the algorithm. */
    protected transient double logLikelihood;

    /** The change in log-likelihood of the algorithm from the current
     *  iteration. */
    protected transient double changeOfLogLikelihood;

    /** The result being produced by the algorithm. */
    protected transient Result result;

    /**
     * Creates a new ProbabilisticSemanticAnalysis with default parameters.
     */
    public ProbabilisticLatentSemanticAnalysis()
    {
        this(DEFAULT_REQUESTED_RANK);
    }

    /**
     * Creates a new ProbabilisticLatentSemanticAnalysis with default parameters
     * and the given random number generator.
     *
     * @param   random
     *      The random number generator to use.
     */
    public ProbabilisticLatentSemanticAnalysis(
        final Random random)
    {
        this(DEFAULT_REQUESTED_RANK, DEFAULT_MINIMUM_CHANGE, random);
    }

    /**
     * Creates a new ProbabilisticLatentSemanticAnalysis with the given rank
     * and otherwise default parameters.
     *
     * @param   requestedRank
     *      The requested rank. Must be non-negative.
     */
    public ProbabilisticLatentSemanticAnalysis(
        final int requestedRank)
    {
        this(requestedRank, DEFAULT_MINIMUM_CHANGE, new Random());
    }

    /**
     * Creates a new ProbabilisticLatentSemanticAnalysis with the given
     * parameters.
     *
     * @param   requestedRank
     *      The requested rank. Must be non-negative.
     * @param   minimumChange
     *      The minimum change in log-likelihood to stop.
     * @param   random
     *      The random number generator to use.
     */
    public ProbabilisticLatentSemanticAnalysis(
        final int requestedRank,
        final double minimumChange,
        final Random random)
    {
        super(DEFAULT_MAX_ITERATIONS);

        this.setRequestedRank(requestedRank);
        this.setRandom(random);
        this.setMinimumChange(minimumChange);
        this.setVectorFactory(VectorFactory.getDefault());
        this.setMatrixFactory(MatrixFactory.getDefault());
    }

    @Override
    protected boolean initializeAlgorithm()
    {
        final Collection<? extends Vectorizable> documents = this.getData();
        this.documentsByTerms = this.getMatrixFactory().copyRowVectors(documents);
        this.termCount = DatasetUtil.getDimensionality(documents);
        this.documentCount = documents.size();
        this.latentCount = Math.min(this.documentCount, this.getRequestedRank());

        // Set up the latent variable information.
        this.latents = new LatentData[this.latentCount];
        for (int i = 0; i < this.latentCount; i++)
        {
            final LatentData latent = new LatentData();
            this.latents[i] = latent;
            latent.index = i;
            
            // Initialize the latent data.
            latent.pLatentGivenDocumentTerm =
                this.getMatrixFactory().createMatrix(
                    this.documentCount, this.termCount);
            
            latent.pTermGivenLatent = this.getVectorFactory().createUniformRandom(
                this.termCount, 0.0, 1.0, this.getRandom());
            VectorUtil.divideByNorm1Equals(latent.pTermGivenLatent);

            latent.pDocumentGivenLatent = this.getVectorFactory().createUniformRandom(
                this.documentCount, 0.0, 1.0, this.getRandom());
            VectorUtil.divideByNorm1Equals(latent.pDocumentGivenLatent);
            
            
            // Uniform prior on latent classes.
            latent.pLatent = 1.0 / latentCount;
        }

        this.logLikelihood = Double.MIN_VALUE;
        this.changeOfLogLikelihood = 0.0;
        this.result = new Result(this.termCount, this.latents);
        return true;
    }

    @Override
    protected boolean step()
    {
        // E-step:
        for (int i = 0; i < this.documentCount; i++)
        {
            for (int j = 0; j < this.termCount; j++)
            {
                double sum = 0.0;
                for (LatentData latent : this.latents)
                {
                    final double value =
                          latent.pLatent
                        * latent.pDocumentGivenLatent.getElement(i)
                        * latent.pTermGivenLatent.getElement(j);

                    latent.pLatentGivenDocumentTerm.setElement(i, j, value);
                    sum += value;
                }

                if (sum != 0.0)
                {
                    for (LatentData latent : this.latents)
                    {
                        double value =
                            latent.pLatentGivenDocumentTerm.getElement(i, j);
                        value /= sum;
                        latent.pLatentGivenDocumentTerm.setElement(i, j, value);
                    }
                }
            }
        }

        // M-step:
        double pLatentSum = 0.0;
        for (LatentData latent : this.latents)
        {
            // This matrix forms the basis for the m-step. It multiplies the
            // count of occurrences of the term in a document by the probability
            // of the aspect given the document and term. The probabilities for
            // documents and terms
            final Matrix countsTimesProbabilities =
                this.documentsByTerms.dotTimes(latent.pLatentGivenDocumentTerm);

            // To get the term probabilities, we sum over documents.
            latent.pTermGivenLatent = countsTimesProbabilities.sumOfRows();

            // To get the document probabilities, we sum over terms.
            latent.pDocumentGivenLatent = countsTimesProbabilities.sumOfColumns();

            // To get the aspect probability, we sum across the whole matrix,
            // which is the same as summing across one of the summed rows or
            // columns. Here we pick documents because normally the number of
            // words is larger than the number of documents.
            latent.pLatent = latent.pDocumentGivenLatent.sum();

            // Make the vectors into probabilities by making them sum to one.
            VectorUtil.divideByNorm1Equals(latent.pTermGivenLatent);
            VectorUtil.divideByNorm1Equals(latent.pDocumentGivenLatent);

            // We use the aspect sum to normalize the aspect probabilities to
            // sum to one.
            pLatentSum += latent.pLatent;
        }

        // Normalize the probabilities of the latent classes so that they sum
        // to one.
        if (pLatentSum != 0.0)
        {
            for (LatentData latent : this.latents)
            {
                latent.pLatent /= pLatentSum;
            }
        }

        // Compute the log-likelihood
        final double previousLogLikelihood = this.logLikelihood;
        this.logLikelihood = 0.0;
        for (int i = 0; i < this.documentCount; i++)
        {
            for (int j = 0; j < this.termCount; j++)
            {
                double pDocumentTerm = 0.0;

                for (LatentData latent : this.latents)
                {
                    pDocumentTerm +=
                          latent.pLatent
                        * latent.pDocumentGivenLatent.getElement(i)
                        * latent.pTermGivenLatent.getElement(j);
                }

                if (pDocumentTerm != 0.0)
                {
                    this.logLikelihood += this.documentsByTerms.getElement(i, j)
                        * Math.log(pDocumentTerm);
                }
            }
        }

        // The stopping criteria is based on the change in log-likelihood.
        final double change = this.logLikelihood - previousLogLikelihood;
        this.changeOfLogLikelihood = change;
        return Math.abs(change) > this.getMinimumChange();
    }

    @Override
    protected void cleanupAlgorithm()
    {
        this.latents = null;
        this.documentsByTerms = null;
    }

    public Result getResult()
    {
        return this.result;
    }

    public Random getRandom()
    {
        return this.random;
    }

    public void setRandom(
        final Random random)
    {
        this.random = random;
    }

    /**
     * Gets the vector factory to use.
     *
     * @return
     *      The vector factory to use.
     */
    public VectorFactory<? extends Vector> getVectorFactory()
    {
        return this.vectorFactory;
    }

    /**
     * Sets the vector factory to use.
     *
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public void setVectorFactory(
        final VectorFactory<? extends Vector> vectorFactory)
    {
        this.vectorFactory = vectorFactory;
    }

    /**
     * Gets the matrix factory to use.
     *
     * @return
     *      The matrix factory to use.
     */
    public MatrixFactory<? extends Matrix> getMatrixFactory()
    {
        return this.matrixFactory;
    }

    /**
     * Sets the matrix factory to use.
     *
     * @param   matrixFactory
     *      The matrix factory to use.
     */
    public void setMatrixFactory(
        final MatrixFactory<? extends Matrix> matrixFactory)
    {
        this.matrixFactory = matrixFactory;
    }

    /**
     * Gets the requested rank to conduct the analysis for. It is the number
     * of latent variables to use.
     *
     * @return 
     *      The requested rank. Must be positive.
     */
    public int getRequestedRank()
    {
        return this.requestedRank;
    }

    /**
     * Sets the requested rank to conduct the analysis for. It is the number
     * of latent variables to use.
     *
     * @param   requestedRank
     *      The requested rank. Must be positive.
     */
    public void setRequestedRank(
        final int requestedRank)
    {
        ArgumentChecker.assertIsPositive("requestedRank", requestedRank);
        this.requestedRank = requestedRank;
    }

    /**
     * Gets the minimum change in log-likelihood to allow before stopping the
     * algorithm.
     *
     * @return
     *      The minimum change in log-likelihood to allow before stopping.
     *      Must be non-negative.
     */
    public double getMinimumChange()
    {
        return this.minimumChange;
    }

    /**
     * Sets the minimum change in log-likelihood to allow before stopping the
     * algorithm.
     *
     * @param   minimumChange
     *      The minimum change in log-likelihood to allow before stopping.
     *      Must be non-negative.
     */
    public void setMinimumChange(
        final double minimumChange)
    {
        ArgumentChecker.assertIsNonNegative("minimumChange", minimumChange);
        this.minimumChange = minimumChange;
    }

    /**
     * The information about each latent variable.
     */
    public static class LatentData
    {
        /** The index of the latent variable. */
        int index;

        /** The probability of the latent variable given the document and term.
         */
        Matrix pLatentGivenDocumentTerm;

        /** The probability of each term given the latent variable. */
        Vector pTermGivenLatent;

        /** The probability of each vector given the latent variable. */
        Vector pDocumentGivenLatent;

        /** The probability of the latent variable. */
        double pLatent;
    }

    /**
     * The dimensionality transform created by probabilistic latent semantic
     * analysis.
     */
    public static class Result
        extends AbstractCloneableSerializable
        implements Evaluator<Vectorizable, Vector>,
            VectorInputEvaluator<Vectorizable, Vector>,
            VectorOutputEvaluator<Vectorizable, Vector>
    {

        /** The number of terms. */
        protected int termCount;

        /** The number of latent variables. */
        protected int latentCount;

        /** The latent variable data. */
        protected LatentData[] latents;

        /** The maximum number of iterations for the E-M evaluation. */
        protected int maxIterations;

        /** The minimum change in log-likelihood for the E-M evaluation. */
        protected double minimumChange;

        /**
         * Creates a new probabilistic latent semantic analysis transform.
         *
         * @param   termCount
         *      The number of terms.
         * @param   latents
         *      The latent variable data.
         */
        public Result(
            final int termCount,
            final LatentData[] latents)
        {
            super();

            this.termCount = termCount;
            this.latentCount = latents.length;
            this.latents = latents;
            this.maxIterations = DEFAULT_MAX_ITERATIONS;
            this.minimumChange = DEFAULT_MINIMUM_CHANGE;
        }

        public Vector evaluate(
            final Vectorizable input)
        {
            final Vector query = input.convertToVector();

            final Matrix pLatentGivenQueryTerm =
                MatrixFactory.getDefault().createMatrix(
                    this.latentCount, this.termCount);
            final Vector pQueryGivenLatent =
                VectorFactory.getDefault().createVector(
                    this.latentCount,
                    1.0 / this.latentCount);

            double logLikelihood = Double.MIN_VALUE;
            for (int iteration = 1; iteration <= this.maxIterations; iteration++)
            {
                final double previousLogLikelihood = logLikelihood;
                logLikelihood = this.step(
                    query, pLatentGivenQueryTerm, pQueryGivenLatent);
                final double change = logLikelihood - previousLogLikelihood;

                if (Math.abs(change) <= this.minimumChange)
                {
                    break;
                }
            }
            
            return pQueryGivenLatent;
        }

        /**
         * Take a step of the expectation-maximization algorithm for computing
         * the probability of the query given each latent variable.
         *
         * @param   query
         *      The query to evaluate.
         * @param   pLatentGivenQueryTerm
         *      The probability of each latent variable given each term in the
         *      query.
         * @param   pQueryGivenLatent
         *      The probability of the query given each latent variable.
         * @return
         *      The log-likelihood of the query.
         */
        protected double step(
            final Vector query,
            final Matrix pLatentGivenQueryTerm,
            final Vector pQueryGivenLatent)
        {
            // E-step:
            // Compute p(z|q, w). We loop over terms because for each term
            // we normalize by latent values.
            for (int j = 0; j < this.termCount; j++)
            {
                double sum = 0.0;
                for (LatentData latent : this.latents)
                {
                    final int k = latent.index;
                    final double value =
                          latent.pLatent
                        * pQueryGivenLatent.getElement(k)
                        * latent.pTermGivenLatent.getElement(j);
                    pLatentGivenQueryTerm.setElement(k, j, value);
                    sum += value;
                }

                if (sum != 0.0)
                {
                    for (LatentData latent : this.latents)
                    {
                        final int i = latent.index;
                        double value = pLatentGivenQueryTerm.getElement(i, j);
                        value /= sum;
                        pLatentGivenQueryTerm.setElement(i, j, value);
                    }
                }
            }

            // M-step:
            // We only modify the p(q|z) since we hold the rest fixed from
            // the
            for (LatentData latent : this.latents)
            {
                final int k = latent.index;
                double sum = 0.0;
                for (int j = 0; j < this.termCount; j++)
                {
                    final double value = query.getElement(j)
                        * pLatentGivenQueryTerm.getElement(k, j);
                    sum += value;
                }
                pQueryGivenLatent.setElement(k, sum);
            }

            // Normalize.
            VectorUtil.divideByNorm1Equals(pQueryGivenLatent);

            double logLikelihood = 0.0;
            for (int j = 0; j < this.termCount; j++)
            {
                double pQueryTerm = 0.0;
                for (LatentData latent : latents)
                {
                    final int k = latent.index;
                    pQueryTerm +=
                          latent.pLatent
                        * latent.pTermGivenLatent.getElement(j)
                        * pQueryGivenLatent.getElement(k);
                }

                if (pQueryTerm != 0.0)
                {
                    logLikelihood += query.getElement(j)
                        * Math.log(pQueryTerm);
                }
            }

            return logLikelihood;
        }

        public int getInputDimensionality()
        {
            return this.termCount;
        }

        public int getOutputDimensionality()
        {
            return this.latents.length;
        }

    }

    /**
     * Prints out the status of the probabilistic latent semantic analysis
     * algorithm. Can be useful for debugging and other purposes.
     */
    public static class StatusPrinter
        extends AbstractIterativeAlgorithmListener
    {
        /** The stream to write the status to. */
        protected PrintStream out;

        /**
         * Creates a new {@code StatusPrinter} writing to {@code System.out}.
         */
        public StatusPrinter()
        {
            this(System.out);
        }

        /**
         * Creates a new {@code StatusPrinter} writing to the given stream.
         *
         * @param   out
         *      The print stream to write status to.
         */
        public StatusPrinter(
            final PrintStream out)
        {
            super();

            this.out = out;
        }

        public void stepStarted(
            final IterativeAlgorithm algorithm)
        {
            final ProbabilisticLatentSemanticAnalysis plsa =
                (ProbabilisticLatentSemanticAnalysis) algorithm;

            NumberFormat format = new DecimalFormat("0.00");
            out.println("Iteration " + plsa.getIteration());
            for (ProbabilisticLatentSemanticAnalysis.LatentData latent
                : plsa.result.latents)
            {
                out.println("    Latent " + latent.index);
                out.println("        p(z)   = " + format.format(latent.pLatent));
                out.println("        p(t|z) = " + latent.pTermGivenLatent.toString(format));
                out.println("        p(d|z) = " + latent.pDocumentGivenLatent.toString(format));
            }
        }

        public void stepEnded(
            final IterativeAlgorithm algorithm)
        {
            final ProbabilisticLatentSemanticAnalysis plsa =
                (ProbabilisticLatentSemanticAnalysis) algorithm;
            
            out.println("Log likelihood: " + plsa.logLikelihood);
            out.println("Change: " + plsa.changeOfLogLikelihood);
        }

    }
}
