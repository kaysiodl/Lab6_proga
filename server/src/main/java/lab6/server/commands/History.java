package lab6.server.commands;


import lab6.common.commands.Command;
import lab6.common.exceptions.ExecutionError;
import lab6.common.managers.CommandManager;
import lab6.common.network.Response;

import java.util.List;

/**
 * A command that prints the last 13 executed commands.
 */
public class History extends Command {
    private final CommandManager commandManager;

    public History(CommandManager commandManager) {
        super("history", "print the last 13 commands", false);
        this.commandManager = commandManager;
    }

    @Override
    public Response apply(String[] args) {
        List<Command> commandHistory = commandManager.getHistory();

        if (commandHistory.isEmpty()) {
            try {
                return Response.builder().message("Command history is empty.").build();
            } catch (Exception e) {
                throw new ExecutionError("Failed to write to console: " + e.getMessage());
            }
        }

        int commandsToShow = Math.min(commandHistory.size(), 13);

        StringBuilder historyOutput = new StringBuilder("Last " + commandsToShow + " commands:\n");
        for (int i = commandHistory.size() - commandsToShow; i < commandHistory.size(); i++) {
            historyOutput.append("  ").append(commandHistory.get(i)).append("\n");
        }

        return Response.builder().message(historyOutput.toString()).build();
    }
}