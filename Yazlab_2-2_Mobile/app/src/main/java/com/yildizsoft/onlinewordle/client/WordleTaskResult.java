package com.yildizsoft.onlinewordle.client;

public enum WordleTaskResult
{
    START_SERVER_SUCCESS,
    START_SERVER_FAIL,
    SIGNUP_SUCCESS,
    SIGNUP_FAIL_USER_ALREADY_EXISTS,
    SIGNUP_FAIL_OTHER,
    LOGIN_SUCCESS,
    LOGIN_FAIL_USERNAME_NOT_FOUND,
    LOGIN_FAIL_WRONG_PASSWORD,
    LOGIN_FAIL_OTHER,
    ENTER_LOBBY_SUCCESS,
    ENTER_LOBBY_FAIL,
    PLAYER_LIST_SUCCESS,
    PLAYER_LIST_FAIL_NO_PLAYERS,
    PLAYER_LIST_FAIL_LOGIN_REQUIRED,
    PLAYER_LIST_FAIL_OTHER,
}
