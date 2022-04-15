package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * Represents a crossing point
 * @author Hamane Aya (345565)
 * @author Sadgal Lina (342075)
 */
public record Waypoint(PointCh pointCh, int closestNodeId) {
}
