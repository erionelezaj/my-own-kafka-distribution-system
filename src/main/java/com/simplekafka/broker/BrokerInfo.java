/*This is the code that falls into step 2  in creating simple Kafka. This code serves in terms of the protocol layer.
* This is the BrokerInfo Class that contains info of each broker such as: Broker's id, hostname, and the port which it is using
* The last two methods serves as ID verification between broker */
package com.simplekafka.broker;

public class BrokerInfo {
    private final int id;
    private final String host;
    private final int port;

    public BrokerInfo(int id, String host, int port){
        this.id=id;
        this.host=host;
        this.port=port;
    }

    public int getId(){
        return id;
    }

    public String getHost(){
        return host;
    }

    public int getPort(){
        return port;
    }


    @Override
    public String toString(){
        return "BrokerInfo {id = " + id + ", host = "  + host + ", port = " + port + " } ";
    }

    // Checks if two broker objects represent the same physical member of the cluster
// by comparing their unique IDs, ignoring changing data like host/port.
@Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj==null || getClass() !=obj.getClass()) return false;

        BrokerInfo other = (BrokerInfo) obj;
        return id == other.id;
    }

// This method creates a unique "Fingerprint" for the ID to be used in hashMaps/sets
    @Override
    public int hashCode(){
        return Integer.hashCode(id);
    }

}
