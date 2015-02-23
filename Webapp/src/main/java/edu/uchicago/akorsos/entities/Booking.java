/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uchicago.akorsos.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "BOOKING")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Booking.findAll", query = "SELECT b FROM Booking b"),
    @NamedQuery(name = "Booking.findById", query = "SELECT b FROM Booking b WHERE b.id = :id"),
    @NamedQuery(name = "Booking.findByFirstname", query = "SELECT b FROM Booking b WHERE b.firstname = :firstname"),
    @NamedQuery(name = "Booking.findByLastname", query = "SELECT b FROM Booking b WHERE b.lastname = :lastname"),
    @NamedQuery(name = "Booking.findByEmail", query = "SELECT b FROM Booking b WHERE b.email = :email"),
    @NamedQuery(name = "Booking.findByTimeofday", query = "SELECT b FROM Booking b WHERE b.timeofday = :timeofday"),
    @NamedQuery(name = "Booking.findByCheckindate", query = "SELECT b FROM Booking b WHERE b.checkindate = :checkindate"),
    @NamedQuery(name = "Booking.findByCheckoutdate", query = "SELECT b FROM Booking b WHERE b.checkoutdate = :checkoutdate"),
    @NamedQuery(name = "Booking.findByTypeofcar", query = "SELECT b FROM Booking b WHERE b.typeofcar = :typeofcar"),
    @NamedQuery(name = "Booking.findByNumberofadults", query = "SELECT b FROM Booking b WHERE b.numberofadults = :numberofadults"),
    @NamedQuery(name = "Booking.findByNumberofchildren", query = "SELECT b FROM Booking b WHERE b.numberofchildren = :numberofchildren")})
public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Integer id;
    @Size(max = 255)
    @Column(name = "FIRSTNAME")
    private String firstname;
    @Size(max = 255)
    @Column(name = "LASTNAME")
    private String lastname;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 255)
    @Column(name = "EMAIL")
    private String email;
    @Size(max = 255)
    @Column(name = "TIMEOFDAY")
    private String timeofday;
    @Column(name = "CHECKINDATE")
    @Temporal(TemporalType.DATE)
    private Date checkindate;
    @Column(name = "CHECKOUTDATE")
    @Temporal(TemporalType.DATE)
    private Date checkoutdate;
    @Size(max = 255)
    @Column(name = "TYPEOFCAR")
    private String typeofcar;
    @Column(name = "NUMBEROFADULTS")
    private Integer numberofadults;
    @Column(name = "NUMBEROFCHILDREN")
    private Integer numberofchildren;

    public Booking() {
    }

    public Booking(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTimeofday() {
        return timeofday;
    }

    public void setTimeofday(String timeofday) {
        this.timeofday = timeofday;
    }

    public Date getCheckindate() {
        return checkindate;
    }

    public void setCheckindate(Date checkindate) {
        this.checkindate = checkindate;
    }

    public Date getCheckoutdate() {
        return checkoutdate;
    }

    public void setCheckoutdate(Date checkoutdate) {
        this.checkoutdate = checkoutdate;
    }

    public String getTypeofcar() {
        return typeofcar;
    }

    public void setTypeofcar(String typeofcar) {
        this.typeofcar = typeofcar;
    }

    public Integer getNumberofadults() {
        return numberofadults;
    }

    public void setNumberofadults(Integer numberofadults) {
        this.numberofadults = numberofadults;
    }

    public Integer getNumberofchildren() {
        return numberofchildren;
    }

    public void setNumberofchildren(Integer numberofchildren) {
        this.numberofchildren = numberofchildren;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Booking)) {
            return false;
        }
        Booking other = (Booking) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.uchicago.akorsos.entities.Booking[ id=" + id + " ]";
    }
    

    
}
