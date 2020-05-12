<!-- LEAGUES level1 level2 level3 level4 level5 -->
<div id="statement_back" class="statement_back" style="display: none"></div>
<div class="statement-body">
  <!-- BEGIN level1 level2 level3 level4 -->
  <!-- LEAGUE ALERT -->
  <div style="color: #7cc576; 
    background-color: rgba(124, 197, 118,.1);
    padding: 20px;
    margin-right: 15px;
    margin-left: 15px;
    margin-bottom: 10px;
    text-align: left;">
    <div style="text-align: center; margin-bottom: 6px">
      <img src="//cdn.codingame.com/smash-the-code/statement/league_wood_04.png" />
    </div>
    <!-- BEGIN level1 -->
    <p style="text-align: center; font-weight: 700; margin-bottom: 6px;">
      Ce challenge se déroule en <b>ligues</b>.
    </p>
    <span class="statement-league-alert-content">
      Pour ce challenge, plusieurs versions du même jeu seront disponibles. Quand vous aurez prouvé votre valeur dans la
      première version, vous accéderez à la ligue supérieure et débloquerez de nouvelles règles.
    </span>
    <!-- END -->
    <!-- BEGIN level2 -->
    <p style="text-align: center; font-weight: 700; margin-bottom: 6px;">
      Résumé des nouvelles règles.
    </p>
    <span class="statement-league-alert-content">
      Vous pouvez maintenant contrôler plusieurs Pacs !<br>
      <br>
      Consultez l'énoncé mis à jour pour plus de détails.</span>
    <!-- END -->
    <!-- BEGIN level3 -->
    <p style="text-align: center; font-weight: 700; margin-bottom: 6px;">
      Résumé des nouvelles règles.
    </p>
    <span class="statement-league-alert-content">
      Toutes les règles sont maintenant débloqués !
      <ul>
        <li>Vous pouvez maintenant donner un boost de vitesse à vos pacs</li>
        <li>Vous pouvez désormais modifier le type de vos pacs</li>
        <li>Seuls les pacs et pastilles qui sont dans votre ligne de mir sont visibles</li>
      </ul>
      <br>
      Consultez l'énoncé mis à jour pour plus de détails.</span>
    <!-- END -->
    <!-- BEGIN level4 -->
    <p style="text-align: center; font-weight: 700; margin-bottom: 6px;">
      Résumé des nouvelles règles.
    </p>
    <span class="statement-league-alert-content">
      Vous pouvez maintenant voir les pacs qui sont morts.
      <br><br>
      Consultez l'énoncé mis à jour pour plus de détails.</span>
    <!-- END -->
  </div>
  <!-- END -->
  <!-- GOAL -->
  <div class="statement-section statement-goal">
    <h2>
      <span class="icon icon-goal">&nbsp;</span>
      <span>Objectif</span>
    </h2>
    <div class="statement-goal-content">
      <div>
        Manger plus de pastilles que votre adversaire !

        <!-- BEGIN level3 level4 level5 -->
        Et éviter de se faire tuer !
        <!-- END -->
      </div>
      <div style="text-align: center; margin: 15px">

        <!-- BEGIN level1 -->
        <img src="https://static.codingame.com/servlet/fileservlet?id=43832304725167"
          alt="Manger plus de pastilles que votre adversaire !" style="width: 60%" />
        <!-- END -->

        <!-- BEGIN level2 -->
        <img src="https://static.codingame.com/servlet/fileservlet?id=43832320304748"
          alt="Manger plus de pastilles que votre adversaire !" style="width: 60%" />
        <!-- END -->

        <!-- BEGIN level3 level4 level5 -->
        <img src="https://static.codingame.com/servlet/fileservlet?id=43832345828298"
          alt="Manger plus de pastilles que votre adversaire !" style="width: 60%" />
        <!-- END -->
      </div>

    </div>
  </div>
  <!-- RULES -->
  <div class="statement-section statement-rules">
    <h2>
      <span class="icon icon-rules">&nbsp;</span>
      <span>Règles</span>
    </h2>

    <div class="statement-rules-content">
      <p>
        Le jeu se joue sur une grille qui vous est donnée au début de chaque partie. La grille est composée de murs et
        de sol.
        <!-- BEGIN level1 -->
        Dans cette première ligue, chaque joueur contrôle un seul pac qui peut bouger dans la grille.
        <!-- END -->
        <!-- BEGIN level2 level3 level4 level5 -->
        Chaque joueur contrôle une équipe de pacs qui peuvent bouger dans la grille.
        <!-- END -->
      </p>
      <br>
      <h3 style="font-size: 14px;
      font-weight: 700;
      padding-top: 5px;
      padding-bottom: 15px;">
        🗺️ La grille</h3>
      <p>
        La grille est générée aléatoirement, et possède une taille <var>width</var>, <var>height</var>.
      </p>
      <br>
      <p>
        Chaque cellule de la grille est soit&nbsp;:
      </p>
      <ul>
        <li>
          Un mur (représenté par le caractère croisillon <action>#</action>)
        </li>
        <li>
          Du sol (représenté par un espace <action>&nbsp;</action>).
        </li>
      </ul>

      <br>
      <p>
        Les grilles sont toujours symétriques par rapport à l'axe vertical central.
        La plupart des grilles ont des cases de type sol sur les extrémités gauche et droite&nbsp;; les pacs peuvent
        <strong>faire le tour de la grille</strong> et apparaître de l'autre côté en passant par ces cases.
      </p>
      <br>

      <p>
        Quand le jeu démarre, la grille est remplie de <strong>pastilles</strong> et d'occasionnelles
        <strong>super-pastilles</strong>.
        <!-- BEGIN level1 -->
        Manger une pastille avec votre <strong>pac</strong> vous rapporte <const>1&nbsp;point</const>.
        <!-- END -->
        <!-- BEGIN level2 level3 level4 level5 -->
        Manger une pastille avec l'un de vos <strong>pacs</strong> vous rapporte <const>1&nbsp;point</const>.
        <!-- END -->
        Les super-pastilles valent <const>10&nbsp;points</const>. Une fois mangée, une pastille disparaît.
      </p>

      <div style="text-align: center; margin: 15px">
        <img src="https://static.codingame.com/servlet/fileservlet?id=43832401302931"
          style="width: 400px; max-width: 100%" />
        <div><em>Une pastille rapporte <const>1&nbsp;point</const> et une super-pastille rapporte <const>10&nbsp;points
            </const>.</em></div>
      </div>

      <br>

      <h3 style="font-size: 14px;
      font-weight: 700;
      padding-top: 5px;
      padding-bottom: 15px;">
        🔵🔴 Les Pacs</h3>


      <!-- BEGIN level1 -->
      <p>Chaque joueur contrôle un pac. Mais dans les ligues suivantes, vous controllerez jusqu'à <const>5</const> pacs
        chacun.</p>
      <!-- END -->

      <!-- BEGIN level2 -->
      <div class="statement-new-league-rule">
        <!-- END -->

        <!-- BEGIN level2 level3 level4 level5 -->
        <p>Chaque joueur commence avec le même nombre de pacs, jusqu'à <const>5</const> chacun.</p>
        <!-- END -->

        <!-- BEGIN level2 -->
      </div>
      <!-- END -->

      <br>


      <br>
      <!-- BEGIN level1 level2 -->
      <p>Vous avez la vision sur l'ensemble des pastilles et des pacs sur la grille (ceci changera dans une ligue
        prochaine).</p>
      <br>
      <!-- END -->

      <!-- BEGIN level3 -->
      <div class="statement-new-league-rule">
        <!-- END -->
        <!-- BEGIN level3 level4 level5 -->
        <p>
          Vos pacs <strong>ne voient pas à travers les murs</strong>.
          À chaque tour, vous voyez toutes les pastilles et les pacs ennemis qui peuvent être reliés à vos pacs par une
          ligne droite continue. Les super-pastilles sont en revanche si brillantes qu'elles sont visibles depuis n'importe où&nbsp;!

          <div style="text-align: center; margin: 15px">

            <img src="https://static.codingame.com/servlet/fileservlet?id=43832388706627"
              style="width: 400px; max-width: 100%" />
            <div><em>La ligne de vision des pacs est bloquée par les murs.</em></div>
          </div>
        </p>
        <br>
        <!-- END -->
        <!-- BEGIN level3 -->
      </div>
      <!-- END -->

      <p>À chaque tour, vous recevez les informations relatives aux pacs et pastilles qui sont visibles. Pour chaque pac
        vous avez son identifiant, s'il vous appartient ou non, ainsi que ses coordonnées. Pour chaque pastille vous
        avez leurs coordonnées et leur valeur.</p>
      <br>

      <!-- BEGIN level3 -->
      <div class="statement-new-league-rule">
        <!-- END -->
        <!-- BEGIN level3 level4 level5 -->
        <p>
          Chaque pac possède un <var>type</var> donné (<action>ROCK</action>, <action>PAPER</action> ou <action>SCISSORS
          </action>).
        </p>
        <p>
          Chaque pac a accès à deux <b>compétences</b> (<action>SWITCH</action> et <action>SPEED</action>) qui partagent
          le même <b>temps de rechargement</b> de <const>10</const> tours.
          Les compétences d'un pac sont déjà disponibles au début de la partie.
        </p>
        <br>
        <!-- END -->

        <!-- BEGIN level3 -->
      </div>
      <!-- END -->


        <!-- BEGIN level1 -->
      <p>Les pacs peuvent recevoir la commande suivante&nbsp;:</p>
        <!-- END -->

        <!-- BEGIN level2 level3 level4 level5 -->
      <p>Les pacs peuvent recevoir les commandes suivantes (un pac ne peut recevoir qu'une commande par tour)&nbsp;:</p>
        <!-- END -->
      <ul>
        <li>
          <action>MOVE</action>&nbsp;: Donne au pac une case cible, le pac va choisir le plus court chemin vers cette
          position et va effectuer <b>le premier déplacement de ce chemin</b>. Le pac ne prend pas en compte la présence
          de pastilles ou d'autres pacs lorsqu'il choisit un chemin.

          <div style="text-align: center; margin: 15px">
            <img src="https://static.codingame.com/servlet/fileservlet?id=43832368078694"
              style="width: 400px; max-width: 100%" />
            <div style="margin: auto; width: 400px; max-width: 100%"><em>Chaque pac ayant reçu une action <action>MOVE
                </action> avancera vers la destination en faisant un pas vers le haut, le bas, la droite ou la
                gauche.</em></div>
          </div>
        </li>

        <!-- BEGIN level3 -->
      </ul>
      <div class="statement-new-league-rule">
        <ul>
          <!-- END -->
          <!-- BEGIN level3 level4 level5 -->
          <li>
            <action>SWITCH</action>&nbsp;: Si les compétences du pac sont disponibles, celui-ci va se transformer en un
            nouveau type de pac. Les types de pacs disponibles sont&nbsp;:
            <ul>
              <li>
                <action>ROCK</action>
              </li>
              <li>
                <action>PAPER</action>
              </li>
              <li>
                <action>SCISSORS</action>
              </li>
            </ul>

            <div style="text-align: center; margin: 15px">

              <img src="https://static.codingame.com/servlet/fileservlet?id=43832448614162"
                style="width: 400px; max-width: 100%" />
              <div style="margin: auto; width: 400px; max-width: 100%">
                <em>L'action <action>SWITCH</action> permet à un pac de changer de type.</em></div>
            </div>

          </li>
          <li>
            <action>SPEED</action>&nbsp;: Si les compétences du pac sont disponibles, celui-ci va accélérer pendant les
            <const>5</const> prochains tours, lui permettant d'avancer de <strong>2 pas</strong> lors de ses mouvements.
            Cela veut dire que le pac peut se déplacer deux fois plus vite que d'habitude à chaque tour.

            <div style="text-align: center; margin: 15px">

              <img src="https://static.codingame.com/servlet/fileservlet?id=43832428009785"
                style="width: 400px; max-width: 100%" />
              <div style="margin: auto; width: 400px; max-width: 100%">
                <em>L'action <action>SPEED</action> permet à un pac de se déplacer de 2 coups au lieu d'un seul pendant
                  les 5 prochains tours.</em></div>
            </div>
          </li>
          <!-- END -->
          <!-- BEGIN level3 -->
        </ul>
      </div>
      <ul>
        <!-- END -->
      </ul>
      <p>
        Référez-vous à la section <b>Protocole de jeu</b> pour plus d'informations sur les commandes à envoyer à vos
        pacs.
      </p>


        <!-- BEGIN level1 level2 -->
        <p>
          Croiser un pac ou atterrir sur la même case va créer une <b>collision</b>. Dans ce cas, les déplacements des pacs qui sont entrés en collision sont annulés.
          <!-- BEGIN level2 -->
          <div class="statement-new-league-rule">
            L'annulation d'un déplacement peut provoquer une autre collision qui sera résolue de la même manière, et ainsi de suite jusqu'à ce qu'il n'y ait plus de collisions.
          </div>
          <!-- END -->
        </p>
        <!-- END -->

        <!-- BEGIN level3 -->
        <div class="statement-new-league-rule">
          <!-- END -->

          <!-- BEGIN level3 level4 level5 -->
          <p>
            Croiser un pac ou atterrir sur la même case peut créer une <b>collision</b>. Voici comment ces collisions sont résolues&nbsp;:
          </p>
          <ol>
            <li>Tous les pacs en mouvement se déplacent de <const>1</const> case, peu importe leur vitesse.</li>
            <li>Si les pacs sont de même type ou appartiennent au même joueur, alors les pacs reviennent à leur position d'où ils sont partis. Si les pacs sont de types différents, ils peuvent attérir sur une même case mais un pac ne peut pas croiser le chemin d'un pac plus fort que lui : il sera alors bloqué.</li>
            <li>Le fait d'annuler un déplacement peut provoquer de nouvelles collisions. Pour cette raison, l'étape précédente est répétée jusqu'à ce que plus aucune nouvelle collision ne se fasse.</li>
            <li>Les pacs qui partagent la même case qu'un pac plus fort qu'eux sont tués. <action>ROCK</action> bat <action>SCISSORS</action>, <action>SCISSORS</action> bat <action>PAPER</action> et
            <action>PAPER</action> bat <action>ROCK</action>.</li>
            <li>Répéter pour chaque pac ayant une compétence SPEED activée.</li>
          </ol>
          <br>
          <!-- END -->

        <!-- BEGIN level3 level4 level5 -->
        <h3 style="font-size: 14px;
      font-weight: 700;
      padding-top: 5px;
      padding-bottom: 15px;">
          🎬 Ordre des actions pour un tour de jeu</h3>
        <ol>
          <li>Décrémenter les compteurs de temps de rechargement</li>
          <li>Décrémenter les compteurs de durée de <action>SPEED</action>
          </li>
          <li>Éxécuter les <b>compétences</b></li>
          <li>Résoudre les mouvements, en incluant les collisions</li>
          <li>Tuer les pacs qui ont perdu lors de collisions</li>
          <li>Ingestions de pastilles</li>
        </ol>
        <!-- END -->

        <!-- BEGIN level3 -->
      </div>
      <!-- END -->

      <h3 style="font-size: 14px;
      font-weight: 700;
      padding-top: 5px;
      padding-bottom: 15px;">
        ⛔ Fin du jeu</h3>

      <p>
        La partie se termine lorsqu'il n'y a plus assez de pastilles en jeu pour changer l'issue de la partie.<br>
        <br>
        Le jeu s'arrête automatiquement après <const>200 tours</const>.<br>

        <!-- BEGIN level3 -->
        <div class="statement-new-league-rule">
          <!-- END -->
          <!-- BEGIN level3 level4 level5 -->
          </br>
          Si tous les pacs d'un joueur sont morts, toutes les pastilles restantes sont automatiquement accordées aux
          pacs survivants et la partie se termine.<br>
          <br>
          Le gagnant est le joueur avec le meilleur score, peu importe le nombre de pacs en vie restants.
          <!-- END -->
          <!-- BEGIN level3 -->
        </div>
        <!-- END -->
      </p>
      <!-- Victory conditions -->
      <div class="statement-victory-conditions">
        <div class="icon victory"></div>
        <div class="blk">
          <div class="title">Conditions de victoire</div>
          <div class="text">
            <ul style="padding-top:0; padding-bottom: 0;">
              Vous avez mangé plus de pastilles que l'adversaire à la fin de la partie.
            </ul>
          </div>
        </div>
      </div>
      <!-- Lose conditions -->
      <div class="statement-lose-conditions">
        <div class="icon lose"></div>
        <div class="blk">
          <div class="title">Conditions de défaite</div>
          <div class="text">
            <ul style="padding-top:0; padding-bottom: 0;">
              Votre programme n'a pas répondu dans le temps imparti ou l'une des commandes est invalide.
            </ul>
          </div>
        </div>
      </div>
      <br>
      <h3 style="font-size: 14px;
    font-weight: 700;
    padding-top: 5px;
    padding-bottom: 15px;">
        🐞 Conseils de débogage</h3>
      <ul>
        <li>Survolez une case de la grille pour voir ses coordonnées</li>
        <li>Survolez un pac pour avoir des informations sur lui</li>
        <li>Rajoutez du texte à la fin d'une commande d'un pac pour afficher ce texte au-dessus de lui</li>
        <li>Cliquez sur la roue dentée pour afficher des options supplémentaires</li>
        <li>Utilisez le clavier pour contrôler les actions&nbsp;: espace pour lire/mettre en pause, les flèches pour
          avancer pas à pas</li>
      </ul>


    </div>

</div>

    <!-- BEGIN level3 level4 level5 -->
    <!-- EXPERT RULES -->
    <div class="statement-section statement-expertrules">
      <h2>
        <span class="icon icon-expertrules">&nbsp;</span>
        <span>Détails techniques</span>
      </h2>
      <div class="statement-expert-rules-content">
        <ul style="padding-left: 20px;padding-bottom: 0">
          <li>
            Vous pouvez voir le code source de ce jeu sur <a rel="nofollow" target="_blank"
              href="https://github.com/CodinGame/SpringChallenge2020">ce repo GitHub</a>.
          </li>
        </ul>
      </div>
    </div>
    <!-- END -->


  <!-- PROTOCOL -->
  <div class="statement-section statement-protocol">
    <h2>
      <span class="icon icon-protocol">&nbsp;</span>
      <span>Protocole de jeu</span>
    </h2>
    <!-- Protocol block -->
    <div class="blk">
      <div class="title">Entrées d'initialisation
      </div>
      <div class="text">
        <span class="statement-lineno">Ligne 1&nbsp;:</span> deux entiers <var>width</var> et <var>height</var> pour la taille
          de la grille.<br>
        <span class="statement-lineno">Les <var>height</var> lignes suivantes&nbsp;:</span> une chaîne de <var>width</var>
          caractères représentant les cases de cette ligne&nbsp;: <const>' '</const> pour du sol et <const>'#'</const>
          pour un mur. 
        <br>
      </div>
    </div>
    <!-- Protocol block -->
    <div class="blk">
      <div class="title">Entrées pour un tour de jeu</div>
      <div class="text">
        <span class="statement-lineno">Ligne 1&nbsp;:</span> Deux entiers séparés par un espace&nbsp;: <br>
        <ul style="padding-left: 20px;padding-top:0;padding-bottom:0px">
          <li><var>myScore</var> votre score actuel</li>
          <li><var>opponentScore</var> le score de votre adversaire</li>
        </ul>

        <span class="statement-lineno">Ligne 2&nbsp;:</span> Un entier&nbsp;: <br>
        <ul style="padding-left: 20px;padding-top:0;padding-bottom:0px">
          <li>
            <var>visiblePacCount</var>&nbsp;: le nombre de pacs visibles pour vous
          </li>
        </ul>
        <span class="statement-lineno">Les <var>visiblePacCount</var> lignes suivantes&nbsp;:</span>
        <br>
        <ul style="padding-left: 20px;padding-top:0;padding-bottom:0px">
          <li>
            <var>pacId</var>&nbsp;: l'ID du pac (unique par joueur)
          </li>
          <li>
            <var>mine</var>&nbsp;: le propriétaire du pac (1 si ce pac est à vous, 0 sinon. Converti en un type booléen pour la majorité des langages.)
          </li>
          <li>
            <var>x</var> & <var>y</var>&nbsp;: la position du pac
          </li>
          <!-- BEGIN level1 level2 -->
          <li>
            <var>typeId</var>&nbsp;: inutilisé dans cette ligue
          </li>
          <li>
            <var>speedTurnsLeft</var>&nbsp;: inutilisé dans cette ligue
          </li>
          <li>
            <var>abilityCooldown</var>&nbsp;: inutilisé dans cette ligue
          </li>
          <!-- END -->

          <!-- BEGIN level3 -->
        </ul>
        <div class="statement-new-league-rule">
          <ul>
            <!-- END -->

            <!-- BEGIN level3 level4 level5 -->

            <!-- BEGIN level4 -->
            </ul>
            <div class="statement-new-league-rule">
            <ul>
            <!-- END -->

            <li>
              <var>typeId</var>&nbsp;: le type de pac (<action>ROCK</action> ou <action>PAPER</action> ou <action>
                SCISSORS</action>).
                <!-- BEGIN level4 level5 -->
                Si le pac est mort, son type est maintenant <action>DEAD</action>.
                <!-- END -->
            </li>

            <!-- BEGIN level4 -->
            </ul>
            </div>
            <ul>
            <!-- END -->

            <li>
              <var>speedTurnsLeft</var>&nbsp;: le nombre de tours restants avant que l'effet du speed ne s'estompe
            </li>
            <li>
              <var>abilityCooldown</var>&nbsp;: le nombre de tours restants avant de pouvoir utiliser une compétence
              avec ce pac (<action>SWITCH</action> et <action>SPEED</action>)
            </li>
            <!-- END -->

            <!-- BEGIN level3 -->
        </div>
        <!-- END -->

        </ul>
        <span class="statement-lineno">Ligne suivante&nbsp;:</span> un entier
        <var>visiblePelletCount</var>&nbsp;: le nombre de pastilles visibles pour vous</span><br>
        <span class="statement-lineno">Les <var>visiblePelletCount</var> lignes suivantes&nbsp;:</span> trois
        entiers&nbsp;:<br>
        <ul style="padding-left: 20px;padding-top:0;padding-bottom:0px">
          <li>
            <var>x</var> & <var>y</var>&nbsp;: la position de la pastille
          </li>
          <li>
            <var>value</var>&nbsp;: le score de la pastille
          </li>
        </ul>
      </div>
    </div>
    <!-- Protocol block -->
    <div class="blk">
      <div class="title">Sortie pour un tour de jeu
      </div>
      <div class="text">
          <!-- BEGIN level1 -->
          Une seule ligne avec votre action&nbsp;:
          <!-- END -->

          <!-- BEGIN level2 -->
          <div class="statement-new-league-rule">
            <!-- END -->
            <!-- BEGIN level2 level3 level4 level5 -->
            Une seule ligne avec une ou plusieurs commandes séparées par <const>|</const>. Par exemple&nbsp;: <action>
              MOVE 0 5 7 | MOVE 1 16 10</action>.
            <!-- END -->
            <!-- BEGIN level2 -->
          </div>
          <!-- END -->
          <ul style="padding-left: 20px;padding-top: 0">
            <li>
              <action>MOVE pacId x y</action>&nbsp;: le pac avec l'identifiant <const>pacId</const> se déplace vers la
              case ciblée.
              <!-- BEGIN level1 -->
              (<action>pacId</action> vaut toujours <const>0</const> dans cette ligue).
              <!-- END -->
            </li>
            <!-- BEGIN level3 -->
          </ul>
          <div class="statement-new-league-rule">
            <ul>
              <!-- END -->
              <!-- BEGIN level3 level4 level5 -->
              <li>
                <action>SPEED pacId </action>&nbsp;: le pac pourra se déplacer de 2 cases pendant les 5 tours suivants.
              </li>
              <li>
                <action>SWITCH pacId pacType</action>&nbsp;: le pac se transforme en <var>pacType</var>.
              </li>
              <!-- END -->
            </ul>
            <!-- BEGIN level3 -->
          </div>
          <!-- END -->
      </div>
    </div>
    <div class="blk">
      <div class="title">Contraintes</div>
      <div class="text">
        <!-- BEGIN level2 level3 level4 level5 -->
        <const>2</const> ≤ Nombre de pacs par joueur ≤ <const>5</const><br>
        <const>29</const> ≤ <var>width</var> ≤ <const>35</const><br>
        <const>10</const> ≤ <var>height</var> ≤ <const>17</const><br>
        <!-- END -->
        Temps de réponse par tour ≤ <const>50</const>ms<br>
        Temps de réponse au premier tour ≤ <const>1000</const>ms
      </div>
    </div>
  </div>
  <!-- BEGIN level1 level2 -->
  <!-- LEAGUE ALERT -->
  <div style="color: #7cc576; 
    background-color: rgba(124, 197, 118,.1);
    padding: 20px;

    margin-top: 10px;
    text-align: left;">
    <div style="text-align: center; margin-bottom: 6px"><img
        src="//cdn.codingame.com/smash-the-code/statement/league_wood_04.png" /></div>

    <p style="text-align: center; font-weight: 700; margin-bottom: 6px;">
      Qu'est-ce qui m'attend dans les prochaines ligues ?
    </p>
    <p>
      Les nouvelles règles débloquées dans les prochaines ligues sont&nbsp;:
    </p>
    <ul>
      <li>les pacs auront la forme de pierre, feuille, et ciseaux.</li>
      <li>les pacs pourront s'entretuer.</li>
      <li>les pacs pourront changer de forme et accélérer.</li>
    </ul>
  </div>
  <!-- END -->

</div>
