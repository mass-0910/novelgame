import javax. swing. JFrame;
import java . awt. Insets;
import java . awt. Point;
import java . awt. PointerInfo;
import java . awt. MouseInfo;

public class MousePointerInfo{
	private JFrame win;
	MousePointerInfo( JFrame win){
		this.win = win;
	}
	public int getX(){
		Point p;
		PointerInfo  pinfo = MouseInfo.getPointerInfo();
		p = pinfo.getLocation();
		Insets insets = win.getInsets();
		int mouseX = (int)(p.getX()-win.getX()-insets.left);
		return mouseX;
	}
	public int getY(){
		Point p;
		PointerInfo  pinfo = MouseInfo.getPointerInfo();
		p = pinfo.getLocation();
		Insets insets = win.getInsets();
		int mouseY = (int)(p.getY()-win.getY()-insets.top);
		return mouseY;
	}
}