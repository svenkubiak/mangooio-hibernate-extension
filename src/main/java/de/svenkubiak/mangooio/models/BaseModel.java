package de.svenkubiak.mangooio.models;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseModel {
    @Id @GeneratedValue
    @Column(name = "id")
    private int id;

    public int getId() {
        return id;
    }
}