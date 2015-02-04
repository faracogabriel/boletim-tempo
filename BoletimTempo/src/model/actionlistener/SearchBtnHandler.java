package model.actionlistener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JTextArea;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import view.FileChooser;
import view.WeatherDayView;

public class SearchBtnHandler implements ActionListener{
	private WeatherDayView wdv;
	public SearchBtnHandler(WeatherDayView source) {
		wdv = source;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		FileChooser fc = new FileChooser();
		File f = fc.getSelectedFile();
		if(f != null) {
				wdv.setFilePath(f.getName());
				try {
					showDataFile(f);
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	public void showDataFile(File file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false);
		DocumentBuilder docBuilder = dbf.newDocumentBuilder();
		Document doc = docBuilder.parse(file);
		NodeList dias = doc.getElementsByTagName("dia");
		
		JTextArea tArea = wdv.getTextAreaWeatherDay();
		NodeList childs;
		NodeList data;
		NodeList periods;
		String[] periodNames = {"Madrugada", "Manh�", "Tarde", "Noite"};
		int k;
		
		for(int i = 0; i < dias.getLength(); i++) {
			 childs = dias.item(i).getChildNodes();
			 
			 data = childs.item(1).getChildNodes();
			 tArea.append("Data: ");
			 tArea.append(data.item(5).getTextContent() + "-" + data.item(3).getTextContent() + "-" + data.item(1).getTextContent());
			 tArea.append("\n");
			 tArea.append("�ndice de calor: " + childs.item(11).getTextContent() + " �C\n");
			 tArea.append("Turnos:\n");
			 
			 k=0;
			 for(int j = 3; j < childs.getLength() - 2; j=j+2) {
				 periods = childs.item(j).getChildNodes();
				 
				 tArea.append("    " + periodNames[k] + "\n");
				 tArea.append("        Press�o M�dia: " + periods.item(1).getTextContent() + " hPa\n");
				 tArea.append("        Temperatura M�nima: " + periods.item(3).getTextContent() + " �C\n");
				 tArea.append("        Temperatura M�xima: " + periods.item(5).getTextContent() + " �C\n");
				 tArea.append("        Umidade m�nima: " + periods.item(7).getTextContent() + " %\n");
				 tArea.append("        Umidade M�xima: " + periods.item(9).getTextContent() + " %\n");
				 tArea.append("        Velocidade M�xima do vento: " + periods.item(11).getTextContent() + " m/s - Dire��o: " + periods.item(13).getTextContent() + " �\n");
				 tArea.append("        Precipita��o acumulada: " + periods.item(15).getTextContent() + " mm\n");
				 tArea.append("\n");
				 k++;
			 }
			 tArea.append("\n");
		}
		
	}

}
