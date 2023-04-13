package org.example;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "plans")
public class Titular {
    @Id
    String profile;
    @Id
    Long beginning_year;
    @Id
    String fgos;

    String program;

    public String toString(){
        return profile+"-"+beginning_year+"-"+fgos;
    }
}
