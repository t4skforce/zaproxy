package org.zaproxy.zap.db.schema;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "plugnhack_message")
@Table(name = "PLUGNHACK_MESSAGE")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlugNHackMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NonNull
    @Column(name = "TIMESTAMP", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @NonNull
    @Column(name = "CLIENT_ID", nullable = false, length = 255)
    private String clientId;

    @NonNull
    @Column(name = "STATE", nullable = false)
    private Integer state;

    @NonNull
    @Column(name = "MESSAGE", nullable = false)
    private String message;

    @NonNull
    @Column(name = "CHANGED", nullable = false)
    private Boolean changed;

}
