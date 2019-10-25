import java.awt.Color;
import java.awt.Font;

public class MsgWindow {

	Picimage pi = null;
	View thisv = null;
	Font font = null;
	boolean visible = true;
	char[] ch = null;
	int[] chAlpha = new int[Const.MESSAGE_NUM_MAX];
	static String talkerName = "";
	int strR = 255;
	int strG = 255;
	int strB = 255;

	MsgWindow(View v, String msg){
		thisv = v;
		thisv.drawClear(Const.LAYER_WINDOW);
		pi = thisv.getWinPic();
		pi.setVisible(true);
		thisv.setImage(pi, Const.LAYER_WINDOW);
		font = new Font("ＭＳ Ｐゴシック", Font.PLAIN, 32);
		ch = msg.toCharArray();
		for(int i = 0 ; i < ch.length ; i++) {
			chAlpha[i] = -10 * i;
		}
	}

	public boolean msgPrint(boolean all) {
		int x = 0, y = 0;
		int end = 1;
		font = new Font("ＭＳ Ｐゴシック", Font.PLAIN, 32);
		thisv.getmyGraphics().setFont(font);
		thisv.getmyGraphics().setColor(new Color(0, 0, 0, 255));
		thisv.getmyGraphics().drawString(talkerName, 75, 575);
		thisv.getmyGraphics().setColor(new Color(255, 255, 255, 255));
		thisv.getmyGraphics().drawString(talkerName, 70, 575);

		for(int i = 0; i < ch.length ; i++) {
			if(all)chAlpha[i] = 255;
			if(ch[i] == ';') { /* 改行文字 */
				y++;
				x = 0;
			}else if(ch[i] == '%'){ /* 文字色 */
				i++;
				switch(ch[i]) {
				case 'r':
					strR = 255 ; strG = 0 ; strB = 0;
					break;
				case 'g':
					strR = 0 ; strG = 255 ; strB = 0;
					break;
				case 'b':
					strR = 0 ; strG = 0 ; strB = 255;
					break;
				case 'w':
					strR = 255 ; strG = 255 ; strB = 255;
					break;
				default:
					System.out.println("色の書式が違います。");
					break;
				}
			}else if(ch[i] == '&') { /* フォント */
				i++;
				switch(ch[i]) {
				case 'p':
					font = new Font("ＭＳ Ｐゴシック", Font.PLAIN, 32);
					break;
				case 'i':
					font = new Font("ＭＳ Ｐゴシック", Font.ITALIC, 32);
					break;
				case 'b':
					font = new Font("ＭＳ Ｐゴシック", Font.BOLD, 32);
					break;
				case 's':
					font = new Font("ＭＳ Ｐゴシック", Font.PLAIN, 16);
					break;
				default:
					System.out.println("色の書式が違います。");
					break;
				}
				thisv.getmyGraphics().setFont(font);
			}else if(chAlpha[i] > 0) {
				thisv.getmyGraphics().setColor(new Color(strR, strG, strB, chAlpha[i]));
				if(visible)thisv.getmyGraphics().drawString(String.valueOf(ch[i]), 50 + x * 32, 630 + y * 45);
				x++;
				if(x > 27) {
					x = 0;
					y++;
				}
			}
			chAlpha[i] += 10;
			if(chAlpha[i] > 255)chAlpha[i] = 255;
			if(chAlpha[i] != 255)end *= 0;
		}
		if(end == 1) {
			return true;
		}else {
			return false;
		}
	}

	public void setVisible(boolean v) {
		visible = v;
		pi.setVisible(v);
	}

	public void setFont(String fontname, int fontstat, int fontpt) {
		font = new Font(fontname, fontstat, fontpt);
	}
}
