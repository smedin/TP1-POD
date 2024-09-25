# TPE1-POD
## Emergency Room

This project is a Distributed and thread-safe remote system for managing emergencies in a hospital's emergency room. The system is designed to facilitate the notification of medical staff and provide reports on attended emergencies.

---

## Project compilation
To compile the project, run the following command on the root folder
```bash
chmod +x compile.sh
./compile.sh
```
## Execution instructions
### Server
To run the server, execute:
```bash
cd ./executables/tpe1-g4-server-2024.2Q
./server.sh
```

### Administration client
The administrator client has 3 different methods:

Add room:
```bash
cd ./executables/tpe1-g4-client-2024.2Q
./administrationClient.sh -DserverAddress=X.X.X.X:YYYYY -Daction=addRoom
````

Add doctor:
```bash
cd ./executables/tpe1-g4-client-2024.2Q
./administrationClient.sh -DserverAddress=X.X.X.X:YYYYY -Daction=addDoctor -Ddoctor=John -Dlevel=3
````

Add doctor:
```bash
./executables/administrationClient.sh -DserverAddress=X.X.X.X:YYYYY -Daction=setDoctor -Ddoctor=John -Davailability=available
````

### Waiting room client
The waiting room client has 3 different methods:

Register patient:
```bash
cd ./executables/tpe1-g4-client-2024.2Q
./waitingRoomClient.sh -DserverAddress=X.X.X.X:YYYYY -Daction=addPatient -Dpatient=Foo -Dlevel=3
```

Change patient emergency level:
```bash
cd ./executables/tpe1-g4-client-2024.2Q
./waitingRoomClient.sh -DserverAddress=X.X.X.X:YYYYY -Daction=updateLevel -Dpatient=Foo -Dlevel=4
```

Get patient waiting time:
```bash
cd ./executables/tpe1-g4-client-2024.2Q
./waitingRoomClient.sh -DserverAddress=X.X.X.X:YYYYY -Daction=checkPatient -Dpatient=Foo
```

### Emergency attention client
The emergency attention client has 3 different methods:

Start attention on a specific room:
```bash
cd ./executables/tpe1-g4-client-2024.2Q
./emergencyCareClient.sh -DserverAddress=X.X.X.X:YYYYY -Daction=carePatient -Droom=2
```

Start attention on all free rooms:
```bash
cd ./executables/tpe1-g4-client-2024.2Q
./emergencyCareClient.sh -DserverAddress=X.X.X.X:YYYYY -Daction=careAllPatients
```

End attention on a specific room:
```bash
cd ./tpe1-g4-client-2024.2Q
./executables/emergencyCareClient.sh -DserverAddress=X.X.X.X:YYYYY -Daction=dischargePatient -Droom=2 -Ddoctor=John -Dpatient=Foo
```

### Notification client
The notification client has 2 different methods:

Register doctor for notifications:
```bash
cd ./executables/tpe1-g4-client-2024.2Q
./doctorPagerClient.sh -DserverAddress=X.X.X.X:YYYYY -Daction=register -Ddoctor=John
```

Unregister doctor for notifications:
```bash
cd ./executables/tpe1-g4-client-2024.2Q
./doctorPagerClient.sh -DserverAddress=X.X.X.X:YYYYY -Daction=unregister -Ddoctor=John
```
### Query client
The query client has 3 different methods:

Current state of all rooms
```bash
cd ./executables/tpe1-g4-client-2024.2Q
./queryClient.sh -DserverAddress=X.X.X.X:YYYYY -Daction=queryRooms -DoutPath=../query1.csv
```

Patients waiting to get attended
```bash
cd ./executables/tpe1-g4-client-2024.2Q
./queryClient.sh -DserverAddress=X.X.X.X:YYYYY -Daction=queryWaitingRoom -DoutPath=query2.csv
```

Finalized emergencies
```bash
cd ./executables/tpe1-g4-client-2024.2Q
./queryClient.sh -Daction=queryCares -DoutPath=/tmp/query3.csv -Droom=2
```











