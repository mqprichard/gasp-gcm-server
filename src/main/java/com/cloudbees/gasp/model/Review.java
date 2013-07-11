package com.cloudbees.gasp.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Model class for Gasp Review data
 * Used with Gson for Java-JSON mapping
 */
@XmlRootElement
public class Review {
    private int id;
    private String comment;
    private String star;
    private int restaurant_id;
    private int user_id;

    public int getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public String getStar() {
        return star;
    }

    public int getRestaurant_id() {
        return restaurant_id;
    }

    public int getUser_id() {
        return user_id;
    }
}
