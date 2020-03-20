import java.awt.Color;
import java.awt.Font;
import java.awt.Point;

public class StringButton {

	char[] textCh;
	View thisv = null;
	double locateX, locateY;
	Font font;
	boolean visible = true;
	int alpha = 255;

	StringButton(double x, double y, String textname, Font font, View v){
		thisv = v;
		textCh = textname.toCharArray();
		locateX = x;
		locateY = y;
		this.font = font;
		System.out.println("originX : " + locateX + " originY : " + locateY);
	}

	public int getWidth() {
		return textCh.length * 32;
	}

	public int getHeight() {
		return font.getSize();
	}

	public void setPosition(int x, int y) {
		locateX = (double)x;
		locateY = (double)y;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public void setVisible(boolean v) {
		visible = v;
	}

	public void drawButton() {
		//Insets inset = thisv.getInsets();
		thisv.getmyGraphics().setFont(font);
		Point mouseP = new Point();
		mouseP = thisv.getMousePointer();
		for(int i = 0 ; i < textCh.length ; i++) {
			if(mouseP.x > locateX && mouseP.y > locateY && mouseP.x < locateX + getWidth() && mouseP.y < locateY + getHeight()) {
				thisv.getmyGraphics().setColor(new Color(0, 0, 0, alpha));
				if(visible)thisv.getmyGraphics().drawString(String.valueOf(textCh[i]), (int)locateX + i * 32, (int)locateY + 60);
				thisv.getmyGraphics().setColor(new Color(255, 255, 255, alpha));
				if(visible)thisv.getmyGraphics().drawString(String.valueOf(textCh[i]), (int)locateX + i * 32 - 5, (int)locateY + 60 - 5);
			}else {
				thisv.getmyGraphics().setColor(new Color(255, 255, 255, alpha));
				if(visible)thisv.getmyGraphics().drawString(String.valueOf(textCh[i]), (int)locateX + i * 32, (int)locateY + 60);
			}
		}
	}

	public boolean isClicked() {
		Point mouseP = new Point();
		mouseP = thisv.clickedPoint();
		if(mouseP.x > locateX && mouseP.y > locateY && mouseP.x < locateX + getWidth() && mouseP.y < locateY + getHeight()) {
			return true;
		}else {
			return false;
		}
	}
}
