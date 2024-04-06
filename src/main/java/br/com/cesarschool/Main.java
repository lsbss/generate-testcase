package br.com.cesarschool;

import javax.swing.JOptionPane;

import br.com.cesarschool.azure.CreateTestCase;
import br.com.cesarschool.azure.GetWorkItem;
import br.com.cesarschool.gpt.GenerateTestCase;

public class Main {
    public static void main(String[] args) {

        GetWorkItem getWorkItem = new GetWorkItem();
        String wkID = JOptionPane.showInputDialog("Digite o ID do WorkItem:");

        String criterios = getWorkItem.getAcceptanceCriteria(wkID);

        if(criterios.equals("Error!")){
            return;
        }
        try {
            CreateTestCase.createTestCase(GenerateTestCase.chatGPT(criterios), wkID);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
