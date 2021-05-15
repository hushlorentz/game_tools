import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.SpinnerNumberModel;

public class ObjectInputDialog extends JDialog 
{
	public ArrayList<Item> items;
	public ArrayList<Quest> quests;

	private JCheckBox cutsceneCheckBox;
	private JComboBox cutsceneBox;
	private JComboBox interactionBox;
	private JComboBox interactionValueBox;
	private ArrayList<Lock> locks;
	private ArrayList<JComboBox> inventory;
	private ArrayList<JSpinner> amounts;
	private ArrayList<Teleporter> teleporters;
	private ArrayList<String> cutscenes;
	private JTextField nameField, animField, imageField, lockField;
	private ArrayList<GameObject> objects;
	private GameObject object;
	private int index;
	
	public ObjectInputDialog(ArrayList<GameObject> objects, ArrayList<Item> items, ArrayList<Quest> quests, ArrayList<Teleporter> teleporters, ArrayList<String> cutscenes, int selection)
	{
		this.items = items;
		this.objects = objects;
		this.quests = quests;
		this.cutscenes = cutscenes;
		this.teleporters = teleporters;
		index = selection;

		setLayout(new GridBagLayout());

		nameField = new JTextField();
		lockField = new JTextField();
		animField = new JTextField();
		imageField = new JTextField();
		inventory = new ArrayList<JComboBox>();
		locks = new ArrayList<Lock>();
		amounts = new ArrayList<JSpinner>();
		cutsceneBox = new JComboBox(cutscenes.toArray());
		interactionValueBox = new JComboBox();
		cutsceneCheckBox = new JCheckBox();
		cutsceneCheckBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (cutsceneCheckBox.isSelected())
				{
					cutsceneBox.setEnabled(true);
				}
				else
				{
					cutsceneBox.setEnabled(false);
				}
			}
		});

		Object[] interactions = {"Get Items", "Complete Quest", "Unlock Object", "Unlock Teleporter"};
		interactionBox = new JComboBox(interactions);
		interactionBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					interactionValueBox.removeAllItems();
					setInteractionBoxes();

					refreshDialog();
				}
			}
		});

		animField.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e) { setAnimPath(); }
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
		});
		imageField.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e) { setImagePath(); }
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
		});
		
		animField.setToolTipText("Click to set anim file");
		animField.setBackground(getBackground());
		animField.setEnabled(false);
		imageField.setToolTipText("Click to set image file");
		imageField.setBackground(getBackground());
		imageField.setEnabled(false);

		if (index < objects.size())
		{
			object = new GameObject(objects.get(index));
		}
		else 
		{
			object = new GameObject();
		}

		nameField.setText(object.name);
		animField.setText(object.animPath);
		imageField.setText(object.imagePath);
		interactionBox.setSelectedIndex(object.interactionType);

		if (object.interactionType > 0 && interactionValueBox.getItemCount() > 0)
		{
			interactionValueBox.setSelectedIndex(object.interactionValue);
		}

		if (object.cutscene > -1)
		{
			cutsceneCheckBox.setSelected(true);
			cutsceneBox.setSelectedIndex(object.cutscene);
		}
		else
		{
			cutsceneCheckBox.setSelected(false);
			cutsceneBox.setEnabled(false);
		}

		lockField.setText(object.lockText);

		for (int i = 0; i < object.locks.size(); i++)
		{
			LockParameter l = object.locks.get(i);

			Lock lock = new Lock(this);
			lock.type.setSelectedIndex(l.type);
			lock.value.setSelectedIndex(l.value);
			lock.amount.setValue(l.amount);

			locks.add(lock);
		}

		for (int i = 0; i < object.inventory.size(); i++)
		{
			JComboBox newBox = new JComboBox(items.toArray());
			newBox.setSelectedIndex(object.inventory.get(i));
			inventory.add(newBox);

			JSpinner newSpinner = new JSpinner(new SpinnerNumberModel(new Integer(1), new Integer(1), new Integer(100), new Integer(1)));
			newSpinner.setValue(object.amounts.get(i));
			amounts.add(newSpinner);
		}
		
		refreshDialog();
		
		setModal(true);
		setSize(new Dimension(800, 400));
	}
	
	private void refreshDialog()
	{
		getContentPane().removeAll();
		
		JLabel nameLabel = new JLabel("Name: ");
		JLabel animLabel = new JLabel("Animation: ");
		JLabel imageLabel = new JLabel("Image: ");
		JLabel interactionLabel = new JLabel("Interaction Type: ");
		JLabel cutsceneLabel = new JLabel("Cutscene: ");
		JLabel lockLabel = new JLabel("Lock Text: ");
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(nameLabel, gbc);
		
		gbc.weightx = 1;
		gbc.gridx++;
		add(nameField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0;
		add(animLabel, gbc);
		
		gbc.gridx++;
		add(animField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		add(imageLabel, gbc);
		
		gbc.gridx++;
		add(imageField, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		add(interactionLabel, gbc);

		gbc.gridx++;
		add(getInteractionBox(), gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		add(cutsceneLabel, gbc);

		gbc.gridx++;
		add(getCutscenePanel(), gbc);

		if (locks.size() > 0)
		{
			gbc.gridy++;
			gbc.gridx = 0;
			add(lockLabel, gbc);

			gbc.gridx++;
			add(lockField, gbc);
		}

		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		add(getBoxes(), gbc);
		
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weighty = 0;
		gbc.weightx = 0;

		add(getButtons(), gbc);
		
		validate();
		repaint();
	}

	public JPanel getInteractionBox()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		panel.add(interactionBox, gbc);

		gbc.gridx++;

		if (interactionBox.getSelectedIndex() > 0)
		{
			panel.add(interactionValueBox, gbc);
		}
	
		return panel;
	}

	public JPanel getCutscenePanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 1;

		panel.add(cutsceneCheckBox, gbc);

		gbc.gridx++;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(cutsceneBox, gbc);

		return panel;
	}

	public void setInteractionBoxes()
	{
		if (interactionBox.getSelectedIndex() == 1)
		{
			for (int i = 0; i < quests.size(); i++)
			{
				interactionValueBox.addItem(quests.get(i));
			}
		}
		else if (interactionBox.getSelectedIndex() == 2)
		{
			for (int i = 0; i < objects.size(); i++)
			{
				interactionValueBox.addItem(objects.get(i));
			}
		}
		else if (interactionBox.getSelectedIndex() == 3)
		{
			for (int i = 0; i < teleporters.size(); i++)
			{
				interactionValueBox.addItem(teleporters.get(i));
			}
		}
	}
	
	public JPanel getBoxes()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(getLockBoxes(), gbc);

		gbc.gridy++;
		panel.add(getInventoryBoxes(), gbc);
		
		return panel;
	}

	public JScrollPane getLockBoxes()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		JScrollPane scroller = new JScrollPane(panel);
		
		TitledBorder border = new TitledBorder("Locks");
		scroller.setBorder(border);
		
		JButton addLockButton = new JButton("Add Lock");
		addLockButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addNewLock();
			}
		});
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		for (int i = 0; i < locks.size(); i++)
		{
			final int index = i;
			
			JButton xButton = new JButton("X");
			xButton.setToolTipText("Remove Lock " + (index + 1));
			xButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					locks.remove(index);
					refreshDialog();
				}
			});
			
			gbc.gridx = 0;
			gbc.weightx = 0;
			panel.add(xButton, gbc);

			Lock lock = locks.get(i);
			
			gbc.gridx++;
			gbc.weightx = 1;
			panel.add(lock.type, gbc);
			gbc.gridx++;
			panel.add(lock.value , gbc);

			if (lock.showAmount)
			{
				gbc.gridx++;
				panel.add(lock.amount, gbc);
			}

			gbc.gridy++;
		}
		
		gbc.gridx = 0;
		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.gridwidth = 4;
		gbc.anchor = GridBagConstraints.SOUTH;
		panel.add(addLockButton, gbc);
		
		return scroller;
	}
	
	public JScrollPane getInventoryBoxes()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		JScrollPane scroller = new JScrollPane(panel);
		
		TitledBorder border = new TitledBorder("Inventory");
		scroller.setBorder(border);
		
		JButton addInventoryButton = new JButton("Add Inventory");
		addInventoryButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addNewInventory();
			}
		});
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		for (int i = 0; i < inventory.size(); i++)
		{
			final int index = i;
			
			JButton xButton = new JButton("X");
			xButton.setToolTipText("Remove inventory " + (index + 1));
			xButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					inventory.remove(index);
					refreshDialog();
				}
			});
			
			gbc.gridx = 0;
			gbc.weightx = 0;
			panel.add(xButton, gbc);
			
			gbc.gridx++;
			gbc.weightx = 1;
			panel.add(inventory.get(i), gbc);

			gbc.weightx = 0;
			gbc.gridx++;
			panel.add(amounts.get(i), gbc);
			
			gbc.gridy++;
		}
		
		gbc.gridx = 0;
		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.SOUTH;
		panel.add(addInventoryButton, gbc);
		
		return scroller;
	}
	
	private JPanel getButtons()
	{
		JPanel panel = new JPanel();
		JButton okButton = new JButton("Ok");
		JButton cancelButton = new JButton("Cancel");
		
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				object.name = nameField.getText();
				object.animPath = animField.getText();
				object.imagePath = imageField.getText();
				object.interactionType = interactionBox.getSelectedIndex();
				object.interactionValue = interactionValueBox.getSelectedIndex();
				object.lockText = "";
				object.inventory = new ArrayList<Integer>();
				object.amounts = new ArrayList<Integer>();
				object.locks = new ArrayList<LockParameter>();
				object.cutscene = -1;

				if (cutsceneCheckBox.isSelected())
				{
					object.cutscene = cutsceneBox.getSelectedIndex();
				}

				for (int i = 0; i < locks.size(); i++)
				{
					object.lockText = lockField.getText();

					LockParameter l = new LockParameter();
					Lock lock = locks.get(i);

					l.type = lock.type.getSelectedIndex();
					l.value = lock.value.getSelectedIndex();
					l.amount = Integer.parseInt("" + lock.amount.getValue());

					object.locks.add(l);
				}

				for (int i = 0; i < inventory.size(); i++)
				{
					object.amounts.add(Integer.parseInt("" + amounts.get(i).getValue()));
					object.inventory.add(inventory.get(i).getSelectedIndex());
				}

				if (index < objects.size())
				{
					objects.add(index, object);
					objects.remove(index + 1);
				}
				else
				{
					objects.add(object);
				}
				
				setVisible(false);
			}
		});
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
		
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(okButton, gbc);
		
		gbc.gridx++;
		panel.add(cancelButton, gbc);
		
		return panel;
	}

	private void addNewInventory()
	{
		JComboBox newBox = new JComboBox(items.toArray());		
		amounts.add(new JSpinner(new SpinnerNumberModel(new Integer(1), new Integer(1), new Integer(100), new Integer(1))));
		inventory.add(newBox);
		refreshDialog();
	}

	private void addNewLock()
	{
		locks.add(new Lock(this));
		refreshDialog();
	}

	public void setAnimPath()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new Filter("anim", "Animation File (.anim)"));
	
		int result = chooser.showOpenDialog(this);
		
		if (result == JFileChooser.APPROVE_OPTION)
		{
			animField.setText(chooser.getSelectedFile().getName());
		}
	}
	
	public void setImagePath()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new Filter("png", "PNG (.png)"));
	
		int result = chooser.showOpenDialog(this);
		
		if (result == JFileChooser.APPROVE_OPTION)
		{
			imageField.setText(chooser.getSelectedFile().getName());
		}
	}

	class Lock
	{
		public static final int TYPE_ITEM = 0;
		public static final int TYPE_QUEST = 1;
		public JComboBox type;
		public JComboBox value;
		public JSpinner amount;
		public boolean showAmount;

		private Object[] types = {"Item", "Quest"};
		private ObjectInputDialog owner;

		public Lock(ObjectInputDialog parent)
		{
			owner = parent;

			type = new JComboBox(types);
			type.addItemListener(new ItemListener()
			{
				public void itemStateChanged(ItemEvent e)
				{
					if (type.getSelectedIndex() == Lock.TYPE_ITEM)
					{
						value.removeAllItems();

						for (int i = 0; i < owner.items.size(); i++)
						{
							value.addItem(owner.items.get(i));
						}
					}
					else if (type.getSelectedIndex() == Lock.TYPE_QUEST)
					{
						value.removeAllItems();

						for (int i = 0; i < owner.quests.size(); i++)
						{
							value.addItem(owner.quests.get(i));
						}
					}
				}
			});

			value = new JComboBox(owner.items.toArray());
			amount = new JSpinner(new SpinnerNumberModel(new Integer(1), new Integer(1), new Integer(100), new Integer(1)));
			showAmount = true;
		}
	}
}

