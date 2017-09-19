# Reconciliation
Transactions reconciliation application (Spring Boot + Angular).

## Requirements:
 - Java 1.8;
 - npm.


## Run
#### Backend
```
/reconciliation/gradlew :backend:bootRun
```

Starts at http://localhost:8080/

#### Frontend
```
/reconciliation/frontend/ npm start
```

Expects a running backend.

Starts at http://localhost:4200/


## Build
#### Backend
```
/reconciliation/gradlew :backend:build
```

#### Frontend
```
/reconciliation/frontend npm install
/reconciliation/frontend npm run-script build
```


## Unit Tests
```
/reconciliation/gradlew :backend:test
```


## Integration Tests
```
/reconciliation/gradlew :backend:integrationTest
```

Server starts automatically.


## End-to-End tests
```
/reconciliation/frontend/ npm run-script e2e
```

Expects a running backend.
