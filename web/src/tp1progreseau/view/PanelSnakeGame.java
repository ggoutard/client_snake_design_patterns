package tp1progreseau.view;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

import tp1progreseau.utils.*;


/**
 * Classe qui permet de charger d'afficher le panneau du jeu à partir d'une carte et de listes d'agents avec leurs positions.
 *
 * Version web : images lues une seule fois depuis le jar et mises à la taille d'une case une seule fois
 * (les redimensionner à chaque dessin est trop lent dans la VM), cases carrées centrées.
 */


public class PanelSnakeGame extends JPanel{

	private static final long serialVersionUID = 1L;

	private static final Map<String, BufferedImage> IMAGES = new HashMap<String, BufferedImage>();


	protected Color ground_Color= new Color(0,0,0);


	private int sizeX;
	private int sizeY;

	private int fen_x;
	private int fen_y;

	private double stepx;
	private double stepy;

	private double origin_x;
	private double origin_y;


	float[] contraste = { 0, 0, 0, 1.0f };


	protected ArrayList<FeaturesSnake> featuresSnakes = new ArrayList<FeaturesSnake>();
	protected ArrayList<FeaturesItem> featuresItems = new ArrayList<FeaturesItem>();


	private boolean[][] walls;

	private final Map<String, BufferedImage> tiles = new HashMap<String, BufferedImage>();
	private int tileSize;


	int cpt;

	public PanelSnakeGame(int sizeX, int sizeY, boolean[][] walls, ArrayList<FeaturesSnake> featuresSnakes, ArrayList<FeaturesItem> featuresItems) {

		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.walls = walls;
		this.featuresSnakes = featuresSnakes;
		this.featuresItems = featuresItems;

	}

	static BufferedImage image(String name) {
		synchronized (IMAGES) {
			BufferedImage img = IMAGES.get(name);
			if (img == null) {
				try {
					img = ImageIO.read(PanelSnakeGame.class.getResource("/tp1progreseau/images/" + name + ".png"));
				} catch (IOException | IllegalArgumentException e) {
					e.printStackTrace();
				}
				IMAGES.put(name, img);
			}
			return img;
		}
	}

	/** Image à la taille d'une case, filtre de couleur éventuel compris, calculée une seule fois. */
	BufferedImage tile(String name, float[] scales) {
		int size = Math.max(1, (int)Math.ceil(stepx));
		if (size != tileSize) {
			tiles.clear();
			tileSize = size;
		}
		String key = scales == null ? name : name + Arrays.toString(scales);
		BufferedImage img = tiles.get(key);
		if (img == null) {
			BufferedImage src = image(name);
			img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
			if (src != null) {
				Graphics2D g2 = img.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g2.drawImage(src, 0, 0, size, size, null);
				g2.dispose();
				if (scales != null) img = new RescaleOp(scales, contraste, null).filter(img, null);
			}
			tiles.put(key, img);
		}
		return img;
	}

	public void paint(Graphics g){

		fen_x = getSize().width;
		fen_y = getSize().height;

		// cases carrées, plateau centré
		double step = Math.min(fen_x/(double)sizeX, fen_y/(double)sizeY);
		this.stepx = step;
		this.stepy = step;
		this.origin_x = (fen_x - step*sizeX) / 2;
		this.origin_y = (fen_y - step*sizeY) / 2;

		g.setColor(ground_Color);
		g.fillRect(0, 0,fen_x,fen_y);

		double position_x=origin_x;

		for(int x=0; x<sizeX; x++)
		{
			double position_y = origin_y ;

			for(int y=0; y<sizeY; y++)
			{
				if (walls[x][y]){
					g.drawImage(tile("wall", null), (int)position_x, (int)position_y, this);
				}

				position_y+=stepy;
			}
			position_x+=stepx;
		}

		ArrayList<FeaturesSnake> snakes = this.featuresSnakes;
		ArrayList<FeaturesItem> items = this.featuresItems;

		for(int i = 0; i < snakes.size(); i++){
			paint_Snake(g,snakes.get(i));
		}

		for(int i = 0; i < items.size(); i++){
			paint_Item(g,items.get(i));
		}


		cpt++;
	}


	void paint_Snake(Graphics g, FeaturesSnake featuresSnake)
	{

		ArrayList<Position> positions = featuresSnake.getPositions();

		AgentAction lastAction = featuresSnake.getLastAction();


		double pos_x;
		double pos_y;

		int cpt_img = -1;


		for(int i = 0; i < positions.size(); i++) {

			pos_x=origin_x+positions.get(i).getX()*stepx;
			pos_y=origin_y+positions.get(i).getY()*stepy;


			if(i == 0) {
				switch (lastAction) {
				case MOVE_UP:
					cpt_img = 0;
					break;
				case MOVE_DOWN:
					cpt_img = 1;
					break;
				case MOVE_RIGHT:
					cpt_img = 2;
					break;
				case MOVE_LEFT:
					cpt_img = 3;
					break;

				default:
					break;
				}

			} else {
				cpt_img = 4;
			}

			String name = null;
			if(featuresSnake.getColorSnake() == ColorSnake.Green) {
				name = "snake_green_" + cpt_img;
			} else if(featuresSnake.getColorSnake() == ColorSnake.Red ) {
				name = "snake_red_" + cpt_img;
			}


			float [] scales = null;

			if (featuresSnake.isInvincible())

				scales = new float[]{3 ,0.75f, 3, 1.0f };

			if (featuresSnake.isSick())
				scales = new float[]{1.5f ,1.5f, 0.75f, 1.0f };


			if(name != null) {
				g.drawImage(tile(name, scales), (int)pos_x, (int)pos_y, this);
			}

		}

	}



	void paint_Item(Graphics g, FeaturesItem featuresItem){

		int x = featuresItem.getX();
		int y = featuresItem.getY();

		double pos_x=origin_x+x*stepx;
		double pos_y=origin_y+y*stepy;

		String name = null;

		if (featuresItem.getItemType() == ItemType.APPLE) name = "apple";

		if (featuresItem.getItemType() == ItemType.BOX) name = "mysteryBox";

		if (featuresItem.getItemType() == ItemType.SICK_BALL) name = "sickBall";

		if (featuresItem.getItemType() == ItemType.INVINCIBILITY_BALL) name = "invicibleBall";

		if (name != null) g.drawImage(tile(name, null), (int)pos_x, (int)pos_y, this);

	}

	public void updateInfoGame( ArrayList<FeaturesSnake> featuresSnakes , ArrayList<FeaturesItem> featuresItems) {

		this.featuresSnakes = featuresSnakes;
		this.featuresItems = featuresItems;

	}

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

}
