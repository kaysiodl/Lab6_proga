package lab6.common.commands;

import lab6.common.models.Person;
import lab6.common.network.Response;

import java.io.Serializable;
import java.util.Objects;

/**
 * An abstract base class for all commands.
 */
public abstract class Command implements Serializable {
    private final String name;
    private final String description;
    private final boolean requiresObject;

    /**
     * Constructs a {@code Command} with the specified name and description.
     *
     * @param name        The name of the command.
     * @param description The description of the command.
     */
    public Command(String name, String description, boolean requiresObject) {
        this.name = name;
        this.description = description;
        this.requiresObject = requiresObject;
    }

    /**
     * Returns the name of the command.
     *
     * @return The name of the command.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the command.
     *
     * @return The description of the command.
     */
    public String getDescription() {
        return description;
    }

    public Response apply(String[] args) {
        return Response.builder().success(true).build();
    }

    public Response apply(String[] args, Person person) {
        return Response.builder().success(true).build();
    }

    public boolean getRequiresObject() {
        return requiresObject;
    }

    /**
     * Checks if this command is equal to another object.
     *
     * @param o The object to compare with.
     * @return {@code true} if the objects are equal, otherwise {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command command = (Command) o;
        return Objects.equals(name, command.name) && Objects.equals(description, command.description);
    }

    /**
     * Returns the hash code of the command.
     *
     * @return The hash code of the command.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }

    /**
     * Returns a string representation of the command.
     *
     * @return A string representation of the command.
     */
    @Override
    public String toString() {
        return "Command{" + "name='" + name + '\'' + ", description='" + description + '\'' + '}';
    }
}