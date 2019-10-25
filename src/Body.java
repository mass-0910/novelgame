
public class Body {

	View thisv = null;
	ScriptManager sm = null;

	Body(View v, boolean isNewGame, int filenum){
		thisv = v;
		thisv.drawClearAll();
		sm = new ScriptManager(thisv, isNewGame, filenum);
		sm.printExection();
		thisv.setImage(thisv.getFader(), Const.LAYER_FADER);
		thisv.setImage(thisv.getBlackFader(), Const.LAYER_FADER);
		while(true) {
			if(sm.execLine() == Const.FAILURE) {
				if(Game.reset)return;
				break;
			}
		}
		System.out.println("END");
	}
}
