import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public class AnimOutput extends JPanel 
{
	private JTextArea stats;
	private JTextArea output;
	private AnimMainFrame parent;
	
	public AnimOutput(AnimMainFrame owner)
	{
		parent = owner;
		setLayout(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();
		
		JTabbedPane tabs = new JTabbedPane();
		
		tabs.add(getStatsPanel(), "Stats");
		tabs.add(getOutputPanel(), "Output");
		cons.weightx = 1;
		cons.weighty = 1;
		cons.fill = GridBagConstraints.BOTH;
		add(tabs, cons);
		
		setPreferredSize(new Dimension(0, 200));
		setMinimumSize(new Dimension(0, 200));
	}
	
	private JPanel getStatsPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();
		
		cons.weightx = 1;
		cons.weighty = 1;
		cons.fill = GridBagConstraints.BOTH;
		cons.gridwidth = 1;
		
		stats = new JTextArea();
		stats.setEditable(false);
		JScrollPane scroller = new JScrollPane(stats);
		panel.add(scroller, cons);	
		
		return panel;
	}
	
	private JPanel getOutputPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();
		
		cons.weightx = 1;
		cons.weighty = 1;
		cons.fill = GridBagConstraints.BOTH;
		cons.gridwidth = 1;
		
		output = new JTextArea();
		JScrollPane scroller = new JScrollPane(output);
		panel.add(scroller, cons);	
		
		return panel;
	}

	public void setStats(String string) 
	{
		stats.setText(string);
	}
}
