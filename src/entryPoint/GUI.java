package entryPoint;

import carriers.ChannelInformation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class GUI {
	JFrame frame;
	JPanel pan, gumbi, img, inf;
	JButton bchorg,by,bcb,bcr,brec,bsum,bmul, brx, bry;
	JLabel image, info;
	ChannelInformation YCh,CbCh,CrCh,currentCh;
	public GUI(ChannelInformation YChh, ChannelInformation CbChh, ChannelInformation CrChh) {
		this.YCh=YChh;
		this.CbCh=CbChh;
		this.CrCh=CrChh;
		
		
		//1. Create the frame.
		frame = new JFrame("REGRESIJSKO KOMPRESIRANJE");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 300);
		pan=  new JPanel();
		gumbi= new JPanel();
		img= new JPanel();
		
		frame.add(pan);
        pan.setLayout(new BorderLayout());
        gumbi.setLayout(new GridLayout(9,1));
		img.setLayout(new GridLayout(1,2));
		
		by= new JButton("Kanal Y");
		bcb=new JButton("Kanal Cb");
		bcr=new JButton("Kanal Cr");

		bchorg= new JButton("Kanal Org");
		brec= new JButton("Kanal Rek.");
		bsum= new JButton("Kanal Sum");
		bmul= new JButton("Kanal Mul.");
		brx= new JButton("Kanal Reg. X");
		bry= new JButton("Kanal Reg. Y");
		
		gumbi.add(by);
		gumbi.add(bcb);
		gumbi.add(bcr);
		gumbi.add(bchorg);
		gumbi.add(brec);
		gumbi.add(bsum);
		gumbi.add(bmul);
		gumbi.add(brx);
		gumbi.add(bry);
		
		
		
		info=new JLabel("x");
		pan.add(gumbi,BorderLayout.EAST);
		pan.add(img,BorderLayout.CENTER);
		pan.add(info,BorderLayout.PAGE_END);
		image = new JLabel(new ImageIcon(getBufferedImage(YCh.grayscale, true)));
        img.add(image);
		refreshInfo();
		currentCh=YCh;
		by.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentCh=YCh;
				image.setIcon(new ImageIcon(getBufferedImage(currentCh.grayscale, true)));
				refreshInfo();
			}
		});
		bcb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentCh=CbCh;
				image.setIcon(new ImageIcon(getBufferedImage(currentCh.grayscale, true)));
				refreshInfo();
			}
		});
		bcr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentCh=CrCh;
				image.setIcon(new ImageIcon(getBufferedImage(currentCh.grayscale, true)));
				refreshInfo();
			}
		});
		bchorg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				image.setIcon(new ImageIcon(getBufferedImage(currentCh.grayscale, true)));
				refreshInfo();
			}
		});
		brec.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				image.setIcon(new ImageIcon(getBufferedImage(currentCh.rec, true)));
				refreshInfo();
			}
		});
		bsum.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				image.setIcon(new ImageIcon(getBufferedImage(currentCh.sum, true)));
				refreshInfo();
			}
		});
		bmul.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				image.setIcon(new ImageIcon(getBufferedImage(currentCh.mul, true)));
				refreshInfo();
			}
		});
		brx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				image.setIcon(new ImageIcon(getBufferedImage(currentCh.regx, true)));
				refreshInfo();
			}
		});
		bry.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				image.setIcon(new ImageIcon(getBufferedImage(currentCh.regy, true)));
				refreshInfo();
			}
		});
		
		
		frame.setVisible(true);
	}
	void refreshInfo(){
		info.setText("Iter:");
	}
	public BufferedImage getBufferedImage(float[][] x, boolean singleCH){
		BufferedImage img=new BufferedImage( x.length,x[0].length,
				BufferedImage.TYPE_INT_RGB);
		
		
		WritableRaster raster = img.getRaster();
		for (int w = 0; w < x.length; w++) {
			for(int h=0;h<x[0].length;h++){
					if(singleCH)raster.setPixel(w, h, new int[]{(int)(255*x[w][h]),(int)(255*x[w][h]),(int)(255*x[w][h])});
					else raster.setPixel(w, h, new int[]{(((int)(255*x[w][h])>>16)&255),(((int)(255*x[w][h])>>8)&255),((int)(255*x[w][h])&255)});
			}
		}
		
		
		
		return img;
	}
	
	
	
	
	
}
