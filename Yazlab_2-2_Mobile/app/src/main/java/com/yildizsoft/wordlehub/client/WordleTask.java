package com.yildizsoft.wordlehub.client;

import java.util.List;

public class WordleTask
{
    private long         id;
    private List<String> parameters;
    private Type         task;
    private Status       status;
    private Result       result;
    //private boolean isProcessing;
    
    public WordleTask(Type task, List<String> parameters)
    {
        this.parameters = parameters;
        this.task       = task;
        this.status     = Status.WAITING;
        this.result     = null;
        //this.isProcessing = false;
    }
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public List<String> getParameters()
    {
        return parameters;
    }
    
    public void setParameters(List<String> parameters)
    {
        this.parameters = parameters;
    }
    
    public Type getTask()
    {
        return task;
    }
    
    public void setTask(Type task)
    {
        this.task = task;
    }
    
    public Status getStatus()
    {
        return status;
    }
    
    public void setStatus(Status status)
    {
        this.status = status;
    }
    
    public Result getResult()
    {
        return result;
    }
    
    public void setResult(Result resultType)
    {
        this.result = resultType;
    }
    
    public static class Result
    {
        private ResultType   type;
        private List<String> parameters;
        
        public Result(ResultType type)
        {
            this.type       = type;
            this.parameters = null;
        }
        
        public Result(ResultType type, List<String> parameters)
        {
            this.type       = type;
            this.parameters = parameters;
        }
        
        public ResultType getType()
        {
            return type;
        }
        
        public void setType(ResultType type)
        {
            this.type = type;
        }
        
        public List<String> getParameters()
        {
            return parameters;
        }
        
        public void setParameters(List<String> parameters)
        {
            this.parameters = parameters;
        }
    }
    
    public enum ResultType
    {
        INVALID_RESULT,
        CONNECT_TO_SERVER_SUCCESS,
        CONNECT_TO_SERVER_FAIL,
        SIGNUP_SUCCESS,
        SIGNUP_FAIL_USER_ALREADY_EXISTS,
        SIGNUP_FAIL_OTHER,
        LOGIN_SUCCESS,
        LOGIN_FAIL_USERNAME_NOT_FOUND,
        LOGIN_FAIL_WRONG_PASSWORD,
        LOGIN_FAIL_USER_ALREADY_LOGGED_IN,
        LOGIN_FAIL_OTHER,
        LOGOUT_SUCCESS,
        LOGOUT_FAIL,
        ENTER_LOBBY_SUCCESS,
        ENTER_LOBBY_FAIL,
        EXIT_LOBBY_SUCCESS,
        EXIT_LOBBY_FAIL,
        PLAYER_LIST_SUCCESS,
        PLAYER_LIST_FAIL_NO_PLAYERS,
        PLAYER_LIST_FAIL_LOGIN_REQUIRED,
        PLAYER_LIST_FAIL_OTHER,
        SEND_GAME_REQUEST_SUCCESS,
        SEND_GAME_REQUEST_FAIL_ALREADY_REQUESTED,
        SEND_GAME_REQUEST_FAIL_NO_LONGER_ONLINE,
        SEND_GAME_REQUEST_FAIL_OTHER,
        NEW_REQUEST_FOUND,
        GAME_REQUEST_ACCEPTED,
        GAME_REQUEST_REJECTED,
        GAME_STARTS,
        TIMES_UP,
        WIN,
        LOSS,
        DRAW,
        VALID_WORD,
        INVALID_WORD,
        GAME_OVER,
        GAME_CONTINUES,
        REMATCH_ACCEPTED,
        REMATCH_DECLINED,
        NEW_REMATCH_OFFER,
        REMATCH_OFFER_SENT,
        RETURNED_TO_LOBBY,
    }
    
    public enum Type
    {
        CONNECT_TO_SERVER,
        QUIT,
        SIGNUP,
        LOGIN,
        LOGOUT,
        ENTER_LOBBY,
        EXIT_LOBBY,
        PLAYER_LIST,
        SEND_GAME_REQUEST,
        LISTEN_TO_GAME_REQUESTS,
        WAIT_GAME_REQUEST_RESPONSE,
        ACCEPT_GAME_REQUEST,
        REJECT_GAME_REQUEST,
        LISTEN_TO_ENTER_WORD_TIMER,
        PRE_GAME_SEND_WORD,
        IN_GAME_SEND_WORD,
        CHECK_GAME_STATUS,
        OFFER_REMATCH,
        WAIT_FOR_REMATCH_RESPONSE,
        LISTEN_TO_REMATCH_OFFERS,
        ACCEPT_REMATCH,
        DECLINE_REMATCH,
        RETURN_TO_LOBBY_AFTER_GAME
    }
    
    public enum Status
    {
        WAITING,
        IN_PROGRESS,
        COMPLETED
    }
}
