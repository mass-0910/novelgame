import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ScriptManager {

	ScriptExection[] exectors = new ScriptExection[Const.LINE_MAX];
	View thisv = null;
	ScriptReader reader = new ScriptReader("senario/define.txt");
	public static String readingFile = "define.txt";
	static MsgWindow w = null;
	int readLineNum = 0;
	PicTable picTable = new PicTable();
	WaveTable waveTable = new WaveTable();
	LabelTable labelTable = new LabelTable();
	FlagTable flagTable = new FlagTable();
	Character[] characters = new Character[Const.CHARACTER_MAX];
	boolean[] flag = new boolean[Const.FLAG_MAX];
	boolean isNewGame = false;
	int filenumber;
	public static Wave bgm = null;
	public static String nowBackPicName;
	public static String nowPlayingBGMName;

	ScriptManager(View v, boolean isNewGame, int filenum) {
		thisv = v;
		this.isNewGame = isNewGame;
		this.filenumber = filenum;
		int i = 0;
		int j = 0;
		while (reader.getLine(j) != null) {
			if (reader.getLine(j).trim().isEmpty() == false) {
				exectors[i] = new ScriptExection(i, reader.getLine(j), flag, this, thisv, picTable, waveTable,
						labelTable, flagTable, characters);
				i++;
			}
			j++;
		}
	}

	public void printExection() {
		int i = 0;
		while (exectors[i] != null) {
			exectors[i].printCommandAndOperands();
			i++;
		}
	}

	public String getCommand(int linenum) {
		return exectors[linenum].getCommand();
	}

	public int execLine() {
		if (exectors[readLineNum] != null) {
			readLineNum = exectors[readLineNum].exec(readLineNum);
			if (readLineNum == -1) {
				readLineNum = 0;
				for (int i = 0; i < exectors.length; i++) {
					exectors[i] = null;
				}
				System.out.println("in");
				reader = new ScriptReader("senario/" + readingFile);
				int i = 0;
				int j = 0;
				while (reader.getLine(j) != null) {
					if (reader.getLine(j).trim().isEmpty() == false) {
						exectors[i] = new ScriptExection(i, reader.getLine(j), flag, this, thisv, picTable, waveTable,
								labelTable, flagTable, characters);
						i++;
					}
					j++;
				}
				printExection();
			} else if (readLineNum == -2) {
				return Const.FAILURE;
			} else if (readLineNum == -3) {
				if (isNewGame) {
					readLineNum = 0;
					readingFile = "first.txt";
				} else {
					load(filenumber);
				}
				for (int i = 0; i < exectors.length; i++) {
					exectors[i] = null;
				}
				reader = new ScriptReader("senario/" + readingFile);
				int i = 0;
				int j = 0;
				while (reader.getLine(j) != null) {
					if (reader.getLine(j).trim().isEmpty() == false) {
						exectors[i] = new ScriptExection(i, reader.getLine(j), flag, this, thisv, picTable, waveTable,
								labelTable, flagTable, characters);
						i++;
					}
					j++;
				}
				printExection();
			}
			return Const.SUCCESS;
		} else {
			return Const.FAILURE;
		}
	}

	public void save(int filenum) {
		File f = new File("system/playerfile/file" + filenum + ".txt");
		String line = "";
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			Calendar c = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH時mm分");
			bw.write(sdf.format(c.getTime()));
			bw.newLine();
			bw.write(readingFile);
			bw.newLine();
			bw.write(String.valueOf(readLineNum));
			bw.newLine();
			bw.write(nowBackPicName);
			bw.newLine();
			bw.write(nowPlayingBGMName);
			bw.newLine();
			for (int i = 0; characters[i] != null; i++)
				line += characters[i].getName() + ",";
			bw.write(line);
			bw.newLine();
			line = "";
			for (int i = 0; characters[i] != null; i++)
				line += characters[i].getPosition() + ",";
			bw.write(line);
			bw.newLine();
			line = "";
			for (int i = 0; characters[i] != null; i++)
				line += characters[i].getNowFaceName() + ",";
			bw.write(line);
			bw.newLine();
			line = "";
			for (int i = 0; characters[i] != null; i++)
				line += characters[i].getVisible() + ",";
			bw.write(line);
			bw.newLine();
			bw.write(flagTable.getFlagLine());
			bw.newLine();
			bw.write(flagTable.getTFLine());
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void load(int filenum) {
		ScriptReader sr = new ScriptReader("system/playerfile/file" + filenum + ".txt");
		readingFile = sr.getLine(1);
		readLineNum = Integer.valueOf(sr.getLine(2));
		nowBackPicName = sr.getLine(3);
		nowPlayingBGMName = sr.getLine(4);
		thisv.setImage(picTable.getPicimage(nowBackPicName), Const.LAYER_BACK);
		bgm = waveTable.getWave(nowPlayingBGMName);
		bgm.play(true);
		String[] characterNameSplit = sr.getLine(5).split(",");
		String[] characterPositionSplit = sr.getLine(6).split(",");
		String[] characterDifSplit = sr.getLine(7).split(",");
		String[] characterVisibleSplit = sr.getLine(8).split(",");
		int j;
		for (int i = 0; i < characterNameSplit.length; i++) {
			j = 0;
			while (characters[j].getName().equals(characterNameSplit[i]) == false)
				j++;
			characters[i].afterLoad(characterPositionSplit[i], characterDifSplit[i], characterVisibleSplit[i]);
		}
		String[] flagNameSplit = sr.getLine(9).split(",");
		String[] flagSplit = sr.getLine(10).split(",");
		for (int i = 0; i < flagNameSplit.length; i++) {
			flagTable.recordFlag(flagNameSplit[i]);
			flagTable.setFlag(flagNameSplit[i], Boolean.valueOf(flagSplit[i]));
		}
	}
}

class ScriptExection {

	View thisv = null;
	int myNumber;
	String command;
	String[] operand = new String[10];
	PicTable picTable = null;
	WaveTable waveTable = null;
	LabelTable labelTable = null;
	FlagTable flagTable = null;
	Character[] characters = null;
	ScriptManager sm = null;
	boolean[] flag = null;

	ScriptExection(int myNumber, String line, boolean[] flag, ScriptManager sm, View v, PicTable pt, WaveTable wt,
			LabelTable lt, FlagTable ft, Character[] characters) {
		thisv = v;
		this.flag = flag;
		this.myNumber = myNumber;
		picTable = pt;
		waveTable = wt;
		labelTable = lt;
		flagTable = ft;
		this.characters = characters;
		this.sm = sm;

		int i = 0;
		while ((command = line.split(" ")[i].trim()).isEmpty())
			i++;
		line = line.replaceAll(command, "");
		for (i = 0; i < line.split(",").length; i++) {
			operand[i] = new String(line.split(",")[i].trim());
		}
		if (command.equals("@")) {
			labelTable.recordLabel(operand[0], myNumber);
		}
	}

	public void printCommandAndOperands() {
		int i = 0;
		System.out.println("Command : " + command);
		while (operand[i] != null) {
			System.out.println("Operand[" + i + "] : " + operand[i]);
			i++;
		}
	}

	public String getCommand() {
		return command;
	}

	public int exec(int nowLineNum) {
		int nextLineNum = 0;
		Picimage im;
		Wave wv;
		Wave se;
		int i;
		boolean tmpb;
		Picimage pi1;
		Picimage pi2;
		Graphics g;
		Picimage groundim;

		switch (command) {

		/* コメント */
		case "//":
			nextLineNum = nowLineNum + 1;
			break;

		/* ラベル */
		case "@":
			nextLineNum = nowLineNum + 1;
			break;

		/* メッセージ */
		case ">":
			ScriptManager.w = new MsgWindow(thisv, operand[0]);
			thisv.initMouse();
			while (thisv.getClicked() == false) {
				thisv.draw(thisv);
				if (ScriptManager.w.msgPrint(false))
					break;
				thisv.update();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				Game.ct.next();
			}
			thisv.initMouse();
			thisv.initEsc();
			while (thisv.getClicked() == false) {
				thisv.draw(thisv);
				ScriptManager.w.msgPrint(true);
				thisv.update();
				if (thisv.getEsc()) {
					menu();
					if (Game.reset)
						return -2;
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				Game.ct.next();
			}
			thisv.initMouse();
			nextLineNum = nowLineNum + 1;
			break;

		/* メッセ―ジ名前欄変更 */
		case "name":
			if (operand[0] != null) {
				MsgWindow.talkerName = operand[0];
			}
			MsgWindow.talkerName = operand[0];
			nextLineNum = nowLineNum + 1;
			break;

		/* メッセージウィンドウ非表示 */
		case "winhide":
			ScriptManager.w.setVisible(false);
			nextLineNum = nowLineNum + 1;
			break;

		/* 画像定義 */
		case "picdef":
			picTable.recordPic(operand[1], new Picimage(operand[0], 0, 0, thisv));
			nextLineNum = nowLineNum + 1;
			break;

		/* 背景変更 */
		case "back":
			im = picTable.getPicimage(operand[0]);
			if (im == null) {
				System.out.println("Error in line " + nowLineNum + " : 指定された画像は定義されていません。");
			} else {
				thisv.drawClear(Const.LAYER_BACK);
				thisv.setImage(im, Const.LAYER_BACK);
				ScriptManager.nowBackPicName = operand[0];
			}
			nextLineNum = nowLineNum + 1;
			break;

		/* 背景変更(フェード) */
		case "backfade":
			im = picTable.getPicimage(operand[0]);
			if (im == null) {
				System.out.println("Error in line " + nowLineNum + " : 指定された画像は定義されていません。");
				nextLineNum = nowLineNum + 1;
				break;
			}
			thisv.setImage(im, Const.LAYER_BACK);
			for (float a = 0.0F; a < 1.0F; a += 0.02F) {
				im.setAlpha(a);
				thisv.draw(thisv);
				thisv.update();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			thisv.drawClear(Const.LAYER_BACK);
			thisv.setImage(im, Const.LAYER_BACK);
			ScriptManager.nowBackPicName = operand[0];
			nextLineNum = nowLineNum + 1;
			break;

		/* 背景変更(アニメーション付き) */
		case "backanim":
			pi1 = thisv.getLayerPicImage(Const.LAYER_BACK, 0);
			pi2 = picTable.getPicimage(operand[0]);
			if (pi2 == null) {
				System.out.println("Error in line " + nowLineNum + " : 指定された画像は定義されていません。");
				nextLineNum = nowLineNum + 1;
				break;
			}
			groundim = new Picimage(0, 0, thisv);
			thisv.drawClear(Const.LAYER_BACK);
			thisv.setImage(groundim, Const.LAYER_BACK);
			double r = 0.0;
			g = groundim.getImage().getGraphics();
			for (int i2 = 0; i2 < 70; i2++) {
				g.setColor(new Color(255, 255, 255));
				g.fillRect(0, 0, Const.WINDOW_WIDTH, Const.WINDOW_HEIGHT);
				for (int y = 0; y < 6; y++) {
					for (int x = 0; x < 8; x++) {
						double t = r - x * 0.5 - (y % 2) * 0.5;
						int size = (int) (128.0 * Math.cos(t));
						if (t < 0)
							size = 128;
						if (t > 3.14 / 2)
							size = 0;
						g.drawImage(pi1.getBufferedImage(), 128 * x + 128 - size, 128 * y, 128 * x + 128, 128 * y + 128,
								128 * x, 128 * y, 128 * x + 128, 128 * y + 128, thisv);
					}
				}
				if (r > 3.14 / 2) {
					for (int y = 0; y < 6; y++) {
						for (int x = 0; x < 8; x++) {
							double t = r - x * 0.5 - (y % 2) * 0.5 - 3.14 / 2;
							int size = (int) (128.0 * Math.cos(t));
							if (t < 0)
								size = 128;
							if (t > 3.14 / 2)
								size = 0;
							g.drawImage(pi2.getBufferedImage(), 128 * x + size, 128 * y, 128 * x + 128, 128 * y + 128,
									128 * x, 128 * y, 128 * x + 128, 128 * y + 128, thisv);
						}
					}
				}
				r += 0.1;
				thisv.draw(thisv);
				thisv.update();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			thisv.drawClear(Const.LAYER_BACK);
			thisv.setImage(pi2, Const.LAYER_BACK);
			ScriptManager.nowBackPicName = operand[0];
			nextLineNum = nowLineNum + 1;
			break;

		/* 音声定義 */
		case "musicdef":
			wv = new Wave();
			wv.load(operand[0], 1);
			waveTable.recordWave(operand[1], wv);
			nextLineNum = nowLineNum + 1;
			break;

		/* BGM再生 */
		case "bgmplay":
			wv = waveTable.getWave(operand[0]);
			ScriptManager.bgm = wv;
			if (wv == null) {
				System.out.println("Error in line " + nowLineNum + " : 指定された音声は定義されていません。");
			} else {
				wv.volume(Game.volume);
				wv.play(true);
				ScriptManager.nowPlayingBGMName = operand[0];
			}
			nextLineNum = nowLineNum + 1;
			break;

		/* BGMストップ */
		case "bgmstop":
			ScriptManager.bgm.stop();
			nextLineNum = nowLineNum + 1;
			ScriptManager.nowPlayingBGMName = null;
			break;

		case "seplay":
			se = waveTable.getWave(operand[0]);
			if (se == null) {
				System.out.println("Error in line " + nowLineNum + " : 指定された音声は定義されていません。");
			} else {
				se.volume(Game.volume);
				se.play(false);
			}
			nextLineNum = nowLineNum + 1;
			break;

		/* キャラクター定義 */
		case "chdef":
			for (i = 0; characters[i] != null; i++)
				;
			System.out.println(i);
			characters[i] = new Character(operand[0], operand[1], thisv);
			nextLineNum = nowLineNum + 1;
			break;

		/* キャラクター差分定義 */
		case "chddef":
			i = 0;
			while (true) {
				if (characters[i].getName().equals(operand[0])) {
					characters[i].addDifference(operand[1], operand[2]);
					break;
				}
				i++;
				if (i == Const.CHARACTER_MAX) {
					System.out.println("Error in line " + nowLineNum + " : 差分定義しようとしたキャラクターは未定義です。");
					break;
				}
			}
			nextLineNum = nowLineNum + 1;
			break;

		/* キャラクター表示 */
		case "chset":
			int[] setChNum = { -1, -1, -1 };
			System.out.println(characters[0].getName());
			for (int j = 0; j < 3; j++) {
				if (operand[j * 2] == null)
					break;
				System.out.println(operand[j * 2]);
				i = 0;
				while (characters[i] != null) {
					if (characters[i].getName().equals(operand[j * 2])) {
						System.out.println("set " + j + " num " + i);
						setChNum[j] = i;
						System.out.println("in");
					}
					i++;
				}
			}
			for (float a = 0.0F; a < 1.0F; a += 0.02F) {
				for (i = 0; i < 3; i++) {
					if (setChNum[i] == -1)
						break;
					if (characters[setChNum[i]].set(operand[i * 2 + 1], a) == Const.FAILURE) {
						System.out.println("Error in line " + nowLineNum + " : ポジションの書き方が間違っています。");
					}
				}
				thisv.draw(thisv);
				thisv.update();

				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
			thisv.printLayerImageName(Const.LAYER_CHARACTER);
			nextLineNum = nowLineNum + 1;
			break;

		/* キャラクター非表示 */
		case "chhide":
			int[] hideChNum = { -1, -1, -1 };
			System.out.println(characters[0].getName());
			for (int j = 0; j < 3; j++) {
				if (operand[j] == null)
					break;
				System.out.println(operand[j]);
				i = 0;
				while (characters[i] != null) {
					if (characters[i].getName().equals(operand[j])) {
						System.out.println("set " + j + " num " + i);
						hideChNum[j] = i;
						System.out.println("in");
					}
					i++;
				}
			}
			for (float a = 1.0F; a > 0.0F; a -= 0.02F) {
				for (i = 0; i < 3; i++) {
					if (hideChNum[i] == -1)
						break;
					if (characters[hideChNum[i]].visible == false)
						break;
					characters[hideChNum[i]].set(characters[hideChNum[i]].position, a);
				}
				thisv.draw(thisv);
				thisv.update();

				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
			for (i = 0; i < 3; i++) {
				if (hideChNum[i] != -1)
					characters[hideChNum[i]].hide();
			}
			nextLineNum = nowLineNum + 1;
			break;

		/* キャラクター差分変更 */
		case "chdset":
			i = 0;
			while (true) {
				if (characters[i].getName().equals(operand[0])) {
					if (characters[i].changeToDifference(operand[1]) == Const.FAILURE) {
						System.out.println("Error in line " + nowLineNum + " : キャラクター" + operand[0] + "の差分 "
								+ operand[1] + "は未定義です。");
					}
					break;
				}
				i++;
				if (i == Const.CHARACTER_MAX) {
					System.out.println("Error in line " + nowLineNum + " : 差分変更しようとしたキャラクターは未定義です。");
					break;
				}
			}
			nextLineNum = nowLineNum + 1;
			break;

		/* キャラクター移動 */
		case "chmove":
			i = 0;
			while (true) {
				if (characters[i].getName().equals(operand[0])) {
					System.out.println("move");
					if (characters[i].move(operand[1]) == Const.FAILURE) {
						System.out.println("Error in line " + nowLineNum + " : ポジションの書き方が間違っています。");
					}
					break;
				}
				i++;
			}
			nextLineNum = nowLineNum + 1;
			break;

		/* ラベル先ジャンプ */
		case "goto":
			nextLineNum = labelTable.getToNumber(operand[0]);
			break;

		/* 選択肢表示 */
		case "branch":
			ScriptManager.w = new MsgWindow(thisv, operand[0]);
			thisv.initMouse();
			while (thisv.getClicked() == false) {
				thisv.draw(thisv);
				if (ScriptManager.w.msgPrint(false))
					break;
				thisv.update();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				Game.ct.next();
			}
			thisv.initMouse();
			float alpha = 0.0F;
			StringButton[] buttons = new StringButton[3];
			for (i = 0; i < 3; i++) {
				if (operand[i + 1] != null) {
					buttons[i] = new StringButton(0, 0, operand[i + 1], thisv.getmyFont(), thisv);
					buttons[i].setPosition(Const.WINDOW_WIDTH / 2 - buttons[i].getWidth() / 2, 200 + i * 60);
				}

			}
			boolean breakflag = false;
			while (true) {
				alpha += 0.02F;
				if (alpha > 0.4F)
					alpha = 0.4F;
				thisv.setBlackFadeAlpha(alpha);
				thisv.draw(thisv);
				ScriptManager.w.msgPrint(true);
				for (i = 1; i < 4; i++) {
					if (operand[i] != null) {
						buttons[i - 1].drawButton();
						if (buttons[i - 1].isClicked()) {
							nextLineNum = nowLineNum + i;
							breakflag = true;
						}
					}
				}
				if (breakflag)
					break;
				thisv.update();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				Game.ct.next();
			}
			thisv.setBlackFadeAlpha(0.0F);
			break;

		/* フラグへの代入 */
		case "flag":
			tmpb = false;
			if (operand[1].equals("true"))
				tmpb = true;
			if (operand[1].equals("false"))
				tmpb = false;
			if (flagTable.setFlag(operand[0], tmpb) == Const.FAILURE) {
				flagTable.recordFlag(operand[0]);
				flagTable.setFlag(operand[0], tmpb);
			}
			nextLineNum = nowLineNum + 1;
			break;

		/* フラグによる分岐 */
		case "if":
			tmpb = false;
			if (operand[1].equals("true"))
				tmpb = true;
			if (operand[1].equals("false"))
				tmpb = false;
			if (flagTable.getFlag(operand[0]) == null) {
				System.out.println("Error in line " + nowLineNum + " : 条件式にあるフラグはまだ定義されていません。");
				nextLineNum = nowLineNum + 1;
				break;
			}
			if (flagTable.getFlag(operand[0]).booleanValue() == tmpb) {
				nextLineNum = nowLineNum + 1;
				break;
			} else {
				nextLineNum = nowLineNum + 2;
				break;
			}

			/* ウェイト */
		case "wait":
			for (i = 0; i < Integer.valueOf(operand[0]); i++) {
				thisv.draw(thisv);
				thisv.update();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
			nextLineNum = nowLineNum + 1;
			break;

		/* ファイル移動 */
		case "gotofile":
			ScriptManager.readingFile = operand[0];
			nextLineNum = -1;
			break;

		/* 定義終了(define.txtの最後に付ける) */
		case "defend":
			nextLineNum = -3;
			break;

		/* 何にも該当しない場合 */
		default:
			nextLineNum = nowLineNum + 1;
			break;

		}

		return nextLineNum;
	}

	private void menu() {
		Picimage menuBack = new Picimage("system/black_fade.png", 0, 0, thisv);
		Font font = new Font("ＭＳ Ｐゴシック", Font.PLAIN, 32);
		StringButton returnButton = new StringButton(400, Const.WINDOW_HEIGHT / 2 - 150, "ゲームに戻る", font, thisv);
		StringButton saveButton = new StringButton(400, Const.WINDOW_HEIGHT / 2 - 50, "セーブする", font, thisv);
		StringButton returnStartButton = new StringButton(400, Const.WINDOW_HEIGHT / 2 + 50, "タイトルに戻る", font, thisv);
		menuBack.setAlpha(0.5F);
		thisv.setImage(menuBack, Const.LAYER_MENU_BACK);
		thisv.initMouse();
		thisv.initEsc();
		while (true) {
			thisv.draw(thisv);
			returnButton.drawButton();
			saveButton.drawButton();
			returnStartButton.drawButton();
			if (returnButton.isClicked()) {
				thisv.initMouse();
				thisv.drawClear(Const.LAYER_MENU_BACK);
				break;
			}
			if (saveButton.isClicked()) {
				thisv.initMouse();
				fileSelect();
			}
			if (returnStartButton.isClicked()) {
				thisv.initMouse();
				if (returnCheck()) {
					thisv.initMouse();
					Game.reset = true;
					break;
				}
			}
			if (thisv.getEsc()) {
				thisv.drawClear(Const.LAYER_MENU_BACK);
				break;
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

	private void fileSelect() {
		File[] files = new File[Const.FILE_NUM_MAX];
		StringButton[] fileButton = new StringButton[Const.FILE_NUM_MAX];
		FileReader freader;
		BufferedReader breader;
		String time;
		Font font = new Font("ＭＳ Ｐゴシック", Font.PLAIN, 32);
		StringButton returnButton = new StringButton(50, 600, "戻る", font, thisv);
		thisv.initMouse();
		for (int i = 0; i < Const.FILE_NUM_MAX; i++) {
			files[i] = new File("system/playerfile/file" + (i + 1) + ".txt");
			try {
				freader = new FileReader(files[i]);
				breader = new BufferedReader(freader);
				time = breader.readLine();
				if (time.contains("nope"))
					time = "ファイルがありません";
				fileButton[i] = new StringButton(200, 100 + i * 60, "File" + (i + 1) + ":" + time, font, thisv);
				breader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		while (true) {
			thisv.draw(thisv);
			for (int i = 0; i < Const.FILE_NUM_MAX; i++) {
				fileButton[i].drawButton();
				if (fileButton[i].isClicked()) {
					if (saveCheck(i + 1)) {
						sm.save(i + 1);
						return;
					}
				}
			}
			returnButton.drawButton();
			if (returnButton.isClicked())
				break;
			thisv.update();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		thisv.initMouse();
	}

	private boolean saveCheck(int filenum) {
		Font font = new Font("ＭＳ Ｐゴシック", Font.PLAIN, 32);
		StringButton yesButton = new StringButton(400, 400, "はい", font, thisv);
		StringButton noButton = new StringButton(500, 400, "いいえ", font, thisv);
		thisv.initMouse();
		while (true) {
			thisv.draw(thisv);
			thisv.getmyGraphics().drawString("ファイル" + filenum + "にセーブします。よろしいですか？", 200, 300);
			yesButton.drawButton();
			noButton.drawButton();
			if (yesButton.isClicked()) {
				thisv.initMouse();
				return true;
			}
			if (noButton.isClicked()) {
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

	private boolean returnCheck() {
		Font font = new Font("ＭＳ Ｐゴシック", Font.PLAIN, 32);
		StringButton yesButton = new StringButton(400, 400, "はい", font, thisv);
		StringButton noButton = new StringButton(500, 400, "いいえ", font, thisv);
		thisv.initMouse();
		while (true) {
			thisv.draw(thisv);
			thisv.getmyGraphics().drawString("タイトル画面に戻ります。よろしいですか？", 200, 300);
			thisv.getmyGraphics().drawString("(セーブされていない記録はすべて消去されます。)", 200, 335);
			yesButton.drawButton();
			noButton.drawButton();
			if (yesButton.isClicked()) {
				thisv.initMouse();
				return true;
			}
			if (noButton.isClicked()) {
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

class Character {

	View thisv;
	Picimage characterPic;
	String nowFaceName = "default";
	String name;
	String position = "center";
	PicTable difPicTable = new PicTable();
	boolean visible = false;

	Character(String filename, String name, View v) {
		thisv = v;
		characterPic = new Picimage(filename, 0, 0, thisv);
		characterPic.setVisible(false);
		thisv.setImage(characterPic, Const.LAYER_CHARACTER);
		this.name = name;
		difPicTable.recordPic("default", characterPic);
		System.out.println("name : " + name);
		characterPic.setAlpha(0.0F);
	}

	public String getName() {
		return name;
	}

	public String getPosition() {
		return position;
	}

	public String getNowFaceName() {
		return nowFaceName;
	}

	public boolean getVisible() {
		return visible;
	}

	public void addDifference(String filename, String name) {
		difPicTable.recordPic(name, new Picimage(filename, 0, 0, thisv));
	}

	public int changeToDifference(String name) {
		Picimage next;
		if ((next = difPicTable.getPicimage(name)) == null) {
			return Const.FAILURE;
		} else {
			thisv.changeLayerPic(Const.LAYER_CHARACTER, characterPic, next);
			characterPic = next;
			switch (position) {
			case "center":
				characterPic.setPosition((Const.WINDOW_WIDTH / 2) - (characterPic.getWidth() / 2),
						(Const.WINDOW_HEIGHT / 2) - (characterPic.getHeight() / 2));
				break;
			case "left":
				characterPic.setPosition((Const.WINDOW_WIDTH / 4) - (characterPic.getWidth() / 2),
						(Const.WINDOW_HEIGHT / 2) - (characterPic.getHeight() / 2));
				break;
			case "right":
				characterPic.setPosition((Const.WINDOW_WIDTH / 4) * 3 - (characterPic.getWidth() / 2),
						(Const.WINDOW_HEIGHT / 2) - (characterPic.getHeight() / 2));
				break;
			}
			nowFaceName = name;
			if (visible)
				characterPic.setAlpha(1.0F);
			characterPic.setVisible(visible);
			return Const.SUCCESS;
		}
	}

	public int set(String position, float a) {
		this.position = position;
		switch (position) {
		case "center":
			characterPic.setPosition((Const.WINDOW_WIDTH / 2) - (characterPic.getWidth() / 2),
					(Const.WINDOW_HEIGHT / 2) - (characterPic.getHeight() / 2));
			break;
		case "left":
			characterPic.setPosition((Const.WINDOW_WIDTH / 4) - (characterPic.getWidth() / 2),
					(Const.WINDOW_HEIGHT / 2) - (characterPic.getHeight() / 2));
			break;
		case "right":
			characterPic.setPosition((Const.WINDOW_WIDTH / 4) * 3 - (characterPic.getWidth() / 2),
					(Const.WINDOW_HEIGHT / 2) - (characterPic.getHeight() / 2));
			break;
		default:
			return Const.FAILURE;
		}
		visible = true;
		characterPic.setVisible(true);
		characterPic.setAlpha(a);
		return Const.SUCCESS;
	}

	public int move(String position) {
		int toX, toY;
		double tmpX;
		this.position = position;
		toY = (Const.WINDOW_HEIGHT / 2) - (characterPic.getHeight() / 2);
		switch (position) {
		case "center":
			toX = (Const.WINDOW_WIDTH / 2) - (characterPic.getWidth() / 2);
			break;
		case "left":
			toX = (Const.WINDOW_WIDTH / 4) - (characterPic.getWidth() / 2);
			break;
		case "right":
			toX = (Const.WINDOW_WIDTH / 4) * 3 - (characterPic.getWidth() / 2);
			break;
		default:
			return Const.FAILURE;
		}
		tmpX = (double) characterPic.getX();
		thisv.printLayerImageName(Const.LAYER_CHARACTER);
		System.out.println(characterPic.getImageName());
		System.out.println("x : " + toX);
		System.out.println("tmpX : " + tmpX);
		for (int i = 0; i < 70; i++) {
			System.out.println("move?" + name);
			System.out.println(tmpX);
			tmpX += ((double) toX - tmpX) * 0.1;
			characterPic.setPosition((int) tmpX, toY);
			thisv.draw(thisv);
			thisv.update();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		characterPic.setPosition(toX, toY);
		return Const.SUCCESS;
	}

	public void hide() {
		visible = false;
		characterPic.setVisible(false);
	}

	public void afterLoad(String position, String nowFaceName, String visible) {
		this.position = position;
		this.nowFaceName = nowFaceName;
		this.visible = Boolean.valueOf(visible);
		changeToDifference(nowFaceName);
	}
}

class PicTable {

	String[] picName = new String[100];
	Picimage[] pic = new Picimage[100];

	PicTable() {

	}

	public void recordPic(String n, Picimage p) {
		int i;
		for (i = 0; picName[i] != null; i++)
			;
		picName[i] = n;
		pic[i] = p;
	}

	public Picimage getPicimage(String name) {
		int i;
		for (i = 0; i < 100; i++) {
			if (pic[i] == null) {
				return null;
			}
			if (picName[i].equals(name)) {
				return pic[i];
			}
		}
		return null;
	}
}

class WaveTable {

	String[] waveName = new String[100];
	Wave[] wave = new Wave[100];

	WaveTable() {

	}

	public void recordWave(String n, Wave w) {
		int i;
		for (i = 0; waveName[i] != null; i++)
			;
		waveName[i] = n;
		wave[i] = w;
	}

	public Wave getWave(String name) {
		int i;
		for (i = 0; i < 100; i++) {
			if (wave[i] == null) {
				return null;
			}
			if (waveName[i].equals(name)) {
				return wave[i];
			}
		}
		return null;
	}
}

class LabelTable {
	String[] labelName = new String[100];
	int[] toNumber = new int[100];

	LabelTable() {
		for (int i = 0; i < toNumber.length; i++) {
			toNumber[i] = -1;
		}
	}

	public void recordLabel(String n, int toNum) {
		int i;
		for (i = 0; labelName[i] != null; i++)
			;
		labelName[i] = n;
		toNumber[i] = toNum;
	}

	public int getToNumber(String name) {
		int i;
		for (i = 0; i < 100; i++) {
			if (toNumber[i] == -1) {
				return Const.FAILURE;
			}
			if (labelName[i].equals(name)) {
				return toNumber[i];
			}
		}
		return Const.FAILURE;
	}
}

class MsgManager {

	String msg;
	String nowmsg = "";
	int viewPoint = 0;

	MsgManager(String msg) {
		this.msg = msg;
	}

	public String getMessage(boolean next) {
		System.out.println(nowmsg);
		if (next && viewPoint < msg.length()) {
			nowmsg += msg.charAt(viewPoint);
			viewPoint++;
		}
		return nowmsg;
	}
}

class Stack {

	int[] stackNum = new int[1000];

	Stack() {
		for (int i = 0; i < 1000; i++) {
			stackNum[i] = -1;
		}
	}

	public void push(int num) {
		for (int i = 0; i < 1000; i++) {
			if (stackNum[i] == -1) {
				stackNum[i] = num;
				break;
			}
		}
	}

	public int pop() {
		for (int i = 0; i < 1000; i++) {
			if (stackNum[i] == -1) {
				return stackNum[i - 1];
			}
		}
		return Const.FAILURE;
	}
}

class FlagTable {
	String[] flagName = new String[1000];
	boolean[] flag = new boolean[1000];

	FlagTable() {
		;
		for (int i = 0; i < flag.length; i++) {
			flag[i] = false;
		}
	}

	public void recordFlag(String n) {
		int i;
		for (i = 0; flagName[i] != null; i++)
			;
		flagName[i] = n;
	}

	public int setFlag(String name, boolean tf) {
		for (int i = 0; i < flag.length; i++) {
			if (flagName[i] == null) {
				return Const.FAILURE;
			}
			if (flagName[i].equals(name)) {
				flag[i] = tf;
				return Const.SUCCESS;
			}
		}
		return Const.FAILURE;
	}

	public Boolean getFlag(String name) {
		int i;
		for (i = 0; i < flag.length; i++) {
			if (flagName[i] == null) {
				return null;
			}
			if (flagName[i].equals(name)) {
				return Boolean.valueOf(flag[i]);
			}
		}
		return null;
	}

	/* for save() only */
	public String getFlagLine() {
		int i = 0;
		String line = "";
		while (flagName[i] != null) {
			line += flagName[i] + ",";
			i++;
		}
		return line;
	}

	public String getTFLine() {
		int i = 0;
		String line = "";
		while (flagName[i] != null) {
			line += flag[i] + ",";
			i++;
		}
		return line;
	}
}
