package tourGuide.service;

import Modeles.Attraction;
import Modeles.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.UUID;


/**
 * Service permettant d'acceder a l'API externe GPSutil
 *
 */


@Service
public class GpsUtilServiceWebClient {


    /**
     *
     * @return : Retour de la Liste de toutes les Attractions
     */

    WebClient webClient = WebClient.create("http://17.18.0.2:8050");

    public List<Attraction>  getAttractions() {

        Mono<List<Attraction>> attractionStream = webClient
                .get()
                .uri("/getAttractions")
                .retrieve().bodyToMono(new ParameterizedTypeReference<List<Attraction>>() {});

        return attractionStream.block();
    }


    /**
     *
     * @param userId : parametre a transmettre a l'API pour obtenir la Localisation de l'utilisateur
     * @return : Retour de la localisation
     */

    public VisitedLocation getUserLocation(UUID userId) {

        Mono<VisitedLocation> visitedStream = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getUserLocation")
                        .queryParam("userId", userId)
                        .build())
                .retrieve().bodyToMono(new ParameterizedTypeReference<VisitedLocation>() {});

        return visitedStream.block();
    }
}

