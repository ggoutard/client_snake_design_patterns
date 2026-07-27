package tp1progreseau.view;

import java.awt.*;
import javax.swing.*;


public class ViewCommand extends ViewObserver  {

    private JButton btPause;
    private JButton btPlay;
    private JButton btRestart;
    private JButton btStep;
    private JSlider slider;

    public ViewCommand()
    {
        this.btPause = new JButton(new ImageIcon("src/tp1progreseau/icons/icon_pause.png"));
        this.btPlay = new JButton(new ImageIcon("src/tp1progreseau/icons/icon_play.png"));
        this.btRestart = new JButton(new ImageIcon("src/tp1progreseau/icons/icon_restart.png"));
        this.btStep = new JButton(new ImageIcon("src/tp1progreseau/icons/icon_step.png"));
        this.slider = new JSlider(0,10);
    }
    
    @Override
    protected void init()
    {
        super.setTitle("Command");
        super.setSize(800, 400);

        super.setLayout(new GridLayout(2,1));
        
        this.AjouterBoutons();
        this.AjouterControleurRetour();
    }

    private void AjouterBoutons()
    {
        JPanel division = new JPanel();
        division.setLayout(new GridLayout(1,4));
        
        division.add(this.btRestart);
        division.add(this.btPlay);
        division.add(this.btStep);
        division.add(this.btPause);
        super.add(division);

    }

    private void AjouterControleurRetour()
    {
        JPanel division = new JPanel();
        division.setLayout(new GridLayout(1,2));

        JPanel division_secondaire = new JPanel();
        division_secondaire.setLayout(new GridLayout(2,1));

        JLabel label = new  JLabel("Number of turn per second", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 15));
        division_secondaire.add(label);

        this.slider.setMajorTickSpacing(1);
        this.slider.setMinorTickSpacing(1);
        this.slider.setPaintTicks(true);
        this.slider.setPaintLabels(true);
        this.slider.setValue(1);
        division_secondaire.add(this.slider);

        super.affichage.setFont(new Font("Arial", Font.BOLD, 15));
        division.add(division_secondaire);
        division.add(super.affichage);

        super.add(division);

    }

    public JButton getBtPause()
    {
        return this.btPause;
    }

    public JButton getBtPlay()
    {
        return btPlay;
    }
    
    public JButton getBtRestart()
    {
        return btRestart;
    }
    
    public JButton getBtStep()
    {
        return btStep;
    }

    public JSlider getSlider()
    {
        return slider;
    }

}

