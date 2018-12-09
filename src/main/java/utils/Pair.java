package utils;

/**
 * Basic pair of value. Follow the same logic as the pair object from C++
 * @see "http://www.cplusplus.com/reference/utility/pair/"
 * @author Antoine FORET
 * @version 1.0
 */
public class Pair<A, B> {
    public A left;
    public B right;

    public Pair(A a, B b) {
        this.left = a;
        this.right = b;
    }
}
