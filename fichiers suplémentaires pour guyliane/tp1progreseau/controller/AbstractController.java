package tp1progreseau.controller;

import tp1progreseau.etat.Etat;

public abstract class AbstractController {
    
    private Etat etat;

    public abstract void restart(); 
    public abstract void step();
    public abstract void play();
    public abstract void pause();
    public abstract void setSpeed(double speed);



    public void setEtat(Etat etat)
    {
        this.etat = etat;
    }

    public Etat getEtat()
    {
        return this.etat;
    }

}
