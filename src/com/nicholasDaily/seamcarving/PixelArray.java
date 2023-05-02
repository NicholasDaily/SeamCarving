package com.nicholasDaily.seamcarving;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class PixelArray {
	private int[] pixelArray;
	private int width;
	
	PixelArray(int[] pixelArray, int width){
		this.pixelArray = pixelArray;
		this.width = width;
	}
	
	PixelArray(BufferedImage img){
		pixelArray = new int[img.getWidth() * img.getHeight()];
		img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixelArray, 0, img.getWidth());
		this.width = img.getWidth();
	}
	
	public int getPixel(int x, int y){
		if(x > width) 
			throw new IndexOutOfBoundsException("Invalid X for grid");
		if(y > pixelArray.length / width - 1) 
			throw new IndexOutOfBoundsException("Invalid Y for grid");
		int index = (width * y) + x;
		return pixelArray[index];
	}

	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.pixelArray.length / this.width;
	}
	
	public int[] getPixels() {
		return this.pixelArray;
	}
	
	
	public void setPixel(int x, int y, int val) {
		if(x > width) 
			throw new IndexOutOfBoundsException("Invalid X for grid");
		if(y > pixelArray.length / width - 1) 
			throw new IndexOutOfBoundsException("Invalid Y for grid");
		int index = (width * y) + x;
		pixelArray[index] = val;
	}
	
	public void applyKernel(double[] kernel) throws Exception {
		if(!(kernel.length == 9)) {
			throw new Exception("Kernel length must be 9");
		}
		
			for(int i = 0; i < this.getHeight(); i++) {
				for(int j = 0; j < this.width; j++) {
					int[] x;
					int newRGB;
					if(j == 0 && i == 0) {
						//if at top left corner of image
						int currentRGB = (this.getPixel(j, i) & 255) ;
						x = new int[] {currentRGB, currentRGB, (this.getPixel(j + 1, i) & 255) , 
										currentRGB, currentRGB, (this.getPixel(j + 1, i) & 255) ,
										(this.getPixel(j, i + 1) & 255) , (this.getPixel(j, i + 1) & 255) , (this.getPixel(j + 1, i + 1) & 255)  };
						//System.out.println((this.getPixel(j, i) & 255));
					}else if (j == this.width - 1 && i == 0) {
							//if at top right corner of image
							x = new int[] 
									{(this.getPixel(j - 1, i) & 255) , (this.getPixel(j, i) & 255) , (this.getPixel(j, i) & 255) ,
									(this.getPixel(j - 1, i) & 255) , (this.getPixel(j, i) & 255) , (this.getPixel(j, i) & 255) ,
									(this.getPixel(j - 1, i + 1) & 255) , (this.getPixel(j, i + 1) & 255) , (this.getPixel(j, i + 1) & 255) };
					}else if(j == 0 && i == this.getHeight() - 1) {
						//if at bottom left
						x = new int[] 
								{(this.getPixel(j, i - 1) & 255) , (this.getPixel(j, i - 1) & 255) , (this.getPixel(j + 1, i - 1) & 255) ,
								 (this.getPixel(j, i) & 255) , (this.getPixel(j, i) & 255) , (this.getPixel(j + 1, i) & 255) ,
								 (this.getPixel(j, i) & 255) , (this.getPixel(j, i) & 255) , (this.getPixel(j + 1, i) & 255) };
					}else if(j == this.width - 1 && i == this.getHeight() - 1) {
						//if at bottom right
						x = new int[]
								{(this.getPixel(j - 1, i - 1) & 255) , (this.getPixel(j, i - 1) & 255) , (this.getPixel(j, i - 1) & 255) ,
								 (this.getPixel(j - 1, i) & 255) , (this.getPixel(j, i) & 255) , (this.getPixel(j, i) & 255) ,
								 (this.getPixel(j - 1, i) & 255) , (this.getPixel(j, i) & 255) , (this.getPixel(j, i) & 255) };
					}else if(j == 0) {
						//if just on the left
						x = new int[]
								 {(this.getPixel(j, i - 1) & 255) , (this.getPixel(j, i - 1) & 255) , (this.getPixel(j + 1, i - 1) & 255) ,
								 (this.getPixel(j, i) & 255) , (this.getPixel(j, i) & 255) , (this.getPixel(j + 1, i) & 255) ,
								 (this.getPixel(j, i + 1) & 255) , (this.getPixel(j, i + 1) & 255) , (this.getPixel(j + 1, i + 1) & 255) };
					}else if(j == this.width - 1) {
						//if just on right
						x = new int[] 
								{(this.getPixel(j - 1, i - 1) & 255) , (this.getPixel(j, i - 1) & 255) , (this.getPixel(j, i - 1) & 255) ,
								(this.getPixel(j - 1, i) & 255) , (this.getPixel(j, i) & 255) , (this.getPixel(j, i) & 255) ,
								(this.getPixel(j - 1, i + 1) & 255) , (this.getPixel(j, i + 1) & 255) , (this.getPixel(j, i + 1) & 255) };
					}else if(i == 0) {
						//if just on top
						x = new int[] 
								{(this.getPixel(j - 1, i) & 255) , (this.getPixel(j, i) & 255) , (this.getPixel(j + 1, i) & 255) ,
								 (this.getPixel(j - 1, i) & 255) , (this.getPixel(j, i) & 255) , (this.getPixel(j + 1, i) & 255) ,
								 (this.getPixel(j - 1, i + 1) & 255) , (this.getPixel(j, i + 1) & 255) , (this.getPixel(j + 1, i + 1) & 255) };
					}else if(i == this.getHeight() - 1) {
						//if just on bottom
						x = new int[] 
							{(this.getPixel(j - 1, i - 1) & 255) , (this.getPixel(j, i - 1) & 255) , (this.getPixel(j + 1, i - 1) & 255) ,
							 (this.getPixel(j - 1, i) & 255) , (this.getPixel(j, i) & 255) , (this.getPixel(j + 1, i) & 255) ,
							 (this.getPixel(j - 1, i) & 255) , (this.getPixel(j, i) & 255) , (this.getPixel(j + 1, i) & 255) };
					}else {
						//if any pixel not on the edge
						x = new int[]
								{(this.getPixel(j - 1, i - 1) & 255) , (this.getPixel(j, i - 1) & 255) , (this.getPixel(j + 1, i - 1) & 255) ,
								(this.getPixel(j - 1, i) & 255) , (this.getPixel(j, i) & 255) , (this.getPixel(j + 1, i) & 255) ,
								(this.getPixel(j - 1, i + 1) & 255) , (this.getPixel(j, i + 1) & 255) , (this.getPixel(j + 1, i + 1) & 255) ,};
					}
					newRGB = multiplyMatrix(x, kernel);
					newRGB = newRGB < 0 ? 0 : newRGB;
					newRGB = newRGB > 255 ? 255 : newRGB;
					this.setPixel(j, i, (255 << 24) | (newRGB << 16) | (newRGB << 8) | newRGB);
				}
			}
	}
	
	public static PixelArray seamCarve(int scale, char dir, PixelArray originalImage, double[][] kernelArray, boolean applyGreyScale) throws Exception {
		BufferedImage img;
		PixelArray copyOf;
		
		if(dir != 'v') { 
			img = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
			img.setRGB(0, 0, originalImage.getWidth(), originalImage.getHeight(), originalImage.getPixels(), 0, originalImage.getWidth());
			BufferedImage tempImg = rotateImage(img, 90);
			int[] pxls = new int[tempImg.getWidth() * tempImg.getHeight()];
			tempImg.getRGB(0, 0, tempImg.getWidth(), tempImg.getHeight(), pxls, 0, tempImg.getWidth());
			copyOf = new PixelArray(pxls, tempImg.getWidth());
		}
		else copyOf = new PixelArray(originalImage.getPixels(), originalImage.getWidth());
		int startWidth = copyOf.getWidth();
			
			PixelArray greyScale = applyGreyScale ? copyOf.getGreyscale() : new PixelArray(copyOf.getPixels(), copyOf.getWidth());
			for(int i = 0; i < kernelArray.length; i++) {
				greyScale.applyKernel(kernelArray[i]);
			}
			
			while(copyOf.width > Math.floor(startWidth * ((double)scale / 100))) {
				
				PixelArray powerMap = greyScale.getPowerMap(false);
				int[] indeciesForDeletion = copyOf.getIndeciesForDeletionFromPowerMap(powerMap);
				greyScale.replaceWithRed(indeciesForDeletion);
				BufferedImage image = new BufferedImage(copyOf.getWidth(), copyOf.getHeight(), BufferedImage.TYPE_INT_ARGB);
				image.setRGB(0, 0, copyOf.getWidth(), copyOf.getHeight(), greyScale.getPixels(), 0, copyOf.getWidth());
				copyOf.removeIndecies(indeciesForDeletion);
				greyScale.removeIndecies(indeciesForDeletion);
			}
			if(dir != 'v') { 
				img = new BufferedImage(copyOf.getWidth(), copyOf.getHeight(), BufferedImage.TYPE_INT_ARGB);
				img.setRGB(0, 0, copyOf.getWidth(), copyOf.getHeight(), copyOf.getPixels(), 0, copyOf.getWidth());
				BufferedImage tempImg = rotateImage(img, 270);
				int[] pxls = new int[tempImg.getWidth() * tempImg.getHeight()];
				tempImg.getRGB(0, 0, tempImg.getWidth(), tempImg.getHeight(), pxls, 0, tempImg.getWidth());
				copyOf = new PixelArray(pxls, tempImg.getWidth());
			}
		return copyOf;
	}
	
	public static BufferedImage rotateImage(BufferedImage original, int degs) {
		double rads = Math.toRadians(degs);
		double sin = Math.abs(Math.sin(rads));
		double cos = Math.abs(Math.cos(rads));
		int w = (int) Math.floor(original.getWidth() * cos + original.getHeight() * sin);
		int h = (int) Math.floor(original.getHeight() * cos + original.getWidth() * sin);
		BufferedImage rotatedImage = new BufferedImage(w, h, original.getType());
		AffineTransform at = new AffineTransform();
		at.translate(w / 2, h / 2);
		at.rotate(rads, 0, 0);
		at.translate(-original.getWidth() / 2, -original.getHeight() / 2);
		AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		rotateOp.filter(original, rotatedImage);
		return rotatedImage;
	}
	
	public PixelArray getGreyscale() {
		int[] pixels = new int[pixelArray.length];
		for(int i = 0; i < pixelArray.length; i++) {
			int r = (pixelArray[i] & 255) >> 16;
			int g = (pixelArray[i] & 255) >> 8;
			int b = (pixelArray[i] & 255);
			int average = ((r + g + b) / 3);
			pixels[i] = (255 << 24) | (average << 16) | (average << 8) | average;
			
		}
		return new PixelArray(pixels, this.width);
	}
	
	public PixelArray getPowerMap(boolean z) {
		PixelArray powerMap = new PixelArray(new int[this.pixelArray.length], this.width);
		
			for(int i = 0; i < this.width; i++) {
				powerMap.setPixel(i, powerMap.getHeight() - 1, pixelToPowerVal(getPixel(i, getHeight() - 1) & 255, z));
			}
			for(int y = powerMap.getHeight() - 2; y >= 0; y--) {
				for(int x = 0; x < powerMap.getWidth(); x++) {
					int currentPower = pixelToPowerVal(getPixel(x, y) & 255, z);
					if(x == 0) {
						if(powerMap.getPixel(x, y + 1) > powerMap.getPixel(x + 1, y + 1)) {
							currentPower += powerMap.getPixel(x + 1, y + 1);
						}else {
							currentPower += powerMap.getPixel(x, y + 1);
						}
					}else if(x == powerMap.getWidth() - 1) {
						if(powerMap.getPixel(x, y + 1) > powerMap.getPixel(x - 1, y + 1)) {
							currentPower += powerMap.getPixel(x - 1, y + 1);
						}else {
							currentPower += powerMap.getPixel(x, y + 1);
						}
					}else {
						if(powerMap.getPixel(x, y + 1) > powerMap.getPixel(x - 1, y + 1)) {
							if(powerMap.getPixel(x - 1, y + 1) > powerMap.getPixel(x + 1, y + 1)) {
								currentPower += powerMap.getPixel(x + 1, y + 1);
							}else {
								currentPower += powerMap.getPixel(x - 1, y + 1);
							}
						}else {
							if(powerMap.getPixel(x, y + 1) > powerMap.getPixel(x + 1, y + 1)) {
								currentPower += powerMap.getPixel(x + 1, y + 1);
							}else {
								currentPower += powerMap.getPixel(x, y + 1);
							}
						}
					}
					powerMap.setPixel(x, y, currentPower);
				}
			}
		return powerMap;
	}
	
	public int[] getIndeciesForDeletionFromPowerMap(PixelArray powerMap) {
		int[] indecies;
		
			indecies = new int[powerMap.getHeight()];
			int capturedIndecies = 0;
			int previousLowestX = 0;
			int previousLowestY = 0;
			int lowestPower = powerMap.getPixel(0, 0);
			for(int i = 0; i < powerMap.getWidth(); i++) {
				int currentPowerVal = powerMap.getPixel(i, 0);
				if(currentPowerVal < lowestPower) {
					lowestPower = currentPowerVal;
					previousLowestX = i;
					previousLowestY = 0;
				}
			}
			indecies[capturedIndecies] = powerMap.getIndexFromPoint(previousLowestX, previousLowestY);
			capturedIndecies++;
			
			while(capturedIndecies < indecies.length) {
				int lowestIndecie;
				if(previousLowestY + 1 == powerMap.getHeight()) break;
				if(previousLowestX + 1 >= powerMap.getWidth()) {
					if(powerMap.getPixel(previousLowestX, previousLowestY + 1) > powerMap.getPixel(previousLowestX - 1, previousLowestY + 1)) {
						lowestIndecie =powerMap.getIndexFromPoint(previousLowestX - 1, previousLowestY + 1);
						previousLowestX -= 1;
					}else {
						lowestIndecie = powerMap.getIndexFromPoint( previousLowestX, previousLowestY + 1);
					}
				}else if(previousLowestX - 1 < 0) {
					if(powerMap.getPixel(previousLowestX, previousLowestY + 1) > powerMap.getPixel(previousLowestX + 1, previousLowestY + 1)) {
						lowestIndecie = powerMap.getIndexFromPoint(previousLowestX + 1, previousLowestY + 1);
						previousLowestX += 1;
					}else {
						lowestIndecie =powerMap.getIndexFromPoint( previousLowestX, previousLowestY + 1);
					}
				}else {
					if(powerMap.getPixel(previousLowestX, previousLowestY + 1) > powerMap.getPixel(previousLowestX + 1, previousLowestY + 1)) {
						if(powerMap.getPixel(previousLowestX + 1, previousLowestY + 1) > powerMap.getPixel(previousLowestX - 1, previousLowestY + 1)) {
							lowestIndecie = powerMap.getIndexFromPoint(previousLowestX - 1, previousLowestY + 1);
							previousLowestX = previousLowestX - 1;
						}else {
							lowestIndecie = powerMap.getIndexFromPoint(previousLowestX + 1, previousLowestY + 1);
							previousLowestX = previousLowestX + 1;
						}
					}else {
						if(powerMap.getPixel(previousLowestX, previousLowestY + 1) > powerMap.getPixel(previousLowestX - 1, previousLowestY + 1)) {
							lowestIndecie = powerMap.getIndexFromPoint(previousLowestX - 1, previousLowestY + 1);
							previousLowestX = previousLowestX - 1;
						}else {
							lowestIndecie = powerMap.getIndexFromPoint(previousLowestX, previousLowestY + 1);
						}
					}
				}
				indecies[capturedIndecies] = lowestIndecie;
				capturedIndecies++;
				previousLowestY++;
			}
		return indecies;
	}
	
	public int getIndexFromPoint(int x, int y) {
		return this.getWidth() * y + x;
	}
	
	public static int pixelToPowerVal(int x, boolean z) {
		int power;
		if(x == 0) {
			power = 0;
		}else if(x > 0 && x <= 51) {
			power = 10;
		}else if(x > 51 && x <= 102) {
			power = 20;
		}else if(x > 102 && x <= 153) {
			power = 30;
		}else if(x > 153 && x <= 204) {
			power = 40;
		}else {
			power = 50;
		}
		power = x;
		power = z ? Math.abs(power - 255) : power; //switches weather white or black gets a higher power value
		return power;
	}
	
	public void removeIndecies(int[] indecies) {
		int[] newArr;
		int newWidth = this.width;
		newWidth--;
		newArr = new int[(newWidth) * this.getHeight()];
		
		int index = 0;
		
		for(int i = 0; i < this.pixelArray.length; i++) {
			if(index == indecies.length  || i != indecies[index]) {
				newArr[i - index] = this.pixelArray[i];
			}else {
				index++;
			}
		}
		this.width = newWidth;
		this.pixelArray = newArr;
		
	}
	
	public void replaceWithRed(int[] indecies) {
		int[] newArr = new int[pixelArray.length];
		
		int index = 0;
		for(int i = 0; i < this.pixelArray.length; i++) {
			if(index == indecies.length - 1 || i != indecies[index]) {
				newArr[i] = this.pixelArray[i];
			}else {
				newArr[i] = (255 << 24) | (255 << 16) | (0 << 8) | 0;
				index++;
			}
		}
		this.pixelArray = newArr;
		
	}
	
	public static int multiplyMatrix(int[] x, double[] y) throws Exception{
		if(x.length != 9 || y.length != 9) {
			throw new Exception("lengths must both be 9");
		}
		int total = 0;
		for(int i = 0; i < 9; i++) {
			total += x[i] * y[i];
		}
		return total;
	}
	
	public BufferedImage getImage() {
		BufferedImage img;
		img = new BufferedImage(this.width, this.getHeight(), BufferedImage.TYPE_INT_ARGB);
		img.setRGB(0, 0, this.getWidth(), this.getHeight(), this.getPixels(), 0, this.getWidth());
		return img;
	}
	
	public String toString() {
		StringBuilder str = new StringBuilder();
		for(int i = 0; i < pixelArray.length;i++) {
			String substr = "" + pixelArray[i];
			while(substr.length() < 6) {
				substr = " " + substr;
			}
			str.append(substr);
			if((i + 1) % this.width == 0 && i != 0) str.append("\n");
		}
		return str.toString();
	}
}
