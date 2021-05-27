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
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "context")
@Table(name = "CONTEXT_DATA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContextData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DATAID")
    private Long id;

    @NonNull
    @Column(name = "CONTEXTID", nullable = false)
    private Integer contextId;

    @NonNull
    @Column(name = "TYPE", nullable = false)
    private Integer type;

    @Column(name = "DATA")
    private String data;
}
