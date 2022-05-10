package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
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
    private RouteComputer rc;
    private LinkedHashMap<Pair<Integer, Integer>, Route> cacheMemoryRoutes;

    private final static int CACHE_MEMORY_ROUTES_CAPACITY = 25;
    private final static int MAX_STEP_LENGTH = 5;

    public RouteBean (RouteComputer rc){
        this.rc = rc;
        cacheMemoryRoutes = new LinkedHashMap<>(CACHE_MEMORY_ROUTES_CAPACITY, 0.75f, true);
        waypoints = FXCollections.observableArrayList();
        route = new SimpleObjectProperty<>();
        elevationProfile = new SimpleObjectProperty<>();
        highlightedPosition = new SimpleDoubleProperty();
        waypoints.addListener((InvalidationListener) l -> computingItineraryAndProfile());
    }

    //USED ONLY DANS LE TEST
    public void setWaypoints(ObservableList<Waypoint> listOfWaypoints) {
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
        if (waypoints.size() < 2){
            routeAndItineraryToNull();
            return;
        }
        Integer startNodeId;
        Integer endNodeId;
        for (int i = 0; i < waypoints.size() - 1; ++i){
            startNodeId = waypoints.get(i).closestNodeId();
            endNodeId = waypoints.get(i + 1).closestNodeId();
            if(! startNodeId.equals(endNodeId)) {
                if (cacheMemoryRoutes.containsKey(new Pair<>(startNodeId, endNodeId))){
                    routes.add(cacheMemoryRoutes.get(new Pair<>(startNodeId, endNodeId)));
                } else {
                    Route routeToAdd = rc.bestRouteBetween(startNodeId, endNodeId);
                    if (routeToAdd == null) {
                        routeAndItineraryToNull();
                        return;
                    }
                    checkCacheCapacity();
                    cacheMemoryRoutes.put(new Pair<>(startNodeId, endNodeId), routeToAdd);
                    routes.add(routeToAdd);
                }
            }
        }
        MultiRoute theRoute = new MultiRoute(routes);
        route.set(theRoute);
        elevationProfile.set(ElevationProfileComputer.elevationProfile(theRoute, MAX_STEP_LENGTH));
    }

    private void routeAndItineraryToNull(){
        route.set(null);
        elevationProfile.set(null);
    }

    public ReadOnlyObjectProperty<Route> routeProperty(){
        return route;
    }

    public DoubleProperty highlightedPositionProperty(){
        return highlightedPosition;
    }

    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty(){
        return elevationProfile;
    }

    public Route route() { return route.get();}

    public double highlightedPosition() {
        return highlightedPosition.get();
    }

    public ObservableList<Waypoint> waypoints() {
        return waypoints;
    }

    private void checkCacheCapacity(){
        if(cacheMemoryRoutes.size() == CACHE_MEMORY_ROUTES_CAPACITY) {
            Iterator<Pair<Integer, Integer>> iterator = cacheMemoryRoutes.keySet().iterator();
            cacheMemoryRoutes.remove(iterator.next());
        }
    }

    public int indexOfNonEmptySegmentAt(double position) {
        int index = route().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).closestNodeId();
            int n2 = waypoints.get(i + 1).closestNodeId();
            if (n1 == n2) index += 1;
        }
        return index;
    }

}
