import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class View extends JFrame implements MouseListener, KeyListener{

	JPanel mp = new JPanel();
	Container c = getContentPane();
	Graphics g;
	Graphics backg;
	double mouseX = -1, mouseY = -1;
	public boolean isPressing = false;
	Layer[] layer = new Layer[Const.LAYER_MAX];
	String drawString = "";
	int stringX, stringY;
	Image im = null;
	boolean clicked = false;
	Color stringColor = null;
	Picimage fade;
	Picimage blackfade;
	Picimage winpic;
	Font font = new Font("ＭＳ Ｐゴシック", Font.PLAIN, 32);
	MousePointerInfo mpi = new MousePointerInfo(this);
	boolean esc = false;

	View(String title){
		int i = 0;
		JPanel panel = new JPanel();
		c.add(mp);
		setTitle(title);
		setBounds(100, 100, Const.WINDOW_WIDTH, Const.WINDOW_HEIGHT);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		panel.addMouseListener(this);
		addKeyListener(this);
		this.g = getGraphics();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		panel.setVisible(true);
		for(i = 0 ; i < Const.LAYER_MAX ; i++) {
			layer[i] = new Layer();
		}
		im = createImage(Const.WINDOW_WIDTH, Const.WINDOW_HEIGHT);
		backg = im.getGraphics();
		backg.drawString("foo", 0, 0);
		fade = new Picimage("system/white_fade.png", 0, 0, this);
		blackfade = new Picimage("system/black_fade.png", 0, 0, this);
		fade.setAlpha(0.0F);
		blackfade.setAlpha(0.0F);
		winpic = new Picimage("system/box_blue_name.png", 30, 500, this);
	}

	public Graphics getmyGraphics() {
		return backg;
	}

	public void setImage(Picimage pic, int layernum) {
		System.out.println("setImage : " + pic.getImageName());
		if(layer[layernum].setImage(pic) == Const.FAILURE) {
			System.out.println("レイヤー[" + layernum + "]画像設定数上限を超えました。1つのレイヤーに設定できる画像数は" + Const.LAYER_PIC_MAX + "枚までです。");
			System.exit(ERROR);
		}
	}

	public Point getMousePointer() {
		Point p = new Point(mpi.getX(), mpi.getY());
		return p;
	}

	public Picimage getWinPic() {
		return winpic;
	}

	public Font getmyFont() {
		return font;
	}

	public Picimage getLayerPicImage(int layernum, int num) {
		return layer[layernum].getPicImage(num);
	}

	public void printLayerImageName(int layernum) {
		layer[layernum].printLayerImageName();
	}

	public void draw(View v) {
		int i;
		backg.setColor(Color.white);
		backg.fillRect(0, 0, Const.WINDOW_WIDTH, Const.WINDOW_HEIGHT);
		for(i = 0 ; i < Const.LAYER_MAX ; i++) {
			layer[i].drawLayer();
		}
	}

	public void update() {
		g.drawImage(im, 0, 0, this);
	}

	public void drawClear(int layernum) {
		layer[layernum].clear();
	}

	public void drawClearAll() {
		int i;
		backg.setColor(Color.white);
		backg.fillRect(0, 0, 1024, 786);
		for(i = 0 ; i < Const.LAYER_MAX ; i++) {
			layer[i].clear();
		}
	}

	public void setDrawString(String msg, int x, int y, Font font) {
		backg.setFont(font);
		drawString = msg;
		stringX = x;
		stringY = y;
	}

	public void setStringColor(Color col) {
		stringColor = col;
	}

	public void setLayerVisible(boolean tf, int layernum) {
		layer[layernum].setVisible(tf);
	}

	public void changeLayerPic(int layernum, Picimage original, Picimage changed) {
		layer[layernum].changePic(original, changed);
	}

	public void setFadeAlpha(float alpha) {
		fade.setAlpha(alpha);
	}

	public void setBlackFadeAlpha(float alpha) {
		blackfade.setAlpha(alpha);
	}

	public Picimage getFader() {
		return fade;
	}

	public Picimage getBlackFader() {
		return blackfade;
	}

	public double getMouseX() {
		return mouseX;
	}

	public double getMouseY() {
		return mouseY;
	}

	public boolean getEsc() {
		boolean tmpesc = esc;
		esc = false;
		return tmpesc;
	}

	public void initEsc() {
		esc = false;
	}

	public void initMouse() {
		mouseX = -1.0;
		mouseY = -1.0;
		clicked = false;
	}

	public Point clickedPoint() {
		Point p = new Point();
		p.setLocation((double)mouseX, (double)mouseY);
		return p;
	}

	public boolean getClicked() {
		return clicked;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Point p = e.getPoint();
		mouseX = p.getX();
		mouseY = p.getY();
		System.out.println("x : " + p.x + " y : " + p.y);
		clicked = true;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			clicked = true;
		}else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			esc = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}
}

class Layer{

	Picimage[] pic = new Picimage[Const.LAYER_PIC_MAX];
	boolean visible = true;

	Layer(){

	}

	public int setImage(Picimage image) {
		int i = 0;
		while(pic[i] != null) {
			i++;
			if(i >= Const.LAYER_PIC_MAX) {
				return Const.FAILURE;
			}
		}
		pic[i] = image;
		return Const.SUCCESS;
	}

	public void setVisible(boolean v) {
		visible = v;
	}

	public void drawLayer() {
		int i = 0;
		if(visible) {
			while(pic[i] != null) {
				pic[i].putImage();
				i++;
			}
		}
	}

	public void printLayerImageName() {
		int i = 0;
		while(pic[i] != null) {
			System.out.println(pic[i].getImageName());
			i++;
		}
	}

	public void changePic(Picimage original, Picimage changed) {
		System.out.println("change from : " + original.getImageName() + " to : " + changed.getImageName());
		for(int i = 0 ; i < Const.LAYER_PIC_MAX ; i++) {
			if(pic[i].equals(original)) {
				pic[i] = changed;
				break;
			}
		}
	}

	public void clear() {
		int i;
		for(i = 0 ; i < Const.LAYER_PIC_MAX ; i++) {
			pic[i] = null;
		}
	}

	public Picimage getPicImage(int num) {
		return pic[num];
	}
}
