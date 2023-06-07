package org.example.flinkanalyzer.analyzer;

import com.datastax.driver.core.utils.UUIDs;
import opennlp.tools.doccat.*;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.example.flinkanalyzer.domain.NewsObject;
import org.example.flinkanalyzer.entity.NewsSentiment;
import org.example.flinkanalyzer.helper.PropertyLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

/**
 * @author irfan.nagoo
 */
public class SentimentAnalyzer implements Analyzer<DataStream<NewsObject>, DataStream<NewsSentiment>>, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SentimentAnalyzer.class);

    private static final TokenizerME TOKENIZER_ME;
    private static final DoccatModel DOC_CATEGORY_MODEL;

    static {
        try {
            TOKENIZER_ME = getTokenizerME(PropertyLoader.INSTANCE.getProperty("org.example.flink-analyzer.analyzer.tokenFilePath"));
            DOC_CATEGORY_MODEL = getDoccatModel(PropertyLoader.INSTANCE.getProperty("org.example.flink-analyzer.analyzer.trainFilePath"));
        } catch (IOException e) {
            LOGGER.info("Exception occurred while initializing the Analyzer: ", e);
            throw new IllegalArgumentException("Invalid tokenizer or train file path", e);
        }
    }

    public SentimentAnalyzer() {

    }

    @Override
    public DataStream<NewsSentiment> analyze(DataStream<NewsObject> newsDS) {
        LOGGER.info("Initializing SentimentAnalyzer");
        // analyze and transform the input data stream to New Sentiment DS
        return newsDS.map(this::mapToNewsSentiment);
    }

    private NewsSentiment mapToNewsSentiment(NewsObject news) {
        NewsSentiment newsSentiment = new NewsSentiment();
        newsSentiment.setPartDate(news.getPubDate().toLocalDateTime().toLocalDate());
        newsSentiment.setPubDate(news.getPubDate().toLocalDateTime());
        newsSentiment.setId(UUIDs.timeBased());
        newsSentiment.setTitle(news.getTitle());
        newsSentiment.setLink(news.getLink());
        newsSentiment.setImageUrl(news.getImage_url());
        newsSentiment.setSentiment(findSentiment(news.getContent()));
        newsSentiment.setCreateTimestamp(LocalDateTime.now());
        return newsSentiment;
    }

    private String findSentiment(String input) {
        // find the sentiment of the input using Apache OpenNLP ML (Artificial Intelligence) library
        String[] tokens = TOKENIZER_ME.tokenize(input);
        DocumentCategorizerME documentCategorizerME = new DocumentCategorizerME(DOC_CATEGORY_MODEL);
        double[] result = documentCategorizerME.categorize(tokens);
        return documentCategorizerME.getBestCategory(result);
    }

    private static TokenizerME getTokenizerME(String tokenFilePath) throws IOException {
        return new TokenizerME(new TokenizerModel(getInputStream(tokenFilePath)));
    }

    private static DoccatModel getDoccatModel(String trainFilePath) throws IOException {
        // read plain text train input file
        InputStreamFactory isf = () -> getInputStream(trainFilePath);
        try (ObjectStream<DocumentSample> objStream = new DocumentSampleStream(new PlainTextByLineStream(isf,
                Charset.defaultCharset()))) {

            // customize doc category factory with feature generators for
            // processing words in a given input
            FeatureGenerator[] featureGenerators = new FeatureGenerator[]{new NGramFeatureGenerator(1, 1),
                    new NGramFeatureGenerator(2, 3), new BagOfWordsFeatureGenerator()};

            // Training parameters-
            //   CUTOFF_PARAM - Minimum number of words to be considered in a given category
            //   ITERATIONS_PARAM - Number of iteration to perform
            TrainingParameters trainingParameters = TrainingParameters.defaultParams();
            trainingParameters.put(TrainingParameters.CUTOFF_PARAM, 1);
            trainingParameters.put(TrainingParameters.ITERATIONS_PARAM, 10);
            return DocumentCategorizerME.train("en", objStream, trainingParameters, new DoccatFactory(featureGenerators));
        }
    }

    private static InputStream getInputStream(String modelFilePath) {
        return SentimentAnalyzer.class.getClassLoader().getResourceAsStream(modelFilePath);
    }


}
