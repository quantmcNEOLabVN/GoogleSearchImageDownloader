public class GImgDownloader {		
	public static void main(String args[]) throws Exception{
		new Thread(new Runnable() {
		
			@Override
			public void run() {
				try {
					GUI gui=new GUI();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}).run();
	}
	
	

}
