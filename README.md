
---
# [Quantitative-Analysis](https://github.com/ssqstone/Quantitative-Analysis.git)

---

## I. Project Structure

### 1. **assets** Directory
- **query_history**

   To save previous queries, with the formula used and the query results. 

- **stockHistories**

   To save the K-line data from 2010

- **pref.txt** and **pref-timestamp.txt**

   The current configuration and previous configurations. 

- **rules.syntax**

   The syntax file used by the formula parser. 
   
   The formula is an LALR(1) language. 

### 2. **libs** Directory
Libraries required.  

### 3. **src** Directory
The source directory of the project. 

- ssq.stock

   Basic libraries about the updating, storing and calculating of the stock data

- ssq.stock.analyser

   Provide an enumeration framework by defining a base class `ssq.stock.analyser.Analyser` with *evaluate* and *scan* methods to facilitate the procedure of evaluating the grade of the stocks given the query formula and traversing over all the stocks. 
   
   There are also a few of example classes inheriting from the base class. Among them, there are utilities for accessing various kinds of databases. 
   
   The core executor of the formula, `ssq.stock.analyser.Interpreter` is also inherited from the base class. 

- ssq.stock.gui

   Provide the essential GUI elements involved in the process of querying stocks, by extending three base classes: frame with a status bar, frame with a table and frame with a tree. 
   
   Methods for generating K-line graph is also included, but not fully supported for now. 

- ssq.stock.interpreter

   The parser of the formula syntax, and the data structure of the AST (abstract syntax tree). 
   
   The parser is implemented with the help of [runcc](http://runcc.sourceforge.net/)

- ssq.utils

   [Utilities](https://git.oschina.net/ssqston/util.git) implemented by myself

- ssq.utils.taskdistributer

   The scheduler of muti-thread task

### 4. **SelectStock.jar**

   The exported executable jar file. 

   Before running it, you should have [JDK8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) or JRE8 installed on your device. 

### 5. **sendkey.exe**

   If you are using the eastmoney client software, just open it and double click one of the items listed in the query result. Then you can see the activate window is changed to the eastmoney.exe and the corresponding stock is displayed in it. 

   This exe file is meant to carry out the steps for you. 

## II. Usage

There are two standard work flows in this software. 

### 1. The GUI based querying and backtesting system

Just make use of the GUI to select the stocks of your desire, to test the effecacy of your query formula through the old data and to debug your formula upon a specific stock on a certain day. 

<a href="https://github.com/ssqstone/Quant-Analysis/blob/master/doc/main%20frame.PNG?raw=true"> <img style="display: block; margin: auto; width: 50%" src="https://github.com/ssqstone/Quant-Analysis/blob/master/doc/main%20frame.PNG?raw=true"/> </a><p  style="text-align: center;" > <a href="https://github.com/ssqstone/Quant-Analysis/blob/master/doc/main%20frame.PNG?raw=true"> The main window </a> </p>
<br/>
<br/>
<a href="https://github.com/ssqstone/Quant-Analysis/blob/master/doc/loopback%20entrance.PNG?raw=true"> <img style="display: block; margin: auto; width: 30%" src="https://github.com/ssqstone/Quant-Analysis/blob/master/doc/loopback%20entrance.PNG?raw=true"/> </a><p  style="text-align: center;" > <a href="https://github.com/ssqstone/Quant-Analysis/blob/master/doc/loopback%20entrance.PNG?raw=true"> The entrance to backtesting </a> </p>
<br/>
<br/>
<a href="https://github.com/ssqstone/Quant-Analysis/blob/master/doc/loopback%20result.PNG?raw=true"> <img style="display: block; margin: auto; width: 50%" src="https://github.com/ssqstone/Quant-Analysis/blob/master/doc/loopback%20result.PNG?raw=true"/> </a><p  style="text-align: center;" > <a href="https://github.com/ssqstone/Quant-Analysis/blob/master/doc/loopback%20result.PNG?raw=true"> The result of backtesting </a> </p>
<br/>
<br/>
<a href="https://github.com/ssqstone/Quant-Analysis/blob/master/doc/debug%20entrance.PNG?raw=true"> <img style="display: block; margin: auto; width: 30%" src="https://github.com/ssqstone/Quant-Analysis/blob/master/doc/debug%20entrance.PNG?raw=true"/> </a><p  style="text-align: center;" > <a href="https://github.com/ssqstone/Quant-Analysis/blob/master/doc/debug%20entrance.PNG?raw=true"> The entrance to debugging </a> </p>
<br/>
<br/>
<a href="https://github.com/ssqstone/Quant-Analysis/blob/master/doc/debug%20detail.PNG?raw=true"> <img style="display: block; margin: auto; width: 50%" src="https://github.com/ssqstone/Quant-Analysis/blob/master/doc/debug%20detail.PNG?raw=true"/> </a><p  style="text-align: center;" > <a href="https://github.com/ssqstone/Quant-Analysis/blob/master/doc/debug%20detail.PNG?raw=true"> The result of debugging </a> </p>
<br/>
<br/>
<a href="https://github.com/ssqstone/Quant-Analysis/blob/master/doc/history%20list.PNG?raw=true"> <img style="display: block; margin: auto; width: 50%" src="https://github.com/ssqstone/Quant-Analysis/blob/master/doc/history%20list.PNG?raw=true"/> </a><p  style="text-align: center;" > <a href="https://github.com/ssqstone/Quant-Analysis/blob/master/doc/history%20list.PNG?raw=true"> The list of query history </a> </p>
<br/>
<br/>
<a href="https://github.com/ssqstone/Quant-Analysis/blob/master/doc/history%20element.PNG?raw=true"> <img style="display: block; margin: auto; width: 50%" src="https://github.com/ssqstone/Quant-Analysis/blob/master/doc/history%20element.PNG?raw=true"/> </a><p  style="text-align: center;" > <a href="https://github.com/ssqstone/Quant-Analysis/blob/master/doc/history%20element.PNG?raw=true"> The detail of a single query </a> </p>

### 2. The programming-oriented way
   
Just making use of the stock data provided by the spider and the evaluating and traversing facilities provided by the `ssq.stock.analyzer.Analyzer` class. 

Examples can be seen under the `ssq.stock.analyzer` package. 

## III. Custom Your **Own** Formula
### 1. General Ideas

As a user of this software, your main efforts should be paid on how to compose your formula

I have already put an example of the formula in this release. 

```Scala
/*All these comments should be removed before you put this formula into the GUI*/
max(250->125).opening/*The opening price*/..norest/*Without adjusting (Backward adjustment is the default option)*/ 
/*The above line is an invocation of a function, to denote 
the maximum of the opening price of the latest 250 days to 125 days, without adjusting the price.*/
* 2 /*Expressions consist of the four arithmetic operations is supported*/
/*Every invocation of a function yields a value, 
the normal numbers are also values, 
and their results after the four arithmetic operations are still all values.*/
< /*Less than operator*/
average(5->1).highest 
/*There isn't a "..norest". So it yields the average of the highest prices in the latest 5 days*/ 
/*All these above make an inequation. 
Every inequation is an item for a stock to be graded. 
Taking the history data of a single stock into the inequation, we will get two values on the two sides. 
The grade of the stock upon this inequation is equal to the "satisfaction level" of the inequation, 
which will define it later. */
@2 /*The weighting factor, defaulting to 1. 
It is the scale factor of the grades failed. */ 
/*All these above is a composed expression. You can build an expression tree by using the 
&&, ||, and ()*/
&& /*And*/
(
3<4 /*Nonsense. Just for demonstration of the syntax. */
|| 
sum(250->1).quantity /*The sum of the deal quantity of the latest 250 days*/ > 10000000000)
/*The priority of &&, ||, and () are just as the same as those in C*/
```

Then you can put this formula into the GUI to get the result. 
```Scala
max(250->125).opening..norest * 2 < average(5->1).highest @2 && (3<4 || sum(250->1).quantity > 10000000000)
```
<small>*Note: This formula is just meant to illustrate the syntax of the formula system, use this and all other formulae on your own risks. *</small>

### 2. *For the experts*

The "satisfaction level" of an inequation is just a quotient followed by a saturation operation. 

```Scala
2 == 3 =======> 2/3			
//just taking the small number as the dividend

3 <= 2 =======> 2/3			
//just taking the right hand side as the dividend

2 <= 3 =======> 3/2 ======> 1		
//the maximum grade is 1, also, the minimum value is 0

3<=2 && 4==5 || 1 == 3  =======> max(2/3 * 4/5, 1/3) =======> 8/15 
//the composition of the inequations are calculated by multiply and picking the max value
```

And, there the grammar [railroad graph](https://ssqstone.github.io/portfolio/Quantitative-Analysis/railroad.html) ;-)

## III. About the interfaces
### 1. Java and C

Can be simply implemented by JNI or cmd line

### 2. Java and Matlab

With the help of javabuilder

### 3. Provide data for other purposes
#### 1. Through SQL Server

By setting the password of account *sa* to *00*, or modify the code, you can put all the history data to SQL Server using `ssq.stock.analyser.SqlserverUpdater`
	
<small>*Note: The underlying JDBC should be connected to a certain database. So you should create a database named "Stock" in the SQL Server Management Studio provided by the SQL Server installation. *</small>

#### 2. By text file

All the history data can be exported using `ssq.stock.analyser.TextOutPuter`

