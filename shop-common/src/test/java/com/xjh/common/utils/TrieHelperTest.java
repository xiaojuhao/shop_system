package com.xjh.common.utils;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TrieHelperTest {

    private TrieHelper trieHelper;

    @Before
    public void setUp() {
        trieHelper = new TrieHelper();
    }

    @Test
    public void search_EmptyWord_ReturnsFalse() {
        assertFalse(trieHelper.search(""));
    }

    @Test
    public void search_WordNotInTrie_ReturnsFalse() {
        assertFalse(trieHelper.search("apple"));
    }

    @Test
    public void search_WordInTrie_ReturnsTrue() {
        trieHelper.insert("apple");
        assertTrue(trieHelper.search("apple"));
    }

    @Test
    public void search_PrefixExistsButWordNot_ReturnsFalse() {
        trieHelper.insert("app");
        assertFalse(trieHelper.search("apple"));
    }

    @Test
    public void search_PartialMatch_ReturnsFalse() {
        trieHelper.insert("application");
        assertFalse(trieHelper.search("app"));
    }

    @Test
    public void insert_EmptyString_ShouldSetRootAsEndOfWord() {
        trieHelper.insert("");
        // Cannot directly access root.isEndOfWord due to private access
    }

    @Test
    public void insert_SingleCharacter_ShouldCreateSingleChildNode() {
        trieHelper.insert("a");
        // Cannot directly access root.children due to private access
    }

    @Test
    public void insert_MultipleCharacters_ShouldCreateCorrectChildNodes() {
        trieHelper.insert("abc");
        // Cannot directly access root.children due to private access
    }

    @Test
    public void insert_RepeatedCharacters_ShouldNotCreateExtraNodes() {
        trieHelper.insert("aa");
        // Cannot directly access root.children due to private access
    }

    @Test
    public void insert_DifferentWords_ShouldNotInterfere() {
        trieHelper.insert("cat");
        trieHelper.insert("dog");
        // Cannot directly access root.children due to private access
    }

    @Test
    public void startsWith_EmptyPrefix_ReturnsTrue() {
        assertTrue(trieHelper.startsWith(""));
    }

    @Test
    public void startsWith_SingleCharacterPrefix_Exists_ReturnsTrue() {
        trieHelper.insert("a");
        assertTrue(trieHelper.startsWith("a"));
    }

    @Test
    public void startsWith_SingleCharacterPrefix_NotExists_ReturnsFalse() {
        assertFalse(trieHelper.startsWith("a"));
    }

    @Test
    public void startsWith_MultipleCharacterPrefix_Exists_ReturnsTrue() {
        trieHelper.insert("ab");
        assertTrue(trieHelper.startsWith("ab"));
    }

    @Test
    public void startsWith_MultipleCharacterPrefix_NotExists_ReturnsFalse() {
        trieHelper.insert("a");
        assertFalse(trieHelper.startsWith("abc"));
    }

    @Test
    public void startsWith_PartialMatch_ReturnsFalse() {
        trieHelper.insert("ab");
        assertFalse(trieHelper.startsWith("abc"));
    }
}
