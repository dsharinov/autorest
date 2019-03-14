package com.intendia.gwt.autorest.example.client;

import com.google.common.base.MoreObjects;

import java.util.Date;

/**
 * Servers to pass object (e.g. Res or Client) expiration to the web client
 */
public class ObjLockExpiration {
    
    //TODO: add object type
    private Long id;
    private Date expiresAt;
    
    public ObjLockExpiration() {
    }
    
    public ObjLockExpiration(Long id, Date expiresAt) {
        this.id = id;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("id", id).add("expiresAt", expiresAt)
                .toString();
    }
    
}
