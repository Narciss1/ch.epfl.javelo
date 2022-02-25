package ch.epfl.javelo;

public final class Bits {

    private Bits(){}

    //Verifier si thats all i need pour l'interpretation
    // comme une valeur signée en complement à deux
    public int extractSigned(int value, int start, int length){
        //Verifier le meaning de length ici
        if(start>=0 && start<=31 && length>=0 && length<=32){
            int newValue = (value << (32-start-length)) >> (32-length) ;
            return newValue;
        }else{
            throw new IllegalArgumentException();
        }
    }
    public int extractUnsigned(int value, int start, int length){
        if(start>=0 && start<=31 && length>=0 && length<32){
            int newValue = (value << (32-start-length)) >>> (32-length);
            return newValue;
        }else{
            throw new IllegalArgumentException();
        }
    }
}
