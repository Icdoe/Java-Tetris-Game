import java.awt.Point;
import java.util.List;
import java.util.Arrays;
import java.util.Random;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Collections;

public enum Shape
{
    I (1, 4, 2),
    J (2, 3, 4),
    L (2, 3, 4),
    O (2, 2, 1),
    S (2, 3, 2),
    T (2, 3, 4),
    Z (2, 3, 2);
        
    public final int minHeight;
    public final int maxHeight;
    public final int formNum;

    public static final 
    EnumMap<Shape, Point[][]> POINT_MAP = 
    new EnumMap<Shape, Point[][]>(Shape.class)
    {
        {
            //I-shape
            put(Shape.I, new Point[][]
            {
                {new Point(3, 0), new Point(2, 0), new Point(1, 0), new Point(0, 0)}, 
                {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)},   
            });
            //J-shape
            put(Shape.J, new Point[][]
            {
                {new Point(2, 1), new Point(1, 1), new Point(0, 1), new Point(0, 0)},
                {new Point(0, 2), new Point(0, 1), new Point(0, 0), new Point(1, 0)},
                {new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(2, 1)},   
                {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2)}
                
            });
            //L-shape
            put(Shape.L, new Point[][]
            {
                {new Point(2, 0), new Point(1, 0), new Point(0, 0), new Point(0, 1)},
                {new Point(1, 2), new Point(1, 1), new Point(1, 0), new Point(0, 0)},
                {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0)},
                {new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(1, 2)}
            });
            //O-shape
            put(Shape.O, new Point[][]
            {
                {new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 0)}
            });
            //S-shape
            put(Shape.S, new Point[][]
            {
                {new Point(2, 0), new Point(1, 0), new Point(1, 1), new Point(0, 1)},
                {new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)}
            });
            //T-shape
            put(Shape.T, new Point[][]
            {
                {new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(1, 1)},
                {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 1)},
                {new Point(2, 1), new Point(1, 1), new Point(0, 1), new Point(1, 0)},
                {new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(1, 1)}
            });
            //Z-shape
            put(Shape.Z, new Point[][]
            {
                {new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)},
                {new Point(1, 0), new Point(1, 1), new Point(0, 1), new Point(0, 2)}
            });
        }
    };
    
    
    private Shape(int min, int max, int formNum)
    {
        this.minHeight = min;
        this.maxHeight = max;
        this.formNum = formNum;
    }
    
    public static final List<Shape> VALUES = 
        Collections.unmodifiableList(Arrays.asList(Shape.values()));
    public static final int SIZE = VALUES.size();

    public static Shape randomShape()
    {
        return VALUES.get(new Random().nextInt(SIZE));
    }
    
}
