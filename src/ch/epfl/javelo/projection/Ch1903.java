package ch.epfl.javelo.projection;

public final class Ch1903 {

    private Ch1903() {}

    public static double e(double lon, double lat){
        double lon1 = Math.pow(10,-4)*(3600*Math.toDegrees(lon) - 26782.5);
        double lat1 = Math.pow(10,-4)*(3600*Math.toDegrees(lat) - 169028.66);
        double e = 2600072.37+211455.93*lon1 - 10938.51*lon1*lat1-0.36*lon1*lat1*lat1
                - 44.54*Math.pow(lon1,3);
        return e;
    }
    public static double n(double lon, double lat){
        
        return 0;
    }
    public static double lon(double e, double n){
      return 0;
    }
    public static double lat(double e, double n){
        return 0;
    }
}
