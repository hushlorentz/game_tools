import java.io.*;

import javax.swing.JFileChooser;

public class Convert
{
	public static void main(String[] args)
	{
		int convertNum = 0;
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int result = chooser.showSaveDialog(null);

		if (result == JFileChooser.APPROVE_OPTION)
		{
			File dir = chooser.getSelectedFile();

			File outFile = new File(chooser.getSelectedFile().getPath() + "/Maps.as");

			String outputString = "package\n";
			outputString += "{\n";
			outputString += "\tpublic class Maps\n";
			outputString += "\t{\n";

			try
			{
				PrintStream output = new PrintStream(outFile);

				File[] files = dir.listFiles();

				for (int i = 0; i < files.length; i++)
				{
					File file = files[i];

					if (file.getName().substring(file.getName().length() - 4, file.getName().length()).equals(".dat"))
					{
						System.out.println("Converting: " + file.getName());
						DataInputStream input = new DataInputStream(new FileInputStream(file));

						outputString += "\t\tpublic static var " + file.getName().substring(0, file.getName().length() - 4).toUpperCase() + "_MAP:Array = [";

						while (input.available() > 0)
						{
							byte b = input.readByte();

							outputString += b;

							if (input.available() > 0)
							{
								outputString += ",";
							}
						}

						outputString += "];\n";
						input.close();

						convertNum++;
					}
				}

				outputString += "\t}\n";
				outputString += "}";

				output.print(outputString);

				output.close();

				System.out.println("Converted " + convertNum + " maps");
			}
			catch (Exception e)
			{
				printError("" + e);
			}
		}
	}

	public static void printError(String msg)
	{
		System.out.println(msg);
		System.exit(0);
	}
}
