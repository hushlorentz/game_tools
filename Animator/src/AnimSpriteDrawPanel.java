import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class AnimSpriteDrawPanel extends JPanel 
{
	private final int STATE_DRAW = 1;
	private final int STATE_DRAG = 2;
	private final int STATE_RESIZE_N = 3;
	private final int STATE_RESIZE_S = 4;
	private final int STATE_RESIZE_E = 5;
	private final int STATE_RESIZE_W = 6;
	private final int RESIZE_OFFSET = 4; //pixel buffer on where the resize happens
	private final int BUFFER_X = 32;
	private final int BUFFER_Y = 32;
	private final int MAX_ZOOM = 4;
	
	public ArrayList<Rectangle> bounds;
	public BufferedImage tileImage;
	public BufferedImage backbuffer;
	public JSpinner frameSpinner;
	private JDialog frameDialog;
	
	private int zoomLevel = 1;
	private int offsetX, offsetY; //offset used when dragging, so the rects don't snap to mouseX, mouseY
	private int initialX, initialY;
	private int rectX, rectY;
	private Rectangle selectedRect;
	private Rectangle currentRect;
	private boolean drawRect;
	private int state;
	private AnimMainFrame parent;
	private JPopupMenu mainMenu;
	private JPopupMenu rectMenu;
	
	public AnimSpriteDrawPanel(AnimMainFrame owner)
	{
		parent = owner;
		bounds = new ArrayList<Rectangle>();
		tileImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		backbuffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		drawRect = false;
		state = STATE_DRAW;
		currentRect = new Rectangle();
		frameSpinner = new JSpinner(new SpinnerNumberModel(new Integer(1), new Integer(1), new Integer(1000), new Integer(1)));
		frameDialog = new JDialog();

		addListeners();
		createPopups();
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		Graphics2D g2 =(Graphics2D)g;
		
		g2.drawImage(backbuffer, BUFFER_X, BUFFER_Y, null);
		
		for (int i = 0; i < bounds.size(); i++)
		{
			Rectangle r = bounds.get(i);
			g2.drawRect(r.x * zoomLevel + BUFFER_X, r.y * zoomLevel + BUFFER_Y, r.width * zoomLevel, r.height * zoomLevel);
		}
		
		if (selectedRect != null)
		{
			g2.setColor(Color.BLUE);
			g2.drawRect(selectedRect.x * zoomLevel + BUFFER_X, selectedRect.y * zoomLevel + BUFFER_Y, selectedRect.width * zoomLevel, selectedRect.height * zoomLevel);
		}
		
		if (drawRect)
		{
			g2.setColor(Color.RED);
			g2.drawRect(currentRect.x, currentRect.y, currentRect.width, currentRect.height);
		}
	}
	
	public void loadImage(String path)
	{
		File inFile = new File(path);
		
		try 
		{
			tileImage = ImageIO.read(inFile);
			backbuffer = tileImage;
			setPreferredSize(new Dimension(tileImage.getWidth() + (BUFFER_X << 1), tileImage.getHeight() + (BUFFER_Y << 1)));
			setMinimumSize(new Dimension(tileImage.getWidth() + (BUFFER_X << 1), tileImage.getHeight() + (BUFFER_Y << 1)));
			repaint();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	
	public void loadImage(BufferedImage img)
	{
		tileImage = img;
		backbuffer = tileImage;
		setPreferredSize(new Dimension(tileImage.getWidth() + (BUFFER_X << 1), tileImage.getHeight() + (BUFFER_Y << 1)));
		repaint();
	}
	
	private void createPopups() 
	{
		mainMenu = new JPopupMenu();
				
		JMenuItem boundsItem = new JMenuItem("Set Bounds");
		boundsItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JDialog dialog = DialogManager.getBoundsDialog(parent);
				dialog.setVisible(true);
			}
		});
		
		JMenuItem clearItem = new JMenuItem("Clear");
		clearItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				bounds = new ArrayList<Rectangle>();
				repaint();
			}
		});
		
		mainMenu.add(boundsItem);
		mainMenu.add(clearItem);
		
		rectMenu = new JPopupMenu();
		JMenuItem delRect = new JMenuItem("Remove");
		delRect.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				bounds.remove(selectedRect);
				selectedRect = null;
				repaint();
			}
		});
		
		JMenuItem addFrame = new JMenuItem("Add Frames");
		addFrame.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addFrames();
			}
		});

		rectMenu.add(addFrame);
		rectMenu.add(new JSeparator());
		rectMenu.add(delRect);
	}

	private void addFrames()
	{
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				frameDialog.dispose();		
				int frameTotal = Integer.parseInt("" + frameSpinner.getValue());

				for (int i = 0; i < frameTotal; i++)
				{
					parent.addFrame(bounds.indexOf(selectedRect));
				}
			}
		});

		frameDialog.add(frameSpinner, "North");
		frameDialog.add(okButton, "South");

		frameDialog.setLocation(new java.awt.Point(rectX, rectY));
		frameDialog.pack();
		frameDialog.setVisible(true);

	}
	
	private void addListeners()
	{
		addMouseWheelListener(new MouseWheelListener()
		{
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				if (e.getWheelRotation() < 0)
				{	
					zoomLevel = 1 << zoomLevel;
				}
				else
				{
					zoomLevel >>= 1;
				}
				
				if (zoomLevel < 1)
				{
					zoomLevel = 1;
				}
				else if (zoomLevel > MAX_ZOOM)
				{
					zoomLevel = MAX_ZOOM;
				}
				
				backbuffer = zoomImage();
				setPreferredSize(new Dimension(backbuffer.getWidth() + (BUFFER_X << 1), backbuffer.getHeight() + (BUFFER_Y << 1)));
				parent.refreshScroll();
				repaint();
			}
		});
		
		addMouseListener(new MouseListener() 
		{
			public void mousePressed(MouseEvent e)
			{
				boolean foundRect = false;
				selectedRect = null;
				
				for (int i = 0; i < bounds.size(); i++)
				{
					Rectangle r = bounds.get(i);
					int mouseX = e.getX();
					int mouseY = e.getY();
					
					if (mouseX >= (r.x * zoomLevel + BUFFER_X) &&
						mouseX <= (r.x * zoomLevel + BUFFER_X) + r.width * zoomLevel &&
						mouseY >= (r.y * zoomLevel + BUFFER_Y) &&
						mouseY <= (r.y * zoomLevel + BUFFER_Y) + r.height * zoomLevel)
					{
						foundRect = true;
						selectedRect = r;
						setFramePanelImage(r);
						offsetX = r.x * zoomLevel + BUFFER_X - e.getX();
						offsetY = r.y * zoomLevel + BUFFER_Y - e.getY();	
						
						if (mouseX <= (r.x * zoomLevel + BUFFER_X + RESIZE_OFFSET))
						{
							state = STATE_RESIZE_W;
							
							/*
							if (mouseY <= (r.y * zoomLevel + BUFFER_Y + RESIZE_OFFSET))
							{
								state = STATE_RESIZE_NW;
							}
							else if (mouseY >= (r.y * zoomLevel + BUFFER_Y - RESIZE_OFFSET + r.height * zoomLevel))
							{
								state = STATE_RESIZE_SW;
							}
							*/
						}
						else if (mouseX >= (r.x * zoomLevel + BUFFER_X - RESIZE_OFFSET + r.width * zoomLevel))
						{
							state = STATE_RESIZE_E;
							
							/*
							if (mouseY <= (r.y * zoomLevel + BUFFER_Y + RESIZE_OFFSET))
							{
								state = STATE_RESIZE_NE;
							}
							else if (mouseY >= (r.y * zoomLevel + BUFFER_Y - RESIZE_OFFSET + r.height * zoomLevel))
							{
								state = STATE_RESIZE_SE;
							}
							*/
						}
						else if (mouseY <= (r.y * zoomLevel + BUFFER_Y + RESIZE_OFFSET))
						{
							state = STATE_RESIZE_N;
						}
						else if (mouseY >= (r.y * zoomLevel + BUFFER_Y - RESIZE_OFFSET + r.height * zoomLevel))
						{
							state = STATE_RESIZE_S;
						}
						else //set selected rectangle for dragging
						{
							state = STATE_DRAG;
							
							if (e.getButton() == MouseEvent.BUTTON3)
							{
								repaint();
								rectX = e.getXOnScreen();
								rectY = e.getYOnScreen();
								rectMenu.show(getInstance(), e.getX(), e.getY());
							}
						}
						break;
					}
				}
				
				if (!foundRect)
				{
					switch (e.getButton())
					{
						case MouseEvent.BUTTON1:
							initialX = e.getX();
							initialY = e.getY();
							
							if (initialX < BUFFER_X)
							{
								initialX = BUFFER_X;
							}
							if (initialY < BUFFER_Y)
							{
								initialY = BUFFER_Y;
							}
							
							currentRect.x = 0;
							currentRect.y = 0;
							currentRect.width = 0;
							currentRect.height = 0;
							drawRect = true;
							state = STATE_DRAW;
							break;
						case MouseEvent.BUTTON3:
							mainMenu.show(getInstance(), e.getX(), e.getY());
							break;
					}
				}
			}
			
			public void mouseReleased(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON3)
				{
					e.consume();
					return;
				}
				
				switch (state)
				{
					case STATE_DRAW:
						currentRect.x -= BUFFER_X;
						currentRect.y -= BUFFER_Y;
						currentRect.x /= zoomLevel;
						currentRect.y /= zoomLevel;
						currentRect.width /= zoomLevel;
						currentRect.height /= zoomLevel;
						bounds.add(new Rectangle(currentRect));
						drawRect = false;
						break;
				}
				
				repaint();
			}
			
			public void mouseClicked(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		
		addMouseMotionListener(new MouseMotionListener()
		{
			public void mouseDragged(MouseEvent e)
			{
				int mouseX = e.getX();
				int mouseY = e.getY();
				
				if (mouseX > backbuffer.getWidth() + BUFFER_X)
				{
					mouseX = backbuffer.getWidth() + BUFFER_X;
				}
				if (mouseY > backbuffer.getHeight() + BUFFER_Y)
				{
					mouseY = backbuffer.getHeight() + BUFFER_Y;
				}
				if (mouseX < BUFFER_X)
				{
					mouseX = BUFFER_X;
				}
				if (mouseY < BUFFER_Y)
				{
					mouseY = BUFFER_Y;
				}
				
				switch (state)
				{
					case STATE_RESIZE_W:
						int width = selectedRect.width + selectedRect.x; 
						selectedRect.x = mouseX + offsetX;
						selectedRect.x -= BUFFER_X;
						selectedRect.x /= zoomLevel;
						selectedRect.width = width - selectedRect.x;
						setFramePanelImage(selectedRect);
						parent.setStats("X: " + selectedRect.x + " Y: " + selectedRect.y + "\nW: " + selectedRect.width + " H: " + selectedRect.height);
						break;
					case STATE_RESIZE_E:
						selectedRect.width = (mouseX - BUFFER_X) / zoomLevel - selectedRect.x;
						setFramePanelImage(selectedRect);
						parent.setStats("X: " + selectedRect.x + " Y: " + selectedRect.y + "\nW: " + selectedRect.width + " H: " + selectedRect.height);
						break;
					case STATE_RESIZE_S:
						selectedRect.height = (mouseY - BUFFER_Y) / zoomLevel - selectedRect.y;
						setFramePanelImage(selectedRect);
						parent.setStats("X: " + selectedRect.x + " Y: " + selectedRect.y + "\nW: " + selectedRect.width + " H: " + selectedRect.height);
						break;
					case STATE_RESIZE_N:
						int height = selectedRect.height + selectedRect.y; 
						selectedRect.y = mouseY + offsetY;
						selectedRect.y -= BUFFER_Y;
						selectedRect.y /= zoomLevel;
						selectedRect.height = height - selectedRect.y;
						setFramePanelImage(selectedRect);
						parent.setStats("X: " + selectedRect.x + " Y: " + selectedRect.y + "\nW: " + selectedRect.width + " H: " + selectedRect.height);
						break;
					case STATE_DRAG:
						selectedRect.x = mouseX + offsetX;
						selectedRect.y = mouseY + offsetY;
						selectedRect.x -= BUFFER_X;
						selectedRect.y -= BUFFER_Y;
						
						selectedRect.x /= zoomLevel;
						selectedRect.y /= zoomLevel;
						
						if (selectedRect.x + selectedRect.width >= backbuffer.getWidth())
						{
							selectedRect.x = backbuffer.getWidth() - selectedRect.width;
						}
						else if (selectedRect.x < 0)
						{
							selectedRect.x = 0;
						}
						if (selectedRect.y + selectedRect.height >= backbuffer.getHeight())
						{
							selectedRect.y = backbuffer.getHeight() - selectedRect.height;
						}
						else if (selectedRect.y < 0)
						{
							selectedRect.y = 0;
						}
						setFramePanelImage(selectedRect);
						parent.setStats("Rect: (" + selectedRect.x + "," + selectedRect.y + ")");
						break;
					case STATE_DRAW:
						if (mouseX < initialX)
						{
							currentRect.x = mouseX;
							currentRect.width = initialX - currentRect.x;
						}
						else
						{
							currentRect.x = initialX;
							currentRect.width = mouseX - initialX;
						}
						if (mouseY < initialY)
						{
							currentRect.y = mouseY;
							currentRect.height = initialY - currentRect.y;
						}
						else
						{
							currentRect.y = initialY;
							currentRect.height = mouseY - initialY;
						}
						
						currentRect.x -= BUFFER_X;
						currentRect.y -= BUFFER_Y;
						
						currentRect.x /= zoomLevel;
						currentRect.x *= zoomLevel;
						currentRect.y /= zoomLevel;
						currentRect.y *= zoomLevel;
						currentRect.width /= zoomLevel;
						currentRect.width *= zoomLevel;
						currentRect.height /= zoomLevel;
						currentRect.height *= zoomLevel;
						
						currentRect.x += BUFFER_X;
						currentRect.y += BUFFER_Y;
												
						parent.setStats("X: " + currentRect.x + " Y: " + currentRect.y + "\nW: " + currentRect.width + " H: " + currentRect.height);
						break;
				}
			
				repaint();
			}
			
			public void mouseMoved(MouseEvent e) {}
		});	
	}
	
	private BufferedImage zoomImage() 
	{
        int w = zoomLevel * tileImage.getWidth();
        int h = zoomLevel * tileImage.getHeight();
        
        BufferedImage zoomImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        
        for (int i = 0; i < h; i++)
        {
            for (int j = 0; j < w; j++)
            {
                zoomImage.setRGB(j, i, tileImage.getRGB(j / zoomLevel, i / zoomLevel));
            }
        }
        
        return zoomImage;
    }
	
	public void setFramePanelImage(int frame)
	{
		setFramePanelImage(bounds.get(frame));
	}
	
	public void setFramePanelImage(Rectangle r)
	{
		parent.setFramePanelImage(tileImage.getSubimage(selectedRect.x, selectedRect.y, selectedRect.width, selectedRect.height));
	}
	
	public AnimSpriteDrawPanel getInstance()
	{
		return this;
	}

	public void drawBounds(int width, int height)
	{
		int numWBounds = tileImage.getWidth() / width;
		int numHBounds = tileImage.getHeight() / height;
		
		int x = 0;
		int y = 0;
		
		for (int i = 0; i < numHBounds; i++)
		{
			for (int j = 0; j < numWBounds; j++)
			{
				bounds.add(new Rectangle(x, y, width, height));
				x += width;
			}
			
			y += height;
			x = 0;
		}
		
		repaint();
	}

	public void loadBounds(ArrayList<Rectangle> bounds)
	{
		this.bounds = bounds;
	}

	public BufferedImage getFrame(int frame) 
	{
		Rectangle r = bounds.get(frame);
		return tileImage.getSubimage(r.x, r.y, r.width, r.height);
	}

	public void setSelectedFrame(int i) 
	{
		selectedRect = bounds.get(i);
		setFramePanelImage(i);
		repaint();
	}
}
