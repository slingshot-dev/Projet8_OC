package tourGuide.service;

import Modeles.Attraction;
import Modeles.VisitedLocation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class GpsUtil {

    public List<Attraction> getAttractions() throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        String ResourceUrl = "http://localhost:8080/getAttractions";
        ResponseEntity<String> response = restTemplate.getForEntity(ResourceUrl, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.getBody(), new TypeReference<List<Attraction>>(){});
    }

    public VisitedLocation getUserLocation(UUID userId) throws IOException {
        String ResourceUrl = "http://localhost:8080/getUserLocation";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ResourceUrl)
                .queryParam("userId", userId);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.getBody(), new TypeReference<VisitedLocation>(){});
    }





}