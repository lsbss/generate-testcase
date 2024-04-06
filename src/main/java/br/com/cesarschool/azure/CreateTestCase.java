package br.com.cesarschool.azure;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

import javax.swing.JOptionPane;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import br.com.cesarschool.gpt.GenerateTitle;
import br.com.cesarschool.utils.GetParameters;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;

public class CreateTestCase {
    public static void createTestCase(ArrayList<String> testsList, String wkID)
            throws IOException, InterruptedException {
        ArrayList<String> testOK = new ArrayList<String>();
        ArrayList<String> testNOK = new ArrayList<String>();
        for (String testCase : testsList) {

            if (testCase.length() >= 100) {
                String title = GenerateTitle.getTitle(testCase);
                try {
                    GetParameters property = new GetParameters();
                    String company = property.getParameter("azure.company");
                    String project = property.getParameter("azure.project");
                    String areaPath = property.getParameter("azure.area.path");
                    String iterationPath = property.getParameter("azure.iteration.path");

                    testCase = testCase.replaceAll(":", "");
                    ArrayList<String> steps = new ArrayList<String>(Arrays.asList(testCase.split("(\\d+\\.)")));
                    int stepsSize = steps.size() - 1;

                    String newStep = "<steps id=\\\"0\\\" last=\\\"" + stepsSize + "\\\">";
                    for (int cont = 0; cont < steps.size(); cont++) {
                        String step = steps.get(cont).replace("\\n", "");
                        if (cont != 0) {
                            newStep += "<step id=\\\"" + cont + "\\\" type=\\\"ActionStep\\\">";
                            newStep += "<parameterizedString isformatted=\\\"true\\\">&lt;DIV&gt;&lt;DIV&gt;&lt;P&gt;"
                                    + step + "&lt;/P&gt;&lt;/DIV&gt;&lt;/DIV&gt;</parameterizedString>";
                            newStep += "<parameterizedString isformatted=\\\"true\\\">&lt;P&gt;&lt;BR/&gt;&lt;/P&gt;</parameterizedString><description/></step>";
                        }
                    }
                    newStep += "</steps>";
                    // Create a new test case work item
                    String url = "https://dev.azure.com/" + company + "/" + project
                            + "/_apis/wit/workitems/$Test%20Case?api-version=7.1-preview.3";

                    String personalAccessToken = property.getParameter("azure.key");
                    ;

                    // Create the request body
                    String requestBody = "[{\"op\": \"add\", \"path\": \"/fields/System.Title\", \"value\": \"" + title
                            + "\"}, {\"op\": \"add\", \"path\": \"/fields/System.AreaPath\", \"value\": \""
                            + areaPath
                            + "\" }, {\"op\": \"add\", \"path\": \"/fields/System.IterationPath\", \"value\": \""
                            + iterationPath + "\"}, {\"op\": \"add\", \"path\": \"/relations/-\", \"value\": {\"rel\": "
                            + "\"Microsoft.VSTS.Common.TestedBy-Reverse\"" + ", \"url\": " + "\"https://dev.azure.com/"
                            + company + "/" + project + "/_apis/wit/workitems/" + wkID
                            + "\",\"attributes\": {\"comment\": \"Making a new link for the dependency\"}}}, {\"op\": \"add\", \"path\": \"/fields/Microsoft.VSTS.TCM.Steps\", \"value\": \""
                            + newStep + "\"}]";
                    // id_ct++;
                    // Set up credentials
                    String credentials = Base64.getEncoder().encodeToString((":" + personalAccessToken).getBytes());

                    // Create HTTP client
                    CloseableHttpClient httpClient = HttpClients.createDefault();

                    // Create HTTP GET request
                    HttpPost request = new HttpPost(new URI(url));

                    // Set request headers
                    request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + credentials);
                    request.setHeader(HttpHeaders.ACCEPT_CHARSET, "UTF-8");
                    request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json-patch+json");
                    request.setHeader(HttpHeaders.ACCEPT, "*/*");
                    request.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br");
                    request.setHeader(HttpHeaders.CONNECTION, "keep-alive");

                    StringEntity stringEntity = new StringEntity(requestBody, "UTF-8");
                    request.getRequestLine();
                    request.setEntity(stringEntity);

                    // Execute the request
                    HttpResponse response = httpClient.execute(request);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        testOK.add(title);
                    } else {
                        testNOK.add(title);
                    }

                    httpClient.close();

                } catch (URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        JOptionPane.showMessageDialog(null, "Testes criados com sucesso: " + testOK);
        JOptionPane.showMessageDialog(null, "Testes não criados: " + testNOK);
    }
}
