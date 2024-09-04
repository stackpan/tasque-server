package io.github.stackpan.tasque.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DtoFieldException extends RuntimeException {

    private Map<String, List<String>> errors = new HashMap<>();

    public DtoFieldException() {
    }

    public void addError(String field, String message) {
        if (!errors.containsKey(field)) {
            errors.put(field, new ArrayList<>());
        }

        errors.get(field).add(message);
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }

    public static DtoFieldException withInitial(String field, String message) {
        var exception = new DtoFieldException();
        exception.addError(field, message);
        return exception;
    }
}
