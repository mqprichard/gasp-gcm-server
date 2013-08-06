package com.cloudbees.gasp.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Model class for Gasp! Restaurants: the gasp-server database is the master
 * (schema as shown below), updates sent via WEAVE@cloud and REST/JSON
 *
 * +---------+--------------+------+-----+---------+----------------+
 * | Field   | Type         | Null | Key | Default | Extra          |
 * +---------+--------------+------+-----+---------+----------------+
 * | id      | int(11)      | NO   | PRI | NULL    | auto_increment |
 * | address | varchar(255) | YES  |     | NULL    |                |
 * | name    | varchar(255) | YES  |     | NULL    |                |
 * | website | varchar(255) | YES  |     | NULL    |                |
 * +---------+--------------+------+-----+---------+----------------+
 *
 * @author Mark Prichard
 */
@XmlRootElement
public class Restaurant {
    private int id;
    private String name;
    private String website;
    private String address;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
