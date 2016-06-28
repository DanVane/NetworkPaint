/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bp;

/**
 *
 * @author wangdan
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.DynamicBackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;



public class BP {
    public static void main(String[] args) throws Exception {
    	gendata();
        ArrayList<String> content=readFile("data.dat");
        TrainingSet trainingSet = new TrainingSet(2, 1);
        for(int i=0;i<content.size();i++)
        { 
        	String[] t=content.get(i).split(",");
        	double di=Double.parseDouble(t[0]);
        	double dj=Double.parseDouble(t[1]);
        	double r=Double.parseDouble(t[2]);       
        trainingSet.addElement(new SupervisedTrainingElement(new double[]{di, dj}, new double[]{r}));
        }
       
        MultiLayerPerceptron network = new MultiLayerPerceptron(TransferFunctionType.TANH, 2,5,5,1);
        
        DynamicBackPropagation train = new DynamicBackPropagation();
        train.setNeuralNetwork(network);
        network.setLearningRule(train);
        drawpic(trainingSet,0,network);
        int epoch = 1;
        do
        {
        	train.doOneLearningIteration(trainingSet);
        		
        	System.out.println("Epoch " + epoch + ", error=" + train.getTotalNetworkError());
        	epoch++;
        	if(epoch%100==0)
        		drawpic(trainingSet,epoch,network);
        	
        } while(train.getTotalNetworkError()>0.1);
        drawpic(trainingSet,epoch,network);
        
        System.out.println("Neural Network Results:");
        
        
        for(TrainingElement element : trainingSet.trainingElements()) {
        	network.setInput(element.getInput());
            network.calculate();
            Vector<Double> output = network.getOutput();
            SupervisedTrainingElement ste = (SupervisedTrainingElement)element;
            
			System.out.println(element.getInput().get(0) + "," + element.getInput().get(1)
					+ ", actual=" + output.get(0) + ",ideal=" + ste.getDesiredOutput().get(0));
		}


    }
    
    public static void drawpic(TrainingSet trainset,int m,MultiLayerPerceptron network) throws Exception
	{
		 int width = 400;   
	        int height = 600;
		 File file = new File("pic\\3image"+m+".jpg");     
	        Font font = new Font("Serif", Font.BOLD, 0);   
	        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);   
	        Graphics2D g2 = (Graphics2D)bi.getGraphics();   
	        g2.setBackground(Color.WHITE);   
	        g2.clearRect(0, 0, width, height);  
	        MultiLayerPerceptron network1 =network ;
	        for(TrainingElement element : trainset.trainingElements()) {
	        	   network1.setInput(element.getInput());
	               network1.calculate();
	   	        Vector<Double> output = network1.getOutput();
	            SupervisedTrainingElement ste = (SupervisedTrainingElement)element;
	            if(Math.abs( network1.getOutput().get(0)-1)<Math.abs(network1.getOutput().get(0)-0))
	            {
	            	Color c=Color.black;
	            	g2.setPaint(c);   
	            	double i=(element.getInput().get(0));
	            	int ii=(int)(i);
	            	double j=(element.getInput().get(1));
	            	int jj=(int)(j);
	            	if(m==0)
	            	System.out.println(ii+","+jj);
			        g2.fillRect(jj*100+50, ii*100+50, 100, 100); 
	            }
			}
	        ImageIO.write(bi, "jpg", file); 
	       
	}
    
    public static ArrayList<String> readFile (String path) throws Exception
	{
		ArrayList<String> result = new ArrayList<String>();
		
		File f = new File(path);
		FileInputStream fis = new FileInputStream(f);
		InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
		BufferedReader br = new BufferedReader(isr);
		String line = "";
		while((line=br.readLine())!=null&&line.trim().length()>0)
		{
			line = line.trim();
			result.add(line);
		}
		
		br.close();
		isr.close();
		fis.close();
		return result;
	}

	public static void gendata() throws Exception
	{
		double[][] data=new double[][]{{1.0,1.0,1.0},{0.0,0.0,1.0},{1.0,1.0,1.0},{0.0,0.0,1.0},{1.0,1.0,1.0}};
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<data.length;i++)
			for(int j=0;j<data[0].length;j++)
				buf.append(i+","+j+","+data[i][j]+"\n");
		writeFile("data.dat", buf.toString());
				
	}

	public static void writeFile(String path, String content) throws Exception
	{
		File f = new File(path);
		FileOutputStream fos = new FileOutputStream(f);
		OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
		BufferedWriter bw = new BufferedWriter(osw);
		bw.write(content);
		bw.close();
		osw.close();
		fos.close();
	}
}