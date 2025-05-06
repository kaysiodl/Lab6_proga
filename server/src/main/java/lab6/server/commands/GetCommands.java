package lab6.server.commands;

import lab6.common.commands.Command;
import lab6.common.commands.CommandInfo;
import lab6.common.managers.CommandManager;
import lab6.common.network.Response;

import java.io.Serializable;
import java.util.List;

public class GetCommands extends Command implements Serializable {
    private final CommandManager serverCommandManager;

    public GetCommands(CommandManager serverCommandManager) {
        super("get_commands", "returns list of available server commands", false);
        this.serverCommandManager = serverCommandManager;
    }

    @Override
    public Response apply(String[] args) {
        List<Command> commands = serverCommandManager.getCommands();
        List<CommandInfo> commandInfo = commands.stream().map(command -> new CommandInfo(command.getName(), command.getDescription(), command.getRequiresObject())).toList();
        System.out.println(commandInfo);
        return Response.builder().data(commandInfo).build();
    }
}