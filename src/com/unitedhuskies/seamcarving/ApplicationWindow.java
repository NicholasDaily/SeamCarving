package com.unitedhuskies.seamcarving;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class ApplicationWindow extends JFrame {
	private enum ImageShown {
		ORIGINAL,
		FILTERED,
		IN_PROGRESS,
		CARVED
	};
	
	private String filePath;
	private String saveFilePath;
	private JFileChooser fileChooser = new JFileChooser();
	private Canvas imagePanel;
	private BufferedImage carvedImage;
	private BufferedImage originalImage;
	private BufferedImage filteredImage;
	private BufferedImage inProgressImage;
	private JCheckBox filterWhite;
	private JCheckBox applyKernels;
	private JCheckBox carveVertical;
	private JPanel kernelPanel;
	private JButton run;
	private ImageShown imageToShow = ImageShown.ORIGINAL;
	private JTextField scaleImageDownToPercentage;
	private ArrayList<JTextField[]> kernels = new ArrayList<JTextField[]>();
	int kernelRow = 0;
	private boolean isImageSelected = false;
	
	ApplicationWindow(){
		super("Seam Carving");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(500, 500);
		setLocationRelativeTo(null);
		setResizable(true);
		setUpComponents();
		addComponentListener(new ComponentAdapter() {
			public void componentResize(ComponentEvent e) {
				imagePanel.repaint();
			}
		});
		
		setVisible(true);
		
	}
	
	
	private void setUpComponents(){
		
		/*ETTING UP JMenuBar*/
		FileNameExtensionFilter filter = new FileNameExtensionFilter("jpg", "jpeg", "png");
		fileChooser.addChoosableFileFilter(filter);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem selectFile = new JMenuItem("Select File");
		//select file
		selectFile.addActionListener(e ->{
			fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
			int x = fileChooser.showOpenDialog(selectFile);
			if(x == fileChooser.APPROVE_OPTION) {
				filePath = fileChooser.getSelectedFile().getPath();
				updateImage();
				run.setEnabled(true);
				isImageSelected = true;
			}
		});
		
		file.add(selectFile);
		//save file
		JMenuItem saveFile = new JMenuItem("Save File");
		saveFile.addActionListener(e ->{
			fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
			int x = fileChooser.showSaveDialog(this);
			if(x == JFileChooser.APPROVE_OPTION) {
				saveFilePath = fileChooser.getSelectedFile().getPath();
				try {
					ImageIO.write(carvedImage, "PNG", new File(saveFilePath));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		saveFile.setEnabled(false);
		file.add(saveFile);
		menuBar.add(file);
		setJMenuBar(menuBar);
		
		BufferedImage placeHolderImage;
		try{
			placeHolderImage = ImageIO.read(new File("placeholder.jpg"));
		}catch(IOException e){
			placeHolderImage = originalImage;
		}
		imagePanel = new Canvas(placeHolderImage);
		add(imagePanel);
		JPanel controlsPanel = new JPanel();
		controlsPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, Color.BLACK));
		controlsPanel.setLayout(new BorderLayout());
		JPanel checkBoxes = new JPanel();
		checkBoxes.setLayout(new GridLayout(0, 1));
		
		filterWhite = new JCheckBox("Filter White", true);
		filterWhite.addActionListener(e->{
			if(filterWhite.isSelected()) imagePanel.setPaintbrushColor(Color.BLACK);
			else imagePanel.setPaintbrushColor(Color.WHITE);
		});
		applyKernels = new JCheckBox("Apply Kernels", true);
		applyKernels.addActionListener(e->applyFilters());
		carveVertical = new JCheckBox("Shrink Vertically", true);
		JLabel scaleImageDownToPercentageLabel = new JLabel("Size to shrink image down to (%): ");
		scaleImageDownToPercentage = new JTextField("65");
		JLabel filteringOptionsLabel = new JLabel("Filtering Options");
		controlsPanel.add(filteringOptionsLabel, BorderLayout.NORTH);
		checkBoxes.add(filterWhite);
		checkBoxes.add(applyKernels);
		checkBoxes.add(carveVertical);
		checkBoxes.add(scaleImageDownToPercentageLabel);
		checkBoxes.add(scaleImageDownToPercentage);
		
		JPanel centerControlPanel = new JPanel();
		centerControlPanel.setLayout(new BorderLayout());
		centerControlPanel.add(checkBoxes, BorderLayout.NORTH);
		
		kernelPanel = new JPanel();
		kernelPanel.setLayout(new GridBagLayout());
		JScrollPane kernelsScroll = new JScrollPane(kernelPanel);
		appendKernel();
		appendKernel();
		appendKernel();
		centerControlPanel.add(kernelsScroll, BorderLayout.CENTER);
		controlsPanel.add(centerControlPanel, BorderLayout.CENTER);
		run = new JButton("Run!");
		run.setEnabled(false);
		run.addActionListener(e->{
			resetSeamCarveImage();
			this.filteredImage = imagePanel.getImage();
			seamCarve();
			saveFile.setEnabled(true);
		});
		controlsPanel.add(run, BorderLayout.SOUTH);
		add(controlsPanel, BorderLayout.WEST);
	}
	
	private void seamCarve() {
		PixelArray filtered, carved;
		
		if(carveVertical.isSelected()) { 
			BufferedImage rotatedFiltered = PixelArray.rotateImage(filteredImage, 90);
			BufferedImage rotatedCarved = PixelArray.rotateImage(originalImage, 90);
			filtered = new PixelArray(rotatedFiltered);
			carved = new PixelArray(rotatedCarved);
		}else {
			filtered = new PixelArray(filteredImage);
			carved = new PixelArray(carvedImage);
		}
		int startWidth = filtered.getWidth();
			updatePanelImage();
			while(carved.getWidth() > Math.floor(startWidth * (Double.parseDouble(scaleImageDownToPercentage.getText()) / 100))) {
				PixelArray powerMap = filtered.getPowerMap(filterWhite.isSelected());
				int[] indeciesForDeletion = filtered.getIndeciesForDeletionFromPowerMap(powerMap);
				filtered.removeIndecies(indeciesForDeletion);
				carved.replaceWithRed(indeciesForDeletion);
				carved.removeIndecies(indeciesForDeletion);
			}
			
			if(carveVertical.isSelected()) { 
				carvedImage = PixelArray.rotateImage(carved.getImage(), 270);
			}else {
				carvedImage = carved.getImage();
			}
			imageToShow = ImageShown.CARVED;
			System.out.println("Finished carving...");
			updatePanelImage();
	}
	
	private void resetSeamCarveImage() {
		int[] pixelArray = new int[originalImage.getWidth() * originalImage.getHeight()];
		originalImage.getRGB(0, 0, originalImage.getWidth(), originalImage.getHeight(), pixelArray, 0, originalImage.getWidth());
		PixelArray pixels = new PixelArray(pixelArray, originalImage.getWidth());
		carvedImage = pixels.getImage();
	}
	
	private void appendKernel() {
		JTextField[] fields = new JTextField[9];
		JPanel kernel = new JPanel();
		kernel.setLayout(new GridLayout(0, 3));
		FocusTextField kernelCell;
		for(int i = 0; i < 9; i++) {
			if(i != 4)
				kernelCell = new FocusTextField("0");
			else
				kernelCell = new FocusTextField("1");
			kernelCell.setColumns(4);
			fields[i] = kernelCell;
			kernelCell.setHorizontalAlignment(JTextField.CENTER);
			kernelCell.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {}
				
				@Override public void insertUpdate(DocumentEvent e) {
					if(isImageSelected) applyFilters();
				}

				@Override public void removeUpdate(DocumentEvent e) {}
				  
			});
			kernel.add(kernelCell);
		}
		kernels.add(fields);
		kernel.setMaximumSize(new Dimension(300, 300));
		GridBagConstraints c = new GridBagConstraints();
		if(kernelRow == 0)
			c.anchor = c.PAGE_START;
		c.gridy = kernelRow;
		c.gridx = 1;
		c.ipady = 20;
		c.ipadx = 30;
		c.insets = new Insets(20, 0, 0, 0);
		kernelRow++;
		kernelPanel.add(kernel, c);
	}
	
	private void applyFilters() {
		if(isImageSelected) {
			this.filteredImage = imagePanel.getImage();
			imageToShow = ImageShown.FILTERED;
			int[] pixelArray = new int[originalImage.getWidth() * originalImage.getHeight()];
			originalImage.getRGB(0, 0, originalImage.getWidth(), originalImage.getHeight(), pixelArray, 0, originalImage.getWidth());
			PixelArray pixels = new PixelArray(pixelArray, originalImage.getWidth());
			pixels = pixels.getGreyscale();
			if(applyKernels.isSelected()) {
				for(int i = 0; i < kernels.size(); i++) {
					JTextField[] tf = (JTextField[]) kernels.get(i);
					double[] kernel = new double[tf.length];
					for(int j = 0; j < tf.length; j++) {
						kernel[j] = Double.parseDouble(tf[j].getText());
					}
					try {
						pixels.applyKernel(kernel);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			filteredImage = pixels.getImage();
			updatePanelImage();
		}
	}
	
	private void updateImage() {
		try {
			filteredImage = ImageIO.read(new File(filePath));
			originalImage = ImageIO.read(new File(filePath));
			carvedImage = ImageIO.read(new File(filePath));
		}catch(IOException e) {
			e.printStackTrace();
		}
		imagePanel.setReferenceImage(originalImage);
		imagePanel.repaint();
	}
	
	private void updatePanelImage() {
		switch(imageToShow) {
		case ORIGINAL:
			imagePanel.setReferenceImage(originalImage);
		break;
		case FILTERED:
			imagePanel.setReferenceImage(filteredImage);
			break;
		case IN_PROGRESS:
			imagePanel.setReferenceImage(inProgressImage);
			break;
		case CARVED:
			imagePanel.setReferenceImage(carvedImage);
			break;
		}
		imagePanel.repaint();
	}
	
	public static void main(String[] args) {
		ApplicationWindow window = new ApplicationWindow();
	}
}
