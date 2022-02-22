package ch.epfl.javelo;

public final class Math2
{
    private Math2(){}

    public static int ceilDiv(int x, int y){
        Preconditions.checkArgument(x >= 0 && y > 0);
        return (x + y - 1) / y;
    }

    public static double interpolate (double y0, double y1, double x){
        return Math.fma(y1-y0, x , y0);
    }
}
