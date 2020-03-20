
public class Main {

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		Game g = new Game();
	}

}

class Game{
	int gamestat;
	View v = new View("ノベルゲーム");
	public static Counter ct = new Counter();
	public static float volume = 0.5F;
	public static boolean reset = false;

	Game(){
		Title t = new Title(v);
		Body b = new Body(v, t.isNewGame, t.filenum);
		while(reset) {
			reset = false;
			ScriptManager.bgm.stop();
			v.drawClearAll();
			t = new Title(v);
			b = new Body(v, t.isNewGame, t.filenum);
		}
	}
}

class Counter {

	int count;

	public void next() {
		count++;
	}

	public int getCount() {
		return count;
	}

	public void initCount() {
		count = 0;
	}

	public boolean isMultipleNum(int num) {
		if(count % num == 0) {
			return true;
		}else {
			return false;
		}
	}
}