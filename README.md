COVID-19 Tracker
===

## Build & Deployment

**Create JAR**
  
```
mvn clean package
```

**Build Docker image**

```
docker build -t eu.gcr.io/astina-474925/covi-tracker:[VERSION] --no-cache . 
```

**Push Docker image**

```
docker push eu.gcr.io/astina-474925/covi-tracker:[VERSION]
```

**Update deployment config**

Set image version in https://github.com/astina/covi-config/blob/master/deployment.yaml#L20
and push.

## Database

**Connect to database**

```
kubectl port-forward -n sql-proxy svc/access-db-prod 5432
```

## Secrets

*Note*: make sure `--namespace` is correct.

```
kubectl create secret --namespace=covid-export \
    --from-literal=foo=bar \
    --from-literal=baz=kux \
    generic my-secret
```
