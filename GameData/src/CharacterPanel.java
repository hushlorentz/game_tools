import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class CharacterPanel extends JPanel
{
	public ArrayList<Character> characters;
	
	private JList characterList;
	private MainFrame owner;
	
	public CharacterPanel(MainFrame parent)
	{
		owner = parent;
		
		characters = new ArrayList<Character>();
		characterList = new JList();
		characterList.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e) 
			{
				if (e.getClickCount() == 2 &&
					e.getButton() == MouseEvent.BUTTON1 &&
					characterList.getSelectedIndex() > -1)
				{
					CharacterInputDialog dialog = new CharacterInputDialog(characters, owner.getItems(), owner.getQuests(), owner.getDialogue(), characterList.getSelectedIndex());
					dialog.setVisible(true);
					refreshList();
				}
			}

			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			
		});
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		JScrollPane scroller = new JScrollPane(characterList);
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		
		add(scroller, gbc);
		
		gbc.weighty = 0;
		gbc.gridy++;
		add(getButtons(), gbc);
	}
	
	private JPanel getButtons()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		JButton addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				CharacterInputDialog dialog = new CharacterInputDialog(characters, owner.getItems(), owner.getQuests(), owner.getDialogue(), characters.size());
				dialog.setVisible(true);
				refreshList();
			}
		});
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(addButton, gbc);
		
		return panel;
	}
	
	public void refreshList()
	{
		characterList.setListData(characters.toArray());
	}
}

class Character
{
	public String name, animPath, imagePath, introText;
	public int aiType;
	public int interactionType; //0 for dialogue, 1 for quest
	public ArrayList<CharacterInventory> inventory;
	public ArrayList<Integer> interactions;
	public int hitpoints, attack, defense, speed;
	
	public Character()
	{
		this.name = "";
		animPath = "";
		imagePath = "";
		introText = "";
		aiType = 0;
		interactions = new ArrayList<Integer>();
		inventory = new ArrayList<CharacterInventory>();
		interactionType = 0;
		hitpoints = 100;
		attack = 10;
		defense = 10;
		speed = 4;
	}

	public Character(Character c)
	{
		this.name = c.name;
		this.animPath = c.animPath;
		this.imagePath = c.imagePath;
		this.introText = c.introText;
		this.aiType = c.aiType;
		this.inventory = new ArrayList<CharacterInventory>(c.inventory);
		this.interactions = new ArrayList<Integer>(c.interactions);
		this.interactionType = c.interactionType;
		this.hitpoints = c.hitpoints;
		this.attack = c.attack;
		this.defense = c.defense;
		this.speed = c.speed;
	}
	
	public String toString()
	{
		return name;
	}
}

class CharacterInventory
{
	public int item;
	public int amount;
	public int percentage;

	public CharacterInventory()
	{
		item = 0;
		amount = 0;
		percentage = 0;
	}
}
