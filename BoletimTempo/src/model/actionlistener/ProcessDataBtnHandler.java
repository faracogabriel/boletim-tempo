package model.actionlistener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.swing.JButton;

import model.util.ApplicationStatus;
import view.ProcessDataView;
import view.WorkWindow;
import view.popup.DialogBox;
import controller.Controller;

/**
 * Handle of Process Button within ProcessDataView class
 * @author Patrick M Lima
 *
 */
public class ProcessDataBtnHandler implements ActionListener {
	
	public ProcessDataBtnHandler() {
	}

	@Override
	public void actionPerformed(ActionEvent source) {
		Controller controller = null;
		//pega uma instancia do controller
		try {
			controller = Controller.getInstance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Caso o status seja diferente do status 'inicial' da aplica��o
		if(WorkWindow.getInstance().getApplicationStatus() != ApplicationStatus.INITIALIZED) {
			//limpa os dados em mem�ria
			controller.clear();
			//configuras as abas para ativar apenas a de processamento de dados
			WorkWindow.getInstance().configureTabbedPaneEnable();
		}
		
		// pega o JPanel ProcessDataView
		ProcessDataView processDataView = (ProcessDataView) ((JButton) source
				.getSource()).getParent().getParent();
		
		WorkWindow workWindow = WorkWindow.getInstance();
		//pega a op��o da janela, e de acordo com ela, toma as decis�es
		ProcessDataViewCases result = ranksTheCase(processDataView);
		
		switch(result) {
		case ALL_DAY_SELECTED:
			workWindow.setNotClosable();
			controller.computeWeatherDay();

			(new DialogBox()).initialize("Processamento finalizado",
					"Todos os dias foram processados\n e salvos com sucesso.",
					processDataView, "OK");
			changeToWeatherDayTab();
			workWindow.setClosable();
			break;

		case ONE_DAY_SELECTED:
			//se o retorno da computa��o � true (computou o dia)
			if (controller.computeWeatherDay(new SimpleDateFormat("dd/MM/yyyy").
					format(processDataView.getDateChooserADay().getDate())) ) {
				//exibe uma mensagem de finaliza��o
				(new DialogBox()).initialize("Processamento finalizado",
						"Todos os dias foram processados\n e salvos com sucesso.",
						processDataView, "OK");

				//muda a aba
				changeToWeatherDayTab();
			}
			//sen�o, exibe uma mensagem informando que o dia n�o foi processado
			else {
				(new DialogBox()).initialize("Processamento n�o realizado",
						"O dia n�o � v�lido ou ainda\n n�o foi salvo no arquivo de dados.",
						processDataView, "error");
			}
			workWindow.setClosable();
			break;

		case RANGE_OF_DAYS_SELECTED:
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			String fDay, lDay;
			fDay = formatter.format(processDataView.getDateChooserFirstDay()
					.getDate());
			lDay = formatter.format(processDataView.getDateChooserLastDay()
					.getDate());

			//verifica se as datas est�o em ordem cronol�gica
			if (controller.validateDate(fDay, lDay)) {
				workWindow.setNotClosable();

				//verifica se os dias foram computados
				if (controller.computeWeatherDay(fDay, lDay) ) {
					//se sim, exibe uma mensagem informando o fim do processamento
					(new DialogBox()).initialize("Processamento finalizado",
							"Todos os dias foram processados\n e salvos com sucesso.",
							processDataView, "OK");
					
					//muda a aba
					changeToWeatherDayTab();
				}
				//sen�o, exibe uma mesagem informando o n�o processamento
				else {
					(new DialogBox()).initialize("Processamento n�o realizado",
							"O dia n�o � v�lido ou ainda\n n�o foi salvo no arquivo de dados.",
							processDataView, "error");
				}
				workWindow.setClosable();

				//se as datas n�o s�o cronol�gicas exibe uma mensagem informando o fato
			} else {
				(new DialogBox()).initialize("Processamento n�o realizado",
						"As datas precisam estar em\n ordem cronol�gica.",
						processDataView, "incorrect");
			}
			break;

		case NO_DATE_SELECTED:
			(new DialogBox()).initialize("Processamento n�o realizado",
					"Selecione a(s) data(s) para iniciar o processamento",
					processDataView, "incorrect");
			break;

		case NO_OPTIONS_SELECTED:
			(new DialogBox()).initialize("Por favor", "Selecione uma op��o para iniciar o processamento", processDataView, "incorrect");
			break;
		}
	}		
	
	/**
	 * Ranks the cases according the options selected in the view
	 * @param processDataView the instance of view
	 * @return an enum which represents the possible states 
	 */
	private ProcessDataViewCases ranksTheCase(ProcessDataView processDataView) {
		if (processDataView.isAllDaysSelected()) {
			return ProcessDataViewCases.ALL_DAY_SELECTED;
		} else if (processDataView.isADaySelected()) {
			if(processDataView.getDateChooserADay().getDate() == null)
				return ProcessDataViewCases.NO_DATE_SELECTED;
			return ProcessDataViewCases.ONE_DAY_SELECTED;
		} else if (processDataView.isRangeDaysSelected()) {
			if(processDataView.getDateChooserFirstDay().getDate() == null || processDataView.getDateChooserLastDay().getDate() == null)
				return ProcessDataViewCases.NO_DATE_SELECTED;
			return ProcessDataViewCases.RANGE_OF_DAYS_SELECTED;
		} else {
			return ProcessDataViewCases.NO_OPTIONS_SELECTED;
		}

	}
	
	/**
	 * Change the tab to show the WeatherDayView
	 */
	private void changeToWeatherDayTab() {
		WorkWindow.getInstance().setStatus(ApplicationStatus.DATA_PROCESSED);
		WorkWindow.getInstance().setSelectedTab(1);
	}

}
