package lab6.server.commands;


import lab6.common.commands.Command;
import lab6.common.models.Person;
import lab6.common.network.Response;
import lab6.server.managers.CollectionManager;

public class Insert extends Command {
    private final CollectionManager collectionManager;

    public Insert(CollectionManager collectionManager) {
        super("insert", "add a new item with the specified key", true);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response apply(String[] args, Person person) {
        if (args[0].isEmpty()) {
            return Response.builder().message("Type person's id.").success(false).build();
        }
        int id;
        try {
            id = Integer.parseInt(args[0].trim());
        } catch (NumberFormatException e) {
            return Response.builder().message("Id has incorrect format. Try again.").success(false).build();
        }
        if (collectionManager.getById(id) != null)
            return Response.builder().message("Entity with this id already exists.").success(false).build();
        collectionManager.add(person, id);
        return Response.builder().message("New element added to collection successfully.").build();
    }
}