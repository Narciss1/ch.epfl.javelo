package ch.epfl.javelo.routing;

/**
 * Represents a cost function
 * @author Lina Sadgal (342075)
 */

public interface CostFunction {

    /**
     * Calculates the cost factor of a given edge going from a given node.
     * @param nodeId the identity of the node
     * @param edgeId the identity of the edge
     * @return the cost factor
     */
    abstract double costFactor(int nodeId, int edgeId);

}
