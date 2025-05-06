package lab6.server.network;

import lab6.common.commands.Command;
import lab6.common.exceptions.NotFoundCommandException;
import lab6.common.managers.CommandManager;
import lab6.common.models.Person;
import lab6.common.network.Request;
import lab6.common.network.Response;

import java.util.Locale;

public class StandartRequestHandler extends RequestHandler {
    private final CommandManager commandManager;
    private final CommandManager serverCommandManager;

    public StandartRequestHandler(CommandManager commandManager, CommandManager serverCommandManager) {
        this.commandManager = commandManager;
        this.serverCommandManager = serverCommandManager;
    }

    @Override
    public Response handleRequest(Request request) {
        String commandName = request.command().toLowerCase(Locale.ROOT);
        String[] args = request.args();
        Person person = request.person();
        try {
            Command command;
            try {
                command = serverCommandManager.getCommand(commandName);
            } catch (NotFoundCommandException e) {
                command = commandManager.getCommand(commandName);
            }
            if (command.getRequiresObject()) return command.apply(args, person);
            else return command.apply(args);
        } catch (Exception e) {
            return Response.builder()
                    .message(e.getMessage())
                    .build();
        }
    }
}
