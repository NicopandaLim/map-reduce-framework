CSC536 Final Project
Xiao Lin

-------------------------------------------------------------------------------------------------------

How to run the project:

We have 3 clients and 2 servers: 
 
•	Open 3 terminals, 1 for serverLeft[4], 1 for serverRight[5], 1 for Client [1] or [2] or [3]
•	Go to the folder mapReduceFramework
•	> sbt
•	> run
•	Run 2 servers first
•	In the third terminal, you can run [2] client.WordCount1
                                           [3] client.reverseIndex2
                                           [1] client.LinkCount3


All the executable files and other test files needed are included in the mapReduceFramework folder.


The overall system design:
•	This is a simple distributed Map-Reduce Framework that can handle multiple problems. 
•	There are three nodes, one is local, the two are remote. For the two remote nodes, each node takes care of two mappers and two reducers. The clients-server communication is adopted. 
•	All clients and server share the files of the common folder. 
•	The client sends the data as messages to servers, the map and reduce functions are wrapped in an object, which are passed in the props configuration as actor constructor arguments when actors are creating or initialized. 
•	The router in the master actor forwards the data and function object to mappers. After the data is processed by mappers, the processed data pairs are sent to reducers. After the pairs are processed in reducers, the results are printed out.


The benefits of various design decisions you have made as well as the tradeoffs
Benefit 1: This framework can handle multiple problems instead of just one.
Benefit 2: This system uses multiple nodes, which can realized a distributed system and take advantage of more CPU power.
