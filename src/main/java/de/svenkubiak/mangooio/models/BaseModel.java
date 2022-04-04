package de.svenkubiak.mangooio.models;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * 
 * @author svenkubiak
 *
 */
@MappedSuperclass
public class BaseModel {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    public int getId() {
        return id;
    }
}