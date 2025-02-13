package com.example.atm;

public interface ResultListener<T> {
    void onSuccess(T t);

    void onError(Throwable throwable);
}
