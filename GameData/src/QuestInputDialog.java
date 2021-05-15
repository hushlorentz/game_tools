import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.SpinnerNumberModel;

public class QuestInputDialog extends JDialog 
{
	public ArrayList<Quest> quests;
	public ArrayList<Item> items;
	public ArrayList<Character> characters;
	public ArrayList<ConditionEntry> conditionEntries;
	public ArrayList<RewardEntry> rewardEntries;
	public ArrayList<ReceivableEntry> receivableEntries;
	public ArrayList<String> cutscenes;
	
	private int index;
	private Object[] rewards = {"Receive Item", "Receive New Quest"};
	private Object[] triggers = {"Immediate", "Return to Origin"};
	private Object[] availableOptions = {"Always", "After Completed Quest", "Player has item"};
	private JComboBox dialogue1Box, dialogue2Box, dialogue3Box, triggerBox, availableBox, availableValueBox;
	private JSpinner expSpinner;
	private JTextField nameField, descriptionField;
	private Quest quest;
	private JCheckBox cutsceneBeforeCheckBox;
	private JCheckBox cutsceneAfterCheckBox;
	private JComboBox cutsceneBeforeBox;
	private JComboBox cutsceneAfterBox;
	
	public QuestInputDialog(ArrayList<Quest> quests, ArrayList<String> dialogue, ArrayList<Item> items, ArrayList<Character> characters, ArrayList<String> cutscenes, int selection)
	{
		this.quests = quests;
		this.items = items;
		this.characters = characters;
		this.cutscenes = cutscenes;
		index = selection;

		setLayout(new GridBagLayout());

		nameField = new JTextField();
		descriptionField = new JTextField();
		dialogue1Box = new JComboBox(dialogue.toArray());
		dialogue2Box = new JComboBox(dialogue.toArray());
		dialogue3Box = new JComboBox(dialogue.toArray());
		conditionEntries = new ArrayList<ConditionEntry>();
		rewardEntries = new ArrayList<RewardEntry>();
		receivableEntries = new ArrayList<ReceivableEntry>();
		triggerBox = new JComboBox(triggers);
		expSpinner = new JSpinner(new SpinnerNumberModel(new Integer(100), new Integer(100), new Integer(100000), new Integer(100)));
		cutsceneBeforeCheckBox = new JCheckBox();
		cutsceneBeforeCheckBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (cutsceneBeforeCheckBox.isSelected())
				{
					cutsceneBeforeBox.setEnabled(true);
				}
				else
				{
					cutsceneBeforeBox.setEnabled(false);
				}
			}
		});
		cutsceneAfterCheckBox = new JCheckBox();
		cutsceneAfterCheckBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (cutsceneAfterCheckBox.isSelected())
				{
					cutsceneAfterBox.setEnabled(true);
				}
				else
				{
					cutsceneAfterBox.setEnabled(false);
				}
			}
		});
		cutsceneBeforeBox = new JComboBox(cutscenes.toArray());
		cutsceneAfterBox = new JComboBox(cutscenes.toArray());
		availableBox = new JComboBox(availableOptions);
		availableBox.addItemListener(new ItemListener() 
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					setAvailableBoxes();
					refreshDialog();
				}
			}
		});
		availableValueBox = new JComboBox();

		if (index < quests.size())
		{
			quest = new Quest(quests.get(index));
		}
		else
		{
			quest = new Quest();
		}

		if (quest.availableType == 2)
		{
			for (int i = 0; i < items.size(); i++)
			{
				availableValueBox.addItem(items.get(i));
			}
		}
		else
		{
			for (int i = 0; i < quests.size(); i++)
			{
				availableValueBox.addItem(quests.get(i));
			}
		}

		nameField.setText(quest.name);
		descriptionField.setText(quest.description);
		availableBox.setSelectedIndex(quest.availableType);

		if (availableValueBox.getItemCount() > 0)
		{
			availableValueBox.setSelectedIndex(quest.availableValue);
		}

		dialogue1Box.setSelectedIndex(quest.dialogue1);
		dialogue2Box.setSelectedIndex(quest.dialogue2);
		dialogue3Box.setSelectedIndex(quest.dialogue3);

		if (quest.cutsceneBefore > -1)
		{
			cutsceneBeforeCheckBox.setSelected(true);
			cutsceneBeforeBox.setSelectedIndex(quest.cutsceneBefore);
		}
		else
		{
			cutsceneBeforeCheckBox.setSelected(false);
			cutsceneBeforeBox.setEnabled(false);
		}

		if (quest.cutsceneAfter > -1)
		{
			cutsceneAfterCheckBox.setSelected(true);	
			cutsceneAfterBox.setSelectedIndex(quest.cutsceneAfter);
		}
		else
		{
			cutsceneAfterCheckBox.setSelected(false);
			cutsceneAfterBox.setEnabled(false);
		}

		for (int i = 0; i < quest.conditions.size(); i++)
		{
			ConditionEntry cEntry = new ConditionEntry(this);
			WinCondition condition = quest.conditions.get(i);

			cEntry.conditionType.setSelectedIndex(condition.winType);
			cEntry.condition.setSelectedIndex(condition.keyIndex);
			cEntry.amount.setValue(condition.amount);

			conditionEntries.add(cEntry);
		}
		for (int i = 0; i < quest.rewards.size(); i++)
		{
			RewardEntry rEntry = new RewardEntry(this);
			Reward reward = quest.rewards.get(i);

			rEntry.rewardType.setSelectedIndex(reward.rewardType);
			rEntry.reward.setSelectedIndex(reward.keyIndex);
			rEntry.amount.setValue(reward.amount);

			rewardEntries.add(rEntry);
		}
		for (int i = 0; i < quest.receivables.size(); i++)
		{
			ReceivableEntry rEntry = new ReceivableEntry(this);
			ItemParameter receivable = quest.receivables.get(i);

			rEntry.items.setSelectedIndex(receivable.type);
			rEntry.amount.setValue(receivable.value);

			receivableEntries.add(rEntry);
		}	
		triggerBox.setSelectedIndex(quest.trigger);
		expSpinner.setValue(quest.exp);
		
		setModal(true);
		setSize(new Dimension(700, 400));
		
		refreshDialog();
	}

	private void setAvailableBoxes()
	{
		availableValueBox.removeAllItems();

		if (availableBox.getSelectedIndex() == 2)
		{
			for (int i = 0; i < items.size(); i++)
			{
				availableValueBox.addItem(items.get(i));
			}
		}
		else
		{
			for (int i = 0; i < quests.size(); i++)
			{
				availableValueBox.addItem(quests.get(i));
			}
		}
	}
	
	public void refreshDialog()
	{
		getContentPane().removeAll();
		
		JLabel nameLabel = new JLabel("Name: ");
		JLabel descriptionLabel = new JLabel("Description: ");
		JLabel availableLabel = new JLabel("Available: ");
		JLabel dialogue1Label = new JLabel("Dialogue Before: ");
		JLabel dialogue2Label = new JLabel("Dialogue During: ");
		JLabel dialogue3Label = new JLabel("Dialogue After: ");
		JLabel cutsceneBeforeLabel = new JLabel("Cutscene Before: ");
		JLabel cutsceneAfterLabel = new JLabel("Cutscene After: ");
		JLabel triggerLabel = new JLabel("Trigger: ");
		JLabel expLabel = new JLabel("Experience: ");
		
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
		add(descriptionLabel, gbc);
		
		gbc.gridx++;
		add(descriptionField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		add(availableLabel, gbc);

		gbc.gridx++;
		add(getAvailablePanel(), gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0;
		add(dialogue1Label, gbc);
		
		gbc.gridx++;
		add(dialogue1Box, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		add(dialogue2Label, gbc);
		
		gbc.gridx++;
		add(dialogue2Box, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		add(dialogue3Label, gbc);
		
		gbc.gridx++;
		add(dialogue3Box, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		add(cutsceneBeforeLabel, gbc);

		gbc.gridx++;
		add(getCutsceneBeforePanel(), gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		add(cutsceneAfterLabel, gbc);

		gbc.gridx++;
		add(getCutsceneAfterPanel(), gbc);
	
		gbc.weightx = 0;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		add(getBoxes(), gbc);

		gbc.weighty = 0;
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy++;
		add(triggerLabel, gbc);
		
		gbc.weightx = 0;
		gbc.gridx++;
		add(triggerBox, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		add(expLabel, gbc);

		gbc.gridx++;
		add(expSpinner, gbc);
		
		gbc.gridwidth = 2;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy++;
		add(getButtons(), gbc);
		
		validate();
		repaint();
	}

	public JPanel getCutsceneBeforePanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 1;
		panel.add(cutsceneBeforeCheckBox, gbc);

		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx++;
		panel.add(cutsceneBeforeBox, gbc);

		return panel;
	}

	public JPanel getCutsceneAfterPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 1;
		panel.add(cutsceneAfterCheckBox, gbc);

		gbc.weightx = 1;
		gbc.gridx++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(cutsceneAfterBox, gbc);

		return panel;
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
		panel.add(getReceivableBoxes(), gbc);

		gbc.gridx++;
		panel.add(getWinBoxes(), gbc);

		gbc.gridx++;
		panel.add(getRewardBoxes(), gbc);
		
		return panel;
	}

	public JPanel getAvailablePanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(availableBox, gbc);

		if (availableBox.getSelectedIndex() > 0)
		{
			gbc.gridx++;
			panel.add(availableValueBox, gbc);
		}

		return panel;
	}

	public JScrollPane getReceivableBoxes()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		JScrollPane scroller = new JScrollPane(panel);
		
		TitledBorder border = new TitledBorder("Receivable Items");
		scroller.setBorder(border);
		
		if (receivableEntries.size() == 1)
		{
			border.setTitle("Receivable Item");
		}
		
		JButton addReceivableButton = new JButton("Add Receivable");
		addReceivableButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addNewReceivable();
			}
		});
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		for (int i = 0; i < receivableEntries.size(); i++)
		{			
			final int index = i;
			
			JButton xButton = new JButton("X");
			xButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					receivableEntries.remove(index);
					refreshDialog();
				}
			});
			
			gbc.gridx = 0;
			panel.add(xButton, gbc);
			
			gbc.gridx++;
			panel.add(receivableEntries.get(i), gbc);
			
			gbc.gridy++;
		}
		
		gbc.gridx = 0;
		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.SOUTH;
		panel.add(addReceivableButton, gbc);
		
		return scroller;
	}
	
	public JScrollPane getWinBoxes()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		JScrollPane scroller = new JScrollPane(panel);
		
		TitledBorder border = new TitledBorder("Win Conditions");
		scroller.setBorder(border);
		
		if (conditionEntries.size() == 1)
		{
			border.setTitle("Win Condition");
		}
		
		JButton addWinButton = new JButton("Add Win Condition");
		addWinButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addNewWinConditions();
			}
		});
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		for (int i = 0; i < conditionEntries.size(); i++)
		{			
			final int index = i;
			
			JButton xButton = new JButton("X");
			xButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					conditionEntries.remove(index);
					refreshDialog();
				}
			});
			
			gbc.gridx = 0;
			panel.add(xButton, gbc);
			
			gbc.gridx++;
			panel.add(conditionEntries.get(i), gbc);
			
			gbc.gridy++;
		}
		
		gbc.gridx = 0;
		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.SOUTH;
		panel.add(addWinButton, gbc);
		
		return scroller;
	}
	
	public JScrollPane getRewardBoxes()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		JScrollPane scroller = new JScrollPane(panel);
		
		TitledBorder border = new TitledBorder("Rewards");
		scroller.setBorder(border);
		
		if (rewardEntries.size() == 1)
		{
			border.setTitle("Reward");
		}
		
		JButton addRewardButton = new JButton("Add Reward");
		addRewardButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addNewReward();
			}
		});
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		for (int i = 0; i < rewardEntries.size(); i++)
		{
			final int index = i;
			
			JButton xButton = new JButton("X");
			xButton.setToolTipText("Remove reward " + (index + 1));
			xButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					rewardEntries.remove(index);
					refreshDialog();
				}
			});
			
			gbc.gridx = 0;
			gbc.weightx = 0;
			panel.add(xButton, gbc);
			
			gbc.gridx++;
			gbc.weightx = 1;
			panel.add(rewardEntries.get(i), gbc);
			
			gbc.gridy++;
		}
		
		gbc.gridx = 0;
		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.SOUTH;
		panel.add(addRewardButton, gbc);
		
		return scroller;
	}
	
	public JPanel getButtons()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		JButton okButton = new JButton("Ok");
		JButton cancelButton = new JButton("Cancel");
		
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				quest.name = nameField.getText();
				quest.description = descriptionField.getText();
				quest.dialogue1 = dialogue1Box.getSelectedIndex();
				quest.dialogue2 = dialogue2Box.getSelectedIndex();
				quest.dialogue3 = dialogue3Box.getSelectedIndex();
				quest.trigger = triggerBox.getSelectedIndex();
				quest.conditions = new ArrayList<WinCondition>();
				quest.rewards = new ArrayList<Reward>();
				quest.receivables = new ArrayList<ItemParameter>();
				quest.availableType = availableBox.getSelectedIndex();
				quest.availableValue = availableValueBox.getSelectedIndex();
				quest.exp = Integer.parseInt("" + expSpinner.getValue());
				quest.cutsceneBefore = -1;
				quest.cutsceneAfter = -1;

				if (cutsceneBeforeCheckBox.isSelected())
				{
					quest.cutsceneBefore = cutsceneBeforeBox.getSelectedIndex();
				}

				if (cutsceneAfterCheckBox.isSelected())
				{
					quest.cutsceneAfter = cutsceneAfterBox.getSelectedIndex();
				}

				for (int i = 0; i < conditionEntries.size(); i++)
				{
					WinCondition condition = new WinCondition();

					ConditionEntry cEntry = conditionEntries.get(i);

					condition.winType = cEntry.conditionType.getSelectedIndex();
					condition.keyIndex = cEntry.condition.getSelectedIndex();
					condition.amount = Integer.parseInt("" + cEntry.amount.getValue());

					quest.conditions.add(condition);
				}

				for (int i = 0; i < rewardEntries.size(); i++)
				{
					Reward reward = new Reward();

					RewardEntry rEntry = rewardEntries.get(i);

					reward.rewardType = rEntry.rewardType.getSelectedIndex();
					reward.keyIndex = rEntry.reward.getSelectedIndex();
					reward.amount = Integer.parseInt("" + rEntry.amount.getValue());

					quest.rewards.add(reward);
				}

				for (int i = 0; i < receivableEntries.size(); i++)
				{
					ItemParameter receivable = new ItemParameter();
					ReceivableEntry rEntry = receivableEntries.get(i);

					receivable.type = rEntry.items.getSelectedIndex();
					receivable.value = Integer.parseInt("" + rEntry.amount.getValue());

					quest.receivables.add(receivable);
				}

				if (index < quests.size())
				{
					quests.add(index, quest);
					quests.remove(index + 1);
				}
				else
				{
					quests.add(quest);
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
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(okButton, gbc);
	
		gbc.gridx++;
		panel.add(cancelButton, gbc);
		
		return panel;
	}
	
	private void addNewWinConditions()
	{
		conditionEntries.add(new ConditionEntry(this));
		refreshDialog();
	}

	private void addNewReward()
	{
		rewardEntries.add(new RewardEntry(this));
		refreshDialog();
	}

	private void addNewReceivable()
	{
		receivableEntries.add(new ReceivableEntry(this));
		refreshDialog();
	}
}

class ConditionEntry extends JPanel
{
	public static final int TYPE_ITEM = 0;

	public JComboBox conditionType;
	public JComboBox condition;
	public JSpinner amount;
	
	private Object[] winConditions = {"Find Item", "Find Character"};
	private QuestInputDialog owner;

	public ConditionEntry(QuestInputDialog parent)
	{
		owner = parent;
		conditionType = new JComboBox(winConditions);
		condition = new JComboBox(owner.items.toArray());

		SpinnerNumberModel numberModel = new SpinnerNumberModel(new Integer(1), new Integer(1), new Integer(100), new Integer(1));

		amount = new JSpinner(numberModel);

		conditionType.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				refreshPanel();
			}
		});

		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx++;
		add(conditionType, gbc);
		
		gbc.gridx++;
		add(condition, gbc);

		gbc.gridx++;
		add(amount, gbc);
	}

	private void refreshPanel()
	{
		removeAll();

		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx++;
		add(conditionType, gbc);

		switch (conditionType.getSelectedIndex())
		{
			case 0:
				condition = new JComboBox(owner.items.toArray());
				gbc.gridx++;
				add(condition, gbc);
				gbc.gridx++;
				add(amount, gbc);
				break;
			case 1:
				condition = new JComboBox(owner.characters.toArray());
				gbc.gridx++;
				add(condition, gbc);
				break;
		}

		validate();
		repaint();
	}
}

class RewardEntry extends JPanel
{
	public static final int TYPE_ITEM = 0;
	public static final int TYPE_QUEST = 1;

	public JComboBox rewardType;
	public JComboBox reward;
	public JSpinner amount;
	
	private Object[] rewards = {"Get Item", "Get New Quest"};
	private QuestInputDialog owner;

	public RewardEntry(QuestInputDialog parent)
	{
		owner = parent;
		rewardType = new JComboBox(rewards);
		reward = new JComboBox(owner.items.toArray());

		SpinnerNumberModel numberModel = new SpinnerNumberModel(new Integer(1), new Integer(1), new Integer(100), new Integer(1));
		amount = new JSpinner(numberModel);

		rewardType.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{	
				refreshPanel();
			}
		});

		setLayout(new GridBagLayout());
		refreshPanel();
	}

	private void refreshPanel()
	{
		removeAll();

		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx++;
		add(rewardType, gbc);

		switch (rewardType.getSelectedIndex())
		{
			case 0:
				reward = new JComboBox(owner.items.toArray());
				gbc.gridx++;
				add(reward, gbc);
				gbc.gridx++;
				add(amount, gbc);
				break;
			case 1:
				reward = new JComboBox(owner.quests.toArray());
				gbc.gridx++;
				add(reward, gbc);
				break;
		}

		validate();
		repaint();
	}
}

class ReceivableEntry extends JPanel
{
	public JComboBox items;
	public JSpinner amount;
	
	private QuestInputDialog owner;

	public ReceivableEntry(QuestInputDialog parent)
	{
		owner = parent;
		items = new JComboBox(owner.items.toArray());

		SpinnerNumberModel numberModel = new SpinnerNumberModel(new Integer(1), new Integer(1), new Integer(100), new Integer(1));
		amount = new JSpinner(numberModel);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(items, gbc);

		gbc.gridx++;
		add(amount, gbc);
	}
}
