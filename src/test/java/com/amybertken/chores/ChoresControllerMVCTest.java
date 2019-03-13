package com.amybertken.chores;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;


import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(secure = false)
@ActiveProfiles("test")
public class ChoresControllerMVCTest {

    private ChoresController classUnderTest;

    @Autowired
    ChoreRepository choreRepository;

    @Autowired
    MockMvc mockMvc;

    @Before
    public void setup() {
        classUnderTest = new ChoresController(choreRepository);
    }

    @After
    public void clear () {
        choreRepository.deleteAll();
    }

    @Test
    public void createCallsChoreRepositorySave() throws Exception {
        mockMvc.perform(post("/chores")
                .param("description", "wake up")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201))
                .andExpect(header().string("Location", containsString("localhost")));
    }

    @Test
    public void getReturnsChore() throws Exception {
        Chore chore = new Chore("wake up");
        choreRepository.save(chore);
        mockMvc.perform(get("/chores/" + chore.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("wake up")));

    }

    @Test
    public void indexReturnsChore() throws Exception {
        Chore chore1 = new Chore("mop");
        choreRepository.save(chore1);
        Chore chore2 = new Chore ("feed the dog");
        choreRepository.save(chore2);

        List<Chore> chores = new ArrayList();
        chores.add(chore1);
        chores.add(chore2);

        mockMvc.perform(get("/chores")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].description", is("mop")));
    }

    @Test
    public void deleteRemovesChore() throws Exception {
        Chore chore1 = new Chore("mop");
        choreRepository.save(chore1);
        Chore chore2 = new Chore("feed the dog");
        choreRepository.save(chore2);

        List<Chore> chores = new ArrayList();
        chores.add(chore1);
        chores.add(chore2);

        mockMvc.perform(get("/chores")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(delete("/chores/" + chore1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(content().string("Chore with id " + chore1.getId() + " successfully deleted")
                );

        mockMvc.perform(get("/chores")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(delete("/chores/5"))
                .andExpect(status().is4xxClientError())
                .andExpect(status().reason(containsString("Chore not found")));
    }
}