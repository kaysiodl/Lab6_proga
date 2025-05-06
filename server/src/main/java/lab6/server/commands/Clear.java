package lab6.server.commands;


import lab6.common.commands.Command;
import lab6.common.network.Response;
import lab6.server.managers.CollectionManager;

/**
 * A command that clears the collection.
 */
public class Clear extends Command {
    private final CollectionManager collectionManager;

    /**
     * Constructs a {@code Clear} command.
     *
     * @param collectionManager The collection manager whose collection will be cleared.
     */
    public Clear(CollectionManager collectionManager) {
        super("clear", "clear the collection", false);
        this.collectionManager = collectionManager;
    }

    /**
     * Executes the command by clearing the collection.
     *
     * @return {@code true} if the collection was cleared successfully.
     */
    @Override
    public Response apply(String[] args) {
        collectionManager.removeAll();
        return Response.builder().message("Collection cleared").build();
    }
}