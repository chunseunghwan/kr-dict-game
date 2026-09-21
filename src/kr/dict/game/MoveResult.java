package kr.dict.game;

/** 한 수(단어 제출)의 결과. */
public enum MoveResult {
    OK,             // 유효한 단어, 게임 계속
    NOT_A_WORD,     // 사전에 없는 단어
    ALREADY_USED,   // 이번 게임에서 이미 나온 단어
    WRONG_START,    // 직전 단어의 끝 글자로 시작하지 않음
    GAME_OVER       // 이미 끝난 게임에 제출함
}
