package lab6.client.commands;

import lab6.client.Controller;
import lab6.common.commands.Command;
import lab6.common.network.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A command that reads and executes a script from a file.
 */
public class ExecuteScript extends Command {
    private final Controller controller;
    private final Set<Path> runningScripts = new HashSet<>();

    /**
     * Constructs an {@code ExecuteScript} command.
     *
     * @param controller The controller used to manage script execution.
     */

    public ExecuteScript(Controller controller) {
        super("execute_script", "reads and executes script from a file.", false);
        this.controller = controller;
    }

    @Override
    public Response apply(String[] args) {
        Path scriptPath = Path.of(args[0]);
        Response validation = validateScript(scriptPath);
        if (!validation.success()) {
            return validation;
        }

        runningScripts.add(scriptPath);

        try {
            List<String> lines = Files.readAllLines(scriptPath);
            controller.runScript(lines,
                    scriptPath.toString());
            return Response.success("Script executed successfully");
        } catch (IOException e) {
            return Response.error("Failed to read script: " + e.getMessage());
        } finally {
            runningScripts.remove(scriptPath);
        }
    }

    /**
     * Validates script file before execution
     */
    private Response validateScript(Path scriptPath) {
        if (!Files.exists(scriptPath)) {
            return Response.error("Script file not found");
        }
        if (!Files.isRegularFile(scriptPath)) {
            return Response.error("Path is not a file");
        }
        if (!Files.isReadable(scriptPath)) {
            return Response.error("No read permissions for script");
        }
        if (runningScripts.contains(scriptPath)) {
            return Response.error("Recursion detected in scripts");
        }
        return Response.builder().message("Script successfully executed.").build();
    }
}