package ai.openfabric.api.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "worker")
public class Worker extends Datable implements Serializable {

    @Id
    @Getter
    @Setter
    @Column
    public String id;
    @Getter
    @Setter
    @Column
    public String image;
    @Getter
    @Setter
    @Column
    public String imageId;
    @Getter
    @Setter
    @Column
    public String command;
    @Getter
    @Setter
    @Column
    public String state;
    @Getter
    @Setter
    @Column
    public String status;
    @Getter
    @Setter
    @Column
    public String labels;
    @Getter
    @Setter
    public String network;
    @Getter
    @Setter
    @Column
    public String hostConfig;
    @Getter
    @Setter
    @Column
    public String ports;
    @Getter
    @Setter
    @Column
    public String name;

    @Override
    public String toString() {
        return "Worker{" +
                "id='" + id + '\'' +
                ", image='" + image + '\'' +
                ", imageId='" + imageId + '\'' +
                ", command='" + command + '\'' +
                ", state='" + state + '\'' +
                ", status='" + status + '\'' +
                ", labels='" + labels + '\'' +
                ", network='" + network + '\'' +
                ", hostConfig='" + hostConfig + '\'' +
                ", ports='" + ports + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

