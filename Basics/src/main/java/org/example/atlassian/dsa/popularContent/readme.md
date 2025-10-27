Popular content  
Imagine you are given a stream of content ids along with an associated action to be performed on them  
Example of contents are video, pages, posts etc. There cam be two actions associated with a content id:  
• increasePopularity -> increases the popularity of the content by 1. The popularity increases when someone comments on the content or likes the comtent  
• decreasePopularity-> decreases the popularity of the content by 1. The popularity decreases when a spam bot’s/users comments are deleted from the content or its likes are removed from the content  
• content ids are positive integers  
Implement a class that can return the mostPopular content id at any time while consuming the stream of content ids and its associated action. If there are no contentIds with popularity greater than 0, return -1