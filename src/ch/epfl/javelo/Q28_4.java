package ch.epfl.javelo;

public final class Q28_4 {

    private Q28_4(){}

    public static void main(String[] args) {
        System.out.println(asDouble(ofInt(1)));
    }
    /**
     * Calculates the Q28.4 value corresponding to a given integer
     * @param i a given integer
     * @return the corresponding Q28.4 value
     */
    public static int ofInt(int i){
        return i << 4; //NEW
        //return i >> 4; //OLD
    } //Decalage a gauche

    /**
     * Calculates  the value of type double corresponding to a given Q28.4 value
     * @param q28_4 a given Q28.4 value
     * @return the corresponding value of type double
     */
    public static double asDouble(int q28_4){
        return Math.scalb(q28_4, -4);
    } //Diviser avec -4(new)
    // avant ct 4

    /**
     * Calculates  the value of type float corresponding to a given Q28.4 value
     * @param q28_4 a given Q28.4 value
     * @return the corresponding value of type float
     */
    public static float asFloat(int q28_4){
        return Math.scalb(q28_4, -4);
    } //Diviser avec -4
}
