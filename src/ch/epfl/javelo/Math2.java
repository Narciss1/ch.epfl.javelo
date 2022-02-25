package ch.epfl.javelo;

public final class Math2
{
    private Math2(){}

    /**
     *
     * @param x
     * @param y
     * @return the ceiling of the division of x by y.
     */
    public static int ceilDiv(int x, int y){
        Preconditions.checkArgument(x >= 0 && y > 0);
        return (x + y - 1) / y;
    }


    /**
     *
     * @param y0
     * @param y1
     * @param x
     * @return the coordinate y of the dot located on the staight line drawn through (à,y0)
     * and (1,y1) and of coordinate x.
     */
    public static double interpolate (double y0, double y1, double x){
        return Math.fma(y1-y0, x , y0);
    }



    public static double clamp(double min, double v, double max){
        Preconditions.checkArgument(min < max);
        if (v < min){
            return min;
        } else if (v > max){
            return max;
        }
        return v;
    }

    public static int clamp(int min, int v, int max){
        Preconditions.checkArgument(min < max);
        if (v < min){
            return min;
        } else if (v > max){
            return max;
        }
        return v;
    }



    /**
     *
     * @param x
     * @return the inverse hyperbolic sine of x
     */
    public static double asinh(double x){
        return Math.log(x + Math.sqrt(1 + Math.pow(x,2)));
    }

    /**
     *
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
     *
     * @param uX coordinate x of the vector
     * @param uY coordinate y of the vector
     * @return the squared norm of the vector
     */
    public static double squaredNorm(double uX, double uY){
        return Math.pow(uX,2)+Math.pow(uY,2);
    }

    /**
     *
     * @param uX coordinate x of the vector
     * @param uY coordinate y of the vector
     * @return the norm of the vector
     */
    public static double norm(double uX, double uY){
        return Math.sqrt(squaredNorm(uX,uY));
    }

    /**
     *
     * @param aX coordinate x of the first dot
     * @param aY coordinate y of the first dot
     * @param bX coordinate x of the third dot
     * @param bY coordinate y of the third dot
     * @param pX coordinate x of the second dot
     * @param pY coordinate y of the second dot
     * @return the projection length of vector from a first dot to a second dot on the vector
     * from the first dot to a third dot.
     */
    public static double projectionLength(double aX, double aY, double bX, double bY,
                                          double pX, double pY){
        return (dotProduct(pX-aX,pY-aY,bX-aX,bY-aY))/norm(bX-aX,bY-aY);
    }



}
