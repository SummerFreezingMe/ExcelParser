package org.example;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "plans")
public class Plan {

    boolean countInPlan;
    @Id
    String index;

    String name;

    String cf_exam;

    String  cf_midterm;

    String  cf_kp;

    String cf_coursework;

    String cf_controlwork;

    String cf_midtermwithmark;

    String ze_expert;

    String ze_factual;

    String ah_expert;

    String ah_plan;

    String ah_controlwork;

    String ah_aud;

    String ah_sr;

    String ah_control;

    String ah_preparation;

    @ElementCollection
  List<String> courses_semesters;

    String faculty_code;

    String faculty_name;
}
