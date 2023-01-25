# oumou_tpe_ousseynou

##Service de base de données MySQL

Dans le répertoire principal du projet, nous créons notre fichier nommé docker-compose.yml et le configurer comme ci-dessus. 

Ici, nous définissons d'abord les paramètres d'image, d'environnement, de port et de volume pour la base de données MySQL.

Une nouvelle définition de réseau est faite sous le paramètre de réseau qui sera utilisé dans l'application article-app. 

Les conteneurs opérant sur le même réseau peuvent interagir via des noms de conteneurs. 
Par exemple, dans l'environnement sous le article-app comme "MYSQL_HOST=article-db"


##Service web (article-app)

Après la configuration de la base de données, nous spécifions l'emplacement du Dockerfile correspondant sous le build paramètre du service article-app. 
Ensuite, nous effectuons les configurations (container_name, service, network, environment).
De plus, les dépendances entre les services sont spécifiées avec le paramètre « depend_on ».

##Dans application.properties 

les éléments liés à la base de données sont inclus dans le paramètre.
 Ainsi, lorsque l'application sera exécutée avec le conteneur sur le fichier docker-compose, elle pourra être exécutée avec des paramètres différents.
 En même temps, lorsqu'il s'exécutera sur la machine locale, il a été conçu pour fonctionner avec des paramètres par défaut.

##Pour builder les images

docker-composer up -d
