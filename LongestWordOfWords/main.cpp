#include <fstream>
#include <set>
#include <list>
#include <iostream>
#include <map>

//26 letters in the alphabet
#define ALPHABET 26

//Location of file
#define FILEPATH "wordsforproblem.txt"

using namespace std;

typedef list<string> List;

//Trie node struct
typedef struct trie {
    bool isQualifyingWord;
    struct trie *letterLeaf[ALPHABET];
}trie;

//Create the actual trie structure
trie* createTrie(trie *trieStructure){

    //Allocate memory to store the trie
    trieStructure = (trie*)malloc(sizeof(trie));

    trieStructure->isQualifyingWord = false;
    
    //Creates an empty node for every letter
    for(int i = 0; i < ALPHABET; i++){
        trieStructure->letterLeaf[i] = NULL;
    }
    
    return trieStructure;
}

//Add a word to the trie
trie* addToTrie(trie *trieNode, const char *word){
    
    //Catch null termination char
    if(word[0] == '\0') {
        trieNode->isQualifyingWord = true;
        
    } else {
        
        //Consume first letter
        int letterIndex = word[0] - 'a';
        trie* &leaf = trieNode->letterLeaf[letterIndex];
        
        //If null, create a new trie node to house
        if(leaf == NULL){
            leaf = createTrie(leaf);
        }
        
        //Increment to new word and call recursively
        word++;
        leaf = addToTrie(leaf, word);
    }
    
    return trieNode;
}

//Fill trie with words from list
void fillTrie(trie* &trieRoot, map<size_t, List> &totalWords, set<size_t> &orderedWords){
    
    //Create root of trie
    trieRoot = createTrie(trieRoot);
    
    //Create a reader for input from word list
    ifstream reader(FILEPATH, ifstream::in);
    istream_iterator<string> iteratorStart(reader), iteratorEnd;
    
    //Adds words to trie until it reaches the end of the list
    while(iteratorStart != iteratorEnd){
        addToTrie(trieRoot, (*iteratorStart).c_str());
        size_t length = iteratorStart->size();
        
        totalWords[length].push_back(*iteratorStart);
        orderedWords.insert(length);
        
        iteratorStart++;
    }
    
    //Shut it down
    reader.close();
}

//Determine if word meets requirments
bool isQualifyingWord(trie *trieNode, const char *word, int firstLetter, int lastLetter){
    
    //Catch null termination char
    if(word[firstLetter] == '\0' || firstLetter > lastLetter){
        return trieNode->isQualifyingWord;
        
    } else {
        
        //Consume first letter
        int letterIndex = word[firstLetter] - 'a';
        
        //Not a qualifying word
        if (trieNode->letterLeaf[letterIndex] == NULL){
            return false;
        }
        
        //Run recursively until \0 is reached
        return isQualifyingWord(trieNode->letterLeaf[letterIndex], word, firstLetter+1, lastLetter);
    }
}

//Iterates over word looking for smaller words
bool isSegmentedWord(trie *trieNode, const char *word, int firstLetter, int lastLetter, int &middleIndex){
    
    //Create a sub node to search within word
    trie *trieSubNode = trieNode;
    
    int i;
    const char *wordIndex = word + firstLetter;

    //Iterate over word
    for(i = firstLetter; i <= lastLetter; i++) {
        
        //Consume first letter
        int letterIndex = *wordIndex - 'a';
        
        //No match found, break out of function
        if (trieSubNode->letterLeaf[letterIndex] == NULL) {
            return false;
        }
        
        //Looks good, keep moving
        wordIndex++;
        trieSubNode = trieSubNode->letterLeaf[letterIndex];
        
        //Match found
        if(i >= middleIndex && trieSubNode->isQualifyingWord) {
            middleIndex = i;
            return true;
        }
    }
    
    middleIndex = lastLetter;
    return trieSubNode->isQualifyingWord;
}

//Determines if word is made of other words and returns number of words in it
int numberOfWords(trie *trieNode, const char *word, int firstLetter, int lastLetter, bool &isMatch){
    
    //Iterate over entire word
    for(int i = firstLetter; i <= lastLetter; i++) {
        
        //Check if word is made of other words
        bool isSegment = isSegmentedWord(trieNode, word, firstLetter, lastLetter, i);
        
        //Catches non-segmented words
        if(i == lastLetter){
            isMatch = isSegment;
            
            if(isSegment){
                return 1;
            } else {
                return 0;
            }
        }
        
        //If a segemented word, continue
        isMatch = false;
        int totalSegmentedWords = numberOfWords(trieNode, word, i+1, lastLetter, isMatch);
        
        //If a segemented word and a match, return number of words in word
        if (isSegment && isMatch){
            return 1 + totalSegmentedWords;
        }
    }
    
    return 0;
}

int main(int argc, const char * argv[]){
    
    //Trie root
    trie *root = NULL;
    
    //All words in list
    map<size_t, List> totalWords;
    
    //Words ordered by length
    set<size_t> orderedWords;
    
    //Iterator, starts with longest words
    set<size_t>::reverse_iterator reverseIterator;
    
    //Counters
    int constructedWords = 0;
    int wordsWithinWord = 0;
    
    //Fill trie with words from list
    fillTrie(root, totalWords, orderedWords);
    
    //Start with longest words
    for(reverseIterator = orderedWords.rbegin(); reverseIterator != orderedWords.rend(); reverseIterator++){
        List& words = totalWords[*reverseIterator];
        
        //Iterate through all words of the same length
        for(List::const_iterator iterator = words.begin(); iterator != words.end(); iterator++) {
            bool isQualifying = false;
            wordsWithinWord = numberOfWords(root, iterator->c_str(), 0, (int)iterator->size()-1, isQualifying);
            
            //Prints word if meets criteria
            if(isQualifying && wordsWithinWord > 1) {
                
                //Longest if no constructed words have been found yet
                if(constructedWords == 0){
                    cout << "Longest: " << *iterator << endl;
                } else {
                    //Second longest if one constructed word has already been found
                    if(constructedWords == 1){
                        cout << "Second: " << *iterator << endl;
                    }
                }
                
                //Increment number of constructed words
                constructedWords++;
            }
        }
    }
    
    //Output nunber of constructed words
    cout << "Words constructed of other words: " << constructedWords << endl;
    
    //Shut it down
    delete root;
    return 0;
}
