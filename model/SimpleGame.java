package tp1progreseau.model;

public class SimpleGame extends Game {
    
    public SimpleGame(int maxturn)
    {
        super(maxturn);
    }

    @Override
    public void initializeGame(){}

    @Override
    public void takeTurn()
    {
        System.out.println("Tours "+Integer.toString(super.getTurn()) + "  du jeu en cours");
    }

    @Override
    public boolean gameContinue()
    {  
        return true;
    }

    @Override
    public void gameOver()
    {
        System.out.println("Le jeu est terminé");

    }
    
}
