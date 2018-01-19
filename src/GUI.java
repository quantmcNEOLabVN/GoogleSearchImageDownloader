import java.awt.Font;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;
public class GUI{
	public JFrame mainFrame;
	public TextField keywordBox,targetDBox;
	public JButton buttonBrowse,buttonSearchImage,buttonDownload,buttonNext,buttonPrev;
	public JPanel iliterateButtons;
	public JLabel imgPanel;
	public ArrayList<String> linkResult; 
	public int indexLink;
	public Customsearch myGSearchServ;
	public JTextArea infoImg;
	private String apikey="AIzaSyBseCgmvsJ5L4QBzKYQUSAnxcQ2fWIEA_o",cx="006695016743302591096:zwejjbi9yay";
	public ArrayList<String>histurl=new ArrayList<String>();
	private void setupSearchService() {
		myGSearchServ = new Customsearch(new NetHttpTransport(),new JacksonFactory(), new HttpRequestInitializer(){
            public void initialize(HttpRequest httpRequest) {
                try {
                    // set connect and read timeouts
                    httpRequest.setConnectTimeout(6000000);
                    httpRequest.setReadTimeout(6000000);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }});
	}
	public String currentMD5="";
	public long searchIndex=1;
	public ArrayList<Result> totalResultList;
	public void exeQuery(String keyword) {
	    List<Result> resultList=null;
	    try {
	        Customsearch.Cse.List list=myGSearchServ.cse().list(keyword);
	        list.setKey(apikey);
	        list.setCx(cx);
	        list.setSearchType("image");
	        list.setStart(searchIndex);
	        
	        Search results=list.execute();
	        resultList=results.getItems();
	        for (int i=0;i<resultList.size();i++) {
	        	Result res=resultList.get(i);
	        	histurl.add(res.getLink());
	        	
	        	//System.out.println(res.getLink());
	        	totalResultList.add(res);
	        }
	        searchIndex+=resultList.size();
	    }
	    catch (  Exception e) {
	        e.printStackTrace();
	    }
	    
	}
	public class fileNameGenerator{
		public char [] fNameChars; 
		public fileNameGenerator() {
			this.reset();
		}
		public void reset() {
			String dir=targetDBox.getText();
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
	public byte[] imageToMD5(BufferedImage buffImg,String fType) throws Exception{
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
        byte[] data = ((DataBufferByte) buffImg.getData().getDataBuffer()).getData();

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data);
        byte[] hash = md.digest();
        return hash;
	}
	public void showImg() throws Exception{	
		try {
			String thumburl=totalResultList.get(currentIndex).getImage().getThumbnailLink();
			
			BufferedImage image=ImageIO.read(new URL(thumburl));
			
			String fType="";
			for (int i=thumburl.length()-1;i>=0;i--) {
				if (thumburl.charAt(i)=='.') break;
				fType=Character.toString(thumburl.charAt(i));
			}
			imgPanel.setIcon(new ImageIcon(image));
			byte[] md5=imageToMD5(image, fType);
			currentMD5=bytesToHexString(md5);
			System.out.println(currentMD5);
			imgPanel.repaint();
		}catch(Exception e) {
			System.out.println("Error in loading image thumb.");
			e.printStackTrace();
		}
		mainFrame.repaint();
		
	}
	public char[] hexDigit;
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
	public fileNameGenerator fNameGenerator;
	
	public void syncObj() {
		hashMan.targetDir=targetDBox.getText();
		hashMan.currentMD5=currentMD5;
		
	}
	public HashesManager hashMan;
	
	public GUI() throws Exception {
		
		totalResultList=new ArrayList<Result>();
		setupSearchService();
		mainFrame=new JFrame();
		mainFrame.setTitle("Google Image Downloader");
		mainFrame.setSize(1040, 720);
		mainFrame.setLayout(null);
		fNameGenerator=null;
		keywordBox=new TextField();
		keywordBox.setSize(800, 40);
		keywordBox.setLocation(5, 5);
		keywordBox.setFont(new Font(null, 0, 18));
		mainFrame.add(keywordBox);
		
		buttonSearchImage = new JButton("Search Images");
		buttonSearchImage.setSize(200, 40);
		buttonSearchImage.setLocation(810, 5);
		mainFrame.add(buttonSearchImage);

		
		targetDBox=new TextField("targetDirectory");
		targetDBox.setSize(800, 40);
		targetDBox.setLocation(5, 50);
		targetDBox.setFont(new Font(null, 0, 18));
		targetDBox.setEditable(false);
		mainFrame.add(targetDBox);
		
		
		buttonBrowse = new JButton("Select Directory");
		buttonBrowse.setSize(200, 40);
		buttonBrowse.setLocation(810, 50);
		buttonBrowse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					hashMan.saveFileLog();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				JFileChooser f = new JFileChooser();
		        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
		        f.showSaveDialog(null);
		        
		        System.out.println(f.getCurrentDirectory());
		        targetDBox.setEditable(true);
		        targetDBox.setText(f.getSelectedFile().toString());
		        targetDBox.setEditable(false);
		        if (fNameGenerator==null)
		        	fNameGenerator=new fileNameGenerator();
		        fNameGenerator.reset();	
		        try {
					hashMan.reset();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        syncObj();
			}
		});
		mainFrame.add(buttonBrowse);
		
		
		iliterateButtons=new JPanel();
		iliterateButtons.setSize(1080,50);
		iliterateButtons.setLocation(5,100);
		iliterateButtons.setLayout(null);
		
		buttonPrev= new JButton("Previous");
		buttonPrev.setSize(100, 40);
		buttonPrev.setLocation(5, 5);
		iliterateButtons.add(buttonPrev);
		
		buttonDownload= new JButton("Download/Save");
		buttonDownload.setSize(200, 40);
		buttonDownload.setLocation(110, 5);
		iliterateButtons.add(buttonDownload);
		
		buttonNext= new JButton("Next");
		buttonNext.setSize(200, 40);
		buttonNext.setLocation(330, 5);
		iliterateButtons.add(buttonNext);
		
	
		mainFrame.add(iliterateButtons);
		
		imgPanel=new JLabel();
		imgPanel.setLocation(5, 200);
		mainFrame.add(imgPanel); 
		imgPanel.setSize(500, 500);
		
		
		infoImg=new JTextArea();
		infoImg.setSize(500,700);
		infoImg.setLocation(520,230);
		infoImg.setLineWrap(true);
		infoImg.setFont(new Font(null, 0, 18));
		mainFrame.add(infoImg);	
		
		hashMan=new HashesManager();
		mainFrame.show();
		addActionToButtons();
		mainFrame.setDefaultCloseOperation(mainFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(mainFrame, 
			            "Are you sure to close this window?", "Really Closing?", 
			            JOptionPane.YES_NO_OPTION,
			            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
		        			try {
								hashMan.saveFileLog();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				            System.exit(0);
			        	}
		        }
		});
	}
	public int currentIndex;
	public void updateInfo() {
		Result res=totalResultList.get(currentIndex);
		Result.Image img=res.getImage();
		String text="Information:\n Original Dimension: "+Integer.toString(img.getWidth())+" x "+Integer.toString(img.getHeight())+"\n Link: "+res.getLink()+"\n\nthumb MD5:\n"+currentMD5;
		if (hashMan.checkInlist()) {
			text=text+"\nThis file has been downloaded!";
			buttonDownload.setEnabled(false);
		}else {
			buttonDownload.setEnabled(true);
		}
		buttonDownload.repaint();
		iliterateButtons.repaint();
		infoImg.setText(text);
		infoImg.repaint();
		mainFrame.repaint();
	}
	private void addActionToButtons() {
		buttonSearchImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				currentIndex=0;
				histurl.clear();
				totalResultList.clear();
				currentMD5="";
				try {
					setupSearchService();
					exeQuery(keywordBox.getText());
					showImg();
					updateInfo();
				}catch (Exception e) {
					infoImg.setText("");
					JOptionPane.showMessageDialog(mainFrame, "can't find any result!");
					e.printStackTrace();
				}
			}
		});
		buttonPrev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				currentIndex=Math.max(0, currentIndex);
				if (currentIndex==0) {		
					JOptionPane.showMessageDialog(mainFrame, "can't find any results!");
					return;
				}
				currentIndex--;
				try {
					showImg();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				updateInfo();
			}
		});
		buttonNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				currentIndex++;
				if (currentIndex>=histurl.size()) {
					exeQuery(keywordBox.getText());
					if (currentIndex>=histurl.size()) {
						JOptionPane.showMessageDialog(mainFrame, "can't find anymore results!");
						currentIndex--;
						infoImg.setText("");
						return;
					}
					
				}
				try {
					showImg();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}					
				updateInfo();
			}
		});
		
		buttonDownload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {					
				hashMan.addToList();
				downloadFileFromURL(histurl.get(currentIndex), fNameGenerator.generateFileName());
			}
		});
	}
	public void downloadFileFromURL(String urlString, String fName) {  
        new Thread(new Runnable() {
			@Override
			public void run() {
				        try {
				        	String fType="";
				        	for (int i=urlString.length()-1;i>=0;i--) {
				        		fType=Character.toString(urlString.charAt(i))+fType;
				        		if (fType.charAt(0)=='.') break;
				        	}
				        	String destination=fName+fType;
				            URL website = new URL(urlString);
				            ReadableByteChannel rbc;
				            rbc = Channels.newChannel(website.openStream());
				            FileOutputStream fos = new FileOutputStream(destination);
				            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				            fos.close();
				            rbc.close();
				            System.out.println("File downloaded: "+destination);
				            updateInfo();
				        } catch (IOException e) {
				        	System.out.println("File unable to downloaded: "+urlString);
				            e.printStackTrace();
				        }
			}}).run();
        
    }
}