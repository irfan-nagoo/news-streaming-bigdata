package org.example.flinkanalyzer.persister;

/**
 * @author irfan.nagoo
 */
public interface Persister<I> {

    void persist(I input);
}
