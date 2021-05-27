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
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "websocket_message")
@Table(name = "WEBSOCKET_MESSAGE")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @NonNull
    @Column(name = "MESSAGE_ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "SYS_FK_10164"), name = "CHANNEL_ID", referencedColumnName = "CHANNEL_ID")
    private WebSocketChannel channel;

    @NonNull
    @Column(name = "TIMESTAMP", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @NonNull
    @Column(name = "OPCODE", nullable = false)
    private Integer opcode;

    @Column(name = "PAYLOAD_UTF8")
    private String payloadString;

    @Column(name = "PAYLOAD_BYTES")
    private byte[] payloadBytes;

    @NonNull
    @Column(name = "PAYLOAD_LENGTH", nullable = false)
    private Integer payloadLength;

    @NonNull
    @Column(name = "IS_OUTGOING", nullable = false)
    private Boolean outgoing;

}
