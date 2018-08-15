package sample.utils;


public class Helper {


    public static double round(double d)
    {
        int temp = (int)(d * Math.pow(10 , 4));
        return ((double)temp)/Math.pow(10 , 4);
    }
}
