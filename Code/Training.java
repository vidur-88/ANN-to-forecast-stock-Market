/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stockforecast;

import java.io.*;
import java.util.*;

class MultiLayerneuNet {

    double[][][] weight = new double[10][20][20];		// for storing weights
    double[][] theta = new double[10][20];			// theta
    double[][][] dw = new double[10][20][20];			// delta w for adjusting weights
    double[][] dth = new double[10][20];			// delta theta for adjstng theta
    double[][] ho = new double[10][20];				// output of hiiden layers n output layers
    double[] err  = new double[20];				// error
    double[] grd  = new double[20];				// gredient
    double alpha = 0.8;   					// learning rate
    int countEpoch = 0;			// counting epochs
    int hidLayers;			// number of hidden layers
    int outNeurons;			// number of output neurons
    int inpNeurons;			// number of input neurons
    int numoftset;			// number of test data
    double [][] inpT = new double[5000][5];			// input data
    double [][] outT = new double[5000][5];			// output data
    int [] hidTemp = new int[10];				// storing neurons in each hidden layers


    void fillRandWeight()
    {
        Random rnd = new Random();
        for(int i = 0; i <10; i++ ) {
            for(int j = 0; j < 20; j++) {
                for (int k = 0; k < 20; k++) {
                    weight[i][j][k] = rnd.nextDouble() - 0.5;
                }
            }
        }
    //   weight[0][0][0] = 0.5; weight[0][0][1] = 0.9; weight[0][1][0] = 0.4; weight[0][1][1] = 1.0;
    //   weight[1][0][0] = -1.2; weight[1][1][0] = 1.1;
    }
    void fillRandTheta()
    {
        Random rnd = new Random();
        for(int i = 0; i < 10; i++) {
            for (int j = 0; j < 20; j++) {
                theta[i][j] = rnd.nextDouble() - 0.5;
            }
        }
      //  theta[0][0] = 0.8; theta[0][1] = -0.1; theta[1][0] = 0.3;
    }

    void showWeightMatrix(int inpN, int outN, int[] hid)
    {
        int hidLayers = hid.length;
        for(int i = 0; i < hidLayers + 1; i++ ) {
                int currN = i==0 ? inpN : hid[i-1];
                int nextN = i== hidLayers ? outN : hid[i];
                for (int j = 0; j < currN; j++) {
                    for (int k = 0; k < nextN; k++) {
                        System.out.print(weight[i][j][k] + " ");
                    }
                    System.out.println();
                }
        }
    }

    double calculateError(double[] inp, double[] out, int[] hid)
    {

        for(int i = 0; i < hidLayers + 1; i++) {
            int bound = (i == hidLayers) ? outNeurons : hid[i];
            for(int j = 0; j < bound; j++) {
                int prevN = i == 0 ? inpNeurons : hid[i-1] ;
                double s = 0.0;
                if(i == 0) {
                    for(int k = 0; k < prevN; k++) {
                        s += inp[k]*weight[i][k][j];
                    }
                }
                else {
                    for(int k = 0; k < prevN; k++) {
                        s += ho[i-1][k]*weight[i][k][j];
                    }
                }
                s = s - theta[i][j];
                ho[i][j] = 1 / ( 1 + Math.exp(-s));
            }
        }
        double sumofError = 0.0;
        for(int i = 0; i < outNeurons; i++) {
            err[i] = out[i] - ho[hidLayers][i];
            sumofError += err[i]*err[i];
        }
        return sumofError;
    }

    double testing(double[] inp, int[] hid)			// to test learning
    {

        for(int i = 0; i < hidLayers + 1; i++) {
            int bound = (i == hidLayers) ? outNeurons : hid[i];
            for(int j = 0; j < bound; j++) {
                int prevN = i == 0 ? inpNeurons : hid[i-1] ;
                double s = 0.0;
                if(i == 0) {
                    for(int k = 0; k < prevN; k++) {
                        s += inp[k]*weight[i][k][j];
                    }
                }
                else {
                    for(int k = 0; k < prevN; k++) {
                        s += ho[i-1][k]*weight[i][k][j];
                    }
                }
                s = s - theta[i][j];
                ho[i][j] = 1 / ( 1 + Math.exp(-s));
            }
        }
        return ho[hidLayers][0];
    }

    void calcErrGradientAndDelta(double[] inp, double[] out, int[] hid)
    {

        for(int i = hidLayers; i >= 0; i--) {                   // backprapogation Algorithm
            int N = (i == hidLayers) ? outNeurons : hid[i];
            for(int j = 0; j < N; j++) {
                if(i==hidLayers) {
                    grd[j] = ho[i][j]*(1 - ho[i][j])*err[j];
                }
                else {
                    int M = (i == hidLayers-1) ? outNeurons : hid[i+1];
                    double prd = 0.0;
                    for(int k = 0; k < M; k++) {
                       prd += weight[i][j][k]*grd[k];
                    }
                    grd[j] = ho[i][j]*(1 - ho[i][j])*prd;
                }
                int PrevN = (i == 0) ? inpNeurons : hid[i-1];
                for(int k = 0; k < PrevN; k++) {
                    if(i > 0) {
                        dw[i][k][j] = alpha * grd[j] * ho[i-1][k];
                    }
                    else {
                        dw[i][k][j] = alpha * grd[j] * inp[k];
                    }
                }
                dth[i][j] = alpha * (-1) * grd[j];
            }
        }
    }
    void updateWeight(double[] inp, double[] out, int[] hid)
    {

        for (int i = 0; i < hidLayers + 1; i++) {
            int currN = (i == 0) ? inpNeurons : hid[i-1];
            int nextN = (i == hidLayers) ? outNeurons : hid[i];
            for(int j = 0; j < currN; j++) {
                for(int k = 0; k < nextN; k++) {
                    weight[i][j][k] += dw[i][j][k];
                }
            }
        }
        for(int i = 0; i < hidLayers + 1 ; i++) {
            int N = (i == hidLayers) ? outNeurons : hid[i];
            for(int j = 0; j < N; j++) {
                theta[i][j] += dth[i][j];
            }
        }
    }

    double trainNetw(double[] inp, double[] out, int[] hid)
    {
            double soerr = calculateError(inp, out, hid);
            calcErrGradientAndDelta(inp, out, hid);
            updateWeight(inp, out, hid);
           // showWeightMatrix(inp.length, out.length, hid);
            return soerr;
    }

    void writeTrainedDatainFile()
    {
	    try {
		    BufferedWriter out = new BufferedWriter(new FileWriter("adjstWeight.txt"));

		    out.write(inpNeurons + "\n");
		    out.write(outNeurons + "\n");
		    out.write(hidLayers + "\n");
		    for (int i = 0; i < hidLayers; i++) out.write(hidTemp[i] + "\n");

        	    for(int i = 0; i < hidLayers + 1; i++ ) {
                    int currN = i==0 ? inpNeurons : hidTemp[i-1];
                    int nextN = i== hidLayers ? outNeurons : hidTemp[i];
                    for (int j = 0; j < currN; j++) {
                         for (int k = 0; k < nextN; k++) {
                              out.write(weight[i][j][k] + "\n");
                         }
                    }
	            out.close();
        	}
	    }
	    catch (Exception e) {
		   System.out.println("write error");
	    }
    }

    void readTrainedDatafromFile()
    {
	    try {
	    	BufferedReader in = new BufferedReader(new FileReader("adjstWeight.txt"));
		String str = in.readLine();  inpNeurons = Integer.parseInt(str);
		str = in.readLine();  outNeurons = Integer.parseInt(str);
		str = in.readLine();  hidLayers = Integer.parseInt(str);

		for (int i = 0; i < hidLayers; i++) {
			str = in.readLine();
			System.out.println(str);
			hidTemp[i] = Integer.parseInt(str);
		}
        	for(int i = 0; i < hidLayers + 1; i++ ) {
        	   int currN = i==0 ? inpNeurons : hidTemp[i-1];
             	   int nextN = i== hidLayers ? outNeurons : hidTemp[i];
                   for (int j = 0; j < currN; j++) {
                       for (int k = 0; k < nextN; k++) {
                            str = in.readLine();
			    weight[i][j][k] = Double.valueOf(str).doubleValue();
                       }
                   }
		}
		in.close();
	    }
	    catch (Exception e) {
		    //System.out.println("read error");
	    }
     }
    
     public class thread1 implements Runnable{
       public void run(){
            double t_err = 100.0;
            while(t_err > 0.01 )
            {
                t_err = 0.0;
                for(int i = 0; i < numoftset; i++) {
                    t_err +=  trainNetw(inpT[i],outT[i],hidTemp);
                }
                countEpoch++;
                System.out.println("Epochs : " + countEpoch);
                if(countEpoch > 10000) break;

                UserInterface.jProgressBar1.setValue(countEpoch/100); //Set value
                UserInterface.jProgressBar1.repaint(); //Refresh graphics
                UserInterface.jProgressBar1.setStringPainted(true);
                try{Thread.sleep(0);} //Sleep 50 milliseconds
                catch (InterruptedException err){}
            }
        }
     }
}

class Training {

    double [] vPrice = new double[5000];
    static double [] aPrice = new double[5000];
    static double [] pPrice = new double[5000];
    static int sz = 0;
    double mxn = 0.0;

    void readData(String fileLocation)
    {
        //setInitialValues();
        MultiLayerneuNet ntw = new MultiLayerneuNet();
        ntw.inpNeurons = 4;
        ntw.outNeurons = 1;
        ntw.hidLayers = 1;
   //    for(int i = 0; i < ntw.hidLayers; i++) {
           ntw.hidTemp[0] = 9;
           //ntw.hidTemp[1] = 4;
     // }
     
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileLocation));
            String str;
            
            while ((str = br.readLine()) != null) {
                String [] stockPrice = str.split(" ");
                vPrice[sz++] = (new Double(stockPrice[1]));
                mxn = Math.max(Double.parseDouble(stockPrice[1]),mxn);
            }
            for (int i = 0; i < sz; i++ ) {
                vPrice[i] = (vPrice[i]/mxn)*0.8+0.1;
                System.out.print(" size = " + vPrice[i]);
            }

            ntw.numoftset = sz - ntw.inpNeurons;
            System.out.println("Enter Number of Input Neurons : " + ntw.numoftset);
            
            for( int i = 0; i < ntw.numoftset; i++ ) {
             //   br = new BufferedReader(new FileReader(fileLocation));
                int j;
                for(j = 0; j < ntw.inpNeurons; j++) {
                  //  str = br.readLine();
                  //  String [] stockPrice = str.split(" ");
                  //  System.out.print(str);
                    ntw.inpT[i][j] = vPrice[i+j];
                 //   System.out.println(stockPrice[1]);
                 //   System.out.println(ntw.inpT[i][j]);
                }
                for(int k = 0; k < ntw.outNeurons; k++) {
                    ntw.outT[i][k] = vPrice[i+j];
                }
            }
        }
        catch (Exception E) {
            System.out.println("Cant Open File");
        }
        ntw.fillRandWeight();	// fill rand weight
        ntw.fillRandTheta();	// fill rand theta
      
        new Thread(ntw.new thread1()).start();
        for(int i = 0; i < ntw.numoftset; i++) {
            pPrice[i] = ntw.testing(ntw.inpT[i],ntw.hidTemp);
            aPrice[i] = vPrice[i+5];
        }
    }

    void DrawStockGraph()
    {
          PredictionGraph pg = new PredictionGraph();
          pg.drawGraph(aPrice, pPrice, sz-5);
          System.out.println("size = " + sz);
    }
}
