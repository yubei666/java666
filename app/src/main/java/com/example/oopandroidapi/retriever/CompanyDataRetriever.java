package com.example.oopandroidapi.retriever;

import com.example.oopandroidapi.data.CompanyData;
import com.example.oopandroidapi.data.WeatherData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class CompanyDataRetriever {
    private final String COMPANY_API_URL = "https://avoindata.prh.fi/tr/v1?totalResults=true&registeredOffice=%s&companyForm=AOY";

    public CompanyData getData(String municipalityName) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode companyJson;
        try {
            URL companyUrl = new URL(String.format(COMPANY_API_URL, municipalityName));
            companyJson = objectMapper.readTree(companyUrl);
        } catch (IOException e) {
            return null;
        }

        CompanyData companyData = new CompanyData(
                companyJson.get("totalResults").asInt()
        );

        return companyData;
    }

}
