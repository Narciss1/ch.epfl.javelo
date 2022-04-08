package ch.epfl.javelo.projection;

/**
 * Provides static methods to convert between WGS 84 and Swiss coordinates
 * @author Aya Hamane (345565)
 */
public final class Ch1903 {

    private Ch1903() {}

    /**
     * Calculates the east coordinate of a point in the WGS84 system
     * @param lon longitude
     * @param lat latitude
     * @return east coordinate of a point
     */
    public static double e(double lon, double lat){
        double lon1 = 0.0001 * (3600.0*Math.toDegrees(lon) - 26782.5);
        double lat1 = 0.0001 * (3600.0*Math.toDegrees(lat) - 169028.66);
        return 2600072.37 + 211455.93*lon1 - 10938.51*lon1*lat1 - 0.36*lon1*lat1*lat1
                - 44.54*lon1*lon1*lon1;
    }

    /**
     * Calculates the north coordinate of a point in the WGS84 system
     * @param lon longitude
     * @param lat latitude
     * @return north coordinate of a point
     */
    public static double n(double lon, double lat){
        double lon1 = 0.0001 * (3600.0*Math.toDegrees(lon) - 26782.5);
        double lat1 = 0.0001 * (3600.0*Math.toDegrees(lat) - 169028.66);
        return 1200147.07 + 308807.95*lat1 + 3745.25*lon1*lon1 + 76.63*lat1*lat1
                - 194.56*lat1*lon1*lon1 + 119.79*lat1*lat1*lat1;
    }

    /**
     * Calculates the longitude of a point in the swiss system
     * @param e east coordinate
     * @param n north coordinate
     * @return longitude of a point
     */
    public static double lon(double e, double n){
        double x = 0.000001 * (e - 2600000.0);
        double y = 0.000001 * (n - 1200000.0);
        double lon0 = 2.6779094 + 4.728982*x + 0.791484*x*y + 0.1306*x*y*y
                - 0.0436*x*x*x;
        double lon = lon0*100.0 / 36.0;
      return Math.toRadians(lon);
    }

    /**
     * Calculates the latitude of a point in the swiss system
     * @param e east coordinate
     * @param n north coordinate
     * @return latitude of a point
     */
    public static double lat(double e, double n){
        double x = 0.000001 * (e - 2600000.0);
        double y = 0.000001 * (n - 1200000.0);
        double lat0 = 16.9023892 + 3.238272*y - 0.270978*x*x - 0.002528*y*y - 0.0447*x*x*y
                - 0.0140*y*y*y;
        double lat = lat0*100.0 / 36.0;
        return Math.toRadians(lat);
    }
}
