# KeyWordExtraction

## Introduction
Automatic keyword extraction is an important research topic. Keywords serve as a dense summary for a document, lead to improved information retrieval, or be the entrance to a document collection. The aim of keyword assignment is to find a small set of terms that describes a specific document, independently of the domain it belongs to. In this experiments, we will use two term selection approaches: n-grams and terms matching any of a set of part-of-speech (POS) tag sequences. We will use three different features: term frequency, collection frequency and relative position of the first occurrence.

## Procedures
- We first read and extract all the Unigrams(a term that has only one word), Bigrams(a term that has two words), and Trigrams(a term that has three words) from a file.
- Then we remove all the Unigrams and Bigrams that contain any of the stopwords, and also remove all the Trigrams whose first or third word is a stopwords.(Stopwords are those words that are unlikely to become keyword but are commonly appeared, like "it" and "or").
- Apply POS Tag Pattern(Identify the part of speech), and the terms that fit into the following patterns are more likely to become keywords:

NOUN NOUN

ADJECTIVE NOUN

NOUN

- Find Term Frequency, Inverse Document Frequency, and Relative position of the first-occurrence for each of the candidates for keyword (Explained more in calculation).



## Calculation
For each candidates for keywords:

- score(t) = term frequency * Inverse Document Frequency * Relative position of the first-occurrence

- If t follows the POS-tag patterns: score(t) *= 1.66 

Term Frequency: how many times a word/phrase appear in the document

Inverse Document Frequency: in how many documents does the word/phrase appear

Relative position of the first-occurrence: index of the first character of the term/total characters

## Ranking and Precision:
- For each of the file in the folder(abstr), we extract top five candidates as the keywords generated by our algorithm.

- Compare the keywords generated by our algorithm with the manually chosen keywords (in uncontr folder).

- We get the average precision score for all the files in a folder.



## Report and Conclusion
When we ran the code for uni-, bi-, and tri-gram seperately, we get the following precision score:

uni-gram: 0.011199999999999995 (1.12%)

 bi-gram: 0.13220000000000065  (13.22%)
 
tri-gram: 0.06860000000000034  (6.86%)

We found that our calculation is more accurate when regarding the bi-gram. Our hypothesis is that "human selected words" contain more bi-grams than the other two. 

## Note:

- You can download POSTagger package from https://nlp.stanford.edu/software/tagger.shtml#Download

- You will find the list of tags and their in this link: https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html

- If you try to run the code, you need to import the POSTagger package, download the three folders of files, and change the filepath to where you store these three folders in the main of Calculation.java


## Reference
Grinnell CSC-207 Project from Famida Hamid 2020 Spring.

