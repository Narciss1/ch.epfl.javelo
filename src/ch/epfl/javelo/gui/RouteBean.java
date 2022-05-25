package ch.epfl.javelo.gui;

import ch.epfl.javelo.Cache;
import ch.epfl.javelo.routing.*;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.List;

/**
 * A JavaFX bean grouping properties linked to the waypoints and the itinerary
 * @author Lina Sadgal (342075)
 * @author Aya Hamane (345565)
 */
public final class RouteBean {

    private final ObservableList<Waypoint> waypoints;
    private final ObjectProperty<Route> routeP;
    private final ObjectProperty<ElevationProfile> elevationProfileP;
    private final DoubleProperty highlightedPositionP;
    private final RouteComputer rc;
    private final Cache<Pair<Integer, Integer>, Route> cacheMemoryRoutes;

    /**
     * The maximum capacity of the cache containing the last added routes
     */
    private final static int CACHE_MEMORY_ROUTES_CAPACITY = 25;
    /**
     * The maximum step length
     */
    private final static int MAX_STEP_LENGTH = 5;
    /**
     * The minimum size of the list of the waypoints so that we attempt to calculate a route
     */
    private final static int WAYPOINTS_MINIMAL_SIZE_CHECK = 2;

    /**
     * Constructor
     * @param rc the RouteComputer used to compute the route
     */
    public RouteBean (RouteComputer rc) {
        this.rc = rc;
        cacheMemoryRoutes = new Cache<>(CACHE_MEMORY_ROUTES_CAPACITY);
        waypoints = FXCollections.observableArrayList();
        routeP = new SimpleObjectProperty<>();
        elevationProfileP = new SimpleObjectProperty<>();
        highlightedPositionP = new SimpleDoubleProperty();
        waypoints.addListener((InvalidationListener) l -> computingItineraryAndProfile());
    }

    /**
     * Stores the given position in the highlightedPositionProperty if the route is not null
     * and the position is between 0 and the length of the route, and NaN if not
     * @param position the position to be stored in the highlightedPositionProperty
     */
    public void setHighlightedPositionP(double position) {
        if ((routeP.get() != null) && !(0 <= position && position <= routeP.get().length())) {
            highlightedPositionP.set(Double.NaN);
        } else {
            highlightedPositionP.set(position);
        }
    }

    /**
     * Returns the route property as a ReadOnlyObjectProperty
     * @return a route property as a ReadOnlyObjectProperty
     */
    public ReadOnlyObjectProperty<Route> routeProperty() {
        return routeP;
    }

    /**
     * Returns the elevation profile property as a ReadOnlyObjectProperty
     * @return an elevation profile property
     */
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty() {
        return elevationProfileP;
    }

    /**
     * Returns the route
     * @return a route
     */
    public Route route() {
        return routeP.get();
    }

    /**
     * Returns the elevation profile
     * @return an elevation profile
     */
    public ElevationProfile elevationProfile() {
        return elevationProfileP.get();
    }

    /**
     * Returns the highlighted position
     * @return the position in the itinerary of the highlighted position
     */
    public double highlightedPosition() {
        return highlightedPositionP.get();
    }

    /**
     * Returns the highlighted position property
     * @return the highlighted position property
     */
    public DoubleProperty highlightedPositionProperty() {
        return highlightedPositionP;
    }

    /**
     * Returns the list of waypoints
     * @return the observable list containing the waypoints
     */
    public ObservableList<Waypoint> waypoints() {
        return waypoints;
    }

    /**
     * Computes the itinerary and the profile between the existing waypoints
     * and respectively stores them in the route property and elevation profile property
     */
    private void computingItineraryAndProfile() {
        List<Route> routes = new ArrayList<>();
        if (waypoints.size() < WAYPOINTS_MINIMAL_SIZE_CHECK){
            routeAndItineraryToNull();
            return;
        }
        int startNodeId, endNodeId;
        for (int i = 0; i < waypoints.size() - 1; ++i) {
            startNodeId = waypoints.get(i).closestNodeId();
            endNodeId = waypoints.get(i + 1).closestNodeId();
            if(startNodeId != endNodeId) {
                if (cacheMemoryRoutes.containsKey(new Pair<>(startNodeId, endNodeId))) {
                    routes.add(cacheMemoryRoutes.getValue(new Pair<>(startNodeId, endNodeId)));
                } else {
                    Route routeToAdd = rc.bestRouteBetween(startNodeId, endNodeId);
                    if (routeToAdd == null) {
                        routeAndItineraryToNull();
                        return;
                    }
                    cacheMemoryRoutes.addToCache(new Pair<>(startNodeId, endNodeId), routeToAdd);
                    routes.add(routeToAdd);
                }
            }
        }
        MultiRoute theRoute = new MultiRoute(routes);
        routeP.set(theRoute);
        elevationProfileP.set(ElevationProfileComputer.elevationProfile(theRoute, MAX_STEP_LENGTH));
    }

    /**
     * Stores null in the route and itinerary properties
     */
    private void routeAndItineraryToNull() {
        routeP.set(null);
        elevationProfileP.set(null);
    }

    /**
     * Gives the index of the route's segment at a given position
     * @param position the given position for which we want the segment's index
     * @return the index of the route's segment at a given position
     */
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
