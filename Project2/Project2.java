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
import java.awt.Font;


 

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
 	int imageCycle;
    Thread thr = new Thread(this);
    Scanner scanner;
    Frame window; 
 	boolean ready;
 	int[][] regions;
 	int numRegions;
 	boolean[][] connectedRegions;
 	ArrayList<Integer> regionVotes;
 	ArrayList<Integer> verticalCount;
 	ArrayList<Integer> horizontalCount;
 	ArrayList<Integer> regionDensity;
 	ArrayList<int[]> minmax;//min x,y max x,y
 	ArrayList<Boolean> goodRegions;
 	ArrayList<Boolean> connected;
 	ArrayList<Boolean> vertical;
 	ArrayList<Boolean> horizontal;
 	ArrayList<Boolean> tlbr;
 	ArrayList<Boolean> trbl;

 	boolean regionsCalculated;
 	int [][] huffAccumulator;
 	boolean huff;
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
					    	case "c": cycle();
					    		break;
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
					    	case "sl2":saves.add(copyArray2D(array2D));sharpenLaplacian(2);
					    		break;
					    	case "save": save();
					    		break;
					    	case "ek":saves.add(copyArray2D(array2D));detectEdgeKirsch(5);
					    		break;
					    	case "eko":saves.add(copyArray2D(array2D));overlayEdges(5);
					    		break;
					    	case "ekt":saves.add(copyArray2D(array2D));overlayThinnedEdges(5);
					    		break;	
					    	case "el":saves.add(copyArray2D(array2D));detectEdgeLaplacian(7,1);
					    		break;
					    	case "el2":saves.add(copyArray2D(array2D));detectEdgeLaplacian(7,2);
					    		break;
					    	case "m": saves.add(copyArray2D(array2D));increaseContrast(200,5);
					    		break;
					    	case "n": saves.add(copyArray2D(array2D));decreaseContrast(200,5);
					    		break;
					    	case "t":saves.add(copyArray2D(array2D));thin();
					    		break;
					    	case "rn":saves.add(copyArray2D(array2D));ReduceNoise(2);
					    		break;
					    	case "flip":saves.add(copyArray2D(array2D));invertColor();
					    		break;
					    	case "rn3":saves.add(copyArray2D(array2D));ReduceNoise(3);
					    		break;
					    	case "rn4":saves.add(copyArray2D(array2D));ReduceNoise(4);
					    		break;
					    	case "h":saves.add(copyArray2D(array2D));HoughTransform();
					    		break;
					    	case "sf": saves.add(copyArray2D(array2D));smartFill();
					    		break; 
					    	case "f":
					    			saves.add(copyArray2D(array2D));
					    		   	sharpenLaplacian(1);
					    		   	increaseContrast(200,5);
					    		   	regions=detectRegions(15);
					    			calcVotes();
					    			regionsCalculated=true;
					    			revertToOriginal();
					    			image2=createResizedCopy(image2,image2.getWidth(), image2.getHeight(),true);

					    	//case "f": saves.add(copyArray2D(array2D));Fill();
					    		break; 
					    	case "reg":regions=detectRegions(15);
					    			//	calcVotes();
					    				regionsCalculated=true;
					    		break;
					    	case "resize": saves.add(copyArray2D(array2D));resize();
					    		break;
					    	case "z":undo();
					    		break;
					    	case "open": open();
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
 		imageCycle=0;
 		clickCount=0; 
 		filename="im1-c.bmp";
 		//m m m sh sm sg sg
 		scanner = new Scanner(System.in);
 		saves = new ArrayList<int[][]>();

 		thr.start();

 		 addMouseListener(new MouseAdapter(){
 		  public void mousePressed(MouseEvent evt){
 		  
 		  		clickCount++;
			}});
 		


 	}
int [] cpyarr(int[] ar,int size){
	int[] temp= new int[size];
	for(int i=0; i<size; i++)
		temp[i]=ar[i];
	return temp;
}
int [][] copyArray2D(int [][] array){
	int[][] temp;
    temp = new int[image2.getWidth()][image2.getHeight()]; //*

	for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
	    {
	    for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
	        {
	        	temp[xPixel][yPixel] = array[xPixel][yPixel]; 
	        }
	    }
	 return temp;
}
public void menu(){
    System.out.println("Darken:d  Lighten:l  Smooth:sm/sa/sg Sharpen:sh/sl");
    System.out.println("EdgeDetect:ek/el Thin:t Undo:z contrast:n/m Revert:o");
    System.out.println("ReduceNoise:rn hough:h sfill:sf fill:f Save:s Exit:0");

  

}
public void calcVotes(){
	//connectedRegions=new boolean[numRegions+1][numRegions+1];
	int neighborRange=100;
	int offset1=1;
	int offset2=3;
	int offset3=3;
	int hcount=50;
	int vcount=50;
	int columnsthreshhold=20;
	int rowthreshold=20;
	verticalCount=new ArrayList<Integer>();
	horizontalCount=new ArrayList<Integer>();
	connected=new ArrayList<Boolean>();
 	vertical=new ArrayList<Boolean>();
 	horizontal=new ArrayList<Boolean>();
 	tlbr=new ArrayList<Boolean>();
 	trbl=new ArrayList<Boolean>();
 	boolean found1,found2; 
	for(int r=0; r<=numRegions; r++){
		verticalCount.add(0);
		horizontalCount.add(0);
		regionVotes.add(0);
		connected.add(false);
		vertical.add(false);
		horizontal.add(false);
		tlbr.add(false);
		trbl.add(false);
	}
	//for(int r=0; r<numRegions; r++)
		//for(int i=0; i<numRegions;i++)
		//	connectedRegions[r][i]=false;
	for(int r=0; r<numRegions; r++){
		if(!goodRegions.get(r))
			continue;
		System.out.println("analyze region: " +r);
		int topx, topy, leftx, lefty,
			rightx,righty, bottomx,bottomy;
		leftx=minmax.get(r)[0]+offset3; lefty=-1;
		bottomy=minmax.get(r)[1]+offset3;bottomx=-1;
		rightx=minmax.get(r)[2]-offset3;righty=-1;
		topy=minmax.get(r)[3]-offset3;topx=-1;
		hcount=(int)((double)(rightx-leftx)*0.5);
		vcount=(int)((double)(topy-bottomy)*0.5);
		columnsthreshhold=(int)((rightx-leftx)/5);
		rowthreshold=(int)((double)(topy-bottomy)/5);
		int countRight=0, countLeft=0, countTop=0, countBottom=0;
		ArrayList<Integer> nearbyRegions=new ArrayList<Integer>();
		ArrayList<Integer> nearbyRegionsL=new ArrayList<Integer>();
		ArrayList<Integer> nearbyRegionsR=new ArrayList<Integer>();
		ArrayList<Integer> nearbyRegionsU=new ArrayList<Integer>();
		ArrayList<Integer> nearbyRegionsD=new ArrayList<Integer>();



		for(int i=0;i<numRegions;i++){
			nearbyRegions.add(0);
			nearbyRegionsL.add(0);
			nearbyRegionsR.add(0);
			nearbyRegionsU.add(0);
			nearbyRegionsD.add(0);

		}
		int current, off1,off2,off3,off4;
		//detect pseudo-corners of "boxes"
		found1=false;found2=false;
		for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++){
			//System.out.println(xPixel+" " +topy);
			current=regions[xPixel][topy];
			off1=regions[xPixel][topy+offset1];
			off2=regions[xPixel][topy+offset2];
			off3=regions[xPixel][topy-offset1];
			off4=regions[xPixel][topy-offset2];
			if(current==r||off1==r||off2==r||off3==r||off4==r){
				countTop++;
				if(!found1){
						topx=xPixel;
						found1=true;
					}

			}
			if(xPixel<=rightx+neighborRange&&xPixel>=leftx-neighborRange){
				nearbyRegionsU.set(current,nearbyRegions.get(current)+1);
				if(off1!=current){
					nearbyRegionsU.set(off1,nearbyRegions.get(off1)+1);
					if(off2!=off1){
						nearbyRegionsU.set(off2,nearbyRegions.get(off2)+1);
						if(off3!=off2){
							nearbyRegionsU.set(off3,nearbyRegions.get(off3)+1);
							if(off4!=off3){
								nearbyRegionsU.set(off4,nearbyRegions.get(off4)+1);
							}
						}
					}
				}
				
			}

			current=regions[xPixel][bottomy];
			off1=regions[xPixel][bottomy+offset1];
			off2=regions[xPixel][bottomy+offset2];
			off3=regions[xPixel][bottomy-offset1];
			off4=regions[xPixel][bottomy-offset2];
			if(current==r||off1==r||off2==r||off3==r||off4==r){
				countBottom++;
				if(!found2){
					bottomx=xPixel;
					found2=true;
				}

			}
			if(xPixel<=rightx+neighborRange&&xPixel>=leftx-neighborRange){
				nearbyRegionsD.set(current,nearbyRegions.get(current)+1);
				if(off1!=current){
					nearbyRegionsD.set(off1,nearbyRegions.get(off1)+1);
					if(off2!=off1){
						nearbyRegionsD.set(off2,nearbyRegions.get(off2)+1);
						if(off3!=off2){
							nearbyRegionsD.set(off3,nearbyRegions.get(off3)+1);
							if(off4!=off3){
								nearbyRegionsD.set(off4,nearbyRegions.get(off4)+1);
							}
						}
					}
				}
				
			}
		}
		//detect pseudo-corners of "boxes"
		found1=false;found2=false;
		for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++){
			current=regions[leftx][yPixel];
			off1=regions[leftx+offset1][yPixel];
			off2=regions[leftx+offset2][yPixel];
			off3=regions[leftx-offset1][yPixel];
			off4=regions[leftx-offset2][yPixel];
			if(current==r||off1==r||off2==r||off3==r||off4==r){
				countLeft++;
				lefty=yPixel;
			}
			if(yPixel<=topy+neighborRange&&yPixel>=bottomy-neighborRange){
				nearbyRegionsL.set(current,nearbyRegions.get(current)+1);
				if(off1!=current){
					nearbyRegionsL.set(off1,nearbyRegions.get(off1)+1);
					if(off2!=off1){
						nearbyRegionsL.set(off2,nearbyRegions.get(off2)+1);
						if(off3!=off2){
							nearbyRegionsL.set(off3,nearbyRegions.get(off3)+1);
							if(off4!=off3){
								nearbyRegionsL.set(off4,nearbyRegions.get(off4)+1);
							}
						}
					}
				}
				
			}
			current=regions[rightx][yPixel];
			off1=regions[rightx+offset1][yPixel];
			off2=regions[rightx+offset2][yPixel];
			off3=regions[rightx-offset1][yPixel];
			off4=regions[rightx-offset2][yPixel];
			if(current==r||off1==r||off2==r||off3==r||off4==r){
				countRight++;
				righty=yPixel;
				}
			if(yPixel<=topy+neighborRange&&yPixel>=bottomy-neighborRange){
				nearbyRegionsR.set(current,nearbyRegions.get(current)+1);
				if(off1!=current){
					nearbyRegionsR.set(off1,nearbyRegions.get(off1)+1);
					if(off2!=off1){
						nearbyRegionsR.set(off2,nearbyRegions.get(off2)+1);
						if(off3!=off2){
							nearbyRegionsR.set(off3,nearbyRegions.get(off3)+1);
							if(off4!=off3){
								nearbyRegionsR.set(off4,nearbyRegions.get(off4)+1);
							}
						}
					}
				}
				
			}

		}
		//record regions that are connected
		//if region has a connected region then it is "more valid"
		for(int i=0; i<nearbyRegions.size(); i++){
			if(goodRegions.get(i)){
				// System.out.println("countTop: "+ countTop
				// 	+"\ncountBottom:"+countBottom
				// 	+"\ncountLeft:"+countLeft
				// 	+"\ncountRight:"+countRight
				// 	+"\nnbRT:"+nearbyRegionsU.get(i)
				// 	+"\nnbRB:"+nearbyRegionsD.get(i)
				// 	+"\nnbRL:"+nearbyRegionsL.get(i)
				// 	+"\nnbRR:"+nearbyRegionsR.get(i)
				// 	+"\nnbR:"+nearbyRegions.get(i));

				if(i!=r)
					if(nearbyRegionsU.get(i)>(min((rightx-leftx),(topy-bottomy))/2) )
					{
					//connectedRegions[r][i]=true;
					connected.set(r,true);
					regionVotes.set(r,regionVotes.get(r)+1);
				}
				if(nearbyRegionsD.get(i)>(min((rightx-leftx),(topy-bottomy))/2) )
					{
					//connectedRegions[r][i]=true;
					connected.set(r,true);
					regionVotes.set(r,regionVotes.get(r)+1);
				}
				if(nearbyRegionsL.get(i)>(min((rightx-leftx),(topy-bottomy))/2) )
					{
					//connectedRegions[r][i]=true;
					connected.set(r,true);
					regionVotes.set(r,regionVotes.get(r)+1);
				}
				if(nearbyRegionsR.get(i)>(min((rightx-leftx),(topy-bottomy))/2) )
					{
					//connectedRegions[r][i]=true;
					connected.set(r,true);
					regionVotes.set(r,regionVotes.get(r)+1);
				}
			}
		}
		for(int i=leftx; i<=rightx; i++){
			boolean done=false;
			int count=0;
			for(int j=0; j<image2.getHeight()&&!done;j++){
				if(regions[i][j]==r)
					count++;
				else count=0;
				if(count>vcount){
					verticalCount.set(r,verticalCount.get(r)+1);
					done=true;
				}
		}
	}
		for(int i=0; i<image2.getWidth(); i++){
			boolean done=false;
			int count=0;
			for(int j=bottomy; j<=topy&&!done;j++){
				//if(r==5523)
				//System.out.print(count +" ");

				if(regions[i][j]==r)
					count++;
				else count=0;
				if(count>hcount){
					horizontalCount.set(r,horizontalCount.get(r)+1);
					done=true;
				}
		}
	}
				if(verticalCount.get(r)>columnsthreshhold){
						regionVotes.set(r,regionVotes.get(r)+2);
						System.out.println("columnsthreshhold");

					}
					System.out.println(verticalCount.get(r)+" "+columnsthreshhold+" "+vcount+"\n"
						+horizontalCount.get(r)+" "+rowthreshold+" "+hcount);

				if(horizontalCount.get(r)>rowthreshold){
						regionVotes.set(r,regionVotes.get(r)+2);
						System.out.println("rowthreshold");

					}

				if(countRight>vcount&&countLeft>vcount){//vertical pair
						regionVotes.set(r,regionVotes.get(r)+1);
						vertical.set(r,true);
					}
				if(countBottom>hcount&&countTop>hcount){//horizontal pair
						regionVotes.set(r,regionVotes.get(r)+1);
						horizontal.set(r,true);
					}
				int c1=(int)pow((topx-leftx),2)+(int)pow((topy-lefty),2);
				int c2=(int)pow((bottomx-rightx),2)+(int)pow((bottomy-righty),2);
				c1=(int)sqrt(c1);
				c2=(int)sqrt(c2);
				System.out.println(max((rightx-leftx),(topy-bottomy))+"\n");

				if(abs(c1-c2)<(max((rightx-leftx),(topy-bottomy)))/5){//two sides same length
						tlbr.set(r,true);
						regionVotes.set(r,regionVotes.get(r)+1);
					}
				System.out.println(""+topx+" "+leftx+" "+topy+" "+lefty);
				System.out.println(""+bottomx+" "+rightx+" "+bottomy+" "+righty);

				System.out.println(""+c1+" "+c2);
				System.out.println(""+(abs(c1-c2)));


				c1=(int)pow((topx-rightx),2)+(int)pow((topy-righty),2);
				c2=(int)pow((bottomx-leftx),2)+(int)pow((bottomy-lefty),2);
				c1=(int)sqrt(c1);
				c2=(int)sqrt(c2);
				if(abs(c1-c2)<(max((rightx-leftx),(topy-bottomy)))/5){//two sides same length
						regionVotes.set(r,regionVotes.get(r)+1);
						trbl.set(r,true);
					}
				System.out.println(""+topx+" "+rightx+" "+topy+" "+righty);
				System.out.println(""+bottomx+" "+leftx+" "+bottomy+" "+lefty);

				System.out.println(""+c1+" "+c2);
				System.out.println(""+(abs(c1-c2)));


				System.out.println("region " +r+": votes:"+regionVotes.get(r)+" connected: "
					+connected.get(r)+" vertical:"+vertical.get(r)+
					"\nhorizontal:"+horizontal.get(r)+" TLBR, TRBL:"
					+tlbr.get(r)+trbl.get(r));
				System.out.println("");

			
		
	}

	
	int max=0;
	int count =0;

	for(int i=0; i<regionVotes.size()-1; i++){
		if(regionVotes.get(i)>max)
			max=regionVotes.get(i);
	}
	System.out.println("max:" +max);
	for(int i=0; i<regionVotes.size()-1; i++){
		if(goodRegions.get(i)){
			if(regionVotes.get(i)==max)
			System.out.println("boxregion:" +i);
			else 
				goodRegions.set(i,!goodRegions.get(i));
		}
	}

}
public int[][] detectRegions(int threshhold){

	//https://www.cs.auckland.ac.nz/courses/compsci773s1c/lectures/ImageProcessing-html/topic3.htm
	image2=createResizedCopy(image2,image2.getWidth(), image2.getHeight(),true);
	int[][] temp;
	int regionNum=1;
	int count;
	goodRegions= new ArrayList<Boolean>();
	minmax= new ArrayList<int[]>();
	regionDensity=new ArrayList<Integer>();
	regionDensity.add(1);
	regionVotes=new ArrayList<Integer>();
	int[] dummy = new int[1];
	minmax.add(dummy);

	goodRegions.add(Boolean.FALSE);
	temp = new int[image2.getWidth()][image2.getHeight()];
	for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
	    {
	    for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
	        {
	        	temp[xPixel][yPixel] = 0; 
	        }
	    }

	for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
	    {
	    for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
	        {
	        	if(temp[xPixel][yPixel] == 0){
	        		//System.out.println("grow region at: " +xPixel+" "+yPixel);
	        		count=regionGrow(temp,threshhold,regionNum, xPixel,yPixel);
	        		// if(count>3000)
	        		// 	goodRegions.add(Boolean.TRUE);
	        		// else
	        		// 	goodRegions.add(Boolean.FALSE);
	        			
	        		regionNum++;
	        	} 
	        }
	    }
	System.out.println("numRegions: " +regionNum);

	numRegions=regionNum;

	return temp;
}
public int regionGrow(int[][] temp, int threshhold, int regionNum,int x, int y){
	Color color1 = new Color(array2D[x][y]);
	Color color2 = new Color(2);
	int[] minmaxarray= new int[4];

	minmaxarray[0]=minmaxarray[2]=x;
	minmaxarray[1]=minmaxarray[3]=y;
	int average = color1.getGreen();
	int averageTotal=average;
	int averageCount=1;
	temp[x][y]=regionNum;
	ArrayList<int[]> boarder = new ArrayList<int[]>();
	int count=1;
	int [] center = new int [2];
	center [0]=x;
	center [1]=y; 
	boarder.add(center);
	boolean done = false; 
	while (!done){
		done=true;
		for(int i=0; i<boarder.size(); i++){
			x=boarder.get(i)[0];
			y=boarder.get(i)[1];
			//color1 = new Color(array2D[x][y]);
			//up
			if(y>0){
				if(temp[x][y-1]==0){

					color2= new Color(array2D[x][y-1]);
					if(abs(average-color2.getGreen())<threshhold){
						//System.out.println("added: "+x+" "+(y-1));
						int [] newBoarder = new int [2];
						newBoarder [0]=x;
						newBoarder [1]=y-1; 
						boarder.add(newBoarder);
						averageTotal+=color2.getGreen();
						averageCount++;
						average=averageTotal/averageCount;
						temp[x][y-1]=regionNum;
						if((y-1)<minmaxarray[1])
							minmaxarray[1]=y-1;
						if((y-1)>minmaxarray[3])
							minmaxarray[3]=y-1;
						if(x>minmaxarray[2])
							minmaxarray[2]=x;
						if(x<minmaxarray[0])
							minmaxarray[0]=x;
						count++;
						done=false;

					}
				}

			}
			//down
			if(y<image2.getHeight()-1){
				if(temp[x][y+1]==0){
					color2= new Color(array2D[x][y+1]);
					if(abs(average-color2.getGreen())<threshhold){
						//System.out.println("added: "+x+" "+(y+1));
						int [] newBoarder = new int [2];
						newBoarder [0]=x;
						newBoarder [1]=y+1; 
						boarder.add(newBoarder);
						averageTotal+=color2.getGreen();
						averageCount++;
						average=averageTotal/averageCount;
						temp[x][y+1]=regionNum;	
						if((y+1)<minmaxarray[1])
							minmaxarray[1]=y+1;
						if((y+1)>minmaxarray[3])
							minmaxarray[3]=y+1;
						if(x>minmaxarray[2])
							minmaxarray[2]=x;
						if(x<minmaxarray[0])
							minmaxarray[0]=x;
						count++;
						done=false;					

					}
				}
			}
			//left
			if(x>0){
				if(temp[x-1][y]==0){
					color2= new Color(array2D[x-1][y]);
					if(abs(average-color2.getGreen())<threshhold){
						//System.out.println("added: "+(x-1)+" "+y);
						int [] newBoarder = new int [2];
						newBoarder [0]=x-1;
						newBoarder [1]=y; 
						boarder.add(newBoarder);
						averageTotal+=color2.getGreen();
						averageCount++;
						average=averageTotal/averageCount;
						temp[x-1][y]=regionNum;
						if((y)<minmaxarray[1])
							minmaxarray[1]=y;
						if((y)>minmaxarray[3])
							minmaxarray[3]=y;
						if((x-1)>minmaxarray[2])
							minmaxarray[2]=(x-1);
						if((x-1)<minmaxarray[0])
							minmaxarray[0]=(x-1);
						count++;
						done=false;
					}
				}
			}
			//right
			if(x<image2.getWidth()-1){
				if(temp[x+1][y]==0){
					color2= new Color(array2D[x+1][y]);
					if(abs(average-color2.getGreen())<threshhold){
						//System.out.println("added: "+(x+1)+" "+y);
						int [] newBoarder = new int [2];
						newBoarder [0]=x+1;
						newBoarder [1]=y; 
						boarder.add(newBoarder);
						averageTotal+=color2.getGreen();
						averageCount++;
						average=averageTotal/averageCount;
						temp[x+1][y]=regionNum;
						if((y)<minmaxarray[1])
							minmaxarray[1]=y;
						if((y)>minmaxarray[3])
							minmaxarray[3]=y;
						if((x+1)>minmaxarray[2])
							minmaxarray[2]=(x+1);
						if((x+1)<minmaxarray[0])
							minmaxarray[0]=(x+1);
						count++;
						done=false;
					}
				}
			}
		} 

	}

	minmax.add(minmaxarray);
	regionDensity.add(count);
	if(count>3000&&average>150){
			if(isBoxish(minmaxarray,1000,3.5,regionNum,count,2))
		    	goodRegions.add(Boolean.TRUE);
		    else 
		    	goodRegions.add(Boolean.FALSE);
		}
	else
	  	goodRegions.add(Boolean.FALSE);
	
	return count;
}
int max(int x, int y){
	if(x>y)
		return x;
	else return y;
}
int min(int x, int y){
	if(x<y)
		return x;
	else return y;
}
public boolean isBoxish(int[] minmaxarray,
		 int deltaLength,double deltaRatio,
		 int regionNum,int count, double countRatio){
	int xdif,ydif;
	boolean foreGround=true;
	if(minmaxarray[0]<3
		||minmaxarray[1]<3
		||minmaxarray[2]>image2.getWidth()-4
		||minmaxarray[3]>image2.getHeight()-4)
			foreGround=false;
	xdif=minmaxarray[2]-minmaxarray[0];
	ydif=minmaxarray[3]-minmaxarray[1];
	if(!foreGround)
		return false;
	int guessArea=xdif*ydif;
	if(guessArea/count >countRatio)
		return false;

	if(abs(xdif-ydif)<deltaLength){
		double ratio=max(xdif,ydif)/min(xdif,ydif);
			if(ratio<deltaRatio)
				return true;
			else 
				return false;
	}
	else 
		return false;

}
public boolean isMostlyFilled(int[] minmaxarray, int regionNum, int area, int ratio){

	return true;
}
public boolean onEdge(int [][]temp, int x,int y,int regionNum,boolean four, Color color){
	//System.out.println(x + "  "+y);
	int w = image2.getWidth()-1;
	int h=image2.getHeight()-1;
	if(x<w&&temp[x+1][y]==regionNum){
		color= new Color (array2D[x+1][y]);
		return true; 
	}
	if(x>0&&temp[x-1][y]==regionNum){
		color = new Color(array2D[x-1][y]);
		return true; 
	}
	if(y<h&&temp[x][y+1]==regionNum){
		color = new Color(array2D[x][y+1]);
		return true; 
	}
	if(y>0&&temp[x][y-1]==regionNum){
		color = new Color(array2D[x][y-1]);
		return true; 
	}
	return false;
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
public void resize(){
	image2=createResizedCopy(image2,image2.getWidth()/2, image2.getHeight()/2,true);
    array2D = new int[image2.getWidth()][image2.getHeight()]; //*

	    for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
	    {
	        for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
	        {
	            int color = image2.getRGB(xPixel, yPixel); //*
	            array2D[xPixel][yPixel] = color;

	        }
	    }
}
BufferedImage createResizedCopy(Image originalImage, 
    		int scaledWidth, int scaledHeight, 
    		boolean preserveAlpha)
    {
    	System.out.println("resizing...");
    	int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
    	BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
    	Graphics2D g = scaledBI.createGraphics();
    	if (preserveAlpha) {
    		g.setComposite(AlphaComposite.Src);
    	}
    	g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null); 
    	g.dispose();
    	return scaledBI;
    }
public void revertToOriginal(){
	try{
	 		  		 		  			getBMPImage(filename);
	 		  		
	 		  		 		  		}
	 		  		 		  		catch(IOException e){};
	 		  			image2 = new BufferedImage(image.getWidth(),image.getHeight(),  image.getType());
	 		  		 array2D = new int[image.getWidth()][image.getHeight()]; //*

				    for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
				    {
				        for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
				        {
				            int color = image.getRGB(xPixel, yPixel); //*
				            array2D[xPixel][yPixel] = color;

				        }
				    }
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
	for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
		    {
		        for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
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
	for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
		    {
		        for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
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
    temp = new int[image2.getWidth()][image2.getHeight()]; //*

	for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
	    {
	    for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
	        {
	        	temp[xPixel][yPixel] = getBW(getEdgeElement(xPixel,yPixel,delta)).getRGB(); 
	        }
	    }
	array2D=copyArray2D(temp);
}
public int[][] getKirschArray(int delta){
	int[][] temp;
    temp = new int[image2.getWidth()][image2.getHeight()]; //*

	for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
	    {
	    for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
	        {
	        	temp[xPixel][yPixel] = getBW(getEdgeElement(xPixel,yPixel,delta)).getRGB(); 
	        }
	    }
	return temp;
}
public void overlayEdges(int delta){
	int[][] kirsch=copyArray2D(getKirschArray(delta));
	int[][] temp = copyArray2D(array2D);
	int edge = getBW(0).getRGB();
	for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
	    {
	    for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
	        {
	        	if(kirsch[xPixel][yPixel]==edge)
	        		temp[xPixel][yPixel]=edge; 
	        }
	    }
	array2D=copyArray2D(temp);
}
public void overlayThinnedEdges(int delta){
	int[][] temp = copyArray2D(array2D);
	int[][] kirsch=copyArray2D(getThinnedKirsch(delta));
	//revertToOriginal();
	int edge = getBW(0).getRGB();
	for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
	    {
	    for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
	        {
	        	if(kirsch[xPixel][yPixel]==edge)
	        		temp[xPixel][yPixel]=edge; 
	        }
	    }
	array2D=copyArray2D(temp);
}
public int[][] getThinnedKirsch(int delta){
	detectEdgeKirsch(delta);
	thin();
	return array2D;
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

	int[][] temp=copyArray2D(array2D);

	for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
		{
		for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
			        {
						box=getBox(xPixel, yPixel);
						if(countNeighbors(box)<threshhold){
							newVal=255;
							color=getBW(newVal);
							temp[xPixel][yPixel]=color.getRGB();
						}

			        }
			    }
	array2D=copyArray2D(temp);

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
	int [][]temp=copyArray2D(array2D);
	for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
		{
		for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
			        {
						box=getBox(xPixel, yPixel);
						if(abs(convolution(box,filter))<delta)
							newVal=255;
						else 
							newVal=0;
						color=getBW(newVal);
						temp[xPixel][yPixel]=color.getRGB();

			        }
			    }
	array2D=copyArray2D(temp);

}
public void smoothMed(){
	Color color;
	int [][] temp= copyArray2D(array2D);
	int[][] box= new int[3][3];
	int median;

		for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
			    {
			        for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
			        {
						box=getBox(xPixel, yPixel);
						median=calcMedianBox(box);
						color=getBW(median);
						temp[xPixel][yPixel]=color.getRGB();

			        }
			    }
	array2D=copyArray2D(temp);	 
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
public void Fill(){
	int [][] box = new int [3][3];
	int [][] temp= copyArray2D(array2D);

		for (int xPixel = 1; xPixel < image2.getWidth()-1; xPixel++) //*
		{
		for (int yPixel = 1; yPixel < image2.getHeight()-1; yPixel++) //*
			        {
						box=getBox(xPixel, yPixel);
						if(box[1][1]<255){
							temp[xPixel+1][yPixel+1]=getBW(0).getRGB();

							temp[xPixel][yPixel+1]=getBW(0).getRGB();

							temp[xPixel-1][yPixel+1]=getBW(0).getRGB();

							temp[xPixel+1][yPixel]=getBW(0).getRGB();

							temp[xPixel-1][yPixel]=getBW(0).getRGB();

							temp[xPixel+1][yPixel-1]=getBW(0).getRGB();

							temp[xPixel][yPixel-1]=getBW(0).getRGB();

							temp[xPixel-1][yPixel-1]=getBW(0).getRGB();

						}

			        }
			    }
	array2D=copyArray2D(temp);


}
public void smartFill(){
	int [][] box = new int [3][3];
	int [][] temp= copyArray2D(array2D);

		for (int xPixel = 1; xPixel < image2.getWidth()-1; xPixel++) //*
		{
		for (int yPixel = 1; yPixel < image2.getHeight()-1; yPixel++) //*
			        {
						box=getBox(xPixel, yPixel);
						if(box[1][1]<255){
							if(box[0][0]<255){
							temp[xPixel+1][yPixel+1]=getBW(0).getRGB();
							}
							if(box[0][1]<255){
							temp[xPixel][yPixel+1]=getBW(0).getRGB();
							}
							if(box[0][2]<255){
							temp[xPixel-1][yPixel+1]=getBW(0).getRGB();
							}
							if(box[1][0]<255){
							temp[xPixel+1][yPixel]=getBW(0).getRGB();
							}
							if(box[1][2]<255){
							temp[xPixel-1][yPixel]=getBW(0).getRGB();
							}
							if(box[2][0]<255){
							temp[xPixel+1][yPixel-1]=getBW(0).getRGB();
							}
							if(box[2][1]<255){
							temp[xPixel][yPixel-1]=getBW(0).getRGB();
							}
							if(box[2][2]<255){
							temp[xPixel-1][yPixel-1]=getBW(0).getRGB();
							}

						}

			        }
			    }
	array2D=copyArray2D(temp);

}
public void sharpenLaplacian(int mode){
	double sum;
	double[][] filter = getLaplacianFilter(mode);
	int[][] box= new int[3][3];
	int newVal;
	Color color;
	int [][] temp= copyArray2D(array2D);

	for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
		{
		for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
			        {
						box=getBox(xPixel, yPixel);
						newVal=convolution(box,filter);
						if(newVal>7){
							color=getBW(0);
							temp[xPixel][yPixel]=color.getRGB();
						}
			        }
			    }
	array2D=copyArray2D(temp);

}
public int convolution (int [][] box, double [][] filter){
	int sum=0;
	for(int i=0; i<3; i++)
		for(int j=0; j<3; j++)
			sum+=(int)((double)box[i][j]*filter[i][j]);
		if(sum<0)
			return (0-sum);
		return sum;

}
public void sharpenHistoEq(){
	int[] H = new int [256];
	int [][] temp= copyArray2D(array2D);

		for(int i=0; i<256; i++){
			H[i]=0;
		}
		for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
		  {
			 for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
			 {
			 	H[getArrayColor(xPixel,yPixel)]++; //fill histogram
			 }

		}
		printHisto(H);
	for(int i=1; i<256; i++)
		H[i]=H[i]+H[i-1];//cumulative histogram
	printHisto(H);

	for(int i=0; i<256; i++)
		H[i]=(int)((double)H[i]*(((double)255)/((double)(image2.getWidth()*image2.getHeight())))); //normalize
	
	printHisto(H);

	for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
		  {
			 for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
			 {
				temp[xPixel][yPixel]=getBW(H[getArrayColor(xPixel,yPixel)]).getRGB(); //fill histogram
			 }

		}
	array2D= copyArray2D(temp);

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

		for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
			    {
			        for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
			        {
						box=getBox(xPixel, yPixel);
						if(gaussian)
							average=getBoxAveGaussianWeighted(box);
						else 
							average=getBoxAve(box);
						color=getBW(average);
						temp[xPixel][yPixel]=color.getRGB();

			        }
			    }
	array2D=copyArray2D(temp);
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
			if(y+1<image2.getHeight())
				box[0][2]= getArrayColor(x-1, y+1);//array2D[x-1][y+1];
			}
	if(y-1>=0)		
		box[1][0]= getArrayColor(x, y-1);//array2D[x][y-1];
	box[1][1]= getArrayColor(x, y);//array2D[x][y];
	if(y+1<image2.getHeight())
		box[1][2]= getArrayColor(x, y+1);//array2D[x][y+1];
	if(x+1<image2.getWidth()){
		if(y-1>=0)
			box[2][0]= getArrayColor(x+1, y-1);//array2D[x+1][y-1];
		box[2][1]= getArrayColor(x+1, y);//array2D[x+1][y];
		if(y+1<image2.getHeight())
			box[2][2]= getArrayColor(x+1, y+1);//array2D[x+1][y+1];
	}
	return box;
}
public void darkenArrayImage(){
	Color color;
	for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
		    {
		        for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
		        {
		        	color= new Color(array2D[xPixel][yPixel]);
		        	color=color.darker();
					array2D[xPixel][yPixel]=color.getRGB();
		        }
		    }

}
public void invertColor(){
	Color color;
	int[][] temp;
    temp = new int[image2.getWidth()][image2.getHeight()];
	for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
		    {
		        for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
		        {
		        	color= new Color(array2D[xPixel][yPixel]);
		        	if(color.getGreen()<100)
		        		temp[xPixel][yPixel]=getBW(255).getRGB();

		        	else 
		        		temp[xPixel][yPixel]=getBW(0).getRGB();

		        }
		    }
array2D=copyArray2D(temp);
}
public void lightenArrayImage(){
	Color color;
	for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
		    {
		        for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
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

public void cycle(){
	String outputFileName;
	imageCycle++;
	if(imageCycle>7)
		imageCycle=1;
	filename="im"+imageCycle+"-c.bmp";
		try{
	 		 	getBMPImage(filename);
	 		  		
	 		  		 		  		}
	 		  		 		  		catch(IOException e){};
	 		  			image2 = new BufferedImage(image.getWidth(),image.getHeight(),  image.getType());
	 		  		 array2D = new int[image.getWidth()][image.getHeight()]; //*

				    for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
				    {
				        for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
				        {
				            int color = image.getRGB(xPixel, yPixel); //*
				            array2D[xPixel][yPixel] = color;

				        }
				    }
}
public void open(){
	Scanner scan = new Scanner(System.in);
	String outputFileName;
	System.out.println("file name:");
	filename=scan.nextLine();
		try{
	 		 	getBMPImage(filename);
	 		  		
	 		  		 		  		}
	 		  		 		  		catch(IOException e){};
	 		  			image2 = new BufferedImage(image.getWidth(),image.getHeight(),  image.getType());
	 		  		 array2D = new int[image.getWidth()][image.getHeight()]; //*

				    for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
				    {
				        for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
				        {
				            int color = image.getRGB(xPixel, yPixel); //*
				            array2D[xPixel][yPixel] = color;

				        }
				    }
}
public void drawArrayImage(){
	   for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
	    {
	        for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
	        {
	            image2.setRGB(xPixel,yPixel,array2D[xPixel][yPixel]);

	        }
	    }
}
public void paint(Graphics g){
	if(ready){
		 if(regionsCalculated){
		 	drawRegions();
		 	// for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
			 //    {
			 //    for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
			 //        {
			        	
			 //        	System.out.print(regions[xPixel][yPixel]);
			        		
			        	
			 //        }
			 //    }
		 	regionsCalculated=false;
		 }
		 g.drawImage(image2, 0, 0, null);
		 if(huff){
		 	g.setColor(Color.RED);
		 	drawHuffLines(g,200);
		 	huff=false;
		 }
		 drawRegionNumbers(g);
		 System.out.println("image printed");


	}

}
public void drawRegionNumbers(Graphics g){
	g.setColor(Color.RED);
	for(int i=0; i<numRegions; i++)
		if(goodRegions.get(i)){
	 		g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
			g.drawString("BOX IS HERE", (int)((minmax.get(i)[0]+minmax.get(i)[2])/2)-50, (int)((minmax.get(i)[1]+minmax.get(i)[3])/2));
			g.drawRect(minmax.get(i)[0]-(minmax.get(i)[2]-minmax.get(i)[0])/2,minmax.get(i)[1]-(minmax.get(i)[3]-minmax.get(i)[1])/2,(minmax.get(i)[2]-minmax.get(i)[0])*2,(minmax.get(i)[3]-minmax.get(i)[1])*2);
		}
}
public void drawRegions(){
	Color color; 
	Random rand = new Random();
	float r,g,b;
	for(int i=0; i<numRegions; i++){
		if(!goodRegions.get(i))
			continue;
		r=rand.nextFloat(); g=rand.nextFloat(); b=rand.nextFloat();
	   	color=Color.GREEN;
	    //color=new Color(r,g,b);
	    if(i%10000==0)
	    	System.out.println("drawing region: " +i);

	  for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
	    {
	        for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
	        {	
	         
	        	if(regions[xPixel][yPixel]==i){
	        		// System.out.println("set color ");

	           	 	image2.setRGB(xPixel,yPixel,color.getRGB());
	        	}

	        }
	    }
	}

}
public void thin(){
	int [] [] F = new int[image2.getWidth()][image2.getHeight()]; //*

	for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
	    {
	    for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
	        {
	        	F[xPixel][yPixel] = getBW(255).getRGB(); 
	        }
	    } 
	int [][] I= array2D;
	int [][] C=new int[image2.getWidth()][image2.getHeight()];
	int i=0;
	int counter=0;
	while (counter<5){
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
	for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
	    {
	    for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
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
	for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
	    {
	    for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
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
	for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
	    {
	    for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
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
	for (int xPixel = 0; xPixel < image2.getWidth(); xPixel++) //*
	    {
	    for (int yPixel = 0; yPixel < image2.getHeight(); yPixel++) //*
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

public void HoughTransform(){
	Color color;
	int h = image2.getHeight();
	int w = image2.getWidth();
	int val;
	double r;
	int theta;
	double hough_h = ((Math.sqrt(2.0)*((double)(h>w?h:w)))/2);
	int acc_width = (int)(hough_h * 2.0)+1;
	double center_x=w/2;
	double center_y = h/2;
	int [][] accumulator = new int[180][acc_width];

	for (int xPixel = 0; xPixel < w; xPixel++) //*
	    {
	    for (int yPixel = 0; yPixel < h; yPixel++) //*
	        {
	        	color = new Color(array2D[xPixel][yPixel]);
	        	val = color.getGreen();
	        	//http://www.keymolen.com/2013/05/hough-transformation-c-implementation.html
	        	//r=xcos(theta)+ysin(theta);
	        	//theta from 0 to 180
	        	//solve for r for each edge element
	        	if(val<5){
	        		for(theta = 0 ; theta < 180; theta ++ ){
	        			r=hough_h+((double)xPixel-center_x)*Math.cos(theta*Math.PI/180)
	        				+((double)yPixel-center_y)*Math.sin(theta*Math.PI/180);
	        	//System.out.println(r);
	        			accumulator[theta][(int)r]++;
	        		}
	        	}

	        }
	    }
	huffAccumulator=accumulator;
	huff=true;
}
public void drawHuffLines(Graphics g, int threshhold){
	Color color;
	int h = image2.getHeight();
	int w = image2.getWidth();
	
	int val;
	int r;
	int theta;
	double hough_h = (((Math.sqrt(2.0))*((double)(h>w?h:w)))/2);
	int acc_width = (int)(hough_h * 2.0)+1;
	/*for(theta = 0; theta < 180; theta ++){
		for(r = 0; r< acc_width; r++){
			System.out.print(huffAccumulator[theta][r]
			+" ");
	
		}
		System.out.println("\n\nendline\n\n");
	}*/
	for(theta = 0; theta < 180; theta ++){
		for(r = 0; r< acc_width; r++){
			if(huffAccumulator[theta][r]>threshhold)
				drawRThetaLine((int)(r),theta,g, acc_width);
		}
	}
}
public void drawRThetaLine(int r, int theta, Graphics g,int acc_width){
	double x1,y1,x2,y2; 
	int h = image2.getHeight();
	int w = image2.getWidth();
	double center_x=w/2;
	double center_y = h/2;
	//find x,y by going in the perpendicular direction from the rtheta direction
	if(theta>=45&&theta<=135){
			x1 = 0; 
			x2 = w;
			//y=(r-xcos(theta))/sin(theta)
			y1=((double)(r-(acc_width/2))-(x1-center_x)*Math.cos(theta*(Math.PI/180)))/Math.sin(theta*(Math.PI/180))+center_y;
			y2=((double)(r-(acc_width/2))-(x2-center_x)*Math.cos(theta*(Math.PI/180)))/Math.sin(theta*(Math.PI/180))+center_y;
			g.drawLine((int)x1,(int)y1, (int)x2, (int)y2);
	}
	else {
			y1 = 0; 
			y2 = h;
			//x = (r - y sin(t)) / cos(t);  
			x1=((double)(r-(acc_width/2))-(y1-center_y)*Math.sin(theta*(Math.PI/180)))/Math.cos(theta*(Math.PI/180))+center_x;
			x2=((double)(r-(acc_width/2))-(y2-center_y)*Math.sin(theta*(Math.PI/180)))/Math.cos(theta*(Math.PI/180))+center_x;
			g.drawLine((int)x1,(int)y1, (int)x2, (int)y2);

	}
}

}