import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

public class DialogueInputDialog extends JDialog
{
	private ArrayList<String> list;
	private int index;
	private JTextArea textArea;
	
	public DialogueInputDialog(ArrayList<String> stringList, int selected)
	{
		list = stringList;
		index = selected;
		
		setModal(true);
		setSize(new Dimension(300, 200));
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		add(getPanel(), gbc);
	}
	
	private JPanel getPanel()
	{
		JPanel panel = new JPanel();
		
		textArea = new JTextArea();
		textArea.getDocument().addDocumentListener(new DocumentListener()
		{
			public void insertUpdate(DocumentEvent e)
			{
				if (textArea.getText().contains("\n"))
				{
					saveAndClose();
				}
			}
			public void changedUpdate(DocumentEvent e) {}
			public void removeUpdate(DocumentEvent e) {}
		});
		
		if (index < list.size())
		{
			textArea.setText(list.get(index));
		}

		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		
		JScrollPane scroller = new JScrollPane(textArea);
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				saveAndClose();
			}
		});
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				setVisible(false);
			}
		});
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		
		panel.add(scroller, gbc);
		
		gbc.weighty = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridwidth = 1;
		gbc.gridy++;
		gbc.anchor = GridBagConstraints.EAST;
		panel.add(okButton, gbc);
		
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx++;
		panel.add(cancelButton, gbc);
		
		return panel;
	}

	private void saveAndClose()
	{
		String text = textArea.getText().trim();
		
		if (!text.isEmpty())
		{
			if (index == list.size())
			{
				list.add(text);	
			}
			else
			{
				list.add(index, text);	
				list.remove(index + 1); //now list has one too many elements
			}
		}
		
		setVisible(false);
	}
}
