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

class CutscenePanel extends JPanel
{
	public ArrayList<String> cutscenes;

	private MainFrame owner;
	private JList cutsceneList;

	public CutscenePanel(MainFrame parent)
	{
		owner = parent;
		cutscenes = new ArrayList<String>();

		cutsceneList = new JList();
		cutsceneList.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2 &&
					e.getButton() == MouseEvent.BUTTON1)
				{
					if (cutsceneList.getSelectedIndex() > -1)
					{
						JFileChooser chooser = new JFileChooser();
						chooser.setFileFilter(new Filter("swf", "Cutscene Flash File (.swf)"));
					
						int result = chooser.showOpenDialog(owner);

						if (result == JFileChooser.APPROVE_OPTION)
						{
							cutscenes.add(cutsceneList.getSelectedIndex(), chooser.getSelectedFile().getName());
							cutscenes.remove(cutsceneList.getSelectedIndex() + 1);
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
		JScrollPane scroller = new JScrollPane(cutsceneList);

		JButton addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new Filter("swf", "Cutscene Flash File (.swf)"));
			
				int result = chooser.showOpenDialog(owner);

				if (result == JFileChooser.APPROVE_OPTION)
				{
					cutscenes.add(chooser.getSelectedFile().getName());	
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
		cutsceneList.setListData(cutscenes.toArray());
		repaint();
	}
}
