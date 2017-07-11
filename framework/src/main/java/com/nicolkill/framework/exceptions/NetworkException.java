package com.nicolkill.framework.exceptions;

/**
 * Created by Nicol Acosta on 10/24/16.
 * nicol@parkiller.com
 */
public class NetworkException extends Exception {

    private static final String TAG = NetworkException.class.getSimpleName();

    public NetworkException() {
        super("You need internet connection");
    }

}
