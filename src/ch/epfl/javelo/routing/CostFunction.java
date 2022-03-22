package ch.epfl.javelo.routing;

public interface CostFunction {

    public abstract double costFactor(int nodeId, int edgeId);

}
