package com.webbricks.cms;

public class Pair<F, S> {
    private F first; //first member of pair
    private S second; //second member of pair

    public Pair()
    {
    	
    }
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }
}
