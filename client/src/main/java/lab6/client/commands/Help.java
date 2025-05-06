package lab6.client.commands;

import lab6.client.Controller;
import lab6.common.commands.Command;
import lab6.common.commands.CommandInfo;
import lab6.common.exceptions.ExecutionError;
import lab6.common.managers.CommandManager;
import lab6.common.network.Response;

import java.util.Collection;
import java.util.List;

/**
 * A command that lists all available commands and their descriptions.
 */
public class Help extends Command {
    private final CommandManager commandManager;
    private final Controller controller;

    /**
     * Constructs a {@code Help} command.
     *
     * @param commandManager The command manager providing the list of commands.
     */
    public Help(CommandManager commandManager, Controller controller) {
        super("help", "list of all available commands", false);
        this.commandManager = commandManager;
        this.controller = controller;
    }

    @Override
    public Response apply(String[] args) throws ExecutionError {
        try {
            List<Command> clientCommands = commandManager.getCommands();
            StringBuilder helpBuilder = new StringBuilder("Available commands:\n");
            clientCommands.forEach(cmd ->
                    helpBuilder.append(" • ")
                            .append(cmd.getName())
                            .append(" - ")
                            .append(cmd.getDescription())
                            .append("\n"));

            Collection<?> serverCommands = controller.serverCommands;
            if (serverCommands != null) {
                for (Object cmdObj : serverCommands) {
                    if (cmdObj instanceof CommandInfo) {
                        CommandInfo cmdInfo = (CommandInfo) cmdObj;
                        helpBuilder.append(" • ")
                                .append(cmdInfo.commandName())
                                .append(" - ")
                                .append(cmdInfo.Description())
                                .append("\n");
                    }
                }
            }
            return Response.success(helpBuilder.toString());

        } catch (Exception e) {
            return Response.error("Failed to display help: " + e.getMessage());
        }
    }
}