import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JPanel;

public class AnimAnimationPanel extends JPanel 
{
	private AnimTimer timer;
	private AnimMainFrame parent;
	private AnimAnimationDrawPanel drawPanel;
	private JButton playButton, stopButton;
	
	public AnimAnimationPanel(AnimMainFrame owner)
	{
		timer = null;
		parent = owner;
		setPanel();
	}
	
	private void setPanel()
	{
		setLayout(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();
		
		drawPanel = new AnimAnimationDrawPanel();
		playButton = new JButton("Play");
		playButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (parent.animSelected())
				{
					if (timer != null)
					{
						return;
					}
					
					parent.resetFrames();
					timer = new AnimTimer();
					timer.start();
				}
			}
		});
		
		stopButton = new JButton("Stop");
		stopButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (timer != null)
				{
					timer.stopThread();
					timer = null;
				}
			}
		});
		
		cons.fill = GridBagConstraints.BOTH;
		cons.gridx = 0;
		cons.gridy = 0;
		cons.weightx = 1;
		cons.weighty = 1;
		cons.gridwidth = 1;
		cons.gridheight = 1;
		cons.gridwidth = 2;
		add(drawPanel, cons);
		
		cons.fill = GridBagConstraints.NONE;
		cons.anchor = GridBagConstraints.EAST;
		cons.weighty = 0;
		cons.weightx = 1;
		cons.gridy++;
		cons.gridwidth = 1;
		add(playButton, cons);
		
		cons.anchor = GridBagConstraints.WEST;
		cons.gridx++;
		add(stopButton, cons);
	}

	public void setImage(BufferedImage img)
	{
		drawPanel.setImage(img);
	}
	
	public void play()
	{
		timer.start();
	}
	
	public long lastTime = 0;
	private class AnimTimer extends Thread
	{
		private final int MSPF = 33;
		private boolean stop = false;
	
		public AnimTimer()
		{
			stop = false;
		}
		
		public void run() 
		{
			while (!stop)
			{
				parent.nextFrame();
				
				try 
				{
					sleep(MSPF);
				}
				catch (InterruptedException e)
				{
					System.out.println(e.getMessage());
				}
			}
		}
	
		public void stopThread()
		{
			stop = true;
		}
	}
}