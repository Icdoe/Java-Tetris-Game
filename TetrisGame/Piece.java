import java.awt.Point;
import java.util.Random;
import java.util.HashMap;
public class Piece 
{
    private int height;  // this.shape current height ,related to offset value
    private int offset;  // [0, this.shape.formNum)
    private int preOffset;
   
    public final Shape shape;
    public final java.awt.Color color;
    
    
    public Piece()
    {
        this.shape = Shape.randomShape();
        this.offset = new Random().nextInt(this.shape.formNum);
        this.height = this.offset % 2 == 0 ? this.shape.minHeight : this.shape.maxHeight;
        this.color = Color.randomColor().color;
    }
    
    public int height()
    {
        return this.height;
    }
    
    public void rotate(int i)
    {
        preOffset = this.offset;
        offset = (this.offset + i) % this.shape.formNum;
        if(this.offset < 0)
            offset = this.shape.formNum - 1;
            
        refreshheight();
    }
    
    public void undoRotate()
    {
        if(preOffset == -1)
            return;
            
        offset = preOffset;
        refreshheight();
        
        preOffset = -1;
    }
    
    public Point[] pointRange()
    {
        return Shape.POINT_MAP.get(shape)[offset];
    }
    
    private void refreshheight()
    {
        height = this.offset % 2 == 0 ? this.shape.minHeight : this.shape.maxHeight;
    }
    
}
