package br.com.cesarschool.gpt;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.cesarschool.utils.GetParameters;


public class GenerateTestCase {
    public static ArrayList<String> chatGPT(String prompt) {

        GetParameters property = new GetParameters();
 
        String url = property.getParameter("chatgpt.url");
        String apiKey = property.getParameter("chatgpt.key");
        String model = property.getParameter("chatgpt.model");
        ArrayList<String> testsList = new ArrayList<String>();

        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Content-Encoding", "br");
            connection.setRequestProperty("Accept", "*/*");

            // The request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);
            requestBody.put("temperature", 0.0);

            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);

            requestBody.put("messages", new JSONArray().put(message));

            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(requestBody.toString());
            writer.flush();
            writer.close();

            // Response from ChatGPT
            byte conteudo[] = connection.getInputStream().readAllBytes();
            
            // calls the method to extract the message.
            
            testsList.addAll(Arrays.asList(extractMessageFromJSONResponse(new String(conteudo, "UTF-8")).split("(Fluxo principal|Fluxo alternativo|Fluxo de exceção)")));

            return testsList;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String extractMessageFromJSONResponse(String response) {
        int start = response.indexOf("content") + 11;

        int end = response.indexOf("\"", start);

        return response.substring(start, end);
    }

}