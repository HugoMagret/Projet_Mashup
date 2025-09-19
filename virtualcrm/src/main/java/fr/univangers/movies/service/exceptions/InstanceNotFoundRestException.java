package fr.univangers.movies.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class InstanceNotFoundRestException extends Exception {

    private Object instanceId;
    private String instanceType;

    public InstanceNotFoundRestException(Object instanceId, String instanceType) {

        super("Instance not found (identifier = '" + instanceId + "' - type = '"
                + instanceType + "')");
        this.instanceId = instanceId;
        this.instanceType = instanceType;

    }

    public Object getInstanceId() {
        return instanceId;
    }

    public String getInstanceType() {
        return instanceType;
    }
}
