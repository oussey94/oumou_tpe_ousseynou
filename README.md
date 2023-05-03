### mbodjiousseynou94@gmail.com 

## Service de base de données MySQL (article-db)

Dans le répertoire principal du projet, nous créons notre fichier nommé docker-compose.yml et le configurer comme ci-dessus. 

Ici, nous définissons d'abord les paramètres d'image, d'environnement, de port et de volume pour la base de données MySQL.

Une nouvelle définition de réseau est faite sous le paramètre de réseau qui sera utilisé dans l'application article-app. 

Les conteneurs opérant sur le même réseau peuvent interagir via des noms de conteneurs. 
Par exemple, dans l'environnement sous le article-app comme "MYSQL_HOST=article-db"


## Service web (article-app)

Après la configuration de la base de données, nous spécifions l'emplacement du Dockerfile correspondant sous le build paramètre du service article-app. 
Ensuite, nous effectuons les configurations (container_name, service, network, environment).
De plus, les dépendances entre les services sont spécifiées avec le paramètre « depend_on ».

## Dans application.properties 

les éléments liés à la base de données sont inclus dans le paramètre.
 Ainsi, lorsque l'application sera exécutée avec le conteneur sur le fichier docker-compose, elle pourra être exécutée avec des paramètres différents.
 En même temps, lorsqu'il s'exécutera sur la machine locale, il a été conçu pour fonctionner avec des paramètres par défaut.

## Pour builder les images

docker-composer up -d

## ===========================================================================

## Exemple simple d'utilisation Kubernetes avec MYSQL et Spring Boot

Maintenant que nous avons parcouru les bases de Kubernetes dans notre article, ici nous allons détailler un exemple simple de son utilisation. Dans cet exemple, nous allons déployer un api Web à l'aide de Kubernetes. Nous utiliserons Docker comme runtime de conteneur. L'application dans notre exemple comporte deux parties distinctes :

* Base de données (serveur MySQL)
* Back-end (application Java Spring Boot)

Nous allons déployer tous ces composants sur un cluster Kubernetes. Nous aurons une réplique de la base de données, un réplique du back-end. L'instance back-end communiquera avec la base de données. Pour faciliter cette communication, nous devons configurer Kubernetes en conséquence.

### Nous allons configurer le cluster en créant des objets Kubernetes 

Ces objets Kubernetes contiendront l'état souhaité de notre déploiement. Une fois ces objets conservés dans le magasin d'état du cluster (ETCD), l'architecture interne de Kubernetes prendra les mesures nécessaires pour garantir que l'état dans l'ETCD est le même que l'état physique du cluster.

#### Nous utiliserons kubectl pour créer les objets

Kubernetes prend en charge les méthodes impératives et déclaratives de création d'objets. Les environnements de production sont généralement configurés par l'approche déclarative. Nous utiliserons l'approche déclarative dans cet exemple. Pour chaque objet, nous allons d'abord préparer un fichier manifeste qui est un fichier .yaml contenant toutes les informations liées à l'objet. Ensuite, nous exécuterons la commande kubectl `kubectl apply -f <FILE_NAME>` pour conserver l'objet dans le magasin d'état du cluster (ETCD).

* Mais avant cela nous allons d'abord conteneuriser le code de l'application que nous avons implémenté avec le Dockerfile:

``` yaml
    # Première étape : Compiler le projet Spring Boot
    FROM maven:3.6.3-openjdk-11 as build
    COPY src /app/src
    COPY pom.xml /app
    RUN mvn -f /app/pom.xml clean package -DskipTests
    # Deuxième étape : Créer une image en copiant seulement l'artefact JAR généré dans la première étape
    FROM openjdk:11-jre
    COPY --from=build /app/target/*.jar /app.jar
    EXPOSE 8080
    ENTRYPOINT ["java", "-jar", "/app.jar"]
``` 

` docker build -t mbodji/api-spring-test:v2.0 . `
` docker push mbodji/api-spring-test:v2.0 `

* Ensuite configurer la base de données:

Les instances principales doivent communiquer avec la base de données. Tous les détails de configuration requis pour se connecter à la base de données sont stockés dans un fichier de configuration: `application.properties `.
Ce fichier de configuration attend certaines variables d'environnement, comme :
  * MYSQL_USERNAME, 
  * MYSQL_PASSWORD, 
  * MYSQL_HOST, 
  * MYSQL_DATABASE. 

L'image du docker de la base de données MySQL aussi attend certaines variables d'environnement comme:
  * MYSQL_ROOT_PASSWORD, 
  * MYSQL_USER, 
  * MYSQL_PASSWORD, 
  * MYSQL_DATABASE.

Nous transmettrons les valeurs de ces variables à Kubernetes via `app-configmap.yaml` et `app-secret.yaml`.

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: api-configmap
data:
  mysql_host: mysql
  mysql_database: testDbForArticle
  #mysql_port: 3306
```
```yaml
apiVersion: v1
kind: Secret
metadata:
 name: api-secret
type: Opaque
data:
 #dbname: YXBwLWRi
 mysql_user: dXNlcg==
 mysql_password: cGFzc3dvcmQ=
 mysql_root_password: cm9vdA==
```

Nos pods seront configurés pour lire les variables d'environnements à partir du configMaps et du secrets.

* Enfin nous crérons nos services, statefullset et déploiement:
  * pour la base de données
```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: mysql-sts
spec:
  replicas: 1
  serviceName: mysql
  selector:
    matchLabels:
      app: mysql
  template:
    metadata:
      labels:
        app: mysql
    spec:
      terminationGracePeriodSeconds: 10
      containers:
        - name: mysql
          image: mysql:8.0
          ports:
            - name: tpc
              protocol: TCP
              containerPort: 3306
          env:
            - name: MYSQL_ROOT_PASSWORD
              valueFrom: 
               secretKeyRef: 
                key: mysql_root_password
                name: api-secret
            - name: MYSQL_PASSWORD
              valueFrom: 
               secretKeyRef: 
                key: mysql_password
                name: api-secret
            - name: MYSQL_USER
              valueFrom: 
               secretKeyRef: 
                key: mysql_user
                name: api-secret
            - name: MYSQL_DATABASE
              valueFrom: 
               configMapKeyRef: 
                key: mysql_database
                name: api-configmap
          volumeMounts:
            - name: data
              mountPath: /var/lib/mysql
  volumeClaimTemplates:
    - metadata:
        name: data
      spec:
        storageClassName: standard
        accessModes:
          - ReadWriteOnce
        resources:
          requests:
            storage: 1Gi
```
```yaml
# Define a 'Service' To Expose mysql to Other Services
apiVersion: v1
kind: Service
metadata:
  name: mysql  # DNS name 
  labels:
    app: mysql
spec:
  ports:
    - port: 3306
      targetPort: 3306
  selector:       # mysql Pod Should contain same labels
    app: mysql
  clusterIP: None  # We Use DNS, Thus ClusterIP is not relevant 
```
  * pour l'api
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: deploy-test-api
  labels:
    app: springboot
spec:
  replicas: 1 # Number of replicas of back-end application to be deployed
  selector:
    matchLabels: # backend application pod labels should match these
      app: springboot
  template:
    metadata:
      labels: # Must macth 'Service' and 'Deployment' labels
        app: springboot
    spec:
      containers:
        - name: api-spring
          image: mbodji/api-spring-test:v2.0 # docker image of backend application
          env: # Setting Enviornmental Variables
            - name: MYSQL_HOST # Setting Database host address from configMap
              valueFrom:
                configMapKeyRef:
                  name: api-configmap # name of configMap
                  key: mysql_host
            - name: MYSQL_DATABASE # Setting Database name from configMap
              valueFrom:
                configMapKeyRef:
                  name: api-configmap
                  key: mysql_database
            - name: MYSQL_USERNAME # Setting Database username from Secret
              valueFrom:
                secretKeyRef:
                  name: api-secret # Secret Name
                  key: mysql_user
            - name: MYSQL_PASSWORD # Setting Database password from Secret
              valueFrom:
                secretKeyRef:
                  name: api-secret
                  key: mysql_password
          ports:
            - containerPort: 8080
```

