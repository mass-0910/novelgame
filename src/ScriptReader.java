import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ScriptReader {

	File file;
	BufferedReader reader;
	String[] scriptLines = new String[Const.LINE_MAX];

	ScriptReader(String scriptFileName){
		file = new File(scriptFileName);
		String tmpstr;
		int i = 0;
		if(file.exists()) {
			try {
				FileReader freader = new FileReader(file);
				reader = new BufferedReader(freader);
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}else {
			System.out.println("スクリプトファイルが存在しません。");
			System.exit(-1);
		}
		try {
			while((tmpstr = reader.readLine()) != null) {
				scriptLines[i] = new String(tmpstr);
				i++;
			}
			System.out.println("i : " + i);
			reader.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void printScript() {
		int i = 0;
		while(scriptLines[i] != null) {
			System.out.println(scriptLines[i]);
			i++;
		}
	}

	public String getLine(int linenum) {
		return scriptLines[linenum];
	}

}
