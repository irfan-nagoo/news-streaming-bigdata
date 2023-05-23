package org.example.newsstreaming.exception;

/**
 * @author irfan.nagoo
 */
public class RecordNotFoundException extends RuntimeException{

    public RecordNotFoundException(String message){
        super(message);
    }
}
