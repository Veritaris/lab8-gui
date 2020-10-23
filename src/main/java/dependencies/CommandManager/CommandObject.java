package dependencies.CommandManager;


import dependencies.Collection.StudyGroup;
import dependencies.UserAuthorization.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("UnusedReturnValue")
public class CommandObject implements Serializable {

    private HashMap<String, String> body = new HashMap<String, String>(){{
        put("status", "");
        put("message", "");
    }};
    private StudyGroup studyGroup;
    private String stringArgument;
    private Long groupID;

    private ArrayList<StudyGroup> studyGroups;

    private User user = new User("", "");

    private boolean isScripted = false;
    private boolean isFailed = false;
    private String failReason;
    private String name;
    private static final long serialVersionUID = 8599384652358861241L;

    public CommandObject(String name, StudyGroup studyGroup){
        this.name = name;
        this.studyGroup = studyGroup;
    }

    public CommandObject(String name, Long groupID){
        this.name = name;
        this.groupID = groupID;
    }

    public CommandObject(String name, String stringArgument){
        this.name = name;
        this.stringArgument = stringArgument;
    }

    public CommandObject(String name){
        this.name = name;
    }

    public CommandObject(String command, String username, String rawPassword) {
        this.name = command;
        this.user = new User(username, rawPassword);
    }

    public ArrayList<StudyGroup> getStudyGroups() {
        return studyGroups;
    }

    public void setStudyGroups(ArrayList<StudyGroup> studyGroups) {
        this.studyGroups = studyGroups;
    }

    public HashMap<String, String> getBody() {
        return this.body;
    }

    public void setBody(HashMap<String, String> body) {
        this.body = body;
    }

    public String getName() {
        return this.name;
    }

    public StudyGroup getStudyGroup() {
        return this.studyGroup;
    }

    public Long getGroupID() {
        return this.groupID;
    }

    public String getStringArgument() {
        return this.stringArgument;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFailed() {
        return this.isFailed;
    }

    public void setIsFailed(boolean isFailed) {
        this.isFailed = isFailed;
    }

    public String getFailReason() {
        return this.failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public void setSender(User user) {
        this.user = user;
    }

    public User getSender() {
        return this.user;
    }

    @Override
    public String toString(){
        return String.format("CommandObject<" +
                "command=\"%s\"", getName()) +
                ((getStudyGroup() != null) ? String.format(", firstArgument='%s'", getStudyGroup()) : "") +
                ((getGroupID() != null) ? String.format(", secondArgument='%s'", getGroupID()) : "") +
                ((getStringArgument() != null) ? String.format(", thirdArgument='%s'", getStringArgument()) : "") +
                (String.format(", %s", getSender().toString())) +
                ">";

    }

    public boolean isScripted() {
        return this.isScripted;
    }

    public void setScripted(boolean scripted) {
        this.isScripted = scripted;
    }
}
