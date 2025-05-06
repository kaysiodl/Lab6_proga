package lab6.server.commands;


import lab6.common.commands.Command;
import lab6.common.exceptions.ExecutionError;
import lab6.common.models.Color;
import lab6.common.models.Person;
import lab6.common.network.Response;
import lab6.server.managers.CollectionManager;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A command that prints elements whose hair color is greater than the specified value.
 */
public class FilterGreaterThanHairColor extends Command {
    private final CollectionManager collectionManager;

    /**
     * Constructs a {@code FilterGreaterThanHairColor} command.
     *
     * @param collectionManager The collection manager whose collection will be processed.
     */
    public FilterGreaterThanHairColor(CollectionManager collectionManager) {
        super("filter_greater_than_hair_color",
                "print elements whose value of the hair color field is greater than the specified value", false);
        this.collectionManager = collectionManager;
    }

    /**
     * Executes the command by filtering and printing elements with hair color greater than the specified value.
     *
     * @return {@code true} if the command executed successfully.
     * @throws ExecutionError If the argument is missing or invalid.
     */
    @Override
    public Response apply(String[] args) {
        if (args.length < 1) {
            throw new ExecutionError("This command requires one argument: hair color.");
        }

        Color specifiedHairColor;
        try {
            specifiedHairColor = Color.valueOf(args[0].trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ExecutionError("Invalid hair color. Available values: BLUE, ORANGE, WHITE, BROWN.");
        }

        Collection<Person> people = collectionManager.getCollection().values();
        List<String> result = people.stream()
                .filter(Objects::nonNull)
                .map(Person::getHairColor)
                .filter(Objects::nonNull)
                .filter(hairColor -> hairColor.compareTo(specifiedHairColor) > 0)
                .map(Color::toString)
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            return Response.error("No people with such hair color found");
        }

        return Response.success("Matching people:", result);
    }
}