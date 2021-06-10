import java.util.List;
import java.util.Arrays;
import java.util.Random;
import java.util.Collections;
public enum Color
{
    RED    (new java.awt.Color(240, 106, 128, 255)),
    ORANGE (new java.awt.Color(240, 173, 106, 255)),
    YELLOW (new java.awt.Color(238, 240, 106, 255)),
    GREEN  (new java.awt.Color(106, 240, 146, 255)),
    CYAN   (new java.awt.Color(106, 222, 240, 255)),
    BLUE   (new java.awt.Color(106, 108, 240, 255)),
    PURPLE (new java.awt.Color(171, 106, 240, 255));
    
    public final java.awt.Color color;
    
    private Color(java.awt.Color color)
    {
        this.color = color;
    }
    
    public static final List<Color> VALUES = 
        Collections.unmodifiableList(Arrays.asList(Color.values()));
    public static final int SIZE = VALUES.size();

    public static Color randomColor()  
    {
        return VALUES.get(new Random().nextInt(SIZE));
    }
}
