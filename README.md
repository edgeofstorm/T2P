Text2Pictogram for Turkish
==========================
Text2Pictogram aims to **help** people with intellectual or developmental disabilities (IDD) communicate the world with ease.

## Usage

### Open project with IntelliJ IDEA

Steps for opening the cloned project:

* Start IDE
* Select **File | Open** from main menu
* Choose `T2P/pom.xml` file
* Select open as project option
* Couple of seconds, dependencies with Maven will be downloaded. 

### xercesimpl.jar

* you need to import xercesimpl.jar manually(they do not publish official maven release, this causes conflictions) [check out more info here ->](https://stackoverflow.com/questions/11677572/dealing-with-xerces-hell-in-java-maven)

### WHEN UPDATING
*Always pull before you push in case someone has done any work since the last time you pulled - you wouldn't want anyone's work to get lost or to have to resolve many coding conflicts.

###
tested some random picked pictos.zemberek causes a problem in both inserting and selecting

##PARTS THAT NEED FIXING

//FIXME
* NER test dosyasini gelistir.
* zemberek sacma sacma kokler aliyo reyizin morfolojisine bak(maybe ITS BECAUSE .ignoreDiacriticsInAnalysis())
* SyntaxAnalysis
* deletePossession updatele
* linkersynsetpictogram (yuklerken orjinal kelimeyi al possession yoksa).

***Please see issues section for further issues and feel free to create new ones.***

## SynoymFilter

Replaces a word in a .txt file with the highest usage frequency of all its synoyms.(Not functioning correctly rn)

## NamedEntityRecognition

train,test and model files can be found in src/main/resources
https://github.com/ahmetaa/zemberek-nlp and http://haydut.isikun.edu.tr/nlptoolkit.html eyvallah reyizler

## LAST BUT NOT LEAST
***Licensed under Onur®,Hakaci© and haQQi™***