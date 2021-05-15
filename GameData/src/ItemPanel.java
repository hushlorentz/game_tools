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

public class ItemPanel extends JPanel 
{
	public ArrayList<Item> items;
	
	private MainFrame parent;
	private JList itemList;
	
	public ItemPanel(MainFrame parent)
	{
		this.parent = parent;
		items = new ArrayList<Item>();
		itemList = new JList();
		itemList.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e) 
			{
				if (e.getClickCount() == 2 &&
					e.getButton() == MouseEvent.BUTTON1 &&
					itemList.getSelectedIndex() > -1)
				{
					ItemInputDialog dialog = new ItemInputDialog(items, itemList.getSelectedIndex());
					dialog.setVisible(true);
					refreshList();
				}
			}

			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
		});
		
		JScrollPane scroller = new JScrollPane(itemList);
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
				ItemInputDialog dialog = new ItemInputDialog(items, items.size());
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
		itemList.setListData(items.toArray());
	}
}

class Item
{
	public String name;
	public String description;
	public int itemType;
	public ArrayList<ItemParameter> parameters;

	public Item()
	{
		name = "";
		description = "";
		itemType = 0;
		parameters = new ArrayList<ItemParameter>();
	}
	
	public String toString()
	{
		return name;
	}
}

class ItemParameter
{
	public int type;
	public int value;
}
