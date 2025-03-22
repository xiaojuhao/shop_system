package com.xjh.common.utils;

import java.util.HashMap;
import java.util.Map;

public class TrieHelper {
    private static class TrieNode {
        Map<Character, TrieNode> children;
        boolean isEndOfWord;

        TrieNode() {
            children = new HashMap<>();
            isEndOfWord = false;
        }
    }

    private final TrieNode root;

    public TrieHelper() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            current = current.children.computeIfAbsent(ch, c -> new TrieNode());
        }
        current.isEndOfWord = true;
    }

    public boolean search(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            current = current.children.get(ch);
            if (current == null) {
                return false;
            }
        }
        return current.isEndOfWord;
    }

    public boolean startsWith(String prefix) {
        TrieNode current = root;
        for (char ch : prefix.toCharArray()) {
            current = current.children.get(ch);
            if (current == null) {
                return false;
            }
        }
        return true;
    }

    public static void test() {
        TrieHelper trie = new TrieHelper();

        // 插入单词
        trie.insert("apple");
        trie.insert("app");
        trie.insert("banana");

        // 测试搜索功能
        System.out.println("Search 'apple': " + trie.search("apple")); // true
        System.out.println("Search 'app': " + trie.search("app"));     // true
        System.out.println("Search 'appl': " + trie.search("appl"));   // false

        // 测试前缀搜索功能
        System.out.println("StartsWith 'app': " + trie.startsWith("app")); // true
        System.out.println("StartsWith 'ban': " + trie.startsWith("ban")); // true
        System.out.println("StartsWith 'bat': " + trie.startsWith("bat")); // false

        // 测试不存在的单词
        System.out.println("Search 'orange': " + trie.search("orange")); // false
    }

    public static void test2(){
        TrieHelper trie = new TrieHelper();

        // 插入中文词语
        trie.insert("中国");
        trie.insert("中华");
        trie.insert("美食");
        trie.insert("美味");

        // 测试搜索功能
        System.out.println("搜索 '中国': " + trie.search("中国"));     // true
        System.out.println("搜索 '中华': " + trie.search("中华"));     // true
        System.out.println("搜索 '中': " + trie.search("中"));         // false
        System.out.println("搜索 '美味': " + trie.search("美味"));     // true

        // 测试前缀搜索功能
        System.out.println("前缀匹配 '中': " + trie.startsWith("中")); // true
        System.out.println("前缀匹配 '美': " + trie.startsWith("美")); // true
        System.out.println("前缀匹配 '华': " + trie.startsWith("华")); // false

        // 测试不存在的词语
        System.out.println("搜索 '日本': " + trie.search("日本"));     // false
    }


    public static void test3(){
        // 测试案例，使用中文复杂
    }

    public static void main(String[] args) {
        test3();
    }
}
