package com.amybertken.chores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class ChoresController {


    private ChoreRepository choreRepository;

    @Autowired
    public ChoresController (final ChoreRepository choreRepository) {
        this.choreRepository = choreRepository;
    }

    @DeleteMapping("/chores/{id}")
    public String delete(@PathVariable Long id) {
        try {
            choreRepository.deleteById(id);
            return "Chore with id " + id + " successfully deleted";
        } catch(Exception e) {
            throw new ChoreNotFoundException(id);
        }
    }

    @GetMapping("/chores")
    public Iterable<Chore> index() {
        return choreRepository.findAll();
    }

    @PostMapping("/chores")
    public ResponseEntity<Object> create(@RequestParam String description){
        Chore chore = new Chore(description);
        System.out.println(chore.getDescription());
        System.out.println(choreRepository.getClass().toString());
        Chore savedChore = choreRepository.save(chore);
        System.out.println(savedChore.getDescription());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedChore.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/chores/{id}")
    @ResponseBody
    public Chore get(@PathVariable Long id) {
        return choreRepository.findById(id).orElseThrow(() -> new ChoreNotFoundException(id));
    }
}
