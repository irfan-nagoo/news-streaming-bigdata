package org.example.flinkanalyzer.consumer;

/**
 * @author irfan.nagoo
 */
public interface Consumer<O> {

    O consume();
}
