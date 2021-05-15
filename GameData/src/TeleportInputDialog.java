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

public class TeleportInputDialog extends JDialog 
{
	public ArrayList<Item> items;
	public ArrayList<Quest> quests;

	private ArrayList<String> maps;
	private ArrayList<Lock> locks;
	private ArrayList<Destination> destinations;
	private ArrayList<JComboBox> addedQuests;
	private JTextField nameField, locationField, lockField;
	private ArrayList<Teleporter> teleporters;
	private Teleporter teleporter;
	private int index;
	
	public TeleportInputDialog(ArrayList<Teleporter> teleporters, ArrayList<Item> items, ArrayList<Quest> quests, ArrayList<String> maps, int selection)
	{
		this.items = items;
		this.quests = quests;
		this.teleporters = teleporters;
		this.maps = maps;
		index = selection;

		setLayout(new GridBagLayout());

		nameField = new JTextField();
		lockField = new JTextField();
		locationField = new JTextField();
		locks = new ArrayList<Lock>();
		destinations = new ArrayList<Destination>();
		addedQuests = new ArrayList<JComboBox>();

		if (index < teleporters.size())
		{
			teleporter = new Teleporter(teleporters.get(index));
		}
		else 
		{
			teleporter = new Teleporter();
		}

		nameField.setText(teleporter.name);
		locationField.setText(teleporter.locationText);
		lockField.setText(teleporter.lockText);

		for (int i = 0; i < teleporter.destinations.size(); i++)
		{
			Destination d = new Destination(quests, items, maps);
			TeleporterDestination td = teleporter.destinations.get(i);

			d.xCoord.setValue(td.xCoord);
			d.yCoord.setValue(td.yCoord);
			d.mapBox.setSelectedIndex(td.map);
			d.conditionBox.setSelectedIndex(td.condition);
			d.conditionValueBox.setSelectedIndex(td.conditionValue);

			destinations.add(d);
		}

		for (int i = 0; i < teleporter.locks.size(); i++)
		{
			LockParameter l = teleporter.locks.get(i);

			Lock lock = new Lock(this);
			lock.type.setSelectedIndex(l.type);
			lock.value.setSelectedIndex(l.value);
			lock.amount.setValue(l.amount);

			locks.add(lock);
		}

		for (int i = 0; i < teleporter.quests.size(); i++)
		{
			JComboBox questBox = new JComboBox(quests.toArray());
			questBox.setSelectedIndex(teleporter.quests.get(i));
			addedQuests.add(questBox);
		}

		refreshDialog();
		
		setModal(true);
		setSize(new Dimension(800, 400));
	}
	
	private void refreshDialog()
	{
		getContentPane().removeAll();
		
		JLabel nameLabel = new JLabel("Name: ");
		JLabel locationLabel = new JLabel("Location: ");
		JLabel mapLabel = new JLabel("Destination: ");
		JLabel lockLabel = new JLabel("Lock Text: ");
		JLabel xLabel = new JLabel("X Coord: ");
		JLabel yLabel = new JLabel("Y Coord: ");
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
		add(locationLabel, gbc);
		
		gbc.gridx++;
		add(locationField, gbc);

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
		panel.add(getDestinationBoxes(), gbc);

		gbc.gridy++;
		panel.add(getLockBoxes(), gbc);

		gbc.gridy++;
		panel.add(getQuestBoxes(), gbc);

		return panel;
	}

	public JScrollPane getDestinationBoxes()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		JScrollPane scroller = new JScrollPane(panel);
		
		TitledBorder border = new TitledBorder("Destinations");
		scroller.setBorder(border);
		
		JButton addDestinationButton = new JButton("Add Destination");
		addDestinationButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addNewDestination();
			}
		});
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		for (int i = 0; i < destinations.size(); i++)
		{
			Destination destination = destinations.get(i);
			final int index = i;
			
			JButton xButton = new JButton("X");
			xButton.setToolTipText("Remove Destination " + (index + 1));
			xButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					destinations.remove(index);
					refreshDialog();
				}
			});
			
			gbc.gridx = 0;
			gbc.weightx = 0;
			panel.add(xButton, gbc);

			gbc.gridx++;
			gbc.weightx = 1;
			panel.add(destination.conditionBox, gbc);

			gbc.gridx++;
			panel.add(destination.conditionValueBox, gbc);

			gbc.gridx++;
			panel.add(destination.mapBox, gbc);

			gbc.gridx++;
			gbc.weightx = 0;
			panel.add(destination.xCoord, gbc);

			gbc.gridx++;
			panel.add(destination.yCoord, gbc);

			gbc.gridy++;
		}
		
		gbc.gridx = 0;
		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.gridwidth = 6;
		gbc.anchor = GridBagConstraints.SOUTH;
		panel.add(addDestinationButton, gbc);
		
		return scroller;
	}
	
	public JScrollPane getQuestBoxes()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		JScrollPane scroller = new JScrollPane(panel);
		
		TitledBorder border = new TitledBorder("Quests");
		scroller.setBorder(border);
		
		JButton addQuestButton = new JButton("Add Quest");
		addQuestButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addNewQuest();
			}
		});
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		for (int i = 0; i < addedQuests.size(); i++)
		{
			final int index = i;
			
			JButton xButton = new JButton("X");
			xButton.setToolTipText("Remove Quest " + (index + 1));
			xButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					addedQuests.remove(index);
					refreshDialog();
				}
			});
			
			gbc.gridx = 0;
			gbc.weightx = 0;
			panel.add(xButton, gbc);

			gbc.gridx++;
			gbc.weightx = 1;
			panel.add(addedQuests.get(i), gbc);

			gbc.gridy++;
		}
		
		gbc.gridx = 0;
		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.SOUTH;
		panel.add(addQuestButton, gbc);
		
		return scroller;
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
	
	private JPanel getButtons()
	{
		JPanel panel = new JPanel();
		JButton okButton = new JButton("Ok");
		JButton cancelButton = new JButton("Cancel");
		
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				teleporter.name = nameField.getText();
				teleporter.locationText = locationField.getText();
				teleporter.lockText = "";
				teleporter.locks = new ArrayList<LockParameter>();
				teleporter.quests = new ArrayList<Integer>();
				teleporter.destinations = new ArrayList<TeleporterDestination>();

				for (int i = 0; i < destinations.size(); i++)
				{
					Destination d = destinations.get(i);
					TeleporterDestination td = new TeleporterDestination();

					td.xCoord = Integer.parseInt("" + d.xCoord.getValue());
					td.yCoord = Integer.parseInt("" + d.yCoord.getValue());
					td.map = d.mapBox.getSelectedIndex();
					td.condition = d.conditionBox.getSelectedIndex();
					td.conditionValue = d.conditionValueBox.getSelectedIndex();

					teleporter.destinations.add(td);
				}
//				teleporter.map = mapBox.getSelectedIndex();
//				teleporter.xCoord = Integer.parseInt("" + xCoord.getValue());
//				teleporter.yCoord = Integer.parseInt("" + yCoord.getValue());

				for (int i = 0; i < locks.size(); i++)
				{
					teleporter.lockText = lockField.getText();

					LockParameter l = new LockParameter();
					Lock lock = locks.get(i);

					l.type = lock.type.getSelectedIndex();
					l.value = lock.value.getSelectedIndex();
					l.amount = Integer.parseInt("" + lock.amount.getValue());

					teleporter.locks.add(l);
				}

				for (int i = 0; i < addedQuests.size(); i++)
				{
					teleporter.quests.add(addedQuests.get(i).getSelectedIndex());
				}

				if (index < teleporters.size())
				{
					teleporters.add(index, teleporter);
					teleporters.remove(index + 1);
				}
				else
				{
					teleporters.add(teleporter);
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

	private void addNewDestination()
	{
		destinations.add(new Destination(quests, items, maps));
		refreshDialog();
	}

	private void addNewQuest()
	{
		addedQuests.add(new JComboBox(quests.toArray()));
		refreshDialog();
	}

	private void addNewLock()
	{
		locks.add(new Lock(this));
		refreshDialog();
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
		private TeleportInputDialog owner;

		public Lock(TeleportInputDialog parent)
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

	class Destination
	{
		public Object[] conditions = {"Default", "After Quest", "During Quest", "Player has Item"};
		public JComboBox conditionBox;
		public JComboBox conditionValueBox;
		public JComboBox mapBox;
		public JSpinner xCoord;
		public JSpinner yCoord;

		public Destination(ArrayList<Quest> quests, ArrayList<Item> items, ArrayList<String> maps)
		{
			conditionBox = new JComboBox(conditions);
			conditionBox.addItemListener(new ItemListener()
			{
				public void itemStateChanged(ItemEvent e)
				{
					conditionValueBox.removeAllItems();
					setBoxes();
				}
			});

			conditionValueBox = new JComboBox();
			conditionValueBox.setEnabled(false);

			xCoord = new JSpinner(new SpinnerNumberModel(new Integer(0), new Integer(0), new Integer(100), new Integer(1)));

			yCoord = new JSpinner(new SpinnerNumberModel(new Integer(0), new Integer(0), new Integer(100), new Integer(1)));
			mapBox = new JComboBox(maps.toArray());
		}

		private void setBoxes()
		{
			conditionValueBox.setEnabled(true);

			if (conditionBox.getSelectedIndex() == 0)
			{
				conditionValueBox.setEnabled(false);
			}
			else if (conditionBox.getSelectedIndex() < 3)
			{
				for (int i = 0; i < quests.size(); i++)
				{
					conditionValueBox.addItem(quests.get(i));
				}
			}
			else
			{

				for (int i = 0; i < items.size(); i++)
				{
					conditionValueBox.addItem(items.get(i));
				}
			}
		}
	}
}

