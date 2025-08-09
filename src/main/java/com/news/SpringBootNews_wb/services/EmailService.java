package com.news.SpringBootNews_wb.services;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    @Value("${spring.sendgrid.api.key}")
    private String api_key;

    @Value("${spring.sendgrid.from.email}")
    private String senderEmail;


    public void sendOtpEmail(String toEmail, String subject, String message)throws IOException {


        Email from = new Email(senderEmail);

        Email to = new Email(toEmail);
        
        Content content = new Content("text/plain", message);

        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(api_key);
        Request request = new Request();

        

        try  {

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

             
            Response response = sg.api(request);

    

            System.out.println("Email Status Code: " + response.getStatusCode());
            System.out.println("Email Body: " + response.getBody());
            System.out.println("Email Headers: " + response.getHeaders());
            
        } catch (Exception e) {
           e.printStackTrace();
        }

    }

}
