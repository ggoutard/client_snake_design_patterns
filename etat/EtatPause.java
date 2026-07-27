package tp1progreseau.etat;

import tp1progreseau.controller.AbstractController;


public class EtatPause extends Etat {

    public EtatPause(AbstractController controller) 
    {
        super(controller);
    }



    @Override
    public void restart()
    {
        super.getController().setEtat(new EtatRunning(super.getController()));
        super.getController().restart();
    }

    @Override
    public void step()
    {
        super.getController().step();
    }

    @Override
    public void play()
    {
        super.getController().setEtat(new EtatRunning(super.getController()));
        super.getController().play();
    }

    @Override
    public void pause()
    {
        System.out.println("Action Impossible");
    }

}
