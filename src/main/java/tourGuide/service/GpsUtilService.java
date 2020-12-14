package tourGuide.service;

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
import reactor.core.publisher.Mono;
import java.io.IOException;
import java.util.List;
import java.util.UUID;


/**
 * Service permettant d'acceder a l'API externe GPSutil
 *
 */


@Service
public class GpsUtilService {

    private final Logger logger = LoggerFactory.getLogger(GpsUtilService.class);


    /**
     *
     * @return : Retour de la Liste de toutes les Attractions
     */

    public List<Attraction> getAttractions() throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        String ResourceUrl = "http://17.18.0.2:8050/getAttractions";
        ResponseEntity<String> response = restTemplate.getForEntity(ResourceUrl, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.getBody(), new TypeReference<List<Attraction>>(){});
    }



    /**
     *
     * @param userId : parametre a transmettre a l'API pour obtenir la Localisation de l'utilisateur
     * @return : Retour de la localisation
     */

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
