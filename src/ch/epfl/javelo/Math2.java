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

    //Pour cette surcharge, n'y a-t-il pas moyen de la connecter avec l'autre ?
    // Idée : coder celle de double puis appeler dans celle de int celle de double ou un truc.
    //Un peu comme ceci :

//    public static int clamp2(int min, int v, int max){
//        return int(clamp(double(min), double(v), double (max)));
//    }

    public static double asinh(double x){
        return Math.log(x + Math.sqrt(1 + Math.pow(x,2)));  //Est-ce que vaut mieux créer
                                                            //une variable et la return, ou comme ça ?
    }

    public static double dotProduct(double uX, double uY, double vX, double vY){
        return Math.fma(uX, vX, uY*vY);
    }

    public static double squaredNorm(double uX, double uY){
        return Math.pow(uX,2)+Math.pow(uY,2);
    }

    public static double norm(double uX, double uY){
        return Math.sqrt(squaredNorm(uX,uY));
    }

    public static double projectionLength(double aX, double aY, double bX, double bY,
                                          double pX, double pY){
        return (dotProduct(pX-aX,pY-aY,bX-aX,bY-aY))/norm(bX-aX,bY-aY);
    }



}
