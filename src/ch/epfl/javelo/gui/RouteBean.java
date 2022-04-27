package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public final class RouteBean {

    private ObservableList<Waypoint> waypoints;
    private ObjectProperty<Route> route;
    private ObjectProperty<ElevationProfile> elevationProfile;
    private DoubleProperty highlightedPosition;

    //Propriété ou pas propriété ?
    private RouteComputer rc;
    //Route ou SingleRoute ???
    private LinkedHashMap<Pair<Integer, Integer>, Route> cacheMemoryRoutes;

    private final static int CACHE_MEMORY_ROUTES_CAPACITY = 20;
    private final static int MAX_STEP_LENGTH = 5;

    public RouteBean (RouteComputer rc){
        this.rc = rc;
        //pr la initial capacity, à vérifier.
        cacheMemoryRoutes = new LinkedHashMap<>(CACHE_MEMORY_ROUTES_CAPACITY, 0.75f, true);
        waypoints = FXCollections.observableArrayList();
        route = new SimpleObjectProperty<>();
        elevationProfile = new SimpleObjectProperty<>();
        highlightedPosition = new SimpleDoubleProperty();
        waypoints.addListener((InvalidationListener) l -> computingItineraryAndProfile());
    }

    public void setWaypoints(ObservableList<Waypoint> listOfWaypoints){
        waypoints.clear();
        waypoints.setAll(listOfWaypoints);
    }

    public void setHighlightedPosition(double position){
        if ((route.get() != null) && !(0 <= position && position <= route.get().length())) {
            highlightedPosition.set(Double.NaN);
        } else {
            highlightedPosition.set(position);
        }
    }

    private void computingItineraryAndProfile(){
        List<Route> routes = new ArrayList<>();
        Integer startNodeId;
        Integer endNodeId;
        if (waypoints.size() < 2){
            System.out.println("dans inf à deux cause why not");
            routeAndItineraryToNull();
            return;
        }
        for (int i = 0; i < waypoints.size() - 1; ++i){
            System.out.println(waypoints.size());
            System.out.println(waypoints.get(0).closestNodeId());
            System.out.println(waypoints.get(1).closestNodeId());
            startNodeId = waypoints.get(i).closestNodeId();
            System.out.println(startNodeId);
            endNodeId = waypoints.get(i+1).closestNodeId();
            System.out.println(endNodeId);
            if (cacheMemoryRoutes.containsKey(new Pair<>(startNodeId, endNodeId))){
                routes.add(cacheMemoryRoutes.get(new Pair<>(startNodeId, endNodeId)));
            } else {
                Route routeToAdd = rc.bestRouteBetween(startNodeId, endNodeId);
                if (routeToAdd == null){
                    System.out.println( i + " : je suis dans routeToAdd");
                    routeAndItineraryToNull();
                    return;
                }
                checkCacheCapacity();
                cacheMemoryRoutes.put(new Pair<>(startNodeId, endNodeId), routeToAdd);
                routes.add(routeToAdd);
            }
        }
        MultiRoute theRoute = new MultiRoute(routes);
        System.out.println("je set la route");
        route.set(theRoute);
        elevationProfile.set(ElevationProfileComputer.elevationProfile(theRoute, MAX_STEP_LENGTH));
    }

    private void routeAndItineraryToNull(){
        System.out.println("je ss dans toNull");
        route.set(null);
        elevationProfile.set(null);
    }

    //Est-ce que c'est suffisant ?
    public ReadOnlyObjectProperty<Route> routeProperty(){
        return route;
    }

    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty(){
        return elevationProfile;
    }

    public double highlightedPosition(){
        return highlightedPosition.get();
    }
    
    private void checkCacheCapacity(){
        if(cacheMemoryRoutes.size() == CACHE_MEMORY_ROUTES_CAPACITY) {
            Iterator<Pair<Integer, Integer>> iterator = cacheMemoryRoutes.keySet().iterator();
            cacheMemoryRoutes.remove(iterator.next());
        }
    }
}
