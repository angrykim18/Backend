package com.newez.backend.util;

public class ChosungUtils {

    private static final char[] CHO =
            {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};

    public static String extractChosung(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch >= 0xAC00 && ch <= 0xD7A3) { // 한글 음절 범위 체크
                int uniVal = ch - 0xAC00;
                int choIndex = uniVal / (21 * 28);
                sb.append(CHO[choIndex]);
            } else {
                sb.append(ch); // 한글이 아니면 그대로 추가
            }
        }
        return sb.toString();
    }
}