import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class ItemInputDialog extends JDialog 
{
	private JTextField nameField;
	private JTextField descriptionField;
	private JComboBox typeBox;
	private ArrayList<ParameterBox> parameterBoxes;
	private ArrayList<Item> items;
	private int index;
	
	public ItemInputDialog(ArrayList<Item> items, int selection)
	{
		nameField = new JTextField();
		descriptionField = new JTextField();
		parameterBoxes = new ArrayList<ParameterBox>();

		Object[] types = {"Health", "Armour", "Ammo", "Key Item"};
		typeBox = new JComboBox(types);
		this.items = items;
		index = selection;
		
		if (index < items.size())
		{
			Item item = items.get(index);
			nameField.setText(item.name);
			descriptionField.setText(item.description);
			typeBox.setSelectedIndex(item.itemType);

			for (int i = 0; i < item.parameters.size(); i++)
			{
				ItemParameter p = item.parameters.get(i);
				ParameterBox newBox = new ParameterBox();

				newBox.type.setSelectedIndex(p.type);
				newBox.value.setText("" + p.value);
				parameterBoxes.add(newBox);
			}
		}
		
		setLayout(new GridBagLayout());

		refreshDialog();
		
		setModal(true);
		setSize(new Dimension(400, 400));
	}

	public void refreshDialog()
	{
		getContentPane().removeAll();

		GridBagConstraints gbc = new GridBagConstraints();
		
		JLabel nameLabel = new JLabel("Name: ");
		JLabel descriptionLabel = new JLabel("Description: ");
		JLabel typeLabel = new JLabel("Item Type: ");
			
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(nameLabel, gbc);
		
		gbc.weightx = 1;
		gbc.gridx++;
		add(nameField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0;
		add(descriptionLabel, gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx++;
		add(descriptionField, gbc);

		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy++;
		add(getParameterPanel(), gbc);

		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weighty = 0;
		gbc.weightx = 0;
		add(typeLabel, gbc);

		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx++;
		add(typeBox, gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		add(getButtons(), gbc);
	}

	private JScrollPane getParameterPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		JButton paramButton = new JButton("Add Parameter");
		paramButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addParameter();
			}
		});

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTH;

		for (int i = 0; i < parameterBoxes.size(); i++)
		{
			final int index = i;
			ParameterBox box = parameterBoxes.get(i);

			JButton removeButton = new JButton("X");
			removeButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					parameterBoxes.remove(index);
					refreshDialog();
					validate();
					repaint();
				}		
			});

			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.NONE;
			gbc.gridx = 0;
			panel.add(removeButton, gbc);

			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx++;
			panel.add(box.type, gbc);

			gbc.gridx++;
			panel.add(box.value, gbc);
			gbc.gridy++;
		}

		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.gridx = 0;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(paramButton, gbc);

		return new JScrollPane(panel);
	}

	public void addParameter()
	{
		parameterBoxes.add(new ParameterBox());
		refreshDialog();
		validate();
		repaint();
	}
	
	public JPanel getButtons()
	{
		JPanel panel = new JPanel();
		JButton okButton = new JButton("Ok");
		JButton cancelButton = new JButton("Cancel");
		
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Item item = new Item();
				item.name = nameField.getText();
				item.description = descriptionField.getText();
				item.itemType = typeBox.getSelectedIndex();
				item.parameters = new ArrayList<ItemParameter>();

				for (int i = 0; i < parameterBoxes.size(); i++)
				{
					ParameterBox box = parameterBoxes.get(i);

					ItemParameter p = new ItemParameter();
					p.type = box.type.getSelectedIndex();
					p.value = Integer.parseInt(box.value.getText());
					item.parameters.add(p);
				}

				if (index < items.size())
				{
					items.add(index, item);
					items.remove(index + 1);
				}
				else
				{
					items.add(item);
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
}

class ParameterBox
{
	public JComboBox type;
	public JTextField value;

	public ParameterBox()
	{
		Object[] types = {"Heal"};
		type = new JComboBox(types);
		value = new JTextField();
	}
}
