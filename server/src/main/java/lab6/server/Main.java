package lab6.server;

import lab6.common.managers.CommandManager;
import lab6.server.commands.*;
import lab6.server.managers.CollectionManager;
import lab6.server.network.RequestHandler;
import lab6.server.network.Server;
import lab6.server.network.StandartRequestHandler;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.System.exit;

public class Main {
    public static void main(String[] args) {
        CommandManager commandManager = new CommandManager();
        CommandManager serverCommandManager = new CommandManager();
        Path collectionPath = null;
        try {
            collectionPath = Path.of(args[0]);
            if (!Files.exists(collectionPath)) {
                System.err.println("File not found: " + collectionPath);
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("Type name of the file");
            exit(0);
        }
        CollectionManager collectionManager = new CollectionManager(collectionPath);

        collectionManager.loadCollection(collectionPath);

        commandManager.register(new AddRandom(collectionManager));
        commandManager.register(new Clear(collectionManager));
        commandManager.register(new History(commandManager));
        commandManager.register(new CountLessThanNationality(collectionManager));
        commandManager.register(new FilterGreaterThanHairColor(collectionManager));
        commandManager.register(new Info(collectionManager));
        commandManager.register(new Insert(collectionManager));
        commandManager.register(new RemoveGreater(collectionManager));
        commandManager.register(new RemoveKey(collectionManager));
        commandManager.register(new Show(collectionManager));
        commandManager.register(new UpdateId(collectionManager));
        commandManager.register(new ReplaceIfGreater(collectionManager));

        serverCommandManager.register(new GetCommands(commandManager));
        serverCommandManager.register(new Save(collectionManager));


        RequestHandler requestHandler = new StandartRequestHandler(commandManager, serverCommandManager);

        Server server;
        try {
            server = new Server(new InetSocketAddress(InetAddress.getLocalHost(), 13531), requestHandler);
            new Thread(server).start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
