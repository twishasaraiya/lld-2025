package org.example.SearchEngine;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        SearchEngine searchEngine = new SearchEngine();
        Category sports = searchEngine.createCategory("Sports");
        Category news = searchEngine.createCategory("News");

        String doc1 = searchEngine.insertDocument("doc1", "It should be possible to create a category in the search engine", List.of(sports, news));
        String doc2 = searchEngine.insertDocument("doc2", "It should be possible to search through documents for a search pattern in a given category.", List.of(news));
        String doc3 = searchEngine.insertDocument("doc3", "It should be possible to insert and delete documents in a given category.", List.of(sports));

        SortOrder sortOrder = new SortOrder(List.of(SortBy.COUNT_FREQUENCY));
        SearchResult searchResult = searchEngine.search("documents", sports,sortOrder);
        print(searchResult, "documents");
    }

    private static void print(SearchResult searchResult, String pattern){
        System.out.println("Search result for " + pattern + " : ");
        for (Document document: searchResult.documents){
            System.out.println("Pattern found in " + document.title);
        }
    }
}

/**
 * SearchEngine
 *  - Create Category
 *  - Insert/Delete document in a category
 *  - Search(pattern, category, SortOrder)
 *
 *  Document
 *  - title
 *  - text
 *  - List<Category>
 *  - createdAt
 *  - updatedAt
 *
 *  Category
 *  - String name
 *
 *  InvertedIndex
 *  - word<>documentId
 *  - addDocument
 *  - removeDocument
 */

class Document{
    String id;
    String title;
    String text;
    List<Category> categories;
    long createdAt;
    long updatedAt;

    public Document(String title, String text, List<Category> categories) {
        this.title = title;
        this.text = text;
        this.categories = categories;
        this.id = UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public long patternFrequency(String searchPattern){
        Pattern pattern = Pattern.compile(searchPattern.toLowerCase().trim());
        String content = this.title.toLowerCase().trim() + " " + this.text.toLowerCase().trim();
        return pattern.matcher(content).results().count();

    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(title, document.title) && Objects.equals(text, document.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, text);
    }
}
class Category{
    String name;

    public Category(String name) {
        this.name = name;
    }
}
class SearchResult{
    List<Document> documents;
    SortOrder sortOrder;

    public SearchResult(List<Document> documents, SortOrder sortOrder) {
        this.documents = documents;
        this.sortOrder = sortOrder;
    }
}

enum SortBy{
    COUNT_FREQUENCY,
    CREATED_AT,
    UPDATED_AT
}
class SortOrder{
    List<SortBy> sortByList;

    public SortOrder(List<SortBy> sortByList) {
        this.sortByList = sortByList;
    }
}

class InvertedIndex {
    Map<String, List<String>> wordToDocumentIds;
    Map<String, Document> documentMap;
    private static final String SEPARATOR = " ";
    private static final List<String> stopWords = List.of(",", " ", ".");

    public InvertedIndex() {
        this.wordToDocumentIds = new ConcurrentHashMap<>();
        this.documentMap = new ConcurrentHashMap<>();
    }

    public void add(Document document){
        String content = document.title.toLowerCase() + SEPARATOR + document.text.toLowerCase();
        String[] words = content.split(SEPARATOR);
        documentMap.put(document.id,document);
        for(String word: words){
            if(!word.isEmpty() && !stopWords.contains(word)) {
                wordToDocumentIds.computeIfAbsent(word, k -> new ArrayList<>())
                        .add(document.id);
            }
        }
    }

    public void remove(Document document){
        String content = document.title.toLowerCase() + SEPARATOR + document.text.toLowerCase();
        String[] words = content.split(SEPARATOR);
        documentMap.remove(document.id);
        for(String word: words) {
            if(!word.isEmpty() && !stopWords.contains(word)){
                wordToDocumentIds.get(word)
                        .remove(document.id);
            }

        }
    }

    public SearchResult searchResults(String pattern, SortOrder sortOrder){
        List<String> documentIds = wordToDocumentIds.getOrDefault(pattern.toLowerCase(), new ArrayList<>());
        List<Document> documents = documentIds.stream()
                .map(id -> documentMap.get(id))
                .filter(document ->  document.text.contains(pattern))
                .sorted((d1, d2) -> compare(d1,d2, pattern, sortOrder))
                .collect(Collectors.toList());

        return new SearchResult(documents, sortOrder);

    }

    private int compare(Document a, Document b, String pattern, SortOrder sortOrder){
        for (SortBy sortBy: sortOrder.sortByList){
            switch (sortBy){
                case CREATED_AT: {
                    if(b.createdAt == a.createdAt) continue;
                    return Long.compare(b.createdAt,a.createdAt);
                }
                case UPDATED_AT:
                    if (a.updatedAt == b.updatedAt) continue;
                    return Long.compare(b.createdAt, a.createdAt);
                case COUNT_FREQUENCY:
                    long freqA = a.patternFrequency(pattern);
                    long freqB = b.patternFrequency(pattern);
                    if(freqA == freqB) continue;
                    return Long.compare(freqB, freqA);
            }
        }
        return a.title.compareTo(b.title);
    }
    public Document getDocument(String id) {
        // throw error if not present
        return documentMap.get(id);
    }
}
class SearchEngine{
    Map<String, Category> categoryMap;
    Map<Integer, Document> documentIndex;
    Map<Category, InvertedIndex> categoryInvertedIndexMap;
    InvertedIndex globalInvertedIndex;

    public SearchEngine() {
        this.categoryMap = new ConcurrentHashMap<>();
        this.documentIndex = new ConcurrentHashMap<>();
        this.categoryInvertedIndexMap = new ConcurrentHashMap<>();
        this.globalInvertedIndex = new InvertedIndex();
    }

    public Category createCategory(String categoryName){
        if(categoryMap.containsKey(categoryName)){
            throw new RuntimeException("Category already exits");
        }
        Category category = new Category(categoryName);
        categoryMap.put(categoryName, category);
        categoryInvertedIndexMap.put(category, new InvertedIndex());
        return category;
    }

    public String insertDocument(String title, String text, List<Category> categories){
        // Duplication content check
        int hash = Objects.hash(title, text);
        if(documentIndex.containsKey(hash)){
            throw new RuntimeException("Duplicate content found" + documentIndex.get(hash).id + " already exits");
        }
        Document document = new Document(title, text, categories);
        documentIndex.put(hash, document);
        globalInvertedIndex.add(document);
        for(Category category: categories){
            categoryInvertedIndexMap.get(category).add(document);
        }
        return document.id;
    }

    public void deleteDocument(String id){
        Document document = globalInvertedIndex.getDocument(id);
        int hash = document.hashCode();
        documentIndex.remove(hash);
        globalInvertedIndex.remove(document);
        for (Category category: document.categories){
            categoryInvertedIndexMap.get(category).remove(document);
        }
    }

    public SearchResult search(String pattern, Category category, SortOrder sortOrder){
        if(category == null){
            return globalInvertedIndex.searchResults(pattern, sortOrder);
        }
        return categoryInvertedIndexMap.get(category).searchResults(pattern, sortOrder);
    }

}

