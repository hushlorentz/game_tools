import java.util.ArrayList;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFileChooser;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JList;

class MapPanel extends JPanel
{
	public ArrayList<String> maps;

	private MainFrame owner;
	private JList mapList;

	public MapPanel(MainFrame parent)
	{
		owner = parent;
		maps = new ArrayList<String>();

		mapList = new JList();
		mapList.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2 &&
					e.getButton() == MouseEvent.BUTTON1)
				{
					if (mapList.getSelectedIndex() > -1)
					{
						JFileChooser chooser = new JFileChooser();
						chooser.setFileFilter(new Filter("map", "Map File (.map)"));
					
						int result = chooser.showOpenDialog(owner);

						if (result == JFileChooser.APPROVE_OPTION)
						{
							maps.add(mapList.getSelectedIndex(), chooser.getSelectedFile().getName());
							maps.remove(mapList.getSelectedIndex() + 1);
							refreshList();
						}
					}
				}
			}

			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
		});

		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		JScrollPane scroller = new JScrollPane(mapList);

		JButton addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new Filter("map", "Map File (.map)"));
			
				int result = chooser.showOpenDialog(owner);

				if (result == JFileChooser.APPROVE_OPTION)
				{
					maps.add(chooser.getSelectedFile().getName());	
					refreshList();
				}
			}
		});

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		add(scroller, gbc);

		gbc.gridy++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weighty = 0;
		add(addButton, gbc);
	}

	public void refreshList()
	{
		mapList.setListData(maps.toArray());
		repaint();
	}
}
