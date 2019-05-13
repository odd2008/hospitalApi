package com.hospital.tools;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class MailHelper {
    //引入javaMailSender
    private JavaMailSender mailSender;

    public MailHelper(JavaMailSender mailSender){
        this.mailSender=mailSender;
    }



    public void asyncSendMail(String from,String to,String subject,String text){
        Thread thread=new MailThread(from,to,subject,text);
        thread.start();
    }

    class MailThread extends Thread{
        private String from;
        private String to;
        private String subject;
        private String text;
        public MailThread(String from,String to,String subject,String text){
            this.from=from;
            this.to=to;
            this.subject=subject;
            this.text=text;
        }
        public void run(){
            sendSimpleMail(from,to,subject,text);
        }
        public void sendSimpleMail(String from,String to,String subject,String text){
            MimeMessage mimeMailMessage=mailSender.createMimeMessage();

            MimeMessageHelper helper= null;
            try {
                helper = new MimeMessageHelper(mimeMailMessage,true);
                helper.setFrom(from);
                helper.setTo(to.split(","));
//        if (StringUtils.isNotBlank(cc)){
//            helper.setCc(cc.split(","));
//        }
                mimeMailMessage.setSubject(subject);
                mimeMailMessage.setText(text);
                mailSender.send(mimeMailMessage);
            } catch (MessagingException e) {
                e.printStackTrace();
            }


        }
    }
}
