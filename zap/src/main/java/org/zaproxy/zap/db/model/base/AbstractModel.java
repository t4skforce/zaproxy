package org.zaproxy.zap.db.model.base;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Version;

public abstract class AbstractModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Version
    @Column(name = "VERSION")
    private Long version;

}
