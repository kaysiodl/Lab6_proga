package lab6.common.managers;


import lab6.common.commands.Command;
import lab6.common.exceptions.NotFoundCommandException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CommandManager implements Serializable {
    private final List<Command> commands = new ArrayList<>();
    private final List<Command> commandHistory = new ArrayList<>();

    public void register(Command command){
        commands.add(command);
    }

    public void addToHistory(Command command){
        commandHistory.add(command);
        if (commandHistory.size() > 13) {
            commandHistory.remove(0);
        }
    }

    public Command getCommand(String commandName){
        if (commandName == null || commandName.trim().isEmpty()) {
            throw new NotFoundCommandException("Command name can't be empty.");
        }

        return commands.stream()
                .filter(command -> commandName.equalsIgnoreCase(command.getName()))
                .findFirst()
                .orElseThrow(() -> new NotFoundCommandException(commandName));
    }

    public List<Command> getHistory(){
        return new ArrayList<>(commandHistory);
    }

    public List<Command> getCommands() {
        return commands;
    }
}
