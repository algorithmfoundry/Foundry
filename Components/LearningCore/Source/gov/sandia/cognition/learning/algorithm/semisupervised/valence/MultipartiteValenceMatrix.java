
package gov.sandia.cognition.learning.algorithm.semisupervised.valence;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.minimization.matrix.MatrixVectorMultiplier;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.custom.DenseMatrix;
import gov.sandia.cognition.math.matrix.custom.DenseVector;
import gov.sandia.cognition.math.matrix.custom.DiagonalMatrix;
import gov.sandia.cognition.math.matrix.custom.ParallelSparseMatrix;
import gov.sandia.cognition.math.matrix.custom.SparseMatrix;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * This class implements a semi-supervised learning algorithm for spreading
 * "valence" across a multi-partite graph. First some definitions: Valence is
 * any spectrum with two distinguishable sides; a multipartite graph is a graph
 * that can be separated into different groups where each group can link to any
 * node in other groups, but does not link to any nodes within his own group.
 *
 * The simplest use for this algorithm is the one it was originally proposed
 * for: Semi-supervised document and term tagging for sentiment analysis. Given
 * a set of n documents, create a graph based on the document/term matrix. There
 * are n document nodes that refer to a term node for each term in that
 * document. Thus each document will point to many term nodes and most term
 * nodes will be referred to by several document nodes. If you then supply a
 * small set of initial weights (+1/-1) for any "happy" or "sad" terms and/or
 * documents and let this algorithm run, the influence of the marked terms will
 * spread to their neighboring documents, the influence of marked documents will
 * spread to their neighboring terms. The power passed into the constructor
 * correlates to the distance that influence spreads.
 *
 * However, this implementation is more generic than that description.
 * Specifically, it supports any multi-partition graph with any set of nodes
 * labeled and then spreading to the rest. In fact, if you want it to be any
 * generic graph, this can be done by creating a graph with as many partitions
 * as there are nodes (yes, a bit clumsy). This last case has not been tested
 * explicitly, but we have examined the math this is based on and have not found
 * any assumptions requiring proper partitions.
 *
 * This code has been tested on many different scale problems including 100s of
 * documents with 10,000s terms (seconds to run) up to a low-connectivity
 * 5-partition graph of ~500,000,000 nodes (1.2 average degree; ~6 days to run).
 *
 * @author jdwendt
 */
@PublicationReferences(
    references =
{
    @PublicationReference(
        author =
    {
        "Richard Colbaugh",
        "Kristin Glass"
    },
        title =
        "Agile Sentiment Analysis of Social Media Content for Security Informatics Applications",
        type = PublicationType.Conference,
        year = 2011,
        publication = "Intelligence and Security Informatics Conference (EISIC)",
        pages =
    {
        327, 331
    },
        url = "http://www.cs.princeton.edu/introcs/97data/FFT.java.html"),
    @PublicationReference(
        author =
    {
        "Kristin Glass",
        "Richard Colbaugh"
    },
        title =
        "Estimating the Sentiment of Social Media Content for Security Informatics Applications",
        type = PublicationType.Conference,
        year = 2011,
        publication = "Intelligence and Security Informatics (ISI)",
        pages =
    {
        65, 70
    },
        url = "http://www.cs.princeton.edu/introcs/97data/FFT.java.html")
})
public class MultipartiteValenceMatrix
    extends MatrixVectorMultiplier
    implements Serializable
{

    /**
     * The default number of threads used when solving. As the matrix size grows
     * bigger, more threads are far more useful.
     */
    private static final int NUM_THREADS = 4;

    /**
     * The default trust for non-scored items. This exists solely to keep scores
     * from wandering from zero for no apparent reason.
     */
    private static final double DEFAULT_TRUST = 0.001;

    /**
     * The start position of each partition in the graph. If there are n
     * partitions, this has length n+1 so that any single partition's size can
     * be calculated as eachPartsStart[i+1]-eachPartsStart[i].
     */
    private int[] eachPartsStart;

    /**
     * The power to raise the matrix to (for spreading of influence across
     * neighbors)
     */
    private int power;

    /**
     * The partial-solution multipartite adjacency matrix (contains all of the
     * weighted links between partitions in a symmetric matrix).
     */
    private SparseMatrix multipartiteAdjacency;
    // NOTE: the parent's "m" matrix stores the L_tilde matrix from the paper 
    // that is applied at each iteration of the solver.

    /**
     * The down-the-diagonal weights for solving the equation. This stores the
     * Beta*I values from equation 5 of "Agile Sentiment..."
     */
    private DiagonalMatrix additional;

    /**
     * The right-hand-side vector (see equation 5)
     */
    private Vector rhs;

    /**
     * True if createNormalizedLaplacian has been called more recently than
     * addRelationship, setElementsScore, or the constructor.
     */
    private boolean isInitialized;

    /**
     * The number of threads that this instance will use when performing
     * matrix-vector multiplies.
     */
    private int numThreads;

    /**
     * NEVER call this from real code. Java's serialization code will need to
     * call this, but if all of the values aren't immediately filled with useful
     * values through introspection, the instance created with this method won't
     * provide useful values.
     */
    protected MultipartiteValenceMatrix()
    {
        // NOTE: I have to pass something, but it's going to be overwritten immediately
        super(new SparseMatrix(1, 1));
    }

    /**
     * Initializees this with the expected size of each partition, and the power
     * to raise the L_tilde matrix to. NOTE: This is not ready to solve
     * anything, as there are no trusted/weighted elements and no relationships
     * between the elements yet. Both addRelationship and setElementsScore
     * should be called many times.
     *
     * @param eachPartsSize The ordered list of the sizes of each partition.
     * This ordering needs to be maintained for the group IDs passed in to
     * addRelationship and setElementsScore.
     * @param power The power to raise L_tilde to for the matrix solution -- the
     * higher the power, the farther the spread of the trusted/weighted
     * elements. However, the farther the spread, the slower the solver and the
     * more homogenous results.
     */
    public MultipartiteValenceMatrix(List<Integer> eachPartsSize,
        int power)
    {
        this(eachPartsSize, power, NUM_THREADS);
    }

    /**
     * Initializes this with the expected size of each partition, and the power
     * to raise the L_tilde matrix to. NOTE: This is not ready to solve
     * anything, as there are no trusted/weighted elements and no relationships
     * between the elements yet. Both addRelationship and setElementsScore
     * should be called many times.
     *
     * @param eachPartsSize The ordered list of the sizes of each partition.
     * This ordering needs be maintained for the group IDs passed in to
     * addRelationship and setElementsScore.
     * @param power The power to raise L_tilde to for the matrix solution -- the
     * higher the power, the farther the spread of the trusted/weighted
     * elements. However, the farther the spread, the slower the solver and the
     * more homogenous results.
     * @param numThreads The number of threads that should be used for
     * multithreading the SparseMatrix/Vector multiplies in the minimization
     * step. We've found that 2 threads is good for smallish sizes (10,000
     * elements), 4 is good for medium (100,000-1,000,000), 8 is good for large
     * (>10,000,000).
     */
    public MultipartiteValenceMatrix(List<Integer> eachPartsSize,
        int power,
        int numThreads)
    {
        super(new SparseMatrix(1, 1));

        // Some basic set-up
        this.isInitialized = false;
        this.power = power;
        this.numThreads = numThreads;

        // Initialize the start positions for each part of the graph
        int n = eachPartsSize.size();
        this.eachPartsStart = new int[n + 1];
        int size = 0;
        for (int i = 0; i < n; ++i)
        {
            this.eachPartsStart[i] = size;
            size += eachPartsSize.get(i);
        }
        this.eachPartsStart[n] = size;

        // Initialize the empty matrices and vector
        this.multipartiteAdjacency = new SparseMatrix(size, size);
        this.additional = new DiagonalMatrix(size);
        for (int i = 0; i < size; ++i)
        {
            this.additional.setElement(i, i, DEFAULT_TRUST);
        }
        this.rhs = new DenseVector(size);
    }

    /**
     * Checks that the input node from the multi-partite graph is within proper
     * bounds.
     *
     * @param group The group id [0..eachPartsStart.length-1) to check.
     * @param index The within-group index [0..group.size) to check.
     */
    private void checkNode(int group,
        int index)
    {
        if ((group < 0) || (group > (eachPartsStart.length - 1)))
        {
            throw new IllegalArgumentException("Input group (" + group
                + ") outside allowed bounds.");
        }
        if ((index < 0) || (index > (eachPartsStart[group + 1]
            - eachPartsStart[group])))
        {
            throw new IllegalArgumentException("Input within group index ("
                + index + ") outside allowed bounds.");
        }
    }

    /**
     * Adds a relationship between the two indexed elements. They must be in
     * different groups and within the acceptable ranges of their own groups. A
     * symmetric relationship is assumed
     *
     * @param fromGroup The group id of the "from" element
     * @param fromIndex The within-group id of the "from" element
     * @param toGroup The group id of the "to" element
     * @param toIndex The within-group id of the "to" element
     * @param weight The weight of association between the two elements (zero
     * indicates no relationship). This value replaces any value previously
     * entered between these two elements.
     */
    public void addRelationship(int fromGroup,
        int fromIndex,
        int toGroup,
        int toIndex,
        double weight)
    {
        isInitialized = false;

        // First, make sure they are both within the space
        checkNode(fromGroup, fromIndex);
        checkNode(toGroup, toIndex);
        if (fromGroup == toGroup)
        {
            throw new IllegalArgumentException(
                "In a multipartite graph, nodes within the same "
                + "group can't refer directly to each other.");
        }

        // Second, put the relationship in the multipartiteAdjacency matrix
        int i = eachPartsStart[fromGroup] + fromIndex;
        int j = eachPartsStart[toGroup] + toIndex;
        // Both directions
        multipartiteAdjacency.setElement(i, j, weight);
        multipartiteAdjacency.setElement(j, i, weight);
    }

    /**
     * Sets elements of the group with their score (+1/-1 or similar) and how
     * much to trust that weight.
     *
     * @param group The element's group id
     * @param index The element's within-group id
     * @param trust The amount the score should be trusted
     * @param score The score assigned to the element
     */
    public void setElementsScore(int group,
        int index,
        double trust,
        double score)
    {
        isInitialized = false;

        // Make sure its in within space
        checkNode(group, index);

        // Add to solution and diagonal
        int i = eachPartsStart[group] + index;
        rhs.setElement(i, trust * score);
        additional.setElement(i, i, trust);
    }

    /**
     * Creates the L_tilde (normalized laplacian) matrix for the multi-partite
     * graph. This must be called (via init) after all relationships and
     * weighted elements are added and before this can be passed to a solver.
     */
    private void createNormalizedLaplacian()
    {
        // NOTE: This method uses a trick.  The paper specifies the following:
        // D = diag(rowSum(A))
        // L = D - A
        // L~ = D^(-1/2)LD^(-1/2)
        // 
        // This code does the following:
        // Since L = D - A...
        // L~ = D^(-1/2)(D - A)D^(-1/2) (now distribute)
        // L~ = D^(-1/2)DD^(-1/2) - D^(-1/2)AD^(-1/2)
        // L~ = I - D^(-1/2)AD^(-1/2)
        DiagonalMatrix diag = new DiagonalMatrix(
            multipartiteAdjacency.getNumRows());

        // calculate diagonals = 1/sqrt(rowsum) ... (see paper)
        double rowSum;
        Vector rowSums = multipartiteAdjacency.sumOfColumns();
        for (int i = 0; i < rowSums.getDimensionality(); i++)
        {
            rowSum = rowSums.getElement(i);
            if (rowSum > 0)
            {
                rowSum = 1.0 / Math.sqrt(rowSum);
                diag.setElement(i, i, rowSum);
            }
        }

        // D*A*D, used for the normalized Laplacian
        Matrix tmp = diag.times(multipartiteAdjacency);
        Matrix DAD = tmp.times(diag);
        Matrix minusDAD = DAD.scale(-1);

        // L~ = I - D*A*D
        DiagonalMatrix I =
            new DiagonalMatrix(multipartiteAdjacency.getNumRows());
        I.identity();
        Matrix l_tilde = I.plus(minusDAD);
        if (l_tilde instanceof SparseMatrix)
        {
            m = new ParallelSparseMatrix((SparseMatrix) l_tilde, numThreads);
        }
        else if (l_tilde instanceof DenseMatrix)
        {
            m = new ParallelSparseMatrix((DenseMatrix) l_tilde, numThreads);
        }
        else if (l_tilde instanceof DiagonalMatrix)
        {
            m = new ParallelSparseMatrix((DiagonalMatrix) l_tilde, numThreads);
        }
        else if (l_tilde instanceof ParallelSparseMatrix)
        {
            m = new ParallelSparseMatrix((ParallelSparseMatrix) l_tilde,
                numThreads);
        }
        else
        {
            throw new RuntimeException("Received a matrix of unexpected type: "
                + l_tilde.getClass().getCanonicalName());
        }
        isInitialized = true;
    }

    /**
     * This method must be called before an instance is passed to an iterative
     * solver and after all relationships and trusted/weighted elements are
     * added. It completes the matrix representation necessary for the iterative
     * solver. Upon completion of the initialization, the matrix and
     * right-hand-side vector are ready to use. The RHS vector is returned.
     *
     * @return The RHS vector for the system of equations (to pass into an
     * iterative solver).
     */
    public Vector init()
    {
        if (!isInitialized)
        {
            createNormalizedLaplacian();
        }

        return rhs;
    }

    /**
     * Overrides the default implementation so that L_tilde can be raised to a
     * power and the diagonal weights can be added implicitly (which is much
     * faster and memory efficient than the explicit representation).
     *
     * @param input The vector to multiply by the implicit represetation of the
     * matrix
     * @return The result of the function.
     */
    @Override
    public Vector evaluate(Vector input)
    {
        Vector v = input;
        for (int i = 0; i < power; ++i)
        {
            v = m.times(v);
        }
        Vector plusV = additional.times(input);

        return v.plus(plusV);
    }

    /**
     * Called by serialization (through the magic of Java reflection) and
     * shouldn't be called by anyone else.
     *
     * @param oos The stream to write this to
     * @throws IOException If there's a problem
     */
    private void writeObject(ObjectOutputStream oos)
        throws IOException
    {
        oos.writeBoolean(isInitialized);
        oos.writeInt(power);
        oos.writeInt(numThreads);
        oos.writeInt(eachPartsStart.length);
        for (int i = 0; i < eachPartsStart.length; i++)
        {
            oos.writeInt(eachPartsStart[i]);
        }
        oos.writeObject(multipartiteAdjacency);
        oos.writeObject(additional);
        oos.writeObject(rhs);
    }

    /**
     * Called by de-serialization (through the magic of Java reflect) and
     * shouldn't be called by anyone else.
     *
     * @param ois The stream to read this from
     * @throws IOException If there's a problem
     * @throws ClassNotFoundException If there's a problem
     */
    private void readObject(ObjectInputStream ois)
        throws IOException, ClassNotFoundException
    {
        isInitialized = ois.readBoolean();
        power = ois.readInt();
        numThreads = ois.readInt();
        int n = ois.readInt();
        eachPartsStart = new int[n];
        for (int i = 0; i < eachPartsStart.length; i++)
        {
            eachPartsStart[i] = ois.readInt();
        }
        multipartiteAdjacency = (SparseMatrix) ois.readObject();
        additional = (DiagonalMatrix) ois.readObject();
        rhs = (DenseVector) ois.readObject();
    }

}
