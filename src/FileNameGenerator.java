import java.io.File;

public class FileNameGenerator{
	public char [] fNameChars;
	public String dir;
	public FileNameGenerator() {
		dir="targetDir";
	}
	public void reset(String dir) {
		this.dir=dir;
		fNameChars=(dir+"/0000000").toCharArray();
		while (check()==false) next();				
	}
	public String[] types={".PNG",".png",".jpg",".JPG",".JPEG",".jpeg",".tiff",".TIFF",".tif",".TIF"};
	public boolean check() {
		String fName=getFName();
		System.out.println(fName);
		for (String fType:types) {
			File f= new File(fName+fType);
			if (f.isFile()==true) return false;
		}
		return true;
	}
	public String getFName() {
		return String.valueOf(fNameChars);
	}
	public String generateFileName() {
		while (check()==false) next();
		System.out.println("File name generated: "+getFName());
		return getFName();
	}
	private void next() {
		int i=fNameChars.length-1;
		while (true) {
			if (fNameChars[i]=='9'){
				fNameChars[i]='0';
				i--;
			}
			else {
				fNameChars[i]=(char)(((int)fNameChars[i])+1); 
				break;
			}
		}	
	}
};
