package org.zaproxy.zap.db.schema;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.parosproxy.paros.db.RecordHistory;
import org.parosproxy.paros.network.HttpMalformedHeaderException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "history")
@Table(name = "HISTORY", indexes = {
        @Index(columnList = "URI,METHOD,SESSIONID,HISTTYPE,HISTORYID,STATUSCODE", name = "HISTORY_INDEX") })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HISTORYID")
    private Long id;

    @Column(name = "SESSIONID", nullable = false)
    private Long sessionId;

    @Builder.Default
    @Column(name = "HISTTYPE")
    private int type = 1;

    @Builder.Default
    @Column(name = "STATUSCODE")
    private int statusCode = 0;

    @Builder.Default
    @Column(name = "TIMESENTMILLIS")
    private Long timeSentMillis = 0L;

    @Builder.Default
    @Column(name = "TIMEELAPSEDMILLIS")
    private Long timeElapsedMillis = 0L;

    @Builder.Default
    @Column(name = "METHOD", length = 1024)
    private String method = StringUtils.EMPTY;

    @Column(name = "URI")
    private String uri;

    @Column(name = "REQHEADER")
    private String requestHeader;

    @Column(name = "REQBODY")
    private String requestBody;

    @Column(name = "RESHEADER")
    private String responseHeader;

    @Column(name = "RESBODY")
    private String responseBody;

    @Column(name = "TAG")
    private String tag;

    @Column(name = "NOTE")
    private String note;

    @Builder.Default
    @Column(name = "RESPONSEFROMTARGETHOST")
    private Boolean responseFromTargetHost = Boolean.FALSE;

    public RecordHistory toRecord() throws HttpMalformedHeaderException {
        return new RecordHistory(getId().intValue(), getType(), getSessionId().intValue(),
                getTimeSentMillis().intValue(), getTimeElapsedMillis().intValue(), getRequestHeader(),
                getRequestBody().getBytes(StandardCharsets.UTF_8), getResponseHeader(),
                getResponseBody().getBytes(StandardCharsets.UTF_8), getTag(), getNote(), getResponseFromTargetHost());
    }

}
