package com.mcmiddleearth.entities.util;

@FunctionalInterface
public interface TriFunction<X, Y, Z, R> {

    R apply(X x, Y y, Z z);
}
