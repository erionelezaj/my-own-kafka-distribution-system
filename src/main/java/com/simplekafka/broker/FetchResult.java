package com.simplekafka.broker;

public class FetchResult {
    public final byte[][] message;
    public final String error;

    public FetchResult(byte[][] message, String error){
        this.message = message;
        this.error = error;
    }

    public byte[][] getMessage(){
        return message;
    }

    public String getError(){
        return error;
    }

    @Override
    public String toString(){
        return "FetchResult {message = " + message + ", error " + error + " } ";
    }
}
