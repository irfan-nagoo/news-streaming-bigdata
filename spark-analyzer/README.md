# spark-analyzer

This Spark application consumes raw news data from Kafka topic, does Sentiment analytics using Apache OpenNLP and loads that information to Bigdata database (Cassandra DB). Download and install Apache Spark from internet. Here are the steps to deploy this application on Spark cluster (Windows standalone):

1. Run Spark master node first using this command:

            C:/spark-3.4.0-bin-hadoop3/bin/spark-class.cmd org.apache.spark.deploy.master.Master 

      This will print the Spark url in the startup logs and in the Spark console: http://localhost:8080/

2. Run Spark worker node with the Spark url from previous step:

            C:/spark-3.4.0-bin-hadoop3/bin/spark-class.cmd org.apache.spark.deploy.worker.Worker spark://<IP>:<PORT>
            
      The Spark worker is accessible at: http://localhost:4040/
            
3. Now, Spark cluster is up. Deploy the spark-analyzer (jar) on Apache Spark using:

            C:/spark-3.4.0-bin-hadoop3/bin/spark-submit.cmd \
						--class org.example.sparkanalyzer.SparkAnalyzer \
						--master spark://<IP>:<PORT> \
						--deploy-mode cluster \
						--packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.4.0,org.apache.opennlp:opennlp-tools:1.8.1,com.datastax.spark:spark-cassandra-connector_2.12:3.3.0,com.github.jnr:jnr-posix:3.1.16 \
						file:///C:/news-streaming-bigdata/spark-analyzer/target/spark-analyzer.jar

4. From Spark console, check the application logs to make sure there are no errors while startup
