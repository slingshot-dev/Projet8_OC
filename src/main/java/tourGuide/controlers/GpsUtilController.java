package tourGuide.controlers;

import Modeles.Attraction;
import Modeles.VisitedLocation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GpsUtilController {

    private Logger logger = LoggerFactory.getLogger(GpsUtilController.class);


    public List<Attraction> getAttractions() throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        String ResourceUrl = "http://17.18.0.2:8050/getAttractions";
        ResponseEntity<String> response = restTemplate.getForEntity(ResourceUrl, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.getBody(), new TypeReference<List<Attraction>>(){});
    }

    WebClient webClient = WebClient.create("http://17.18.0.2:8050");

    public List<Attraction>  getAttractionsnew() {

        Mono<List<Attraction>> attractionStream = webClient
                .get()
                .uri("/getAttractions")
                .retrieve().bodyToMono(new ParameterizedTypeReference<List<Attraction>>() {});

        return attractionStream.block();
    }


    public VisitedLocation getUserLocationnew(UUID userId) {

        Mono<VisitedLocation> visitedStream = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getUserLocation")
                        .queryParam("userId", userId)
                        .build())
                .retrieve().bodyToMono(new ParameterizedTypeReference<VisitedLocation>() {});

        return visitedStream.block();
    }


    public VisitedLocation getUserLocation(UUID userId) throws IOException {
        String ResourceUrl = "http://17.18.0.2:8050/getUserLocation";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ResourceUrl)
                .queryParam("userId", userId);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.getBody(), new TypeReference<VisitedLocation>(){});
    }





}
