package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

public final class RouteComputer {

    private final Graph graph;
    private final CostFunction costFunction;


    //Faut-il rajouter des trucs à ce constructeur pour immuabilité ?
    public RouteComputer(Graph graph, CostFunction costFunction){
        this.graph = graph;
        this.costFunction = costFunction;
    }

//    public Route bestRouteBetween(int startNodeId, int endNodeId){
//        Preconditions.checkArgument(startNodeId != endNodeId);
//
//    }

}
