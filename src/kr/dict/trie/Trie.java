package kr.dict.trie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 국어사전 트라이. TrieNode는 패키지 내부 구현이라 외부에는 이 클래스만 공개한다.
 */
public class Trie {

    private final TrieNode root = new TrieNode();

    /** 단어와 품사를 트라이에 넣는다. 같은 단어가 여러 품사로 중복 등록될 수 있다. */
    public void insert(String word, String pos) {
        TrieNode cur = root;
        for (int cp : word.codePoints().toArray()) {
            cur = cur.children().computeIfAbsent(cp, k -> new TrieNode());
        }
        cur.markEnd();
        if (pos != null && !pos.isEmpty()) {
            cur.addPos(pos);
        }
    }

    /** 해당 단어가 사전에 실제로 존재하는 완전한 단어인지 확인한다. */
    public boolean contains(String word) {
        TrieNode node = findNode(word);
        return node != null && node.isEnd();
    }

    /** 해당 단어에 달린 품사 목록을 돌려준다. 단어가 없으면 빈 리스트. */
    public List<String> posOf(String word) {
        TrieNode node = findNode(word);
        return node == null ? List.of() : node.posList();
    }

    /** prefix로 시작하는 단어를 최대 limit개까지 찾아준다 (자동완성). */
    public List<String> autocomplete(String prefix, int limit) {
        List<String> result = new ArrayList<>();
        TrieNode node = findNode(prefix);
        if (node == null) return result;
        StringBuilder sb = new StringBuilder(prefix);
        collect(node, sb, result, limit);
        return result;
    }

    private TrieNode findNode(String word) {
        TrieNode cur = root;
        for (int cp : word.codePoints().toArray()) {
            cur = cur.children().get(cp);
            if (cur == null) return null;
        }
        return cur;
    }

    private void collect(TrieNode node, StringBuilder sb, List<String> result, int limit) {
        if (result.size() >= limit) return;
        if (node.isEnd()) result.add(sb.toString());
        for (Map.Entry<Integer, TrieNode> e : node.children().entrySet()) {
            if (result.size() >= limit) return;
            int cp = e.getKey();
            sb.appendCodePoint(cp);
            collect(e.getValue(), sb, result, limit);
            sb.setLength(sb.length() - Character.charCount(cp));
        }
    }
}
