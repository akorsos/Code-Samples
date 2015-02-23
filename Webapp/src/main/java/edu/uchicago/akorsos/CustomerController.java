/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uchicago.akorsos;

import edu.uchicago.akorsos.beans.BookingFacade;
import edu.uchicago.akorsos.jsf.BookingController;
import edu.uchicago.akorsos.jsf.PaymentController;
import edu.uchicago.akorsos.jsf.SubmitController;
import java.io.Serializable;
import static java.lang.ProcessBuilder.Redirect.from;
import static java.lang.ProcessBuilder.Redirect.to;
import java.util.Properties;
import javax.activation.*;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.*;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Named
@SessionScoped //you must change this to javax.enterprise.context.* when using @Named
public class CustomerController implements Serializable {

    @EJB
    private EmailSession es;
    @Inject
    private BookingController bc;
    @Inject
    private PaymentController pc;
    @Inject
    private SubmitController sc;

    /**
     * Creates a new instance of CustomerController
     */
    public CustomerController() {
    }

    public void saveCustomer(ActionEvent actionEvent) {
        FacesMessage facesMessage = new FacesMessage("You just ordered a car!");
        facesMessage.setSeverity(FacesMessage.SEVERITY_INFO);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
        bc.create();
        pc.create();
        sc.create();
        
        es.sendEmail(bc);
    }
}
