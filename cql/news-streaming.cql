CREATE KEYSPACE IF NOT EXISTS news_analytics WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE news_analytics;

CREATE TABLE IF NOT EXISTS news_sentiment (
	part_date date,
	id timeuuid,
	title text,
	link text,
	image_url text,
	pub_date timestamp,
	sentiment text,
	create_timestamp timestamp,
	PRIMARY KEY (part_date, pub_date)
) WITH CLUSTERING ORDER BY (pub_date DESC);

CREATE INDEX IF NOT EXISTS news_sentiment_sentiment_index ON news_sentiment(sentiment);

-- Thw SASI index type is experimental as the Cassandra docs and not should not be used in prod
CREATE CUSTOM INDEX news_sentiment_title_index ON news_sentiment (title) USING 'org.apache.cassandra.index.sasi.SASIIndex'
WITH OPTIONS = {
'mode': 'CONTAINS',
'analyzer_class': 'org.apache.cassandra.index.sasi.analyzer.NonTokenizingAnalyzer',
'case_sensitive': 'false'
};

CREATE TABLE IF NOT EXISTS news_scheduler_audit (
	id timeuuid PRIMARY KEY,
	task_name text,
	start_timestamp timestamp,
	end_timestamp timestamp,
	status text,
);

CREATE INDEX IF NOT EXISTS news_scheduler_audit_task_name_index ON news_scheduler_audit(task_name);