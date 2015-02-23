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
@Table(name = "PAYMENT")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Payment.findAll", query = "SELECT p FROM Payment p"),
    @NamedQuery(name = "Payment.findByTeamId", query = "SELECT p FROM Payment p WHERE p.teamId = :teamId"),
    @NamedQuery(name = "Payment.findByCreditcard", query = "SELECT p FROM Payment p WHERE p.creditcard = :creditcard"),
    @NamedQuery(name = "Payment.findByAddressline1", query = "SELECT p FROM Payment p WHERE p.addressline1 = :addressline1"),
    @NamedQuery(name = "Payment.findByAddressline2", query = "SELECT p FROM Payment p WHERE p.addressline2 = :addressline2"),
    @NamedQuery(name = "Payment.findByCity", query = "SELECT p FROM Payment p WHERE p.city = :city"),
    @NamedQuery(name = "Payment.findByState", query = "SELECT p FROM Payment p WHERE p.state = :state"),
    @NamedQuery(name = "Payment.findByZipcode", query = "SELECT p FROM Payment p WHERE p.zipcode = :zipcode")})
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "TEAM_ID")
    private Integer teamId;
    @Size(max = 255)
    @Column(name = "CREDITCARD")
    private String creditcard;
    @Size(max = 255)
    @Column(name = "ADDRESSLINE1")
    private String addressline1;
    @Size(max = 255)
    @Column(name = "ADDRESSLINE2")
    private String addressline2;
    @Size(max = 255)
    @Column(name = "CITY")
    private String city;
    @Size(max = 255)
    @Column(name = "STATE")
    private String state;
    @Size(max = 255)
    @Column(name = "ZIPCODE")
    private String zipcode;

    public Payment() {
    }

    public Payment(Integer teamId) {
        this.teamId = teamId;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public String getCreditcard() {
        return creditcard;
    }

    public void setCreditcard(String creditcard) {
        this.creditcard = creditcard;
    }

    public String getAddressline1() {
        return addressline1;
    }

    public void setAddressline1(String addressline1) {
        this.addressline1 = addressline1;
    }

    public String getAddressline2() {
        return addressline2;
    }

    public void setAddressline2(String addressline2) {
        this.addressline2 = addressline2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
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
        if (!(object instanceof Payment)) {
            return false;
        }
        Payment other = (Payment) object;
        if ((this.teamId == null && other.teamId != null) || (this.teamId != null && !this.teamId.equals(other.teamId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.uchicago.akorsos.entities.Payment[ teamId=" + teamId + " ]";
    }
    
    
    
}
