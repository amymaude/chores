package com.amybertken.chores;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason = "Chore not found")
public class ChoreNotFoundException extends RuntimeException  {
    public ChoreNotFoundException(Long id) {
        super("Could not find chore " + id);
    }
}
