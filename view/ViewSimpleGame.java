package tp1progreseau.view;

public class ViewSimpleGame extends ViewObserver {

    public ViewSimpleGame()
    {
        super();
    }
    
    @Override
    protected void init()
    {
        super.setTitle("Game");
        super.setSize(400, 300);

        super.add(super.affichage);

        super.setVisible(true);
    }

    
}
