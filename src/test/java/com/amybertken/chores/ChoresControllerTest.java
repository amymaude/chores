package com.amybertken.chores;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.persistence.EntityNotFoundException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(secure = false)
@ActiveProfiles("test")
public class ChoresControllerTest {

    private ChoresController classUnderTest;

    @Mock
    ChoreRepository mockChoreRepository;


    @Before
    public void setup() {
        classUnderTest = new ChoresController(mockChoreRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }


    @Test
    public void createCallsChoreRepositorySave() throws Exception {
        Chore chore = new Chore("wake up");
        Mockito.when(mockChoreRepository.save(any(Chore.class))).thenReturn(chore);
        URI uri = new URI("http://localhost/0");
        ResponseEntity mockedResponse = ResponseEntity.created(uri).build();
        assertEquals(classUnderTest.create("wake up"), mockedResponse);
        Mockito.verify(mockChoreRepository, atLeastOnce()).save(any(Chore.class));
    }

    @Test
    public void getReturnsChore() {
        Chore chore = new Chore("wake up");
        Optional<Chore> opt = Optional.of(chore);
        Mockito.when(mockChoreRepository.findById(Long.valueOf(1))).thenReturn(opt);
        assertEquals(classUnderTest.get(Long.valueOf(1)), chore);
        Mockito.verify(mockChoreRepository, atLeastOnce()).findById(Long.valueOf(1));
        try {
            classUnderTest.get(Long.valueOf(5));
            Assert.fail("expected exception to be thrown");
        } catch(RuntimeException exception) {
            Exception mockException = new ChoreNotFoundException(Long.valueOf(5));
            assertThat(exception, instanceOf(ChoreNotFoundException.class));
            assertEquals(exception.getMessage(), mockException.getMessage());
        }

    }


    @Test
    public void indexReturnsAllChores() throws Exception{
        Chore chore1 = new Chore("eat dinner");
        Chore chore2 = new Chore("wash dishes");
        List<Chore> chores = new ArrayList();
        chores.add(chore1);
        chores.add(chore2);
        Mockito.when(mockChoreRepository.findAll()).thenReturn(chores);
        assertEquals(classUnderTest.index(), chores);
        Mockito.verify(mockChoreRepository, times(1)).findAll();
    }

    @Test
    public void deleteRemovesChore() throws Exception {
        Chore chore = new Chore ("delete chore");
        Mockito.doNothing().when(mockChoreRepository).deleteById(Long.valueOf(2));
        assertEquals(classUnderTest.delete(Long.valueOf(2)), "Chore with id 2 successfully deleted");
        Mockito.verify(mockChoreRepository, times(1)).deleteById(Long.valueOf(2));
        Mockito.doThrow(new RuntimeException()).when(mockChoreRepository).deleteById(Long.valueOf(10));
        Exception exception = new ChoreNotFoundException(Long.valueOf(10));
        try {
            classUnderTest.delete(Long.valueOf(10));
            Assert.fail("expected exception to be thrown");
        }catch(Exception e) {
            assertEquals(e.getMessage(), exception.getMessage());
        }
    }


}