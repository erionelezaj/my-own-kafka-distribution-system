package com.simplekafka.broker;
import java.util.List;

public class MetadataResult {
    private final List<BrokerInfo> brokerInfoList;
    private final List<TopicMetaData> topicMetaDataList;
    private final String error;

    public MetadataResult(List<BrokerInfo> brokerInfoList,List<TopicMetaData> topicMetaDataList, String error ){
        this.brokerInfoList=brokerInfoList;
        this.topicMetaDataList=topicMetaDataList;
        this.error = error;
    }

    public List<BrokerInfo> getBrokerInfoList(){
        return brokerInfoList;
    }
    public List<TopicMetaData> getTopicMetaDataList(){
        return topicMetaDataList;
    }

    public String getError(){
        return error;
    }

    @Override
    public String toString(){
        return "MetadataResult { BrokerInfoList: " + brokerInfoList + ", TopicMetadataList: " + topicMetaDataList + ", Error: " + error + " } ";
    }
}
