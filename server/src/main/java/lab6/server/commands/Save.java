package lab6.server.commands;

import lab6.common.commands.Command;
import lab6.common.network.Response;
import lab6.server.managers.CollectionManager;

import java.io.IOException;

public class Save extends Command {
    private final CollectionManager collectionManager;

    public Save(CollectionManager collectionManager) {
        super("save", "saves collection to a file", false);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response apply(String[] args) {
        try {
            collectionManager.saveCollection();
            collectionManager.backUpManager.deleteBackUpFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Response.builder()
                .message("Collection saved")
                .build();
    }
}
