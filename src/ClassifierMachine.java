/**
/**
 * @author Nabeel Sarwar nsarwar@princeton.edu
 * 
    Copyright (C) <2014>  

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/
 *
 */
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.awt.Color;

import javax.imageio.ImageIO;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instance;
import weka.core.Instances;
 

/**
 * Data collection for the test image is mostly located in the constructor.
 * Improvement of the machine is done by adding correctly classified images in the appropiate folder.
 *
 */
public class ClassifierMachine {
	
	
	//the test image goes through a similar process to the 5 fold evaluation
	private final int AERO = 90238;
	private final int CHEMEX = 90239;
	private final int FRENCHPRESS = 90240;
	private final int OFFSET = 90238;
	
	private final String IMAGES_LOCATION = "images/TestIMG/";
	
	private ArrayList<BufferedImage> testimages;
	private ColorCollector data;
	private BufferedWriter output;
	private Color[][][] testimageDifferences; //dimensions of [number of categories][size of the categories][normalized width of images]
	//note, normalization is not used right now much
	
	public ClassifierMachine(BufferedImage testimage)
	{
		URL url = getClass().getResource(IMAGES_LOCATION);
		File folder = new File(url.getPath());
		File[] imageFiles = folder.listFiles();
		BufferedImage[] images = new BufferedImage[imageFiles.length];
		for (int i = 0; i < imageFiles.length; i++) {
			try {
				images[i] = ImageIO.read(imageFiles[i]);
			}

			catch (IOException exception) {
				System.out.println("Some file was moved.");
			}
			testimages.add(images[i]);
		}
		
		
		try
		{
			output = new BufferedWriter(new FileWriter( "output.txt"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		data = new ColorCollector();

		for (int i = 0; i < testimages.size(); i++)
		{
			testimageDifferences = data.test(testimages.get(i));
			
			try {
				output.write(imageFiles[i].getName() + "	" +  analyze( testimageDifferences, AERO) );
				output.write(imageFiles[i].getName() + "	" +  analyze( testimageDifferences, CHEMEX) );
				output.write(imageFiles[i].getName() + "	" +  analyze( testimageDifferences, FRENCHPRESS) );
			} catch (IOException e) {
				e.printStackTrace(); //this try/catch is here in case output was not able to be opened
			}
			
		}
		
		//should not need to need to write anymore if we are done analyzing
		try {
			output.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
	private boolean analyze(Color[][][] bigArrayOfDifferences, int whichArray) throws IOException //throws exception from output
	{
		boolean match = false; //burden of proof on us
		Color[][] testdifferences = bigArrayOfDifferences[whichArray-OFFSET]; //this is made to not be out of bounds by the constants
		Color[][] standard;
		
		/*
		 * BIG NOTE: MUST FIND A WAY TO GENERALIZE THIS. 
		 */
		switch (whichArray) 
		{
		
			case AERO:
				standard = data.getAero();
				break;
			case CHEMEX:
				standard = data.getChemex();
			case FRENCHPRESS:
				standard = data.getFrenchPress();
			default:
				output.write("Using Aero as default because of invalid input");
				standard = data.getAero();
				break;
		}
		//implement 90% confidence interval code
		return match;
	}
	
	/* might be useful later but I decided on another way to analyze equality
	public double distance (Color[][] test, Color[][] category) throws Exception
	{
		//problem of error hiding if there  are errors in the dimensions of width after this check
		//it is patched up a little right after
		if (test.length != category.length)
		{
			//incorrect number of folds as the data is matched across the data from the 5
			//fold evaluation procedure detailed in ColorCollector
			throw new Exception("Incorrect number of folds");
		}
		
		boolean correctWidth; //check that each fold has right number of regions
		
		for (int i =0; i < test.length; i ++)
		{
			correctWidth = test[i].length == category[i].length;
			if (!correctWidth)
			{
				throw new Exception("Fold #" + i + " has incorrect number of folds.");
			}
				
		}
		setTestImageDifferences( data.calculateDistance(testimage));
		double distance = 0;
		int numberOfFolds = test.length;
		double[] instancesRed = new double[numberOfFolds];
		double[] instancesGreen = new double[numberOfFolds];
		double[] instancesBlue =  new double[numberOfFolds];
		
		
		//precaution that will most likely never be used
		distance = distance > 0 ? distance : -1 * distance;
		return distance;
	}*/
	

}
