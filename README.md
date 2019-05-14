Text2Pictogram for Turkish
==========================
Text2Pictogram aims to **help** people with intellectual or developmental disabilities (IDD) communicate the world with ease.

## Usage

### Open IntelliJ IDEA

Steps for starting the cloned project:

* You should need to download resources with Git Bash. Command-line code below;
  * git clone https://github.com/ahmetaa/zemberek-nlp
  * git clone https://github.com/olcaytaner/MorphologicalAnalysis
  *	git clone https://github.com/olcaytaner/WordNet
  *	git clone https://github.com/edgeofstorm/T2P 
* You should use Maven
  * Add pom.xml all downloaded resources to Maven
  * Then Build project, Maven "compile", "package"
  * If there is any failure. Use 11 version of Java SE
* You must use link WordNet to pictograms
  * Check linker class on main code
* You must need train model for NER
* You must need train model for Syntax


### xercesimpl.jar

* you need to import xercesimpl.jar manually(they do not publish official maven release, this causes conflictions) [check out more info here ->](https://stackoverflow.com/questions/11677572/dealing-with-xerces-hell-in-java-maven)

### WHEN UPDATING
*Always pull before you push in case someone has done any work since the last time you pulled - you wouldn't want anyone's work to get lost or to have to resolve many coding conflicts.

###
tested some random picked pictos.zemberek causes a problem in both inserting and selecting

## PARTS THAT NEED FIXING

***Please see issues section for further issues and feel free to create new ones.***


## LAST BUT NOT LEAST
***Licensed under Onur®,Hakaci© and haQQi™***
