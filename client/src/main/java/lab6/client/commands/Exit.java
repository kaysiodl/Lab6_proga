package lab6.client.commands;


import lab6.common.commands.Command;
import lab6.common.network.Response;


/**
 * A command that exits the program without saving.
 */
public class Exit extends Command {

    public Exit() {
        super("exit", "exit the program", false);
    }

    @Override
    public Response apply(String[] args) {
        System.exit(0);
        return Response.builder().message("Exit from program").build();
    }
}