Your organization has started a new tech blog with interesting tech stories and you’re responsible for

designing and implementing an in-memory search engine, supporting the search functionality on the blog content.

It should be possible to create a category in the search engine.

It should be possible to insert and delete documents in a given category.

It should be possible to search through documents for a search pattern in a given category.

It should be possible to sort the search results : Sorting based upon the lastUpdatedDate and createdAt
-----'

Approach: Gathered the requirements like:
`* no case sensitive

a document can belong to multiple categories
should the sorting be stable?
Let's say we search for python, and in the document if multiple documents have the python
then sort it on the basis of the occurrences i.e. Higher frequencey one should come at the top basically desc order
document content will be less than 2000`