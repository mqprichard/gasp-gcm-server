package com.cloudbees.gasp.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Model class for Gasp! Users: the gasp-server database is the master
 * (schema as shown below), updates sent via WEAVE@cloud and REST/JSON
 *
 * +-------+--------------+------+-----+---------+----------------+
 * | Field | Type         | Null | Key | Default | Extra          |
 * +-------+--------------+------+-----+---------+----------------+
 * | id    | int(11)      | NO   | PRI | NULL    | auto_increment |
 * | name  | varchar(255) | YES  |     | NULL    |                |
 * +-------+--------------+------+-----+---------+----------------+
 *
 * @author Mark Prichard
 */
@XmlRootElement
public class User {
    private int id;
    private String name;

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
}
