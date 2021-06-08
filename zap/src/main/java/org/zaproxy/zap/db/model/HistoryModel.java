package org.zaproxy.zap.db.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.parosproxy.paros.db.RecordHistory;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.zaproxy.zap.db.model.base.AbstractModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "HISTORY")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "history")
public class HistoryModel extends AbstractModel {

    private static final long serialVersionUID = -1559584596221855591L;

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

    // NOTE: Loading big data lazily makes sense when moving to new models, at the
    // moment it has no impact on performance
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "REQBODY")
    private byte[] requestBody;

    @Column(name = "RESHEADER")
    private String responseHeader;

    // NOTE: Loading big data lazily makes sense when moving to new models, at the
    // moment it has no impact on performance
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "RESBODY")
    private byte[] responseBody;

    @Column(name = "TAG")
    private String tag;

    @Column(name = "NOTE")
    private String note;

    @Builder.Default
    @Column(name = "RESPONSEFROMTARGETHOST")
    private Boolean responseFromTargetHost = Boolean.FALSE;

    @Builder.Default
    @OneToMany(mappedBy = "history", fetch = FetchType.LAZY)
    private List<TagModel> tags = new ArrayList<>();

    /**
     * Legacy support for zapproxy models
     *
     * @deprecated (2.10.1) Replaced by
     *             {@link org.zaproxy.zap.db.model.HistoryModel}
     */
    @Deprecated
    public RecordHistory toRecord() throws HttpMalformedHeaderException {
        return new RecordHistory(getId().intValue(), getType(), getSessionId().intValue(),
                getTimeSentMillis().intValue(), getTimeElapsedMillis().intValue(), getRequestHeader(),
                Optional.ofNullable(getRequestBody()).orElse(new byte[] {}), getResponseHeader(),
                Optional.ofNullable(getResponseBody()).orElse(new byte[] {}), getTag(), getNote(),
                getResponseFromTargetHost());
    }

}
