import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

public class AnimMainFrame extends JFrame 
{
	private JMenuItem openItem, saveItem, compileItem, exitItem;
	private AnimListPanel lPanel;
	private AnimSpritePanel sPanel;
	private AnimOutput oPanel;
	private AnimAnimationPanel aPanel;
	private String lastPath;
	
	public AnimMainFrame()
	{
		JMenu fileMenu = new JMenu("File");

		lastPath = "";
		
		openItem = new JMenuItem("Open");
		openItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) { openFile(); }
		});
		
		saveItem = new JMenuItem("Save");
		saveItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) { saveFile(); }
		});
		
		compileItem = new JMenuItem("Compile");
		compileItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) { compileFile(); }
		});
		
		exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(compileItem);
		fileMenu.add(new JSeparator());
		fileMenu.add(exitItem);
		
		JMenu imageMenu = new JMenu("Image");
		
		JMenuItem loadImage = new JMenuItem("Load");
		loadImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				loadSpritePanelImage();
			}
		});
		
		imageMenu.add(loadImage);
		
		JMenuBar bar = new JMenuBar();
		bar.add(fileMenu);
		bar.add(imageMenu);
		
		lPanel = new AnimListPanel(this);
		sPanel = new AnimSpritePanel(this);
		oPanel = new AnimOutput(this);
		aPanel = new AnimAnimationPanel(this);
		
		setLayout(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();
		
		cons.weightx = 0;
		cons.weighty = 0;
		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.gridwidth = 3;
		cons.gridx = 0;
		cons.gridy = 0;
		cons.anchor = GridBagConstraints.PAGE_START;
		bar.setPreferredSize(new java.awt.Dimension(1000, 32));
		bar.setMinimumSize(new java.awt.Dimension(1000, 32));
		add(bar, cons);
		
		cons.gridx = 0;		
		cons.gridy = 1;
		cons.weightx = 0;
		cons.weighty = 0;
		cons.gridheight = 2;
		cons.fill = GridBagConstraints.VERTICAL;
		cons.gridwidth = 1;
		lPanel.setPreferredSize(new Dimension(150, 0));
		add(lPanel, cons);
		
		cons.fill = GridBagConstraints.BOTH;
		cons.gridheight = 1;
		cons.weightx = 1;
		cons.weighty = 1;
		cons.gridx = 1;
		add(aPanel, cons);
		
		cons.anchor = GridBagConstraints.PAGE_END;
		cons.weighty = 0;
		cons.weightx = 1;
		cons.gridx = 1;
		cons.gridy++;
		cons.gridwidth = 1;
		cons.fill = GridBagConstraints.HORIZONTAL;
		add(oPanel, cons);
		
		cons.gridwidth = 1;
		cons.gridheight = 2;
		cons.anchor = GridBagConstraints.PAGE_START;
		cons.weightx = 1;
		cons.weighty = 1;
		cons.gridx = 2;
		cons.gridy = 1;
		cons.fill = GridBagConstraints.BOTH;
		add(sPanel, cons);
	}
	
	private void loadSpritePanelImage()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new Filter("png", "PNG Image (.png)"));
		chooser.setCurrentDirectory(new File(lastPath));
		
		int option = chooser.showOpenDialog(null);
		
		if (option == JFileChooser.APPROVE_OPTION)
		{
			lastPath = chooser.getSelectedFile().getParent();
			sPanel.loadImage(chooser.getSelectedFile().getPath());
		}
	}

	public void setStats(String string) 
	{
		oPanel.setStats(string);
	}

	public void drawBounds(int width, int height)
	{
		sPanel.drawBounds(width, height);
	}

	public void addFrame(int i) 
	{
		lPanel.addFrame(i);
	}
	
	private void compileFile() 
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new Filter("anim", "Compiled Data (.anim)"));
		chooser.setCurrentDirectory(new File(lastPath));
		
		int result = chooser.showSaveDialog(this);
		
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File outFile = chooser.getSelectedFile();
			String path = outFile.getPath();
			lastPath = outFile.getParent();
			
			if (!path.endsWith(".anim"))
			{
				path += ".anim";
				outFile = new File(path);
			}
			
			try
			{
				DataOutputStream out = new DataOutputStream(new FileOutputStream(outFile));
				
				ArrayList<Rectangle> bounds = sPanel.getBoundList();
				BufferedImage img = sPanel.getTileSheet();
				
				out.writeInt(bounds.size());
				
				for (int i = 0; i < bounds.size(); i++)
				{
					Rectangle r = bounds.get(i);
					
					out.writeInt(r.x);
					out.writeInt(r.y);
					out.writeInt(r.width);
					out.writeInt(r.height);
				}
				
				ArrayList<Animation> anims = lPanel.anims;
				
				out.writeInt(anims.size());
				
				int startFrame = 0;
				
				for (int i = 0; i < anims.size(); i++)
				{
					Animation a = anims.get(i);
					
					out.writeInt(a.type);
					out.writeInt(a.loopType);
					out.writeInt(a.frames.size());
					out.writeInt(startFrame);
					
					startFrame += a.frames.size();
				}
				
				for (int i = 0; i < anims.size(); i++)
				{
					Animation a = anims.get(i);

					for (int j = 0; j < a.frames.size(); j++)
					{
						out.writeInt(Integer.parseInt("" + a.frames.get(j))); //hax to get numbers out of the vector
					}
				}
				
				out.close();
				JOptionPane.showMessageDialog(null, "Compiled to " + outFile.getPath());
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
		}
	}
	
	private void saveFile()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new Filter("adat", "Animation Data (.adat)"));
		chooser.setCurrentDirectory(new File(lastPath));
		
		int result = chooser.showSaveDialog(this);
		
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File outFile = chooser.getSelectedFile();
			String path = outFile.getPath();
			lastPath = outFile.getParent();
			
			if (!path.endsWith(".adat"))
			{
				path += ".adat";
				outFile = new File(path);
			}
			
			try
			{
				DataOutputStream out = new DataOutputStream(new FileOutputStream(outFile));
				
				ArrayList<Rectangle> bounds = sPanel.getBoundList();
				BufferedImage img = sPanel.getTileSheet();
				
				out.writeInt(img.getWidth());
				out.writeInt(img.getHeight());
				
				for (int i = 0; i < img.getHeight(); i++)
				{
					for (int j = 0; j < img.getWidth(); j++)
					{
						out.writeInt(img.getRGB(j, i));
					}
				}
				
				out.writeInt(bounds.size());
				
				for (int i = 0; i < bounds.size(); i++)
				{
					Rectangle r = bounds.get(i);
					
					out.writeInt(r.x);
					out.writeInt(r.y);
					out.writeInt(r.width);
					out.writeInt(r.height);
				}
				
				ArrayList<Animation> anims = lPanel.anims;
				
				out.writeInt(anims.size());
				
				for (int i = 0; i < anims.size(); i++)
				{
					Animation a = anims.get(i);
					
					out.writeInt(a.name.length());
					out.writeChars(a.name);
					out.writeInt(a.type);
					out.writeInt(a.loopType);
					out.writeInt(a.frames.size());
					
					for (int j = 0; j < a.frames.size(); j++)
					{
						out.writeInt(Integer.parseInt("" + a.frames.get(j))); //hax to get numbers out of the vector
					}
				}
				
				out.close();
				JOptionPane.showMessageDialog(null, "Save completed to " + outFile.getPath());
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
		}
	}
	
	private void openFile()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new Filter("adat", "Animation Data (.adat)"));
		chooser.setCurrentDirectory(new File(lastPath));
		
		int result = chooser.showOpenDialog(this);
		
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File inFile = chooser.getSelectedFile();
			lastPath = inFile.getParent();
			
			try 
			{
				DataInputStream in = new DataInputStream(new FileInputStream(inFile));
				
				int imgWidth = in.readInt();
				int imgHeight = in.readInt();
				
				BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
				
				for (int i = 0; i < imgHeight; i++)
				{
					for (int j = 0; j < imgWidth; j++)
					{
						img.setRGB(j, i, in.readInt());
					}
				}
				
				sPanel.setTileSheet(img);

				int numBounds = in.readInt();
				
				ArrayList<Rectangle> bounds = new ArrayList<Rectangle>();
				
				for (int i = 0; i < numBounds; i++)
				{
					Rectangle r = new Rectangle(in.readInt(), in.readInt(), in.readInt(), in.readInt());
					bounds.add(r);
				}
				
				sPanel.loadBounds(bounds);
				
				int numAnims = in.readInt();
				
				ArrayList<Animation> anims = new ArrayList<Animation>();
				
				for (int i = 0; i < numAnims; i++)
				{
					int nameLength = in.readInt();
					String name = "";

					for (int j = 0; j < nameLength; j++)
					{
						name += in.readChar();
					}

					Animation a = new Animation(name);
					
					a.type = in.readInt();
					a.loopType = in.readInt();

					int numFrames = in.readInt();
					
					for (int j = 0; j < numFrames; j++)
					{
						a.addFrame(in.readInt());
					}
					
					anims.add(a);
				}
				
				lPanel.loadAnims(anims);
				
				in.close();
				JOptionPane.showMessageDialog(this, "Load Successful: " + inFile.getPath());		
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		}
	}

	public Animation getSelectedAnim() 
	{
		return lPanel.getSelectedAnim();
	}

	public void play() 
	{
		aPanel.play();
	}

	public void setFramePanelImage(BufferedImage img) 
	{
		aPanel.setImage(img);
		aPanel.repaint();
	}

	public void setSelectedFrame(int i) 
	{
		sPanel.setSelectedFrame(i);
	}

	public void nextFrame() 
	{
		lPanel.nextFrame();
	}

	public void resetFrames() 
	{
		lPanel.resetFrames();	
	}

	public boolean animSelected()
	{
		return (lPanel.anims.size() > 0 && lPanel.animSelected());
	}

	public void refreshScroll() 
	{
		sPanel.refreshScroll();
	}
}
