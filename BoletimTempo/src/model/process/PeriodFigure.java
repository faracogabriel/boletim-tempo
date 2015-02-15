package model.process;

import java.util.List;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import model.util.CircularArrayList;
import model.util.Util;

/**
 * Class which generate a turn figure (figure which illustrates  the data
 * obtained in a period processing 
 * @author Patrick M Lima
 * Date: 09/01/2015
 */
public class PeriodFigure {
	private List<WeatherDay> weatherDay;
	
	private int X_PERIOD;
	private int Y_PERIOD = 370;
	
	/**
	 * The class constructor which initializes the weatherDay class attribute
	 * @param weatherDay a list containing WeatherDay objects
	 */
	public PeriodFigure(List<WeatherDay> weatherDay) {
		this.weatherDay = weatherDay; 
	}
	
	/**
	 * Takes the weatherDay List attribute, runs it, find the periods and send them to
	 * the figure being generated 
	 */
	public CircularArrayList<BufferedImage[]> generateFigures() {
		CircularArrayList<BufferedImage[]> periodFigures = new CircularArrayList<BufferedImage[]>();
		BufferedImage[] bi = new BufferedImage[4];
		int count;
		String periodName;
		for(WeatherDay wd : weatherDay) {
			count = 0;
			periodName = "";
			for(DayPeriod period : wd.getDayPeriods()) {
				periodName = Util.ranksPeriod(count);
				
				String date = Integer.toString(wd.getDay()) +"."+ Integer.toString(wd.getMonth()) +"."+ Integer.toString(wd.getYear());
				
				bi[count] = organizeImage(period, date, periodName, wd.getHeatIndex());
				count++;
			}
			periodFigures.add(bi);
		}
		return periodFigures;
	}
	
	/**
	 * Mount the image to be saved
	 * @param dayPeriod a DayPeriod object which represents a turn
	 * @param date the date of the object DayPeriod
	 * @param turnName a specific turn name which can be either, dawn, morning, afternoon, night.
	 */
	private BufferedImage organizeImage(DayPeriod dayPeriod, String date, String periodName, double heatIndex) {
		Graphics2D g2d = null;
		BufferedImage biToSave = null;
		Image img = null;
		
		if( periodName.equals("madrugada")) {
			X_PERIOD = 715;
		} else {
			X_PERIOD = 1250;
		}
		
		if( periodName.equals("manha") || periodName.equals("tarde") ) {
			if(dayPeriod.getAcumRain() == 0.0) {
				img = new ImageIcon(Util.SUNNY).getImage();
			} else {
				img = new ImageIcon(Util.RAINY).getImage();
			}
		} else {
			if(dayPeriod.getAcumRain() == 0.0) {
				img = new ImageIcon(Util.NIGHT).getImage();
			}
			else {
				img = new ImageIcon(Util.RAINY_NIGHT).getImage();
			}
		}
		
		if(periodName.equals("manha")) {
			periodName = "manh�";
		}
		
		biToSave = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		g2d = (Graphics2D) biToSave.getGraphics();
		g2d.drawImage(img, 0, 0, null);
		
		g2d.setColor(Color.BLACK);
		g2d.setFont(new Font("Cambria", Font.BOLD, 240));	
		g2d.drawString(periodName.toUpperCase(), X_PERIOD, Y_PERIOD);
		g2d.setFont(new Font("Cambria", Font.BOLD, 220));
		g2d.drawString(date.replace(".",  "/"), 1040, 680);
		
		g2d.setFont(new Font("Cambria", Font.BOLD, 200));
		
		g2d.drawString(String.format("%.2f", dayPeriod.getHighTemp() ).replace(",", ".") + "�C", 1500, 1370);
		g2d.drawString(String.format("%.2f", dayPeriod.getLowTemp() ).replace(",", ".") + "�C", 1500, 1700);
		g2d.drawString(String.format("%.2f", dayPeriod.getMaxSpeed()).replace(",", ".") + " km/h", 50, 2900);
		g2d.drawString(String.format("%.2f", heatIndex).replace(",", ".") + "�C",  1500, 2900);
		if(dayPeriod.getAcumRain() > 0.0) {
			g2d.drawString( String.format("%.2f", dayPeriod.getAcumRain()).replace(",", ".") + " mm", 50, 1400);
		}
		g2d.dispose();
		
		return biToSave;
//		try {
//			File folder = new File(Util.PERIOD_FIGURES_FOLDER);
//			if(!folder.exists()) {
//				folder.mkdir();
//			}
//			//Salvar a imagem
//			ImageIO.write(biToSave, "png", new File(Util.PERIOD_FIGURES_FOLDER + date + "-" + periodName + ".png"));
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}
