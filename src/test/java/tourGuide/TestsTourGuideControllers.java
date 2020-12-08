package tourGuide;

import org.junit.Test;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;
import tourGuide.Modeles.User;
import tourGuide.Modeles.UserPreferences;
import tourGuide.controlers.TourGuideController;
import tourGuide.service.TourGuideService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

    @SpringBootTest
    @AutoConfigureMockMvc
    public class TestsTourGuideControllers {

        private User user;
        private UserPreferences userPreferences;

        @Autowired
        MockMvc mockMvc;

        @Test
        public void getNearbyAttractions() throws Exception {
            // Arange & Act
/*            user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
            userPreferences =new UserPreferences(1,"US",0.0d,1000.0d,5,2,1,1);
            user.setUserPreferences(userPreferences);*/

            mockMvc.perform(get("http://localhost:8090/"))
//                    .param("userName", "jon"))
                    // Assert
                    .andExpect(status().isOk());
        }
/*
        @Test
        public void getBidListAdd() throws Exception {
            // Arange & Act
            this.mockMvc.perform(get("/bidList/add"))
                    // Assert
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andDo(MockMvcResultHandlers.print());
        }


        @Test
        public void getBidListValidateOk() throws Exception {
            // Arange & Act
            this.mockMvc.perform(post("/bidList/validate")
                    .param("bidListId", "1")
                    .param("account", "NameTests")
                    .param("type", "Desctests")
                    .param("bidQuantity", "10"))
                    // Assert
                    .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                    .andDo(MockMvcResultHandlers.print());
        }
        @Test
        public void getBidListValidateKo() throws Exception {
            // Arange & Act
            this.mockMvc.perform(post("/bidList/validate")
                    .param("account", "")
                    .param("type", "Desctests")
                    .param("bidQuantity", "10"))
                    // Assert
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("bidList/add"))
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        public void getBidListUpdate() throws Exception {
            // Arange & Act
            this.mockMvc.perform(get("/bidList/update/{id}", "1"))
                    // Assert
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        public void postBidListUpdate() throws Exception {
            // Arange & Act
            this.mockMvc.perform(post("/bidList/update/{id}", "1")
                    .param("account", "NameTestsUpdate")
                    .param("type", "DescTestsUpdate")
                    .param("bidQuantity", "50"))
                    // Assert
                    .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        public void postBidListUpdateKo() throws Exception {
            // Arange & Act
            this.mockMvc.perform(post("/bidList/update/{id}", "1")
                    .param("account", "")
                    .param("type", "DescTestsUpdate")
                    .param("bidQuantity", "50"))
                    // Assert
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("bidList/update"))
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        public void BidListDelete() throws Exception {
            // Arange & Act
            this.mockMvc.perform(get("/bidList/delete/{id}", "2"))
                    // Assert
                    .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                    .andDo(MockMvcResultHandlers.print());
        }

    }*/







}
