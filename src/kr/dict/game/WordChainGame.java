package kr.dict.game;

import kr.dict.trie.Trie;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 끝말잇기 한 판의 상태와 규칙을 담당하는 클래스.
 * Trie(사전)는 여러 게임에서 재사용할 수 있게 순수 조회용으로만 쓰고,
 * "이번 판에 어떤 단어가 나왔는지" 같은 게임 상태는 이 클래스가 따로 들고 있는다.
 * (사전 클래스에 게임 상태를 섞으면 사전을 한 번 로딩해서 여러 판을 돌릴 수 없게 된다.)
 */
public class WordChainGame {

    private final Trie dictionary;
    private final Set<String> usedWords = new HashSet<>();
    private String lastWord;   // 직전에 나온 단어. 게임 시작 전이면 null
    private boolean over = false;

    public WordChainGame(Trie dictionary) {
        this.dictionary = dictionary;
    }

    /** 단어 하나를 제출한다. 결과에 따라 내부 상태(사용된 단어, 마지막 단어)를 갱신한다. */
    public MoveResult submit(String word) {
        if (over) {
            return MoveResult.GAME_OVER;
        }
        if (word.codePointCount(0, word.length()) < 2) {
            return MoveResult.TOO_SHORT;
        }
        if (!dictionary.contains(word)) {
            return MoveResult.NOT_A_WORD;
        }
        if (usedWords.contains(word)) {
            return MoveResult.ALREADY_USED;
        }
        if (lastWord != null && !startsWithLastChar(word)) {
            return MoveResult.WRONG_START;
        }

        usedWords.add(word);
        lastWord = word;

        // 다음 사람이 이어받을 단어가 (아직 안 쓴 것 중에) 하나도 없으면 여기서 게임 종료
        if (nextCandidates(1).isEmpty()) {
            over = true;
        }
        return MoveResult.OK;
    }

    private boolean startsWithLastChar(String word) {
        int lastCp = lastWord.codePointBefore(lastWord.length());
        int firstCp = word.codePointAt(0);
        return lastCp == firstCp; // 두음법칙 보정은 별도 유틸에서 처리 (예: DueumRule)
    }

    /**
     * 다음 사람이 낼 수 있는 단어 후보를 최대 limit개 준다(힌트/AI 상대용).
     * 이미 쓰인 단어는 걸러낸다.
     */
    public List<String> nextCandidates(int limit) {
        if (lastWord == null) return List.of();
        int lastCp = lastWord.codePointBefore(lastWord.length());
        String prefix = new String(Character.toChars(lastCp));
        // 이미 쓰인 단어가 섞여 있을 수 있으니 여유 있게 뽑아서 걸러낸다
        return dictionary.autocomplete(prefix, Math.max(limit * 5, 50)).stream()
                .filter(w -> !usedWords.contains(w))
                .filter(w -> w.codePointCount(0, w.length()) >= 2) // 한 글자 단어는 후보에서 제외
                .limit(limit)
                .toList();
    }

    public boolean isOver() {
        return over;
    }

    public String lastWord() {
        return lastWord;
    }

    public int usedCount() {
        return usedWords.size();
    }
}
