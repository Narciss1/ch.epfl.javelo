package ch.epfl.javelo;

public final class Bits {

    private Bits(){}

    public int extractSigned(int value, int start, int length){
        if(start>=0 && start<=31 && length>=0 && length<=31){ //A verifier
            return 0;
        }else{
            throw new IllegalArgumentException();
        }
    }
    public int extractUnsigned(int value, int start, int length){
        if(start>=0 && start<=31 && length>=0 && length<=31){ //A verifier
            return 0;                                        //pas de size 32 meaning?
        }else{
            throw new IllegalArgumentException();
        }
    }
}
