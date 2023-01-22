package uz.java.springbootfileuploaddownloaddemo.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Attachment{

    @Id
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String name;
    private String contentType;
    private int size;

    private String path;

    public Attachment(String name, String contentType, int size) {
        this.name = name;
        this.contentType = contentType;
        this.size = size;
    }

    public Attachment(String name, String contentType, int size, String path) {
        this.name = name;
        this.contentType = contentType;
        this.size = size;
        this.path = path;
    }
}
