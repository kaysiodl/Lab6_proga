package lab6.client;

import lab6.common.commands.Command;
import lab6.common.commands.CommandInfo;
import lab6.common.exceptions.NotFoundCommandException;
import lab6.common.managers.AskManager;
import lab6.common.managers.CommandManager;
import lab6.common.models.Person;
import lab6.common.network.Request;
import lab6.common.network.Response;
import lab6.common.network.Serializer;
import lab6.common.utility.console.StandartConsole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Controller {
    private final StandartConsole console;
    private final CommandManager commandManager;
    private final NetworkManager networkManager;
    private final List<String> launchedScripts = new ArrayList<>();
    public final Collection<?> serverCommands;

    public Controller(CommandManager commandManager, StandartConsole console, NetworkManager networkManager) {
        this.console = console;
        this.commandManager = commandManager;
        this.networkManager = networkManager;
        this.serverCommands = fetchServerCommands();
    }

    public void addLaunchedScript(String name) {
        launchedScripts.add(name);
    }

    public List<String> getLaunchedScripts() {
        return launchedScripts;
    }

    public void clearLaunchedScripts() {
        this.launchedScripts.clear();
    }

    public void removeLaunchedScript(String filePath) {
        launchedScripts.remove(filePath);
    }

    public void run() {
        String input;
        try {
            while (true) {
                console.print("Enter the command: ");
                input = console.read().trim();
                if (input.isEmpty()) {
                    console.println("Error: Command name can't be empty.");
                    continue;
                }
                try {
                    String result = handleInput(input);
                    console.println(result);
                } catch (Exception e) {
                    console.println("Error: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            console.println("Error: " + e.getMessage());
        }
    }

    public String handleInput(String input) {
        try {
            parseInput(input);
            return "";
        } catch (NotFoundCommandException e) {
            return e.getMessage() + " Type help to see all available commands.";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public void runScript(List<String> lines, String scriptName) {
        for (String line : lines) {
            console.println(line.trim());
            console.println(handleInput(line.trim()));
        }
        launchedScripts.remove(scriptName);
    }

    private void parseInput(String input) throws Exception {
        String[] data = input
                .trim()
                .replace("\t", " ")
                .split("\\s+");

        String commandName = data[0];
        String[] args = Arrays.copyOfRange(data, 1, data.length);
        try {
            Command command = this.commandManager.getCommand(commandName);
            Response response = command.apply(args);
            printResponse(response);
        } catch (NotFoundCommandException e) {
            Request request;
            if (commandRequiresObject(commandName)) {
                Person person = AskManager.askPerson(console);
                request = new Request(commandName, args, person);
            } else {
                request = new Request(commandName, args, null);
            }
            Response response = makeRequest(request);
            printResponse(response);
        }
    }


    private void printResponse(Response response) {
        if (response == null) {
            console.println("Error: Received null response from server");
            return;
        }

        if (response.message() != null) {
            console.println(response.message());
        }

        if (!response.success()) {
            console.println("Warning: The command was not executed successfully");
        }

        if (response.data() != null) {
            console.println(response.data().toString());
        }
    }

    private Response makeRequest(Request request) throws Exception {
        networkManager.sendData(Serializer.serializeObject(request));
        return (Response) Serializer.deserialazeObject(networkManager.receiveData());
    }

    private Collection<?> fetchServerCommands() {
        try {
            Request request = new Request("get_commands", new String[0], null);
            Response response = makeRequest(request);

            if (response.success() && response.data() != null) {
                return response.data();
            }
        } catch (Exception ignored) {

        }
        return null;
    }

    public boolean commandRequiresObject(String commandName) {
        try {
            Command localCommand = commandManager.getCommand(commandName);
            if (localCommand != null) {
                return localCommand.getRequiresObject();
            }
        } catch (NotFoundCommandException ignored) {}

        if (serverCommands != null) {
            for (Object cmdObj : serverCommands) {
                if (cmdObj instanceof CommandInfo cmdInfo &&
                        cmdInfo.commandName().equalsIgnoreCase(commandName)) {
                    return cmdInfo.requiresObject();
                }
            }
        }

        return false;
    }
}
