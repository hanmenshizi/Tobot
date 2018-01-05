package com.tobot.tobot.db.model;

import com.tobot.tobot.sqlite.annotation.Column;
import com.tobot.tobot.sqlite.annotation.Id;
import com.tobot.tobot.sqlite.annotation.Table;

/**
 * Created by Javen on 2017/12/13.
 */
@Table(name = "tab_tobot_answer")
public class Answer {

    @Id(name = "keyId")
    public String keyId ;

//    @Column(name = "id")
//    private int id;
//
//    @Column(name = "type")
//    private int type;
//
//    @Column(name = "robotId")
//    private String robotId;
//
//    @Column(name = "robot_new")
//    private String robot_new;

    @Column(name = "question")
    private String question;

    @Column(name = "answer")
    private String answer;

//    @Column(name = "createTime")
//    private String createTime;
//
//    @Column(name = "valid")
//    private boolean valid;

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public int getType() {
//        return type;
//    }
//
//    public void setType(int type) {
//        this.type = type;
//    }
//
//    public String getRobotId() {
//        return robotId;
//    }
//
//    public void setRobotId(String robotId) {
//        this.robotId = robotId;
//    }
//
//    public String getRobot_new() {
//        return robot_new;
//    }
//
//    public void setRobot_new(String robot_new) {
//        this.robot_new = robot_new;
//    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

//    public String getCreateTime() {
//        return createTime;
//    }
//
//    public void setCreateTime(String createTime) {
//        this.createTime = createTime;
//    }
//
//    public boolean isValid() {
//        return valid;
//    }
//
//    public void setValid(boolean valid) {
//        this.valid = valid;
//    }
}
