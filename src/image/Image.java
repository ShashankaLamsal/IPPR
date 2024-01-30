package image;

/*The Image class
 * --> Read/Save/display Images
 * --> Extract pixel arrays, etc. 
 * --> Based on BufferedArray 
 ----------------------------
 Nischal Regmi
 Associate Professor, EEC
 */


import java.awt.Color;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.util.Arrays;


public class Image {
	BufferedImage image;
	
	//*********************************************************************
	//************** Constructors *****************************************
	public Image(String filename) {
		 try{
			 this.image = ImageIO.read(new File(filename));
		    }catch(IOException e){
		      System.out.println("Error: "+e);
		  }
	}
	
	public Image(BufferedImage img) {
		this.image = img;
	}
	
	public Image(int[][] A) {//gray-scale image from array
		image = new BufferedImage(A.length,A[0].length,
								BufferedImage.TYPE_BYTE_GRAY);
		for(int x=0;x<image.getWidth(); x++)
			for(int y=0;y<image.getHeight();y++) {
				Color newColor = new Color(A[x][y],A[x][y],A[x][y]);
	    		image.setRGB(x, y,newColor.getRGB());
			}
	}		
	
	
	
	//*******************************************************************
	//************** Basic IO and other functions ***********************
	
	BufferedImage getImage() {
		return this.image;
	}
		
	void saveToFile(String filename,String extension) {
		 try{
		      ImageIO.write(image, "jpg", new File(filename+"."+extension));
		    }catch(IOException e){
		      System.out.println("Error: "+e);
		    }
	}
	
	public static void saveToFile(int[][] f, String filename, String extension) {
		Image im = new Image(f);
		im.saveToFile(filename, extension);
	}
	
	void display(String title) {
		ImageIcon icon=new ImageIcon(this.image);
		JFrame frame=new JFrame(title);
        frame.setLayout(new FlowLayout());
        frame.setSize(this.image.getWidth(),this.image.getHeight());
        JLabel lbl=new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
	}
	
	public static void display(int[][] f,String title) {
		//clip intensities to the range [0,255]
		for(int x=0;x<f.length;x++)
			for(int y=0;y<f[0].length;y++) {
				if(f[x][y]>255)
					f[x][y]=255;
				if(f[x][y]<0)
					f[x][y]=0;
				}

		Image img = new Image(f);
		img.display(title);
	}
	
	public static void display(double[][] f,String title){
		//clip intensities to the range [0,255]
		int[][] F = new int[f.length][f[0].length];
		for(int x=0;x<f.length;x++)
			for(int y=0;y<f[0].length;y++) {
				F[x][y] = (int) Math.round(f[x][y]);
				if(F[x][y]>255)
							F[x][y]=255;
				if(F[x][y]<0)
							F[x][y]=0;
				}
		Image img = new Image(F);
		img.display(title);
                
                

	}
	
	
	int[][] getPixelArray(){
		int[][] A = new int[image.getWidth()][image.getHeight()];
		for(int x=0;x<image.getWidth();x++)
			for(int y=0;y<image.getHeight();y++) {
				Color c = new Color(image.getRGB(x, y));
				A[x][y] = (int) (c.getRed()+c.getGreen()+c.getBlue())/3;			}
		return A;	
	}
	// creating binary image from a grayscale image with threshold
		public static int[][] createBinaryImage(int[][] grayscaleArray, int threshold) {
			int height = grayscaleArray.length;
			int width = grayscaleArray[0].length;
			int[][] binaryArray = new int[height][width];

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					//if pixel is greater than threshold i.e. 128, set it to 255, else to 0
					binaryArray[y][x] = (binaryArray[y][x] > threshold) ? 255 : 0;

				}
			}
			
			return binaryArray;
		}

   
	
       // Apply log to focus in dark parts
        public static int [][] logTrans( int[][] f, int c)
        {
            int min=255,max=0;
            int[][] g= new int[f.length][f[0].length];
            
            
            for( int x=0; x<f.length;x++)
                 for(int y=0;y<f[0].length;y++)
                    {
                        g[x][y]= c*(int) Math.log(f[x][y]+1);
                    }
            
            for( int x=0; x<f.length;x++)
                    for(int y=0;y<f[0].length;y++)
                    {
                        //f[x][y]= (int) Math.log(f[x][y]);
                        if(g[x][y]<min)
                            min=f[x][y];
                        if ((g[x][y]>max))
                            max=g[x][y];
                    }
            
            for( int x=0; x<f.length;x++)
                for(int y=0;y<f[0].length;y++)
                        g[x][y]= (int)(((float) (g[x][y]-min)/(max-min))*255);
                return g;
        }
        
        // to apply histogram Question 2
        public static int [][] histogram( int[][] f){
            int l= 256;
            int [] n = new int [l];	//count of each intensity
            Arrays.fill(n, 0);
            
            int [] s = new int [l];
            
            float [] p = new float [l]; //percentage of given intensity index in the image
            
            int [][] g = new int [f.length][f[0].length];
            
            
            for( int x=0; x<f.length;x++)
                    for(int y=0;y<f[0].length;y++)
                    {
                        n[f[x][y]]++;
                    }
            
            for(int i=0;i<l;i++){
            	  p[i]=(float) ((double) n[i]/(f.length*f[0].length));
            }
            
            //transformation function for histogram equalization
            for(int j=0;j<l;j++){
            	
                float sum=0;
            
                for(int i=0;i<=j;i++){
                	
                    sum= p[i]+ sum;
                    
                   }
                
                 s[j] = (int)((l- 1) * sum ); 
            } 
            
            
            
            
            for( int x=0; x<f.length;x++)
                    for(int y=0;y<f[0].length;y++)
                    {
                        g[x][y]=s[f[x][y]];
                    }
                        
             return g;       
        }
    
        public static int [][] correlation( int[][] f){
        
        	//kernel creation
        	
        	//laplacian
        	//int[][] w= {{0,1,0},{1,-4,1},{0,1,0}};
        	
        	
        	int[][] w= {{-1,0,1},{-2,0,2},{-1,0,1}}; 
        	
        	
        	int a=(w.length-1)/2;
        	int b=(w.length-1)/2;
        	
        	//creating padded version of f image
        	int[][] f_padded= new int[2*a+f.length][2*b+f[0].length];
        	int[][] F=new int[f.length][f[0].length];
        	
        	for( int x=0; x<f.length;x++)
                for(int y=0;y<f[0].length;y++)
                {
                    f_padded[a+x][b+y]=f[x][y];
                }
        	for( int x=0; x<F.length;x++)
                for(int y=0;y<F[0].length;y++)
	                for(int s=-a;s<=a;s++)
	                	for(int t=-b;t<=b;t++)
	                	{
	                		int v=w[s+a][t+b];
	                		F[x][y]=F[x][y]+v*f_padded[(a+x)-s][(b+y)-t];
	                		
	                	}
        	
        	
			return F;
        }
        public static int [][] sobel( int[][] f){
            
        	//kernel creation
        	
        	//laplacian
        	//int[][] w= {{0,1,0},{1,-4,1},{0,1,0}};
        	
        	//sobel vertical
        	int[][] w= {{-1,-2,-1},{0,0,0},{1,2,1}}; 
        	
        	//sobel horizontal
        	int[][] w1= {{-1,0,1},{-2,0,2},{-1,0,1}}; 
        	
        	
        	int a=(w.length-1)/2;
        	int b=(w.length-1)/2;
        	
        	//creating padded version of f image
        	int[][] f_padded= new int[2*a+f.length][2*b+f[0].length];
        	int[][] F=new int[f.length][f[0].length];
        	
        	for( int x=0; x<f.length;x++)
                for(int y=0;y<f[0].length;y++)
                {
                    f_padded[a+x][b+y]=f[x][y];
                }
        	for( int x=0; x<F.length;x++)
                for(int y=0;y<F[0].length;y++)
	                for(int s=-a;s<=a;s++)
	                	for(int t=-b;t<=b;t++)
	                	{
	                		int v=w[s+a][t+b];
	                		F[x][y]=F[x][y]+v*f_padded[(a+x)-s][(b+y)-t];
	                		
	                	}
        	int a1=(w1.length-1)/2;
        	int b1=(w1.length-1)/2;
        	
        	//creating padded version of f image
        	int[][] f1_padded= new int[2*a+f.length][2*b+f[0].length];
        	int[][] F1=new int[f.length][f[0].length];
        	
        	for( int x=0; x<f.length;x++)
                for(int y=0;y<f[0].length;y++)
                {
                    f1_padded[a1+x][b1+y]=f[x][y];
                }
        	for( int x=0; x<F1.length;x++)
                for(int y=0;y<F1[0].length;y++)
	                for(int s=-a;s<=a;s++)
	                	for(int t=-b;t<=b;t++)
	                	{
	                		int v=w[s+a][t+b];
	                		F1[x][y]=F1[x][y]+v*f1_padded[(a1+x)-s][(b1+y)-t];
	                		
	                	}
        	
        	int[][] F2=new int[f.length][f[0].length];
        	for( int x=0; x<F1.length;x++)
                for(int y=0;y<F1[0].length;y++)
                {
                	F2[x][y]=F[x][y]+F1[x][y];
                	
                	
                }
        	
			return F2;
        }
        
        public static int [][] meanFilter( int[][] f)
        {
        	int size=9;//mean sample size
        	int[][] w= new int[size][size];
        	int i,j;
        	
        	//creating equal weight values for all neighbour pixels
        	for(i=0;i<size;i++) {
    			
    			for(j=0;j<size;j++) {
    				w[i][j]=1;
    			}
    		}
        	
        	//creating limit values for each
        	int a=(w.length-1)/2;
        	int b=(w.length-1)/2;
        	
        	//creating padded version of f image
        	int[][] f_padded= new int[2*a+f.length][2*b+f[0].length];
        	int[][] F=new int[f.length][f[0].length];
        	
        	for( int x=0; x<f.length;x++)
                for(int y=0;y<f[0].length;y++)
                {
                    f_padded[a+x][b+y]=f[x][y];
                }
        	
        	//actual mean filter
        	for( int x=0; x<F.length;x++)
                for(int y=0;y<F[0].length;y++)
                {
	                for(int s=-a;s<=a;s++)
	                {
	                	for(int t=-b;t<=b;t++)
	                	{
	                		int v=w[s+a][t+b];
	                		//creating the sum of all values within the mean sample
	                		F[x][y]=F[x][y]+v*f_padded[(a+x)-s][(b+y)-t];
	                		
	                	}
	                }
	                //finding the mean value for each pixel
	                F[x][y]=F[x][y]/(size*size);
                }
        	return F;
        }
        
        
        public static int [][] medianFilter( int[][] f)
        {
        	int w =3;	//median size
        	int i;
        	
        	int a=(w-1)/2;
        	int b=(w-1)/2;
        	
        	//creating padded version of f image
        	int[][] f_padded= new int[2*a+f.length][2*b+f[0].length];
        	
        	int[][] F=new int[f.length][f[0].length];
        	
        	
        	
        	for( int x=0; x<f.length;x++)
                for(int y=0;y<f[0].length;y++)
                {
                    f_padded[a+x][b+y]=f[x][y];
                }
        	//creating an array to store the median samples for each pixel
        	int[] sortArray= new int[w*w];
        	
        	//actual median filter
        	for( int x=0; x<F.length;x++)
                for(int y=0;y<F[0].length;y++)
                {
                	i=0;
	                for(int s=-a;s<=a;s++)
	                {
	                	for(int t=-b;t<=b;t++)
	                	{
	                		//storing the median neighbour values to an array to sort
	                		sortArray[i]=f_padded[(a+x)-s][(b+y)-t];   
	                		i++;
	                		
	                	}
	                }
	              //using sort function provided by java to sort the median sample values stored in the array
	                Arrays.sort(sortArray, 0, 8);
	                //assigning the mid value of the sorted array that provides the median
	                F[x][y]=sortArray[(w*w+1)/2];
	                
                }
        	return F;
        }
        
        public static double calculateSNR(int[][] original, int[][] restored) {
            double num= 0.0;
            double den = 0.0;
            double snr;

            int M = original.length;
            int N = original[0].length;

            for (int x = 0; x < M; x++) {
                for (int y = 0; y < N; y++) {
                    num += Math.pow(restored[x][y], 2);
                    den += Math.pow(restored[x][y] - original[x][y], 2);
                }
            }
            snr= num/den;
            return snr;
        }
        
     
                
        public static void main(String[] args){
		Image img = new Image("C:/Users/shash/eclipse-workspace/ippr/hands.jpg");
		int[][] f = img.getPixelArray();
		int[][] outputImage;
		Image.display(f, "Original");
		Image.saveToFile(f,"sameAsOriginal","png");
                
		outputImage=Image.logTrans(f, 1000);
        Image.display(outputImage, "After log");
        Image.saveToFile(outputImage,"LogTransform","png");
                
        outputImage=Image.histogram(f);
        Image.display(outputImage, "After histogram");
        Image.saveToFile(outputImage,"histo","png");
        outputImage=Image.correlation(f);
        Image.display(outputImage, "After correlation");
        Image.saveToFile(outputImage,"Correlation","png");
        
		outputImage=Image.sobel(f);
		Image.display(outputImage, "After sobel masking");
        Image.saveToFile(outputImage,"SobelMask","png");
        
        
		
        Image Noiseimg = new Image("C:/Users/shash/eclipse-workspace/ippr/ronaldo_N.jpg");
		int[][] f1 = Noiseimg.getPixelArray();
		int[][] restoredImage = Image.meanFilter(f1);
        Image.display(restoredImage, "After mean");
        Image.saveToFile(outputImage,"MeanFilter","png");
        
        restoredImage = Image.medianFilter(f1);
        Image.display(restoredImage, "After median");
        Image.saveToFile(outputImage,"MedianFilter","png");
        
        
        
        
        double snrValue = calculateSNR(f1, restoredImage);
        System.out.println("Signal-to-Noise Ratio (SNR): " + snrValue);
        
       
	}
}
