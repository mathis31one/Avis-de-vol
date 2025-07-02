# Lancer l'application

Bien qu'il soit possible de lancer l'application dans son environnement personnel, il est recommandé comme vu plus haut d'utiliser Docker.

### lancer toute l'application :

```
docker-compose up --build
```

### Voir les logs :

```
Pour tout :
docker-compose logs -f

Par module :
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres
```

### Stop :

```
docker-compose down
```

### Stop + Détruire volumes :

```
docker-compose down -v
```

### Points d'accès :

* **Frontend (Angular):** [http://localhost:4200](http://localhost:4200)
* **Backend API:** http://localhost:8080/api
* **PostgreSQL:** localhost:5432 (username: username, password: password)

Comptes initialement créés :

Admin : admin@avisdevol.com/admin123

utilisateur lambda : user@avisdevol.com

## Environement de recette :

un server de recette, idéal en cas de difficultés à lancer est disponible sous : http://195.35.1.76:4200/login

Il est lui même le docker décrit ici, exposé depuis un server VPS.

# Architecture du Projet:

![1751486586678](image/readme/1751486586678.png)

L'architecture de ce projet a été pensée en amont, avec une structure modulaire permettant une implémentation progressive de ses différentes parties. Dès le départ, des aspects cruciaux comme la **gestion des droits** et l'**authentification** ont été intégrés, afin d'éviter une restructuration majeure du code ultérieurement.

On retrouve donc un Compte (Account) qui a un rôle. Il peut écrire des avis et des réponses (one to many). Chaque review peut contenir plusieurs réponses (many to one). La review se fait sur un vol (Many to One) et contient un status unique.

Les énumérations ne sont pas des objets bd (ils sont gérés par hibernate pour passer du Java à la BD)

Pour les Class, il s'agit bien d'objets BD. On utilise ici une BD relationnelle car les objets ont de fortes interdépendance et on un format fixe. On aurait toutefois pu imaginer au moins une partie en NoSQL orienté document pour stocker les avis de façon plus flexible.

---

## Technologies Clés

### Docker

**Docker** est au cœur de ce projet. Il est essentiel pour contrôler l'environnement de développement et assurer la portabilité de l'application. Cette approche permet de simplifier le développement et de faciliter le déploiement sur différents environnements, tout en minimisant les problèmes liés aux spécificités de chacun.

Sa composition est :

- PostgreSQL database (la base de données, qui est donc gérée dans docker)
- Spring Boot backend (le back-end)
- Angular frontend (le front-end, utilisant Nginx)

### Base de Données

Nous utilisons  **PostgreSQL** , une base de données moderne et particulièrement bien adaptée à une intégration avec Docker.

### Back-end

Le choix des technologies back-end a été guidé par la robustesse, la sécurité et la facilité de développement :

* **JWT (JSON Web Tokens)** : Utilisé pour créer des tokens de connexion, gérant ainsi les sessions utilisateur et permettant une gestion efficace des rôles.
* **JPA (Hibernate)** : Simplifie grandement la manipulation des données, réduisant le besoin d'écrire des requêtes SQL complexes manuellement.
* **Lombok** : Génère automatiquement les *getters* et *setters* des classes, améliorant la clarté et l'efficacité du code.
* **BCrypt** : Un encodeur puissant pour sécuriser les mots de passe des utilisateurs.

### Front-end

Pour le front-end, l'accent a été mis sur la rapidité de développement et la cohérence visuelle :

* **Angular Material** : Une bibliothèque de composants qui nous permet d'éviter de styliser (appliquer du CSS à) chaque composant individuellement, assurant ainsi une cohérence esthétique à travers toute l'application.
* **Material Icons** : Une bibliothèque d'icônes qui rend l'interface utilisateur plus dynamique et vivante.

---

## Utilisation de l'Intelligence Artificielle (IA)

L'IA a été un outil précieux dans certaines phases du projet, mais son rôle a été bien défini :

* **Non utilisée pour l'architecture ou la base de données** : L'IA n'a pas été employée pour concevoir l'architecture générale du projet, ni pour la création des entités ou la manipulation de la base de données. Ces aspects ont été développés manuellement.
* **Génération de services** : L'IA a été utilisée pour générer les services, en fournissant des listes d'actions utiles. Il s'agit principalement de code dont le nom de la fonction est auto-explicatif.
* **Implémentation de JWT** : L'IA a aidé à la mise en place du module JWT, en générant une utilisation basique de celui-ci.
* **Développement Front-end** : C'est dans ce domaine que l'IA a été la plus sollicitée. Étant donné mes compétences limitées en design, l'IA a trouvé son efficacité pour obtenir un rendu visuel attrayant rapidement, privilégiant ainsi l'efficacité.

De façon générale, ce projet aurait été difficilement aussi abouti (du moins dans le temps consacré) qu'avec l'aide de l'IA. Mais l'IA n'est pas un outil qu'on peut laisser libre d'agir en ne récupérant un résultat. Cela demande une façon différente de travailler plus basé sur formulation claire de la demande (nécessitant une idée claire des attendus techniques), une relecture de code fourni et une reformulation avec souvent du vocabulaire technique quand le résultat n'est pas celui attendu (cela arrive assez souvent, surtout quand le projet prend de l'ampleur).

---

# Compléments

## Sécurité

La sécurité est une préoccupation majeure. L'application s'appuie sur **JWT** pour gérer les tokens de connexion des comptes. Cette solution est particulièrement bien adaptée aux frameworks back-end et front-end utilisés, assurant une gestion sécurisée de la connectivité.

Et comme vu précédemment, on utilise aussi **BCrypt** pour ne pas stocker les mots de passe ne clair.

De plus, des contrôles de sécurité sont fait dans le back pour s'assurer qu'un utilisateur n'utilise pas une URL lui étant interdite.

## Idées d'amélioration

- Étoffer les objects par exemple le vol qui aurait pu intégrer aéroports, heures d'arrivée, départ et type d'avion mais cela rendrait la démo lourde à utiliser au vu du fait que les vols soient créés à la main.
- Utilisation d'API publiques pour récupérer les vols : ici il s'agit d'une démo mais si les API étaient facilement libre d'accès (elles demandent toutes un compte et un jeton API pour des quantités limitées d'informations ce qui m'a poussé à ne pas l'intégrer) elles permettraient de retrouver, sans avoir à l'ajouter manuellement, les compagnies et vols.

---

## Informations supplémentaires vis à vis des consignes

- Visualisation d'un avis spécifique : je me suis rendu compte que les avis sont toujours traités en liste, il serait cependant possible de n'afficher qu'un avis (le back le permet) mais je n'ai pas vu de moyen de selectionner un avis hors des listes et ça rendait la chose sans intérêt (vu qu'on selectionne déjà l'avis depuis la liste)
- proposer une recherche par jour, numéro de vol, mot clé, etc : cela a été implémenté dans la section review moderation et dans flight management
