package kr.dict.trie;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("사용법: cd C:/Users/user/Downloads/kr-dict-game_1/" +
                    "./run.bat");

            return;
        }

        Trie trie = new Trie();

        long t0 = System.currentTimeMillis();
        int count = DictionaryLoader.load(trie, args[0]);
        long t1 = System.currentTimeMillis();
        System.err.printf("%d개 항목 로딩 완료 (%.2f초)%n", count, (t1 - t0) / 1000.0);

        for (String w : new String[]{"사과", "대한민국", "학교", "없는단어zzz"}) {
            System.out.printf("[존재?] %s -> %s%n", w, trie.contains(w) ? "있음" : "없음");
        }

        System.out.println("[자동완성] '사과'로 시작하는 단어 (최대 10개):");
        for (String w : trie.autocomplete("사과", 10)) {
            System.out.println("  " + w);
        }
    }
}
