package lab6.common.commands;

import java.io.Serializable;

public record CommandInfo(String commandName, String Description, boolean requiresObject) implements Serializable {

}
