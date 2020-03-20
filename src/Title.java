import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Title {

	View thisv = null;
	Picimage msgwindow = null;
	Wave bgm = new Wave();
	boolean isNewGame;
	int filenum;

	Title(View v){
		thisv = v;
		Picimage logo = new Picimage("system/logo.png", 0, 0, v);
		Picimage back = new Picimage("system/air.jpeg", 0, 0, v);
		float a = 0.0F;
		bgm.load("system/title.wav", 1);
		bgm.volume(Game.volume);
		bgm.play(true);

		thisv.setImage(logo, Const.LAYER_BACK);
		for(int i = 0 ; i < 150 ; i++) {
			a += 0.02F;
			if(a > 1.0F)a = 1.0F;
			logo.setAlpha(a);
			thisv.draw(thisv);
			thisv.update();
			if(thisv.getClicked())break;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		for(; a > 0.0F ; a -= 0.02F) {
			logo.setAlpha(a);
			thisv.draw(thisv);
			thisv.update();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

		}
		thisv.drawClearAll();
		thisv.setImage(back, Const.LAYER_BACK);
		PicButton pbtn = new PicButton(0, 0, "system/button_newstart.png", thisv);
		PicButton pbtn2 = new PicButton(0, 0, "system/button_loadstart.png", thisv);
		pbtn.setPosition(Const.WINDOW_WIDTH / 2 - pbtn.getWidth() / 2, 500);
		pbtn2.setPosition(Const.WINDOW_WIDTH / 2 - pbtn.getWidth() / 2, 600);

		for(a = 0.0F ; a < 1.0F ; a += 0.01F) {
			back.setAlpha(a);
			pbtn.getPic().setAlpha(a);
			pbtn2.getPic().setAlpha(a);
			thisv.draw(thisv);
			thisv.update();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

		while(true) {

			thisv.draw(thisv);
			thisv.update();

			if(pbtn.isClicked()) {
				isNewGame = true;
				break;
			}
			if(pbtn2.isClicked()) {
				thisv.initMouse();
				filenum = fileSelect();
				if(filenum != -1) {
					isNewGame = false;
					break;
				}
			}
			thisv.initMouse();

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			Game.ct.next();
		}

		for(a = 1.0F ; a > 0.0F ; a -= 0.01F) {
			back.setAlpha(a);
			pbtn.getPic().setAlpha(a);
			pbtn2.getPic().setAlpha(a);
			thisv.draw(thisv);
			thisv.update();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		thisv.drawClearAll();
		thisv.initMouse();
		System.out.println("button clicked");
		bgm.stop();
	}

	private int fileSelect() {
		Picimage menuBack = new Picimage("system/black_fade.png", 0, 0, thisv);
		menuBack.setAlpha(0.5F);
		thisv.setImage(menuBack, Const.LAYER_MENU_BACK);
		File[] files = new File[Const.FILE_NUM_MAX];
		StringButton[] fileButton = new StringButton[Const.FILE_NUM_MAX];
		boolean[] isNopeFile = new boolean[Const.FILE_NUM_MAX];
		FileReader freader;
		BufferedReader breader;
		String time;
		Font font = new Font("ＭＳ Ｐゴシック", Font.PLAIN, 32);
		StringButton returnButton = new StringButton(50, 600, "戻る", font, thisv);
		thisv.initMouse();
		for(int i = 0 ; i < Const.FILE_NUM_MAX ; i++) {
			files[i] = new File("system/playerfile/file" + (i + 1) + ".txt");
			try {
				freader = new FileReader(files[i]);
				breader = new BufferedReader(freader);
				time = breader.readLine();
				if(time.contains("nope")) {
					time = "ファイルがありません";
					isNopeFile[i] = true;
				}else {
					isNopeFile[i] = false;
				}
				fileButton[i] = new StringButton(200, 100 + i * 60, "File" + (i + 1) + ":" + time, font, thisv);
				breader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		while(true) {
			thisv.draw(thisv);
			for(int i = 0 ; i < Const.FILE_NUM_MAX ; i++) {
				fileButton[i].drawButton();
				if(fileButton[i].isClicked() && isNopeFile[i] == false) {
					if(loadCheck(i + 1)) {
						thisv.initMouse();
						thisv.drawClear(Const.LAYER_MENU_BACK);
						return i + 1;
					}
				}
			}
			returnButton.drawButton();
			if(returnButton.isClicked()) {
				thisv.initMouse();
				thisv.drawClear(Const.LAYER_MENU_BACK);
				return -1;
			}
			thisv.update();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}

	private boolean loadCheck(int filenum) {
		Font font = new Font("ＭＳ Ｐゴシック", Font.PLAIN, 32);
		StringButton yesButton = new StringButton(400, 400, "はい", font, thisv);
		StringButton noButton = new StringButton(500, 400, "いいえ", font, thisv);
		thisv.initMouse();
		while(true) {
			thisv.draw(thisv);
			thisv.getmyGraphics().drawString("ファイル" + filenum + "をロードしますか？", 300, 300);
			yesButton.drawButton();
			noButton.drawButton();
			if(yesButton.isClicked()) {
				thisv.initMouse();
				return true;
			}
			if(noButton.isClicked()) {
				thisv.initMouse();
				return false;
			}
			thisv.update();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}
}
