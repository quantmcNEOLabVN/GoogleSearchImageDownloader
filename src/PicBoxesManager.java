import java.awt.GridLayout;
import java.util.*;

import javax.swing.*;

import com.google.api.services.customsearch.model.Result;

public class PicBoxesManager extends JPanel{
	public PictureBox[] picBoxes;
	public HashesManager hashMan;
	public FileNameGenerator fNameGen;
	public PicBoxesManager(HashesManager hMan,FileNameGenerator fgen) {
		super();
		hashMan=hMan;
		fNameGen=fgen;
		picBoxes=new PictureBox[10];
		setLayout(new GridLayout(2, 5, 4,4));
		int i;
		for (i=0;i<picBoxes.length;i++) {
			picBoxes[i]=new PictureBox(hMan);
			this.add(picBoxes[i]);
			
		}
	}
	public void reset() {
		for (int i=0;i<picBoxes.length;i++)
			picBoxes[i].reset();
	}
	public void doRepaint(){
		for (int i=0;i<picBoxes.length;i++)
			picBoxes[i].doRepaint();
	}
	public void showResults(List<Result> resList) throws Exception{
		if (resList==null) {
			reset();
			return;
		}
		if (resList.size()>picBoxes.length) 
				throw new Exception("Too many results to show: "+resList.size());
		for (int i=0;i<picBoxes.length;i++) {
			if (i>=resList.size())
				picBoxes[i].reset();
			picBoxes[i].showResult(resList.get(i));
			picBoxes[i].trySetSelected(true);
		}
	}
	public void doDownload() {
		for (PictureBox pb:picBoxes) {
			pb.downloadFile(fNameGen.generateFileName());
		}
	}
	public void  trySetSelected(boolean b) {
		for (PictureBox pb:picBoxes) {
			pb.trySetSelected(b);
		}
	}
	public void keyselect(int id) throws Exception {
		if ((id>9)||(id<0))
			throw new Exception();
		picBoxes[id].trySetSelected(!picBoxes[id].isSelected());
	}
}
