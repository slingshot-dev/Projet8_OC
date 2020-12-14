package tourGuide.service;

import Modeles.Provider;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Service permettant d'acceder a l'API externe TripPricer
 *
 */

@Service
public class TripPricerService {

    /**
     *
     * @param apiKey : Apikey
     * @param attractionId : Id de l'Attraction
     * @param adults : Nbr d'adultes
     * @param children : Nbr d'enfants
     * @param nightsStay : Nbr de nuits du sejout
     * @param rewardsPoints : Points asscoiés a l'Attraction
     * @return : Retourne la Liste de Provider
     * @throws IOException : Exception
     */

    public List<Provider> getPrice(String apiKey, UUID attractionId, int adults, int children, int nightsStay, int rewardsPoints) throws IOException {
        String ResourceUrl = "http://17.18.0.4:8060/getPrice";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ResourceUrl)
                .queryParam("apiKey", apiKey)
                .queryParam("attractionId", attractionId)
                .queryParam("adults", adults)
                .queryParam("children", children)
                .queryParam("nightsStay", nightsStay)
                .queryParam("rewardsPoints", rewardsPoints);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.getBody(), new TypeReference<List<Provider>>(){});
    }

}
