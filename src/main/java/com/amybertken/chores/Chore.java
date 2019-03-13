package com.amybertken.chores;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Chore {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String description;

    public Chore(){
    }

    public Chore(String description){
        this.description = description;
    }

    public Chore(long id, String description){
        this.id = id;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("Chore [id=%s, description=%s]", id, description);
    }


}
