package com.tasha.socialinfo.group;

import com.tasha.socialinfo.student.Student;
import com.tasha.socialinfo.user.User;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "student_groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private GroupCategory category;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "curator_id")
    private User curator;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Student> students;

    public Group() {
    }

    public Group(String code, GroupCategory category, User curator) {
        this.code = code;
        this.category = category;
        this.curator = curator;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public GroupCategory getCategory() {
        return category;
    }

    public void setCategory(GroupCategory category) {
        this.category = category;
    }

    public User getCurator() {
        return curator;
    }

    public void setCurator(User curator) {
        this.curator = curator;
    }

    public List<Student> getStudents() {
        return students;
    }
}
