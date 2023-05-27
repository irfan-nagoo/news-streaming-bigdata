# flink-analyzer

This Flink application consumes raw news data from Kafka topic, does Sentiment analytics using Apache OpenNLP and loads that information to Bigdata database (Cassandra DB). Download and install Apache Flink from internet. Here are the steps to deploy this application on Flink cluster (Windows standalone):

1. In C:/flink-1.17.0/bin/flink-daemon.sh file, remove these two classpath constants (along with colon) if they are not required: 

            SQL_GATEWAY_CLASSPATH, INTERNAL_HADOOP_CLASSPATHS
            
2. In C:/flink-1.17.0/conf/flin-conf.yaml file, add this property:  

            taskmanager.resource-id: TaskManager_1
            
3. In the git bash/cygwin CLI, set this porperty:

            export FLINK_CONF_DIR=C:/flink-1.17.0/conf
            
4. Now, Run this command to start Flink cluster: 

           C:/Softwares/flink-1.17.0/bin/start-cluster.sh
           
     The Flink admin consle is accessible at: http://localhost:8081
        
5.  In the admin console, go to "Submit New Job" and upload newly created flink-analyzer.

6.  Click on the record and click "Submit" button to deploy the application.

7.  From admin console or Flink log file directory, check the application logs to make sure there are no errors while startup.       

