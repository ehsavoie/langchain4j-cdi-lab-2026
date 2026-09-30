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

    @Tool(description = "Rolls a number of dice and returns the results")
    public String roll(@ToolArg(description = "The number of dice") int numberOfDice) {
        logger.info("Dice roll: " + numberOfDice + " dice");
        int[] result = new int[numberOfDice];
        for (int i = 0; i < numberOfDice; i++) {
            result[i] = new Random().nextInt(1, 7);
            logger.info("Die " + i + ": " + result[i]);
        }
        return Arrays.toString(result);
    }
}
