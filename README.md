# CSYE7200-Final-Project _Sleep, and Health
**For the Group Project of CSYE 7200** 

**Team 1** <br/>
**Authur 1:** Chen Yang (002837912)<br />
**Authur 2:** Zihao Lu (002642258)<br />
**Authur 3:** Pei-Han Hsu (002244953)<br />

**Final presentation slide link:** https://docs.google.com/presentation/d/1S1Hdj6DFZsHaTad7NZDdT-6RjfCLsFXDaBAj_HdfdB0/edit?usp=sharing  <br />
**Planning slide link:** https://docs.google.com/presentation/d/1ybOM_H7R_ITxYjDuL4E1OvvS_Rtsov5dIWZ8Xa3GhCg/edit?usp=sharing 
____

# Installation Requirement
**Java Version**: 11.0.7<br />
**sbt**: 1.9.8 (should be added into your pc environment)<br />
**Hadoop**: 3.3.0 (pc environment)<br />
**Spark**: 3.5.0(pc environment)<br />

**Note:**

The Java version is required with higher than **11.0.7, 13.0.3, 14.0.2** **(you can use the build in version in the intellij)**<br />

The purpose to restrain the the Jdk version is because is would connect with MongoDB cloud for the remote database.<br />

Hadoop is for saving and exporting files, in our project, we need to export the feature engineering files as csv to conduct the futher action <br />

sbt is required since we need to run the interface with play framework and have localhost:9000

# Introduction
This project is to investigate the relationship between sleep condition with health.<br />

Project would extract data from cloud and through data featureing, combine two dataset into one after cleaning the data. <br />

Then the cleaned dataset would be added into the machine learning (gbt and regression), then once we have model<br />

We use it to calculate the Accuracy and F1-measure to verify if this is the acceptance cretia. <br />

Once we analyzing the data and machine learning. <br />

We export the model and let it have interaction with user interface in play framework in localhost:9000, with the input data that user has, model would tell his/her BMI and Blood Pressure.

# Instruction
In terms of the how to run the program, we open the terminal and type **sbt run**, once the compilation is done, it would tell you its in localhost:9000 <br />

Open the browser and go to localhost:9000, click the button, user could type in their own data and click the button to continue, it might take while, since it would analyze the data you typed in. <br />

After time waiting, the interface would tell you how is your health. When you go back to the terminal, we could find the accuracy and F-1 measure. <br />




