import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class DialoguePanel extends JPanel
{
	private MainFrame parent;
	public ArrayList<String> stringList;
	private JList dialogueList;
	private JScrollPane listScroller;
	
	public DialoguePanel(MainFrame parent)
	{
		this.parent = parent;
		
		stringList = new ArrayList<String>();
		
		dialogueList = new JList(stringList.toArray());
		dialogueList.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) 
			{
				if (e.getClickCount() == 2 &&
					e.getButton() == MouseEvent.BUTTON1 &&
					dialogueList.getSelectedIndex() > -1)
				{
					DialogueInputDialog dialog = new DialogueInputDialog(stringList, dialogueList.getSelectedIndex());
					dialog.setVisible(true);
					refreshList();
				}
			}

			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
		});
		listScroller = new JScrollPane(dialogueList);
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		add(listScroller, gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
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
				DialogueInputDialog dialog = new DialogueInputDialog(stringList, stringList.size());
				dialog.setVisible(true);
				refreshList();
			}
		});
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.CENTER;
		panel.add(addButton, gbc);
		
		return panel;
	}
	
	public void refreshList()
	{
		dialogueList.setListData(stringList.toArray());
	}
}
