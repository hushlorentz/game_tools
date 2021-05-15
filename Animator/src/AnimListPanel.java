import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class AnimListPanel extends JPanel 
{
	public ArrayList<Animation> anims;
	
	private AnimMainFrame parent;
	private JList animList;
	private JList frameList;
	private JComboBox typeBox;
	private JComboBox loopBox;

	private Object[] animChoices = {"Idle", "Action", "Walk Left", "Walk Right", "Idle Left", "Idle Right", "Jump Left", "Jump Right", "Fall Left", "Fall Right"};
	private Object[] loopChoices = {"Looping", "One Shot"};
	
	public AnimListPanel(AnimMainFrame owner)
	{
		parent = owner;
		
		setLayout(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();
		
		anims = new ArrayList<Animation>();
		typeBox = new JComboBox(animChoices);
		typeBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					if (animList.getSelectedIndex() > -1)
					{
						Animation a = anims.get(animList.getSelectedIndex());
						a.type = typeBox.getSelectedIndex();
					}
				}
			}
		});
		loopBox = new JComboBox(loopChoices);
		loopBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					if (animList.getSelectedIndex() > -1)
					{
						Animation a = anims.get(animList.getSelectedIndex());
						a.loopType = loopBox.getSelectedIndex();
					}
				}
			}
		});
	
		cons.gridx = 0;
		cons.gridy = 0;
		cons.weighty = 1;
		cons.fill = GridBagConstraints.BOTH;
		add(getAnimPanel(), cons);
		
		cons.gridy++;
		add(getFramePanel(), cons);
	}
	
	private JPanel getAnimPanel()
	{
		JPanel animPanel = new JPanel();
		
		animPanel.setLayout(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();
	
		animList = new JList();
		animList.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (animList.getSelectedIndex() > -1)
				{
					Animation a = anims.get(animList.getSelectedIndex());
					typeBox.setSelectedIndex(a.type);
					loopBox.setSelectedIndex(a.loopType);
					frameList.setListData(a.getFrames());
				}
			}
		});
		animList.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (animList.getSelectedIndex() > -1 &&
					(e.getClickCount() == 2) &&
					e.getButton() == MouseEvent.BUTTON1)
				{
					final JDialog dialog = new JDialog(new javax.swing.JFrame(), "Rename Anim", true);
					final JTextField nameField = new JTextField();
					JButton closeButton = new JButton("Ok");
					closeButton.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							anims.get(animList.getSelectedIndex()).name = nameField.getText();
							animList.setListData(anims.toArray());
							dialog.dispose();
						}
					});

					nameField.setText(anims.get(animList.getSelectedIndex()).name);

					dialog.add(nameField, "North");
					dialog.add(closeButton, "Center");
					dialog.pack();
					dialog.setLocation(new java.awt.Point(e.getXOnScreen(), e.getYOnScreen()));
					dialog.setVisible(true);
				}
			}
			
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		JScrollPane animScroll = new JScrollPane(animList);
		
		JButton addAnim = new JButton("Add");
		addAnim.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				anims.add(new Animation("Anim: " + anims.size()));
				animList.setListData(anims.toArray());
			}
		});
		
		JButton delAnim = new JButton("Del");
		delAnim.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (animList.getSelectedIndex() > -1)
				{
					anims.remove(animList.getSelectedIndex());
					animList.setListData(anims.toArray());
					frameList.setListData(new Object[0]);
				}
			}
		});
		
		cons.weighty = 1;
		cons.anchor = GridBagConstraints.PAGE_START;
		cons.fill = GridBagConstraints.BOTH;
		cons.weightx = 1;
		cons.gridx = 0;
		cons.gridy = 0;
		cons.gridwidth = 2;
		animPanel.add(animScroll, cons);
		
		cons.weighty = 0;
		cons.fill = GridBagConstraints.NONE;
		cons.gridy++;
		cons.gridwidth = 1;
		animPanel.add(addAnim, cons);
		
		cons.gridx++;
		animPanel.add(delAnim, cons);
		
		return animPanel;
	}
	
	private JPanel getFramePanel()
	{
		JPanel framePanel = new JPanel();
		
		framePanel.setLayout(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();
	
		frameList = new JList();
		frameList.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (frameList.getSelectedIndex() > -1)
				{
					parent.setSelectedFrame(Integer.parseInt("" + frameList.getSelectedValue()));
				}
			}
		});
		
		JScrollPane animScroll = new JScrollPane(frameList);
		JButton delAnim = new JButton("Remove");
		delAnim.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{	
				if (frameList.getSelectedIndex() > -1)
				{
					Animation a = anims.get(animList.getSelectedIndex());
					int offset = 0;

					for (int i = 0; i < frameList.getSelectedIndices().length; i++)
					{
						a.removeFrame(frameList.getSelectedIndices()[i] - offset);
						offset++;
					}

					frameList.setListData(a.getFrames());
				}
			}
		});
		
		JLabel typeLabel = new JLabel("Anim: ");
		JLabel loopLabel = new JLabel("Loop: ");

		cons.gridy = 0;
		cons.gridx = 0;
		framePanel.add(typeLabel, cons);

		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.weightx = 1;
		cons.gridx++;
		framePanel.add(typeBox, cons);

		cons.fill = GridBagConstraints.NONE;
		cons.gridx = 0;
		cons.gridy++;
		framePanel.add(loopLabel, cons);

		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.gridx++;
		framePanel.add(loopBox, cons);

		cons.gridwidth = 2;
		cons.weighty = 1;
		cons.anchor = GridBagConstraints.PAGE_START;
		cons.fill = GridBagConstraints.BOTH;
		cons.weightx = 1;
		cons.gridx = 0;
		cons.gridy++;
		framePanel.add(animScroll, cons);
		
		cons.weighty = 0;
		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.gridy++;
		framePanel.add(delAnim, cons);
		
		return framePanel;
	}
	
	public AnimListPanel getInstance()
	{
		return this;
	}

	public void addFrame(int i)
	{
		if (animList.getSelectedIndex() > -1)
		{
			Animation a = anims.get(animList.getSelectedIndex());
			a.addFrame(i);
			frameList.setListData(a.getFrames());
		}
	}

	public void loadAnims(ArrayList<Animation> anims) 
	{
		this.anims = anims;
		animList.setListData(anims.toArray());
	}

	public Animation getSelectedAnim()
	{
		return anims.get(animList.getSelectedIndex());
	}
	
	public void resetFrames()
	{
		frameList.setSelectedIndex(0);
	}

	public void nextFrame() 
	{
		int listSize = anims.get(animList.getSelectedIndex()).frames.size();
		
		if (frameList.getSelectedIndex() < (listSize - 1))
		{
			frameList.setSelectedIndex(frameList.getSelectedIndex() + 1);
		}
		else
		{
			frameList.setSelectedIndex(0);
		}
	}

	public boolean animSelected() 
	{
		return animList.getSelectedIndex() > -1;
	}
}
