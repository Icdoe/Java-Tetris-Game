import java.awt.Font;
import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Tetris extends JPanel
{
    private static final int LEVEL_MIN = 4;
    private static final int LEVEL_MAX = 10;
    private static final int LEVEL_DEFAULT = 7;
    private static final int TIME_BASE = 214;
    private final Point origin = new Point(0, 0);  

    private final Color baseColor = new Color(149, 161, 155, 255);      // the base color of well
    
    private Point offsetPoint;
    private Color[][] well;
    
    private Piece currentPiece;
    private Piece nextPiece;
    
    private int minOffsetY;     // The row where the highest small piece is located
    private int level;
    private int score;
    private boolean firstStart;
    private boolean pause;
    private boolean gameover;

    {
        init();
        firstStart = true;
        pause = true;
    }//init
    

    public boolean isPause()
    {
        return pause;
    }

    // The time required for each automatic fall
    public int sleepTime()
    {
        return TIME_BASE + (LEVEL_DEFAULT - level) * (LEVEL_DEFAULT / level + 1) * 37;
    }
    
    //Change the value of program running status Pause/Continue/Restart
    public void changeRunning()
    {
        if(!gameover)   
            pause = !pause;
        else
            init();
        repaint();
    }
    
    // Change the difficulty of the this
    public void changeLevel(int i, boolean loop)
    {
        if(this.pause) return;
            
        if(loop)
        {
            this.level = LEVEL_MIN + (level + i - LEVEL_MIN) % (LEVEL_MAX - LEVEL_MIN + 1);
            if(this.level < LEVEL_MIN)
                this.level = LEVEL_MAX;
        }
        else
        {
            int level = this.level + i;
            if(level > LEVEL_MAX)
                level = LEVEL_MAX;
            else if(level < LEVEL_MIN)
                level = LEVEL_MIN;
            
            this.level = level; 
        }
    }

    // Move the piece left or right
    public void move(int i) 
    {
        if(pause) return;
        
        if(!collidesAt(offsetPoint.x + i, offsetPoint.y)) 
            offsetPoint.x += i;
        //rotationCorrect();

        repaint();
    }
    

    // Rotate the piece clockwise or anticlockwise
    public void rotate(int i) 
    {
        if(pause) return;
        
        int reg = offsetPoint.x;
        
        currentPiece.rotate(i);
        rotationCorrect();   // It will be possible to modify the value of offsetPoint.x
        
        if(collidesAt(offsetPoint.x, offsetPoint.y))
        {
            currentPiece.undoRotate();
            offsetPoint.x = reg;
        }

        repaint();
    }

    // Drops the piece one line or fixes it to the well if it can't drop
    public void dropDown()
    {
        if(pause) return;
        
        if(!collidesAt(offsetPoint.x, offsetPoint.y + 1))
            offsetPoint.y += 1;
        else
            fixToWell();
        
        repaint();
    }
        
    // Make the piece fall directly to the lowest point.
    public void dropBottom()
    {
       if(pause) return;
       
       int coffset = offsetPoint.y;
       while(true)
       {
           if(!collidesAt(offsetPoint.x, offsetPoint.y + 1))
                offsetPoint.y += 1;
           else
           {
               score += (int) (level * 3.0 / LEVEL_DEFAULT  * (offsetPoint.y - coffset) / 2);
               fixToWell();
               break;
           }
       }
       
       repaint();
    }
    
    // Clear rows that have been spelled out.
    public void clearCompleteRows() 
    {
        boolean gap;
        int numClears = 0;
        
        for(int i = offsetPoint.y; i < offsetPoint.y + currentPiece.height(); i++)
        {                                                                                        
            gap = false;                                                                                                                           
            for(int j = 0; j < Cons.COLUMN; j++)                                                 
            {
               if(well[i][j].equals(baseColor))
               {
                    gap = true;
                    break;
               }
            }
            if (!gap) 
            {
               deleteRow(i);
               numClears += 1;
            }
        }
    
        calScore(numClears);
    }

    // Update the current piece and create the next piece.
    public void newPiece() 
    {
        currentPiece = nextPiece;  
        nextPiece = new Piece();
        
        offsetPoint.x = Cons.COLUMN / 2;
        offsetPoint.y = -currentPiece.height();
    }
    
    // Keystroke listener
    public void setListener()
    {
        this.addKeyListener(new KeyListener() 
        {
            public void keyTyped(KeyEvent e) {}
            
            public void keyPressed(KeyEvent e) 
            {
                switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    rotate(1);
                    break;
                case KeyEvent.VK_DOWN:
                    rotate(-1);
                    break;
                case KeyEvent.VK_LEFT:
                    move(-1);
                    break;
                case KeyEvent.VK_RIGHT:
                    move(+1);
                    break;
                case KeyEvent.VK_SPACE:
                    dropDown();
                    break;
                case KeyEvent.VK_ENTER:
                    changeRunning();
                    break;
                case KeyEvent.VK_NUMPAD0:
                    dropBottom();
                    break;
                case KeyEvent.VK_NUMPAD2:
                    changeLevel(-1, false);
                    break;
                case KeyEvent.VK_NUMPAD1:
                    changeLevel(1, false);
                    break;
                case KeyEvent.VK_NUMPAD7:
                    if(!firstStart)
                        init();
                    break;
                case KeyEvent.VK_ESCAPE:
                    System.exit(0);
                    break;
                } 
            }
            
            public void keyReleased(KeyEvent e) {}
        });
    }
    
    
    public void launch()
    {
        new Thread() 
        {
            @Override public void run() 
            {
                while (true) 
                {
                    try 
                    {
                        requestFocus();
                        Thread.sleep(sleepTime());
                        if(!isPause())
                            dropDown();
                    } catch (InterruptedException e ) {}
                }
            }
        }.start();
    }
    
    
    @Override 
    public void paintComponent(Graphics g)
    {
        if(this.firstStart)
        {
            drawInit(g);
            if(!this.pause)
                this.firstStart = false;
            return;
        }
            
        // Fill background color
        g.setColor(new Color(76, 75, 75, 255));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        // Paint the well
        drawWell(g);
        // Draw the currently falling piece
        drawCurrentPiece(g);
        // Draw additional information
        drawInfo(g);   
            
        if(this.gameover)     
            drawgameover(g);
        else if(this.pause)
            drawPause(g);
    }
    

    private void init()
    {
        score = 0;
        level = LEVEL_DEFAULT;
        pause = false;
        gameover = false;
        minOffsetY = Cons.ROW;
        
        currentPiece = new Piece();
        nextPiece = new Piece();
        offsetPoint = new Point(Cons.COLUMN / 2 - 1, -currentPiece.height() - 2);

        well = new Color[Cons.ROW][Cons.COLUMN];
        for(int i = 0; i < Cons.ROW; i++)
            for(int j = 0; j < Cons.COLUMN; j++) 
                well[i][j] = baseColor;
    }
    
    // Make the dropping piece part of the well, so it is available for
    // collision detection.
    private void fixToWell() 
    {
        if(offsetPoint.y < 0)
        {
            pause = true;
            gameover = true;
            return;
        }
            
        for(Point p : currentPiece.pointRange()) 
        {
            well[p.y + offsetPoint.y][p.x + offsetPoint.x] = currentPiece.color;
        }
        
        minOffsetY = offsetPoint.y < minOffsetY ? offsetPoint.y : minOffsetY;
            
        clearCompleteRows();
        newPiece();
    }


    private void deleteRow(int row) 
    {
        for(;row >= minOffsetY; row--)
            for(int j = 0; j < Cons.COLUMN; j++)
            {
                well[row][j] = well[row - 1][j];
            }
        this.minOffsetY += 1;
    }
    
    
    private void calScore(int numClears)
    {
        switch (numClears) 
        {
            case 1:
                score += (int)((this.level * 1.0 / LEVEL_DEFAULT) * 100);
                break;
            case 2:
                score += (int)((this.level * 1.0 / LEVEL_DEFAULT) * 300);
                break;
            case 3:
                score += (int)((this.level * 1.0 / LEVEL_DEFAULT) * 500);
                break;
            case 4:
                score += (int)((this.level * 1.0 / LEVEL_DEFAULT)* 800);
                break;
        }
    }
       
    
    // Detect whether the falling block has collided.
    // Ignore colliding the left and right walls.
    private boolean collidesAt(int x, int y) 
    {   
        for(Point p : currentPiece.pointRange()) 
        {
            if(p.y + y >= Cons.ROW)             // Collision bottom
                return true;
            
            if(p.x + x < 0 || p.x + x >= Cons.COLUMN)   // Collision to the left or right of the well
                return true;

            if(p.y + y >= 0 &&                 // The current small piece has entered the well
                !well[p.y + y][p.x + x].equals(baseColor))    
                    return true;
        }
        
        return false;
    }
    
    // If you follow the top-down, left-to-right principle to arrange each small block, 
    // then min_x is the x of the first point, and max_x is equal to the x of the last point.
    // Only correct the value of offsetPoint.x!
    private int rotationCorrect()
    {
        int min_x, max_x;
        int ans_x;
        //int reg = offsetPoint.x;
        min_x = max_x = currentPiece.pointRange()[0].x;
        for(Point p : currentPiece.pointRange()) 
        {
            if(p.x < min_x)                     // Collide the left wall
                min_x = p.x;
                
            if(p.x > max_x)                 
                max_x = p.x;
        }
        
        if(min_x + offsetPoint.x < 0)
            offsetPoint.x += -(min_x + offsetPoint.x);
        
        if(max_x + offsetPoint.x >= Cons.COLUMN)
            offsetPoint.x -= ((max_x + offsetPoint.x) % Cons.COLUMN + 1);
        
        return offsetPoint.x;
    }

    
    private void drawInit(Graphics g)
    {
        g.setColor(new Color(76, 75, 75, 255));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        
        g.setColor(new Color(161, 149, 151, 255));
        g.fillRect(0, 0, Cons.COLUMN * Cons.SIZE, Cons.ROW * Cons.SIZE);
        
        g.setColor(Color.BLACK);
        g.setFont(new Font("Lucida Handwriting", 0, 21));
        g.drawString("Press \"Enter\" to start",
                        (Cons.COLUMN / 8) * Cons.SIZE, (Cons.ROW / 2) * Cons.SIZE);
        
        g.setFont(new Font("Perpetua",0, 14));
        g.drawString("Thank you for running!",
                        Cons.SIZE, Cons.SIZE);
        g.drawString("By creator Icdoe",
                        4 * Cons.SIZE, (int) 5.0 / 2 * Cons.SIZE);
        
        drawPrompt(g, (Cons.COLUMN / 8));
        
    }
    
    // Paint the well
    private void drawWell(Graphics g)
    {
        for(int i = origin.x; i < Cons.ROW; i++) 
        {
            for(int j = origin.y; j < Cons.COLUMN; j++) 
            {   
                int size = Cons.SIZE;
                if(!well[i][j].equals(baseColor))
                    size = Cons.SIZE - 1;
                    
                g.setColor(well[i][j]);
                g.fillRect(Cons.SIZE * j, Cons.SIZE * i, 
                            size, size);
            }
            
            g.setColor(Color.BLACK);
            g.fillRect(Cons.SIZE * 1, Cons.SIZE * 1,
                        Cons.SIZE, Cons.SIZE);
        }
    }
    
    // Draw the currently falling piece
    private void drawCurrentPiece(Graphics g) 
    {       
        g.setColor(currentPiece.color);
        for (Point p : currentPiece.pointRange()) 
        {
            g.fillRect((p.x + offsetPoint.x) * Cons.SIZE, 
                       (p.y + offsetPoint.y) * Cons.SIZE, 
                       Cons.SIZE - 1 , Cons.SIZE - 1);
        }
    }   
   
    
    private void drawInfo(Graphics g)
    {
        int xoffset = 2;
        drawScore(g, xoffset);
        drawLevel(g, xoffset);
        drawNextPiece(g, xoffset);
        drawPrompt(g, xoffset);
    }
    

    private void drawScore(Graphics g, int xoffset)
    {
        g.setFont(new Font("Time New Roman", Font.BOLD + Font.ITALIC, 26));
        g.setColor(Color.BLACK);
        g.drawString("Score: " + score,
                        Cons.SIZE * (Cons.COLUMN + xoffset),
                        Cons.SIZE );
    }
    
    
    private void drawLevel(Graphics g, int xoffset)
    {
        g.setFont(new Font("Time New Roman", Font.BOLD + Font.ITALIC, 23));
        g.setColor(Color.BLACK);
        g.drawString("Level: " + level, 
                        Cons.SIZE * (Cons.COLUMN + xoffset), 
                        Cons.SIZE * 2 );
    
    }
    
    
    private void drawNextPiece(Graphics g, int xoffset)
    {
        int yoffset = 4;
        int side_length = 5;
        g.setFont(new Font("Time New Roman", Font.BOLD, 21));
        g.setColor(new Color(4, 0, 13));
        g.drawString("Next Piece",
                        Cons.SIZE * (Cons.COLUMN + xoffset),
                        Cons.SIZE * yoffset);
        g.drawRect(Cons.SIZE * (Cons.COLUMN + xoffset) , Cons.SIZE * (yoffset + 1),
                    side_length * Cons.SIZE, side_length * Cons.SIZE);
        
        g.setColor(nextPiece.color);
        for(Point p : nextPiece.pointRange())
        {
            g.fillRect(Cons.SIZE * (p.x + Cons.COLUMN + xoffset + 1),
                        Cons.SIZE * (p.y + yoffset + 2), Cons.SIZE - 1, Cons.SIZE - 1);
        }
    }
    
    // Draw help information
    private void drawPrompt(Graphics g, int xoffset)
    {
        int yoffset =  12;
        int i = 0;
        g.setFont(new Font("Time New Roman", Font.BOLD , 16));
        g.setColor(new Color(159, 172, 116));
        g.drawString("¡ü     Clockwise rotation", 
                        Cons.COLUMN * (Cons.SIZE + xoffset),
                        Cons.SIZE * (yoffset + i++) );
        g.drawString("¡ý     Anticlockwise rotation",
                        Cons.COLUMN * (Cons.SIZE + xoffset), 
                        Cons.SIZE * (yoffset + i++));
        g.drawString("¡û     Move left",
                        Cons.COLUMN * (Cons.SIZE + xoffset), 
                        Cons.SIZE * (yoffset + i++));
        g.drawString("¡ú     Move right",
                        Cons.COLUMN * (Cons.SIZE + xoffset),
                        Cons.SIZE * (yoffset + i++));
        g.drawString("0      Land immediately",
                        Cons.COLUMN * (Cons.SIZE + xoffset),
                        Cons.SIZE * (yoffset + i++));
        g.drawString("1      Difficulty upgrade",
                        Cons.COLUMN * (Cons.SIZE + xoffset),
                        Cons.SIZE * (yoffset + i++));
        g.drawString("2      Difficulty decrease",
                        Cons.COLUMN * (Cons.SIZE + xoffset),
                        Cons.SIZE * (yoffset + i++));
        g.drawString("SPACE  Accelerated fall", 
                        Cons.COLUMN * (Cons.SIZE + xoffset),
                        Cons.SIZE * (yoffset + i++));
        i++;
        g.drawString("Press \"Enter\" for pause/continue",
                        Cons.COLUMN * (Cons.SIZE + xoffset),
                        Cons.SIZE * (yoffset + i++));
        g.drawString("Number \"7\" for restart.", 
                        Cons.COLUMN * (Cons.SIZE + xoffset), 
                        Cons.SIZE * (yoffset + i++));
        
    }
    
    
    private void drawPause(Graphics g)
    {
        g.setColor(new Color(95, 79, 83, 209));
        g.fillRect(origin.x * Cons.SIZE, origin.y * Cons.SIZE,
                    Cons.SIZE * (origin.x + 4), Cons.SIZE * (origin.y + 2));
                    
        g.setColor(Color.BLACK);
        g.setFont(new Font("Bubblegum Sans", Font.BOLD, 21));
        g.drawString("Paused", Cons.SIZE * (origin.x + 1),
                        Cons.SIZE * (origin.y + 1));
    }
    
    // It is a pity that the this is over, the end message is displayed and the final score is output
    private void drawgameover(Graphics g)
    {
        int offsetX = 1;
        int offsetY = Cons.ROW / 2 - Cons.ROW / 4;
        int rectBoundX = Cons.COLUMN - Cons.COLUMN / 4;
        int rectBoundY = Cons.ROW / 2 - Cons.ROW / 8;
        
        g.setColor(new Color(242, 245, 123, 164));
        g.fillRect(offsetX * Cons.SIZE ,offsetY * Cons.SIZE,
                    rectBoundX * Cons.SIZE, rectBoundY * Cons.SIZE);
        //this over
        g.setColor(Color.RED);
        g.setFont(new Font("Delius", Font.BOLD + Font.ITALIC, 25));
        g.drawString("Game Over",
                        (offsetX + 2) * Cons.SIZE,
                        (offsetY + 3) * Cons.SIZE);
        //score                
        g.setColor(Color.BLACK);
        g.setFont(new Font("Delius", Font.BOLD + Font.ITALIC, 28));
        g.drawString("" + score, 
                        (offsetX + 3) * Cons.SIZE,
                        (offsetY + 5) * Cons.SIZE);
        //prompt             
        g.setColor(new Color(59, 60, 59, 231));
        g.setFont(new Font("Delius", Font.BOLD, 24));
        g.drawString("Press \"Enter\" to restart",
                        offsetX * Cons.SIZE,
                        (Cons.ROW - 4) * Cons.SIZE);
        g.drawString("\"Esc\" for quit!",
                        (offsetX + 2) * Cons.SIZE,
                        (Cons.ROW -3) * Cons.SIZE);
        
    }
}//class

























