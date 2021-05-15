import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class AnimSpritePanel extends JPanel
{
	private AnimMainFrame parent;
	private AnimSpriteDrawPanel drawPanel;
	private JScrollPane spriteScroll;
	
	public AnimSpritePanel(AnimMainFrame owner)
	{
		parent = owner;
		setLayout(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();
		
		drawPanel = new AnimSpriteDrawPanel(parent);
		
		spriteScroll = new JScrollPane(drawPanel);
		
		cons.weightx = 1;
		cons.weighty = 1;
		cons.fill = GridBagConstraints.BOTH;
		add(spriteScroll, cons);
	}
	
	public void loadImage(String path)
	{
		drawPanel.loadImage(path);
		spriteScroll.revalidate();
	}

	public void drawBounds(int width, int height) 
	{
		drawPanel.drawBounds(width, height);
	}

	public ArrayList<Rectangle> getBoundList() 
	{
		return drawPanel.bounds;
	}

	public BufferedImage getTileSheet()
	{
		return drawPanel.tileImage;
	}

	public void setTileSheet(BufferedImage img) 
	{
		drawPanel.loadImage(img);
		spriteScroll.revalidate();
	}

	public void loadBounds(ArrayList<Rectangle> bounds) 
	{
		drawPanel.loadBounds(bounds);
	}

	public void setSelectedFrame(int i)
	{
		drawPanel.setSelectedFrame(i);
	}
	
	public void refreshScroll()
	{
		spriteScroll.revalidate();
	}
}
