# Build Your Own Kafka — SimpleKafka

## Why I Built This

I was inspired to build my own distributed messaging system after a friend sent me a video about projects you can build from scratch to truly understand how things work under the hood.

Kafka caught my attention immediately — it's written in Java (a language I can't seem to forget), it's used by some of the biggest companies in the world, and it solves a genuinely hard problem: how do you move millions of messages between systems reliably and efficiently?

So instead of just using Kafka, I decided to build it. Not to replace it — but to understand it.

---

## What is Kafka?

Kafka is a **distributed message broker** — a system that sits between applications and allows them to send and receive messages reliably at massive scale.

```
[Producer App] ──sends──▶ [Kafka Broker] ──delivers──▶ [Consumer App]
```

Companies like **LinkedIn** (who invented Kafka), **Netflix**, **Uber**, and **Spotify** use it to process millions of events per second — user activity, location updates, payments, notifications, and more.

---

## Why Binary Protocol?

Unlike web servers that use JSON or XML, Kafka uses a **binary protocol** for communication. Every message, response, and request is encoded as raw bytes.

Why? Because at millions of messages per second, even small inefficiencies multiply into massive overhead. A 4-byte integer is always exactly 4 bytes in binary — but could be anywhere from 1 to 10 characters as text.

Each message is identified by a **type byte** at the start:
- `0x01` → PRODUCE (write a message)
- `0x02` → FETCH (read messages)
- `0x03` → METADATA (cluster info)
- `0x04` → CREATE_TOPIC

The broker reads the first byte and immediately knows what kind of request it's dealing with — no parsing, no ambiguity.

---

## What I've Built So Far

### Stage 1 — Project Setup
Maven project structure with the `com.simplekafka.broker` package.

### Stage 2 — Wire Protocol (`Protocol.java`)
The binary protocol layer that defines how all communication is encoded and decoded.

**Encode methods** (client → broker):
- `encodeProduceRequest` — write a message to a topic/partition
- `encodeFetchRequest` — read messages from a topic/partition/offset
- `encodeMetadataRequest` — request cluster information
- `encodeCreateTopicRequest` — create a new topic
- `encodeReplicateRequest` — broker-to-broker replication
- `encodeTopicNotification` — notify brokers of new topics

**Decode methods** (broker → client):
- `decodeProduceResponse` — unpack produce confirmation
- `decodeFetchResponse` — unpack fetched messages
- `decodeMetadataResponse` — unpack cluster metadata

**Result classes:**
- `ProduceResult` — offset and error from a produce operation
- `FetchResult` — array of messages and error from a fetch operation
- `MetadataResult` — broker list, topic list, and error from metadata
- `TopicMetaData` — topic name and partition information
- `PartitionMetadata` — partition ID, leader broker, and replicas

**Supporting classes:**
- `BrokerInfo` — holds broker ID, host, and port

### Stage 3 — ZooKeeper Integration (In Progress)
ZooKeeper acts as the coordination layer for the cluster — tracking which brokers are alive, who is the leader of each partition, and what topics exist.

---

## Project Structure

```
src/
└── main/
    └── java/
        └── com/simplekafka/broker/
            ├── BrokerInfo.java
            ├── Protocol.java
            ├── ProduceResult.java
            ├── FetchResult.java
            ├── MetadataResult.java
            ├── TopicMetaData.java
            └── PartitionMetadata.java
```

---

## Roadmap

- [x] Stage 1 — Project Setup
- [x] Stage 2 — Wire Protocol
- [ ] Stage 3 — ZooKeeper Integration
- [ ] Stage 4 — Storage Layer
- [ ] Stage 5 — Broker Implementation
- [ ] Stage 6 — Replication
- [ ] Stage 7 — Producer & Consumer APIs
- [ ] Stage 8 — End-to-End Testing

---

## Resources I'm Following

- [Medium Article Series by Raghav](https://buildthingsuseful.medium.com/building-your-own-kafka-like-system-from-scratch-a-step-by-step-guide-d3c5f0a303c0)
- [Reference Repository](https://github.com/buildthingsuseful/build-your-own-kafka.git)

---

## Tech Stack

- **Java** — core implementation language
- **Maven** — build and dependency management
- **ZooKeeper** — distributed coordination
- **ByteBuffer** — binary protocol serialization