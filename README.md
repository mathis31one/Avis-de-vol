Comment ce dévloppement a été appréhendé :

Architecture (pas d'IA) :

L'architecture a été imaginée en amont du projet, elle est structurée de façon a pouvoir implémenter certaines parties au fur et à mesure mais de façon générale. Par exemple la question de la gestion des droits et de l'authentification a été prévue dès le début, car cela aurait demandé une restructuration important du code pour le faire à la fin.

Squelette (pas d'IA) :

Ici, il est question de créer les bases du projet avec sa structure et déclarer les types d'objets (entités) qu'on souhaite manipuler.

Fonctionnalités (assisté par IA) :

Cette fois, on utilise l'IA pour mettre en place, selon les entités que je lui donne, les fonctionnalités liées.

Elles ont été explicitement cités et l'IA a servi à générer des versions (donc par itération) de code, qui sont relues et dont on signale chaque différence avec la solution souhaitée.

Pour le front :

Ca a été très utile pour la création du style pour le front (une fois Angular Material préparé)

Pour le back :

pour la création des fonctions de services et controllers notamment : du code pas très complexe


Docker au coeur du projet :

Ici, une volonté personnelle que j'aime mettre en place sur mes projets : une structure docker (via docker compose) prête dès le début du projet pour rendre le développement plus simple et surtout le lancement sur différents environements possible sans risquer d'avoir des problèmes liés à ceux-ci)


Question sécurité :

L'application utilise JWT, qui gère donc des tokens pour gérer la connectivité des comptes. Il s'agit d'une solution bien adaptée pour les deux frameworks utilisés
