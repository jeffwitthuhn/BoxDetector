//CSCI 440 Project 2
//Jeff Witthuhn
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Math.round;
import static java.lang.Math.abs;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.event.WindowEvent;
import javax.imageio.*;
import java.io.*;

 

public class Project2 extends Frame{

	public static void main(String[] args){
		new Project2();
	}
	 public int imagewidth=1024;
	 public int imageheight=768;
	 public int width=imagewidth;
	 public int height=imageheight; 
	 public int margin=20; 
	 Project2(){
	 	super ("Project 2 BOX DETECTOR!");
	 	addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){System.exit(0);}});
	 		pack();
	 		Insets boarder = getInsets();
	 		setSize (width+boarder.right+boarder.left,height+boarder.top+boarder.bottom);
			add("Center", new incenterGraphics(this, boarder, margin));
			show();
		}
 }




 class incenterGraphics extends Canvas implements Runnable{
 	Insets boarder; 
 	Dimension d;
 	int margin;
 	Graphics gImage;
    Thread thr = new Thread(this);
    Scanner scanner;
    Frame window; 
 	boolean ready;
 	BufferedImage image,image2;
 	ArrayList<int[][]> saves;
 	int clickCount;
 	int[][] array2D; 
 	String filename;
 	public void update(Graphics g){
 		paint(g);

 	}
    public void run(){
    		try{ 
    		 	for (;;){
		    		if(!ready){
	 		  			try{
	 		  		 		  			getBMPImage(filename);
	 		  		
	 		  		 		  		}
	 		  		 		  		catch(IOException e){};
	 		  			image2 = new BufferedImage(image.getWidth(),image.getHeight(),  image.getType());
	 		  		 }
		 		   	ready=true;
		 		   	Scanner scanner = new Scanner(System.in);
		 		   	String command;
		 		   	while (ready){
						gImage = image2.getGraphics();
						drawArrayImage();
			 		  	repaint();
						menu();
						command=scanner.nextLine();
					
					    switch (command){
					    	case "d": saves.add(copyArray2D(array2D));darkenArrayImage();
					    		break;
					    	case "l":saves.add(copyArray2D(array2D));lightenArrayImage();
					    		break;
					    	case "sa":saves.add(copyArray2D(array2D));smoothAve(false);
					    		break;	
					    	case "sg":saves.add(copyArray2D(array2D));smoothAve(true);
					    		break;					    	
					    	case "sm":saves.add(copyArray2D(array2D));smoothMed();
					    		break;
					    	case "sh":saves.add(copyArray2D(array2D));sharpenHistoEq();
					    		break;
					    	case "sl":saves.add(copyArray2D(array2D));sharpenLaplacian(1);
					    		break;
					    	case "sl2":saves.add(copyArray2D(array2D));sharpenLaplacian(2f);
					    		break;
					    	case "save": save();
					    		break;
					    	case "ek":saves.add(copyArray2D(array2D));detectEdgeKirsch(5);
					    		break;
					    	case "el":saves.add(copyArray2D(array2D));detectEdgeLaplacian(5,1);
					    		break;
					    	case "el2":saves.add(copyArray2D(array2D));detectEdgeLaplacian(5,2);
					    		break;
					    	case "m": saves.add(copyArray2D(array2D));increaseContrast(100,10);
					    		break;
					    	case "n": saves.add(copyArray2D(array2D));decreaseContrast(100,10);
					    		break;
					    	case "t":saves.add(copyArray2D(array2D));thin();
					    		break;
					    	case "rn":saves.add(copyArray2D(array2D));ReduceNoise(2);
					    		break;
					    	case "z":undo();
					    		break;
					    	case "o": saves.add(copyArray2D(array2D));revertToOriginal();
					    		break;

					    	case "0": exit();
					    		break;

					    }
	 		  		}
    		 	
    		 	    Thread.sleep (2);
    		 	}
			}
		
			catch (InterruptedException e){}
		}
 	incenterGraphics(Frame _window, Insets _boarder, int _margin){
 		boarder=_boarder;
 		window=_window;
 		margin=_margin;
 		ready=false;
 		clickCount=0; 
 		filename="im1-c.bmp";
 		scanner = new Scanner(System.in);
 		saves = new ArrayList<int[][]>();

 		thr.start();

 		 addMouseListener(new MouseAdapter(){
 		  public void mousePressed(MouseEvent evt){
 		  
 		  		clickCount++;
			}});
 		


 	}
int [][] copyArray2D(int [][] array){
	int[][] temp;
    temp = new int[image.getWidth()][image.getHeight()]; //*

	for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) //*
	    {
	    for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) //*
	        {
	        	temp[xPixel][yPixel] = array[xPixel][yPixel]; 
	        }
	    }
	 return temp;
}
public void menu(){
    System.out.println("Darken:d  Lighten:l  Smooth:sm/sa/sg Sharpen:sh/sl");
    System.out.println("EdgeDetect:ek/el Thin:t Undo:z contrast:n/m Revert:o");
    System.out.println("ReduceNoise:rn OriginalImage:o Save:s Exit:0");

  

}
public void getBMPImage(String BMPFileName) throws IOException {
	//stolen from http://stackoverflow.com/questions/17015340/how-to-read-a-bmp-file-identify-which-pixels-are-black-in-java

	  	image = ImageIO.read(getClass().getResource(BMPFileName));
	  	System.out.println("reading image");

	    array2D = new int[image.getWidth()][image.getHeight()]; //*

	    for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) //*
	    {
	        for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) //*
	        {
	            int color = image.getRGB(xPixel, yPixel); //*
	            array2D[xPixel][yPixel] = color;

	        }
	    }
}
public void undo(){
	if(saves.size()>0){
		array2D=saves.get(saves.size()-1);
		saves.remove(saves.size()-1);
	}

}

public void revertToOriginal(){
	try{
	 		  		 		  			getBMPImage(filename);
	 		  		
	 		  		 		  		}
	 		  		 		  		catch(IOException e){};
	 		  			image2 = new BufferedImage(image.getWidth(),image.getHeight(),  image.getType());
}

public Color getBW(int val){
	if(val > 255)
		val=255;
	else if(val<0)
		val=0;
	return new Color(val, val, val);
}
public void increaseContrast(int center, int degree){
	Color color;
	int range =5;
	for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) //*
		    {
		        for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) //*
		        {
		        	color= new Color(array2D[xPixel][yPixel]);
		        	if (color.getGreen()<(center-5)){
		        		color=getBW(color.getGreen()-degree);

		   				array2D[xPixel][yPixel]=color.getRGB();

		        	}
		        	else if(color.getGreen()>(center+5)){
		        		color=getBW(color.getGreen()+degree);
		        		array2D[xPixel][yPixel]=color.getRGB();

		        	}

		        }
		    }

}
public void decreaseContrast(int center, int degree){
	Color color;
	int range =5;
	for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) //*
		    {
		        for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) //*
		        {
		        	color= new Color(array2D[xPixel][yPixel]);
		        	if (color.getGreen()<(center-5)){
		        		color=getBW(color.getGreen()+degree);

		   				array2D[xPixel][yPixel]=color.getRGB();

		        	}
		        	else if(color.getGreen()>(center+5)){
		        		color=getBW(color.getGreen()-degree);
		        		array2D[xPixel][yPixel]=color.getRGB();

		        	}

		        }
		    }

}
public int getArrayColor(int x, int y){
	return new Color(array2D[x][y]).getGreen();
}
public void detectEdgeKirsch(int delta){
	int[][] temp;
    temp = new int[image.getWidth()][image.getHeight()]; //*

	for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) //*
	    {
	    for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) //*
	        {
	        	temp[xPixel][yPixel] = getBW(getEdgeElement(xPixel,yPixel,delta)).getRGB(); 
	        }
	    }
	array2D=copyArray2D(temp);
}

public int getEdgeElement(int x, int y, int delta){
	int [] [] box = getBox(x,y);
	int diff1, diff2, diff3, diff4;
	int sum1, sum2; 
	sum1=box[0][0]+box[0][1]+box[1][0];
	sum2=box[2][1]+box[2][2]+box[1][2];
	diff1=sum1-sum2;

	sum1=box[1][0]+box[2][1]+box[2][0];
	sum2=box[1][2]+box[0][2]+box[0][1];
	diff2=sum1-sum2;

	sum1=box[0][0]+box[0][1]+box[0][2];
	sum2=box[2][1]+box[2][2]+box[2][0];
	diff3=sum1-sum2;

	sum1=box[0][0]+box[2][0]+box[1][0];
	sum2=box[0][2]+box[2][2]+box[1][2];
	diff4=sum1-sum2;

	if(abs(diff1)<delta||abs(diff2)<delta||abs(diff3)<delta||abs(diff4)<delta)
		return 255;
	else return 0;
}

public void ReduceNoise(int threshhold){
	int[][] box= new int[3][3];
	int newVal;
	Color color;
	
	for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) //*
		{
		for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) //*
			        {
						box=getBox(xPixel, yPixel);
						if(countNeighbors(box)<threshhold){
							newVal=255;
							color=getBW(newVal);
							array2D[xPixel][yPixel]=color.getRGB();
						}

			        }
			    }

}
public int countNeighbors(int [][] box){
	int sum=0;
	for(int i=0; i<3; i++)
		for(int j=0; j<3; j++)
			if(box[i][j]==0)
				sum++;
	return sum-1;
}

public void detectEdgeLaplacian(int delta,int mode){
	double sum;
	double[][] filter = getLaplacianFilter(mode);
	int[][] box= new int[3][3];
	int newVal;
	Color color;
	
	for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) //*
		{
		for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) //*
			        {
						box=getBox(xPixel, yPixel);
						if(abs(convolution(box,filter))<delta)
							newVal=0;
						else 
							newVal=255;
						color=getBW(newVal);
						array2D[xPixel][yPixel]=color.getRGB();

			        }
			    }

}
public void smoothMed(){
	Color color;
	int [][] temp= copyArray2D(array2D);
	int[][] box= new int[3][3];
	int median;

		for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) //*
			    {
			        for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) //*
			        {
						box=getBox(xPixel, yPixel);
						median=calcMedianBox(box);
						color=getBW(median);
						array2D[xPixel][yPixel]=color.getRGB();

			        }
			    }
}
public double[][] getLaplacianFilter(int mode){
	double[][] filter = new double[3][3];
	double sum;
	if(mode==1){
		sum=2*4+3*4+20;
		filter[0][0] = (double)(-2)/sum;		
		filter[0][1] = (double)(-3)/sum;
		filter[0][2] = (double)(-2)/sum;
		filter[1][0] = (double)(-3)/sum;
		filter[1][1] = (double)(20)/sum;
		filter[1][2] = (double)(-3)/sum;
		filter[2][0] = (double)(-2)/sum;
		filter[2][1] = (double)(-3)/sum;
		filter[2][2] = (double)(-2)/sum;
	}	
	if(mode==2){
		sum=16;
		filter[0][0] = (double)(-1)/sum;		
		filter[0][1] = (double)(-1)/sum;
		filter[0][2] = (double)(-1)/sum;
		filter[1][0] = (double)(-1)/sum;
		filter[1][1] = (double)(8)/sum;
		filter[1][2] = (double)(-1)/sum;
		filter[2][0] = (double)(-1)/sum;
		filter[2][1] = (double)(-1)/sum;
		filter[2][2] = (double)(-1)/sum;
	}
	return filter;
}
public void sharpenLaplacian(int mode){
	double sum;
	double[][] filter = getLaplacianFilter(mode);
	int[][] box= new int[3][3];
	int newVal;
	Color color;
	
	for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) //*
		{
		for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) //*
			        {
						box=getBox(xPixel, yPixel);
						newVal=convolution(box,filter);
						color=getBW(newVal);
						array2D[xPixel][yPixel]=color.getRGB();

			        }
			    }

}
public int convolution (int [][] box, double [][] filter){
	int sum=0;
	for(int i=0; i<3; i++)
		for(int j=0; j<3; j++)
			sum+=(int)((double)box[i][j]*filter[i][j]);

		return sum;

}
public void sharpenHistoEq(){
	int[] H = new int [256];
		for(int i=0; i<256; i++){
			H[i]=0;
		}
		for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) //*
		  {
			 for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) //*
			 {
			 	H[getArrayColor(xPixel,yPixel)]++; //fill histogram
			 }

		}
		printHisto(H);
	for(int i=1; i<256; i++)
		H[i]=H[i]+H[i-1];//cumulative histogram
	printHisto(H);

	for(int i=0; i<256; i++)
		H[i]=(int)((double)H[i]*(((double)255)/((double)(image.getWidth()*image.getHeight())))); //normalize
	
	printHisto(H);

	for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) //*
		  {
			 for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) //*
			 {
				array2D[xPixel][yPixel]=getBW(H[getArrayColor(xPixel,yPixel)]).getRGB(); //fill histogram
			 }

		}
}
public void printHisto(int [] hist){
	for (int i=0; i<256; i++)
		System.out.print(hist[i]+" ");
	System.out.println("\n\n");

}
public void smoothAve(boolean gaussian){
	Color color;
	int [][] temp= copyArray2D(array2D);
	int[][] box= new int[3][3];
	int average;

		for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) //*
			    {
			        for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) //*
			        {
						box=getBox(xPixel, yPixel);
						if(gaussian)
							average=getBoxAveGaussianWeighted(box);
						else 
							average=getBoxAve(box);
						color=getBW(average);
						array2D[xPixel][yPixel]=color.getRGB();

			        }
			    }
}

//approximate median algorithm https://web.cs.wpi.edu/~hofri/medsel.pdf
public void Tripplet_Adjust(int [] A, int i, int step){
	int j=i+step, k=i+2*step;
	if(A[i]<A[j]){
		if(A[k]<A[i])
			swap(i,j,A);
		else if(A[k]<A[j])
			swap(j,k,A);
	}
	else {
		if(A[i]<A[k])
			swap(i,j,A);
		else if(A[k]>A[j])
			swap(j,k,A);
	}
}
public int approxMedian(int A[], int r){
	int step=1, size=9;
	int i;
	for(int j=0; j<2; j++){
		i=(step-1)/2;
		while(i<size){
			Tripplet_Adjust(A, i, step);
			i=i+(3*step);
		}
		step=3*step;
	}
	return A[(size-1)/2];
}
void swap(int i, int j, int[] arr) {
	  int t = arr[i];
	  arr[i] = arr[j];
	  arr[j] = t;
}
public int calcMedianBox(int [][] box){
	//int[][] temp= new int[3][3];
	int[] A=new int[9];
	int counter=5;
	/*
	for(int i=0; i<3; i++)
		for(int j=0; j<3; j++){
			temp[i][j]=counter++;
		}
		*/
	counter =0;
	for(int i=0; i<3; i++)
		for(int j=0; j<3; j++){
			A[counter++]=box[i][j];
		}
	/*
	for(int i=0; i<9; i++)
		System.out.print(A[i]+" ");
	System.out.print("\n");
	System.out.print("median is:"+ approxMedian(A,2)+"\n");
	*/


	return approxMedian(A,2);

}
public void printBox(int[][] box){
	for(int i=0; i<3; i++){
		for(int j=0; j<3; j++)
			System.out.print(box[i][j]+" ");
		 System.out.print("\n");

		}
				
			
}
public int getBoxAve(int [][] box){
	int ave=0;
	int counter=0;
	for(int i=0; i<3; i++)
		for(int j=0; j<3; j++)
			if(box[i][j]>=0){
				ave+= box[i][j];
				counter++;
			}
	return	ave/=counter; //(counter>0? ave/=counter:ave);
}
public int getBoxAveGaussianWeighted(int [][] box){
	int ave=0;
	int counter=0;
	ave += box[0][0];
	ave += 3*box[0][1];
	ave += box[0][2];
	ave += 3*box[1][0];
	ave += 6*box[1][1];
	ave += 3*box[1][2];
	ave += box[2][0];
	ave += 3*box[2][1];
	ave += box[2][2];
	return	ave/=22; //(counter>0? ave/=counter:ave);
}
public int [][] getBox(int x, int y){
	int [][] box= new int [3][3];
	for(int i=0; i<3; i++)
		for(int j=0; j<3; j++)
			box[i][j]=-1;
	if(x-1>=0)
		{
			if(y-1>=0)
				box[0][0]= getArrayColor(x-1, y-1);//array2D[x-1][y-1];
			box[0][1]= getArrayColor(x-1, y);//array2D[x-1][y];
			if(y+1<image.getHeight())
				box[0][2]= getArrayColor(x-1, y+1);//array2D[x-1][y+1];
			}
	if(y-1>=0)		
		box[1][0]= getArrayColor(x, y-1);//array2D[x][y-1];
	box[1][1]= getArrayColor(x, y);//array2D[x][y];
	if(y+1<image.getHeight())
		box[1][2]= getArrayColor(x, y+1);//array2D[x][y+1];
	if(x+1<image.getWidth()){
		if(y-1>=0)
			box[2][0]= getArrayColor(x+1, y-1);//array2D[x+1][y-1];
		box[2][1]= getArrayColor(x+1, y);//array2D[x+1][y];
		if(y+1<image.getHeight())
			box[2][2]= getArrayColor(x+1, y+1);//array2D[x+1][y+1];
	}
	return box;
}
public void darkenArrayImage(){
	Color color;
	for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) //*
		    {
		        for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) //*
		        {
		        	color= new Color(array2D[xPixel][yPixel]);
		        	color=color.darker();
					array2D[xPixel][yPixel]=color.getRGB();
		        }
		    }

}
public void lightenArrayImage(){
	Color color;
	for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) //*
		    {
		        for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) //*
		        {
		        	color= new Color(array2D[xPixel][yPixel]);
		        	color=color.brighter();
					array2D[xPixel][yPixel]=color.getRGB();
		        }
		    }

}
public void exit(){
	window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
	try{thr.join();} catch(InterruptedException e){}
	System.exit(0);
}
public void save(){
	Scanner scan = new Scanner(System.in);
	String outputFileName;
	System.out.println("input file name:");
	outputFileName=scan.nextLine();
	File file = new File(outputFileName);
	try {
	    // retrieve image

	    ImageIO.write(image2, "bmp", file);
		} catch (IOException e) {}

}
public void drawArrayImage(){
	   for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) //*
	    {
	        for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) //*
	        {
	            image2.setRGB(xPixel,yPixel,array2D[xPixel][yPixel]);

	        }
	    }
}
public void paint(Graphics g){
	if(ready){

		 g.drawImage(image2, 0, 0, null);
		 System.out.println("image printed");


	}

}
public void thin(){
	int [] [] F = new int[image.getWidth()][image.getHeight()]; //*

	for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) //*
	    {
	    for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) //*
	        {
	        	F[xPixel][yPixel] = getBW(255).getRGB(); 
	        }
	    } 
	int [][] I= array2D;
	int [][] C=new int[image.getWidth()][image.getHeight()];
	int i=0;
	int counter=0;
	while (counter<30){
		addFinals(F,I,i);
		if(equals(F,I))break;
		calcContour(C, I, i);
		calcNewImage(I, C, F);
		i=(i+1)%4;
		counter++;
	}


				


}
public void calcNewImage(int [][] I, int [][] C, int [][] F){
	Color color;
	int i,c,f;
	for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) //*
	    {
	    for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) //*
	        {
	        	i=getArrayColor(xPixel, yPixel);
	        	c= new Color(C[xPixel][yPixel]).getGreen();
	        	f= new Color(F[xPixel][yPixel]).getGreen();

	        	if (c==0)
	        		i=255;
	        	if (f==0)
	        		i=0;
	        	color = getBW(i);
	        	array2D[xPixel][yPixel]= color.getRGB();

	        }
	    } 
}
public void addFinals(int[][] F, int [][] I, int i){
	int [][] box;
	Color color;
	for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) //*
	    {
	    for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) //*
	        {
	        	box = getBox(xPixel,yPixel);
	        	if(isfinalBi(box,i)){
	        		color=getBW(0);
	        		F[xPixel][yPixel]=color.getRGB();
	        	}

	        }
	    } 
}
public boolean isA(int [][] box){
	int [] B = new int [10];
	int counter =1;
	for(int i=0; i<3; i++)
		for(int j=0; j<3; j++){
			B[counter++]=box[i][j];
		}
	if ((B[1]==0||B[2]==0||B[3]==0)&&B[4]==255&&B[5]==0&&B[6]==255&&(B[7]==0||B[8]==0||B[9]==0))
		return true;
	else if((B[1]==0||B[4]==0||B[7]==0)&&B[2]==255&&B[5]==0&&B[7]==255&&(B[3]==0||B[6]==0||B[9]==0))
		return true;
	else if((B[4]==0||B[7]==0||B[8]==0)&&B[1]==255&&B[5]==0&&B[9]==255&&(B[2]==0||B[3]==0||B[6]==0))
		return true;
	else if((B[1]==0||B[2]==0||B[4]==0)&&B[7]==255&&B[5]==0&&B[3]==255&&(B[6]==0||B[8]==0||B[9]==0))
		return true; 
	else 
		return false;
}
public boolean isfinalBi(int [][] box, int i){
	int [] B = new int [10];
	int counter =1;
	for(int k=0; k<3; k++)
		for(int j=0; j<3; j++){
			B[counter++]=box[k][j];
		}
	if ((B[1]==0||B[2]==0||B[3]==0)&&B[7]==255&&B[5]==0&&B[6]==255&&B[8]==0&&(i==0||i==2))
		return true;
	else if((B[1]==0||B[4]==0||B[7]==0)&&B[3]==255&&B[5]==0&&B[8]==255&&B[6]==0&&(i==0||i==3))
		return true;
	else if((B[9]==0||B[7]==0||B[8]==0)&&B[3]==255&&B[5]==0&&B[4]==255&&B[2]==0&&(i==1||i==3))
		return true;
	else if((B[3]==0||B[6]==0||B[9]==0)&&B[7]==255&&B[5]==0&&B[2]==255&&B[4]==0&&(i==1||i==2))
		return true; 
	else 
		return false;
}
public boolean isContouri(int [][] box, int i){
	if(box[1][1]==0){
		if(box[2][1]==255&&i==0)
			return true;
		if(box[0][1]==255&&i==1)
			return true;
		if(box[1][0]==255&&i==2)
			return true;
		if(box[1][2]==255&&i==3)
			return true;
	}
	return false; 

}
public boolean equals(int[][] A, int [][] B){
	for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) //*
	    {
	    for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) //*
	        {

	        	if(A[xPixel][yPixel]!=B[xPixel][yPixel])
	        		return false; 
	        }


	    }
	 return true;

}
public void calcContour(int [][] C, int [][] I, int i){
	int [][] box;
	Color color;
	for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) //*
	    {
	    for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) //*
	        {
	        	box = getBox(xPixel,yPixel);
	        	if(isContouri(box,i))
	        		color=getBW(0);
	        	else 
	        		color=getBW(255);
	        	C[xPixel][yPixel]=color.getRGB();

	        }
	    } 
}


}