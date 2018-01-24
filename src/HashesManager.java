import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Scanner;
public class HashesManager{
	public char[] hexDigit;
	public class trieNode{
		private trieNode[] nodes=null;
		private boolean isEnd;
		public trieNode(){
			nodes=new trieNode[16];
			for (int i=0;i<nodes.length;i++){
				nodes[i]=null;
			}
			isEnd=false;
		}
		public void add(char[] a, int i) {
			if (i>=a.length) {
				this.isEnd=true;
				return;
			}
			int x=hexDigitInt(a[i]);
			if (nodes[x]==null) nodes[x]=new trieNode();
			nodes[x].add(a, i+1);
		}
		public boolean checkExist(char [] a, int i) {
			if (i>=a.length) return this.isEnd;
			int x=hexDigitInt(a[i]);
			if (nodes[x]==null) return false;
			return nodes[x].checkExist(a, i+1);
		}
		public ArrayList<String>listAll() {
			ArrayList<String> list=new ArrayList<String>();
			list.clear();
			if (this.isEnd==true)
				list.add(str.toString());
			for (int i=0;i<nodes.length;i++) if (nodes[i]!=null){
				str.append(hexDigit[i]);
				list.addAll(nodes[i].listAll());
				str.deleteCharAt(str.length()-1);
			}
			return list;
		}
	}
	public int hexDigitInt(char x) {
		if ('0'<=x&&x<='9')
			return (int)(x-'0');
		else
			return 10+((int)(x-'a'));
	}
	public trieNode trie;
	public String targetDir="";

	public HashesManager(String targ) throws Exception{
		reset(targ);
	}
	public void reset(String target) throws Exception {
		hexDigit="0123456789abcdefg".toCharArray();
		this.targetDir=target;
		this.trie=new trieNode();
		this.loadFileLog();
	}
	public StringBuilder str;
	public ArrayList<String> getList(){
		str=new  StringBuilder();
		return trie.listAll();
	}
	public void saveFileLog() throws Exception{
		File theDir = new File(targetDir);
		if (!theDir.exists()) {
		    System.out.println("creating directory: " + targetDir);
		    boolean result = false;
		    try{
		        theDir.mkdir();
		        result = true;
		    } 
		    catch(Exception se){
		    	se.printStackTrace();
		        //handle it
		    }        
		    if(result) {    
		        System.out.println("Target Directory created");  
		    }
		}
		FileWriter fWriter=new FileWriter(targetDir+"/downloadedFile.txt");
		ArrayList<String> l=getList();
		for (int i=0;i<l.size();i++) {
			if (i>0) fWriter.write("\n");
			fWriter.write(l.get(i));
		}
		fWriter.flush();
		fWriter.close();
		System.out.print("downloadedFile.txt was saved");
	}
	public String bytesToHexString(byte[] b) {
		StringBuilder sb=new  StringBuilder();
		for (int i=0;i<b.length;i++) {
			int x=Byte.toUnsignedInt(b[i]);
			//System.out.println(x);
			sb.append(hexDigit[x/16]);
			sb.append(hexDigit[x%16]);
		}
		return sb.toString();
	}
	
	public String imageToMD5(BufferedImage buffImg,String fType) throws Exception{		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = ((DataBufferByte) buffImg.getData().getDataBuffer()).getData();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data);
        byte[] hash = md.digest();
        return bytesToHexString(hash);
	}

	public void loadFileLog() throws Exception{
		try{
			trie=new trieNode();
			Scanner reader=new Scanner(new File(targetDir+"/downloadedFile.txt"));
			while (reader.hasNextLine()){
				trie.add(reader.nextLine().toCharArray(), 0);
			}
			reader.close();
		}
		catch (Exception e) {
			System.out.println(targetDir +"/downloadedFile.txt doesn't exist!");
		}
	}
	public boolean checkInlist(String currentMD5) {
		return trie.checkExist(currentMD5.toCharArray(), 0);
		
	}
	public void addToList(String currentMD5) {
		if (currentMD5=="") return;
		trie.add(currentMD5.toCharArray(), 0);				
	}
}