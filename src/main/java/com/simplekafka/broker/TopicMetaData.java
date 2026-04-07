package com.simplekafka.broker;
import java.util.List;

public class TopicMetaData {
    private final String topic;
    private final List<PartitionMetadata> partitionMetadataInfo;

    public TopicMetaData(String topic, List<PartitionMetadata> partitionMetadataInfo){
        this.topic = topic;
        this.partitionMetadataInfo = partitionMetadataInfo;
    }

    public String getTopic(){
        return topic;
    }
    public List<PartitionMetadata> getPartitionMetadataInfo(){
        return partitionMetadataInfo;
    }

    @Override
    public String toString(){
        return "TopicMetaData { Topic: " + topic + ", PartitionMetadataInfo " + partitionMetadataInfo + " } ";
    }
}
