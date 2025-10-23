# Kafka on k8s

⚠️ Note:
The sample producer, consumer and stream applications in this repository can only run inside the Kubernetes cluster (i.e., they must be deployed using `clients.yaml`).
This is because Kafka is exposed only internally via a Kubernetes service (no external access is configured). SSL/TLS encryption is enabled by default for all communication between clients and the Kafka broker.
SSL/TLS is not strictly necessary for local development but in production, it is strongly recommended (and considered best practice). If you want to disable SSL/TLS in dev, update the listener and client configuration accordingly.

## Prerequisites

- Docker Desktop
- kubectl (included with Docker Desktop)
- IDE Terminal 

Before you begin, clone this repository and run all commands in this README.md from the repository root (in IDE terminal).

If you don’t have Docker Desktop yet, install it:

👉 https://docs.docker.com/desktop/setup/install/mac-install/

👉 https://docs.docker.com/desktop/setup/install/windows-install/

Once installed, open Docker Desktop → Settings → Kubernetes and enable Kubernetes:

![Enable k8s](https://imgur.com/aV5BDCA.png)

## Setup (Mac/Unix)

1. Once the k8s Cluster has started, verify your current Kubernetes context (the one marked with a *):

```bash
  kubectl config get-contexts
```

2. Make sure the current context is `docker-desktop`. If not, switch to it:

```bash
   kubectl config use-context docker-desktop
```

3. Create a new namespace for Kafka:

```bash
  kubectl create namespace kafka
```

4. Set the current context to use this namespace by default:

```bash
   kubectl config set-context --current --namespace=kafka
```

5. Install Strimzi Operator: 

```bash
   kubectl create -f "https://strimzi.io/install/latest?namespace=kafka" -n kafka
```

6. Once installed, watch and wait until the operator pod is up and ready:


```bash
   kubectl get pods -n kafka -w
```

```
NAME                                        READY   STATUS    RESTARTS   AGE
strimzi-cluster-operator-64574988c8-vmqv8   1/1     Running   0          101s
```

7. Apply Kafka cluster configuration:

```bash
   kubectl apply -f k8s/cluster.yaml
```

8. Monitor the pods until all of them are up and ready (control+c to stop watching):

```bash
   kubectl get pods -n kafka -w
```

```
NAME                                            READY   STATUS    RESTARTS   AGE
kafka-cluster-brokers-100                       1/1     Running   0          102s
kafka-cluster-brokers-101                       1/1     Running   0          102s
kafka-cluster-controllers-0                     1/1     Running   0          102s
kafka-cluster-entity-operator-df9d74ccd-276d8   2/2     Running   0          61s
strimzi-cluster-operator-64574988c8-vmqv8       1/1     Running   0          19m
```

9. Deploy Kafka Clients (Producer & Consumer):

```bash
   kubectl apply -f k8s/clients.yaml
```

10. Check Producer Logs (in new terminal tab):

```bash
   kubectl logs -f deploy/kafka-demo-producer -n kafka
```

11. Follow the logs to verify the producer is sending (control+c to stop following):
```
{"@timestamp":"2025-10-19T12:46:53.624427052Z","level":"INFO","thread_name":"kafka-producer-network-thread | producer-1","logger_name":"at.kafka.on.k8s.producer.service.ProducerService","message":"Sent key=demo value=hello kafka 0 partition=1 offset=1"}
```

12. Check Consumer Logs (in new terminal tab):

```bash
   kubectl logs -f deploy/kafka-demo-consumer -n kafka
```

13. Follow the logs to verify the consumer is receiving (control+c to stop following):

```
{"@timestamp":"2025-10-19T12:47:46.695674424Z","level":"INFO","thread_name":"org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1","logger_name":"at.kafka.on.k8s.consumer.service.ConsumerService","message":"Received key=demo value=hello kafka 0"}
```

14. Check Stream Logs (in new terminal tab):

```bash
   kubectl logs -f deploy/kafka-demo-stream -n kafka
```

15. Follow the logs to verify consume → transform → produce:

```
{"@timestamp":"2025-10-20T09:56:41.268976303Z","level":"INFO","thread_name":"kafka-demo-stream-66990c7f-5872-4fb4-81e2-beefbaaf91b9-StreamThread-1","logger_name":"at.kafka.on.k8s.stream.topology.StreamTopology","message":"Received key=demo value=Event[id=demo, message=hello kafka 0] from topic=kafka-demo-topic"}
{"@timestamp":"2025-10-20T09:56:41.26911747Z","level":"INFO","thread_name":"kafka-demo-stream-66990c7f-5872-4fb4-81e2-beefbaaf91b9-StreamThread-1","logger_name":"at.kafka.on.k8s.stream.topology.StreamTopology","message":"Transformed key=demo value=Event[id=demo, message=hello women 0] sending to topic=kafka-demo-transformed"}
```

## Clean up

⚠️ Always wait until resources have been fully deleted; otherwise, they may remain stuck in a “terminating” state.

```bash
   kubectl delete -f k8s/clients.yaml
```
```bash
   kubectl delete -f k8s/cluster.yaml
```

Watch and wait until all pods (excluding operator pod) have been deleted (control+c to stop watching)

```bash
   kubectl get pods -n kafka -w
```
```bash
   kubectl delete -f "https://strimzi.io/install/latest?namespace=kafka"
```
```bash
   kubectl get pods -n kafka
```
Once all pods (including operator) have been deleted:

```bash
   kubectl delete namespace kafka
```

Wait and check if the namespace is gone:

```bash
   kubectl get namespaces
```

Mac / Unix:
```bash
   kubectl get crds | grep kafka
```

Windows:

```bash
   kubectl get crds | Select-String "kafka"
```


If you want to stop the local Kubernetes cluster, open Docker Desktop → Settings → Kubernetes and uncheck “Enable Kubernetes.”
This will fully stop the local k8s cluster.

If you want to completely remove Docker Desktop from your system, follow the official uninstall instructions:

👉 https://docs.docker.com/desktop/uninstall/