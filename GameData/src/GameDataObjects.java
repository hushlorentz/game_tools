import java.util.ArrayList;

class Character
{
	public String name, animPath, imagePath;
	public int interactionType; //0 for dialogue, 1 for quest
	public ArrayList<Integer> inventory;
	public ArrayList<Integer> interactions;
	
	public Character()
	{
		this.name = "";
		animPath = "";
		imagePath = "";
		interactions = new ArrayList<Integer>();
		inventory = new ArrayList<Integer>();
		interactionType = 0;
	}

	public Character(Character c)
	{
		this.name = c.name;
		this.animPath = c.animPath;
		this.imagePath = c.imagePath;
		this.inventory = new ArrayList<Integer>(c.inventory);
		this.interactions = new ArrayList<Integer>(c.interactions);
		this.interactionType = c.interactionType;
	}
	
	public String toString()
	{
		return name;
	}
}
