import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

public class ImageSegmentation {
public static void main(String[] args){
	int K=5;
	try{
		System.out.println("Working Directory = " +
	              System.getProperty("user.dir"));
	  
		
	ArrayList<File> images = new ArrayList<File>();
	File image1=new File("image1.jpg");
	images.add(image1);
	File image2=new File("image2.jpg");
	images.add(image2);
	File image3=new File("image5.jpg");
	images.add(image3);
	performSegmentation(images,K);
	} catch(Exception e){
		System.out.println("Exception while performing image segmentation ");
		e.printStackTrace();
	}
}

private static double findDistance(int point1,int point2) {
	double distance=0;
	Color point1Color=new Color(point1);
	Color point2Color=new Color(point2);
    int redDistance=point1Color.getRed()-point2Color.getRed();
	int greenDistance=point1Color.getGreen()-point2Color.getGreen();
	int blueDistance=point1Color.getBlue()-point2Color.getBlue();
	distance=(redDistance*redDistance)+(greenDistance*greenDistance)+(blueDistance*blueDistance);
 return distance;
}

public static void performSegmentation(ArrayList<File> images, int K){
	for(File currentImage: images){
		try{
			BufferedImage processedImage=processImage(ImageIO.read(currentImage),K);
			if(new File("clusteredImages").exists() || new File("clusteredImages").mkdir()){
				File newImage=new File("clusteredImages/"+currentImage.getName());
				ImageIO.write(processedImage,"jpg",newImage);
			} else {
				System.out.println("Unable to create directory");
				throw new Exception("Unable to create directory");
			}
			
		} catch (Exception e){
			System.out.println("Exception while processing the image files ");
			e.printStackTrace();
		}
	}
}


public static BufferedImage processImage(BufferedImage currentImage,int K){
	int height=currentImage.getHeight();
	int width=currentImage.getWidth();
	int currentPixel=0;
	BufferedImage processingImage=new BufferedImage(width,height,currentImage.getType());
	Graphics2D graphicsObj=processingImage.createGraphics();
	graphicsObj.drawImage(currentImage,0,0,width,height,null);
	int[] pixelsRGB=new int[height*width];
    for(int x=0;x<width;x++)
    {
    	for(int y=0;y<height;y++)
    	{
    		pixelsRGB[currentPixel]=processingImage.getRGB(x, y);
    		currentPixel++;
    	}
    }
	performKmeansAlgorithm(K,pixelsRGB);
    currentPixel=0;
    for(int x=0;x<width;x++)
    {
    	for(int y=0;y<height;y++){
    		processingImage.setRGB(x, y, pixelsRGB[currentPixel]);
    		currentPixel++;
    	}
    	
    }
   return processingImage;
}

private static void performKmeansAlgorithm(int K,int[] pixelsRGB) {
	
	double distanceArray[][]=new double[pixelsRGB.length][2];
	boolean notConverged=true;
	for (int j=0;j<distanceArray.length;j++) 
	{
		distanceArray[j][1] = 0;
		distanceArray[j][0] = 1.797E308;}
	
	int kPnts[]=new int[K];
	Random rndm=new Random();
	for(int j=0;j<K;j++)
	{
	   kPnts[j]=pixelsRGB[rndm.nextInt(pixelsRGB.length)+1];
	}
	int tempKPoints[]=new int[K];
	int currentPixel=0;
	while(notConverged){
		for(int x=0;x<pixelsRGB.length;x++)
		{
			currentPixel=pixelsRGB[x];
			int kpnt;
			double pixelDistance=0;
			for(int k=0;k<K;k++)
			{
				 kpnt=kPnts[k];
				 pixelDistance=findDistance(currentPixel,kpnt);
				 if (pixelDistance<distanceArray[x][0])
				 {  distanceArray[x][1] = k;
					distanceArray[x][0] = pixelDistance;
					}
			}
		} 
	for(int i=0;i<K;i++) 
	{
	int sum= 0;
	int sumArray[]=new int[3];
	for(int j=0;j< pixelsRGB.length;j++) 
	{ if (distanceArray[j][1]==i)
	{ Color color=new Color(pixelsRGB[j]);
	sumArray[0]+=color.getRed();
	sumArray[1]+=color.getBlue();
	sumArray[2]+=color.getGreen();
				sum++;
				}
			}
	tempKPoints[i]=kPnts[i];
	if(sum!=0)
	{
		int red=sumArray[0]/sum;
		int blue=sumArray[1]/sum;
		int green=sumArray[2]/sum;
		Color color = new Color(red,blue,green);
		kPnts[i]=color.getRGB();
	}
	}
		int z = 0;
		for(int n=0;n<K;n++) 
		{
			if(kPnts[n]==tempKPoints[n]) 
			{
				z=z+1;
			}
		}
		if (z==K) {
			notConverged=false;
		}
	}
	for (int m=0;m<pixelsRGB.length;m++) {
		for (int n=0;n<K;n++) {
			if (distanceArray[m][1] == n) {
				pixelsRGB[m] = kPnts[n];
				break;
			}
		}
	}
}
}
