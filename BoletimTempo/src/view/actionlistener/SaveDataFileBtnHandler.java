package view.actionlistener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import controller.Controller;
import model.util.ApplicationStatus;
import view.WeatherDayView;
import view.WorkWindow;
import view.popup.DialogBox;

/**
 * Class which implements the necessary methods to handle the click on 'Salvar'
 * button in WeatherDayView
 * 
 * @author Patrick M Lima
 *
 */
public class SaveDataFileBtnHandler implements ActionListener {
	private String title;
	private String msg;

	/**
	 * Class constructor
	 */
	public SaveDataFileBtnHandler() {
	}

	/**
	 * Method which handle the save action
	 */
	@Override
	public void actionPerformed(ActionEvent action) {
		title = "Arquivo n\u00E3o p\u00F4de ser salvo";
		msg = "Verifique se voc� inseriu um nome ou diret\u00F3rio v\u00E1lido e tente novamente.";
		//Pega a inst�ncia de WeatherDayView 
		WeatherDayView wdView = (WeatherDayView) ((JButton) action.getSource()).getParent();
		
		//Cria um JFileChooser para salvar o arquivo
		JFileChooser saveChooser = new JFileChooser();
		//adiciona as extens�es suportadas
		saveChooser.setFileFilter(new FileNameExtensionFilter("Arquivo de texto", "txt"));
		saveChooser.setFileFilter(new FileNameExtensionFilter("Arquivo XML", "xml"));
		//remove a extens�o 'Todos os arquivos'
		saveChooser.setAcceptAllFileFilterUsed(false);
		//Mostra a janela e captura a op��o escolhida pelo usu�rio
		int option = saveChooser.showSaveDialog(null);
		
		//caso seja a op��o 'Salvar'
		if(option == JFileChooser.APPROVE_OPTION) {
			//pega o arquivo selecionado
			File file = saveChooser.getSelectedFile();
			boolean result = false;
			//caso a extens�o escolhida seja XML
			if(saveChooser.getFileFilter().getDescription().equals("Arquivo XML")){
				result = saveAsXmlFile(file);
			}
			else {
				result = saveAsTxtFile(file, wdView.getTextAreaWeatherDay());
			}
			
			//analisa o retorno e caso tenha ocorrido tudo bem mostra uma mensagem
			if(result) {
				(new DialogBox()).initialize(" ", "Arquivo salvo com sucesso.", wdView, "OK");
				changeToFiguresTab();
			}else {
				if (title != null)
					(new DialogBox()).initialize(title, msg, wdView, "error");
			}
				
		}
	}	
	
	/**
	 * Method which storage the processed data in a XML file
	 * 
	 * @param file
	 *            the file to saves the data
	 * @return a boolean confirming the save success or failure
	 */
	private boolean saveAsXmlFile(File file) {
		try {
			String fPath = file.getAbsolutePath();
			if(!fPath.endsWith(".xml"))
				fPath = fPath + ".xml";
			
			Controller.getInstance().saveXMLDataFile(fPath);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Method which storage the processed data in a text file (like shown in
	 * JTextArea on WeatherDayView)
	 * 
	 * @param file
	 *            the file to saves the data
	 * @param textArea
	 *            JTextArea that contains the text which will be save
	 * @return a boolean confirming the save success or failure
	 */
	private boolean saveAsTxtFile(File file, JTextArea textArea) {
		try {
			String fPath = file.getAbsolutePath();
			if(!fPath.endsWith(".txt"))
				fPath = fPath + ".txt";
			
			File f = new File(fPath);
			if (chooseReplaceFile(f)) {

				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(f), "UTF-8"));
				out.write(textArea.getText());
				out.flush();
				out.close();

				return true;
			}
		} catch (IOException exc) {

		}
		return false;
	}

	/*
	private boolean copyXmlFile(File source, File destination) throws IOException {    
		if (chooseReplaceFile(destination)) {
			FileChannel sourceChannel = null;
			FileChannel destinationChannel = null;

			try {
				sourceChannel = new FileInputStream(source).getChannel();
				destinationChannel = new FileOutputStream(destination)
						.getChannel();
				sourceChannel.transferTo(0, sourceChannel.size(),
						destinationChannel);
			} finally {
				if (sourceChannel != null && sourceChannel.isOpen())
					sourceChannel.close();
				if (destinationChannel != null && destinationChannel.isOpen()) {
					destinationChannel.close();
				}
			}
			return true;
		}
		return false;
	}
	*/
	
	/**
	 * Assistant method which verifies the file existence and give to user the
	 * option to replace it.
	 * 
	 * @param file
	 *            the file which will be verified
	 * @return the confirmation if replaced or not
	 */
	private boolean chooseReplaceFile(File file) {
		if (file.exists()) {
			int result = JOptionPane.showConfirmDialog(null,
					"O arquivo com o nome selecionado j\u00E1 existe\n"
							+ "Deseja sobrescrev\u00EA-lo?", "Aviso",
					JOptionPane.OK_OPTION, JOptionPane.CANCEL_OPTION);
			if (result == JOptionPane.NO_OPTION) {
				title = null;
				return false;
			}
			file.delete();
		}
		return true;
	}
	
	/**
	 * Changes to the show figures tab.
	 */
	private void changeToFiguresTab() {
		if (WorkWindow.getInstance().getApplicationStatus() == ApplicationStatus.DATA_PROCESSED)
			WorkWindow.getInstance().setStatus(
					ApplicationStatus.DATA_FILE_SAVED);
		WorkWindow.getInstance().setSelectedTab(2);
	}		
}