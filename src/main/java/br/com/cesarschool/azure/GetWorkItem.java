package br.com.cesarschool.azure;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import br.com.cesarschool.utils.GetParameters;

public class GetWorkItem {
    public String getAcceptanceCriteria(String workItemId) {
        try {
            GetParameters property = new GetParameters();

            // Set up Azure DevOps API URL
            String url = property.getParameter("azure.url") + workItemId + "?api-version=6.0";

            // Set up personal access token (PAT)
            String personalAccessToken = property.getParameter("azure.key");

            // Set up credentials
            String credentials = Base64.getEncoder().encodeToString((":" + personalAccessToken).getBytes());

            // Create HTTP client
            CloseableHttpClient httpClient = HttpClients.createDefault();

            // Create HTTP GET request
            HttpGet request = new HttpGet(new URI(url));

            // Set request headers
            request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + credentials);
            request.setHeader(HttpHeaders.ACCEPT_CHARSET, "UTF-8");
            request.setHeader(HttpHeaders.ACCEPT, "application/json");

            // Execute the request
            HttpResponse response = httpClient.execute(request);

            // Get the response entity
            HttpEntity entity = response.getEntity();

            // Convert entity to JSON
            String json = EntityUtils.toString(entity);

            // Convert JSON string to JSON object
            JSONObject jsonObject = new JSONObject(json);
            String acceptanceCriteria = jsonObject.getJSONObject("fields").getString("Microsoft.VSTS.Common.AcceptanceCriteria");

            acceptanceCriteria = acceptanceCriteria.replaceAll("<div>", "-");
            acceptanceCriteria = acceptanceCriteria.replaceAll("</div>", ";");
            acceptanceCriteria = acceptanceCriteria.replaceAll("<(.*?)>", "");
            
            // Close the HTTP client
            httpClient.close();

            return "Crie casos de teste, levantando fluxo principal, fluxos alternativos e de exceção  para os critérios de aceite a seguir: " + acceptanceCriteria;

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return "Error!";
    }
}