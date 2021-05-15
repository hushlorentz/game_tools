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
import javax.swing.SpinnerNumberModel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class CharacterInputDialog extends JDialog 
{
	public ArrayList<Item> items;

	private final String[] aiTypes = {"Human", "Zombie", "Turret", "Zombie Spawn"};
	private final String[] interactionTypes = {"Dialogue", "Quests"};
	private static final int INTERACTION_DIALOGUE = 0;
	private static final int INTERACTION_QUEST = 1;

	private JTextField hpField, attackField, speedField, defenseField;
	private ArrayList<InventoryItem> inventory;
	private ArrayList<JComboBox> interactions;
	private JComboBox aiBox;
	private JComboBox interactionBox;
	private JTextField nameField, animField, imageField;
	private JTextField introField;
	private ArrayList<Character> characters;
	private ArrayList<String> dialogue;
	private ArrayList<Quest> quests;
	private Character character;
	private int index;
	private JPanel propsPanel, statsPanel;
	
	public CharacterInputDialog(ArrayList<Character> characters, ArrayList<Item> items, ArrayList<Quest> quests, ArrayList<String> dialogue, int selection)
	{
		this.quests = quests;
		this.items = items;
		this.characters = characters;
		this.dialogue = dialogue;
		index = selection;
		
		JTabbedPane tabs = new JTabbedPane();
		statsPanel = new JPanel();
		statsPanel.setLayout(new GridBagLayout());
		propsPanel = new JPanel();
		propsPanel.setLayout(new GridBagLayout());

		tabs.add(propsPanel, "Properties");
		tabs.add(statsPanel, "Stats");
		add(tabs);

		nameField = new JTextField();
		animField = new JTextField();
		imageField = new JTextField();
		introField = new JTextField();
		aiBox = new JComboBox(aiTypes);
		inventory = new ArrayList<InventoryItem>();
		interactions = new ArrayList<JComboBox>();
		interactionBox = new JComboBox(interactionTypes);
		interactionBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					introField.setText("");
					interactions = new ArrayList<JComboBox>();
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

		JLabel hpLabel = new JLabel("Hit Points: ");
		JLabel attackLabel = new JLabel("Attack: ");
		JLabel speedLabel = new JLabel("Speed: ");
		JLabel defenseLabel = new JLabel("Defense: ");
		hpField = new JTextField();
		attackField = new JTextField();
		defenseField = new JTextField();
		speedField = new JTextField();

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		statsPanel.add(hpLabel, gbc);

		gbc.gridx++;
		gbc.weightx = 1;
		statsPanel.add(hpField, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0;
		statsPanel.add(attackLabel, gbc);

		gbc.weightx = 1;
		gbc.gridx++;
		statsPanel.add(attackField, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0;
		statsPanel.add(defenseLabel, gbc);

		gbc.weightx = 1;
		gbc.gridx++;
		statsPanel.add(defenseField, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0;
		statsPanel.add(speedLabel, gbc);

		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.gridx++;
		statsPanel.add(speedField, gbc);

		if (index < characters.size())
		{
			character = new Character(characters.get(index));
		}
		else 
		{
			character = new Character();
		}

		nameField.setText(character.name);
		animField.setText(character.animPath);
		imageField.setText(character.imagePath);
		aiBox.setSelectedIndex(character.aiType);
		interactionBox.setSelectedIndex(character.interactionType);
		introField.setText(character.introText);

		for (int i = 0; i < character.inventory.size(); i++)
		{
			InventoryItem inv = new InventoryItem(this);
			CharacterInventory ci = character.inventory.get(i);

			inv.item.setSelectedIndex(ci.item);
			inv.amount.setValue(ci.amount);
			inv.percentage.setValue(ci.percentage);

			inventory.add(inv);
		}

		for (int i = 0; i < character.interactions.size(); i++)
		{
			JComboBox newBox;
			
			if (character.interactionType == CharacterInputDialog.INTERACTION_DIALOGUE)
			{
				newBox = new JComboBox(dialogue.toArray());
			}
			else
			{
				newBox = new JComboBox(quests.toArray());
			}
			newBox.setSelectedIndex(character.interactions.get(i));
			interactions.add(newBox);
		}
		hpField.setText("" + character.hitpoints);
		attackField.setText("" + character.attack);
		defenseField.setText("" + character.defense);
		speedField.setText("" + character.speed);

		add(getButtons(), "South");

		refreshDialog();
		
		setModal(true);
		setSize(new Dimension(800, 400));
	}
	
	private void refreshDialog()
	{
		propsPanel.removeAll();
		
		JLabel nameLabel = new JLabel("Name: ");
		JLabel animLabel = new JLabel("Animation: ");
		JLabel imageLabel = new JLabel("Image: ");
		JLabel introLabel = new JLabel("Intro Text: ");
		JLabel interactionLabel = new JLabel("Interaction: ");
		JLabel aiLabel = new JLabel("AI: ");
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		propsPanel.add(nameLabel, gbc);
		
		gbc.weightx = 1;
		gbc.gridx++;
		propsPanel.add(nameField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0;
		propsPanel.add(animLabel, gbc);
		
		gbc.gridx++;
		propsPanel.add(animField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		propsPanel.add(imageLabel, gbc);
		
		gbc.gridx++;
		propsPanel.add(imageField, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		propsPanel.add(aiLabel, gbc);

		gbc.gridx++;
		propsPanel.add(aiBox, gbc);

		gbc.gridwidth = 1;
		gbc.gridy++;
		gbc.gridx = 0;
		propsPanel.add(interactionLabel, gbc);

		gbc.gridx++;
		propsPanel.add(interactionBox, gbc);

		if (interactionBox.getSelectedIndex() == 1)
		{
			gbc.gridx = 0;
			gbc.gridy++;
			propsPanel.add(introLabel, gbc);

			gbc.gridx++;
			propsPanel.add(introField, gbc);
		}
		
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		propsPanel.add(getBoxes(), gbc);
		
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
		panel.add(getInventoryBoxes(), gbc);
		
		gbc.gridx++;
		panel.add(getInteractionBoxes(), gbc);
		
		return panel;
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
			panel.add(inventory.get(i).item, gbc);
			
			gbc.gridx++;
			panel.add(inventory.get(i).amount, gbc);
			
			gbc.gridx++;
			panel.add(inventory.get(i).percentage, gbc);
			
			gbc.gridy++;
		}
		
		gbc.gridx = 0;
		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.gridwidth = 4;
		gbc.anchor = GridBagConstraints.SOUTH;
		panel.add(addInventoryButton, gbc);
		
		return scroller;
	}
	
	public JScrollPane getInteractionBoxes()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		JScrollPane scroller = new JScrollPane(panel);
		String borderText = "Quests";
		String buttonText = "Add Quest";
		String removeText = "Remove quest ";

		if (interactionBox.getSelectedIndex() == CharacterInputDialog.INTERACTION_DIALOGUE)
		{
			borderText = "Dialogue";
			buttonText = "Add Dialogue";
			removeText = "Remove dialogue ";
		}
		
		TitledBorder border = new TitledBorder(borderText);
		scroller.setBorder(border);
		
		JButton addQuestButton = new JButton(buttonText);
		addQuestButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addNewInteraction();
			}
		});
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		for (int i = 0; i < interactions.size(); i++)
		{
			final int index = i;
			
			JButton xButton = new JButton("X");
			xButton.setToolTipText(removeText + (index + 1));
			xButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					interactions.remove(index);
					refreshDialog();
				}
			});
			
			gbc.gridx = 0;
			gbc.weightx = 0;
			panel.add(xButton, gbc);
			
			gbc.gridx++;
			gbc.weightx = 1;
			panel.add(interactions.get(i), gbc);
			
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
	
	private JPanel getButtons()
	{
		JPanel panel = new JPanel();
		JButton okButton = new JButton("Ok");
		JButton cancelButton = new JButton("Cancel");
		
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				character.name = nameField.getText();
				character.animPath = animField.getText();
				character.imagePath = imageField.getText();
				character.aiType = aiBox.getSelectedIndex();
				character.introText = introField.getText();
				character.interactions = new ArrayList<Integer>();
				character.inventory = new ArrayList<CharacterInventory>();
				character.interactionType = interactionBox.getSelectedIndex();
				character.hitpoints = Integer.parseInt(hpField.getText());
				character.attack = Integer.parseInt(attackField.getText());
				character.defense = Integer.parseInt(defenseField.getText());
				character.speed = Integer.parseInt(speedField.getText());

				for (int i = 0; i < inventory.size(); i++)
				{
					InventoryItem inv = inventory.get(i);
					CharacterInventory ci = new CharacterInventory();

					ci.item = inv.item.getSelectedIndex();
					ci.amount = Integer.parseInt("" + inv.amount.getValue());
					ci.percentage = Integer.parseInt("" + inv.percentage.getValue());

					character.inventory.add(ci);
				}
				for (int i = 0; i < interactions.size(); i++)
				{
					character.interactions.add(interactions.get(i).getSelectedIndex());
				}

				if (index < characters.size())
				{
					characters.add(index, character);
					characters.remove(index + 1);
				}
				else
				{
					characters.add(character);
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
		InventoryItem newBox = new InventoryItem(this);
		inventory.add(newBox);
		refreshDialog();
	}

	private void addNewInteraction()
	{
		JComboBox newBox;

		if (interactionBox.getSelectedIndex() == CharacterInputDialog.INTERACTION_QUEST)
		{
			newBox = new JComboBox(quests.toArray());		
		}
		else
		{
			newBox = new JComboBox(dialogue.toArray());		
		}

		interactions.add(newBox);
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
}

class InventoryItem
{
	public JComboBox item;
	public JSpinner amount;
	public JSpinner percentage;
	public CharacterInputDialog owner;

	public InventoryItem(CharacterInputDialog parent)
	{
		owner = parent;
		item = new JComboBox(owner.items.toArray());
		amount = new JSpinner(new SpinnerNumberModel(new Integer(1), new Integer(1), new Integer(100), new Integer(1)));
		percentage = new JSpinner(new SpinnerNumberModel(new Integer(1), new Integer(1), new Integer(100), new Integer(1)));
	}
}
