package br.com.cesarschool.gpt;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.cesarschool.utils.GetParameters;

public class GenerateTitle {
    public static String getTitle(String testCase) {

        GetParameters property = new GetParameters();

        String url = property.getParameter("chatgpt.url");
        String apiKey = property.getParameter("chatgpt.key");
        String model = property.getParameter("chatgpt.model");

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
            message.put("content", "Defina e retorne somente um título para o caso de teste a seguir: " + testCase);

            requestBody.put("messages", new JSONArray().put(message));

            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(requestBody.toString());
            writer.flush();
            writer.close();

            // Response from ChatGPT
            byte[] conteudo = connection.getInputStream().readAllBytes();
            String response = extractMessageFromJSONResponse(new String(conteudo, "UTF-8"));
            return response;

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
