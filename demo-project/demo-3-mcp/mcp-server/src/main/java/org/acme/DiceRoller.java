package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import org.mcp_java.annotations.tools.Tool;
import org.mcp_java.annotations.tools.ToolArg;

import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

@SuppressWarnings("unused")
@ApplicationScoped
public class DiceRoller {

    private static final Logger logger = Logger.getLogger(DiceRoller.class.getName());

    @Tool(description = "Lance un nombre de dés et retourne les résultats")
    public String roll(@ToolArg(description = "Le nombre de dés") int numberOfDice) {
        logger.info("Dice rolled: " + numberOfDice + " dice ");
        int[] result = new int[numberOfDice];
        for (int i = 0; i < numberOfDice; i++) {
            result[i] = new Random().nextInt(1, 7);
            logger.info("Dice: " + i + " give " + result[i]);
        }
        return Arrays.toString(result);
    }
}
