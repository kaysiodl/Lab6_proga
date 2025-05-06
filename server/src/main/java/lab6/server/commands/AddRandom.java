package lab6.server.commands;

import lab6.common.commands.Command;
import lab6.common.exceptions.ExecutionError;
import lab6.common.models.*;
import lab6.common.network.Response;
import lab6.server.managers.CollectionManager;

import java.util.Random;


/**
 * A command that adds a randomly generated element to the collection.
 */
public class AddRandom extends Command {
    private final CollectionManager collectionManager;
    private final Random random = new Random();

    /**
     * Constructs an {@code AddRandom} command.
     *
     * @param collectionManager The collection manager to which the random element will be added.
     */
    public AddRandom(CollectionManager collectionManager) {
        super("add_random", "adds a random element to collection.", false);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response apply(String[] args) {
        try {
            int count;
            if (args.length == 0) count = 1;
            else count = Integer.parseInt(args[0]);

            for (int i = 0; i < count; i++) {
                addRandom();
            }

            return Response.builder().message("%d entities added".formatted(count)).build();
        } catch (NumberFormatException e) {
            throw new ExecutionError("Type a number of entities you want to add.");
        } catch (ExecutionError e) {
            throw new ExecutionError(e.getMessage());
        }
    }

    public void addRandom() throws ExecutionError {
        try {
            Person person = new Person("person_" + random.nextInt(1000), new Coordinates(random.nextInt(-20, 20), random.nextInt(-20, 20)), (float) random.nextDouble(0.1, 160.0), Color.BLUE, Color.WHITE, Country.FRANCE, new Location((float) random.nextDouble(), (long) random.nextInt(-20, 20), (long) random.nextInt(-20, 20), "location_" + random.nextInt(0, 20)));
            collectionManager.add(person);
        } catch (ExecutionError e) {
            throw new ExecutionError(e.getMessage());
        }
    }
}