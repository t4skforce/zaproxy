package org.zaproxy.zap.db.schema;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "websocket_message_fuzz")
@Table(name = "WEBSOCKET_MESSAGE_FUZZ")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessageFuzz implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "FUZZ_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "SYS_FK_10181"), name = "MESSAGE_ID", referencedColumnName = "MESSAGE_ID")
    private WebSocketMessage message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "SYS_FK_10181"), name = "CHANNEL_ID", referencedColumnName = "CHANNEL_ID")
    private WebSocketChannel channel;

    @NonNull
    @Column(name = "STATE", nullable = false, length = 50)
    private String state;

    @NonNull
    @Column(name = "FUZZ", nullable = false)
    private String name;

}
