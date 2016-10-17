package tests.models.submodels;

import javax.persistence.Entity;

import de.svenkubiak.mangooio.models.BaseModel;

@Entity
public class Car extends BaseModel {
    private String name;

    public Car() {
    }

    public Car(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}