package lab6.server.commands;


import lab6.common.commands.Command;
import lab6.common.models.Person;
import lab6.common.network.Response;
import lab6.server.managers.CollectionManager;

public class UpdateId extends Command {
    private final CollectionManager collectionManager;

    public UpdateId(CollectionManager collectionManager) {
        super("update", "update the value of a collection item" + " whose id is equal to the specified one", true);
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
        try {
            Person old = collectionManager.getById(id);
            if (person != null) {
                person.validate();
                collectionManager.removeById(old.getId());
                collectionManager.add(person, old.getId());
                collectionManager.sort();

            }
        } catch (NullPointerException e) {
            return Response.builder().message("The fields of the person are not valid!").success(false).build();
        }
        return Response.success("Person updated successfully.\n");
    }

}
