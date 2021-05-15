import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


public class AnimAnimationDrawPanel extends JPanel 
{
	private BufferedImage image;
	private int zoomLevel = 1;
	private final int MAX_ZOOM = 4;
	
	public AnimAnimationDrawPanel()
	{
		image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		addMouseWheelListener(new MouseWheelListener()
		{
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				if (e.getWheelRotation() < 0)
				{	
					zoomLevel = 1 << zoomLevel;
				}
				else
				{
					zoomLevel >>= 1;
				}
				
				if (zoomLevel < 1)
				{
					zoomLevel = 1;
				}
				else if (zoomLevel > MAX_ZOOM)
				{
					zoomLevel = MAX_ZOOM;
				}
				
				repaint();
			}
		});
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;

		BufferedImage zImage = zoomImage();
		g2.drawImage(zImage, (getWidth() - zImage.getWidth()) >> 1, (getHeight() - zImage.getHeight()) >> 1, null);
	}
	
	public void setImage(BufferedImage img)
	{
		image = img;
	}
	
	private BufferedImage zoomImage() 
	{
        int w = zoomLevel * image.getWidth();
        int h = zoomLevel * image.getHeight();
        
        BufferedImage zoomImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        
        for (int i = 0; i < h; i++)
        {
            for (int j = 0; j < w; j++)
            {
                zoomImage.setRGB(j, i, image.getRGB(j / zoomLevel, i / zoomLevel));
            }
        }
        
        return zoomImage;
    }
}