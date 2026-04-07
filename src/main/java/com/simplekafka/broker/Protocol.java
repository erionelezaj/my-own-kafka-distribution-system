package com.simplekafka.broker;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Protocol {
    // Client Request Type
    public static final byte PRODUCE =0x01;
    public static final byte FETCH =0x02;
    public static final byte METADATA =0x03;
    public static final byte CREATE_TOPIC =0x04;


    // Broker to Broker Type
    public static final byte REPLICATE = 0x05;
    public static final byte TOPIC_NOTIFICATION = 0x06;


    // Broker Response Type
    public static final byte PRODUCE_RESPONSE=0x11;
    public static final byte FETCH_RESPONSE=0x12;
    public static final byte METADATA_RESPONSE=0x13;
    public static final byte CREATE_TOPIC_RESPONSE=0x14;


    // Error Response type
    public static final byte ERROR_RESPONSE = 0x015;




    public ByteBuffer encodeProduceRequest(String topic, int partition, byte[] message){
        byte[] topicBytes = topic.getBytes();
        int bufferSize = 1 + 2 + topicBytes.length + 4 + 4 + message.length;
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

        buffer.put(PRODUCE);
        buffer.putShort((short) topicBytes.length);
        buffer.put(topicBytes);
        buffer.putInt(partition);
        buffer.putInt(message.length);
        buffer.put(message);

        buffer.flip();
        return buffer;

    }

    public static ByteBuffer encodeFetchRequest(String topic, int partition, long offset, int maxByte){
        byte[] topicBytes = topic.getBytes();
        int bufferSize = 1 + 2 + topicBytes.length + 4 + 8 +  4;
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        buffer.put(FETCH);
        buffer.putShort((short) topicBytes.length);
        buffer.put(topicBytes);
        buffer.putInt(partition);
        buffer.putLong(offset);
        buffer.putInt(maxByte);
        buffer.flip();
        return buffer;
    }


    public static ByteBuffer encodeMetaDataRequest(){
        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put(METADATA);
        buffer.flip();
        return buffer;
    }

    public static ByteBuffer encodeTopicRequest(String topic, int numPartitions, short replicationFactor){
        byte[] topicBytes = topic.getBytes();
        int bufferSize = 1 + 2  + topicBytes.length + 4 + 2;
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        buffer.put(CREATE_TOPIC);
        buffer.putShort((short) topicBytes.length);
        buffer.put(topicBytes);
        buffer.putInt(numPartitions);
        buffer.putShort(replicationFactor);

        buffer.flip();
        return buffer;
    }

    public static ByteBuffer encodeReplicateRequest(String topic, int partition, long offset, byte[] message){
        byte[] topicBytes = topic.getBytes();
        int bufferSize = 1 + 2 + topicBytes.length +4 + 8 + 4 + message.length;
        ByteBuffer  buffer = ByteBuffer.allocate(bufferSize);
        buffer.put(REPLICATE);
        buffer.putShort((short) topicBytes.length);
        buffer.put(topicBytes);
        buffer.putInt(partition);
        buffer.putLong(offset);
        buffer.putInt(message.length);
        buffer.put(message);

        buffer.flip();
        return buffer;
    }

    public static ByteBuffer encodeTopicNotification(String topic){
        byte[] topicBytes = topic.getBytes();
        int bufferSize = 1  + 2 + topicBytes.length;

        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        buffer.put(TOPIC_NOTIFICATION);
        buffer.putShort((short)topicBytes.length);
        buffer.put(topicBytes);

        buffer.flip();
        return buffer;
    }

    public static void sendErrorResponse(SocketChannel channel, String errorMessage){
            byte[] errorMessageBytes = errorMessage.getBytes();
            int bufferSize = 1 + 2 + errorMessageBytes.length;
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            buffer.put(ERROR_RESPONSE);
            buffer.putShort((short) errorMessageBytes.length);
            buffer.put(errorMessageBytes);

            try {
                buffer.flip();
                channel.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }




    // Decode Methods

    public static ProduceResult decodeProduceResponse(ByteBuffer buffer){
        byte responseType = buffer.get();
        if(responseType !=PRODUCE_RESPONSE){
            return new ProduceResult (-1, "Invalid response type");
        }


        long offset = buffer.getLong();
        short errorLength = buffer.getShort();
        byte[] errorBytes = new byte[errorLength];
        buffer.get(errorBytes);
        String error = new String(errorBytes);

        return  new ProduceResult(offset, error);
    }

    public static FetchResult decodeFetchResult(ByteBuffer buffer){
        byte responseType = buffer.get();
        if(responseType != FETCH_RESPONSE){
            return new FetchResult (null, "Invalid response type");
        }

        int messageCount = buffer.getInt();
        byte[][] message = new byte[messageCount][];

        for (int i=0;i<messageCount;i++){
            int messageSize =buffer.getInt();
            byte[] simpleMessage = new byte[messageSize];
            buffer.get(simpleMessage);
            message[i] = simpleMessage;
        }
        short errorLength =buffer.getShort();
        byte[] errorByte = new byte[errorLength];
        buffer.get(errorByte);
        String error = new String(errorByte);

        return new FetchResult(message, error);
    }

    public static MetadataResult decodeMetadataResult(ByteBuffer buffer){
        byte responseType = buffer.get();
        if(responseType != METADATA_RESPONSE){
            return new MetadataResult(null,null,"Invalid Response Type");
        }

        int brokerCount = buffer.getInt();
        List<BrokerInfo> brokerInfo = new ArrayList<>(brokerCount);

        for(int i=0;i<brokerCount;i++){
            int brokerId = buffer.getInt();
            short brokerHostLength = buffer.getShort();
            byte[] brokerHostByte = new byte[brokerHostLength];
            buffer.get(brokerHostByte);
            String brokerHost = new String(brokerHostByte);
            int brokerPort = buffer.getInt();

            brokerInfo.add(new BrokerInfo(brokerId, brokerHost, brokerPort));
        }
        int topicMetadataCount = buffer.getInt();
        List<TopicMetaData> topicMetadata = new ArrayList<>();

        for(int i=0;i<topicMetadataCount;i++){
            short topicMetadataLength = buffer.getShort();
            byte[] topicMetadataBytes = new byte[topicMetadataLength];
            buffer.get(topicMetadataBytes);
            String topicMetadataMessage = new String(topicMetadataBytes);

            int partitionMetadataLength = buffer.getInt();
            List<PartitionMetadata> partitionMetadataInfo = new ArrayList<>();
            for(int j=0;j<partitionMetadataLength;j++){
                int partitionId = buffer.getInt();
                int leaderId = buffer.getInt();
                int replicaCount = buffer.getInt();
                int[] replicas = new int[replicaCount];
                for (int k=0;k<replicaCount;k++){
                    replicas[k] = buffer.getInt();
                }

               partitionMetadataInfo.add(new PartitionMetadata(partitionId, leaderId, replicas));
            }
            topicMetadata.add(new TopicMetaData(topicMetadataMessage,partitionMetadataInfo));
        }

        short errorLength = buffer.getShort();
        byte[] errorByte = new byte[errorLength];
        buffer.get(errorByte);
        String error = new String(errorByte);

        return new MetadataResult(brokerInfo,topicMetadata,error);
    }
}
