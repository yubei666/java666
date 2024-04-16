package com.example.oopandroidapi.retriever;

import static com.example.oopandroidapi.retriever.MunicipalityDataRetriever.objectMapper;

import android.content.Context;

import com.example.oopandroidapi.R;
import com.example.oopandroidapi.data.MunicipalityData;
import com.example.oopandroidapi.data.TrafficData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class TrafficDataRetriever {
    public TrafficData getData(Context context, String municipalityCode) {
        try {
            JsonNode jsonQuery = objectMapper.readTree(context.getResources().openRawResource(R.raw.query_traffic));
            ((ObjectNode) jsonQuery.findValue("query").get(0).get("selection")).putArray("values").add(municipalityCode);
            HttpURLConnection con = connectToAPIAndSendPostRequest("https://trafi2.stat.fi:443/PXWeb/api/v1/en/TraFi/Liikennekaytossa_olevat_ajoneuvot/010_kanta_tau_101.px", objectMapper, jsonQuery);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                JsonNode municipalityData = objectMapper.readTree(response.toString());
                int data = municipalityData.get("value").get(0).asInt();

                return new TrafficData(data);
            }
        } catch (IOException e) {
            return null;
        }
    }

    private static HttpURLConnection connectToAPIAndSendPostRequest(String url_str, ObjectMapper objectMapper, JsonNode jsonQuery)
            throws MalformedURLException, IOException, ProtocolException, JsonProcessingException {
        URL url = new URL(url_str);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = objectMapper.writeValueAsBytes(jsonQuery);
            os.write(input, 0, input.length);
        }
        return con;
    }
}
