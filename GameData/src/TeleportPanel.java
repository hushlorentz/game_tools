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

public class TeleportPanel extends JPanel 
{
	public ArrayList<Teleporter> teleporters;
	
	private MainFrame owner;
	private JList teleporterList;
	
	public TeleportPanel(MainFrame parent)
	{
		owner = parent;
		teleporters = new ArrayList<Teleporter>();
		teleporterList = new JList();
		teleporterList.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e) 
			{
				if (e.getClickCount() == 2 &&
					e.getButton() == MouseEvent.BUTTON1 &&
					teleporterList.getSelectedIndex() > -1)
				{
					TeleportInputDialog dialog = new TeleportInputDialog(teleporters, owner.getItems(), owner.getQuests(), owner.getMaps(), teleporterList.getSelectedIndex());
					dialog.setVisible(true);
					refreshList();
				}
			}

			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			
		});
		
		JScrollPane scroller = new JScrollPane(teleporterList);
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
	
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
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
				TeleportInputDialog dialog = new TeleportInputDialog(teleporters, owner.getItems(), owner.getQuests(), owner.getMaps(), teleporters.size());
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
		teleporterList.setListData(teleporters.toArray());
	}
}

class Teleporter
{
	public String name;
	public String lockText;
	public String locationText;
	public ArrayList<TeleporterDestination> destinations;
	public int map;
	public int xCoord, yCoord;
	public ArrayList<LockParameter> locks;
	public ArrayList<Integer> quests;
	
	public Teleporter()
	{
		name = "";
		lockText = "";
		locationText = "";
		xCoord = 0;
		yCoord = 0;
		map = -1;
		locks = new ArrayList<LockParameter>();
		quests = new ArrayList<Integer>();
		destinations = new ArrayList<TeleporterDestination>();
	}

	public Teleporter(Teleporter teleporter)
	{
		name = teleporter.name;
		lockText = teleporter.lockText;
		locationText = teleporter.locationText;
		map = teleporter.map;
		xCoord = teleporter.xCoord;
		yCoord = teleporter.yCoord;
		locks = new ArrayList<LockParameter>(teleporter.locks);
		quests = new ArrayList<Integer>(teleporter.quests);
		destinations = new ArrayList<TeleporterDestination>(teleporter.destinations);
	}
	
	public String toString()
	{
		return name;
	}
}

class TeleporterDestination
{
	public int xCoord;
	public int yCoord;
	public int map;
	public int condition;
	public int conditionValue;
}
