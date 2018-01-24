import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.*;
import com.google.api.client.json.jackson2.*;
import com.google.api.services.customsearch.*;
import com.google.api.services.customsearch.Customsearch.Builder;
import com.google.api.services.customsearch.model.*;

public class GUI{
	public JFrame mainFrame;
	public TextField keywordBox,targetDBox;
	public JButton buttonBrowse,buttonSearchImage,buttonDownload,buttonNext,buttonPrev,selectAll,selectNone;
	public JPanel iterateButtons;	
	public ArrayList<String> linkResult; 
	public int indexLink;
	public Customsearch myGSearchServ;
	public JSpinner startIndexSelector;
	private String apikey="AIzaSyBseCgmvsJ5L4QBzKYQUSAnxcQ2fWIEA_o",cx="006695016743302591096:zwejjbi9yay";
	private void setupSearchService() {
		
		myGSearchServ = new Customsearch.Builder(new NetHttpTransport(), new JacksonFactory(),new HttpRequestInitializer() {
			@Override
			public void initialize(HttpRequest httpRequest) throws IOException {
                try {
                    // set connect and read timeouts
                    httpRequest.setConnectTimeout(6000000);
                    httpRequest.setReadTimeout(6000000);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
			}
		}).setApplicationName("GImgDownloader").build();
	}
	public long searchIndex=1;
	public ArrayList<Result> totalResultList;
	public void exeQuery(String keyword) {
	    List<Result> resultList=null;
	    try {
	        Customsearch.Cse.List list=myGSearchServ.cse().list(keyword);
	        list.setKey(apikey);
	        list.setCx(cx);
	        list.setSearchType("image");
	        String fGet=filterGroup.getSelected();
	        list.setImgSize(fGet);
	        list.setStart(searchIndex);
	        
	        Search results=list.execute();
	        resultList=results.getItems();
	        
	        for (int i=0;i<resultList.size();i++) {
	        	Result res=resultList.get(i);
	        	totalResultList.add(res);
	        }
	        searchIndex+=resultList.size();
//	        startIndexSelector.setValue(searchIndex);
	    }
	    catch (  Exception e) {
	        e.printStackTrace();
	    }
	    
	}
	
	public FileNameGenerator fNameGenerator;
	public HashesManager hashMan;
	public PicBoxesManager picMan;
	public FilterGroup filterGroup;
	public GUI() throws Exception {
		totalResultList=new ArrayList<Result>();
		setupSearchService();
		mainFrame=new JFrame();
		mainFrame.setTitle("Google Image Downloader");
		mainFrame.setSize(2040, 1020);
		mainFrame.setLocation(30, 30);
		mainFrame.setLayout(null);

		
		fNameGenerator=new FileNameGenerator();
		keywordBox=new TextField();
		keywordBox.setSize(800, 40);
		keywordBox.setLocation(5, 5);
		keywordBox.setFont(new Font(null, 0, 18));
		mainFrame.add(keywordBox);
		
		filterGroup=new FilterGroup();
		filterGroup.setLocation(5,48);
		filterGroup.setSize(400,40);
		mainFrame.add(filterGroup);
		
		JLabel idl=new JLabel("Starting index: ");
		idl.setSize(150,40);
		idl.setLocation(450,48);
		idl.setFont(new Font(null, 0, 18));
		mainFrame.add(idl);
		
		startIndexSelector=new JSpinner(new SpinnerNumberModel(1, 1, null, 10));
		startIndexSelector.setLocation(590, 48);
		startIndexSelector.setSize(120, 48);
		startIndexSelector.setFont(new Font(null, 0, 18));
		mainFrame.add(startIndexSelector);

		buttonSearchImage = new JButton("Search Images");
		buttonSearchImage.setSize(200, 90);
		buttonSearchImage.setLocation(810, 5);
		mainFrame.add(buttonSearchImage);
		
		targetDBox=new TextField("targetDirectory");
		targetDBox.setSize(800, 40);
		targetDBox.setLocation(5, 100);
		targetDBox.setFont(new Font(null, 0, 18));
		targetDBox.setEditable(false);
		try{
			new File(targetDBox.getText()).mkdir();
		}catch (Exception e) {
			e.printStackTrace();
		}
		mainFrame.add(targetDBox);
		
		
		buttonBrowse = new JButton("Select Directory");
		buttonBrowse.setSize(200, 40);
		buttonBrowse.setLocation(810, 100);
		
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
		        
		        //System.out.println(f.getCurrentDirectory());
		        targetDBox.setEditable(true);
		        targetDBox.setText(f.getSelectedFile().toString());
		        targetDBox.setEditable(false);
		        if (fNameGenerator==null)
		        	fNameGenerator=new FileNameGenerator();
		        fNameGenerator.reset(targetDBox.getText());	
		        try {
					hashMan.reset(targetDBox.getText());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		mainFrame.add(buttonBrowse);
		
		
		iterateButtons=new JPanel();
		iterateButtons.setSize(1080,50);
		iterateButtons.setLocation(5,150);
		iterateButtons.setLayout(null);
		
		buttonPrev= new JButton("Previous");
		buttonPrev.setSize(100, 40);
		buttonPrev.setLocation(5, 5);
		iterateButtons.add(buttonPrev);
		
		buttonDownload= new JButton("Download/Save");
		buttonDownload.setSize(200, 40);
		buttonDownload.setLocation(110, 5);
		iterateButtons.add(buttonDownload);
		
		buttonNext= new JButton("Next");
		buttonNext.setSize(200, 40);
		buttonNext.setLocation(330, 5);
		iterateButtons.add(buttonNext);
		
		selectAll=new JButton("Select All");
		selectAll.setSize(200, 40);
		selectAll.setLocation(540, 5);
		iterateButtons.add(selectAll);
		
		
		selectNone=new JButton("Select None");
		selectNone.setSize(200, 40);
		selectNone.setLocation(750,5);
		iterateButtons.add(selectNone);
		
		
		
		mainFrame.add(iterateButtons);
		
		hashMan=new HashesManager(targetDBox.getText());
		
		picMan=new PicBoxesManager(hashMan,fNameGenerator);
		picMan.setSize(1600,700);
		picMan.setLocation(2, 210);
		mainFrame.add(picMan);
		
		
		mainFrame.setVisible(true);
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
		
		KeyListener keylistener=new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				int keycode=e.getKeyCode();
				if (keycode==KeyEvent.VK_LEFT) {
					buttonPrev.doClick();
				}
				if (keycode==KeyEvent.VK_RIGHT) {
					buttonNext.doClick();
				} 
				if (keycode==KeyEvent.VK_ENTER) {
					buttonDownload.doClick();					
				}
				if (keycode==KeyEvent.VK_A) {
					selectAll.doClick();					
				}
				if (keycode==KeyEvent.VK_N) {
					selectNone.doClick();					
				}
				if ((KeyEvent.VK_0<=keycode)&&(keycode<=KeyEvent.VK_9)) {
					try {
						picMan.keyselect(keycode-KeyEvent.VK_0);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		};
		buttonDownload.addKeyListener(keylistener);
		buttonNext.addKeyListener(keylistener);
		buttonPrev.addKeyListener(keylistener);
		selectAll.addKeyListener(keylistener);
		selectNone.addKeyListener(keylistener);

		keywordBox.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode()==KeyEvent.VK_ENTER) {
					buttonSearchImage.doClick();
					
				}
				
			}
		});
	}
	public int getStartIndex() {
		try {
			int x=(int)startIndexSelector.getValue();
			if (x<=0) x=1;
			else x=(x-(x%10))+1;
			return x;
		}catch (Exception e) {
			e.printStackTrace();
			return 1; 
		}
	}
	public int currentIndex;
	
	private void addActionToButtons() {

		buttonSearchImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				currentIndex=0;
				totalResultList.clear();
				searchIndex=getStartIndex();
				try{
					
					exeQuery(keywordBox.getText());
					picMan.showResults(totalResultList.subList(currentIndex, Math.min(currentIndex+10, totalResultList.size())));
					hashMan.reset(targetDBox.getText());
					fNameGenerator.reset(targetDBox.getText());
			
				}catch (Exception e) {
					JOptionPane.showMessageDialog(mainFrame, "can't find any result!");
					e.printStackTrace();
				}
				
			}	
		});
		buttonDownload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mainFrame.setEnabled(false);
				mainFrame.repaint();
				picMan.doDownload();
				mainFrame.setEnabled(true);
				mainFrame.repaint();
			}
		});
		buttonPrev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				goPrev();
			}
		});
		buttonNext.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				goNext();
			}
		});
		selectAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				picMan.trySetSelected(true);
			}
		});		
		selectNone.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				picMan.trySetSelected(false);
			}
		});		
	}
	public void goPrev() {
		if (currentIndex<10) {
			JOptionPane.showMessageDialog(mainFrame, "can't find any older result!\nYou may want to drop down the starting index.");
			return;
		}
		
		try {
			currentIndex-=10;
			picMan.showResults(totalResultList.subList(currentIndex, Math.min(currentIndex+10, totalResultList.size())));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			currentIndex+=10;
		}
	}
	public void goNext() {
		try {
			if (currentIndex+10>=totalResultList.size()) {
				exeQuery(keywordBox.getText());
			}
			currentIndex+=10;
			picMan.showResults(totalResultList.subList(currentIndex, Math.min(currentIndex+10, totalResultList.size())));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			currentIndex-=10;
			JOptionPane.showMessageDialog(mainFrame, "can't find any more result!");
		}
	}
}