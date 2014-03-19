package com.webbricks.datautility;

public class Dimension<T> {
    private T X; //first member of pair
    private T Y; //second member of pair

    public Dimension()
    {
    	
    }
    public Dimension(T X, T Y) {
        this.X = X;
        this.Y = Y;
    }

    public T getX() {
        return X;
    }

    public T getY() {
        return Y;
    }
}
