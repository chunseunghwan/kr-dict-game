package kr.dict.trie;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * kr_korean.csv 같은 "단어,품사" 형식의 사전 CSV를 읽어 Trie에 채워 넣는다.
 */
public class DictionaryLoader {

    /** path의 CSV를 읽어 trie에 삽입하고, 삽입한 줄 수를 반환한다. */
    public static int load(Trie trie, String path) throws IOException {
        int count = 0;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) {
                    // Java는 BOM을 자동으로 걷어내지 않으므로 첫 줄에서 직접 제거
                    if (!line.isEmpty() && line.charAt(0) == '﻿') {
                        line = line.substring(1);
                    }
                    first = false;
                }
                if (line.isEmpty()) continue;
                int comma = line.indexOf(',');
                if (comma < 0) continue;
                String word = line.substring(0, comma);
                String pos = line.substring(comma + 1);
                trie.insert(word, pos);
                count++;
            }
        }
        return count;
    }
}
