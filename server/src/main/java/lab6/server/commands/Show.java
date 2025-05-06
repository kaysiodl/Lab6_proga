package lab6.server.commands;


import lab6.common.commands.Command;
import lab6.common.exceptions.ExecutionError;
import lab6.common.models.Person;
import lab6.common.network.Response;
import lab6.server.managers.CollectionManager;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Show extends Command {
    private final CollectionManager collectionManager;

    public Show(CollectionManager collectionManager) {
        super("show", "output all the elements of the collection in a string representation", false);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response apply(String[] args) throws ExecutionError {

        List<Integer> sortedKeys = collectionManager.sort();

        if (sortedKeys.isEmpty()) {
            return Response.error("Collection is empty.");
        }

        String output = sortedKeys.stream().map(collectionManager.getCollection()::get).filter(Objects::nonNull).map(Person::toString).collect(Collectors.joining("\n"));
        return Response.success("Collection of people:\n" + output);
    }
}
