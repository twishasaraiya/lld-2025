Given an org chart, write an algorithm to determine the lowest common parent of a list of children. Expected to build the graph on my local machine and run the code to show it worked.


Find the closest Org for target Employees  
a) Imagine you are the team that maintains the Atlassian employee directory. At Atlassian – there are multiple groups, and each can have one or more groups. Every employee is part of a group. You are tasked with designing a system that could find the closest common parent group giv a target set of employees in the organization.  
b) The Atlassian hierarchy sometimes can have shared group across an org or employees shared across different groups – How will the code evolve n this case if the requirement is to provide ONE closest common group  
c) The system now introduced 4 methods to update the structure of the hierarchy in the org. Supose these dynamic updates are done in separate threads while getCommonGroupForEmployees is being called, How ill your system handled reads and writes into the system efficiently such that at any given time getCommonGroupForEmployees always reflects the latest updated state of the hierarchy?  
d) The company consists of a single level of groups with no subgroups. Each group has a set of employees.
