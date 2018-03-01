package com.example.saiprasadgarimella.navigation;

/**
 * Created by sai prasad garimella on 09-02-2018.
 */

class ChatMessage {
    public String msgText;
    public String msgUser;



    public ChatMessage(String msgText, String msgUser){
        this.msgText = msgText;
        this.msgUser = msgUser;

    }


    public ChatMessage(){

    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public String getMsgUser() {
        return msgUser;
    }

    public void setMsgUser(String msgUser) {
        this.msgUser = msgUser;
    }

}
