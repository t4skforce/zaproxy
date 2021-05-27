package org.zaproxy.zap.db.schema;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "websocket_channel")
@Table(name = "WEBSOCKET_CHANNEL")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketChannel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @NonNull
    @Column(name = "CHANNEL_ID", nullable = false)
    private Long id;

    @NonNull
    @Column(name = "HOST", nullable = false, length = 255)
    private String host;

    @Column(name = "PORT")
    private Integer port;

    @NonNull
    @Column(name = "URL", nullable = false, length = 255)
    private String url;

    @NonNull
    @Column(name = "START_TIMESTAMP", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date start;

    @NonNull
    @Column(name = "END_TIMESTAMP", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date end;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "SYS_FK_10149"), name = "HISTORY_ID", referencedColumnName = "HISTORYID")
    private History history;

}
