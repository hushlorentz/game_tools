import java.io.File;

public class Filter extends javax.swing.filechooser.FileFilter 
{
	String suffix;
	String description;

	public Filter(String suffix, String description)
	{
		this.suffix = suffix;
		this.description = description;
	}

	public boolean accept(File file) 
	{
		if (file.isDirectory()) return true;
		String fileName = file.getPath();
		int dotIndex = fileName.lastIndexOf('.');
		String fileExt = fileName.substring(dotIndex + 1, fileName.length());
		if (fileExt.equalsIgnoreCase(suffix)) return true;
		return false;
	}

	public String getDescription() 
	{
		return description;
	}
}
