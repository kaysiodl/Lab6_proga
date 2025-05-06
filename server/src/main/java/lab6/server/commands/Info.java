package lab6.server.commands;


import lab6.common.commands.Command;
import lab6.common.exceptions.ExecutionError;
import lab6.common.network.Response;
import lab6.server.managers.CollectionManager;

import java.time.LocalDateTime;

/**
 * A command that outputs information about the collection.
 */
public class Info extends Command {
    private final CollectionManager collectionManager;

    /**
     * Constructs an {@code Info} command.
     *
     * @param collectionManager The collection manager providing the collection information.
     */
    public Info(CollectionManager collectionManager) {
        super("info", "output information about the collection (type, initialization date, number of items, etc.)", false);
        this.collectionManager = collectionManager;
    }

    /**
     * Executes the command by printing information about the collection.
     *
     * @param args The arguments passed to the command (not used).
     * @return {@code true} if the command executed successfully.
     * @throws ExecutionError If an error occurs during execution.
     */
    @Override
    public Response apply(String[] args) {
        String collectionType = collectionManager.getCollection().getClass().getSimpleName();
        LocalDateTime lastInitTime = collectionManager.getLastInitTime();
        int collectionSize = collectionManager.getCollection().size();

        String info = String.format("""
                Collection information:
                  Type: %s
                  Initialization date: %s
                  Number of elements: %d
                """, collectionType, lastInitTime, collectionSize);

        return Response.builder().message(info).build();
    }
}