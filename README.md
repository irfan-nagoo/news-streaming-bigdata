# news-streaming-bigdata

![spark](https://github.com/irfan-nagoo/news-streaming-bigdata/assets/96521607/1211cd07-8eaa-4270-91f6-a6327aeb9f6f)


This Project is a collection of heterogenous applications which perform the real time Sentiment Analysis of the latest News data and expose a set of REST APIs to retrieve news with sentiment information. The latest news data is pulled from newsdata.io, is processed and stored in Bigdata database (Cassandra DB). This project provides modules for Apache Spark (4G technology of streaming processing) as well as Apache Flink (5G technology of stream processing). Anyone could be used as per requirement.


This project is a typical example of an open source Data Pipeline setup (above diagram is for illustration only). This project includes following components:

	1. news-streaming-app - A web application (war) which consist of scheduled tasks that pull news data from newsdata.io and publish it to Kafka topic. It also exposes set of REST APIs which give access to final processed information. Almost everything is configurable in this application.
	2. spark-analyzer     - Its a Apache Spark application (jar) which consumes raw news data from Kafka topic, does Sentiment analytics using Apache OpenNLP and loads that information in to Bigdata database (Cassandra DB). Check the README section of this module for steps to deploy this application on Spark.
	3. flink-analyzer     - Its a Apache Flink application (jar) which has the similar functionality as spark-analyzer but in Flink ecosystem. Check the README section of this module for steps to deploy this application on Flink.
	
	
A CQL script is also included as part of this project to create necessary tables and indexes in Cassandra. Here are the capabilities of the web app module:

	1. DataRetrieverTask/DataPurgerTask - The RetrieverTask is a scheduled task which run after every given interval and pulls latest News data from newsdata.io via a free REST API. A free API key needs to be generated on newsdata.io and used in the properties file. The newdata.io REST API is very highly used and sometimes returns "Connection Refused" error. A retry mechanism is setup to make the given number of retries in order to get the response. The Purger task purges a Cassandra partition before given days. The execution status of these tasks is logged in the audit table.
	2. A List API                       - This paginated and sorted API returns the latest (near real time) news with sentiment information.
	3. A Sentiment API                  - This paginated and sorted API returns the news as per given sentiment e.g. only Positive (Happy) news etc.
	4. A Search API                     - This paginated and sorted API return the title search news sentiment record as per given input query string.
	5. A Report API                     - This API returns the sentiment statistics of the given interval.
	
	
Tech Stack: Java 8, Spring, Spring Data JPA, Spring Retry, Lombok, Apache Kafka, Apache Spark, Apache Flink, Apache OpenNLP, Apache Cassandra, Apache Tomcat, Junit 5

	
Run infrastructure:

	1. Apache Kafka running (required topic created)
	2. Apache Cassandra running and tables created 
	3. Spark/Flink cluster running and application deployed
	3. newsdata.io API key placed in the properties file


Here is the Swagger url for various REST endpoints on local: http://localhost:8082/news-streaming-app/swagger-ui.html
