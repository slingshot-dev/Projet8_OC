package tourGuide.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;
import java.util.UUID;

/**
 * Service permettant d'acceder a l'API externe RewardCentral
 *
 */

@Service
public class RewardCentralService {

    /**
     *
     * @param attractionId : parametre id de l'Attraction a transmettre a l'API
     * @param userId : parametre a transmettre a l'API pour obtenir le reward Point de l'attraction
     * @return : Retour du Reward Point de l'Attraction
     * @throws IOException : Exception
     */

    public int getAttractionRewardPoints(UUID attractionId, UUID userId) throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        String ResourceUrl = "http://17.18.0.3:8070/getAttractionRewardPoints";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ResourceUrl)
                .queryParam("attractionId", attractionId)
                .queryParam("userId", userId);

        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.getBody(), new TypeReference<Integer>(){});
    }
}
