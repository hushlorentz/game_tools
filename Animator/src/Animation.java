import java.util.Vector;

public class Animation 
{
	public int type;
	public int loopType;
	public String name;
	public Vector<Integer> frames;
	
	public Animation(String name)
	{
		this.name = name;
		frames = new Vector<Integer>();
		type = 0;
		loopType = 0;
	}
	
	public void addFrame(int frame)
	{
		frames.add(frame);
	}
	
	public void removeFrame(int position)
	{
		frames.remove(position);
	}
	
	public String toString()
	{
		return name;
	}

	public Object[] getFrames() 
	{
		return frames.toArray();
	}
}
