/*  
* Wizard Game Tutorial
* ====================
* see README.md for details.
* fixed collision detection
* 
*/  
package game.wizard;
 
import org.newdawn.slick.Animation;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

/**
 * @author panos (original tutorial)
 * updated by CannonFodder
 * 
 */
public class WizardGame extends BasicGame
{
    private TiledMap grassMap;
    private Animation sprite, up, down, left, right;
    // initial position
    private float x = 1*32f, y = 4*32f;
    
    // The collision map is indicating which tiles block movement - generated based
    // on tile properties
    private boolean[][] blocked;
    
    // size of tiles and sprite is 32x32
    private static final int SIZE = 32;
    private int myDelta;        // The amount of time in milliseconds passed since last update  
    
    public WizardGame()
    {
        super("Wizard game");
    }
    
    
    public static void main(String[] arguments)
    {
        try
        {
            AppGameContainer app = new AppGameContainer(new WizardGame());
            app.setDisplayMode(640, 640, false);
            app.setShowFPS(false);
            //app.setTargetFrameRate(160);
            app.start();
        }
        catch (SlickException e)
        {
            e.printStackTrace();
        }
    }
    
    
    @Override
    public void init(GameContainer container) throws SlickException
    {
        // Map, created with Tiled
        // only gzip/zlib base64 supported
        grassMap = new TiledMap("data/grassmap20x20_01.tmx");
        
        // SMALL Wizard was the base for the stuff above!
        // Create arrays of Image class
        // public Image(java.lang.String ref)
        // ref - The location of the image file to load
        // variation would be to use boolean flipped
        Image [] movementUp    = {new Image("data/wmg1_bk1.png"), new Image("data/wmg1_bk2.png")};
        Image [] movementDown  = {new Image("data/wmg1_fr1.png"), new Image("data/wmg1_fr2.png")};
        Image [] movementLeft  = {new Image("data/wmg1_lf1.png"), new Image("data/wmg1_lf2.png")};
        Image [] movementRight = {new Image("data/wmg1_rt1.png"), new Image("data/wmg1_rt2.png")};
        
        // The duration in ms to show each of the 2 frames        
        int[] duration = { 300, 300 };
        
        // public Animation(Image[] frames, int[] duration, boolean autoUpdate)
        // frames - The images for the animation frames
        // duration - The duration to show each frame
        // autoUpdate - True if this animation should automatically update.
        //              This means that the current frame will be calculated based
        //              on the time between renders

        up = new Animation(movementUp, duration, false);
        down = new Animation(movementDown, duration, false);
        left = new Animation(movementLeft, duration, false);
        right = new Animation(movementRight, duration, false);
        // Original orientation of the sprite. It will look right.
        sprite = right;
        
        // build a collision map based on tile properties in the Tiled map
        System.out.println("Size of grassMap (x * y): " + grassMap.getWidth() + " x " + grassMap.getHeight());
        blocked = new boolean[grassMap.getWidth()][grassMap.getHeight()];
        for (int xAxis = 0; xAxis < grassMap.getWidth(); xAxis++) {
            for (int yAxis = 0; yAxis < grassMap.getHeight(); yAxis++) {
                int tileID = grassMap.getTileId(xAxis, yAxis, 0);
                String value = grassMap.getTileProperty(tileID, "blocked", "false");
                if (value.equals("true")) {
                    System.out.println("Found blocked Tile at (x , y) : " + xAxis + " , " + yAxis);
                    blocked[xAxis][yAxis] = true;
                }
            }
        }
    }
    
    
    // Update the game logic here.
    // No rendering should take place in this method though it won't do any harm. 
    // delta - The amount of time in milliseconds passed since last update
    // Reduced SIZE by one pixel for collision detection!
    @Override
    public void update(GameContainer container, int delta) throws SlickException
    {
        Input input = container.getInput();
        
        myDelta = delta;
        // The lower the delta the slower the sprite will animate.
		
        // delta is normally 1 ms ...
        
        // x,y = 0,0 is top left corner!
        // move up, decrease y
        if (input.isKeyDown(Input.KEY_UP)) {
            sprite = up;
            if (!isBlocked(x, y - delta * 0.1f) && !isBlocked(x + (SIZE - 1), y - delta * 0.1f)) {
                sprite.update(delta);
                y -= delta * 0.1f;
            }
        }
        // move down, increase y
        else if (input.isKeyDown(Input.KEY_DOWN)) {
            sprite = down;
            if (!isBlocked(x, y + (SIZE - 1) + delta * 0.1f) && !isBlocked(x + (SIZE - 1) , y + (SIZE - 1)  + delta * 0.1f)) {
                sprite.update(delta);
                y += delta * 0.1f;
            }
        }
        // move left, decrease x
        else if (input.isKeyDown(Input.KEY_LEFT)) {
            sprite = left;
            if (!isBlocked(x - delta * 0.1f, y) && !isBlocked(x - delta * 0.1f, y + (SIZE - 1))) {
                sprite.update(delta);
                x -= delta * 0.1f;
            }
        }
        // move right, increase x
        else if (input.isKeyDown(Input.KEY_RIGHT)) {
            sprite = right;
            if (!isBlocked(x + (SIZE - 1) + delta * 0.1f, y) && !isBlocked(x + (SIZE - 1) + delta * 0.1f, y + (SIZE - 1))) {
                sprite.update(delta);
                x += delta * 0.1f;
            }
        }
    }
    
    
    public void render(GameContainer container, Graphics g) throws SlickException
    {
        grassMap.render(0, 0);
        sprite.draw((int) x, (int) y);
        int xBlock = (int) (x / SIZE);
        int yBlock = (int) (y / SIZE);
        
        // Display the location of the wizard and other debug infos
        // public void drawString(java.lang.String str, float x, float y)
        // str - The string to draw
        // y - The y coordinate to draw the string at
        // x - The x coordinate to draw the string at
        g.drawString("X: " + x + " Y: " + y, 10f, 10f);
        g.drawString("Delta: " + myDelta, 10f, 55f);        // container.getDelta() is protected!
        g.drawString("xBlock: " + xBlock + ", yBlock: " + yBlock, 10f, 25f);
        g.drawString("blocked= " + blocked[xBlock][yBlock], 10f, 40f);
    }
    
    
    // is called during update
    private boolean isBlocked(float x, float y)
    {
        int xBlock = (int) (x / SIZE);
        int yBlock = (int) (y / SIZE);
        
        if(xBlock >= (grassMap.getWidth()) || yBlock >= (grassMap.getHeight())){
        	//System.out.println("+BorderBLOCKED @ xBlock: " + xBlock + ", yBlock: " + yBlock + "| X: " + x + " Y: " + y);
            return true;
        }
        
        // problem with casting small negative values to int...
        if(xBlock < 0 || yBlock < 0 || x < 0 || y < 0){
        	//System.out.println("-BorderBLOCKED @ xBlock: " + xBlock + ", yBlock: " + yBlock + "| X: " + x + " Y: " + y);
        	return true;
        }
        
        if (blocked[xBlock][yBlock]) {
        	//System.out.println("BLOCKED @ xBlock: " + xBlock + ", yBlock: " + yBlock + "| X: " + x + " Y: " + y);
        	return true;
        }
        
        return false;
    }
}
