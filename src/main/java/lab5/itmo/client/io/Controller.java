package lab5.itmo.client.io;

import lab5.itmo.client.CommandManager;
import lab5.itmo.client.commands.Command;
import lab5.itmo.client.io.console.StandartConsole;
import lab5.itmo.exceptions.ExecutionError;
import lab5.itmo.exceptions.NotFoundCommandException;

import java.util.Arrays;
import java.util.Objects;

public class Controller {
    private final StandartConsole console;
    private final CommandManager commandManager;

    public Controller(CommandManager commandManager, StandartConsole console) {
        this.console = console;
        this.commandManager = commandManager;
    }

    public void run() {
        String input;
        try {
            while (true) {
                console.print("Enter the command: ");
                input = console.read();
                if (input == null) {
                    break;
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
            boolean isSuccess = parseInput(input);
            return input + (isSuccess ? " successfully" : " unsuccessfully") + " completed";
        } catch (ExecutionError e) {
            return "Execution error: " + e.getMessage();
        } catch (NotFoundCommandException e) {
            return "Error: " + e.getMessage() + " Type help to get info about commands.";
        }
    }

    private boolean parseInput(String input) throws ExecutionError, NotFoundCommandException {
        String[] data = input.split(" ");
        String commandName;
        if (data.length>=2 && (Objects.equals(data[1], "id") && Objects.equals(data[0], "update") || Objects.equals(data[1], "null")
                && Objects.equals(data[0], "insert"))){
            commandName = data[0] + " " + data[1];
        }
        else{
            commandName = data[0];
        }
        if (commandName.equals("update id") && data.length < 3) {
            throw new ExecutionError("Missing argument for 'update id'.");
        }
        if (commandName.equals("insert null") && data.length < 3) {
            throw new ExecutionError("Missing argument for 'insert null'.");
        }
        Command command = commandManager.getCommand(commandName);
        if (command == null) {
            throw new NotFoundCommandException("Command '" + commandName + "' is not found.");
        }
        commandManager.addToHistory(command);
        String[] args = data.length > 1 ? Arrays.copyOfRange(data, 1, data.length) : new String[0];
        return command.apply(args);
    }
}