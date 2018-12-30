package ru.silverhammer.injection.data;

import java.util.Collection;
import java.util.List;

public class InjectMe {

    private String message;
    private final int number;
    private Collection<String> collection;

    public InjectMe(Number number, List<String> list) {
        this.number = number.intValue();
        collection = list;
    }

    public String getMessage() {
        return message;
    }

    public int getNumber() {
        return number;
    }

    public Collection<String> getCollection() {
        return collection;
    }

    public void setValues(String message, Collection<String> collection) {
        this.message = message;
        this.collection = collection;
    }
}
