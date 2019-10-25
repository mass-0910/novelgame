
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Picimage {

	BufferedImage bi = null;
	View thisv = null;
	int x;
	int y;
	float alpha = 1.0F;
	boolean visible = true;
	String imageName;

	//変形イメージ用
	Image im = null;
	boolean isPlaneImage;

	Picimage(String imageName, int x, int y, View v){
		this.x = x;
		this.y = y;
		this.imageName = imageName;
		try {
			bi = ImageIO.read(new File("picture/" + imageName));
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		thisv = v;
		isPlaneImage = false;
	}

	public String getImageName() {
		return imageName;
	}

	Picimage(int x, int y, View v){
		this.x = x;
		this.y = y;
		thisv = v;
		isPlaneImage = true;
		im = thisv.createImage(Const.WINDOW_WIDTH, Const.WINDOW_HEIGHT);
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setVisible(boolean v) {
		visible = v;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	BufferedImage getBufferedImage() {
		return bi;
	}

	//for View only
	void putImage() {
		if(isPlaneImage == false) {
			Insets insets = thisv.getInsets();
			Graphics2D tmpg = (Graphics2D)thisv.getmyGraphics();
			tmpg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			if(visible)thisv.getmyGraphics().drawImage(bi, x, y + insets.top, null);
			tmpg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F));
		}else {
			Insets insets = thisv.getInsets();
			Graphics2D tmpg = (Graphics2D)thisv.getmyGraphics();
			tmpg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			if(visible)thisv.getmyGraphics().drawImage(im, x, y + insets.top, null);
			tmpg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F));
		}
	}

	public int getWidth() {
		return bi.getWidth();
	}

	public int getHeight() {
		return bi.getHeight();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Image getImage() {
		return im;
	}
}
