package tourGuide.service;


import Modeles.Attraction;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Service
public class RewardCentral {

    public int getAttractionRewardPoints() throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        String ResourceUrl = "http://localhost:8070/getAttractionRewardPoints";
        ResponseEntity<String> response = restTemplate.getForEntity(ResourceUrl, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.getBody(), new TypeReference<Integer>(){});
    }
}
