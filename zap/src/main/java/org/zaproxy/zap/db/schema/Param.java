package org.zaproxy.zap.db.schema;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "param")
@Table(name = "PARAM")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Param implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PARAMID")
    private Long id;

    @NonNull
    @Column(name = "SITE", nullable = false)
    private String site;

    @NonNull
    @Column(name = "TYPE", nullable = false)
    private String type;

    @NonNull
    @Column(name = "NAME", nullable = false)
    private String name;

    @NonNull
    @Column(name = "USED", nullable = false)
    private Integer used;

    @NonNull
    @Column(name = "FLAGS", nullable = false)
    private String falgs;

    @NonNull
    @Column(name = "VALS", nullable = false)
    private String vals;

}
