package gov.sandia.cognition.text.topic;

import java.util.Random;

/**
 * @deprecated 
 * Deprecated class to fix a spelling error in original version
 * @author jfsheph
 */
@Deprecated
public class LatentDirichetAllocationVectorGibbsSampler extends LatentDirichletAllocationVectorGibbsSampler
{
    public LatentDirichetAllocationVectorGibbsSampler() {
        super();
    }
    
    public LatentDirichetAllocationVectorGibbsSampler(
        final int topicCount,
        final double alpha,
        final double beta,
        final int maxIterations,
        final int burnInIterations,
        final int iterationsPerSample,
        final Random random) {
        super(topicCount, alpha, beta, maxIterations, burnInIterations, iterationsPerSample, random);
    }
}
