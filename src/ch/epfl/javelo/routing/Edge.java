package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length,
                   DoubleUnaryOperator profile) {

    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId){
        return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId),
                graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    public double positionClosestTo(PointCh point){
//        double position = 0;
//        position = Math2.projectionLength(fromPoint.getE(), fromPoint.getN(), toPoint.getE(),
//                toPoint.getN(), point.getE(), point.getN());
//        if (position < 0){
//            return 0;
//        } else if (position > length){
//            return length;
//        }
//        return position;
        return Math2.projectionLength(fromPoint.getE(), fromPoint.getN(), toPoint.getE(),
                toPoint.getN(), point.getE(), point.getN());
    }


    public double atElevation(double position){
        return profile().applyAsDouble(position);
    }


}
