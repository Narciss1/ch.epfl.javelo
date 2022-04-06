package ch.epfl.javelo;

public final class Math2
{
    private Math2(){}

    /**
     * calculates the ceiling of the division between two integers x and y.
     * @throws IllegalArgumentException if x is strictly negative or y is negative.
     * @param x the dividend
     * @param y the divider
     * @return the ceiling of the division of x by y.
     */
    public static int ceilDiv(int x, int y){
        Preconditions.checkArgument(x >= 0 && y > 0);
        return (x + y - 1) / y;
    }

    /**
     * calculates the coordinate y of the dot located on the straight line drawn through (0,y0)
     * and (1,y1) and of coordinate x.
     * @param y0 the image of 0
     * @param y1 the image of 1
     * @param x  the operand we find the image to
     * @return the coordinate y of the dot located on the straight line drawn through (à,y0)
     * and (1,y1) and of coordinate x.
     */
    public static double interpolate (double y0, double y1, double x){
        return Math.fma(y1-y0, x , y0);
    }

    /**
     * takes a double v as an argument, returns it if the interval between the arguments min and max
     * contains v, returns min if the double is smaller than min, or returns max if v is bigger
     * than max.
     * @throws IllegalArgumentException if min is strictly smaller than max
     * @param min the lower bound
     * @param v the value we
     * @param max the upper bound
     * @return min if v is inferior to min, max if v is superior to max, v otherwise.
     */
    public static double clamp(double min, double v, double max){
        Preconditions.checkArgument(min < max);
        return Math.min(Math.max(min,v),max);
    }

    /**
     * takes an int v as an argument, returns it if the interval between the arguments min and max contains v,
     * returns min if the double is smaller than min, or returns max if v is bigger than max.
     * @throws IllegalArgumentException if min is strictly smaller than max
     * @param min the lower bound
     * @param v the value we
     * @param max the upper bound
     * @return min if v is inferior to min, max if v is superior to max, v otherwise.
     */
    public static int clamp(int min, int v, int max){
        Preconditions.checkArgument(min < max);
        return Math.min(Math.max(min,v),max);
    }

    /**
     * calculates and returns the inverse of the hyperbolic sine of the argument x.
     * @param x the operand for which we want to calculate the inverse hyperbolic sine.
     * @return the inverse hyperbolic sine of x
     */
    public static double asinh(double x){
        return Math.log(x + Math.sqrt(1 + Math.pow(x,2)));
    }

    /**
     * calculates and returns the dot product of the two vectors which coordinates are given as arguments.
     * @param uX  coordinate x of the first vector
     * @param uY  coordinate y of the first vector
     * @param vX  coordinate x of the second vector
     * @param vY  coordinate y of the second vector
     * @return the dotProduct of two vectors
     */
    public static double dotProduct(double uX, double uY, double vX, double vY){
        return Math.fma(uX, vX, uY*vY);
    }

    /**
     * calculates and returns the squared norm of the vector which coordinates are given as arguments.
     * @param uX coordinate x of the vector
     * @param uY coordinate y of the vector
     * @return the squared norm of the vector
     */
    public static double squaredNorm(double uX, double uY){
        return Math.pow(uX,2)+Math.pow(uY,2);
    }

    /**
     * calculates and returns the norm of the vector which coordinates are given as arguments.
     * @param uX coordinate x of the vector
     * @param uY coordinate y of the vector
     * @return the norm of the vector
     */
    public static double norm(double uX, double uY){
        return Math.sqrt(squaredNorm(uX,uY));
    }

    /**
     * calculates and returns the length of the projection of a vector on another.
     * @param aX coordinate x of the first dot
     * @param aY coordinate y of the first dot
     * @param bX coordinate x of the third dot
     * @param bY coordinate y of the third dot
     * @param pX coordinate x of the second dot
     * @param pY coordinate y of the second dot
     * @return the projection's length of the vector from A to P on the vector
     * from A to B
     */
    public static double projectionLength(double aX, double aY, double bX, double bY,
                                          double pX, double pY){
        return (dotProduct(pX-aX,pY-aY,bX-aX,bY-aY))/norm(bX-aX,bY-aY);
    }

}
