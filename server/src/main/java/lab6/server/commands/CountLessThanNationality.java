package lab6.server.commands;


import lab6.common.commands.Command;
import lab6.common.exceptions.ExecutionError;
import lab6.common.models.Country;
import lab6.common.models.Person;
import lab6.common.network.Response;
import lab6.server.managers.CollectionManager;

import java.util.Collection;
import java.util.Objects;

/**
 * A command that counts the number of elements in the collection
 * whose nationality field value is less than the specified value.
 */
public class CountLessThanNationality extends Command {
    private final CollectionManager collectionManager;

    /**
     * Constructs a {@code CountLessThanNationality} command.
     *
     * @param collectionManager The collection manager whose collection will be processed.
     */
    public CountLessThanNationality(CollectionManager collectionManager) {
        super("count_less_than_nationality", "print the number of elements whose value of the nationality field is less than the specified value", false);
        this.collectionManager = collectionManager;
    }

    /**
     * Executes the command by counting the number of elements
     * whose nationality is less than the specified value.
     *
     * @return {@code true} if the command executed successfully.
     * @throws ExecutionError If the argument is missing or invalid.
     */
    @Override
    public Response apply(String[] args) {
        if (args.length < 1) {
            throw new ExecutionError("This command requires one argument: nationality.");
        }

        Country specifiedNationality;
        try {
            specifiedNationality = Country.valueOf(args[0].trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ExecutionError("Invalid nationality. Available values: FRANCE, SPAIN, THAILAND.");
        }

        Collection<Person> people = collectionManager.getCollection().values();

        long count = people.stream().map(Person::getNationality).filter(Objects::nonNull).filter(nationality -> nationality.compareTo(specifiedNationality) < 0).count();

        return Response.builder().success(true).message("Found " + count + " people with nationality < " + specifiedNationality).build();
    }
}