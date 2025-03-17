package tp1progreseau.etat;

import tp1progreseau.controller.AbstractController;

public abstract class Etat {

    private final AbstractController controller;

    public Etat(AbstractController controller)
    {
        this.controller = controller;
    }

    protected AbstractController getController()
    {
        return this.controller;
    }

    public abstract void restart(); 
    public abstract void step();
    public abstract void play();
    public abstract void pause();
    
}
