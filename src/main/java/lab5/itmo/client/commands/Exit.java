package lab5.itmo.client.commands;

import lab5.itmo.exceptions.ExecutionError;

import static java.lang.System.exit;

public class Exit extends Command{
    public Exit(){
        super("exit", "exit the program (without saving)");
    }

    @Override
    public boolean apply(String[] args) throws ExecutionError {
        if (args.length > 0) {
            throw new ExecutionError("This command does not accept any arguments.");
        }

        exit(0);
        return true;
    }
}
