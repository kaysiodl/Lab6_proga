package lab6.server.commands;


import lab6.common.commands.Command;
import lab6.common.models.Person;
import lab6.common.network.Response;
import lab6.server.managers.CollectionManager;

import java.util.Map;

public class RemoveGreater extends Command {
    private final CollectionManager collectionManager;

    public RemoveGreater(CollectionManager collectionManager) {
        super("remove_greater", "delete all items from the collection that exceed the specified item", false);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response apply(String[] args) {
        if (args[0].isEmpty()) {
            return Response.builder().message("This command accepts one argument.").success(false).build();
        }

        int greaterId = 0;
        try {
            greaterId = Integer.parseInt(args[0].trim());
        } catch (NumberFormatException e) {
            return Response.error("id hasn't correct format");
        }

        Map<Integer, Person> persons = collectionManager.getCollection();

        if (persons == null || persons.isEmpty()) {
            return Response.error("No persons to compare.\n");
        }

        try {
            int finalGreaterId = greaterId;
            collectionManager.sort().stream().filter(id -> id > finalGreaterId).forEach(collectionManager::removeById);
        } catch (Exception e) {
            return Response.builder().message("Failed to remove persons.").success(false).build();
        }

        return Response.success("Persons with greater ids removed successfully");
    }
}