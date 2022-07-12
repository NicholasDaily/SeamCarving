package com.unitedhuskies.seamcarving;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class Canvas extends Component implements MouseListener, MouseMotionListener, MouseWheelListener{
	private BufferedImage imageReference;
	private boolean drawCrosshair = true;
	private int crosshairX = 0;
	private int crosshairY = 0;
	private int crosshairSize = 50;
	private int imageOffsetX = 0;
	private int imageOffsetY = 0;
	private double scaleFactor = 0;
	private Color paintBrushColor = Color.BLACK;
	
	Canvas(BufferedImage imageReference){
		this.imageReference = imageReference;
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}
	
	@Override
	public void paint(final Graphics g) {
		final Graphics2D g2d = (Graphics2D)g;
		if(imageReference != null) {
			BufferedImage imageToDraw = getImageToDraw();
			imageOffsetX = (this.getWidth() - imageToDraw.getWidth()) / 2;
			imageOffsetY = (this.getHeight() - imageToDraw.getHeight()) / 2;
			g2d.drawImage(imageToDraw, null, imageOffsetX, imageOffsetY);
			scaleFactor = (double)imageToDraw.getWidth() / (double)imageReference.getWidth();
		}
		
		if(drawCrosshair) {
			g2d.setColor(Color.RED);
			int scaledCrosshairSize = (int) (scaleFactor * crosshairSize);
			g2d.drawOval(crosshairX, crosshairY, scaledCrosshairSize, scaledCrosshairSize);
		}
		
	}
	
	public void setReferenceImage(BufferedImage imageReference) {
		this.imageReference = imageReference;
	}
	
	public BufferedImage getImage() {
		return imageReference;
	}
	
	public void setPaintbrushColor(Color c){
		this.paintBrushColor = c;
	}
	
	
	
	private BufferedImage getImageToDraw() {
		int parentContainerHeight = getHeight();
		int parentContainerWidth = getWidth();
		int imageHeight = imageReference.getHeight();
		int imageWidth =  imageReference.getWidth();
		
		
		int newImageWidth = parentContainerWidth;
		int newImageHeight = newImageWidth * imageHeight / imageWidth;
		Image img;
		if(newImageHeight <= parentContainerHeight) {
			img = imageReference.getScaledInstance(parentContainerWidth, -1, imageReference.SCALE_SMOOTH);
		}
		else {
			newImageWidth = parentContainerHeight * imageWidth / imageHeight;
			img = imageReference.getScaledInstance(newImageWidth, -1, imageReference.SCALE_SMOOTH);
		}
		BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D biGr = bi.createGraphics();
		biGr.drawImage(img, 0, 0, null);
		biGr.dispose();
		return bi;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		updateCrosshair();
		double parentContainerHeight = getHeight();
		double parentContainerWidth = getWidth();
		double imageHeight = imageReference.getHeight();
		double imageWidth =  imageReference.getWidth();
		double newImageWidth = parentContainerWidth;
		double newImageHeight = parentContainerWidth * imageHeight / imageWidth;
		Graphics2D g2d = imageReference.createGraphics();
		g2d.setColor(paintBrushColor);
		
		
		if((newImageHeight >= parentContainerHeight)) {
			newImageHeight = parentContainerHeight;
			newImageWidth = parentContainerHeight * imageWidth / imageHeight;
		}
		double imageWidthRatio =  imageWidth / newImageWidth;
		double imageHeightRatio = imageHeight / newImageHeight;
		int drawX = (int) ((crosshairX - imageOffsetX) * imageWidthRatio);
		int drawY = (int) ((crosshairY - imageOffsetY) * imageHeightRatio);
		g2d.fillOval(drawX, drawY, crosshairSize, crosshairSize);
		g2d.dispose();
		repaint();
	}
	
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {
		updateCrosshair();
		repaint();
	}
	
	private void updateCrosshair() {
		double scaledCrosshairSize = crosshairSize * scaleFactor;
		crosshairX =(int) (MouseInfo.getPointerInfo().getLocation().x - this.getLocationOnScreen().x - (scaledCrosshairSize / 2));
		crosshairY = (int) (MouseInfo.getPointerInfo().getLocation().y - this.getLocationOnScreen().y - (scaledCrosshairSize / 2));
	}

	@Override 
	public void mouseEntered(MouseEvent e) {
		drawCrosshair = true;
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		drawCrosshair = false;
		repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(e.getWheelRotation() > 0) {
			crosshairSize--;
		}else {
			crosshairSize++;
		}
		updateCrosshair();
		repaint();
	}
}
