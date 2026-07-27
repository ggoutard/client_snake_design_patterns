package tp1progreseau.etat;

import tp1progreseau.controller.*;


public class EtatRunning extends Etat {

    public EtatRunning(AbstractController controller) 
    {
        super(controller);
    }

    @Override
    public void restart()
    {
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
        System.out.println("Action Impossible");
    }

    @Override
    public void pause()
    {
        super.getController().setEtat(new EtatPause(super.getController()));
        super.getController().pause();
    }

}
