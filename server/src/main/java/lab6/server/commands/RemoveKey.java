package lab6.server.commands;


import lab6.common.commands.Command;
import lab6.common.network.Response;
import lab6.server.managers.CollectionManager;

public class RemoveKey extends Command {
    private final CollectionManager collectionManager;

    public RemoveKey(CollectionManager collectionManager) {
        super("remove_key", "delete an item from the collection by its key", false);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response apply(String[] args) {
        if (args.length != 1 || args[0].trim().isEmpty()) {
            return Response.error("This command accepts one argument. Please try again.");
        }

        Integer id;
        try {
            id = Integer.parseInt(args[0].trim());
        } catch (NumberFormatException e) {
            return Response.error("Id must be a valid integer.");
        }

        try {
            if (collectionManager.getById(id) == null) {
                return Response.error("Can't find this id in collection.");
            }
            collectionManager.removeById(id);
            return Response.success("Element with id " + id + " removed successfully.\n");
        } catch (IllegalStateException e) {
            return Response.error("Can't find this id in collection.");
        }
    }
}