import java.awt.Point;

public class PicButton {

	Picimage pic = null;
	View thisv = null;
	double locateX, locateY;
	Wave pushSE = new Wave();

	PicButton(double x, double y, String filename, View v){
		thisv = v;
		pushSE.load("system/ok.wav", 1);
		pushSE.volume(Game.volume);
		pic = new Picimage(filename, (int)x, (int)y, thisv);
		locateX = x;
		locateY = y;
		thisv.setImage(pic, Const.LAYER_BUTTON);
		System.out.println("originX : " + locateX + " originY : " + locateY);
		System.out.println("sitaX : " + (locateX + pic.getWidth()) + " sitaY : " + (locateY + pic.getHeight()));
	}

	public int getWidth() {
		return pic.getWidth();
	}

	public int getHeight() {
		return pic.getHeight();
	}

	public Picimage getPic() {
		return pic;
	}

	public void setPosition(int x, int y) {
		locateX = (double)x;
		locateY = (double)y;
		pic.setPosition(x, y);
	}

	public boolean isClicked() {
		Point mouseP = new Point();
		mouseP = thisv.clickedPoint();
		if(mouseP.x > locateX && mouseP.y > locateY && mouseP.x < locateX + pic.getWidth() && mouseP.y < locateY + pic.getHeight()) {
			pushSE.play(false);
			return true;
		}else {
			return false;
		}
	}
}
