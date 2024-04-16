package com.example.oopandroidapi.retriever;

import android.content.Context;

import com.example.oopandroidapi.R;
import com.example.oopandroidapi.data.MunicipalityData;
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
import java.util.logging.Logger;

public class MunicipalityDataRetriever {
    static ObjectMapper objectMapper = new ObjectMapper();
    static HashMap<String, String> municipalityNamesToCodesMap = null;

    public static void getMunicipalityCodesMap() {
        if (municipalityNamesToCodesMap == null) {
            JsonNode areas = readAreaDataFromTheAPIURL(objectMapper);
            if (areas != null) municipalityNamesToCodesMap = createMunicipalityNamesToCodesMap(areas);
        }
    }

    public float getEmploymentData(Context context, String municipalityName) {
        if (municipalityNamesToCodesMap == null) {
            return 0;
        }
        String code = municipalityNamesToCodesMap.get(municipalityName);
        try {
            JsonNode jsonQuery = objectMapper.readTree(context.getResources().openRawResource(R.raw.query_employ));
            ((ObjectNode) jsonQuery.findValue("query").get(0).get("selection")).putArray("values").add(code);
            HttpURLConnection con = connectToAPIAndSendPostRequest("https://pxdata.stat.fi:443/PxWeb/api/v1/en/StatFin/tyokay/statfin_tyokay_pxt_115x.px", objectMapper, jsonQuery);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                JsonNode municipalityData = objectMapper.readTree(response.toString());
                float employData = Float.valueOf(municipalityData.get("value").get(0).asText());

                return employData;
            }
        } catch (IOException e) {
            return 0;
        }
    }

    public float getWorkplaceData(Context context, String municipalityName) {
        if (municipalityNamesToCodesMap == null) {
            return 0;
        }
        String code = municipalityNamesToCodesMap.get(municipalityName);
        try {
            JsonNode jsonQuery = objectMapper.readTree(context.getResources().openRawResource(R.raw.query_workplace));
            ((ObjectNode) jsonQuery.findValue("query").get(1).get("selection")).putArray("values").add(code);
            HttpURLConnection con = connectToAPIAndSendPostRequest("https://pxdata.stat.fi:443/PxWeb/api/v1/en/StatFin/tyokay/statfin_tyokay_pxt_125s.px", objectMapper, jsonQuery);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                JsonNode municipalityData = objectMapper.readTree(response.toString());
                float workplaceData = Float.valueOf(municipalityData.get("value").get(0).asText());

                return workplaceData;
            }
        } catch (IOException e) {
            return 0;
        }
    }

    public ArrayList<MunicipalityData> getData(Context context, String municipalityName) {
        if (municipalityNamesToCodesMap == null) {
            return null;
        }
        String code = municipalityNamesToCodesMap.get(municipalityName);
        try {
            // The query for fetching data from a single municipality is stored in query.json
            JsonNode jsonQuery = objectMapper.readTree(context.getResources().openRawResource(R.raw.query));
            // Let's replace the municipality code in the query with the municipality that the user gave
            // as input
            ((ObjectNode) jsonQuery.findValue("query").get(0).get("selection")).putArray("values").add(code);
            HttpURLConnection con = connectToAPIAndSendPostRequest("https://pxdata.stat.fi:443/PxWeb/api/v1/en/StatFin/synt/statfin_synt_pxt_12dy.px", objectMapper, jsonQuery);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                JsonNode municipalityData = objectMapper.readTree(response.toString());
                ArrayList<String> years = new ArrayList<>();
                JsonNode populations = null;
                for (JsonNode node : municipalityData.get("dimension").get("Vuosi")
                        .get("category").get("label")) {
                    years.add(node.asText());
                }

                populations = municipalityData.get("value");
                ArrayList<MunicipalityData> populationData = new ArrayList<>();
                for (int i = 0; i < populations.size(); i+=2) {
                    Integer population = populations.get(i).asInt();
                    Integer populationChange = populations.get(i+1).asInt();
                    populationData.add(new MunicipalityData(Integer.parseInt(years.get(i / 2)), populationChange, population));
                }

                return populationData;
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


    /**
     * In this method, we find all the municipality names and codes from the Json and put them into a HashMap,
     * so that we can get search for the municipality code by providing the municipality name
     *
     * @param areas
     * @return HashMap where municipality name is mapped to municipality code
     */
    private static HashMap<String, String> createMunicipalityNamesToCodesMap(JsonNode areas) {
        JsonNode codes = null;
        JsonNode names = null;

        // Here we find the element "variables", and inside it we have the element "text", that has value "Area".
        // Within the same element, we have the keys "values" which contains the municipality codes (e.g. KU123) as a list
        // and "valueTexts" which contains the municipality names (e.g. Lahti) as a list
        for (JsonNode node : areas.findValue("variables")) {
            if (node.findValue("text").asText().equals("Area")) {
                codes = node.findValue("values");
                names = node.findValue("valueTexts");
            }
        }

        // Let's store the municipality names as keys, and municipality codes as values in a HashMap

        HashMap<String, String> municipalityNamesToCodesMap = new HashMap<>();

        // Here we can assume that the size of names and codes are equal, at there are as many municipality codes
        // as there are municipality names
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i).asText();
            String code = codes.get(i).asText();
            municipalityNamesToCodesMap.put(name, code);

        }
        return municipalityNamesToCodesMap;
    }


    /**
     * Here we read the all the JSON from the URL to a JsonNode
     * <p>
     * How to improve this: instead of fetching the same data all over again when restarting the app, we could store
     * the areas JSON to a file and read it from there. Then we would only need to fetch it once, if the file does
     * not yet exist.
     *
     * @param objectMapper
     * @return JsonNode with municipality data
     */
    private static JsonNode readAreaDataFromTheAPIURL(ObjectMapper objectMapper) {
        JsonNode areas = null;
        try {
            areas = objectMapper.readTree(new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/en/StatFin/synt/statfin_synt_pxt_12dy.px"));


        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return areas;
    }
}
