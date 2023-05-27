package org.example.flinkanalyzer.deserializer;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.flinkanalyzer.domain.NewsObject;

import java.io.IOException;

/**
 * This value deserializer converts Kafka messages to NewObject objects
 *
 * @author irfan.nagoo
 */
public class JsonDeserializationSchema implements DeserializationSchema<NewsObject> {

    private static ObjectMapper objectMapper;

    @Override
    public NewsObject deserialize(byte[] message) throws IOException {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
        }
        return objectMapper.readValue(message, NewsObject.class);
    }

    @Override
    public boolean isEndOfStream(NewsObject nextElement) {
        return false;
    }

    @Override
    public TypeInformation<NewsObject> getProducedType() {
        return TypeInformation.of(NewsObject.class);
    }
}
