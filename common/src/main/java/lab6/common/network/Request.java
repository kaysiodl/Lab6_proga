package lab6.common.network;

import lab6.common.models.Person;

import java.io.Serializable;

public record Request(String command, String[] args, Person person) implements Serializable {

}
