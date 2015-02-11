package model.actionlistener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JTabbedPane;

import view.ProcessDataView;
import view.WorkWindow;
import view.popup.DialogBox;
import controller.Controller;

public class ProcessBtnHandler implements ActionListener {
	public ProcessBtnHandler() {
	}

	@Override
	public void actionPerformed(ActionEvent source) {
		Controller controller = null;
		try {
			controller = Controller.getInstance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// pega o JPanel ProcessDataView
		ProcessDataView processDataView = (ProcessDataView) ((JButton) source
				.getSource()).getParent().getParent();
		DialogBox dialog = new DialogBox();
		WorkWindow workWindow = WorkWindow.getInstance();
		
		//se o radiobutton de todos os dias est� selecionado
		if (processDataView.isAllDaysSelected()) {
			//computa e mostra uma mensagem ao fim
			workWindow.setNotClosable();
			controller.computeWeatherDay();
			
			dialog.initialize("Processamento finalizado",
					"Todos os dias foram processados\n e salvos com sucesso.",
					processDataView, "OK");
			changeTabbed(workWindow);
			workWindow.setClosable();
			
		//se o radiobutton de um dia est� selecionado
		} else if (processDataView.isADaySelected()) {
			workWindow.setNotClosable();
			//verifica se o dia foi computado
			if (controller.computeWeatherDay(new SimpleDateFormat("dd/MM/yyyy").
					format(processDataView.getDateChooserADay().getDate()))) {
				//se sim, exibe uma mensagem de finaliza��o
				dialog.initialize("Processamento finalizado",
						"Todos os dias foram processados\n e salvos com sucesso.",
						processDataView, "OK");
				changeTabbed(workWindow);
			}
			//sen�o, exibe uma mensagem informando que o dia n�o foi processado
			else {
				dialog.initialize("Processamento n�o realizado",
						"O dia n�o � v�lido ou ainda\n n�o foi salvo no arquivo de dados.",
						processDataView, "error");
			}
			workWindow.setClosable();
		//se o radiobutton de um conjunto de dias est� selecionado
		} else if (processDataView.isRangeDaysSelected()) {
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
				if (controller.computeWeatherDay(fDay, lDay)) {
					//se sim, exibe uma mensagem informando o fim do processamento
					dialog.initialize("Processamento finalizado",
							"Todos os dias foram processados\n e salvos com sucesso.",
							processDataView, "OK");
					changeTabbed(workWindow);
				}
				//sen�o, exibe uma mesagem informando o n�o processamento
				else {
					dialog.initialize("Processamento n�o realizado",
							"O dia n�o � v�lido ou ainda\n n�o foi salvo no arquivo de dados.",
							processDataView, "error");
				}
				workWindow.setClosable();
			//se as datas n�o s�o cronol�gicas exibe uma mensagem informando o fato
			} else {
				dialog.initialize("Processamento n�o realizado",
						"As datas precisam estar em\n ordem cronol�gia.",
						processDataView, "incorrect");
			}
		} else {
			dialog.initialize("Por favor", "Selecione uma op��o para iniciar o processamento", processDataView, "incorrect");
		}
	}
	
	private void changeTabbed(WorkWindow workWindow) {
		workWindow.setSelectedTab(1);
//		workWindow.configureTabbedPaneEnable(tabbedPane);
	}
}
