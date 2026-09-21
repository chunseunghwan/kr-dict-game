package kr.dict.trie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 트라이의 노드 하나.
 * 자식은 "유니코드 코드포인트(한 글자) -> 자식 노드" 해시맵으로 관리한다.
 * 한글 음절 공간(11,172자)만큼 고정 배열을 쓰면 노드당 메모리가 너무 커지므로,
 * 실제로 등장한 글자만 저장하는 HashMap 방식이 훨씬 실용적이다.
 */
class TrieNode {

    private final Map<Integer, TrieNode> children = new HashMap<>();
    private boolean end = false;
    private List<String> posList; // 이 단어에 달린 품사들 (동음이의어 대비, 필요 시에만 생성)

    Map<Integer, TrieNode> children() {
        return children;
    }

    boolean isEnd() {
        return end;
    }

    void markEnd() {
        this.end = true;
    }

    List<String> posList() {
        return posList == null ? List.of() : posList;
    }

    void addPos(String pos) {
        if (posList == null) posList = new ArrayList<>();
        if (!posList.contains(pos)) posList.add(pos);
    }
}
