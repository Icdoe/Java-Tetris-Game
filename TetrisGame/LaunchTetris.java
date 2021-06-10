import javax.swing.JFrame;
public class LaunchTetris extends JFrame
{
    LaunchTetris()
    {
        super("Tetris");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(300, 100, (2 * Cons.COLUMN) * Cons.SIZE, 
                    (Cons.ROW + Cons.ROW / 7) * Cons.SIZE);
        setVisible(true);
    }
    
    public static void main(String[] args) 
    {   
        Tetris game = new Tetris();
        new LaunchTetris().add(game);
        game.setListener();
        game.launch();
    }//main()
}
