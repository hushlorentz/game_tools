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


public class QuestPanel extends JPanel 
{
	public ArrayList<Quest> quests;
	
	private JList questList;
	private MainFrame owner;
	
	public QuestPanel(MainFrame parent)
	{
		owner = parent;
		quests = new ArrayList<Quest>();
		questList = new JList();
		questList.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e) 
			{
				if (e.getClickCount() == 2 &&
					e.getButton() == MouseEvent.BUTTON1 &&
					questList.getSelectedIndex() > -1)
				{
					QuestInputDialog dialog = new QuestInputDialog(quests, owner.getDialogue(), owner.getItems(), owner.getCharacters(), owner.getCutscenes(), questList.getSelectedIndex());
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
		JScrollPane scroller = new JScrollPane(questList);
		
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
				QuestInputDialog dialog = new QuestInputDialog(quests, owner.getDialogue(), owner.getItems(), owner.getCharacters(), owner.getCutscenes(), quests.size());
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
		questList.setListData(quests.toArray());
	}
}

class Quest
{
	public String name;
	public String description;
	public int dialogue1;
	public int dialogue2;
	public int dialogue3;
	public int cutsceneBefore;
	public int cutsceneAfter;
	public int trigger;
	public int availableType;
	public int availableValue;
	public ArrayList<ItemParameter> receivables;
	public ArrayList<WinCondition> conditions;
	public ArrayList<Reward> rewards;
	public int exp;
	
	public Quest(Quest quest)
	{
		name = quest.name;
		dialogue1 = quest.dialogue1;
		dialogue2 = quest.dialogue2;
		dialogue3 = quest.dialogue3;
		cutsceneBefore = quest.cutsceneBefore;
		cutsceneAfter = quest.cutsceneAfter;
		conditions = new ArrayList<WinCondition>(quest.conditions);
		rewards = new ArrayList<Reward>(quest.rewards);
		receivables = new ArrayList<ItemParameter>(quest.receivables);
		trigger = quest.trigger;
		description = quest.description;
		exp = quest.exp;
		availableType = quest.availableType;
		availableValue = quest.availableValue;
	}
	public Quest()
	{
		name = "";
		description = "";
		dialogue1 = -1;
		dialogue2 = -1;
		dialogue3 = -1;
		cutsceneBefore = -1;
		cutsceneAfter = -1;
		conditions = new ArrayList<WinCondition>();
		rewards = new ArrayList<Reward>();
		receivables = new ArrayList<ItemParameter>();
		trigger = 0;
		exp = 100;
		availableType = 0;
		availableValue = 0;
	}
	
	public String toString()
	{
		return name;
	}
}

class WinCondition
{
	public int winType;
	public int keyIndex;
	public int amount;

	public WinCondition()
	{
		winType = 0;
		keyIndex = 0;
		amount = 1;
	}
}

class Reward
{
	public int rewardType;
	public int keyIndex;
	public int amount;

	public Reward()
	{
		rewardType = 0;
		keyIndex = 0;
		amount = 1;
	}
}
