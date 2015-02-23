/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uchicago.akorsos.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "SUBMIT")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Submit.findAll", query = "SELECT s FROM Submit s"),
    @NamedQuery(name = "Submit.findByTeamId", query = "SELECT s FROM Submit s WHERE s.teamId = :teamId"),
    @NamedQuery(name = "Submit.findByMessage", query = "SELECT s FROM Submit s WHERE s.message = :message"),
    @NamedQuery(name = "Submit.findByTotal", query = "SELECT s FROM Submit s WHERE s.total = :total")})
public class Submit implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "TEAM_ID")
    private Integer teamId;
    @Size(max = 255)
    @Column(name = "MESSAGE")
    private String message;
    @Size(max = 255)
    @Column(name = "TOTAL")
    private String total;

    public Submit() {
    }

    public Submit(Integer teamId) {
        this.teamId = teamId;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (teamId != null ? teamId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Submit)) {
            return false;
        }
        Submit other = (Submit) object;
        if ((this.teamId == null && other.teamId != null) || (this.teamId != null && !this.teamId.equals(other.teamId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.uchicago.akorsos.entities.Submit[ teamId=" + teamId + " ]";
    }
    
    
}
