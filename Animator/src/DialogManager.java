import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.IllegalFormatException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class DialogManager 
{
	private static JDialog dialog;
	private static AnimMainFrame parent;
	
	public static JDialog getBoundsDialog(AnimMainFrame owner)
	{
		parent = owner;
		dialog = new JDialog();
		dialog.setModal(true);
		dialog.setLayout(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();
		
		final JTextField widthField = new JTextField();
		final JTextField heightField = new JTextField();
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					int width = Integer.parseInt(widthField.getText());
					int height = Integer.parseInt(heightField.getText());
					
					parent.drawBounds(width, height);
					dialog.dispose();
				}
				catch (NumberFormatException ex)
				{
					JOptionPane.showMessageDialog(dialog, ex.getMessage());
				}
			}		
		});
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dialog.dispose();
			}
		});
		
		cons.weightx = 0;
		cons.weighty = 1;
		cons.gridx = 0;
		cons.gridy = 0;
		dialog.add(new JLabel("Width: "), cons);
		
		cons.gridx++;
		cons.fill = GridBagConstraints.HORIZONTAL;
		dialog.add(widthField, cons);

		cons.fill = GridBagConstraints.NONE;
		cons.gridx = 0;
		cons.gridy++;
		dialog.add(new JLabel("Height: "), cons);

		cons.gridx++;
		cons.fill = GridBagConstraints.HORIZONTAL;
		dialog.add(heightField, cons);
		
		cons.gridx = 0;
		cons.gridy++;
		dialog.add(okButton, cons);
		
		cons.gridx++;
		dialog.add(cancelButton, cons);
		
		dialog.pack();
		dialog.setResizable(false);
		
		return dialog;
	}
}
