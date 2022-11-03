package com.mrx.springjava2smali.java2smali.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Mr.X
 * @since 2022-11-03 20:05
 */
public class XPromise<T> implements Consumer<T>, Supplier<T> {

    private T data;

    @Override
    public void accept(T t) {
        data = t;
    }

    @Override
    public T get() {
        return data;
    }

}
