package tp1progreseau.gameElement.fabrique;

import tp1progreseau.gameElement.GameElement;
import tp1progreseau.gameElement.Snake;
import tp1progreseau.utils.Features;
import tp1progreseau.utils.FeaturesSnake;

public class FabriqueSnake implements FabriqueGameElement {
    
    private static int counter = 0;

    @Override
    public GameElement build(Features features)
    {
        
        FeaturesSnake featuresSnake = (FeaturesSnake) features;

        Snake snake = new Snake(
            FabriqueSnake.counter,
            features,
            featuresSnake.getPositions(),
            featuresSnake.getLastAction(),
            TypeSnake.RANDOM

        );

        FabriqueSnake.counter ++;
        return snake;
        

    }

    public GameElement buildHuman(Features features)
    {
        
        FeaturesSnake featuresSnake = (FeaturesSnake) features;
        Snake snake = new Snake(
            FabriqueSnake.counter,
            features,
            featuresSnake.getPositions(),
            featuresSnake.getLastAction(),
            TypeSnake.HUMAN

        );

        FabriqueSnake.counter ++;
        return snake;
    
    }


    public GameElement buildIA(Features features)
    {
        
        FeaturesSnake featuresSnake = (FeaturesSnake) features;

        Snake snake = new Snake(
            FabriqueSnake.counter,
            features,
            featuresSnake.getPositions(),
            featuresSnake.getLastAction(),
            TypeSnake.IA

        );

        FabriqueSnake.counter ++;
        return snake;
    }


    public static void resetCounter()
    {
        FabriqueSnake.counter = 0;
    }
}
