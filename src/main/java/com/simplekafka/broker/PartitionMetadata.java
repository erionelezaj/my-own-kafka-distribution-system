package com.simplekafka.broker;

import java.util.Arrays;

public class PartitionMetadata {
    private final int partitionId;
    private final int leaderId;
    private final int[] replicas;

    public PartitionMetadata(int partitionId, int leaderId, int[] replicas){
        this.partitionId = partitionId;
        this.leaderId = leaderId;
        this.replicas = replicas;
    }

    public int getPartitionId(){
        return partitionId;
    }
    public int getLeaderId(){
        return leaderId;
    }

    public int[] getReplicas(){
        return replicas;
    }

    @Override
    public  String toString(){
        return "PartitionMetadata { PartitionId: " + partitionId + ", leaderID: " + leaderId + ",  Replicas: " +  Arrays.toString(replicas) + " } ";
    }
}
