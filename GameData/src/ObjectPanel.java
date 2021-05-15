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

public class ObjectPanel extends JPanel 
{
	public ArrayList<GameObject> objects;
	
	private MainFrame owner;
	private JList objectList;
	
	public ObjectPanel(MainFrame parent)
	{
		owner = parent;
		objects = new ArrayList<GameObject>();
		objectList = new JList();
		objectList.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e) 
			{
				if (e.getClickCount() == 2 &&
					e.getButton() == MouseEvent.BUTTON1 &&
					objectList.getSelectedIndex() > -1)
				{
					ObjectInputDialog dialog = new ObjectInputDialog(objects, owner.getItems(), owner.getQuests(), owner.getTeleporters(), owner.getCutscenes(), objectList.getSelectedIndex());
					dialog.setVisible(true);
					refreshList();
				}
			}

			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			
		});
		
		JScrollPane scroller = new JScrollPane(objectList);
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
				ObjectInputDialog dialog = new ObjectInputDialog(objects, owner.getItems(), owner.getQuests(), owner.getTeleporters(), owner.getCutscenes(), objects.size());
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
		objectList.setListData(objects.toArray());
	}
}

class GameObject 
{
	public String name;
	public String lockText;
	public String animPath, imagePath;
	public int interactionType;
	public int interactionValue;
	public int cutscene;
	public ArrayList<Integer> inventory;
	public ArrayList<Integer> amounts;
	public ArrayList<LockParameter> locks;
	
	public GameObject()
	{
		name = "";
		lockText = "";
		animPath = "";
		imagePath = "";
		interactionType = 0;
		interactionValue = 0;
		cutscene = -1;
		inventory = new ArrayList<Integer>();
		amounts = new ArrayList<Integer>();
		locks = new ArrayList<LockParameter>();
	}

	public GameObject(GameObject object)
	{
		name = object.name;
		lockText = object.lockText;
		animPath = object.animPath;
		imagePath = object.imagePath;
		interactionType = object.interactionType;
		interactionValue = object.interactionValue;
		cutscene = object.cutscene;
		inventory = new ArrayList<Integer>(object.inventory);
		amounts = new ArrayList<Integer>(object.amounts);
		locks = new ArrayList<LockParameter>(object.locks);
	}
	
	public String toString()
	{
		return name;
	}
}

class LockParameter
{
	public int type;
	public int value;
	public int amount;

	public LockParameter()
	{
		type = 0;
		value = 0;
		amount = 0;
	}
}
