package tp1progreseau.model;
import java.util.Observable;

public abstract class Game extends Observable implements Runnable {

    protected int turn;
    private int maxturn;
    protected boolean isRunning;
    private long time = 1000;
    protected Thread thread;

    Game(int maxturn)
    {
        this.maxturn = maxturn;
    }
        

    public void init()
    {
        this.turn = 0;
        this.isRunning = true;
        
    }

    @Override
    public void run()
    {
        while(this.isRunning)
        {

            this.step(); 
        }
    }


    public void step()
    {

        System.out.println("Test1");

        if(this.gameContinue())
        {
            System.out.println("Test2");
            if(this.turn < this.maxturn)
            {
                System.out.println("Test3");

                ++this.turn;
                this.takeTurn();
            }
            else 
            {
                this.isRunning = false;
                this.gameOver();
            }
            this.setChanged();
            this.notifyObservers();
        }

        try 
        {
            Thread.sleep(this.time); 
        } 
        catch (InterruptedException e) {
            System.out.println(e);
        }
    }

    public void pause()
    {
        try {
            this.isRunning = false;
            this.thread.join(); 
        } 
        catch (InterruptedException e) {
            System.out.println(e);
        }
    }

    public void Launch()
    {
        this.isRunning = true;
        this.thread = new Thread(this);
        this.thread.start();
    }
    

    public boolean getIsRunning()
    {
        return this.isRunning;
    }

    public int getTurn()
    {
        return this.turn;
    }
    public int getMaxturn()
    {
        return this.maxturn;
    }

    public void restart()
    {
        this.turn = 0;
        this.thread.interrupt();
        this.pause();
        this.initializeGame();
    }


    public void setTime(double time)
    {
        this.time = (long) time;
        System.out.println(time);
    }

    public double getTime()
    {
        return this.time;
    }

    public abstract void initializeGame();
    public abstract void takeTurn();
    public abstract boolean gameContinue();
    public abstract void gameOver();
}
