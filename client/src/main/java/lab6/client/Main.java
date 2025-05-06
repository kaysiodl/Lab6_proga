package lab6.client;

import lab6.client.commands.ExecuteScript;
import lab6.client.commands.Exit;
import lab6.client.commands.Help;
import lab6.common.managers.CommandManager;
import lab6.common.utility.console.StandartConsole;

import java.net.InetAddress;

public class Main {
    public static void main(String[] args) throws Exception {
        InetAddress serverAddress = InetAddress.getLocalHost();
        int serverPort = 13531;

        StandartConsole console = new StandartConsole();
        CommandManager commandManager = new CommandManager();
        NetworkManager networkManager = new NetworkManager(serverPort, serverAddress);
        Controller controller = new Controller(commandManager, console, networkManager);

        commandManager.register(new ExecuteScript(controller));
        commandManager.register(new Exit());
        commandManager.register(new Help(commandManager, controller));


        controller.run();
    }
}
