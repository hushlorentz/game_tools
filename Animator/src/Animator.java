import java.awt.Dimension;

import javax.swing.WindowConstants;

public class Animator
{
	private static final int SCREEN_WIDTH = 800;
	private static final int SCREEN_HEIGHT = 500;
	
	public static void main(String[] args)
	{
		AnimMainFrame frame = new AnimMainFrame();
		
		frame.setTitle("Animator");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		frame.setMinimumSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		frame.setVisible(true);
	}
}
