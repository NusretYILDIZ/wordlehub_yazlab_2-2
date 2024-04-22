package org.yildizsoft;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Wordstation
{
    private static List<String> words_4_letter, words_5_letter, words_6_letter, words_7_letter;
    
    public static void Start()
    {
        words_4_letter = new ArrayList<>();
        words_5_letter = new ArrayList<>();
        words_6_letter = new ArrayList<>();
        words_7_letter = new ArrayList<>();
        
        // 4-letter words
        System.out.println("[Wordstation] Loading 4-letter words...");
        
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader("./words/4_harfli_kelimeler.txt")))
        {
            String line;
            while((line = bufferedReader.readLine()) != null)
            {
                if(!line.isEmpty()) words_4_letter.add(line);
            }
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
        
        // 5-letter words
        System.out.println("[Wordstation] Loading 5-letter words...");
        
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader("./words/5_harfli_kelimeler.txt")))
        {
            String line;
            while((line = bufferedReader.readLine()) != null)
            {
                if(!line.isEmpty()) words_5_letter.add(line);
            }
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
        
        // 6-letter words
        System.out.println("[Wordstation] Loading 6-letter words...");
        
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader("./words/6_harfli_kelimeler.txt")))
        {
            String line;
            while((line = bufferedReader.readLine()) != null)
            {
                if(!line.isEmpty()) words_6_letter.add(line);
            }
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
        
        // 7-letter words
        System.out.println("[Wordstation] Loading 7-letter words...");
        
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader("./words/7_harfli_kelimeler.txt")))
        {
            String line;
            while((line = bufferedReader.readLine()) != null)
            {
                if(!line.isEmpty()) words_7_letter.add(line);
            }
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
        
        System.out.println("[Wordstation] Loaded all words.");
    }
    
    public static List<Character> CompareWords(String mainWord, String guess)
    {
        List<Character> comparison = new ArrayList<>();
        
        for(int i = 0; i < mainWord.length(); i++)
        {
            char cMain = mainWord.charAt(i), cGuess = guess.charAt(i);
            
            if(cGuess == cMain) comparison.add('C');
            else if(Contains(mainWord, cGuess)) comparison.add('M');
            else comparison.add('-');
        }
        
        return comparison;
    }
    
    public static boolean Contains(String word, char c)
    {
        for(char wc : word.toCharArray())
        {
            if(wc == c) return true;
        }
        
        return false;
    }
    
    public static boolean Exists(String word)
    {
        for(String w : words_4_letter)
        {
            if(w.equalsIgnoreCase(word)) return true;
        }
        
        for(String w : words_5_letter)
        {
            if(w.equalsIgnoreCase(word)) return true;
        }
        
        for(String w : words_6_letter)
        {
            if(w.equalsIgnoreCase(word)) return true;
        }
        
        for(String w : words_7_letter)
        {
            if(w.equalsIgnoreCase(word)) return true;
        }
        
        return false;
    }
    
    public static boolean Exists(String word, int wordLength)
    {
        if(wordLength == 4)
        {
            for(String w : words_4_letter)
            {
                if(w.equalsIgnoreCase(word)) return true;
            }
            return false;
        }
        else if(wordLength == 5)
        {
            for(String w : words_5_letter)
            {
                if(w.equalsIgnoreCase(word)) return true;
            }
            return false;
        }
        else if(wordLength == 6)
        {
            for(String w : words_6_letter)
            {
                if(w.equalsIgnoreCase(word)) return true;
            }
            return false;
        }
        else if(wordLength == 7)
        {
            for(String w : words_7_letter)
            {
                if(w.equalsIgnoreCase(word)) return true;
            }
            return false;
        }
        else return false;
    }
}
