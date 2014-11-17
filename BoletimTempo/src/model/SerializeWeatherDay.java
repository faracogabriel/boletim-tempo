package model;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Classe que recebe um conjunto de dias meteorologicos e faz a
 * serializacao em um arquivo xml.
 * 
 * @author Elloa
 *
 */
public class SerializeWeatherDay {

	List<WeatherDay> days;

	/**
	 * Constroi um arquivo a partir de um conjunto de dias meteorologicos passados como parametro
	 * 
	 * @param list
	 */
	public SerializeWeatherDay(List<WeatherDay> list){
		this.days = list;
	}

	/**
	 * Processa e escreve os dados sobre os dias meteorol�gicos em
	 * um arquivo
	 * 
	 * @throws Throwable se houver problemas na escrita do arquivo.
	 */
	public void writeDaybyDay() throws Throwable{
		DocumentBuilderFactory dayBuilder = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dayBuilder.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element dias = doc.createElement("dias");
		doc.appendChild(dias);

		for (WeatherDay weatherDay : days) {


			// Dia
			Element dia = doc.createElement("dia");
			dias.appendChild(dia);

			// Data
			Element data = doc.createElement("data");
			dias.appendChild(data);

			Element ano = doc.createElement("ano");
			ano.appendChild(doc.createTextNode(Integer.toString(weatherDay.getYear())));
			data.appendChild(ano);
			// M�s
			Element mes = doc.createElement("mes");
			mes.appendChild(doc.createTextNode(Integer.toString(weatherDay.getMonth())));
			data.appendChild(mes);
			// N�mero do dia
			Element nDia = doc.createElement("nDia");
			nDia.appendChild(doc.createTextNode(Integer.toString(weatherDay.getDay())));
			data.appendChild(nDia);

			int counter = 0;
			List<DayPeriod> turnos = weatherDay.getDayPeriods();

			for (DayPeriod dayPeriod : turnos) {

				String sTurno;
				if (counter == 0) sTurno = "madrugada";
				else if (counter == 1) sTurno = "manha";
				else if (counter == 2) sTurno = "tarde";
				else sTurno = "noite";
				

				// Elementos de cada dia		
				Element turno = doc.createElement(sTurno);
				dia.appendChild(turno);
				{
					Element pressaoMedia = doc.createElement("pressao_media");
					pressaoMedia.appendChild(doc.createTextNode(Double.toString(weatherDay.getDawn().getAvgPressure())));
					turno.appendChild(pressaoMedia);

					Element temperaturaMax = doc.createElement("temperatura_maxima");
					temperaturaMax.appendChild(doc.createTextNode(Double.toString(weatherDay.getDawn().getHighTemp())));
					turno.appendChild(temperaturaMax);

					Element temperaturaMin = doc.createElement("temperatura_minima");
					temperaturaMin.appendChild(doc.createTextNode(Double.toString(weatherDay.getDawn().getLowTemp())));
					turno.appendChild(temperaturaMin);

					Element umidadeMin = doc.createElement("umidade_minima");
					umidadeMin.appendChild(doc.createTextNode(Double.toString(weatherDay.getDawn().getLowHumid())));
					turno.appendChild(umidadeMin);

					Element umidadeMax = doc.createElement("umidade_maxima");
					umidadeMax.appendChild(doc.createTextNode(Double.toString(weatherDay.getDawn().getHighHumid())));
					turno.appendChild(umidadeMax);

					Element speed = doc.createElement("velocidade_maxima");
					speed.appendChild(doc.createTextNode(Double.toString(weatherDay.getDawn().getMaxSpeed())));
					turno.appendChild(speed);

					Element direction = doc.createElement("direcao_velocidade_maxima");
					direction.appendChild(doc.createTextNode(Double.toString(weatherDay.getDawn().getMaxDirect())));
					turno.appendChild(direction);

					Element rain = doc.createElement("precipitacao_acumulada");
					rain.appendChild(doc.createTextNode(Double.toString(weatherDay.getDawn().getAcumRain())));
					turno.appendChild(rain);



				}
				
				counter++;

			}


		}

		final Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.transform(new DOMSource(doc), new StreamResult(new File(Util.OUTPUT_FOLDER + "alldays.xml")));

		System.out.println("File saved!");

	}






}
