import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;

import javax.imageio.ImageIO;
import javax.print.DocFlavor.STRING;
import javax.swing.*;

import com.google.api.services.customsearch.model.Result;

public class PictureBox extends JPanel{
	private JLabel picture;
	private JLabel desc;
	private HashesManager hashMan;
	private JCheckBox selectBox;
	private String fType,md5,urlString="";
	public PictureBox(HashesManager hMan) {
		super();
		hashMan=hMan;
		setSize(350,400);
		picture=new JLabel();
		desc=new JLabel();
		picture.setLocation(3,3);
		picture.setPreferredSize(new Dimension(300,300));
		desc.setLocation(3,303);
		desc.setSize(300,30);
		
		selectBox = new JCheckBox("Download", false);
		selectBox.setLocation(3, 350);
		selectBox.setPreferredSize(new Dimension(300,30));
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(picture);
		this.add(desc);
		this.add(selectBox);
		fType="";
		
		addMouseListener(mouseListener);
		picture.addMouseListener(mouseListener);
		reset();
	}
	
	public void doRepaint() {
		picture.repaint();
		desc.repaint();
		selectBox.repaint();
		super.repaint();
		
	}
	private void clearcontent() {
		picture.setIcon(null);
		picture.revalidate();
		desc.setText("");
		urlString="";
		
		selectBox.setSelected(false);
		selectBox.setEnabled(false);
		doRepaint();
	}
	public String[] types={".PNG",".png",".jpg",".JPG",".JPEG",".jpeg",".tiff",".TIFF",".tif",".TIF"};
	public void showResult(Result res) {
		if (res==null) {
			clearcontent();
			return;
		}
		try {
			String thumburl=res.getImage().getThumbnailLink();
			urlString=res.getLink();
			BufferedImage image=ImageIO.read(new URL(thumburl));
			
			int u=-1;
			for (String t:types) {
				int v=urlString.lastIndexOf(t);
				if (u<v) {
					u=v;
					fType=t;
				}
			}
				
			
			md5=hashMan.imageToMD5(image, fType);
			picture.setIcon(new ImageIcon(image));
			desc.setText(Integer.toString(res.getImage().getWidth())+" x "+Integer.toString(res.getImage().getHeight()));
			if (hashMan.checkInlist(this.md5)) {
				selectBox.setSelected(true);
				selectBox.setEnabled(false);
				selectBox.setText("Downloaded!");
			}else {
				selectBox.setSelected(false);
				selectBox.setText("Download");
				selectBox.setEnabled(true);
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		repaint();
	}
	
	public void reset() {
		clearcontent();
	}
	
	private MouseListener mouseListener =new MouseListener() {
		
		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseClicked(MouseEvent arg0) {
			if (selectBox.isEnabled())
				selectBox.setSelected(!selectBox.isSelected());
			
		}
	};
	public void downloadFile(String fName) {  
		if ((urlString.length()<=2)||(hashMan.checkInlist(md5)==true)||(selectBox.isSelected()==false)||(selectBox.isEnabled()==false)){
			return;
		}
		try {
        	String destination=fName+fType;
        	
            URL website = new URL(urlString);
            HttpURLConnection httpcon = (HttpURLConnection) website.openConnection();
            httpcon.addRequestProperty("User-Agent", "Mozilla/4.76");
            ReadableByteChannel rbc;
            rbc = Channels.newChannel(httpcon.getInputStream());
            FileOutputStream fos = new FileOutputStream(destination);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
            System.out.println("File downloaded: "+destination);
            hashMan.addToList(md5);
            
            if (hashMan.checkInlist(this.md5)) {
				selectBox.setSelected(true);
				selectBox.setEnabled(false);
				selectBox.setText("Downloaded!");
			}else {
				selectBox.setSelected(false);
				selectBox.setText("Download");
				selectBox.setEnabled(true);
			}
        } catch (IOException e) {
        	System.out.println("Link unable to downloaded: "+urlString);
            e.printStackTrace();
        }
		
    }
	public void  trySetSelected(boolean b) {
		if (selectBox.isEnabled()==false) return;
		selectBox.setSelected(b);
	}
	public boolean isEnabled() {
		return selectBox.isEnabled();
	}
	public boolean isSelected() {
		return selectBox.isSelected();
	}
}
	