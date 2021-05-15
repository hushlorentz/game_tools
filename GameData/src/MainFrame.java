import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;

public class MainFrame extends JFrame 
{
	private DialoguePanel dPanel;
	private QuestPanel qPanel;
	private ItemPanel iPanel;
	private CharacterPanel cPanel;
	private ObjectPanel oPanel;
	private MapPanel mPanel;
	private TeleportPanel tPanel;
	private CutscenePanel cuPanel;
	
	public MainFrame()
	{
		JMenuBar menu = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		
		JMenuItem openItem = new JMenuItem("Open");
		JMenuItem saveItem = new JMenuItem("Save");
		JMenuItem exportItem = new JMenuItem("Export");
		JMenuItem closeItem = new JMenuItem("Exit");
		
		openItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) { openFile(); }
		});
		saveItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) { saveFile(); }
		});
		closeItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) { System.exit(0); }
		});
		
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(exportItem);
		fileMenu.add(new JSeparator());
		fileMenu.add(closeItem);
		
		menu.add(fileMenu);
		
		JTabbedPane tabs = new JTabbedPane();
		
		dPanel = new DialoguePanel(this);
		qPanel = new QuestPanel(this);
		iPanel = new ItemPanel(this);
		cPanel = new CharacterPanel(this);
		oPanel = new ObjectPanel(this);
		mPanel = new MapPanel(this);
		tPanel = new TeleportPanel(this);
		cuPanel = new CutscenePanel(this);
		
		tabs.add(dPanel, "Dialogue");
		tabs.add(iPanel, "Items");
		tabs.add(qPanel, "Quests");
		tabs.add(cPanel, "Characters");
		tabs.add(oPanel, "Objects");
		tabs.add(mPanel, "Maps");
		tabs.add(tPanel, "Teleporters");
		tabs.add(cuPanel, "Cutscenes");
		
		add(menu, "North");
		add(tabs, "Center");
		setSize(new Dimension(800, 500));
	}
	
	public ArrayList<String> getDialogue()
	{
		return dPanel.stringList;
	}
	
	private void saveFile()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new Filter("qdat", "Game Data (.qdat)"));
		
		int result = chooser.showSaveDialog(this);
		
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File outFile = chooser.getSelectedFile();
			String path = outFile.getPath();
			
			if (!path.endsWith(".qdat"))
			{
				path += ".qdat";
				outFile = new File(path);
			}
			
			try
			{
				DataOutputStream out = new DataOutputStream(new FileOutputStream(outFile));
				
				//write dialogue data
				ArrayList<String> strings = dPanel.stringList;	
				out.writeInt(strings.size());
				for (int i = 0; i < strings.size(); i++)
				{
					String string = strings.get(i);
					
					out.writeInt(string.length());
					out.writeChars(string);
				}
				
				//write items data
				ArrayList<Item> items = iPanel.items;
				out.writeInt(items.size());
				for (int i = 0; i < items.size(); i++)
				{
					Item item = items.get(i);
					String name = item.name;
					String description = item.description;
					
					out.writeInt(name.length());
					out.writeChars(name);
					
					out.writeInt(description.length());
					out.writeChars(description);

					out.writeInt(item.itemType);

					out.writeInt(item.parameters.size());
					for (int j = 0; j < item.parameters.size(); j++)
					{
						ItemParameter p = item.parameters.get(j);
						out.writeInt(p.type);
						out.writeInt(p.value);
					}
				}

				//write quest data
				ArrayList<Quest> quests = qPanel.quests;
				out.writeInt(quests.size());
				for (int i = 0; i < quests.size(); i++)
				{
					Quest q = quests.get(i);
					String name = q.name;
					String description = q.description;

					out.writeInt(name.length());
					out.writeChars(name);

					out.writeInt(description.length());
					out.writeChars(description);

					out.writeInt(q.dialogue1);
					out.writeInt(q.dialogue2);
					out.writeInt(q.dialogue3);
					out.writeInt(q.cutsceneBefore);
					out.writeInt(q.cutsceneAfter);
					out.writeInt(q.trigger);
					out.writeInt(q.availableType);
					out.writeInt(q.availableValue);
					out.writeInt(q.exp);

					out.writeInt(q.conditions.size());
					for (int j = 0; j < q.conditions.size(); j++)
					{
						WinCondition w = q.conditions.get(j);

						out.writeInt(w.winType);
						out.writeInt(w.keyIndex);
						out.writeInt(w.amount);
					}

					out.writeInt(q.rewards.size());
					for (int j = 0; j < q.rewards.size(); j++)
					{
						Reward r = q.rewards.get(j);

						out.writeInt(r.rewardType);
						out.writeInt(r.keyIndex);
						out.writeInt(r.amount);
					}

					out.writeInt(q.receivables.size());
					for (int j = 0; j < q.receivables.size(); j++)
					{
						ItemParameter receivable = q.receivables.get(j);

						out.writeInt(receivable.type);
						out.writeInt(receivable.value);
					}
				}

				//write character data
				ArrayList<Character> characters = cPanel.characters;
				out.writeInt(characters.size());
				for (int i = 0; i < characters.size(); i++)
				{
					Character c = characters.get(i);	
					String name = c.name;
					String animPath = c.animPath;
					String imagePath = c.imagePath;
					String introText = c.introText;
					ArrayList<CharacterInventory> inventory = c.inventory;
					ArrayList<Integer> interactions = c.interactions;

					out.writeInt(name.length());
					out.writeChars(name);

					out.writeInt(animPath.length());
					out.writeChars(animPath);

					out.writeInt(imagePath.length());
					out.writeChars(imagePath);

					out.writeInt(c.aiType);

					out.writeInt(introText.length());
					out.writeChars(introText);

					out.writeInt(c.interactionType);
					out.writeInt(c.hitpoints);
					out.writeInt(c.attack);
					out.writeInt(c.defense);
					out.writeInt(c.speed);

					out.writeInt(inventory.size());
					for (int j = 0; j < inventory.size(); j++)
					{
						CharacterInventory ci = inventory.get(j);
						out.writeInt(ci.item);
						out.writeInt(ci.amount);
						out.writeInt(ci.percentage);
					}

					out.writeInt(interactions.size());
					for (int j = 0; j < interactions.size(); j++)
					{
						out.writeInt(interactions.get(j));
					}
				}

				//write object data
				ArrayList<GameObject> objects = oPanel.objects;
				out.writeInt(objects.size());
				for (int i = 0; i < objects.size(); i++)
				{
					GameObject o = objects.get(i);	
					String name = o.name;
					String animPath = o.animPath;
					String imagePath = o.imagePath;
					String lockText = o.lockText;
					ArrayList<Integer> inventory = o.inventory;
					ArrayList<Integer> amounts = o.amounts;
					ArrayList<LockParameter> locks = o.locks;

					out.writeInt(name.length());
					out.writeChars(name);

					out.writeInt(animPath.length());
					out.writeChars(animPath);

					out.writeInt(imagePath.length());
					out.writeChars(imagePath);

					out.writeInt(o.interactionType);
					out.writeInt(o.interactionValue);
					out.writeInt(o.cutscene);

					out.writeInt(lockText.length());
					out.writeChars(lockText);

					out.writeInt(locks.size());
					for (int j = 0; j < locks.size(); j++)
					{
						LockParameter l = locks.get(j);

						out.writeInt(l.type);
						out.writeInt(l.value);
						out.writeInt(l.amount);
					}

					out.writeInt(inventory.size());
					for (int j = 0; j < inventory.size(); j++)
					{
						out.writeInt(amounts.get(j));
						out.writeInt(inventory.get(j));
					}
				}
			
				//write map data
				ArrayList<String> maps = mPanel.maps;
				out.writeInt(maps.size());

				for (int i = 0; i < maps.size(); i++)
				{
					String map = maps.get(i);

					out.writeInt(map.length());
					out.writeChars(map);
				}

				//write teleporter data
				ArrayList<Teleporter> teleporters = tPanel.teleporters;
				out.writeInt(teleporters.size());

				for (int i = 0; i < teleporters.size(); i++)
				{
					Teleporter teleporter = teleporters.get(i);	

					Teleporter t = teleporters.get(i);	
					String name = t.name;
					String lockText = t.lockText;
					String locationText = t.locationText;
					ArrayList<LockParameter> locks = t.locks;

					out.writeInt(name.length());
					out.writeChars(name);

					out.writeInt(locationText.length());
					out.writeChars(locationText);

					out.writeInt(lockText.length());
					out.writeChars(lockText);

					out.writeInt(t.destinations.size());
					for (int j = 0; j < t.destinations.size(); j++)
					{
						TeleporterDestination td = t.destinations.get(j);

						out.writeInt(td.map);
						out.writeInt(td.xCoord);
						out.writeInt(td.yCoord);
						out.writeInt(td.condition);
						out.writeInt(td.conditionValue);
					}

					out.writeInt(locks.size());
					for (int j = 0; j < locks.size(); j++)
					{
						LockParameter l = locks.get(j);

						out.writeInt(l.type);
						out.writeInt(l.value);
						out.writeInt(l.amount);
					}

					out.writeInt(t.quests.size());
					for (int j = 0; j < t.quests.size(); j++)
					{
						out.writeInt(t.quests.get(j));
					}
				}	
			
				//write cutscene data
				ArrayList<String> cutscenes = cuPanel.cutscenes;
				out.writeInt(cutscenes.size());

				for (int i = 0; i < cutscenes.size(); i++)
				{
					String cutscene = cutscenes.get(i);

					out.writeInt(cutscene.length());
					out.writeChars(cutscene);
				}

				out.close();
				JOptionPane.showMessageDialog(null, "Save completed to " + outFile.getPath());
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
		}
	}
	
	private void openFile()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new Filter("qdat", "Game Data (.qdat)"));
		
		int result = chooser.showOpenDialog(this);
		
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File inFile = chooser.getSelectedFile();
			
			try 
			{
				DataInputStream in = new DataInputStream(new FileInputStream(inFile));
					
				//read dialogue data
				dPanel.stringList = new ArrayList<String>();
				int size = in.readInt();
				for (int i = 0; i < size; i++)
				{
					int buffSize = in.readInt();
					String string = "";
					
					for (int j = 0; j < buffSize; j++)
					{
						string += in.readChar();
					}
					
					dPanel.stringList.add(string);
				}
				
				//read item data
				iPanel.items = new ArrayList<Item>();
				size = in.readInt();
				for (int i = 0; i < size; i++)
				{
					int buffSize = in.readInt();
					Item item = new Item();
					
					for (int j = 0; j < buffSize; j++)
					{
						item.name += in.readChar();
					}
					
					buffSize = in.readInt();
					
					for (int j = 0; j < buffSize; j++)
					{
						item.description += in.readChar();
					}

					item.itemType = in.readInt();

					int paramSize = in.readInt();

					for (int j = 0; j < paramSize; j++)
					{
						ItemParameter p = new ItemParameter();

						p.type = in.readInt();
						p.value = in.readInt();
						item.parameters.add(p);
					}
					
					iPanel.items.add(item);
				}

				//read quest data
				qPanel.quests = new ArrayList<Quest>();
				size = in.readInt();

				for (int i = 0; i < size; i++)
				{
					int buffSize = in.readInt();
					Quest q = new Quest();

					for (int j = 0; j < buffSize; j++)
					{
						q.name += in.readChar();
					}

					buffSize = in.readInt();
					for (int j = 0; j < buffSize; j++)
					{
						q.description += in.readChar();
					}

					q.dialogue1 = in.readInt();
					q.dialogue2 = in.readInt();
					q.dialogue3 = in.readInt();
					q.cutsceneBefore = in.readInt();
					q.cutsceneAfter = in.readInt();
					q.trigger = in.readInt();
					q.availableType = in.readInt();
					q.availableValue = in.readInt();
					q.exp = in.readInt();

					buffSize = in.readInt();

					for (int j = 0; j < buffSize; j++)
					{
						WinCondition w = new WinCondition();
						w.winType = in.readInt();
						w.keyIndex = in.readInt();
						w.amount = in.readInt();

						q.conditions.add(w);
					}	

					buffSize = in.readInt();

					for (int j = 0; j < buffSize; j++)
					{
						Reward r = new Reward();
						r.rewardType = in.readInt();
						r.keyIndex = in.readInt();
						r.amount = in.readInt();

						q.rewards.add(r);
					}

					buffSize = in.readInt();
					for (int j = 0; j < buffSize; j++)
					{
						ItemParameter receivable = new ItemParameter();
						receivable.type = in.readInt();
						receivable.value = in.readInt();

						q.receivables.add(receivable);
					}

					qPanel.quests.add(q);
				}

				//read character data
				cPanel.characters = new ArrayList<Character>();
				size = in.readInt();

				for (int i = 0; i < size; i++)
				{
					int buffSize = in.readInt();
					Character c = new Character();

					for (int j = 0; j < buffSize; j++)
					{
						c.name += in.readChar();
					}
					
					buffSize = in.readInt();
					
					for (int j = 0; j < buffSize; j++)
					{
						c.animPath += in.readChar();
					}

					buffSize = in.readInt();
					
					for (int j = 0; j < buffSize; j++)
					{
						c.imagePath += in.readChar();
					}

					c.aiType = in.readInt();

					buffSize = in.readInt();

					for (int j = 0; j < buffSize; j++)
					{
						c.introText += in.readChar();
					}

					c.interactionType = in.readInt();
					c.hitpoints = in.readInt();
					c.attack = in.readInt();
					c.defense = in.readInt();
					c.speed = in.readInt();

					buffSize = in.readInt();
					for (int j = 0; j < buffSize; j++)
					{
						CharacterInventory ci = new CharacterInventory();

						ci.item = in.readInt();
						ci.amount = in.readInt();
						ci.percentage = in.readInt();

						c.inventory.add(ci);
					}

					buffSize = in.readInt();
					for (int j = 0; j < buffSize; j++)
					{
						c.interactions.add(in.readInt());
					}

					cPanel.characters.add(c);
				}

				//read object data
				oPanel.objects = new ArrayList<GameObject>();
				size = in.readInt();

				for (int i = 0; i < size; i++)
				{
					int buffSize = in.readInt();
					GameObject o = new GameObject();

					for (int j = 0; j < buffSize; j++)
					{
						o.name += in.readChar();
					}
					
					buffSize = in.readInt();
					
					for (int j = 0; j < buffSize; j++)
					{
						o.animPath += in.readChar();
					}

					buffSize = in.readInt();
					
					for (int j = 0; j < buffSize; j++)
					{
						o.imagePath += in.readChar();
					}

					o.interactionType = in.readInt();
					o.interactionValue = in.readInt();
					o.cutscene = in.readInt();

					buffSize = in.readInt();
					for (int j = 0; j < buffSize; j++)
					{
						o.lockText += in.readChar();
					}

					buffSize = in.readInt();
					for (int j = 0; j < buffSize; j++)
					{
						LockParameter l = new LockParameter();
						l.type = in.readInt();
						l.value = in.readInt();
						l.amount = in.readInt();
						o.locks.add(l);
					}

					buffSize = in.readInt();
					for (int j = 0; j < buffSize; j++)
					{
						o.amounts.add(in.readInt());
						o.inventory.add(in.readInt());
					}

					oPanel.objects.add(o);
				}

				//read map data
				mPanel.maps = new ArrayList<String>();

				int mapSize = in.readInt();
				for (int i = 0; i < mapSize; i++)
				{
					String map = "";
					int buffSize = in.readInt();

					for (int j = 0; j < buffSize; j++)
					{
						map += in.readChar();
					}

					mPanel.maps.add(map);
				}

				tPanel.teleporters = new ArrayList<Teleporter>();
				size = in.readInt();
				for (int i = 0; i < size; i++)
				{
					int buffSize = in.readInt();
					Teleporter t = new Teleporter();

					for (int j = 0; j < buffSize; j++)
					{
						t.name += in.readChar();
					}
					
					buffSize = in.readInt();
					
					for (int j = 0; j < buffSize; j++)
					{
						t.locationText += in.readChar();
					}

					buffSize = in.readInt();
					for (int j = 0; j < buffSize; j++)
					{
						t.lockText += in.readChar();
					}

					buffSize = in.readInt();
					for (int j = 0; j < buffSize; j++)
					{
						TeleporterDestination td = new TeleporterDestination();

						td.map = in.readInt();
						td.xCoord = in.readInt();
						td.yCoord = in.readInt();
						td.condition = in.readInt();
						td.conditionValue = in.readInt();

						t.destinations.add(td);
					}

					buffSize = in.readInt();
					for (int j = 0; j < buffSize; j++)
					{
						LockParameter l = new LockParameter();
						l.type = in.readInt();
						l.value = in.readInt();
						l.amount = in.readInt();
						t.locks.add(l);
					}

					buffSize = in.readInt();
					for (int j = 0; j < buffSize; j++)
					{
						t.quests.add(in.readInt());
					}

					tPanel.teleporters.add(t);
				}

				//read cutscene data
				cuPanel.cutscenes = new ArrayList<String>();

				int cutsceneSize = in.readInt();
				for (int i = 0; i < cutsceneSize; i++)
				{
					String cutscene = "";
					int buffSize = in.readInt();

					for (int j = 0; j < buffSize; j++)
					{
						cutscene += in.readChar();
					}

					cuPanel.cutscenes.add(cutscene);
				}
				
				dPanel.refreshList();
				iPanel.refreshList();
				qPanel.refreshList();
				cPanel.refreshList();
				oPanel.refreshList();
				mPanel.refreshList();
				tPanel.refreshList();
				cuPanel.refreshList();
				
				in.close();
				JOptionPane.showMessageDialog(this, "Load Successful: " + inFile.getPath());
				
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		}
	}

	public ArrayList<Item> getItems() 
	{
		return iPanel.items;
	}

	public ArrayList<Quest> getQuests() 
	{
		return qPanel.quests;
	}

	public ArrayList<Character> getCharacters()
	{
		return cPanel.characters;
	}

	public ArrayList<String> getMaps()
	{
		return mPanel.maps;
	}

	public ArrayList<String> getCutscenes()
	{
		return cuPanel.cutscenes;
	}

	public ArrayList<Teleporter> getTeleporters()
	{
		return tPanel.teleporters;
	}
}
