package tourGuide;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tourGuide.Modeles.User;
import tourGuide.Modeles.UserPreferences;
import tourGuide.controlers.GpsUtilController;
import tourGuide.service.TourGuideService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

    @SpringBootTest
    @AutoConfigureMockMvc
    @RunWith(SpringRunner.class)
    public class TestsTourGuideControllers {

        private User user;
        private UserPreferences userPreferences;

        @Autowired
        MockMvc mockMvc;

        @Autowired
        WebApplicationContext wac;

        @Autowired
        GpsUtilController gpsUtilController;

        @Autowired
        TourGuideService tourGuideService;


        @Before
        public void setup() {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        }


        @Test
        public void getAccueil() throws Exception {
            // Arange & Act
            mockMvc.perform(get("/"))
            // Assert
                    .andExpect(status().isOk())
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        public void getAllLocation() throws Exception {

            // Arange & Act
            mockMvc.perform(get("/getAllCurrentLocations"))
                    // Assert
                    .andExpect(status().isOk())
                    .andDo(MockMvcResultHandlers.print());
        }


        @Test
        public void getNearbyAttractions() throws Exception {
/*            user = tourGuideService.getUser("internalUser1");
            userPreferences =new UserPreferences(1,"US",0.0d,1000.0d,5,2,1,1);
            user.setUserPreferences(userPreferences);*/

            // Arange & Act
            mockMvc.perform(post("/getNearbyAttractions?userName=internalUser1"))
                    // Assert
                    .andExpect(status().isOk())
                    .andDo(MockMvcResultHandlers.print());

        }

}
