package org.example.newsstreaming.producer;

/**
 * @author irfan.nagoo
 */
public interface AsyncMessageProducer<T> {

    void sentMessage(T object);
}
