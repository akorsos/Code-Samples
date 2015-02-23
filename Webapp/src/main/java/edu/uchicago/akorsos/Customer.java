/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uchicago.akoros;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;


@Named
@SessionScoped //you must change this to javax.enterprise.context.* when using @Named
public class Customer implements Serializable {

  /**
   * Creates a new instance of Customer
   */
  public Customer() {
  }

  private String firstName;
  private String lastName;
  private String email; 
  private String timeOfDay;
  private String typeOfCar;
  private Integer numberOfAdults;
  private Integer numberOfChildren;
  private String creditcard;
  private String address1;
  private String address2;
  private String city;
  private String state;
  private String zip;
  private String message; 
  private Date checkinDate;
  private Date checkoutDate;
  private String termsOfService;
  private String total = "100";
  private SimpleDateFormat sdf; 

  public SimpleDateFormat getSdf() {
    return sdf;
  }

  public void setSdf(SimpleDateFormat sdf) {
    this.sdf = sdf;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getTimeOfDay() {
    return timeOfDay;
  }

  public void setTimeOfDay(String timeOfDay) {
    this.timeOfDay = timeOfDay;
  }

  public String getTypeOfCar() {
    return typeOfCar;
  }

  public void setTypeOfCar(String typeOfCar) {
    this.typeOfCar = typeOfCar;
  }

  public Integer getNumberOfAdults() {
    return numberOfAdults;
  }

  public void setNumberOfAdults(Integer numberOfAdults) {
    this.numberOfAdults = numberOfAdults;
  }

  public Integer getNumberOfChildren() {
    return numberOfChildren;
  }

  public void setNumberOfChildren(Integer numberOfChildren) {
    this.numberOfChildren = numberOfChildren;
  }

  public String getCreditcard() {
    return creditcard;
  }

  public void setCreditcard(String creditcard) {
    this.creditcard = creditcard;
  }

  public String getAddress1() {
    return address1;
  }

  public void setAddress1(String address1) {
    this.address1 = address1;
  }

  public String getAddress2() {
    return address2;
  }

  public void setAddress2(String address2) {
    this.address2 = address2;
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

  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  public Date getCheckinDate() {
    return checkinDate;
  }

  public void setCheckinDate(Date checkinDate) {
    this.checkinDate = checkinDate;
  }

  public Date getCheckoutDate() {
    return checkoutDate;
  }

  public void setCheckoutDate(Date checkoutDate) {
    this.checkoutDate = checkoutDate;
  }

  public String getTermsOfService() {
    return termsOfService;
  }

  public void setTermsOfService(String termsOfService) {
    this.termsOfService = termsOfService;
  }

  public String getTotal() {
    return total;
  }

  public void setTotal(String total) {
    this.total = total;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
  

}
