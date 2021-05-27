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
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "structure")
@Table(name = "STRUCTURE")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Structure implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STRUCTUREID")
    private Long id;

    @NonNull
    @Column(name = "SESSIONID", nullable = false)
    private Integer session;

    @NonNull
    @Column(name = "PARENTID", nullable = false)
    private Integer parent;

    @NonNull
    @Column(name = "HISTORYID", nullable = false)
    private Integer history;

    @NonNull
    @Column(name = "NAME", nullable = false)
    private String name;

    @NonNull
    @Column(name = "NAMEHASH", nullable = false)
    private Integer nameHash;

    @NonNull
    @Column(name = "URL", nullable = false)
    private String url;

    @NonNull
    @Column(name = "METHOD", nullable = false, length = 10)
    private String method;

}
